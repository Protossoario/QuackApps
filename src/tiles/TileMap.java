package tiles;

import game.GamePanel;
import image.ImageLoader;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class TileMap {
	private static final int TILE_SIZE = 64;
	private static final int EMPTY_TILE = -1;
	
	// Guarda las imagenes de los tiles
	private ArrayList <BufferedImage> tiles;
	
	// Matriz bidimensional que guarda el numero de tile que corresponde al subindice (i, j)
	// Para cada subindice, se guarda un numero (0, 1, 2, etc.) que indica el numero de tile que es
	// De lo contrario, se marca con un -1 los espacios vacios
	private int map[][];
	
	public static int pixelsToTiles(double pixels) {
		return (int) pixels / TILE_SIZE;
	}
	
	public static int tilesToPixels(int tiles) {
		return tiles * TILE_SIZE;
	}
	
	/**
	 * Checa la colision de un rectangulo con alguno de los tiles del mapa
	 * @param rect El rectangulo de colision
	 * @return El tile con el que colisiona el rectangulo, o null si no hay colision
	 */
	public Point checkTileCollision(Rectangle rect) {
		int fromTileX = pixelsToTiles(rect.getMinX());
		int fromTileY = pixelsToTiles(rect.getMinY());
		int toTileX = pixelsToTiles(rect.getMaxX() - 1);
		int toTileY = pixelsToTiles(rect.getMaxY() - 1);
		
		for (int x = fromTileX; x <= toTileX; x++) {
			for (int y = fromTileY; y <= toTileY; y++) {
				if (x < 0 || x >= getWidth() || map[x][y] != EMPTY_TILE) {
					return new Point(x, y);
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Metodo para leer el archivo de texto y cargar los datos a la matriz de enteros
	 * @param fname Indica el nombre del archivo a leer, con su directorio
	 */
	private void loadMap(String fname) {
		int max_length = 0;
		ArrayList <String> lines = new ArrayList <String> ();
		try {
			InputStream in = this.getClass().getClassLoader().getResourceAsStream(fname);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			
			String line;
			while ((line = reader.readLine()) != null) {
				lines.add(line);
				if (line.length() > max_length) {
					max_length = line.length();
				}
			}
			reader.close();
		}
		catch (IOException ex) {
			System.out.println("Error al leer el archivo del mapa: " + fname);
		}
		
		map = new int[lines.size()][max_length];
		
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			int j;
			for (j = 0; j < line.length(); j++) {
				if (line.charAt(j) >= 'A' && line.charAt(j) <= 'Z') {
					map[i][j] = line.charAt(j) - 'A';
				}
			}
			
			for ( ; j < max_length; j++) {
				map[i][j] = EMPTY_TILE;
			}
		}
	}
	
	/**
	 * Pide al ImageLoader las imagenes de los tiles, si es que existen.
	 * Pide los tiles siguiendo el patron tile_*.png, empezado con 0.
	 * Se detiene al llegar al primer tile que no exista.
	 * @param imageL El ImageLoader que contiene las imagenes del juego
	 */
	private void getTiles(ImageLoader imageL) {
		if (imageL == null) {
			System.out.println("Image loader es nulo. No se pueden obtener las imagenes de los tiles.");
		}
		
		String imgName = "tile_";
		String imgExt = ".png";
		int i = 0;
		boolean end = false;
		while (!end) {
			BufferedImage img = imageL.getImage(imgName + i + imgExt);
			if (img != null) {
				tiles.add(img);
			}
			else {
				System.out.println("Tile #" + i + " es null.");
				end = true;
			}
		}
	}
	
	/**
	 * Constructor que manda a cargar los datos del tilemap
	 * @param file Es el nombre del archivo de texto de donde se leera la informacion del tilemap
	 */
	public TileMap(String file, GamePanel gp) {
		loadMap("maps/" + file);
		getTiles(gp.getImageLoader());
	}
	
	public int getWidth() {
		return map[0].length;
	}
	
	public int getHeight() {
		return map.length;
	}
	
	public BufferedImage getTileImage(int x, int y) {
		if (map[x][y] == EMPTY_TILE) {
			return null;
		}
		return tiles.get(map[x][y]);
	}
}
