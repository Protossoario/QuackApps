/**
 * @author Eduardo A. Sanchez y Gina Gil lozano
 * 
 * Clase utilizada para cargar clips de audio cortos.
 * Soporta multiples clips reproduciendose de manera
 * concurrente.
 * Soporta formatos AIFF, AU y WAV.
 * Se encarga de leer de un archivo los clips de audio.
 * Para ver el codigo de cargar sonido, revisar la clase
 * ClipInfo.
 * 
 * @creation 21/09/2013
 */

package sound;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class ClipsLoader {
	private static final String SOUNDS_DIR = "res/";
	private HashMap<String, ClipInfo> clipsMap;
	
	public ClipsLoader(String soundsFnm) {
		clipsMap = new HashMap<String, ClipInfo>();
		
		loadSoundsFile(SOUNDS_DIR + soundsFnm);
	}
	
	private void loadSoundsFile(String soundsFnm) {
		try {
			InputStream in = this.getClass().getClassLoader().getResourceAsStream(soundsFnm);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			
			String line;
			while ((line = reader.readLine()) != null) {
				String[] params = line.split(" ");
				if (params.length == 2) {
					load(params[0], SOUNDS_DIR + params[1]);
				}
				else {
					System.out.println("Error: el formato para el archivo de clips es \"[nombre] [nombre del archivo]\"");
				}
			}
			reader.close();
		}
		catch (IOException ex) {
			System.out.println("Error leyendo el archivo: " + soundsFnm);
			System.out.println(ex);
			System.exit(1);
		}
	}
	
	public void load(String name, String fnm) {
		if (clipsMap.containsKey(name)) {
			System.out.println("Error: " + name + " ya se encuentra almacenado");
		}
		else {
			clipsMap.put(name, new ClipInfo(name, fnm));
		}
	}
	
	public void play(String name, boolean toLoop) {
		ClipInfo clip = clipsMap.get(name);
		if (clip != null) {
			clip.play(toLoop);
		}
		else {
			System.out.println("Error: " + name + " no esta almacenado");
		}
	}
	
	public void resume(String name) {
		ClipInfo clip = clipsMap.get(name);
		if (clip != null) {
			clip.resume();
		}
		else {
			System.out.println("Error: " + name + " no esta almacenado");
		}
	}
	
	public void stop(String name) {
		ClipInfo clip = clipsMap.get(name);
		if (clip != null) {
			clip.stop();
		}
		else {
			System.out.println("Error: " + name + " no esta almacenado");
		}
	}
	
	public void stopAll() {
		ArrayList<ClipInfo> clipsList = new ArrayList<ClipInfo>(clipsMap.values());
		for (ClipInfo clip : clipsList) {
			clip.stop();
		}
	}
	
	public void pause(String name) {
		ClipInfo clip = clipsMap.get(name);
		if (clip != null) {
			clip.pause();
		}
		else {
			System.out.println("Error: " + name + " no esta almacenado");
		}
	}
	
	public void close(String name) {
		ClipInfo clip = clipsMap.get(name);
		if (clip != null) {
			clip.close();
		}
		else {
			System.out.println("Error: " + name + " no esta almacenado");
		}
	}
	
	public void setWatcher(String name, SoundsWatcher sw) {
		ClipInfo clip = clipsMap.get(name);
		if (clip != null) {
			clip.setWatcher(sw);
		}
		else {
			System.out.println("Error: " + name + " no esta almacenado");
		}
	}
}
