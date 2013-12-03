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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import sound.ClipsLoader;
import sound.MidisLoader;
import tiles.TileMap;

@SuppressWarnings("serial")
public class QuackPanel extends GamePanel implements MouseListener{
	private static final String BACKGROUND = "fondo.jpg";
	private static final String GAME_OVER = "gameOver.png";
	private static final String GAME_WIN = "gameWin.png";
	private static final String MAIN_MENU = "mainMenu.png";
	private static final String CREDITS = "credits.png";
	private static final String INSTRUCTIONS = "instructions.png";
	private static final String PAUSE = "pause.png";
	private static final String FONTS_DIR = "fonts/";
	private static final String HUD_FONT = "FromWhereYouAre.ttf";
	
	private static final String MUSIC = "overworld";
	private static final String PICKUP = "pickup";
	private static final String COINS = "coins";
		
	private static final float HUD_FONT_SIZE = 16f;
	
	private ArrayList <Enemy> enemies;
	private Player player;
	private TileMap map;
	private Graphics dbg;
	private Font HUDFont;
	
	private int trashCollectedTotal;
	private int[] trashCollected;
	private static final String[] trashNames = {"basuraAluminio.png", "basuraOrganica.png", "basuraPapel.png", "basuraPlastico.png"};
	private static final String DUCKHIT = "duck1";
	private static final String ENEMYHIT = "hit";
	private int[] trashTypeCollectedTotal;
	
	private boolean gameOver;
	private boolean gameWin;
	private boolean mainMenu;
	private boolean credits;
	private boolean instructions;
	
	private int levelCounter = 1;
	
	
	public QuackPanel() {
		super();
		mainMenu = true;
		addMouseListener(this);
		initialize("level" + levelCounter + ".txt");
	}
	
	public void initialize(String file){
		
		imageL = new ImageLoader("images.txt");
		clipsL = new ClipsLoader("clips.txt");
		midisL = new MidisLoader("midis.txt");
		
		player = new Player(this);
		enemies = new ArrayList <Enemy> ();
		map = new TileMap(file, this);
		Point playerSpawn = map.getPlayerSpawn();
		player.setPos(TileMap.tilesToPixels(playerSpawn.x), TileMap.tilesToPixels(playerSpawn.y));
		ArrayList <Point> enemySpawns = map.getEnemySpawns();
		for (Point p : enemySpawns) {
			Enemy e = new Enemy(this);
			// Se toma el tile de abajo del spawn y se le resta la altura del enemigo
			// Esto para que este alineado al piso (i.e. al borde superior del tile de abajo de su spawn)
			e.setPos(TileMap.tilesToPixels(p.x), TileMap.tilesToPixels(p.y + 1) - e.getHeight());
			enemies.add(e);
		}
		
		gameOver = false;
		gameWin = false;
		
		
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
		
		trashCollectedTotal = 0;
		trashCollected = new int[4];
		trashTypeCollectedTotal = new int[4];
		
		midisL.play(MUSIC, true);
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
							trashCollected[i]++;
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
					if (trashCan.x == x && trashCan.y == y && trashCollected[i] > 0) {
						trashCollectedTotal += trashCollected[i];
						trashTypeCollectedTotal[i] += trashCollected[i];
						trashCollected[i] = 0;
						clipsL.play(COINS, false);
						if (i == 0  && !player.getDoubleJump() && map.getTrashTileTypeTotal(0) == trashTypeCollectedTotal[0]) {
								player.setDoubleJump(true);
						}
					}
				}
			}
		}
		
		if(trashCollectedTotal == map.getTrashTilesTotal()){
			gameWin = true;
			midisL.stop();
		}
	}
	
	private void checkSpikes(){
		int fromTileX = TileMap.pixelsToTiles(player.getPos().getX());
		int fromTileY = TileMap.pixelsToTiles(player.getPos().getY());
		int toTileX = TileMap.pixelsToTiles(player.getPos().getX() + player.getWidth());
		int toTileY = TileMap.pixelsToTiles(player.getPos().getY() + player.getHeight());
		
		for (int i = 0; i < map.getSpikeTilesSize(); i++){
			Point spikeTile = map.getSpikeTile(i);
			for (int x = fromTileX; x <= toTileX; x++) {
				for (int y = fromTileY; y <= toTileY; y++) {
					if (spikeTile.x == x && spikeTile.y == y) {
						midisL.stop();
						gameOver = true;
					}
				}
			}
		}
	}
	
	
	
	protected void gameUpdate() {
		if (!gameOver && !player.getIsPaused() && !gameWin && !mainMenu) {
			player.update();
			for (Enemy e : enemies) {
				e.update();
				
				boolean edge = false;
				boolean blocked = false;
				// Checar espacio debajo
				Rectangle rect = new Rectangle(	(int) e.getPos().getX(),
												(int) e.getPos().getY() + e.getHeight() + 1,
												e.getWidth(),
												1);
				edge = map.checkEmptySpace(rect);
				// Checar colisiones en horizontal
				rect.setBounds(	(int) e.getPos().getX(),
								(int) e.getPos().getY(),
								e.getWidth(),
								e.getHeight());
				blocked = map.checkTileCollision(rect) != null;
				
				if (edge || blocked) {
					e.setFacingRight(!e.isFacingRight());
				}
				
				// Indica si el jugador es capaz de matar al enemigo, lo cual es cierto cuando el jugador esta cayendo
				boolean canKill = player.getVel().getY() > 0;
				boolean collides = player.collides(e);
				if (collides) {
					if (canKill) {
						clipsL.play(ENEMYHIT, false);
						player.getVel().setY(-player.getVel().getY());
						e.setMarkedForDeletion(true);
					}
					else if (!player.isHit()) {
						player.hit();
						clipsL.play(DUCKHIT, false);
						if (player.getLives() == 0) {
							midisL.stop();
							gameOver = true;
						}
					}
				}
			}
			Iterator <Enemy> iter = enemies.iterator();
			while (iter.hasNext()) {
				Enemy e = iter.next();
				if (e.isMarkedForDeletion()) {
					iter.remove();
				}
			}
			checkCollisions();
			checkTrash();
			checkSpikes();
		}
		
	}
	
	private void renderHUD(Graphics g) {
		g.drawImage(imageL.getImage("aluminioIcon.png"), 0, 0, this);
		g.drawImage(imageL.getImage("organicaIcon.png"), 80, 0, this);
		g.drawImage(imageL.getImage("papelIcon.png"), 155, 0, this);
		g.drawImage(imageL.getImage("plasticoIcon.png"), 224, 0, this);
		if (HUDFont != null) {
			g.setFont(HUDFont);
		}
		g.setColor(Color.BLACK);
		g.drawString("X " + trashCollected[0], 35, 20);
		g.drawString("X " + trashCollected[1], 110, 20);
		g.drawString("X " + trashCollected[2], 185, 20);
		g.drawString("X " + trashCollected[3], 252, 20);
		g.drawString( trashCollectedTotal + "/" + map.getTrashTilesTotal() , 740, 30);
		g.drawString(trashTypeCollectedTotal[0] + "/" + map.getTrashTileTypeTotal(0), 35, 38);
		g.drawString(trashTypeCollectedTotal[1] + "/" + map.getTrashTileTypeTotal(1), 110, 38);
		g.drawString(trashTypeCollectedTotal[2] + "/" + map.getTrashTileTypeTotal(2), 185, 38);
		g.drawString(trashTypeCollectedTotal[3] + "/" + map.getTrashTileTypeTotal(3), 252, 38);
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
		
		/* Pintar los enemigos */
		for (Enemy e : enemies) {
			e.paint(dbg, offsetX, offsetY);
		}
		
		/* Pintar los pedazos de basura */
		for (int i = 0; i < map.getTrashTilesSize(); i++) {
			ArrayList <Point> trashPieces = map.getTrashTiles(i);
			for (Point trash : trashPieces) {
				if (trash.x >= fromTileX && trash.x <= toTileX &&
					trash.y >= fromTileY && trash.y <= toTileY) {
					BufferedImage img = imageL.getImage(trashNames[i]);
					int drawX = TileMap.tilesToPixels(trash.x) + offsetX + img.getWidth() / 2;
					int drawY = TileMap.tilesToPixels(trash.y) + offsetY + img.getHeight() / 2;
					dbg.drawImage(img, drawX, drawY, this);
				}
			}
		}
		
		renderHUD(dbg);
		if(gameOver){
			dbg.drawImage(imageL.getImage(GAME_OVER), 0, 0, this);
		}
		
		if(gameWin){
			dbg.drawImage(imageL.getImage(GAME_WIN), 0, 0, this);
		}
		
		if(mainMenu){
			dbg.drawImage(imageL.getImage(MAIN_MENU), 0, 0, this);
		}
		
		if(credits){
			dbg.drawImage(imageL.getImage(CREDITS), 0, 0, this);
		}
		
		if(instructions){
			dbg.drawImage(imageL.getImage(INSTRUCTIONS), 0, 0, this);
		}
		
		if(player.getIsPaused() && !gameOver && !gameWin && !mainMenu){
			dbg.drawImage(imageL.getImage(PAUSE), 0, 0, this);
		}
		
		
	}
	
		public void mouseReleased(MouseEvent e) {
			if(mainMenu){
				if(e.getX()>= 122 && e.getX() <= 122+454 && e.getY()>= 183 && e.getY() <= 183+91 && !credits && !instructions){
					mainMenu = false;
				}
				
				if(e.getX()>= 122 && e.getX() <= 122+454 && e.getY()>= 341 && e.getY() <= 341+91 && !credits){
					instructions = true;
				}
				
				if(e.getX()>= 122 && e.getX() <= 122+454 && e.getY()>= 463 && e.getY() <= 463+91 && !instructions){
					credits = true;
				}
				
			}
			
			if(credits){
				if(e.getX()>= 551 && e.getX() <= 551+223 && e.getY()>= 539 && e.getY() <= 539+49){
					credits = false;
				} 
			}
			
			if(instructions){
				if(e.getX()>= 551 && e.getX() <= 551+223 && e.getY()>= 539 && e.getY() <= 539+49){
					instructions = false;
				} 
			}
			
			if(gameOver){
				if(e.getX()>= 551 && e.getX() <= 551+223 && e.getY()>= 539 && e.getY() <= 539+49){
					mainMenu = true;
					levelCounter = 1;
					initialize("level1.txt");
				} 
				
				if(e.getX()>= 551 && e.getX() <= 551+223 && e.getY()>= 484 && e.getY() <= 484+49){
					initialize("level" + levelCounter + ".txt");
				} 
			}
			
			
			if(gameWin) {
				if(e.getX()>= 551 && e.getX() <= 551+223 && e.getY()>= 539 && e.getY() <= 539+49){
					mainMenu = true;
					levelCounter = 1;
					initialize("level1.txt");
				} 
				
				if(e.getX()>= 551 && e.getX() <= 551+223 && e.getY()>= 490 && e.getY() <= 490+49){
						initialize("level" + levelCounter + ".txt");	
				}
				
				if(e.getX()>= 551 && e.getX() <= 551+223 && e.getY()>= 443 && e.getY() <= 443+49){
					levelCounter++;
					if(map.peekMap("maps/level" + levelCounter + ".txt")) {
						initialize("level" + levelCounter + ".txt");
					}
					else{
						levelCounter = 1;
						initialize("level" + levelCounter + ".txt");
					}
				} 
			}	
		}

		public void mousePressed(MouseEvent e) {

		}

		public void mouseEntered(MouseEvent e) {

		}

		public void mouseExited(MouseEvent e) {

		}

		public void mouseClicked(MouseEvent e) {

		}

}
