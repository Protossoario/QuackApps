package anim;

public class Frame {
	private String spriteName;
	private int endTime;
	
	Frame(String spriteName, int endTime) {
		this.spriteName = spriteName;
		this.endTime = endTime;
	}

	public String getSpriteName() {
		return spriteName;
	}

	public void setSpriteName(String spriteName) {
		this.spriteName = spriteName;
	}

	public int getEndTime() {
		return endTime;
	}

	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}
}
