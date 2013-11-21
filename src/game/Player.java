package game;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import anim.Animation;

public class Player extends GameObject implements KeyListener {
	private Animation walkLeft;
	private Animation walkRight;
	
	private boolean leftPressed;
	private boolean rightPressed;
	private boolean upPressed;
	private boolean onGround;
	
	private static final double GRAVITY = 0.5;
	private static final double MAX_FALL = 10;
	private static final double GROUND_ACCEL = 0.5;
	private static final double AIR_ACCEL = 0.25;
	private static final double MAX_SPEED = 10;
	private static final double FRICTION = 0.75;
	private static final double JUMP = 10;
	
	Player(GamePanel gp) {
		super(gp);
		
		// Inicializamos las animaciones para caminar a la izquierda y derecha
		walkLeft = new Animation();
		walkLeft.addFrame("patoCaminaIzq1.png", 1);
		walkLeft.addFrame("patoCaminaIzq2.png", 1);
		walkLeft.addFrame("patoCaminaIzq3.png", 1);
		walkLeft.addFrame("patoCaminaIzq4.png", 1);
		walkLeft.addFrame("patoCaminaIzq5.png", 1);
		walkLeft.addFrame("patoCaminaIzq6.png", 1);
		walkLeft.addFrame("patoCaminaIzq7.png", 1);
		walkLeft.addFrame("patoCaminaIzq8.png", 1);
		walkLeft.setLooping(true);
		
		walkRight = new Animation();
		walkRight.addFrame("patoCaminaDer1.png", 1);
		walkRight.addFrame("patoCaminaDer2.png", 1);
		walkRight.addFrame("patoCaminaDer3.png", 1);
		walkRight.addFrame("patoCaminaDer4.png", 1);
		walkRight.addFrame("patoCaminaDer5.png", 1);
		walkRight.addFrame("patoCaminaDer6.png", 1);
		walkRight.addFrame("patoCaminaDer7.png", 1);
		walkRight.addFrame("patoCaminaDer8.png", 1);
		walkRight.setLooping(true);
		
		currentAnimation = walkRight;
		
		// Fijamos la aceleracion de la gravedad de forma permanente
		accel.setY(GRAVITY);
		
		// Inicializamos el tamano
		BufferedImage img = imageL.getImage(currentAnimation.getCurrentSprite());
		width = img.getWidth();
		height = img.getHeight();
	}

	public Rectangle getCollisionRect() {
		// TODO Auto-generated method stub
		return null;
	}

	private void updateAnimations() {
		if (vel.getX() > 0) {
			currentAnimation = walkRight;
		}
		else if (vel.getX() < 0) {
			currentAnimation = walkLeft;
		}
	}
	
	public void update() {
		currentAnimation.updateAnimation();
		
		if (onGround) {
			if (leftPressed && !rightPressed) {
				accel.setX(-GROUND_ACCEL);
			}
			else if (!leftPressed && rightPressed) {
				accel.setX(GROUND_ACCEL);
			}
			else {
				if (vel.getX() > 0) {
					accel.setX(Math.max(-vel.getX(), -FRICTION));
				}
				else if (vel.getX() < 0) {
					accel.setX(Math.min(-vel.getX(), FRICTION));
				}
				else {
					accel.setX(0);
				}
			}
			
			if (upPressed) {
				vel.setY(-JUMP);
			}
		}
		else {
			if (leftPressed && !rightPressed) {
				accel.setX(-AIR_ACCEL);
			}
			else if (!leftPressed && rightPressed) {
				accel.setX(AIR_ACCEL);
			}
			else {
				accel.setX(0);
			}
			
			vel.setY(vel.getY() + accel.getY());
		}
		
		vel.setX(vel.getX() + accel.getX());
		if (vel.getX() > MAX_SPEED) {
			vel.setX(MAX_SPEED);
		}
		else if (vel.getX() < -MAX_SPEED) {
			vel.setX(-MAX_SPEED);
		}
		
		if (vel.getY() > MAX_FALL) {
			vel.setY(MAX_FALL);
		}
		
		updateAnimations();
	}
	
	public boolean isOnGround() {
		return onGround;
	}
	
	public void setOnGround(boolean onGround) {
		this.onGround = onGround;
	}
	
	public BufferedImage getCurrentImage() {
		return imageL.getImage(currentAnimation.getCurrentSprite());
	}
	
	public void setPos(double X, double Y) {
		pos.setX(X);
		pos.setY(Y);
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		if (code == KeyEvent.VK_LEFT) {
			leftPressed = true;
		}
		else if (code == KeyEvent.VK_RIGHT) {
			rightPressed = true;
		}
		else if (code == KeyEvent.VK_UP) {
			upPressed = true;
		}
	}

	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();
		if (code == KeyEvent.VK_LEFT) {
			leftPressed = false;
		}
		else if (code == KeyEvent.VK_RIGHT) {
			rightPressed = false;
		}
		else if (code == KeyEvent.VK_UP) {
			upPressed = false;
		}
	}
}
