package entities.components;

import core.Engine;

public abstract class Component {
	public boolean dirty = false;

	public void markDirty() {
		if (Engine.isOnline()) {
			this.dirty = true;
		}
	}
}
