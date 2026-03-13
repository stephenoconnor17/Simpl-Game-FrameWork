package entities.components.rendering;

import entities.Entity;
import entities.components.Component;

public class Camera extends Component{
	public Entity target;

	// user-defined offset to shift the focus point
	public double userOffsetX;
	public double userOffsetY;

	// computed by RenderingSystem each frame
	public double offsetX;
	public double offsetY;
}
