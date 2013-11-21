package game;

import image.ImageLoader;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import sound.ClipsLoader;
import sound.MidisLoader;
import tiles.TileMap;

@SuppressWarnings("serial")
public class QuackPanel extends GamePanel {
	private static final String BACKGROUND = "fondo.png";
	private Player player;
	private TileMap map;
	private Graphics dbg;
	
	public QuackPanel() {
		super();
		
		imageL = new ImageLoader("images.txt");
		clipsL = new ClipsLoader("sounds.txt");
		midisL = new MidisLoader("sounds.txt");
		
		player = new Player(this);
		player.setPos(TileMap.tilesToPixels(3), 0);
		map = new TileMap("level1.txt", this);
		
		addKeyListener(player);
	}
	
	private void checkCollisions() {
		/* Actualizamos la posicion en X en base a si hay o no colision */
		double posX = player.getPos().getX();
		double velX = player.getVel().getX();
		double newX = posX + velX;
		Rectangle playerBox = new Rectangle((int) newX, (int) player.getPos().getY(),
											player.getWidth(), player.getHeight());
		Point tile = map.checkTileCollision(playerBox);
		// No hubo colision en X, asi que actualizamos su posicion en este eje
		if (tile == null) {
			player.getPos().setX(newX);
		}
		else {
			// Si esta moviendo a la derecha, alinearlo al borde izquierdo del tile
			if (velX > 0) {
				newX = TileMap.tilesToPixels(tile.x) - player.getWidth();
				player.getPos().setX(newX);
			}
			// Si se mueve a la izquierda, alinearlo al borde derecho del tile
			else if (velX < 0) {
				newX = TileMap.tilesToPixels(tile.x + 1);
				player.getPos().setX(newX);
			}
			
			player.getVel().setX(0);
		}
		
		/* Lo mismo para la posicion en Y */
		double posY = player.getPos().getY();
		double velY = player.getVel().getY();
		double newY = posY + velY;
		playerBox.setBounds((int) player.getPos().getX(), (int) newY,
											player.getWidth(), player.getHeight());
		tile = map.checkTileCollision(playerBox);
		// No hubo colision en Y, asi que actualizamos su posicion en este eje
		if (tile == null) {
			player.getPos().setY(newY);
			
			// Si hay espacio vacio debajo del pato, avisarle que esta cayendo
			playerBox.setBounds((int) player.getPos().getX(),
								(int) (newY + player.getHeight()) + 1,
								player.getWidth(),
								player.getHeight());
			tile = map.checkTileCollision(playerBox);
			if (tile == null && player.isOnGround()) {
				player.setOnGround(false);
			}
		}
		else {
			// Si esta cayendo (moviendose hacia abajo), alinearlo al borde superior del tile
			if (velY > 0) {
				newY = TileMap.tilesToPixels(tile.y) - player.getHeight();
				player.getPos().setY(newY);
				if (!player.isOnGround()) {
					player.setOnGround(true);
				}
			}
			// Si esta saltando (moviendose hacia arriba), alinearlo al borde inferior del tile
			else if (velY < 0) {
				newY = TileMap.tilesToPixels(tile.y + 1);
				player.getPos().setY(newY);
			}
			
			player.getVel().setY(0);
		}
	}
	
	protected void gameUpdate() {
		player.update();
		checkCollisions();
	}

	protected void gameRender() {
		if (dbImage == null) { // inicializar la imagen buffer si es que no lo esta
			dbImage = getImageLoader().createCompatible(getWidth(), getHeight(), BufferedImage.BITMASK);
			if (dbImage == null) {
				System.out.println("No se pudo crear la imagen buffer");
				return;
			}
			else {
				dbg = dbImage.getGraphics();
			}
		}

		/* Calculamos el offset en X */
		int offsetX = (int) (PWIDTH / 2 - (player.getPos().getX() + player.getWidth()));
		int max_offsetX = PWIDTH - TileMap.tilesToPixels(map.getWidth());
		if (offsetX > 0) {
			offsetX = 0;
		}
		else if (offsetX < max_offsetX) {
			offsetX = max_offsetX;
		}
		
		/* Calculamos el offset en Y */
		int offsetY = (int) (PHEIGHT / 2 - (player.getPos().getY() + player.getHeight()));
		int max_offsetY = PHEIGHT - TileMap.tilesToPixels(map.getHeight());
		if (offsetY > 0) {
			offsetY = 0;
		}
		else if (offsetY < max_offsetY) {
			offsetY = max_offsetY;
		}
		
		dbg.setColor(Color.WHITE);
		dbg.fillRect(0, 0, PWIDTH, PHEIGHT);
		
		/* Pintar el fondo con parallaxing */
		BufferedImage background = imageL.getImage(BACKGROUND);
		int bgX = offsetX *
				(PWIDTH - background.getWidth()) /
				(PWIDTH - TileMap.tilesToPixels(map.getWidth()));
		int bgY = PHEIGHT - background.getHeight();
		dbg.drawImage(background, bgX, bgY, this);
		
		/* Pintar los tiles visibles */
		int fromTileX = TileMap.pixelsToTiles(-offsetX);
		int fromTileY = TileMap.pixelsToTiles(-offsetY);
		int toTileX = fromTileX + TileMap.pixelsToTiles(PWIDTH) + 1;
		int toTileY = fromTileY + TileMap.pixelsToTiles(PHEIGHT) + 1;
		for (int x = fromTileX; x <= toTileX; x++) {
			for (int y = fromTileY; y <= toTileY; y++) {
				BufferedImage tileImg = map.getTileImage(x, y);
				if (tileImg != null) {
					dbg.drawImage(tileImg,
								TileMap.tilesToPixels(x) + offsetX,
								TileMap.tilesToPixels(y) + offsetY, this);
				}
			}
		}
		
		/* Pintar al jugador */
		dbg.drawImage(player.getCurrentImage(),
					(int) (player.getPos().getX() + offsetX),
					(int) (player.getPos().getY() + offsetY), this);
	}
}
