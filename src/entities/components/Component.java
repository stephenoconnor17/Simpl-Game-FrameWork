package entities.components;

import core.Engine;

/** Base class for all components. Provides a dirty flag gated behind {@link Engine#isOnline()} for network sync. */
public abstract class Component {
	public boolean dirty = false;

	public void markDirty() {
		if (Engine.isOnline()) {
			this.dirty = true;
		}
	}
}
