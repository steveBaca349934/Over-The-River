public class Controller {

	public double x = 0, z = 0, y = 0, rotation = 0, xa = 0, za = 0, rotationa = 0;
	public static boolean turnLeft = false;
	public static boolean turnRight = false;
	public static boolean up = false;
	public static boolean down = false;

	public void tick(boolean forward, boolean back, boolean left, boolean right, boolean jump, boolean crouch,
			boolean sprint) {
		double rotationSpeed = .01, walkSpeed = 0.5, jumpHeight = 0.5;
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

}
