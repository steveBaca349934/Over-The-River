package level;

import java.util.Random;

import gui.Game;
import gui.Render3D;

public class Level {

	public Block[] blocks;
	public final int width, height;
	public Game game;

	public Level(int width, int height) {
		this.width = width;
		this.height = height;
		blocks = new Block[width * height];
		game = Game.getGame();
		Random random = new Random();
		
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				Block block = null;

				// pick a number between 0 and 4, and if number is 0 be a solid block
				if (random.nextInt(4) == 0) {
					block = new SolidBlock();
				} else {
					block = new Block();
				}
				blocks[x + y * width] = block;
			}

		}
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @return Block
	 */
	public Block create(int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height) {
			return Block.solidWall;
		}
		return blocks[x + y * width];
	}



}
