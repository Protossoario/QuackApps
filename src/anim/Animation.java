/**
 * @author Eduardo A. Sanchez y Gina Gil lozano
 * 
 * Clase para manejar sprites animados. Lleva la cuenta
 * del sprite que corresponde a cada Frame para una
 * animacion determinada (e.g. el jugador moviendose a la izquierda,
 * saltando, o moviendose a la derecha, etc.).
 * La duracion de cada Frame individual se puede escoger por medio
 * del metodo addFrame().
 * 
 * @creation 21/09/2013
 */

package anim;

import java.util.ArrayList;

public class Animation {
	private static final int DEFAULT_FRAME_TIME = 1;
	
	private ArrayList<Frame> frameList;
	private ArrayList<AnimationObserver> observers;
	
	private int currentIndex;
	private int maxIndex;
	private int elapsedTime;
	private int maxTime;
	
	private boolean looping; // indica si la animacion se repite
	
	Animation(String ... _frames) {
		maxTime = 0;
		maxIndex = 0;
		elapsedTime = 0;
		currentIndex = 0;
		looping = false;
		
		frameList = new ArrayList<Frame>();
		for (String f : _frames) {
			maxTime += DEFAULT_FRAME_TIME;
			maxIndex++;
			Frame frame = new Frame(f, maxTime);
			frameList.add(frame);
		}
		
		observers = new ArrayList<AnimationObserver>();
	}
	
	Animation() {
		maxTime = 0;
		maxIndex = 0;
		elapsedTime = 0;
		currentIndex = 0;
		looping = false;
		
		frameList = new ArrayList<Frame>();
		
		observers = new ArrayList<AnimationObserver>();
	}
	
	public void updateAnimation() {
		elapsedTime++;
		
		Frame currentFrame = frameList.get(currentIndex);
		if (elapsedTime > currentFrame.getEndTime() && currentIndex < maxIndex - 1) {
			currentIndex++;
		}
		
		if (elapsedTime > maxTime) {
			if (!looping) {
				for (AnimationObserver observer : observers) {
					observer.animationFinished(new AnimationEvent(this));
				}
			}
			else {
				elapsedTime = 0;
				currentIndex = 0;
			}
		}
	}
	
	public void addFrame(String spriteName, int duration) {
		maxTime += duration;
		maxIndex++;
		frameList.add(new Frame(spriteName, maxTime));
	}
	
	public void setLooping(boolean loops) {
		looping = loops;
	}
	
	public boolean isLooping() {
		return looping;
	}
	
	public void addAnimationObserver(AnimationObserver observer) {
		observers.add(observer);
	}
	
	public ArrayList<AnimationObserver> getAnimationObservers() {
		return observers;
	}

	public String getCurrentSprite() {
		return frameList.get(currentIndex).getSpriteName();
	}
	
	public void startAnimation() {
		elapsedTime = 0;
		currentIndex = 0;
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}
}
