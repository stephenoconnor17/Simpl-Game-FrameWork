package core;

public class Main {
	public static void main(String[] args) {
		Window w = new Window("Hello");
		Engine e =  new Engine(w.getRenderSurface());
		w.setEngine(e);
		
		e.run();
	}
}
