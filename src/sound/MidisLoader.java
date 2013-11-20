package sound;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Transmitter;

public class MidisLoader implements MetaEventListener {
	private static final String SOUNDS_DIR = "res/";
	private static final int END_OF_TRACK = 47;
	
	private Sequencer sequencer;
	private HashMap<String, MidiInfo> midisMap;
	private MidiInfo currentMidi;
	private SoundsWatcher watcher;
	
	public MidisLoader(String soundsFnm) {
		midisMap = new HashMap<String, MidiInfo>();
		currentMidi = null;
		watcher = null;
		
		initSequencer();
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
		if (midisMap.containsKey(name)) {
			System.out.println("Error: " + name + " ya se encuentra almacenado");
		}
		else if (sequencer == null) {
			System.out.println("Error: no hay secuenciador para " + name);
		}
		else {
			midisMap.put(name, new MidiInfo(name, fnm, sequencer));
		}
	}

	private void initSequencer() {
		try {
			sequencer = MidiSystem.getSequencer();
			
			if (sequencer == null) {
				System.out.println("Error: no se pudo crear el secuenciador");
				System.exit(0);
			}
			
			sequencer.open();
			sequencer.addMetaEventListener(this);
			
			if (!(sequencer instanceof Synthesizer)) {
				System.out.println("Conectando el secuenciador con el sintetizador");
				Synthesizer synthesizer = MidiSystem.getSynthesizer();
				synthesizer.open();
				Receiver synthReceiver = synthesizer.getReceiver();
				Transmitter seqTransmitter = sequencer.getTransmitter();
				seqTransmitter.setReceiver(synthReceiver);
			}
		}
		catch (MidiUnavailableException midiException) {
			System.out.println("No hay secuenciador disponible");
			System.exit(0);
		}
	}
	
	public void play(String name, boolean toLoop) {
		MidiInfo midi = midisMap.get(name);
		if (midi == null) {
			System.out.println("Error: " + name + " no esta almacenado");
		}
		else {
			if (currentMidi != null) {
				System.out.println("Error: " + currentMidi.getName() + " ya se esta reproduciendo");
			}
			else {
				currentMidi = midi;
				midi.play(toLoop);
			}
		}
	}
	
	public void pause() {
		if (currentMidi != null) {
			currentMidi.pause();
		}
		else {
			System.out.println("No hay musica reproduciendose");
		}
	}
	
	public void stop() {
		if (currentMidi != null) {
			currentMidi.stop();
		}
		else {
			System.out.println("No hay musica reproduciendose");
		}
	}
	
	public void resume() {
		if (currentMidi != null) {
			currentMidi.resume();
		}
		else {
			System.out.println("No hay musica reproduciendose");
		}
	}
	
	public void close() {
		stop();
		if (sequencer != null) {
			if (sequencer.isRunning()) {
				sequencer.stop();
			}
			
			sequencer.removeMetaEventListener(this);
			sequencer.close();
			sequencer = null;
		}
	}
	
	public void setWatcher(SoundsWatcher watcher) {
		this.watcher = watcher;
	}

	public void meta(MetaMessage meta) {
		if (meta.getType() == END_OF_TRACK) {
			String name = currentMidi.getName();
			boolean hasLooped = currentMidi.tryLooping();
			if (!hasLooped) {
				currentMidi = null;
			}
			
			if (watcher != null) {
				if (hasLooped) {
					watcher.atSequenceEnd(name, SoundsWatcher.REPLAYED);
				}
				else {
					watcher.atSequenceEnd(name, SoundsWatcher.STOPPED);
				}
			}
		}
	}
}
