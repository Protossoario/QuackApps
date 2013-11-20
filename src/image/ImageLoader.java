/**
 * @author Eduardo A. Sanchez y Gina gil lozano
 * 
 * Clase que carga una serie de imagenes en base a un archivo de texto
 * que lista los nombres de aquellas que deben ser cargadas.
 * De esta manera se pueden cargar todas las imagenes que requiere,
 * por ejemplo, un nivel del juego, al inicializar.
 * 
 * @creation 30/08/2013
 */

package image;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class ImageLoader {
	private static final String IMGS_DIR = "res/";
	
	private HashMap<String, BufferedImage> imagesMap;
	private GraphicsConfiguration gc;

	public ImageLoader(String file) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
		
		imagesMap = new HashMap<String, BufferedImage>();
		
		String imagesFnm = IMGS_DIR + file;
		loadImagesFile(imagesFnm);
	}
	
	private void loadImagesFile(String imagesFnm) {
		try {
			InputStream in = this.getClass().getClassLoader().getResourceAsStream(imagesFnm);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			
			String line;
			while ((line = reader.readLine()) != null) {
				BufferedImage im = loadImage(line);
				if (im != null) {
					imagesMap.put(line, im);
				}
			}
			reader.close();
		}
		catch (IOException ex) {
			System.out.println("Error leyendo el archivo: " + imagesFnm);
			System.out.println(ex);
			System.exit(1);
		}
		catch (Exception ex) {
			System.out.println("Error leyendo el archivo: " + imagesFnm);
			System.out.println(ex);
			System.exit(1);
		}
	}

	/**
	 * Se carga una imagen del directorio de imagenes.
	 * Se crea una copia compatible con el contexto grafico para optimizar
	 * las operaciones de dibujar la imagen.
	 */
	private BufferedImage loadImage(String imgName) {
		if (imgName == null || imgName == "") {
			return null;
		}
		try {
			BufferedImage im = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream(IMGS_DIR + imgName));
			int transparency = im.getColorModel().getTransparency();
			BufferedImage compatible = gc.createCompatibleImage(im.getWidth(), im.getHeight(), transparency);
			
			Graphics g = compatible.createGraphics();
			g.drawImage(im, 0, 0, null);
			g.dispose();
			return compatible;
		}
		catch (IOException ex) {
			System.out.println("Error al cargar la imagen: " + imgName);
			System.out.println(ex);
			return null;
		}
		catch (Exception ex) {
			System.out.println("Error al cargar la imagen: " + imgName);
			System.out.println(ex);
			return null;
		}
	}
	
	/**
	 * Regresa una imagen guardada en el HashMap.
	 * Si la imagen no esta cargada, regresa null.
	 */
	public BufferedImage getImage(String imgName) {
		return imagesMap.get(imgName);
	}
	
	/**
	 * Crear una imagen compatible con el contexto grafico.
	 */
	public BufferedImage createCompatible(int width, int height, int transparency) {
		return gc.createCompatibleImage(width, height, transparency);
	}
}
