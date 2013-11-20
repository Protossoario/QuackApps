package sound;

import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

public class MidiInfo {
	private String name, filename;
	private Sequence sequence = null;
	private Sequencer sequencer;
	private boolean isLooping;
	
	public MidiInfo(String name, String fnm, Sequencer sequencer) {
		this.name = name;
		this.sequencer = sequencer;
		this.filename = fnm;
		loadMidi();
	}

	private void loadMidi() {
		try {
			sequence = MidiSystem.getSequence(this.getClass().getClassLoader().getResourceAsStream(filename));
		}
		catch (InvalidMidiDataException midiException) {
			System.out.println("Error: archivo midi ilegible/no soportado: " + filename);
		}
		catch (IOException ioException) {
			System.out.println("Error al leer: " + filename);
		}
		catch (Exception ex) {
			System.out.println("Error con el archivo: " + filename);
			ex.printStackTrace();
		}
	}

	public String getName() {
		return name;
	}

	public void play(boolean toLoop) {
		if ((sequencer != null) && (sequence != null)) {
			try {
				sequencer.setSequence(sequence);
				sequencer.setTickPosition(0);
				isLooping = toLoop;
				sequencer.start();
			}
			catch (InvalidMidiDataException midiException) {
				System.out.println("Archivo midi invalido: " + filename);
			}
		}
	}

	public void pause() {
		if ((sequencer != null) && (sequence != null)) {
			if (sequencer.isRunning()) {
				sequencer.stop();
			}
		}
	}

	public void resume() {
		if ((sequencer != null) && (sequence != null)) {
			sequencer.start();
		}
	}

	public void stop() {
		if ((sequencer != null) && (sequence != null)) {
			isLooping = false;
			if (!sequencer.isRunning()) {
				sequencer.start();
			}
			sequencer.setTickPosition(sequencer.getTickLength());
		}
	}

	/**
	 * Metodo que reinicia la secuencia MIDI. Regresa un valor
	 * booleano para validar cuando se ejecuta correctamente.
	 */
	public boolean tryLooping() {
		if ((sequencer != null) && (sequence != null)) {
			if (sequencer.isRunning()) {
				sequencer.stop();
			}
			sequencer.setTickPosition(0);
			if (isLooping) {
				sequencer.start();
				return true;
			}
		}
		return false;
	}
}
