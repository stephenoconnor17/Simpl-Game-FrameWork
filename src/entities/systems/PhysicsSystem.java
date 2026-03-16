package entities.systems;

import java.util.ArrayList;
import java.util.List;

import entities.Entity;
import entities.EntityManager;
import entities.components.physics.Collision;
import entities.components.physics.Collision.Shape;
import entities.components.physics.RigidBody;
import entities.components.rendering.Sprite;
import entities.components.transform.Position;

public class PhysicsSystem implements GameSystem {

	@Override
	public void update(EntityManager entityManager, double dt) {
		List<Entity> collidables = new ArrayList<>();

		for (Entity e : entityManager.getEntities()) {
			if (e.has(Position.class) && e.has(Collision.class)) {
				// clear last frame's collision data
				e.get(Collision.class).collidedWith.clear();
				collidables.add(e);
			}
		}

		for (int i = 0; i < collidables.size(); i++) {
			for (int j = i + 1; j < collidables.size(); j++) {
				Entity a = collidables.get(i);
				Entity b = collidables.get(j);

				Collision colA = a.get(Collision.class);
				Collision colB = b.get(Collision.class);

				// layer/mask filter - skip if neither cares about the other
				if ((colA.layer & colB.mask) == 0 && (colB.layer & colA.mask) == 0) continue;

				double[] result = checkCollision(a, b);
				if (result != null) {
					// always flag both entities
					colA.collidedWith.add(b);
					colB.collidedWith.add(a);
					//Collided with is then handled in PickupSystem. 

					// only resolve physics if both have RigidBody and both are solid
					if (a.has(RigidBody.class) && b.has(RigidBody.class)
							&& colA.solid && colB.solid) {
						resolveCollision(a, b, result);
					}
				}
			}
		}
	}

	// --- center helpers (auto-center on sprite) ---

	private double getCenterX(Entity e) {
		Position pos = e.get(Position.class);
		Collision col = e.get(Collision.class);
		return pos.x + getSpriteWidth(e) / 2 + col.offsetX;
	}

	private double getCenterY(Entity e) {
		Position pos = e.get(Position.class);
		Collision col = e.get(Collision.class);
		return pos.y + getSpriteHeight(e) / 2 + col.offsetY;
	}

	private double getSpriteWidth(Entity e) {
		if (e.has(Sprite.class)) {
			Sprite sprite = e.get(Sprite.class);
			if (sprite.image != null) return sprite.image.getWidth();
		}
		Collision col = e.get(Collision.class);
		// no sprite: BOX position is top-left so use width, CIRCLE position is center so use 0
		return (col.shape == Shape.BOX) ? col.width : 0;
	}

	private double getSpriteHeight(Entity e) {
		if (e.has(Sprite.class)) {
			Sprite sprite = e.get(Sprite.class);
			if (sprite.image != null) return sprite.image.getHeight();
		}
		Collision col = e.get(Collision.class);
		return (col.shape == Shape.BOX) ? col.height : 0;
	}

	// --- collision detection ---
	// returns null if no collision, or {normalX, normalY, penetration} if colliding

	private double[] checkCollision(Entity a, Entity b) {
		Collision colA = a.get(Collision.class);
		Collision colB = b.get(Collision.class);

		if (colA.shape == Shape.CIRCLE && colB.shape == Shape.CIRCLE) {
			return checkCircleCircle(a, b);
		} else if (colA.shape == Shape.BOX && colB.shape == Shape.BOX) {
			return checkOBBOBB(a, b);
		} else {
			return checkOBBCircle(a, b);
		}
	}

	private double[] checkCircleCircle(Entity a, Entity b) {
		Collision colA = a.get(Collision.class);
		Collision colB = b.get(Collision.class);

		double dx = getCenterX(a) - getCenterX(b);
		double dy = getCenterY(a) - getCenterY(b);
		double distSq = dx * dx + dy * dy;
		double radiusSum = colA.radius + colB.radius;

		if (distSq >= radiusSum * radiusSum) return null;

		double dist = Math.sqrt(distSq);
		if (dist == 0) { dx = 1; dist = 1; }

		double penetration = radiusSum - dist;
		return new double[]{ dx / dist, dy / dist, penetration };
	}

	private double[] checkOBBOBB(Entity a, Entity b) {
		double[][] cornersA = getCorners(a);
		double[][] cornersB = getCorners(b);

		double rotA = a.get(Position.class).rotation;
		double rotB = b.get(Position.class).rotation;

		// 4 axes: 2 from each box's orientation
		double[][] axes = {
			{ Math.cos(rotA), Math.sin(rotA) },
			{ -Math.sin(rotA), Math.cos(rotA) },
			{ Math.cos(rotB), Math.sin(rotB) },
			{ -Math.sin(rotB), Math.cos(rotB) }
		};

		double minPenetration = Double.MAX_VALUE;
		double[] minAxis = null;

		for (double[] axis : axes) {
			double[] projA = projectCorners(cornersA, axis);
			double[] projB = projectCorners(cornersB, axis);

			double overlap = Math.min(projA[1], projB[1]) - Math.max(projA[0], projB[0]);

			if (overlap <= 0) return null;

			if (overlap < minPenetration) {
				minPenetration = overlap;
				minAxis = axis;
			}
		}

		// ensure normal points from b to a
		double dx = getCenterX(a) - getCenterX(b);
		double dy = getCenterY(a) - getCenterY(b);
		double dot = minAxis[0] * dx + minAxis[1] * dy;
		if (dot < 0) {
			minAxis[0] = -minAxis[0];
			minAxis[1] = -minAxis[1];
		}

		return new double[]{ minAxis[0], minAxis[1], minPenetration };
	}

	private double[] checkOBBCircle(Entity a, Entity b) {
		Entity box = a.get(Collision.class).shape == Shape.BOX ? a : b;
		Entity circle = (box == a) ? b : a;

		Collision circCol = circle.get(Collision.class);
		double cirCX = getCenterX(circle);
		double cirCY = getCenterY(circle);

		double boxCX = getCenterX(box);
		double boxCY = getCenterY(box);
		double boxRot = box.get(Position.class).rotation;
		Collision boxCol = box.get(Collision.class);

		// transform circle center into box's local space
		double localX = cirCX - boxCX;
		double localY = cirCY - boxCY;
		double cos = Math.cos(-boxRot);
		double sin = Math.sin(-boxRot);
		double rotatedX = localX * cos - localY * sin;
		double rotatedY = localX * sin + localY * cos;

		// closest point on the axis-aligned box in local space
		double halfW = boxCol.width / 2;
		double halfH = boxCol.height / 2;
		double closestX = clamp(rotatedX, -halfW, halfW);
		double closestY = clamp(rotatedY, -halfH, halfH);

		double dx = rotatedX - closestX;
		double dy = rotatedY - closestY;
		double distSq = dx * dx + dy * dy;

		if (distSq >= circCol.radius * circCol.radius) return null;

		double dist = Math.sqrt(distSq);

		double nx, ny;
		if (dist == 0) {
			// circle center is inside the box — push along shortest axis
			double overlapX = halfW - Math.abs(rotatedX);
			double overlapY = halfH - Math.abs(rotatedY);
			if (overlapX < overlapY) {
				nx = (rotatedX > 0) ? 1 : -1;
				ny = 0;
				dist = -overlapX;
			} else {
				nx = 0;
				ny = (rotatedY > 0) ? 1 : -1;
				dist = -overlapY;
			}
		} else {
			nx = dx / dist;
			ny = dy / dist;
		}

		double penetration = circCol.radius - dist;

		// rotate normal back to world space
		double worldNX = nx * Math.cos(boxRot) - ny * Math.sin(boxRot);
		double worldNY = nx * Math.sin(boxRot) + ny * Math.cos(boxRot);

		// ensure normal points from box to circle
		double toCirX = cirCX - boxCX;
		double toCirY = cirCY - boxCY;
		if (worldNX * toCirX + worldNY * toCirY < 0) {
			worldNX = -worldNX;
			worldNY = -worldNY;
		}

		// return normal pointing from b to a (consistent with resolve)
		if (box == a) {
			return new double[]{ -worldNX, -worldNY, penetration };
		} else {
			return new double[]{ worldNX, worldNY, penetration };
		}
	}

	// --- collision resolution ---

	private void resolveCollision(Entity a, Entity b, double[] result) {
		Collision colA = a.get(Collision.class);
		Collision colB = b.get(Collision.class);

		if (!colA.solid || !colB.solid) return;

		Position posA = a.get(Position.class);
		Position posB = b.get(Position.class);

		boolean aMovable = a.get(RigidBody.class).movable;
		boolean bMovable = b.get(RigidBody.class).movable;

		// neither can move, nothing to resolve
		if (!aMovable && !bMovable) return;

		double nx = result[0];
		double ny = result[1];
		double penetration = result[2];

		if (aMovable && bMovable) {
			double push = penetration / 2;
			posA.x += nx * push;
			posA.y += ny * push;
			posB.x -= nx * push;
			posB.y -= ny * push;
		} else if (aMovable) {
			posA.x += nx * penetration;
			posA.y += ny * penetration;
		} else {
			posB.x -= nx * penetration;
			posB.y -= ny * penetration;
		}
	}

	// --- geometry helpers ---

	private double[][] getCorners(Entity e) {
		double cx = getCenterX(e);
		double cy = getCenterY(e);
		Collision col = e.get(Collision.class);
		double rot = e.get(Position.class).rotation;

		double halfW = col.width / 2;
		double halfH = col.height / 2;

		// corners relative to center, before rotation
		double[][] local = {
			{ -halfW, -halfH },
			{  halfW, -halfH },
			{  halfW,  halfH },
			{ -halfW,  halfH }
		};

		double cos = Math.cos(rot);
		double sin = Math.sin(rot);

		double[][] corners = new double[4][2];
		for (int i = 0; i < 4; i++) {
			corners[i][0] = cx + local[i][0] * cos - local[i][1] * sin;
			corners[i][1] = cy + local[i][0] * sin + local[i][1] * cos;
		}

		return corners;
	}

	private double[] projectCorners(double[][] corners, double[] axis) {
		double min = Double.MAX_VALUE;
		double max = -Double.MAX_VALUE;

		for (double[] corner : corners) {
			double proj = corner[0] * axis[0] + corner[1] * axis[1];
			if (proj < min) min = proj;
			if (proj > max) max = proj;
		}

		return new double[]{ min, max };
	}

	private double clamp(double val, double min, double max) {
		return Math.max(min, Math.min(max, val));
	}
}
