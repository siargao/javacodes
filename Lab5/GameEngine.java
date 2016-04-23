import java.awt.*;
import java.awt.geom.*;

import javax.swing.*;

import java.awt.event.*;
import java.awt.image.*;
import java.io.*;

import javax.imageio.*;
import javax.sound.sampled.*;

public abstract class GameEngine implements KeyListener, MouseListener, MouseMotionListener {
	//-------------------------------------------------------
	// Members of the GameEngine
	//-------------------------------------------------------
	Graphics2D graphics;
	JFrame frame;
	GamePanel panel;
	boolean initialised = false;

	//-------------------------------------------------------
	// Useful Functions for getting the time etc
	//-------------------------------------------------------
	// Returns the time in milliseconds
	public long getTime() {
		return System.currentTimeMillis();
	}

	// Waits for ms milliseconds
	public void sleep(double ms) {
		try {
			Thread.sleep((long)ms);
		} catch(Exception e) {
			//Do Nothing
		}
	}

	//-------------------------------------------------------
	// Miscellaneous Functions
	//-------------------------------------------------------
	//Function that gives you a random number between 0 and max
	public int rand(int max) {
		return (int)(Math.random() * max);
	}

	// Returns the absolute value of val
	public double abs(double val) {
		return Math.abs(val);
	}

	//Function to give you the distance between two points
	public double distance(double x1, double y1, double x2, double y2) {
		double dx = x1 - x2;
		double dy = y1 - y2;
		return Math.sqrt(dx*dx + dy*dy);
	}

	// Function to give you the length of a vector
	public double length(double x, double y) {
		return Math.sqrt(x*x + y*y);
	}

	public String convertIntegerToString(int i) {
		Integer integer = new Integer(i);
		return integer.toString();
	}

	//-------------------------------------------------------
	// Sound Functions
	//-------------------------------------------------------

	//This function plays a sound file
    //Takes the name of the sound file
    public void playSoundFile(String file_name) {
        Clip clip = null;
        try {
            File clipFile = new File(file_name);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(clipFile);
            clip = AudioSystem.getClip();
            clip.open(audioIn);

            clip.start();

        } catch(Exception e) {
            //clip.close();
        }
    }

	//This function plays a sound file
    //Takes the name of the sound file and a volume in Decibels
    public void playSoundFile(String file_name, float volume) {
        Clip clip = null;
        try {
            File clipFile = new File(file_name);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(clipFile);
            clip = AudioSystem.getClip();
            clip.open(audioIn);
            
            FloatControl volumeControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
            volumeControl.setValue(volume);

            clip.start();

        } catch(Exception e) {
            //clip.close();
        }
    }

    //This function plays a sound file on loop
    //Takes the name of the sound file
    public Clip playSoundFileLoop(String file_name) {
        Clip clip = null;
        try {
            File clipFile = new File(file_name);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(clipFile);
            clip = AudioSystem.getClip();
            clip.open(audioIn);

            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();

        } catch(Exception e) {
            //clip.close();
        }
        return clip;
    }

    //This function plays a sound file
    //Takes the name of the sound file and a volume in Decibels
    public Clip playSoundFileLoop(String file_name, float volume) {
        Clip clip = null;
        try {
            File clipFile = new File(file_name);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(clipFile);
            clip = AudioSystem.getClip();
            clip.open(audioIn);
            
            FloatControl volumeControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
            volumeControl.setValue(volume);

            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();

        } catch(Exception e) {
            //clip.close();
        }
        return clip;
    }

	//-------------------------------------------------------
	// Functions to control the framerate
	//-------------------------------------------------------

	// Two variables to keep track of how much time has passed between frames
	long time = 0, oldTime = 0;

	// Returns the time passed since this function was last called.
	public long measureTime() {
		time = getTime();
		if(oldTime == 0) {
			oldTime = time;
		}
		long passed = time - oldTime;
		oldTime = time;
		return passed;
	}

	//-------------------------------------------------------
	// Functions for setting up the window
	//-------------------------------------------------------
	// Function to create the window and display it
	public void setupWindow(int width, int height) {
		frame = new JFrame();
		panel = new GamePanel();

		frame.setSize(width, height);
		frame.setLocation(200,200);
		frame.setTitle("Window");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(panel);
		frame.setVisible(true);

		panel.setDoubleBuffered(true);
		panel.addMouseListener(this);
		panel.addMouseMotionListener(this);

        // register a key event dispatcher to get a turn in handling all
        // key events, independent of which component currently has the focus
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(new KeyEventDispatcher() {

                    @Override
                    public boolean dispatchKeyEvent(KeyEvent e) {
                        switch (e.getID()) {
                        case KeyEvent.KEY_PRESSED:
                            GameEngine.this.keyPressed(e);
                            return false;
                        case KeyEvent.KEY_RELEASED:
                            GameEngine.this.keyReleased(e);
                            return false;
                        case KeyEvent.KEY_TYPED:
                            GameEngine.this.keyTyped(e);
                            return false;
                        default:
                            return false; // do not consume the event
                        }
                    }
                });

		// Resize the window (insets are just the boards that the Operating System puts on the board)
		Insets insets = frame.getInsets();
		frame.setSize(width + insets.left + insets.right, height + insets.top + insets.bottom);
	}

	public void setWindowSize(int width, int height) {
		// Resize the window (insets are just the boards that the Operating System puts on the board)
		Insets insets = frame.getInsets();
		frame.setSize(width + insets.left + insets.right, height + insets.top + insets.bottom);
		panel.setSize(width, height);
	}

	//-------------------------------------------------------
	// Main Game function
	//-------------------------------------------------------

	// Lab5 main function
	public GameEngine() {
	    SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run()
            {
                // Create the window	
                setupWindow(500,500);
            }
        });
	}

	protected class GameTimer extends Timer {
        private static final long serialVersionUID = 1L;
        private int framerate;
	    
	    protected GameTimer(int framerate, ActionListener listener) {
	        super(1000/framerate, listener);
	        this.framerate = framerate;
	    }
	    
	    protected void setFramerate(int framerate) {
	        if (framerate < 1) framerate = 1;
	        this.framerate = framerate;

	        int delay = 1000 / framerate;
	        setInitialDelay(delay);
	        setDelay(delay);
	    }
	    
	    protected int getFramerate() {
	        return framerate;
	    }
	}
	
	// Main Loop of the game. Runs continuously
	// and calls all the updates of the game and
	// tells the game to display a new frame.
	GameTimer timer = new GameTimer(30, new ActionListener() {
        
        @Override
        public void actionPerformed(ActionEvent e) {
			// Determine the time step
            double passedTime = measureTime();
			double dt = passedTime / 1000.;

			// Update the Game
			update(dt);

			// Tell the Game to draw
			panel.repaint();
        }
    });

	// Initialises and starts the game loop with the given framerate.
	public void gameLoop(int framerate) {
	    initialised = true; // assume init has been called or won't be called

	    timer.setFramerate(framerate);
	    timer.setRepeats(true);

		// Main loop runs until program is closed
	    timer.start();
	}

	//-------------------------------------------------------
	// Initialise function
	//-------------------------------------------------------
	public void init() {}

	//-------------------------------------------------------
	// Update function
	//-------------------------------------------------------
	public abstract void update(double dt);

	//-------------------------------------------------------
	// Paint function
	//-------------------------------------------------------
	protected class GamePanel extends JPanel {
        private static final long serialVersionUID = 1L;

        // This gets called any time the Operating System
	    // tells the program to paint itself
	    public void paintComponent(Graphics g) {
	        // Get the graphics object
	        graphics = (Graphics2D)g;

	        // Rendering settings
	        graphics.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));

	        // Paint the game
	        if (initialised) {
	            GameEngine.this.paintComponent();
	        }
	    }
	}

	// Overridden in subclass
	public abstract void paintComponent();

	//-------------------------------------------------------
	// Keyboard functions
	//-------------------------------------------------------
	
	// Called whenever a key is pressed
	public void keyPressed(KeyEvent e) {

	}

	// Called whenever a key is released
	public void keyReleased(KeyEvent e) {

	}
	
	// Called whenever a key is pressed and immediately released
	public void keyTyped(KeyEvent e) {

	}
	
	//-------------------------------------------------------
	// Mouse functions
	//-------------------------------------------------------
	
	// Called whenever a mouse button is clicked
	// (pressed and released in the same position)
    public void mouseClicked(MouseEvent e) { 

    }

	// Called whenever a mouse button is pressed
    public void mousePressed(MouseEvent e) {
        
    }

	// Called whenever a mouse button is released
    public void mouseReleased(MouseEvent e) {
        
    }

	// Called whenever the mouse cursor enters the game panel
    public void mouseEntered(MouseEvent e) {
        
    }

	// Called whenever the mouse cursor leaves the game panel
    public void mouseExited(MouseEvent e) {
        
    }

    // Called whenever the mouse is moved
	public void mouseMoved(MouseEvent e) {

	}

	// Called whenever the mouse is moved with the mouse button held down
	public void mouseDragged(MouseEvent e) {

	}

	//-------------------------------------------------------
	// Useful Functions for Drawing things on the screen
	//-------------------------------------------------------

	// My Definition of some colors
	Color black = Color.BLACK;
	Color red = Color.RED;
	Color blue = Color.BLUE;
	Color green = Color.GREEN;
	Color white = Color.WHITE;

	//-------------------------------------------------------
	// Changes the background Color to the color c
	public void changeBackgroundColor(Color c) {
		graphics.setBackground(c);
	}

	// Changes the background Color to the color (red,green,blue)
	public void changeBackgroundColor(int red, int green, int blue) {
		graphics.setBackground(new Color(red,green,blue));
	}

	// Clears the background, makes the whole window whatever the background color is
	public void clearBackground(int width, int height) {
		graphics.clearRect(0, 0, width, height);
	}

	//-------------------------------------------------------
	// Changes the drawing Color to the color c
	public void changeColor(Color c) {
		graphics.setColor(c);
	}

	// Changes the drawing Color to the color (red,green,blue)
	public void changeColor(int red, int green, int blue) {
		graphics.setColor(new Color(red,green,blue));
	}

	//-------------------------------------------------------
	// This function draws a rectangle at (x,y) with width and height (w,h)
	void drawRectangle(double x, double y, double w, double h) {
		graphics.draw(new Rectangle2D.Double(x, y, w, h));
	}

	// This function fills in a rectangle at (x,y) with width and height (w,h)
	void drawSolidRectangle(double x, double y, double w, double h) {
		graphics.fill(new Rectangle2D.Double(x, y, w, h));
	}

	//-------------------------------------------------------
	// This function draws a line from (x1,y2) to (x2,y2)
	// Usage: drawLine(g, 50, 50, 250, 250);
	// Where: public void paint(Graphics g)
	void drawLine(double x1, double y1, double x2, double y2) {
    	graphics.draw(new Line2D.Double(x1, y1, x2, y2));
	}

	//-------------------------------------------------------
	// This function draws a circle at (x,y) with radius
	// Usage: drawCircle(g, 100, 100, 50);
	// Where: public void paint(Graphics g)
	void drawCircle(double x, double y, double radius) {
	    graphics.draw(new Ellipse2D.Double(x-radius, y-radius, radius*2, radius*2));
	}

	// This function draws a circle at (x,y) with radius
	// Usage: drawCircle(g, 100, 100, 50);
	// Where: public void paint(Graphics g)
	void drawSolidCircle(double x, double y, double radius) {
	    graphics.fill(new Ellipse2D.Double(x-radius, y-radius, radius*2, radius*2));
	}

	//-------------------------------------------------------
	// Functions to Draw Text on a window
	// Usage: drawText(g, 100, 100, "Hello World");
	// Where: public void paint(Graphics g)
	public void drawText(double x, double y, String s) {
	    graphics.setFont(new Font("Arial", Font.BOLD, 40));
	    graphics.drawString(s, (int)x, (int)y);
	}

	// Functions to Draw Text on a window
	// Usage: drawText(g, 100, 100, "Hello World", 50);
	// Where: public void paint(Graphics g)
	public void drawText(double x, double y, String s, int size) {
	    graphics.setFont(new Font("Arial", Font.BOLD, size));
	    graphics.drawString(s, (int)x, (int)y);
	}

	//-------------------------------------------------------
	// Image Functions
	//-------------------------------------------------------

	// Loads an image from file
    public Image loadImage(String s) {
    	Image image = null;
    	try {
		    image = ImageIO.read(new File(s));
		} catch (IOException e) {
			System.out.println("Error: could not load image " + s);
			System.exit(1);
		}
		return image;
    }

    // Loads a subImage out of an image
    public Image subImage(Image source, int x, int y, int w, int h) {
    	// Extract the subImage
    	BufferedImage buffered = (BufferedImage)source;
    	Image image = buffered.getSubimage(x, y, w, h);
    	return image;
    }

    //-------------------------------------------------------
    // Draws an image on the screen at position (x,y)
    public void drawImage(Image image, double x, double y) {
		graphics.drawImage(image, (int)x, (int)y, null);
    }

    // Draws an image on the screen at position (x,y)
    public void drawImage(Image image, double x, double y, double w, double h) {
		// Draw the asteroid sprite on the screen
		graphics.drawImage(image, (int)x, (int)y, (int)w, (int)h, null);
    }

    //-------------------------------------------------------
	// Transform Functions
	//-------------------------------------------------------

    //Saves the current transform
    AffineTransform transform = null;
    public void saveCurrentTransform() {
    	transform = graphics.getTransform();
    }

    //Restores the last transform
    public void restoreLastTransform() {
    	if(transform != null) {
    		graphics.setTransform(transform);
    	}
    }

    //This function translates the drawing context by (x,y)
	void translate(double x, double y) {
		graphics.translate(x,y);
	}

	//This function rotates the drawing context by a degrees
	void rotate(double a) {
		graphics.rotate(Math.toRadians(a));
	}

	//This function scales the drawing context by (x,y)
	void scale(double x, double y) {
		graphics.scale(x, y);
	}

	//This function shears the drawing context by (x,y)
	void shear(double x, double y) {
		graphics.shear(x, y);
	}
}