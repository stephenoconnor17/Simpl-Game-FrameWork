package entities.components.rendering;

import java.awt.Color;
import java.awt.Font;

import entities.components.Component;

/**
 * Renders a text string on this entity.
 * <p>
 * Works in both world-space (positioned by {@link entities.components.transform.Position Position})
 * and screen-space (positioned by {@link UIElement} anchors). The RenderingSystem
 * draws the text with anti-aliasing disabled for crisp pixel-art style.
 */
public class Text extends Component{

	/** The string to display. */
	public String text;
	/** Font size in points (used when constructing the Font). */
	public int size;
	/** Text colour. */
	public Color colour;
	/** The AWT font used for rendering. */
	public Font font;
	
	public Text setText(String text) {
		this.text = text;
		return this;
	}
	
	public Text setSize(int size) {
		this.size = size;
		return this;
	}
	
	public Text setColour(Color colour) {
		this.colour = colour;
		return this;
	}
	
	public Text setFont(Font font) {
		this.font = font;
		return this;
	}
	
}
