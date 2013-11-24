package game;

import geom.Vector;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import anim.Animation;

public class Player extends GameObject implements KeyListener {
	private Animation standLeft;
	private Animation standRight;
	private Animation walkLeft;
	private Animation walkRight;
	private Animation jumpLeft;
	private Animation jumpRight;
	private Animation ducklingLeft;
	private Animation ducklingRight;
	
	private boolean leftPressed;
	private boolean rightPressed;
	private boolean upPressed;
	private boolean jumpHold;
	private boolean onGround;
	private boolean facingRight;
	private boolean doubleJump;
	private boolean jumping;
	private boolean doubleJumping;
	
	private int lives;
	
	private static final double GRAVITY = 0.8;
	private static final double MAX_FALL = 20; // Maxima velocidad para caida
	private static final double GROUND_ACCEL = 0.45;
	private static final double AIR_ACCEL = 0.75; //Modifica la velocidad horizontal durante el salto
	private static final double MAX_SPEED = 8; //Modifica la velocidad maxima horizontal que puede alcanzar el pato
	private static final double FRICTION = 0.65;
	private static final double JUMP = 14.5;


	private ArrayList <ArrayBlockingQueue <Duckling>> ducklingFrames;
	private double offsetDucklingX;
	private double offsetDucklingY;

	
//	private static final double GRAVITY = 0.5;
//	private static final double MAX_FALL = 10; // Maxima velocidad para caida
//	private static final double GROUND_ACCEL = 0.5;
//	private static final double AIR_ACCEL = 0.25; // Modifica la velocidad horizontal durante el salto
//	private static final double MAX_SPEED = 10; // Modifica la velocidad maxima horizontal que puede alcanzar el pato
//	private static final double FRICTION = 0.75;
//	private static final double JUMP = 12.5;

	
	private static final int FRAME = 5; // Duracion de un frame de la animacion del pato
	private static final int SECOND = 60; // Duracion de un segundo en frames de animacion
	private static final int MAX_LIVES = 5;
	private static final int START_LIVES = 3;
	private static final int FRAME_OFFSET = 15; // Cantidad de frames que separan a la animacion del pato, con el siguiente patito, y a este con el siguiente patito, etc.
	
	Player(GamePanel gp) {
		super(gp);
		
		// Inicializamos las animaciones para el pato parado volteando a la izquierda y a la derecha
		standLeft = new Animation();
		standLeft.addFrame("patoParadoIzq1.png", 2 * SECOND);
		standLeft.addFrame("patoParadoIzq2.png", 1 * FRAME);
		standLeft.addFrame("patoParadoIzq1.png", 2 * SECOND);
		standLeft.addFrame("patoParadoIzq2.png", 1 * FRAME);
		standLeft.addFrame("patoParadoIzq1.png", 2 * SECOND);
		standLeft.addFrame("patoParadoIzq2.png", 1 * FRAME);
		standLeft.addFrame("patoParadoIzq1.png", 2 * SECOND);
		standLeft.addFrame("patoParadoIzq2.png", 1 * FRAME);
		standLeft.addFrame("patoParadoIzq1.png", 2 * SECOND);
		standLeft.addFrame("patoParadoIzq3.png", 3 * FRAME);
		standLeft.addFrame("patoParadoIzq4.png", 3 * FRAME);
		standLeft.addFrame("patoParadoIzq5.png", 3 * FRAME);
		standLeft.setLooping(true);
		
		standRight = new Animation();
		standRight.addFrame("patoParadoDer1.png", 2 * SECOND);
		standRight.addFrame("patoParadoDer2.png", 1 * FRAME);
		standRight.addFrame("patoParadoDer1.png", 2 * SECOND);
		standRight.addFrame("patoParadoDer2.png", 1 * FRAME);
		standRight.addFrame("patoParadoDer1.png", 2 * SECOND);
		standRight.addFrame("patoParadoDer2.png", 1 * FRAME);
		standRight.addFrame("patoParadoDer1.png", 2 * SECOND);
		standRight.addFrame("patoParadoDer2.png", 1 * FRAME);
		standRight.addFrame("patoParadoDer1.png", 2 * SECOND);
		standRight.addFrame("patoParadoDer3.png", 3 * FRAME);
		standRight.addFrame("patoParadoDer4.png", 3 * FRAME);
		standRight.addFrame("patoParadoDer5.png", 3 * FRAME);
		standRight.setLooping(true);
		
		// Inicializamos las animaciones para caminar a la izquierda y derecha
		walkLeft = new Animation();
		walkLeft.addFrame("patoCaminaIzq1.png", FRAME);
		walkLeft.addFrame("patoCaminaIzq2.png", FRAME);
		walkLeft.addFrame("patoCaminaIzq3.png", FRAME);
		walkLeft.addFrame("patoCaminaIzq4.png", FRAME);
		walkLeft.setLooping(true);
		
		walkRight = new Animation();
		walkRight.addFrame("patoCaminaDer1.png", FRAME);
		walkRight.addFrame("patoCaminaDer2.png", FRAME);
		walkRight.addFrame("patoCaminaDer3.png", FRAME);
		walkRight.addFrame("patoCaminaDer4.png", FRAME);
		walkRight.setLooping(true);
		
		// Animaciones de salto a la izquierda y derecha
		jumpLeft = new Animation();
		jumpLeft.addFrame("patoParadoIzq3.png", FRAME);
		jumpLeft.addFrame("patoParadoIzq4.png", FRAME);
		jumpLeft.setLooping(true);
		
		jumpRight = new Animation();
		jumpRight.addFrame("patoParadoDer3.png", FRAME);
		jumpRight.addFrame("patoParadoDer4.png", FRAME);
		jumpRight.setLooping(true);
		
		// La animacion por default es volteando a la derecha
		currentAnimation = walkRight;
		
		// Crear la animacion para los patitos
		ducklingLeft = new Animation();
		ducklingLeft.addFrame("patito1_left.png", FRAME);
		ducklingLeft.addFrame("patito2_left.png", FRAME);
		ducklingLeft.addFrame("patito3_left.png", FRAME);
		ducklingLeft.addFrame("patito4_left.png", FRAME);
		ducklingLeft.setLooping(true);
		
		ducklingRight = new Animation();
		ducklingRight.addFrame("patito1_right.png", FRAME);
		ducklingRight.addFrame("patito2_right.png", FRAME);
		ducklingRight.addFrame("patito3_right.png", FRAME);
		ducklingRight.addFrame("patito4_right.png", FRAME);
		ducklingRight.setLooping(true);
		
		// Inicializamos el tamano de los patitos
		BufferedImage img = imageL.getImage(ducklingLeft.getCurrentSprite());
		int ducklingWidth = img.getWidth();
		int ducklingHeight = img.getHeight();
		
		// Inicializamos el tamano
		img = imageL.getImage(currentAnimation.getCurrentSprite());
		width = img.getWidth();
		height = img.getHeight();
		
		offsetDucklingX = (width - ducklingWidth) / 2;
		offsetDucklingY = height - ducklingHeight;
		
		onGround = false;
		facingRight = true;
		
		// Fijamos la aceleracion de la gravedad de forma permanente
		accel.setY(GRAVITY);
		
		// Inicializamos las vidas
		lives = START_LIVES;
		
		// Inicializamos las filas para pintar patitos
		ducklingFrames = new ArrayList <ArrayBlockingQueue <Duckling>> ();
		for (int i = 0; i < MAX_LIVES; i++) {
			// Se le suma 1 al FRAME_OFFSET para poder realizar las operaciones de meter y sacar
			ArrayBlockingQueue <Duckling> queue = new ArrayBlockingQueue <Duckling> (FRAME_OFFSET + 1);
			for (int j = 0; j < FRAME_OFFSET; j++) {
				queue.add(new Duckling(pos.duplicate(), 0));
			}
			ducklingFrames.add(queue);
		}
		
		// Habilitar doble salto
		doubleJump = true;
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
	
	private void updateDucklings() {
		Vector smallPos = new Vector(pos.getX() + offsetDucklingX,
										pos.getY() + offsetDucklingY);
		Duckling ducklingFrame = new Duckling(smallPos, ducklingLeft.getCurrentIndex());
		for (ArrayBlockingQueue <Duckling> queue : ducklingFrames) {
			queue.offer(ducklingFrame);
			ducklingFrame = queue.poll();
		}
	}
	
	public void update() {
		currentAnimation.updateAnimation();
		ducklingLeft.updateAnimation();
		
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
			
			if (upPressed && !jumping) {
				vel.setY(-JUMP);
				jumping = true;
				jumpHold = true;
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
			
			if (doubleJump && upPressed && !jumpHold && !doubleJumping) {
				vel.setY(-JUMP);
				doubleJumping = true;
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
		updateDucklings();
	}
	
	public void paint(Graphics g, int offsetX, int offsetY) {
		super.paint(g, offsetX, offsetY);
		
		// Pintar los patitos
		for (int i = 0; i < lives; i++) {
			ArrayBlockingQueue <Duckling> queue = ducklingFrames.get(i);
			Duckling ducklingFrame = queue.peek();
			String ducklingImg;
			if (ducklingFrame.pos.getX() - pos.getX() > offsetDucklingX) {
				ducklingImg = ducklingLeft.getSprite(ducklingFrame.animationFrame);
			}
			else {
				ducklingImg = ducklingRight.getSprite(ducklingFrame.animationFrame);
			}
			g.drawImage(imageL.getImage(ducklingImg),
						((int) ducklingFrame.pos.getX()) + offsetX,
						((int) ducklingFrame.pos.getY()) + offsetY, null);
		}
	}
	
	public boolean isOnGround() {
		return onGround;
	}
	
	public void setOnGround(boolean onGround) {
		this.onGround = onGround;
		if (onGround && !upPressed) jumping = false;
		if (doubleJumping) doubleJumping = false;
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
			if (onGround) jumping = false;
			if (jumpHold) jumpHold = false;
		}
	}
	
	private class Duckling {
		Vector pos;
		int animationFrame;
		
		Duckling(Vector pos, int animationFrame) {
			this.pos = pos;
			this.animationFrame = animationFrame;
		}
	}
}
