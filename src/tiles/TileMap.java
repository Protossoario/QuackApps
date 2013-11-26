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
	private static final int FIRST_TILE = 'A';
	private static final int LAST_TILE = 'Z';
	private static final int FIRST_TRASHCAN = 'a';
	private static final int LAST_TRASHCAN = 'z';
	private static final int FIRST_TRASH = '1';
	private static final int LAST_TRASH = '9';
	private static final int ENEMY_TILE = '=';
	private static final int PLAYER_TILE = '*';
	
	// Guarda las imagenes de los tiles
	private ArrayList <BufferedImage> tiles;
	
	private Point playerSpawn;
	private ArrayList <Point> enemySpawns;
	// Guarda las posiciones de cada tipo de bote de basura
	private ArrayList <Point> trashCanTiles;
	
	private ArrayList <Point> spikeTiles;
	// Arreglo de Points de dos dimensiones
	// Guarda las posiciones de todos los pedazos de basura, para cada tipo de basura
	private ArrayList <ArrayList <Point>> trashTiles;
	private int trashTilesTotal;
	private int[] trashTilesTypeTotal;
	private int backgroundTile;
	
	// Matriz bidimensional que guarda el numero de tile que corresponde al subindice (i, j)
	// Para cada subindice, se guarda un numero (0, 1, 2, etc.) que indica el numero de tile que es
	// De lo contrario, se marca con un -1 los espacios vacios
	private int map[][];
	
	/**
	 * Constructor que manda a cargar los datos del tilemap
	 * @param file Es el nombre del archivo de texto de donde se leera la informacion del tilemap
	 */
	public TileMap(String file, GamePanel gp) {
		tiles = new ArrayList <BufferedImage> ();
		trashCanTiles = new ArrayList <Point> ();
		trashTiles = new ArrayList <ArrayList <Point>> ();
		spikeTiles = new ArrayList <Point> ();
		enemySpawns = new ArrayList <Point> ();
		for (int i = 0; i < 4; i++) {
			trashTiles.add(new ArrayList <Point>());
			trashCanTiles.add(new Point(-1, -1));
		}
		
		trashTilesTotal = 0;
		trashTilesTypeTotal = new int[4];
		
		for(int i=0; i<3; i++){
			trashTilesTypeTotal[i]=0;
		}
		
		loadMap("maps/" + file);
		getTiles(gp.getImageLoader());
		
		System.out.println("Background tile: " + backgroundTile);
		
		for (int y = map[0].length / 2; y < map[0].length; y++) {
			for (int x = 0; x < map.length; x++) {
				if (map[x][y] == EMPTY_TILE) {
					map[x][y] = backgroundTile;
				}
			}
		}
	}
	
	
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
				if (x < 0 || x >= map.length ||
					y < 0 || y >= map[0].length ||
					(map[x][y] > EMPTY_TILE && map[x][y] < backgroundTile)) {
					return new Point(x, y);
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Checa si hay un tile vacio dentro del area especificada por un rectangulo
	 * @param rect El area donde se checa
	 * @return Un valor booleano indicando si se encontro un tile vacio
	 */
	public boolean checkEmptySpace(Rectangle rect) {
		int fromTileX = pixelsToTiles(rect.getMinX());
		int fromTileY = pixelsToTiles(rect.getMinY());
		int toTileX = pixelsToTiles(rect.getMaxX() - 1);
		int toTileY = pixelsToTiles(rect.getMaxY() - 1);
		
		for (int x = fromTileX; x <= toTileX; x++) {
			for (int y = fromTileY; y <= toTileY; y++) {
				if (x < 0 || x >= map.length ||
					map[x][y] == EMPTY_TILE || map[x][y] == backgroundTile) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean isSolidTile(char tile) {
		return tile >= FIRST_TILE && tile <= LAST_TILE;
	}
	
	private boolean isTrashCanTile(char tile) {
		return tile >= FIRST_TRASHCAN && tile <= LAST_TRASHCAN;
	}
	
	private boolean isTrashTile(char tile) {
		return tile >= FIRST_TRASH && tile <= LAST_TRASH;
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
				if (!line.startsWith("#")) {
					lines.add(line);
					if (line.length() > max_length) {
						max_length = line.length();
					}
				}
			}
			reader.close();
		}
		catch (IOException ex) {
			System.out.println("Error al leer el archivo del mapa: " + fname);
		}
		
		map = new int[max_length][lines.size()];
		
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				map[i][j] = EMPTY_TILE;
			}
		}
		
		for (int y = 0; y < lines.size(); y++) {
			String line = lines.get(y);
			for (int x = 0; x < line.length(); x++) {
				if (isSolidTile(line.charAt(x))) {
					map[x][y] = line.charAt(x) - 'A';
					if(line.charAt(x) == 'F'){
						spikeTiles.add(new Point(x, y));
					}
				}
				else if (isTrashCanTile(line.charAt(x))) {
					trashCanTiles.set(line.charAt(x) - 'a', new Point(x, y));
				}
				else if (isTrashTile(line.charAt(x))) {
					int trashInd = line.charAt(x) - FIRST_TRASH;
					trashTiles.get(trashInd).add(new Point(x, y));
					if(line.charAt(x) == '1') {
						trashTilesTypeTotal[0]++;
					} else if(line.charAt(x) == '2'){
						trashTilesTypeTotal[1]++;
					}else if(line.charAt(x) == '3'){
						trashTilesTypeTotal[2]++;
					}else if(line.charAt(x) == '4'){
						trashTilesTypeTotal[3]++;
					}
					trashTilesTotal++;
				}
				else if (line.charAt(x) == PLAYER_TILE) {
					playerSpawn = new Point(x, y);
				}
				else if (line.charAt(x) == ENEMY_TILE) {
					enemySpawns.add(new Point(x, y));
				}
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
		backgroundTile = -1;
		boolean end = false;
		while (!end) {
			BufferedImage img = imageL.getImage(imgName + i + imgExt);
			if (img != null) {
				tiles.add(img);
				backgroundTile++;
			}
			else {
				System.out.println("Tile #" + i + " es null.");
				end = true;
			}
			i++;
		}
	}
	
	public int getWidth() {
		return map.length;
	}
	
	public int getHeight() {
		return map[0].length;
	}
	
	public BufferedImage getTileImage(int x, int y) {
		if ((x < 0 || x >= map.length) ||
			(y < 0 || y >= map[0].length) ||
			map[x][y] == EMPTY_TILE) {
			return null;
		}
		return tiles.get(map[x][y]);
	}
	
	public Point getPlayerSpawn() {
		return playerSpawn;
	}
	
	public ArrayList<Point> getEnemySpawns() {
		return enemySpawns;
	}
	
	public Point getTrashCanTile(int trashInd) {
		return trashCanTiles.get(trashInd);
	}
	
	public int getTrashCanTilesSize(){
		return trashCanTiles.size();
	}
	
	public ArrayList<Point> getTrashTiles(int trashInd) {
		return trashTiles.get(trashInd);
	}
	
	public int getTrashTilesSize() {
		return trashTiles.size();
	}
	
	public int getTrashTilesTotal(){
		return trashTilesTotal;
	}
	
	public int getTrashTileTypeTotal(int trashTypeInd){
		return trashTilesTypeTotal[trashTypeInd];
	}
	
	public Point getSpikeTile(int spikeInd) {
		return spikeTiles.get(spikeInd);
	}
	
	public int getSpikeTilesSize() {
		return spikeTiles.size();
	}
}
