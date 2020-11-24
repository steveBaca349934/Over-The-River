package gui;

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
		double ceilingPosition = 35.0;
		double rotation = Game.getGame().controller.rotation;
		double jump = Game.getGame().controller.y;
		double forward = Game.getGame().controller.z;
		double right = Game.getGame().controller.x;
		double up = Game.getGame().controller.y;

		double[] gameControlsArray = { floorPosition, ceilingPosition, rotation, jump, forward, right, up };

		return gameControlsArray;

	}

	
	
	
	/**
	 * @author stevebaca
	 * @since 11/22
	 * Right now this is rendering both the floor and the ceiling which is obviously not ideal... going to try and figure out how to 
	 * render the ceiling seperately
	 */
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

		for (int y = 0; y < height; y++) {

			double ceiling = (y - height / 2.0) / height;

			double z = (floorPosition + jump) / ceiling;
			// System.out.println(z + "" + "this is after hitting jump button");
			if (ceiling < 0) {
				z = (ceilingPosition - jump) / -ceiling;
			}

			for (int x = 0; x < width; x++) {
				double depth = (x - width / 2.0) / height;
				depth *= z;
				double xx = depth * cosine + z * sine;
				double yy = z * cosine - depth * sine;

				// System.out.println(yy);
				int xPix = (int) (xx + right);
				int yPix = (int) (yy + forward);
				

				zBuffer[x + y * width] = z;

				pixels[x  + y * width] = Texture.floor.pixels[(xPix & 7) + (yPix & 7) * 8];

				// Texture.floor.pixels[(xPix & 7) + (yPix & 7) * 8];

				if (z > 200) {
					pixels[x + y * width] = 0;
				}
			}

		}

	}
	
	/**
	 * @author stevebaca
	 * @since 11/22
	 * Right now this is rendering both the floor and the ceiling which is obviously not ideal... going to try and figure out how to 
	 * render the ceiling seperately
	 */
	public void ceiling() {

		double[] gameControlsArray = Render3D.distributeGameControlsLocally();

		double floorPosition = gameControlsArray[0];
		double ceilingPosition = gameControlsArray[1];
		double rotation = gameControlsArray[2];
		double jump = gameControlsArray[3];
		double cosine = Math.cos(rotation);
		double sine = Math.sin(rotation);
		double forward = gameControlsArray[4];
		double right = gameControlsArray[5];

		for (int y = 0; y < height/2; y++) {

			double ceiling = (y - height / 1.65) / height;

			double z = (floorPosition + jump) / ceiling;
			// System.out.println(z + "" + "this is after hitting jump button");
			if (ceiling < 0) {
				z = (ceilingPosition - jump) / -ceiling;
			}

			for (int x = 0; x < width; x++) {
				double depth = (x - width / 2.0) / height;
				depth *= z;
				double xx = depth * cosine + z * sine;
				double yy = z * cosine - depth * sine;

				// System.out.println(yy);
				int xPix = (int) (xx + right);
				int yPix = (int) (yy + forward);

				zBuffer[x + y * width] = z;

				pixels[x  + y * width] = Texture.ceiling.pixels[(xPix & 100) + (yPix & 100) * 800];

				// Texture.floor.pixels[(xPix & 7) + (yPix & 7) * 8];
				
				if (z > 200) {
					pixels[x + y * width] = 0;
				}
			}

		}

	}

	/**
	 * Attempting to recreate the floor method in order to get it right because
	 * right now the game isn't really rendering properly Right now when you try and
	 * move you go diagonoly instead of forward
	 */
	public void floorTest() {
		for (int y = 0; y < height; y++) {
			double ceiling = (y - height / 2.0) / height;
			double z = 8.0 / ceiling;

			for (int x = 0; x < width; x++) {
				double depth = (x - width / 2.0) / height;
				depth *= z;
				int xx = (int) (depth) & 15;
				int yy = (int) (z) & 15;
				pixels[x + y * width] = (xx * 16) | (yy * 16) << 8;
			}

		}

	}

	/**
	 * @author stevebaca
	 * @version 1.0
	 * @since 11/22
	 * @param xLeft
	 * @param xRight
	 * @param zDistance
	 * @param yHeight
	 */
	public void renderWall(double xLeft, double xRight, double zDistanceLeft, double zDistanceRight, double yHeight) {

		double[] gameControlsArray = Render3D.distributeGameControlsLocally();

		double rotation = gameControlsArray[2];
		double cosine = Math.cos(rotation);
		double sine = Math.sin(rotation);
		double forward = gameControlsArray[4];
		double right = gameControlsArray[5];
		double up = gameControlsArray[6];
		
		double upCorrect = 0.062;
		double rightCorrect = 0.062;
		double forwardCorrect = 0.062;


		// xfLeft -> Left calculation
		double xcLeft = ((xLeft) - (right * rightCorrect)) * 2.0;
		// zcLeft -> Zedd calculation
		double zcLeft = ((zDistanceLeft) - (forward * forwardCorrect)) * 2.0;

		double rotLeftSideX = xcLeft * cosine - zcLeft * sine;

		double yCornerTL = ((-yHeight) - (-up * upCorrect)) * 2.0;
		double yCornerBL = ((+0.5 - yHeight) - (-up * upCorrect)) * 2.0;
		double rotLeftSideZ = zcLeft * cosine + xcLeft * sine;

		double xcRight = ((xRight) - (right * rightCorrect)) * 2.0;
		double zcRight = ((zDistanceRight) - (forward * forwardCorrect)) * 2.0;

		double rotRightSideX = xcRight * cosine - zcRight * sine;
		double yCornerTR = ((-yHeight) - (-up * upCorrect)) * 2.0;
		double yCornerBR = ((+0.5 - yHeight) - (-up * upCorrect)) * 2.0;
		double rotRightSideZ = zcRight * cosine + xcRight * sine;

		double xPixelLeft = (rotLeftSideX / rotLeftSideZ * height + width / 2.0);
		double xPixelRight = (rotRightSideX / rotRightSideZ * height + width / 2.0);

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

		//
		double yPixelLeftTop = (yCornerTL / rotLeftSideZ * height + height / 2.0);
		double yPixelLeftBottom = (yCornerBL / rotLeftSideZ * height + height / 2.0);
		double yPixelRightTop = (yCornerTR / rotRightSideZ * height + height / 2.0);
		double yPixelRightBottom = (yCornerBR / rotRightSideZ * height + height / 2.0);

		double tex1 = 1.0 / rotLeftSideZ;
		double tex2 = 1.0 / rotRightSideZ;
		double tex3 = 0.0 / rotLeftSideZ;
		double tex4 = 8.0 / rotRightSideZ - tex3;

		// This part of the method will actually render the pixels
		for (int x = xPixelLeftInt; x < xPixelRightInt; x++) {
			double pixelRotation = (x - xPixelLeft) / (xPixelRight - xPixelLeft);

			int xTexture = (int) ((tex3 + tex4 * pixelRotation) / (tex1 + (tex2 - tex1) * pixelRotation));

			double yPixelTop = yPixelLeftTop + (yPixelRightTop - yPixelLeftTop) * pixelRotation;
			double yPixelBottom = yPixelLeftBottom + (yPixelRightBottom - yPixelLeftBottom) * pixelRotation;

			int yPixelTopInt = (int) (yPixelTop);
			int yPixelBottomInt = (int) (yPixelBottom);

			if (yPixelTopInt < 0) {
				yPixelTopInt = 0;
			}
			if (yPixelBottomInt > height) {
				yPixelBottomInt = height;
			}

			for (int y = yPixelTopInt; y < yPixelBottomInt; y++) {
				double pixelRotationY = (y - yPixelTop) / (yPixelBottom - yPixelTop);
				int yTexture = (int) (8 * pixelRotationY);
				pixels[x + y * width] = xTexture * 100 + yTexture * 100 * 256;

				zBuffer[x + y * width] = 1 / (tex1 + (tex2 - tex1) * pixelRotation) * 16;

			}

		}

	}
	
	
	/**
	 * @author stevebaca
	 * @since 11/22
	 * Trying to render a sword for my first person character.....
	 */
	public void sword() {
		double[] gameControlsArray = Render3D.distributeGameControlsLocally();
		int length = 10;
		int width = 5;
		
		double rotation = gameControlsArray[2];
		double jump = gameControlsArray[3];
		double cosine = Math.cos(rotation);
		double sine = Math.sin(rotation);
		double forward = gameControlsArray[4];
		double right = gameControlsArray[5];

		for (int y = 0; y < length; y++) {

			double swordLength = y;

			double z = 1.2;
			

			for (int x = 0; x < 5; x++) {
				double depth = x;
				depth *= z;
				double xx = depth * cosine + z * sine;
				double yy = z * cosine - depth * sine;

				// System.out.println(yy);
				int xPix = (int) (xx + right);
				int yPix = (int) (yy + forward);
				

				//zBuffer[x + y * width] = z;

				pixels[x  + y * width] = 0xa597a8;

				// Texture.floor.pixels[(xPix & 7) + (yPix & 7) * 8];

			}

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
