/**
 * @author Eduardo A. Sanchez
 * 
 * Clase que se encarga de cargar los clips de audio,
 * y almacenar la informacion para controlar la
 * reproduccion de cada uno.
 * 
 * @creation 21/09/2013
 */

package sound;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class ClipInfo implements LineListener {
	private Clip clip;
	private String name;
	private SoundsWatcher watcher;
	
	private boolean isLooping;
	
	public ClipInfo(String name, String fnm) {
		this.name = name;
		loadClip(fnm);
	}
	
	private void loadClip(String fnm) {
		try {
			// Acceder al archivo de audio como un stream
			AudioInputStream stream = AudioSystem.getAudioInputStream(this.getClass().getClassLoader().getResourceAsStream(fnm));
			
			// Obtener el formato del audio a partir de los datos del stream
			AudioFormat format = stream.getFormat();
			
			// Obtener informacion para crear una linea
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			
			// Convertir formatos ULAW/ALAW a formato PCM
			if (	format.getEncoding() == AudioFormat.Encoding.ULAW ||
					format.getEncoding() == AudioFormat.Encoding.ALAW ) {
				AudioFormat newFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
														format.getSampleRate(),
														format.getSampleSizeInBits() * 2,
														format.getChannels(),
														format.getFrameSize() * 2,
														format.getFrameRate(), true);
				// Actualizar el stream y el formato
				stream = AudioSystem.getAudioInputStream(newFormat, stream);
				format = newFormat;
				System.out.println("Formato de audio convertido: " + newFormat);
			}
			
			// Asegurar que el sistema de sonido soporte esta linea
			if (!AudioSystem.isLineSupported(info)) {
				System.out.println("Archivo de Clip no soportado: " + fnm);
			}
			
			// Crear un Clip vacio usando la informacion de la linea
			clip = (Clip) AudioSystem.getLine(info);
			
			// Monitorear los eventos de linea del Clip
			clip.addLineListener(this);
			
			// Abrir el stream de audio como un clip, alistandolo para reproducirlo
			clip.open(stream);
			stream.close();
		}
		catch (UnsupportedAudioFileException audioException) {
			System.out.println("Archivo de audio no soportado: " + fnm);
		}
		catch (LineUnavailableException lineException) {
			System.out.println("No hay linea de audio disponible para: " + fnm);
		}
		catch (IOException ioException) {
			System.out.println("Error al leer: " + fnm);
		}
		catch (Exception ex) {
			System.out.println("Error con el archivo: " + fnm);
			ex.printStackTrace();
		}
	}

	public void play(boolean toLoop) {
		if (clip != null) {
			isLooping = toLoop;
			clip.start();
		}
	}
	
	public void resume() {
		if (clip != null) {
			clip.start();
		}
	}
	
	public void stop() {
		if (clip != null) {
			clip.stop();
			clip.setFramePosition(0);
		}
	}
	
	public void pause() {
		if (clip != null) {
			clip.stop();
		}
	}
	
	public void close() {
		if (clip != null) {
			clip.stop();
			clip.close();
		}
	}
	
	/**
	 * Actualiza el estado del Clip cuando termina de reproducirse.
	 * Si el clip es 'looped', lo vuelve a empezar.
	 * En ambos casos, notifica al objeto SoundsWatcher, si es que
	 * existe, cual es el estado actual del Clip.
	 */
	public void update(LineEvent event) {
		if (event.getType() == LineEvent.Type.STOP) {
			clip.stop();
			clip.setFramePosition(0);
			
			if (!isLooping) {
				if (watcher != null) {
					watcher.atSequenceEnd(name, SoundsWatcher.STOPPED);
				}
			}
			else {
				clip.start();
				if (watcher != null) {
					watcher.atSequenceEnd(name, SoundsWatcher.REPLAYED);
				}
			}
		}
	}
	
	public String getName() {
		return name;
	}
	
	public void setWatcher(SoundsWatcher watcher) {
		this.watcher = watcher;
	}
}
