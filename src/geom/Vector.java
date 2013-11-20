/**
 * @author Eduardo A. Sanchez
 * 
 * Clase matematica empleada para las fisicas del juego.
 * Maneja los conceptos de puntos en el espacio geometrico bidimensional,
 * asi como de direcciones y magnitudes vectoriales, por ejemplo,
 * la velocidad de un objeto.
 * 
 * @creation 28/08/2013
 */

package geom;

public class Vector {
	private double x, y;

	public Vector() {
		x = 0;
		y = 0;
	}
	
	public Vector(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector(Vector v) {
		x = v.x;
		y = v.y;
	}
	
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
	
	public void addToX(double x) {
		this.x += x;
	}
	
	public void addToY(double y) {
		this.y += y;
	}
	
	public Vector add(Vector v) {
		return new Vector(x + v.x, y + v.y);
	}
	
	public Vector subtract(Vector v) {
		return new Vector(x - v.x, y - v.y);
	}
	
	public Vector unitVector() {
		double magnitude = Math.sqrt((x * x) + (y * y));
		return new Vector( (x / magnitude),  (y / magnitude));
	}
	
	public Vector duplicate() {
		return new Vector(x, y);
	}
	
	public double squareMagnitude() {
		return (x * x) + (y * y);
	}
	
	public double magnitude() {
		return Math.sqrt((x * x) + (y * y));
	}
	
	public double dotProduct(Vector v) {
		return (x * v.x) + (y * v.y);
	}
	
	public boolean equals(Vector v) {
		return (x == v.x && y == v.y);
	}
	
	// Cambia la magnitud del vector, manteniendo la direccion
	public void rescale(double magnitude) {
		if (x != 0  && y != 0) {
			double mag = Math.sqrt((x * x) + (y * y));
			
			if (x != 0) {
				x = (x * magnitude) / mag;
			}
			
			if (y != 0) {
				y = (y * magnitude) / mag;
			}
		}
	}
}
