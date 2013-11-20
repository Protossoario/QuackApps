/**
 * @author Eduardo A. Sanchez
 * 
 * Clase base para el comportamiento de objetos que se mueven y/o interactuan
 * entre si dentro del juego.
 * 
 * @creation 28/08/2013
 */

package game;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import anim.Animation;
import geom.Vector;
import image.ImageLoader;
import sound.ClipsLoader;

abstract class GameObject {
	protected Vector pos; // vector de la posicion en el centro del objeto
	protected Vector vel; // vector de velocidad
	protected Vector accel; // vector de aceleracion
	protected int width, height; // tamanio del objeto
	protected boolean marked; // indica si el objeto esta marcado para ser eliminado

	protected GamePanel gp;
	protected ImageLoader imageL;
	protected ClipsLoader clipsL;
	protected Animation currentAnimation;
	
	GameObject(GamePanel gp) {
		this.gp = gp;
		imageL = gp.getImageLoader();
		clipsL = gp.getClipsLoader();
		
		pos = new Vector();
		vel = new Vector();
		accel = new Vector();
	}
	
	/**
	 * Checa colision con las paredes de la ventana, y evita que el objeto
	 * se salga de estos limites.
	 */
	protected void checkCollisions() {
		if (pos.getX() + width > gp.getWidth()) {
			pos.setX(gp.getWidth() - width);
			
			vel.setX(0); // detenemos la velocidad horizontal
		}
		else if (pos.getX() < 0) {
			pos.setX(0);
			
			vel.setX(0);
		}
		
		if (pos.getY() + height > gp.getHeight()) {
			pos.setY(gp.getHeight() - height);
			
			vel.setY(0); // detenemos la velocidad vertical
		}
		else if (pos.getY() < 0) {
			pos.setY(0);
			
			vel.setY(0);
		}
	}
	
	/**
	 * Codigo para checar colision de un objeto.
	 * Utiliza un cuadrado de colisiones que puede ser distinto al tamanio del objeto.
	 */
	public boolean squareCollides(GameObject go) {
		return (this.getCollisionRect()).intersects(go.getCollisionRect());
	}
	
	abstract public Rectangle getCollisionRect();

	/**
	 * Actualizar el estado del objeto.
	 * Aqui se determina su comportamiento durante la ejecucion del juego.
	 */
	public abstract void update();
	
	/**
	 * Este metodo lo llama el objeto GamePanel al hacer el render
	 * de la pantalla de juego; aqui es donde se pinta al personaje
	 * en base al sprite correspondiente.
	 */
	public void paint(Graphics g) {
		BufferedImage im = imageL.getImage(currentAnimation.getCurrentSprite());
		if (im != null) {
			g.drawImage(im, (int) pos.getX(), (int) pos.getY(), null);
		}
	}
	
	/**
	 * Getters y setters para las variables de la clase
	 */
	public Vector getPos() {
		return pos;
	}

	public void setPos(Vector pos) {
		this.pos = pos;
	}

	public Vector getVel() {
		return vel;
	}

	public void setVel(Vector vel) {
		this.vel = vel;
	}

	public Vector getAccel() {
		return accel;
	}

	public void setAccel(Vector accel) {
		this.accel = accel;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public GamePanel getGp() {
		return gp;
	}

	public void setGp(GamePanel gp) {
		this.gp = gp;
	}

	public ImageLoader getimageL() {
		return imageL;
	}

	public void setimageL(ImageLoader imageL) {
		this.imageL = imageL;
	}

	public ClipsLoader getSoundL() {
		return clipsL;
	}

	public void setSoundC(ClipsLoader soundL) {
		this.clipsL = soundL;
	}
	
	public boolean isMarkedForDeletion() {
		return marked;
	}

	public void setMarkedForDeletion(boolean marked) {
		this.marked = marked;
	}
}
