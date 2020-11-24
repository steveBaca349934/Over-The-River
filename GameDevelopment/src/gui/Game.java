package gui;

import java.awt.event.KeyEvent;
import inputHandler.*;
import level.Level;

public class Game {
	public int time;
	public Controller controller;
	public Level level;

	// private constructor
	private Game() {
		controller = Controller.getController();
		level = new Level(20, 20);
	}

	// semi singleton instance
	private static Game game = new Game();

	// singleton accessor method
	public static Game getGame() {
		return game;
	}

	/**
	 * @param key this is a boolean array that is filled with whether every possible
	 *            action is true or false at any given time....
	 */
	public void tick(boolean[] key) {
		time += .50;
		boolean forward = key[KeyEvent.VK_W];
		boolean back = key[KeyEvent.VK_S];
		boolean right = key[KeyEvent.VK_D];
		boolean left = key[KeyEvent.VK_A];
		boolean jump = key[KeyEvent.VK_SPACE];
		boolean crouch = key[KeyEvent.VK_CONTROL];
		boolean sprint = key[KeyEvent.VK_SHIFT];

		controller.tick(forward, back, left, right, jump, crouch, sprint);

	}

}
