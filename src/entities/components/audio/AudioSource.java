package entities.components.audio;

import entities.Entity;
import entities.components.Component;

/**
 * Plays an audio clip from this entity.
 * <p>
 * When {@code spatial} is enabled, volume and stereo pan are calculated
 * relative to the {@code listener} entity's position. The sound attenuates
 * linearly from full volume at distance 0 to silence at {@code maxDistance}.
 */
public class AudioSource extends Component {
	/** Whether the clip loops continuously. */
	public boolean loop = false;
	/** Set to true to trigger playback. The system resets this after starting. */
	public boolean play = false;
	/** Set to true to stop current playback. The system resets this after stopping. */
	public boolean stop = false;
	/** True while the clip is actively playing. Read-only — managed by AudioSystem. */
	public boolean playing = false;
	/** Path to the audio resource (relative to res/audio/). */
	public String filePath = "";
	/** Base playback volume, clamped 0.0–1.0. Spatial attenuation scales this. */
	public float volume = 1.0f;

	/** If true, volume and pan are computed relative to the listener entity. */
	public boolean spatial = false;
	/** The entity to measure distance/direction from. Required when spatial is true. */
	public Entity listener = null;
	/** Maximum distance (in world units) at which the sound is still audible. */
	public float maxDistance = 300f;

	public AudioSource setLoop(boolean loop) {
		this.loop = loop;
		return this;
	}

	public AudioSource setPlay(boolean play) {
		this.play = play;
		return this;
	}

	public AudioSource setStop(boolean stop) {
		this.stop = stop;
		return this;
	}

	public AudioSource setFilePath(String filepath) {
		this.filePath = filepath;
		return this;
	}

	public AudioSource setVolume(float volume) {
		if (volume > 1.0f) {
			volume = 1.0f;
		} else if (volume < 0.0f) {
			volume = 0.0f;
		}
		this.volume = volume;
		return this;
	}

	public AudioSource setSpatial(boolean spatial) {
		this.spatial = spatial;
		return this;
	}

	public AudioSource setListener(Entity listener) {
		this.listener = listener;
		return this;
	}

	public AudioSource setMaxDistance(float maxDistance) {
		this.maxDistance = maxDistance;
		return this;
	}
}
