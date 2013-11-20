/**
 * @author Eduardo A. Sanchez
 * 
 * Clase que mantiene el bucle principal del juego, mandando a actualizar y pintar
 * los objetos del juego. Hereda de JPanel para utilizarse como componente en un
 * JFrame o JApplet. Optimiza la cantidad de FPS midiendo el tiempo que tarda el
 * bucle de juego en actualizarse y pintar a la pantalla.
 * 
 * Los algoritmos utilizados para mantener las FPS y ajustar el tiempo que duerme
 * el thread de juego (utilizados en el metodo run() ) se obtuvieron del libro:
 * 
 * Davidson, Andrew. (2005). Killer Game Programming in Java. 1st edition. USA: O'Reilly Media, Inc.
 * 
 * @creation 20/08/2013
 */

package game;

import image.ImageLoader;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import sound.ClipsLoader;
import sound.MidisLoader;

public abstract class GamePanel extends JPanel implements Runnable {

	private static final long serialVersionUID = 1L;
	
	protected static final int PWIDTH = 800; // tamanio de la ventana
	protected static final int PHEIGHT = 600;
	
	private Thread animator; // thread de la animacion
	/* Las siguientes variables se marcan como "volatiles" para poder utilizarlas
	 * en otros threads ademas del de la animacion del juego (i.e. el del GUI). Esto es
	 * necesario porque el GUI es quien modifica estas variables para cerrar/pausar
	 * el juego. La etiqueta de "volatile" evita que se haga un duplicado de estas
	 * variables a la memoria local del thread de la animacion, lo cual evitaria que
	 * el GUI las modifique.
	 */
	protected volatile boolean running = false; // para terminar el thread de la animacion
	protected volatile boolean isPaused = false; // para pausar
	
	private static final long period = 50; // tiempo en ms que debe tardar un solo frame de la animacion
	private static final int NO_DELAYS_PER_YIELD = 100; // limite de "frames sin dormir" que debe de soportar la animacion
	private static final int MAX_FRAME_SKIPS = 5; // limite de frames que se puede saltar la animacion cuando esta tardando mucho
	
	protected BufferedImage dbImage; // imagen buffer de la pantalla
	protected ImageLoader imageL;
	private ClipsLoader clipsL;
	private MidisLoader midisL;
		
	/**
	 * Constructor del panel
	 */
	public GamePanel() {
		super();
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(PWIDTH, PHEIGHT)); // importante para que el tamanio del JFrame se extienda para acomodar al panel
		
		setFocusable(true);
		requestFocus();
	}
	
	/**
	 * Espera hasta que el panel se agregue a un JFrame o JApplet para iniciar
	 */
	public void addNotify() {
		super.addNotify();
		startGame();
	}
	
	/**
	 * Inicializa el thread de la animacion por primera vez
	 */
	public void startGame() {		
		if (animator == null || !running) {
			animator = new Thread(this);
			animator.start();
		}
	}
	
	/**
	 * Para que el usuario pueda detener el juego
	 */
	public void stopGame() {
		running = false;
	}
	
	/**
	 * Para pausar el juego
	 */
	public void pauseGame() {
		isPaused = true;
	}
	
	/**
	 * Para reanudar el juego
	 */
	public void resumeGame() {
		isPaused = false;
	}
	
	/**
	 * Inicia el bucle principal del juego.
	 */
	/**
	 * EXPLICACION DEL ALGORITMO PARA AJUSTAR EL TIEMPO QUE DUERME EL THREAD:
	 * 
	 * El algoritmo para mantener la cantidad de FPS consiste en calcular el tiempo
	 * que transcurre durante las operaciones de actualizacion, rendering y pintar
	 * en la pantalla. Este tiempo se resta a un valor fijo de 10 ms, que corresponde
	 * al tiempo optimo que debe tardar cada frame. El resultado es la cantidad
	 * de tiempo que debe "dormir" el thread para mantener un tiempo estable de
	 * 100 FPS. Ademas se calcula el tiempo que realmente duerme el thread, para
	 * medir imprecisiones y tomar en cuenta si durmio mas del tiempo debido. Si esto
	 * pasa, en la siguiente iteracion del bucle se reduce el tiempo que durmio de
	 * mas del tiempo disponible para dormir. Finalmente, cada vez que el bucle se
	 * queda "sin dormir", se le suma 1 a un contador, para que al llegar a un n???mero
	 * limite de frames sin dormir (representado por NO_DELAYS_PER_YIELD), el thread
	 * del juego permita a otros ejecutarse; en forma paralela, se va acumulando el
	 * exceso de tiempo que toma el juego en actualizarse/renderizarse, para que cuando
	 * el total de tiempo que se ha excedido sea mayor al de un frame (10 ms), el juego
	 * comience a actualizarse sin pintar en la pantalla. En otras palabras, se empieza
	 * a saltar frames de animacion para compensar el tiempo perdido.
	 * 
	 */
	public void run() {
		long beforeTime, afterTime, timeDiff, sleepTime;
		long overSleepTime = 0L, excess = 0L;
		int noDelays = 0;
		
		beforeTime = System.currentTimeMillis();
		
		running = true;
		
		while (running) {
			gameUpdate();	// actualizar los elementos del juego
			gameRender();	// pintar los elementos a un buffer
			paintScreen();	// pintar el buffer en la pantalla
			
			afterTime = System.currentTimeMillis();
			timeDiff = afterTime - beforeTime;
			sleepTime = (period - timeDiff) - overSleepTime;
			
			if (sleepTime > 0) {
				try {
					Thread.sleep(sleepTime);
				}
				catch (InterruptedException ex) {
					System.out.println("Error mientras dormia");
				}
				
				// comparar cuanto durmio en verdad el thread en comparacion a cuanto debia
				overSleepTime = (System.currentTimeMillis() - afterTime) - sleepTime;
			}
			else { // sleepTime <= 0 : la actualizacion/animacion tardo mucho asi que no duerme esta iteracion
				overSleepTime = 0L;
				excess -= sleepTime; // se acumula el tiempo que se excedio el bucle de juego en esta iteracion
				
				// Cuando ha habido muchos retardos, el thread cede la prioridad a otros threads
				if (++noDelays >= NO_DELAYS_PER_YIELD) {
					Thread.yield();
					noDelays = 0;
				}
			}
			
			beforeTime = System.currentTimeMillis();
			
			// Saltar frames cuando el tiempo acumulado que se excede el bucle de juego es mayor a un frame (10 ms)
			int skips = 0;
			while ((excess > period) && (skips < MAX_FRAME_SKIPS)) {
				excess -= period;
				gameUpdate(); // actualizar sin llamar al render o paint para saltar frames de la animacion
				skips++;
			}
		}
		
		System.exit(0); // cerrar el JApplet/JFrame
	}
	
	/**
	 * Actualizar el estado del juego
	 */
	protected abstract void gameUpdate();
	
	/**
	 * Realizar las operaciones necesarias para pintar en la pantalla
	 */
	protected abstract void gameRender();
	
	/** 
	 * Mostrar la imagen pintada en la pantalla
	 */
	private void paintScreen() {
		Graphics g;
		try {
			g = this.getGraphics(); // obtener el contexto grafico del panel
			if ((g != null) && (dbImage != null))
				g.drawImage(dbImage, 0, 0, null);
			Toolkit.getDefaultToolkit().sync(); // ayuda a reducir el "flicker" en algunos sistemas (Linux)
			g.dispose();
		}
		catch (Exception ex) {
			System.out.println("Error del contexto grafico: " + ex);
			ex.printStackTrace(System.out);
		}
	}
	
	public int getWidth() {
		return PWIDTH;
	}
	
	public int getHeight() {
		return PHEIGHT;
	}

	public ImageLoader getImageLoader() {
		return imageL;
	}

	public void setImageLoader(ImageLoader imageL) {
		this.imageL = imageL;
	}

	public ClipsLoader getClipsLoader() {
		return clipsL;
	}

	public void setClipsLoader(ClipsLoader clipsL) {
		this.clipsL = clipsL;
	}

	public MidisLoader getMidisLoader() {
		return midisL;
	}

	public void setMidisLoader(MidisLoader midisL) {
		this.midisL = midisL;
	}
}
