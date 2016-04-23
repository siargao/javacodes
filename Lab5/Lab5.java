import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.awt.event.*;

public class Lab5 extends GameEngine {
	// Main Function
	public static void main(String args[]) {
		// Warning - don't edit this function.

		// Create a new game.
		GameEngine engine = new Lab5();

		// Initialise the game
		engine.init();

		// Start the game
		engine.gameLoop(30);
	}

	//-------------------------------------------------------
	// Your Program
	//-------------------------------------------------------

	// Spritesheet
	Image spritesheet;

	// Keep track of keys
	boolean left, right, up, down;
	boolean gameOver;

	// Function to initialise the game
	public void init() {
		// Load all the sprites
		spritesheet        = loadImage("spritesheet.png");
		spaceshipImage     = subImage(spritesheet,   0,   0, 240,  240);
		laserImage         = subImage(spritesheet, 240,   0, 240,  240);
		rocketEngineImage  = subImage(spritesheet,   0, 240, 240,  240);
		rocketLeftImage    = subImage(spritesheet, 240, 240, 240,  240);
		rocketRightImage   = subImage(spritesheet, 480, 240, 240,  240);

		// Load 3 Asteroid Images
		asteroidImages = new Image[3];
		for(int i = 0; i < 3; i ++) {
			asteroidImages[i] = subImage(spritesheet, 480 + i*240, 0, 240, 240);
		}

		// Load explosion sprites
		explosionImages = new Image[32];
		
		int i = 0;
		for(int m = 0; m<4; m++){
		System.out.println(i);
			for(int j = 0; j<8; j++) {			
				explosionImages[i] = subImage(spritesheet, 0 + j*240, 960 + m*240, 240,240);
				i++;
				
			}
		 	
		}		
			
			
		

		// Setup booleans
		left  = false;
		right = false;
		up    = false;
		down  = false;

		gameOver = false;

		// Initialise the spaceship
		spaceshipPositionX = 250;
		spaceshipPositionY = 250;
		spaceshipVelocityX = 0;
		spaceshipVelocityY = 0;
		spaceshipAngle     = 0;

		// Initialise the Laser
		laserActive = false;

		// Initialise the Asteroid
		randomAsteroid();
	}

	//-------------------------------------------------------
	// Spaceship
	//-------------------------------------------------------

	// Image of the spaceship
	Image spaceshipImage;
	Image rocketEngineImage;
	Image rocketLeftImage;
	Image rocketRightImage;

	// Spaceship position
	double spaceshipPositionX;
	double spaceshipPositionY;
	
	// Spaceship velocity
	double spaceshipVelocityX;
	double spaceshipVelocityY;

	// Spaceship angle
	double spaceshipAngle;

	// Function to draw the spaceship
	public void drawSpaceship() {
		// Save the current transform
		saveCurrentTransform();

		// ranslate to the position of the asteroid
		translate(spaceshipPositionX, spaceshipPositionY);

		// Rotate the drawing context around the angle of the asteroid
		rotate(spaceshipAngle);

		// If the spaceship is accelerating forward
		if(up) {drawImage(rocketEngineImage, -30, -30, 60, 60);}

		// If the spaceship is turning left
		if(left) {drawImage(rocketLeftImage, -30, -30, 60, 60);}

		// If the spaceship is turning left
		if(right) {drawImage(rocketRightImage, -30, -30, 60, 60);}

		// Draw the actual spaceship
		drawImage(spaceshipImage, -30, -30, 60, 60);

		// Restore last transform to undo the rotate and translate transforms
		restoreLastTransform();
	}

	// Code to update 'move' the spaceship
	public void updateSpaceship(double dt) {
		if(up == true) {
			// Increase the velocity of the spaceship
			// as determined by the angle
			spaceshipVelocityX += Math.sin(Math.toRadians(spaceshipAngle)) * 250 * dt;
			spaceshipVelocityY -= Math.cos(Math.toRadians(spaceshipAngle)) * 250 * dt;
		}

		// If the user is holding down the left arrow key
		// Make the spaceship rotate anti-clockwise
		if(left == true) {spaceshipAngle -= 250 * dt;}

		// If the user is holding down the right arrow key
		// Make the spaceship rotate clockwise
		if(right == true) {spaceshipAngle += 250 * dt;}

		// Make the spaceship move forward
		spaceshipPositionX += spaceshipVelocityX * dt;
		spaceshipPositionY += spaceshipVelocityY * dt;

		// If the spaceship reaches the right edge of the screen
		// 'Warp' it back to the left edge
		if(spaceshipPositionX > 500) {spaceshipPositionX -= 500;}

		// If the spaceship reaches the left edge of the screen
		// 'Warp' it back to the right edge
		if(spaceshipPositionX < 0)   {spaceshipPositionX += 500;}

		// If the spaceship reaches the top edge of the screen
		// 'Warp' it back to the bottom edge
		if(spaceshipPositionY > 500) {spaceshipPositionY -= 500;}

		// If the spaceship reaches the bottom edge of the screen
		// 'Warp' it back to the top edge
		if(spaceshipPositionY < 0)   {spaceshipPositionY += 500;}
	}

	//-------------------------------------------------------
	// Laser
	//-------------------------------------------------------

	// Image of the laser
	Image laserImage;

	// Laser position
	double laserPositionX;
	double laserPositionY;

	// Laser velocity
	double laserVelocityX;
	double laserVelocityY;

	// Laser Angle
	double laserAngle;

	// Laser active
	boolean laserActive;

	// Function to shoot a new laser
	public void fireLaser() {
		// Can only fire a laser if there isn't already one active
		if(laserActive == false) {
			// Set the laser position as the current spaceship position
			laserPositionX = spaceshipPositionX;
			laserPositionY = spaceshipPositionY;

			// And make it move in the same direction as the spaceship is facing
			laserVelocityX =  Math.sin(Math.toRadians(spaceshipAngle)) * 250;
			laserVelocityY = -Math.cos(Math.toRadians(spaceshipAngle)) * 250;

			// And face the same direction as the spaceship
			laserAngle = spaceshipAngle;

			// Set it to active
			laserActive = true;
		}
	}

	// Function to draw the laser
	public void drawLaser() {
		// Only draw the laser if it's active
		if(laserActive) {
			// Save the current transform
			saveCurrentTransform();

			// ranslate to the position of the laser
			translate(laserPositionX, laserPositionY);

			// Rotate the drawing context around the angle of the laser
			rotate(laserAngle);

			// Draw the actual laser
			drawImage(laserImage, -30, -30, 60, 60);

			// Restore last transform to undo the rotate and translate transforms
			restoreLastTransform();
		}
	}

	// Function to update 'move' the laser
	public void updateLaser(double dt) {
		// Move the Laser
		laserPositionX += laserVelocityX * dt;
		laserPositionY += laserVelocityY * dt;

		// If the laser reaches the left edge of the screen
		// Destroy the laser
		if(laserPositionX < 0) {laserActive = false;}

		// If the laser reaches the right edge of the screen
		// Destroy the laser
		if(laserPositionX >= 500) {laserActive = false;}

		// If the laser reaches the top edge of the screen
		// Destroy the laser
		if(laserPositionY < 0) {laserActive = false;}

		// If the laser reaches the bottom edge of the screen
		// Destroy the laser
		if(laserPositionY >= 500) {laserActive = false;}
	}

	//-------------------------------------------------------
	// Asteroid
	//-------------------------------------------------------

	// Image of the asteroid
	Image asteroidImage;
	Image[] asteroidImages;

	// Asteroid Position
	double asteroidPositionX;
	double asteroidPositionY;

	double asteroidVelocityX;
	double asteroidVelocityY;

	double asteroidAngle;

	double asteroidRadius;

	public void randomAsteroid() {
		// Random position
		asteroidPositionX = rand(500);
		asteroidPositionY = rand(500);

		// Random Velocity
		asteroidVelocityX = -50 + rand(100);
		asteroidVelocityY = -50 + rand(100);

		// Random Angle
		asteroidAngle = rand(360);

		// Fixed Radius
		asteroidRadius = 30;

		// Select random asteroid
		asteroidImage = asteroidImages[rand(3) % 3];
	}

	// Function to update 'move' the asteroid
	public void updateAsteroid(double dt) {
		// Move the asteroid
		asteroidPositionX += asteroidVelocityX * dt;
		asteroidPositionY += asteroidVelocityY * dt;

		// If the asteroid reaches the left edge of the screen
		// 'Warp' it back to the other side of the screen
		if(asteroidPositionX < 0)    {asteroidPositionX += 500;}

		// If the asteroid reaches the right edge of the screen
		// 'Warp' it back to the other side of the screen
		if(asteroidPositionX >= 500) {asteroidPositionX -= 500;}

		// If the asteroid reaches the top edge of the screen
		// 'Warp' it back to the other side of the screen
		if(asteroidPositionY < 0)    {asteroidPositionY += 500;}

		// If the asteroid reaches the bottom edge of the screen
		// 'Warp' it back to the other side of the screen
		if(asteroidPositionY >= 500) {asteroidPositionY -= 500;}
	}

	// Function to draw the asteroid
	public void drawAsteroid() {
		// Save the current transform
		saveCurrentTransform();

		// ranslate to the position of the asteroid
		translate(asteroidPositionX, asteroidPositionY);

		// Rotate the drawing context around the angle of the asteroid
		rotate(asteroidAngle);

		// Draw the actual asteroid
		drawImage(asteroidImage, -30, -30, 60, 60);

		// Restore last transform to undo the rotate and translate transforms
		restoreLastTransform();
	}

	//-------------------------------------------------------
	// Explosion
	//-------------------------------------------------------
	// Images for the explosion
	Image[] explosionImages;

	// Position of the explosion
	double explosionPositionX;
	double explosionPositionY;

	// Timer for the explosion
	double explosionTimer;
	double explosionDuration;

	boolean explosionActive;

	public void createExplosion(double x, double y) {
		
		explosionPositionX = asteroidPositionX;
		explosionPositionY = asteroidPositionY;
		explosionTimer=0;
		explosionDuration = 2;
		explosionActive = true;
				
	}

	// Function to update the explosion
	public void updateExplosion(double dt) {
		
		explosionTimer += dt;
		
		if (explosionTimer>=explosionDuration);
			explosionActive = false;
	}

	// Function to draw the explosion
	public void drawExplosion() {
		
		if (explosionActive == true){
		   int frame =	getAnimationFrame(explosionTimer, explosionDuration, 32);
			drawImage(explosionImages[frame],asteroidPositionX, asteroidPositionY, 240, 240);
			restoreLastTransform();
			
		}			
		
	}

	// Function to get frame of animation
	public int getAnimationFrame(double timer, double duration, int numFrames) {
		// Get frame
		int i = (int)Math.floor(((timer % duration) / duration) * numFrames);
		// Check range
		if(i >= numFrames) {
			i = numFrames-1;
		}
		// Return
		return i;
	}

	//-------------------------------------------------------
	// Game
	//-------------------------------------------------------

	// Updates the display
	public void update(double dt) {
		// If the game is over
		if(gameOver == true) {
			// Don't try to update anything.
			return;
		}

		// Update the spaceship
		updateSpaceship(dt);
		
		// Update the laser
		updateLaser(dt);

		// Update Asteroid
		updateAsteroid(dt);

		// Update Explosion
		updateExplosion(dt);

		//-------------------------------------------------------
		// Add code to check for collisions
		//-------------------------------------------------------

		// Add code here to deal with the asteroid colliding
		// with the laser
		if(laserActive) {
			if(distance(laserPositionX, laserPositionY, asteroidPositionX, asteroidPositionY) < asteroidRadius*1.2) {
				// Destroy the laser
				laserActive = false;

				// Create an explosion
				createExplosion(asteroidPositionX, asteroidPositionY);
				

				// Create a new random Asteroid
				randomAsteroid();
			}
		}
		// Add code here to deal with the asteroid colliding
		// with the spaceship
		if(distance(spaceshipPositionX, spaceshipPositionY, asteroidPositionX, asteroidPositionY) < asteroidRadius*2) {
			// Collision!
			gameOver = true;
		}
	}

	// This gets called any time the Operating System
	// tells the program to paint itself
	public void paintComponent() {
		// Clear the background to black
		changeBackgroundColor(black);
		clearBackground(500, 500);

		// If the game is not over yet
		if(gameOver == false) {
			// Paint the laser (if it's active)
			drawLaser();

			// Paint the Asteroid
			drawAsteroid();

			// Paint the Spaceship
			drawSpaceship();

			// Paint the explosion (if it's active)
			drawExplosion();	
		} else {
			// If the game is over
			// Display GameOver text
			changeColor(white);
			drawText(85, 250, "GAME OVER!", 50);
		}
	}

	// Called whenever a key is pressed
	public void keyPressed(KeyEvent e) {
		// The user pressed left arrow - record it
		// Record it
		if(e.getKeyCode() == KeyEvent.VK_LEFT)  {left  = true;}
		// The user pressed right arrow - record it
		if(e.getKeyCode() == KeyEvent.VK_RIGHT) {right = true;}
		// The user pressed up arrow - record it
		if(e.getKeyCode() == KeyEvent.VK_UP)    {up    = true;}
		// The user pressed space bar
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {fireLaser();}
	}

	// Called whenever a key is released
	public void keyReleased(KeyEvent e) {
		// The user released left arrow - record it
		if(e.getKeyCode() == KeyEvent.VK_LEFT)  {left  = false;}
		// The user released right arrow - record it
		if(e.getKeyCode() == KeyEvent.VK_RIGHT) {right = false;}
		// The user released up arrow - record it
		if(e.getKeyCode() == KeyEvent.VK_UP)    {up    = false;}
	}
}