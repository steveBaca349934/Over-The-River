package inputHandler;

public class Controller {

	public double x = 0, z = 0, y = 0, rotation = 0, xa = 0, za = 0, rotationa = 0;
	public static boolean turnLeft = false;
	public static boolean turnRight = false;
	public static boolean up = false;
	public static boolean down = false;
	private int newX = 0;
	private int newY = 0;
	private int oldX = 0;
	private int oldY = 0;
	public static int mouseSpeed;

	// private constructor
	private Controller() {

	}

	// semi singleton instance
	private static Controller controller = new Controller();

	// singleton accessor method
	public static Controller getController() {
		return controller;
	}

	/**
	 * @author stevebaca
	 * @since 11/23 No idea why in the world this is called tick, it just doesn't
	 *        make any sense at all....
	 * 
	 * @param forward
	 * @param back
	 * @param left
	 * @param right
	 * @param jump
	 * @param crouch
	 * @param sprint
	 */
	public void tick(boolean forward, boolean back, boolean left, boolean right, boolean jump, boolean crouch,
			boolean sprint) {
		double rotationSpeed = .000065 * mouseSpeed, walkSpeed = 0.5, jumpHeight = 0.5;
		double xMove = 0, zMove = 0;

		if (forward) {
			zMove++;
		}
		if (back) {
			zMove--;
		}
		if (left) {
			xMove--;
		}
		if (right) {
			xMove++;
		}
		if (turnRight) {
			rotationa += rotationSpeed;
		}
		if (turnLeft) {
			rotationa -= rotationSpeed;
		}

		if (up) {

		}

		if (down) {

		}

		if (jump) {
			y += jumpHeight;
			sprint = false;

		}

		if (crouch) {
			y -= jumpHeight;
			sprint = false;
			walkSpeed = 0.1;
		}

		if (sprint) {
			walkSpeed = 2;
		}

		xa += (xMove * Math.cos(rotation) + zMove * Math.sin(rotation) * walkSpeed);
		za += (zMove * Math.cos(rotation) + zMove * Math.sin(rotation) * walkSpeed);
		y *= 0.9;
		x += xa;
		z += za;
		xa *= 0.1;
		za *= 0.1;
		rotation += rotationa;
		rotationa *= 0.8;

	}

	/**
	 * handles rotation through mouse movement
	 */
	public void rotationHandler() {
		newX = inputHandler.mouseX;
		if (newX > oldX) {
			Controller.turnRight = true;
		}
		if (newX < oldX) {
			Controller.turnLeft = true;
		}
		if (newX == oldX) {
			Controller.turnRight = false;
			Controller.turnLeft = false;

		}
		
		// speeds mouse movement up if you move the mouse faster
		mouseSpeed = Math.abs(newX - newY);

		oldX = newX;

		newY = inputHandler.mouseY;
		if (newY > oldY) {
			// System.out.println("Up");
			Controller.up = true;
		}
		if (newY < oldY) {
			// System.out.println("Down");
			Controller.down = true;

		}
		if (newY == oldY) {
			// System.out.println("Still!!!!!");
			Controller.up = false;
			Controller.down = false;

		}
		oldY = newY;
	}

}
