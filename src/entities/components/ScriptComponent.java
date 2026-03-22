package entities.components;

import entities.Entity;
import entities.EntityManager;

/**
 * Wraps a {@link Script} lambda to run custom logic each frame.
 * Receives the owning entity, the entity manager, and delta time.
 */
public class ScriptComponent extends Component {
	private final Script script;

	public ScriptComponent(Script script) {
		this.script = script;
	}

	public void update(Entity self, EntityManager entityManager, double dt) {
		script.update(self, entityManager, dt);
	}
}
