
public class Render3D extends Render {

	private Game game = Game.getGame();
	public double[] zBuffer;
	private int renderDistance = 5000;
	
	private double floorPosition = 8.0;
	private double ceilingPosition = 20.0;
	private double rotation = game.controller.rotation;
	private double jump = game.controller.y;
	private double cosine = Math.cos(rotation);
	private double sine = Math.sin(rotation);
	private double forward = game.controller.z;
	private double right = game.controller.x;
	private double up = game.controller.y;

	

	public Render3D(int width, int height) {
		super(width, height);
		zBuffer = new double[width * height + 1];
		// TODO Auto-generated constructor stub
	}

	public void floor() {

		
		for (int y = 0; y < height; y++) {

			double ceiling = (y + -height / 2.0) / height;

			double z = (this.floorPosition + jump) / ceiling;
			// System.out.println(z + "" + "this is after hitting jump button");
			if (ceiling < 0) {
				z = (this.ceilingPosition - jump) / -ceiling;
			}

			for (int x = 0; x < width; x++) {
				double depth = (x - width) / 2.0 / height;
				depth *= z;
				double xx = depth * this.cosine + z * this.sine;
				double yy = z * this.cosine - depth * this.sine;
				int xPix = (int) (xx + this.right);
				int yPix = (int) (yy + this.forward);

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

			for (int y = yPixelTopInt; y < yPixelBottomInt; y++) {
				pixels[x + y * width] = 0x06c98f;
				zBuffer[x + y * width] = 0;
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
