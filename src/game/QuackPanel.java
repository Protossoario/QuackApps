package game;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

@SuppressWarnings("serial")
public class QuackPanel extends GamePanel {
	private Player player;
	private Graphics dbg;
	
	public QuackPanel() {
		super();
		
		player = new Player(this);
	}
	
	protected void gameUpdate() {
		player.update();
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
		
		player.paint(dbg);
	}
}
