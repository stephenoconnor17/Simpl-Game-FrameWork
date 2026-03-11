package entities.components.rendering;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import entities.components.Component;

public class Sprite extends Component {
	public BufferedImage image;
	
	public Sprite setImage(String imageLink) {
		try {
			System.out.println(getClass().getClassLoader().getResource("sprites/bluesquare.png"));

			image = ImageIO.read(getClass().getClassLoader().getResource(imageLink));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return this;
	}
	
	public Sprite setImageLink(String imageLink) {
		imageLink = "sprites/"+imageLink;
		return setImage(imageLink);
	}
}
