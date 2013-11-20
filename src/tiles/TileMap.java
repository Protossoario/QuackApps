package tiles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class TileMap {
	// Matriz bidimensional que guarda el numero de tile que corresponde al subindice (i, j)
	// Para cada subindice, se guarda un numero (0, 1, 2, etc.) que indica el numero de tile que es
	// De lo contrario, se marca con un -1 los espacios vacios
	int map[][];
	
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
				map[i][j] = -1;
			}
		}
	}
	
	/**
	 * Constructor que manda a cargar los datos del tilemap
	 * @param file Es el nombre del archivo de texto de donde se leera la informacion del tilemap
	 */
	public TileMap(String file) {
		loadMap("maps/" + file);
	}
}
