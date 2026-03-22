package entities.components.audio;

import entities.components.Component;

/**
 * Plays an audio clip from this entity.
 */
public class AudioSource extends Component {
	/** Whether the clip loops continuously. */
	public boolean loop = false;
	/** Set to true to trigger playback. */
	public boolean play = false;
	/** Path to the audio resource. */
	public String filePath = "";
	/** Playback volume, clamped 0.0–1.0. */
	public float volume = 1.0f;
	
	public AudioSource setLoop(boolean loop) {
		this.loop = loop;
		return this;
	}
	
	public AudioSource setPlay(boolean play) {
		this.play = play;
		return this;
	}
	
	public AudioSource setFilePath(String filepath) {
		this.filePath = filepath;
		return this;
	}
	
	public AudioSource setVolume(float volume) {
		if(volume > 1.0f) {
			volume = 1.0f;
		}else if (volume < 0.0f) {
			volume = 0.0f;
		}
		
		this.volume = volume;
		return this;
	}
}
