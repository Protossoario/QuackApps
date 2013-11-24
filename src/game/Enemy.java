package game;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import anim.Animation;

public class Enemy extends GameObject {
	private static final int SPEED = 3;
	private static final int FRAME = 10;
	private Animation walkLeft;
	private Animation walkRight;
	private boolean facingRight;
	Enemy(GamePanel gp) {
		super(gp);
		
		// Crear las animaciones
		walkLeft = new Animation();
		walkLeft.addFrame("rata1_left.png", FRAME);
		walkLeft.addFrame("rata2_left.png", FRAME);
		walkLeft.addFrame("rata3_left.png", FRAME);
		walkLeft.addFrame("rata4_left.png", FRAME);
		walkLeft.addFrame("rata5_left.png", FRAME);
		walkLeft.addFrame("rata6_left.png", FRAME);
		walkLeft.addFrame("rata7_left.png", FRAME);
		walkLeft.addFrame("rata8_left.png", FRAME);
		walkLeft.setLooping(true);
		
		walkRight = new Animation();
		walkRight.addFrame("rata1_right.png", FRAME);
		walkRight.addFrame("rata2_right.png", FRAME);
		walkRight.addFrame("rata3_right.png", FRAME);
		walkRight.addFrame("rata4_right.png", FRAME);
		walkRight.addFrame("rata5_right.png", FRAME);
		walkRight.addFrame("rata6_right.png", FRAME);
		walkRight.addFrame("rata7_right.png", FRAME);
		walkRight.addFrame("rata8_right.png", FRAME);
		walkRight.setLooping(true);
		
		currentAnimation = walkLeft;
		
		BufferedImage img = imageL.getImage(currentAnimation.getCurrentSprite());
		width = img.getWidth();
		height = img.getHeight();
	}

	public Rectangle getCollisionRect() {
		// TODO Auto-generated method stub
		return null;
	}

	public void update() {
		currentAnimation.updateAnimation();
		
		if (facingRight) {
			pos.setX(pos.getX() + SPEED);
			currentAnimation = walkRight;
		}
		else {
			pos.setX(pos.getX() - SPEED);
			currentAnimation = walkLeft;
		}
	}
	
	public boolean isFacingRight() {
		return facingRight;
	}
	
	public void setFacingRight(boolean facingRight) {
		this.facingRight = facingRight;
	}
}
