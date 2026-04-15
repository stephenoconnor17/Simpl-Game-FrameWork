package entities.systems;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import entities.Entity;
import entities.EntityManager;
import entities.components.audio.AudioSource;
import entities.components.rendering.Camera;
import entities.components.transform.Position;

/**
 * Manages audio playback for entities with AudioSource components.
 *
 * Caches Clip instances per entity so repeated plays reuse the same clip.
 * Flag protocol:
 *   play=true  → system starts the clip, sets playing=true, resets play=false
 *   stop=true  → system stops the clip, sets playing=false, resets stop=false
 *   clip ends  → system sets playing=false (unless looping)
 *
 * When spatial is enabled on an AudioSource, volume is attenuated by distance
 * and stereo pan is set based on the horizontal offset between the source
 * entity and the listener entity. Distance uses both x and y axes.
 */
public class AudioSystem implements GameSystem {

	private static final String AUDIO_DIR = "res/audio/";

	/** Cached clips keyed by AudioSource component instance. */
	private final Map<AudioSource, Clip> clips = new HashMap<>();
	/** Tracks which filePath a cached clip was loaded from. */
	private final Map<AudioSource, String> loadedPaths = new HashMap<>();

	@Override
	public void update(EntityManager entities, double dt) {
		// Find camera rotation for screen-relative panning
		double camRotation = 0;
		for (Entity e : entities.getEntities()) {
			if (e.has(Camera.class)) {
				camRotation = e.get(Camera.class).rotation;
				break;
			}
		}

		for (Entity entity : entities.getEntities()) {
			if (!entity.has(AudioSource.class)) continue;

			AudioSource src = entity.get(AudioSource.class);

			// Handle stop request
			if (src.stop) {
				src.stop = false;
				Clip clip = clips.get(src);
				if (clip != null && clip.isRunning()) {
					clip.stop();
				}
				src.playing = false;
			}

			// Handle play request
			if (src.play) {
				src.play = false;

				if (src.filePath == null || src.filePath.isEmpty()) continue;

				Clip clip = getOrLoadClip(src, src.filePath);
				if (clip == null) continue;

				applyVolume(clip, src.volume);

				clip.setFramePosition(0);
				if (src.loop) {
					clip.loop(Clip.LOOP_CONTINUOUSLY);
				} else {
					clip.start();
				}
				src.playing = true;
			}

			// Apply spatial audio every frame while playing
			if (src.playing && src.spatial) {
				Clip clip = clips.get(src);
				if (clip != null) {
					applySpatial(clip, src, entity, camRotation);
				}
			}

			// Check if a non-looping clip finished
			if (src.playing && !src.loop) {
				Clip clip = clips.get(src);
				if (clip != null && !clip.isRunning()) {
					src.playing = false;
				}
			}
		}
	}

	/**
	 * Calculates distance-based volume attenuation and horizontal pan
	 * between the source entity and the listener entity.
	 * Pan accounts for camera rotation so left/right matches what's on screen.
	 */
	private void applySpatial(Clip clip, AudioSource src, Entity sourceEntity, double camRotation) {
		if (src.listener == null) return;
		if (!sourceEntity.has(Position.class) || !src.listener.has(Position.class)) return;

		Position srcPos = sourceEntity.get(Position.class);
		Position listenerPos = src.listener.get(Position.class);

		double dx = srcPos.x - listenerPos.x;
		double dy = srcPos.y - listenerPos.y;
		double distance = Math.sqrt(dx * dx + dy * dy);

		// Volume: linear falloff from base volume to 0 at maxDistance
		float attenuation = 1.0f - (float) Math.min(distance / src.maxDistance, 1.0);
		float spatialVolume = src.volume * attenuation;
		applyVolume(clip, spatialVolume);

		// Rotate offset by negative camera rotation so pan is screen-relative
		double cos = Math.cos(-camRotation);
		double sin = Math.sin(-camRotation);
		double screenDx = dx * cos - dy * sin;

		// Pan: screen-relative horizontal offset mapped to -1.0 (left) to 1.0 (right)
		if (clip.isControlSupported(FloatControl.Type.PAN)) {
			float pan = (float) (screenDx / src.maxDistance);
			pan = Math.max(-1.0f, Math.min(1.0f, pan));
			FloatControl panControl = (FloatControl) clip.getControl(FloatControl.Type.PAN);
			panControl.setValue(pan);
		}
	}

	/**
	 * Returns a cached clip or loads a new one. If the filePath changed,
	 * the old clip is closed and a new one is loaded.
	 */
	private Clip getOrLoadClip(AudioSource src, String filePath) {
		String loaded = loadedPaths.get(src);
		if (loaded != null && loaded.equals(filePath) && clips.containsKey(src)) {
			return clips.get(src);
		}

		// Close old clip if path changed
		Clip old = clips.remove(src);
		if (old != null) {
			old.stop();
			old.close();
		}

		try {
			File audioFile = new File(AUDIO_DIR + filePath);
			AudioInputStream stream = javax.sound.sampled.AudioSystem.getAudioInputStream(audioFile);
			Clip clip = javax.sound.sampled.AudioSystem.getClip();
			clip.open(stream);
			clips.put(src, clip);
			loadedPaths.put(src, filePath);
			return clip;
		} catch (Exception e) {
			System.err.println("AudioSystem: failed to load " + filePath + " — " + e.getMessage());
			return null;
		}
	}

	private void applyVolume(Clip clip, float volume) {
		if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
			FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			// Convert 0.0–1.0 to decibels (min to 0 dB)
			float dB = (volume <= 0f)
					? gain.getMinimum()
					: (float) (20.0 * Math.log10(volume));
			dB = Math.max(dB, gain.getMinimum());
			dB = Math.min(dB, gain.getMaximum());
			gain.setValue(dB);
		}
	}
}
