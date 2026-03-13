package entities.components;

import entities.Entity;
import entities.EntityManager;

public abstract class Script extends Component {
	public abstract void update(Entity self, EntityManager entityManager, double dt);
}
