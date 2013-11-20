package anim;

import java.util.EventObject;

@SuppressWarnings("serial")
public class AnimationEvent extends EventObject {
	public AnimationEvent(Object source) {
		super(source);
	}
	
	public Animation getSource() {
		return (Animation) super.getSource();
	}
}
