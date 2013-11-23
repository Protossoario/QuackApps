package game;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import anim.Animation;

public class Player extends GameObject implements KeyListener {
	private Animation standLeft;
	private Animation standRight;
	private Animation walkLeft;
	private Animation walkRight;
	private Animation jumpLeft;
	private Animation jumpRight;
	
	private boolean leftPressed;
	private boolean rightPressed;
	private boolean upPressed;
	private boolean onGround;
	private boolean facingRight;
	
<<<<<<< HEAD
	private static final double GRAVITY = 0.6;
=======
	private int lives;
	
	private static final double GRAVITY = 0.5;
>>>>>>> QuackApps/master
	private static final double MAX_FALL = 10; //Maxima velocidad para caida
	private static final double GROUND_ACCEL = 0.45;
	private static final double AIR_ACCEL = 0.75; //Modifica la velocidad horizontal durante el salto
	private static final double MAX_SPEED = 10; //Modifica la velocidad maxima horizontal que puede alcanzar el pato
	private static final double FRICTION = 0.45;
	private static final double JUMP = 14.8;
	
	private static final int FRAME = 5;
	private static final int SECOND = 60;
	private static final int MAX_LIVES = 5;
	private static final int START_LIVES = 3;
	
	Player(GamePanel gp) {
		super(gp);
		
		// Inicializamos las animaciones para el pato parado volteando a la izquierda y a la derecha
		standLeft = new Animation();
		standLeft.addFrame("patoParadoIzq1.png", 2 * SECOND);
		standLeft.addFrame("patoParadoIzq6.png", 1 * FRAME);
		standLeft.addFrame("patoParadoIzq1.png", 2 * SECOND);
		standLeft.addFrame("patoParadoIzq6.png", 1 * FRAME);
		standLeft.addFrame("patoParadoIzq1.png", 2 * SECOND);
		standLeft.addFrame("patoParadoIzq6.png", 1 * FRAME);
		standLeft.addFrame("patoParadoIzq1.png", 2 * SECOND);
		standLeft.addFrame("patoParadoIzq6.png", 1 * FRAME);
		standLeft.addFrame("patoParadoIzq1.png", 2 * SECOND);
		standLeft.addFrame("patoParadoIzq2.png", 3 * FRAME);
		standLeft.addFrame("patoParadoIzq3.png", 3 * FRAME);
		standLeft.addFrame("patoParadoIzq4.png", 3 * FRAME);
		standLeft.setLooping(true);
		
		standRight = new Animation();
		standRight.addFrame("patoParadoDer1.png", 2 * SECOND);
		standRight.addFrame("patoParadoDer6.png", 1 * FRAME);
		standRight.addFrame("patoParadoDer1.png", 2 * SECOND);
		standRight.addFrame("patoParadoDer6.png", 1 * FRAME);
		standRight.addFrame("patoParadoDer1.png", 2 * SECOND);
		standRight.addFrame("patoParadoDer6.png", 1 * FRAME);
		standRight.addFrame("patoParadoDer1.png", 2 * SECOND);
		standRight.addFrame("patoParadoDer6.png", 1 * FRAME);
		standRight.addFrame("patoParadoDer1.png", 2 * SECOND);
		standRight.addFrame("patoParadoDer2.png", 3 * FRAME);
		standRight.addFrame("patoParadoDer3.png", 3 * FRAME);
		standRight.addFrame("patoParadoDer4.png", 3 * FRAME);
		standRight.setLooping(true);
		
		// Inicializamos las animaciones para caminar a la izquierda y derecha
		walkLeft = new Animation();
		walkLeft.addFrame("patoCaminaIzq1.png", FRAME);
		walkLeft.addFrame("patoCaminaIzq2.png", FRAME);
		walkLeft.addFrame("patoCaminaIzq3.png", FRAME);
		walkLeft.addFrame("patoCaminaIzq4.png", FRAME);
		walkLeft.addFrame("patoCaminaIzq5.png", FRAME);
		walkLeft.addFrame("patoCaminaIzq6.png", FRAME);
		walkLeft.addFrame("patoCaminaIzq7.png", FRAME);
		walkLeft.addFrame("patoCaminaIzq8.png", FRAME);
		walkLeft.setLooping(true);
		
		walkRight = new Animation();
		walkRight.addFrame("patoCaminaDer1.png", FRAME);
		walkRight.addFrame("patoCaminaDer2.png", FRAME);
		walkRight.addFrame("patoCaminaDer3.png", FRAME);
		walkRight.addFrame("patoCaminaDer4.png", FRAME);
		walkRight.addFrame("patoCaminaDer5.png", FRAME);
		walkRight.addFrame("patoCaminaDer6.png", FRAME);
		walkRight.addFrame("patoCaminaDer7.png", FRAME);
		walkRight.addFrame("patoCaminaDer8.png", FRAME);
		walkRight.setLooping(true);
		
		// Animaciones de salto a la izquierda y derecha
		jumpLeft = new Animation();
		jumpLeft.addFrame("patoSaltoIzq1.png", FRAME);
		jumpLeft.addFrame("patoSaltoIzq2.png", FRAME);
		jumpLeft.setLooping(true);
		
		jumpRight = new Animation();
		jumpRight.addFrame("patoSaltoDer1.png", FRAME);
		jumpRight.addFrame("patoSaltoDer2.png", FRAME);
		jumpRight.setLooping(true);
		
		// La animacion por default es volteando a la derecha
		currentAnimation = walkRight;
		
		onGround = false;
		facingRight = true;
		
		// Fijamos la aceleracion de la gravedad de forma permanente
		accel.setY(GRAVITY);
		
		// Inicializamos el tamano
		BufferedImage img = imageL.getImage(currentAnimation.getCurrentSprite());
		width = img.getWidth();
		height = img.getHeight();
		
		// Inicializamos las vidas
		lives = START_LIVES;
	}

	public Rectangle getCollisionRect() {
		// TODO Auto-generated method stub
		return null;
	}

	private void updateAnimations() {
		if (onGround) {
			if (vel.getX() > 0) {
				currentAnimation = walkRight;
				if (!facingRight) {
					facingRight = true;
				}
			}
			else if (vel.getX() < 0) {
				currentAnimation = walkLeft;
				if (facingRight) {
					facingRight = false;
				}
			}
			else {
				currentAnimation = (facingRight ? standRight : standLeft);
			}
		}
		else {
			if (vel.getX() > 0) {
				if (!facingRight) {
					facingRight = true;
				}
			}
			else if (vel.getX() < 0) {
				if (facingRight) {
					facingRight = false;
				}
			}
			currentAnimation = (facingRight ? jumpRight : jumpLeft);
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
