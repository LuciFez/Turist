package controller;

public class Main {

	private static ServerController controller;
	
	public static void main(String args[]) {
		
		controller = new ServerController();
		controller.start();
		
	}
	
}
