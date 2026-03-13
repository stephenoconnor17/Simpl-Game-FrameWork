package entities.components.rendering;

import entities.components.Component;

public class Layer extends Component{
	public int layerLevel = 0;
	
	public int getLayerLevel() {
		return this.layerLevel;
	}
	
	public Layer setLayerLevel(int layerLevel) {
		this.layerLevel = layerLevel;
		return this;
	}
}
