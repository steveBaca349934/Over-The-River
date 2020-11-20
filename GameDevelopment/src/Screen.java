
public class Screen extends Render {

	private Render3D render3d;
	

	public Screen(int width, int height) {
		super(width, height);
		render3d = new Render3D(width, height);
		
		// TODO Auto-generated constructor stub
	}

	public void render() {

		render3d.floor();
		render3d.rednerDistanceLimiter();
		render3d.renderWall(0, 10, 1, 0);
		draw(render3d, 0, 0);
	}

}
