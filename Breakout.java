/*
 * File: Breakout.java
 * -------------------
 * Name: Hayan Khan
 * 
 * Stanford Programming Methodology Assignment #3
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

	/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

	/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;

	/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

	/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

	/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;
	
	/**Total number of bricks */
	private static final int NBRICKS = 100;

	/** Separation between bricks */
	private static final int BRICK_SEP = 4;

	/** Width of a brick */
	private static final int BRICK_WIDTH =
	  (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

	/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

	/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 5;

	/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;
	
	/**Time delay before each movement*/
	private static final int DELAY =8;

	/** Number of turns */

	/* Method: run() */
	/** Runs the Breakout program. */
	
	public void run() {
		initializeBreakout();
		playBreakout();
	}
	
	private void initializeBreakout(){
		generateBricks();
		generatePaddle();
		generateScoreSystem();
		createLivesLabel();
	}
	
	//generates the main features of the game, and lets the user know the status after each round
	 
	private void playBreakout(){
		for (lives = 2 ; lives >= 0; lives--){
			generateBall(); 
			waitForClick();
			moveBall(); 
			updateStatus();
			if (gameOver) break;
			remove(ball);
			updateLives();
		}
		updateStatus();
	} 
	// created multi colored rows of bricks
	private void generateBricks(){
		int spaceEachSide = (WIDTH - NBRICKS_PER_ROW * BRICK_WIDTH - BRICK_SEP * (NBRICKS_PER_ROW - 1)) / 2;
		int widthIncrementor = 0, heightIncrementor = 0;
		for (int rowNum = 1 ; rowNum <= NBRICK_ROWS ; rowNum++){
			for (int brickNum = 1 ; brickNum <= NBRICKS_PER_ROW ; brickNum++){
				rect = new GRect(spaceEachSide + widthIncrementor,BRICK_Y_OFFSET + heightIncrementor,BRICK_WIDTH,BRICK_HEIGHT);
				widthIncrementor = widthIncrementor + BRICK_WIDTH + BRICK_SEP;
				add(rect);
				rect.setFilled(true);
				if (rowNum == 1 || rowNum == 2) {
					rect.setColor(Color.RED);
				} else if (rowNum == 3 || rowNum == 4) {
					rect.setColor(Color.ORANGE);
				} else if (rowNum == 5 || rowNum == 6) {
					rect.setColor(Color.YELLOW);	
				} else if (rowNum == 7 || rowNum == 8) {
					rect.setColor(Color.GREEN);
				} else if (rowNum == 9 || rowNum == 10 ) {
					rect.setColor(Color.CYAN);
				}
			}
			widthIncrementor = 0;
			heightIncrementor = heightIncrementor + BRICK_HEIGHT + BRICK_SEP;
		}
	}
	// creats the paddle and its animations
	private void generatePaddle() {
		paddleRect = new GRect(100, getHeight() - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddleRect.setFilled(true);
		add(paddleRect);
		addMouseListeners();
	}
	//creates the ball and its animations
	private void generateBall() {
		ball = new GOval (getWidth()/2 - BALL_RADIUS,getHeight()/2 - BALL_RADIUS,BALL_RADIUS * 2, BALL_RADIUS *2 );
		ball.setFilled(true);
		add(ball);
		ball.sendToBack();
	}
	//the function of the program that determines the movement of the ball
	private void moveBall() {
		vy = 3.0;
		vx = rgen.nextDouble(1.0,3.0);
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
			while (true){
			ball.move(vx,vy);
				if (ball.getX() > APPLICATION_WIDTH - BALL_RADIUS * 2) {
					vx = -vx;
				} else if (ball.getX() < 0 ) {
					vx = -vx;
				} else if (ball.getY() < 0) {
					vy = -vy;
				} else if (ball.getY() > APPLICATION_HEIGHT - BALL_RADIUS * 2) break; 
				
				checkForBrickCollision();
				checkForPaddleCollision();
				if (numBricksDestroyed  == NBRICKS ){
					gameOver = true;
					break;
				}
				pause(DELAY);
			}
		remove(ball);
	}
	// creates the collision reaction when the ball hits the paddle
	private  void checkForPaddleCollision() {
		GObject object1 = getElementAt(ball.getX(),ball.getY() + BALL_RADIUS * 2);
		GObject object2 = getElementAt(ball.getX() + BALL_RADIUS *2 ,ball.getY() + BALL_RADIUS * 2); 
		if (object1 == paddleRect || object2 == paddleRect) {
			vy = -vy;
		} 
	}
	/* Creates the collision reaction for when the ball hits the brick
	 * Also determines what to change the score by for when it does hit a brick
	 */
	private void checkForBrickCollision() { //void for now should be boolean
		GObject object1 = getElementAt(ball.getX(),ball.getY());
		GObject object2 = getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY());
		GObject object3 = getElementAt(ball.getX(),ball.getY() + BALL_RADIUS * 2);
		GObject object4 = getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY() + BALL_RADIUS * 2);
		if (object1 != null && object1 != paddleRect && object1 != livesLabel && object1 != scoreLabel){
			vy = -vy;
			remove(scoreLabel);
			if (object1.getColor() == Color.CYAN){
				score += 10;
			} else if (object1.getColor() == Color.GREEN){
				score += 20;
			} else if (object1.getColor() == Color.YELLOW) {
				score += 30;
			} else if (object1.getColor() == Color.ORANGE) {
				score += 40;
			} else if (object1.getColor() == Color.RED){
				score += 50;	
			}	
			scoreLabel = new GLabel("Score: " + score,0 ,30 );
			add(scoreLabel);
			remove(object1);
			numBricksDestroyed++;
		} else if (object2 != null && object2 != paddleRect && object2 != livesLabel && object2 != scoreLabel) {
			vy = -vy;
			remove(scoreLabel);
			if (object2.getColor() == Color.CYAN){
				score += 10;
			} else if (object2.getColor() == Color.GREEN){
				score += 20;
			} else if (object2.getColor() == Color.YELLOW) {
				score += 30;
			} else if (object2.getColor() == Color.ORANGE) {
				score += 40;
			} else if (object2.getColor() == Color.RED){
				score += 50;	
			}	
			scoreLabel = new GLabel("Score: " + score,0 ,30 );
			add(scoreLabel);
			remove(object2);
			numBricksDestroyed++;
		} else if (object3 != null && object3 != paddleRect && object3 != livesLabel && object3 != scoreLabel) {
			vy = -vy;
			remove(scoreLabel);
			if (object3.getColor() == Color.CYAN){
				score += 10;
			} else if (object3.getColor() == Color.GREEN){
				score += 20;
			} else if (object3.getColor() == Color.YELLOW) {
				score += 30;
			} else if (object3.getColor() == Color.ORANGE) {
				score += 40;
			} else if (object3.getColor() == Color.RED){
				score += 50;	
			}	
			scoreLabel = new GLabel("Score: " + score,0 ,30 );
			add(scoreLabel);
			remove(object3);
			numBricksDestroyed++;
		} else if (object4 != null && object4 != paddleRect && object4 != livesLabel && object4 != scoreLabel) {
			vy = -vy;
			remove(scoreLabel);
			if (object4.getColor() == Color.CYAN){
				score += 10;
			} else if (object4.getColor() == Color.GREEN){
				score += 20;
			} else if (object4.getColor() == Color.YELLOW) {
				score += 30;
			} else if (object4.getColor() == Color.ORANGE) {
				score += 40;
			} else if (object4.getColor() == Color.RED){
				score += 50;	
			}	
			scoreLabel = new GLabel("Score: " + score,0 ,30 );
			add(scoreLabel);
			remove(object4);
			numBricksDestroyed++;
		}
	}
	//creates a label to output the number of lives left to the user
	private void createLivesLabel(){
		livesLabel = new GLabel("Lives Left: " + lives,0 ,50);
		add(livesLabel);
	}
	//updates the number of lives after the status being updated
	private void updateLives(){
		remove(livesLabel);
		createLivesLabel();
	}
	//creates the score system used in breakout
	private void generateScoreSystem(){
		scoreLabel = new GLabel("Score: " + score,0 ,30 );
		add(scoreLabel);
	}
	/*
	 * When the bricks are all destroyed or lives are all gone,
	 * the user is prompted with the current status of
	 * whether he won or lost. Also displays the developers
	 * credits
	 */
	private void updateStatus() {
		if (numBricksDestroyed == NBRICKS || lives == -1) {
			GRect blankBackground = new GRect(0,0,WIDTH,HEIGHT);
			blankBackground.setFilled(true);
			blankBackground.setColor(Color.BLACK);
			add(blankBackground);
			if (numBricksDestroyed == NBRICKS) {
				endingStatusLabel = new GLabel("YOU WIN - SCORE: " + score, WIDTH / 2 - 160, HEIGHT / 2 - 100);
			} else if (lives == -1) {
				endingStatusLabel = new GLabel("YOU LOSE - SCORE: " + score, WIDTH / 2 - 160, HEIGHT / 2 - 100);
			}
			endingStatusLabel.setColor(Color.WHITE);
			endingStatusLabel.setFont("Times New Roman-BOLDITALIC-30");
			add(endingStatusLabel);
			GLabel creditsLabel = new GLabel("By: Hayan Khan", WIDTH/2 - 70, HEIGHT /2 - 50);
			creditsLabel.setColor(Color.WHITE);
			creditsLabel.setFont("Times New Roman-BOLD-20");
			add(creditsLabel);
		}
	}
	/*
	 * This function makes it so when the user moves the mouse,
	 * the center of the paddle follows the mouse and prevents the paddle
	 * from leaving the boundaries.
	 */
	public void mouseMoved(MouseEvent e) {	
		paddleRect.setLocation(e.getX()-PADDLE_WIDTH/2, getHeight() - PADDLE_Y_OFFSET);
		if (paddleRect.getX() < 0) {
			paddleRect.setLocation(0, getHeight() - PADDLE_Y_OFFSET);
		} else if (paddleRect.getX() > APPLICATION_WIDTH - PADDLE_WIDTH) {
			paddleRect.setLocation(APPLICATION_WIDTH - PADDLE_WIDTH, getHeight() - PADDLE_Y_OFFSET);
		}
	}
	// instance variables
	private GRect paddleRect, rect; 
	private GOval ball;
	private GLabel livesLabel, endingStatusLabel, scoreLabel;
	private double vx, vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private int lives = 3;
	private int numBricksDestroyed = 0;
	private int score = 0;
	private boolean gameOver = false;
}
