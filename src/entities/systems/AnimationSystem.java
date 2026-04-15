package entities.systems;

import java.awt.image.BufferedImage;

import entities.Entity;
import entities.EntityManager;
import entities.components.rendering.Animation;
import entities.components.rendering.Animation.AnimationData;
import entities.components.rendering.Sprite;

/**
 * Advances animation frames and swaps the entity's {@link Sprite#image}
 * each tick. Requires both {@link Animation} and {@link Sprite} on the entity.
 */
public class AnimationSystem {

	public void update(EntityManager entities, double dt) {
		for (Entity e : entities.getEntities()) {
			if (!e.has(Animation.class) || !e.has(Sprite.class)) continue;

			Animation anim = e.get(Animation.class);
			if (anim.currentAnimation == null) continue;

			AnimationData data = anim.animations.get(anim.currentAnimation);
			if (data == null) continue;

			anim.elapsed += dt;

			if (anim.elapsed >= data.frameDuration) {
				anim.elapsed -= data.frameDuration;
				anim.currentFrame++;

				if (anim.currentFrame >= data.frames.length) {
					anim.currentFrame = data.loop ? 0 : data.frames.length - 1;
				}
			}

			BufferedImage frame = data.frames[anim.currentFrame];
			e.get(Sprite.class).image = frame;
		}
	}
}
