package core;

import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ComponentAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

//WINDOW IS JUST THE ACTUAL WINDOW
//GAME PANEL IS WHERE THE DRAWING WILL HAPPEN.

public class Window extends JFrame{
	
	private Engine engine;
	
	private GamePanel renderSurface;
	
	public Window(String name) {
		GraphicsDevice device = GraphicsEnvironment
		        .getLocalGraphicsEnvironment()
		        .getDefaultScreenDevice();
		
		renderSurface = new GamePanel();
		renderSurface.setBackground(Color.black);
		
		this.setUndecorated(true);//REMOVE BORDERS
		this.setResizable(false);//DONT RESIZE FULL SCREEN?!
		
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		//this here is to allow for a more cleaner close
		//possibly put save data in here.
		this.addWindowListener(new WindowAdapter(){
		    @Override
		    public void windowClosing(WindowEvent e) {
		        engine.stop();      
		        dispose();
		        System.exit(0);
		    }
		});
		
		//THIS IS THE DRAWING SURFACE LETS GOOO
		this.setContentPane(renderSurface);
		
		//set title
		this.setTitle(name);

		//we are adding the frame to the devices screen
		device.setFullScreenWindow(this);
	}
	
	public void setEngine(Engine e) {
		this.engine = e;
	}
	
	public GamePanel getRenderSurface() {
		return this.renderSurface;
	}
}
