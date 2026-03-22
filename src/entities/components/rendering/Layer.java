package entities.components.rendering;

import entities.components.Component;

/**
 * Controls rendering draw order. Higher values render on top.
 */
public class Layer extends Component{
	/** Z-order for rendering. */
	public int layerLevel = 0;
	
	public int getLayerLevel() {
		return this.layerLevel;
	}
	
	public Layer setLayerLevel(int layerLevel) {
		this.layerLevel = layerLevel;
		return this;
	}
}
