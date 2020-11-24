package main;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import javax.swing.JFrame;
import inputHandler.*;
import gui.*;

public class Display extends Canvas implements Runnable {

	//The class where everything is compiled and put together 
	
	private static final long serialVersionUID = 4573326356792377108L;
	public static int Width = 800;
	public static int Height = 600;
	static String Title = "Beyond the River";

	private Thread thread;
	private boolean running = false;
	// private Render render;
	private Screen screen;
	private BufferedImage BI;
	private int[] pixels;
	private Game game;
	private inputHandler input;
	private Controller controller;
	
	private int frames;

	public Display() {
		Dimension size = new Dimension(Width, Height);
		screen = new Screen(Width, Height);
		BI = new BufferedImage(Width, Height, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt) BI.getRaster().getDataBuffer()).getData();
		game = Game.getGame();
		setPreferredSize(size);
		setMinimumSize(size);
		input = new inputHandler();
		addKeyListener(input);
		addFocusListener(input);
		addMouseListener(input);
		addMouseMotionListener(input);
		controller = Controller.getController();

	}

	private void start() {
		if (running) {
			return;
		}

		running = true;
		thread = new Thread(this);
		thread.start();
	}

	private void stop() {

		if (!running) {
			return;
		}

		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {

		double unprocessedSeconds = 0;
		long previousTime = System.nanoTime();
		double secondsPerTick = 1 / 60.0;
		int tickCount = 0;
		boolean tick = false;

		requestFocus();
		while (running) {

			long currentTime = System.nanoTime();
			long passedTime = currentTime - previousTime;
			previousTime = currentTime;
			unprocessedSeconds += passedTime / 1000000000.0;
			requestFocus();
			while (unprocessedSeconds > secondsPerTick) {

				tick();
				unprocessedSeconds -= secondsPerTick;
				tick = true;
				tickCount++;
				if (tickCount % 100 == 0) {
					System.out.println(this.frames + " " + "FPS");
					previousTime += 1000;
					this.frames = 0;
				}

			}

			if (tick == true) {
				render();
				frames++;
			}
			render();
			frames++;
			controller.rotationHandler();

		}

	}

	
	/**
	 * Filling an array that is currently empty/constructed in this class will pixels form the Screen class
	 */
	private void render() {

		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}

		screen.render();

		for (int i = 0; i < Width * Height; i++) {
			pixels[i] = screen.pixels[i];
		}

		Graphics g = bs.getDrawGraphics();
		g.drawImage(BI, 0, 0, Width + 10, Height + 10, null);
		
		// printing frames per second every 60 seconds
		if (game.time % 60 == 0) {
			g.drawString(Integer.toString(frames) + "FPS", 0, 20);
		}
		
		
		g.dispose();
		bs.show();
	}

	
	/**
	 * 
	 */
	private void tick() {

		game.tick(input.key);

	}

	public static void main(String[] args) {

		Display display = new Display();
		JFrame frame = new JFrame();
		frame.add(display);
		frame.pack();
		frame.setSize(Width, Height);
		frame.setTitle(Title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);

		frame.setResizable(false);
		frame.setVisible(true);
		display.start();

	}

}
