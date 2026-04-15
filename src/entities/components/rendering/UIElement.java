package entities.components.rendering;

import entities.components.Component;

/**
 * Marks an entity as a UI element.
 * <p>
 * When {@code screenSpace} is {@code true} (the default), the entity is rendered
 * in screen coordinates, ignoring the camera transform — ideal for HUDs, menus,
 * and overlays. When {@code false}, the entity renders in world coordinates like
 * any other entity — useful for health bars above enemies or floating nameplates.
 * <p>
 * Child entities inherit {@code screenSpace} from their root parent, so you only
 * need to set it on the top-level UI entity (e.g. an inventory panel).
 */
public class UIElement extends Component {
	/** If true, render in screen coordinates (no camera transform). */
	public boolean screenSpace = true;

	public UIElement setScreenSpace(boolean screenSpace) {
		this.screenSpace = screenSpace;
		return this;
	}
}
