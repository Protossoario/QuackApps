package sound;

public interface SoundsWatcher {
	static final int STOPPED = 0;
	static final int REPLAYED = 1;

	public void atSequenceEnd(String name, int state);
}
