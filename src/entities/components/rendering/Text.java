package entities.components.rendering;

import java.awt.Color;
import java.awt.Font;

import entities.components.Component;

public class Text extends Component{
	
	public String text;
	public int size;
	public Color colour;
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
