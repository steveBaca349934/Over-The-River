
public class Render3D extends Render {

	public double[] zBuffer;
	private int renderDistance = 5000;

	public Render3D(int width, int height) {
		super(width, height);
		zBuffer = new double[width * height + 1];
		// TODO Auto-generated constructor stub
	}

	/**
	 * @author stevebaca
	 * @since 11/20/2020
	 * @return an array that contains all of the game controls that can then be
	 *         parsed...
	 */
	private static double[] distributeGameControlsLocally() {

		// Question: Why use an array?
		// Answer: To access the elements in the array (which we will do by referencing
		// their index, this will result in big O time complexity
		// of O(1)

		double floorPosition = 8.0;
		double ceilingPosition = 20.0;
		double rotation = Game.getGame().controller.rotation;
		double jump = Game.getGame().controller.y;
		double cosine = Math.cos(rotation);
		double sine = Math.sin(rotation);
		double forward = Game.getGame().controller.z;
		double right = Game.getGame().controller.x;
		double up = Game.getGame().controller.y;

		double[] gameControlsArray = { floorPosition, ceilingPosition, rotation, jump, forward, right, up };

		return gameControlsArray;

	}

	public void floor() {

		double[] gameControlsArray = Render3D.distributeGameControlsLocally();

		double floorPosition = gameControlsArray[0];
		double ceilingPosition = gameControlsArray[1];
		double rotation = gameControlsArray[2];
		double jump = gameControlsArray[3];
		double cosine = Math.cos(rotation);
		double sine = Math.sin(rotation);
		double forward = gameControlsArray[4];
		double right = gameControlsArray[5];
		double up = gameControlsArray[6];

		for (int y = 0; y < height; y++) {

			double ceiling = (y + -height / 2.0) / height;

			double z = (floorPosition + jump) / ceiling;
			// System.out.println(z + "" + "this is after hitting jump button");
			if (ceiling < 0) {
				z = (ceilingPosition - jump) / -ceiling;
			}

			for (int x = 0; x < width; x++) {
				double depth = (x - width) / 2.0 / height;
				depth *= z;
				double xx = depth * cosine + z * sine;
				double yy = z * cosine - depth * sine;
				int xPix = (int) (xx + right);
				int yPix = (int) (yy + forward);

				zBuffer[x + y * width] = z;
				pixels[x + y * width] = Texture.floor.pixels[(xPix & 7) + (yPix & 7) * 8];

				if (z > 200) {
					pixels[x + y * width] = 0;
				}
			}

		}

	}

	/**
	 * @author stevebaca
	 * @version 1.0
	 * @since today
	 * @param xLeft
	 * @param xRight
	 * @param zDistance
	 * @param yHeight
	 */
	public void renderWall(double xLeft, double xRight, double zDistance, double yHeight) {
		
		double[] gameControlsArray = Render3D.distributeGameControlsLocally();

		double rotation = gameControlsArray[2];
		double jump = gameControlsArray[3];
		double cosine = Math.cos(rotation);
		double sine = Math.sin(rotation);
		double forward = gameControlsArray[4];
		double right = gameControlsArray[5];
		double up = gameControlsArray[6];
		
		// xfLeft -> Left calculation
		double xcLeft = ((xLeft) - right) * 2;
		// zcLeft -> Zedd calculation
		double zcLeft = ((zDistance) - forward) * 2;

		double rotLeftSideX = xcLeft * cosine - zcLeft * sine;

		double yCornerTL = ((-yHeight) - up) * 2;
		double yCornerBL = ((+0.5 - yHeight) - up) * 2;
		double rotLeftSideZ = zcLeft * cosine + xcLeft * sine;

		double xcRight = ((xRight) - right) * 2;
		double zcRight = ((zDistance) - forward) * 2;

		double rotRightSideX = xcRight - xcRight * cosine - zcRight * sine;
		double yCornerTR = ((-yHeight) - up) * 2;
		double yCornerBR = ((+0.5 - yHeight) - up) * 2;
		double rotRightSideZ = zcRight * cosine + xcRight * sine;

		double xPixelLeft = (rotLeftSideX / rotLeftSideZ * height + width / 2);
		double xPixelRight = (rotRightSideX / rotRightSideX * height + width / 2);

		// if left pixels overlap with the right pixels, then return aka get out of this
		// method
		if (xPixelLeft >= xPixelRight) {
			return;
		}

		int xPixelLeftInt = (int) (xPixelLeft);
		int xPixelRightInt = (int) (xPixelRight);

		// We don't want either of these variables to have the ability to go
		// negative/beyond the width
		// with these if statements we are saving resources
		if (xPixelLeftInt < 0) {
			xPixelLeftInt = 0;

		}
		if (xPixelRightInt > width) {
			xPixelRightInt = width;

		}

		// casting previously made variables into integer variables
		double yPixelLeftTop = (yCornerTL / rotLeftSideX * height + height / 2);
		double yPixelLeftBottom = (yCornerBL / rotLeftSideZ * height + height / 2);
		double yPixelRightTop = (yCornerTR / rotRightSideZ * height + height / 2);
		double yPixelRightBottom = (yCornerBR / rotRightSideZ * height + height / 2);

		// This part of the method will actually render the pixels
		for (int x = xPixelLeftInt; x < xPixelRightInt; x++) {
			double pixelRotation = (x - xPixelLeft) / (xPixelRight - xPixelLeft);

			double yPixelTop = yPixelLeftTop + (yPixelRightTop - yPixelLeftTop) * pixelRotation;
			double yPixelBottom = yPixelLeftBottom + (yPixelRightBottom - yPixelLeftBottom) * pixelRotation;

			int yPixelTopInt = (int) (yPixelTop);
			int yPixelBottomInt = (int) (yPixelBottom);

			if (yPixelTopInt < 0) {
				yPixelTopInt = 0;
			}
			if (yPixelTopInt > height) {
				yPixelTopInt = height;
			}

			/*
			 * for (int y = yPixelTopInt; y < yPixelBottomInt; y++) { //pixels[x + y *
			 * width] = 0x06c98f; zBuffer[x + y * width] = 0; }
			 */

		}

	}

	public void rednerDistanceLimiter() {
		for (int i = 0; i < width * height; i++) {
			int color = pixels[i];
			int brightness = (int) (renderDistance / zBuffer[i]);

			if (brightness < 0) {
				brightness = 0;
			}

			if (brightness > 255) {
				brightness = 255;
			}

			int r = (color >> 16) & 0xff;
			int g = (color >> 8) & 0xff;
			int b = (color) & 0xff;

			r = r * brightness / 255;
			g = g * brightness / 255;
			b = b * brightness / 255;

			pixels[i] = r << 16 | g << 8 | b;
		}
	}

}
