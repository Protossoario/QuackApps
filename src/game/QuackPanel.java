package game;

import image.ImageLoader;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import sound.ClipsLoader;
import sound.MidisLoader;
import tiles.TileMap;

@SuppressWarnings("serial")
public class QuackPanel extends GamePanel {
	private static final String BACKGROUND = "fondo.jpg";
	private static final String FONTS_DIR = "fonts/";
	private static final String HUD_FONT = "FromWhereYouAre.ttf";
	
	private static final String MUSIC = "overworld";
	private static final String PICKUP = "pickup";
	private static final String COINS = "coins";
	
	private static final float HUD_FONT_SIZE = 20f;
	
	private Player player;
	private TileMap map;
	private Graphics dbg;
	private Font HUDFont;
	
	private int collectedTrashAluminio;
	private int collectedTrashOrganica;
	private int collectedTrashPapel;
	private int collectedTrashPlastico;
	
	public QuackPanel() {
		super();
		
		imageL = new ImageLoader("images.txt");
		clipsL = new ClipsLoader("clips.txt");
		midisL = new MidisLoader("midis.txt");
		
		player = new Player(this);
		map = new TileMap("level1.txt", this);
		Point playerSpawn = map.getPlayerSpawn();
		player.setPos(TileMap.tilesToPixels(playerSpawn.x), TileMap.tilesToPixels(playerSpawn.y));
		
		// Crear las fonts
		try {
			HUDFont = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getClassLoader().getResourceAsStream(FONTS_DIR + HUD_FONT));
			HUDFont = HUDFont.deriveFont(HUD_FONT_SIZE);
		} catch (FontFormatException e) {
			System.out.println("Error al leer la Font del HUD.");
			System.out.println(e);
		} catch (IOException e) {
			System.out.println("Error al leer la Font del HUD.");
			System.out.println(e);
		}
		
		addKeyListener(player);
		
		collectedTrashAluminio= 0;
		collectedTrashOrganica =0;
		collectedTrashPapel = 0;
		collectedTrashPlastico = 0;
		
		//midisL.play(MUSIC, true);
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
								1);
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
	
	private void checkTrash() {
		int fromTileX = TileMap.pixelsToTiles(player.getPos().getX());
		int fromTileY = TileMap.pixelsToTiles(player.getPos().getY());
		int toTileX = TileMap.pixelsToTiles(player.getPos().getX() + player.getWidth());
		int toTileY = TileMap.pixelsToTiles(player.getPos().getY() + player.getHeight());
		
		for (int i = 0; i < map.getTrashTilesSize(); i++) {
			ArrayList <Point> trashPieces = map.getTrashTiles(i);
			Iterator <Point> iter = trashPieces.listIterator();
			while (iter.hasNext()) {
				Point trash = iter.next();
				for (int x = fromTileX; x <= toTileX; x++) {
					for (int y = fromTileY; y <= toTileY; y++) {
						if (trash.x == x && trash.y == y) {
							switch (i) {
							case 0:
								collectedTrashAluminio++;
								break;
							case 1:
								collectedTrashOrganica++;
								break;
							case 2:
								collectedTrashPapel++;
								break;
							case 3:
								collectedTrashPlastico++;
								break;
							}
							iter.remove();
							clipsL.play(PICKUP, false);
						}
					}
				}
			}
		}
		
		for (int i = 0; i < map.getTrashCanTilesSize(); i++){
			Point trashCan = map.getTrashCanTile(i);
			for (int x = fromTileX; x <= toTileX; x++) {
				for (int y = fromTileY; y <= toTileY; y++) {
					if (trashCan.x == x && trashCan.y == y && collectedTrashAluminio > 0) {
						collectedTrashAluminio = 0;
						clipsL.play(COINS, false);
					}
				}
			}
		}
		
	}
	
	protected void gameUpdate() {
		player.update();
		checkCollisions();
		checkTrash();
	}
	
	private void renderHUD(Graphics g) {
		g.drawImage(imageL.getImage("aluminioIcon.png"), 0, 0, this);
		g.drawImage(imageL.getImage("organicaIcon.png"), 80, 0, this);
		g.drawImage(imageL.getImage("papelIcon.png"), 155, 0, this);
		g.drawImage(imageL.getImage("plasticoIcon.png"), 235, 0, this);
		if (HUDFont != null) {
			g.setFont(HUDFont);
		}
		g.setColor(Color.BLACK);
		g.drawString("X " + collectedTrashAluminio, 35, 30);
		g.drawString("X " + collectedTrashOrganica, 110, 30);
		g.drawString("X " + collectedTrashPapel, 185, 30);
		g.drawString("X " + collectedTrashPlastico, 265, 30);
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
		int offsetX = (int) (PWIDTH / 2 - (player.getPos().getX() + player.getWidth() / 2));
		int max_offsetX = PWIDTH - TileMap.tilesToPixels(map.getWidth());
		if (offsetX > 0) {
			offsetX = 0;
		}
		else if (offsetX < max_offsetX) {
			offsetX = max_offsetX;
		}
		
		/* Calculamos el offset en Y */
		int offsetY = (int) (PHEIGHT / 2 - (player.getPos().getY() + player.getHeight() / 2));
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
		
		/* Pintar al bote de basura */
		
		for (int i = 0; i < map.getTrashCanTilesSize(); i++){
			Point trashCanTile = map.getTrashCanTile(i);
			if (trashCanTile.x >= fromTileX && trashCanTile.x <= toTileX &&
					trashCanTile.y >= fromTileY && trashCanTile.y <= toTileY) {
				int drawX = TileMap.tilesToPixels(trashCanTile.x) + offsetX;
				int drawY = TileMap.tilesToPixels(trashCanTile.y) + offsetY;
				if(i == 0){
					dbg.drawImage(imageL.getImage("boteAluminio.png"),
									drawX,
									drawY, this);
				}
				else if(i == 1){
					dbg.drawImage(imageL.getImage("boteOrganica.png"),
									drawX,
									drawY, this);
				}
				else if(i == 2){
					dbg.drawImage(imageL.getImage("botePapel.png"),
										drawX,
										drawY, this);
				}
				else if(i == 3){
					dbg.drawImage(imageL.getImage("botePlastico.png"),
										drawX,
										drawY, this);
				}
			}
		}
		
		
		/* Pintar al jugador */
		player.paint(dbg, offsetX, offsetY);
		
		/* Pintar los pedazos de basura */
		for (int i = 0; i < map.getTrashTilesSize(); i++) {
			ArrayList <Point> trashPieces = map.getTrashTiles(i);
			for (Point trash : trashPieces) {
				if (trash.x >= fromTileX && trash.x <= toTileX &&
					trash.y >= fromTileY && trash.y <= toTileY) {
					int drawX = TileMap.tilesToPixels(trash.x) + offsetX;
					int drawY = TileMap.tilesToPixels(trash.y) + offsetY;
					dbg.drawImage(imageL.getImage("basuraAluminio.png"),
									drawX,
									drawY, this);
				}
			}
		}
		
		renderHUD(dbg);
	}
}
