package entities.components.rendering;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import entities.components.Component;

/**
 * Holds multiple named animations for an entity. Each animation is a sequence
 * of pre-loaded frames with a configurable frame duration and loop setting.
 * <p>
 * The {@link entities.systems.AnimationSystem AnimationSystem} ticks through
 * frames and swaps the entity's {@link Sprite#image} each step.
 * <p>
 * Example usage:
 * <pre>
 * Creator.animation()
 *     .addAnimation("walk_down",  "sprites/player_walk_down.png",  4, 32, 32, 0.15, true)
 *     .addAnimation("walk_up",    "sprites/player_walk_up.png",    4, 32, 32, 0.15, true)
 *     .addAnimation("idle",       "sprites/player_idle.png",       2, 32, 32, 0.5,  true)
 *     .setCurrentAnimation("idle");
 * </pre>
 */
public class Animation extends Component {

	/** All registered animations by name. */
	public Map<String, AnimationData> animations = new HashMap<>();

	/** The name of the currently active animation. */
	public String currentAnimation = null;

	/** Current frame index within the active animation. */
	public int currentFrame = 0;

	/** Time accumulated toward the next frame advance. */
	public double elapsed = 0;

	/**
	 * Adds a named animation from a sprite sheet.
	 *
	 * @param name        unique name for this animation (e.g. "walk_down")
	 * @param sheetPath   classpath resource path to the sprite sheet
	 * @param frameCount  number of frames in the sheet (laid out horizontally)
	 * @param frameWidth  width of a single frame in pixels
	 * @param frameHeight height of a single frame in pixels
	 * @param frameDuration seconds each frame is displayed
	 * @param loop        whether the animation loops
	 * @return this (fluent)
	 */
	public Animation addAnimation(String name, String sheetPath, int frameCount,
								  int frameWidth, int frameHeight, double frameDuration, boolean loop) {
		try {
			BufferedImage sheet = ImageIO.read(getClass().getClassLoader().getResource( "sprites/"+sheetPath));
			BufferedImage[] frames = new BufferedImage[frameCount];
			for (int i = 0; i < frameCount; i++) {
				frames[i] = sheet.getSubimage(i * frameWidth, 0, frameWidth, frameHeight);
			}
			animations.put(name, new AnimationData(frames, frameDuration, loop));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}

	/**
	 * Adds a named animation from pre-loaded frames.
	 *
	 * @param name          unique name for this animation
	 * @param frames        array of pre-loaded frame images
	 * @param frameDuration seconds each frame is displayed
	 * @param loop          whether the animation loops
	 * @return this (fluent)
	 */
	public Animation addAnimation(String name, BufferedImage[] frames, double frameDuration, boolean loop) {
		animations.put(name, new AnimationData(frames, frameDuration, loop));
		return this;
	}

	/**
	 * Sets the active animation by name. Resets frame index and elapsed time
	 * only if switching to a different animation.
	 */
	public Animation setCurrentAnimation(String name) {
		if (name != null && !name.equals(currentAnimation)) {
			currentAnimation = name;
			currentFrame = 0;
			elapsed = 0;
		}
		return this;
	}

	/**
	 * Returns the current frame image, or null if no animation is active.
	 */
	public BufferedImage getCurrentFrameImage() {
		if (currentAnimation == null) return null;
		AnimationData data = animations.get(currentAnimation);
		if (data == null) return null;
		return data.frames[currentFrame];
	}

	/**
	 * Data for a single named animation sequence.
	 */
	public static class AnimationData {
		public BufferedImage[] frames;
		public double frameDuration;
		public boolean loop;

		public AnimationData(BufferedImage[] frames, double frameDuration, boolean loop) {
			this.frames = frames;
			this.frameDuration = frameDuration;
			this.loop = loop;
		}
	}
}
