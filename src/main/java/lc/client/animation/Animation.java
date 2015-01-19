package lc.client.animation;

import java.util.ArrayList;

import lc.common.util.data.ImmutablePair;

public abstract class Animation {

	private int time = 0, duration = 0;
	private ArrayList<ImmutablePair<Integer, Keyframe>> frames;
	private ArrayList<Keyframe> runningFrames;

	public Animation() {
		frames = new ArrayList<ImmutablePair<Integer, Keyframe>>();
		runningFrames = new ArrayList<Keyframe>();
	}

	public void init() {
		duration = 0;
		for (ImmutablePair<Integer, Keyframe> frame : frames) {
			int end = frame.getA() + frame.getB().duration();
			if (end > duration)
				duration = end;
		}
	}

	public void think() {
		if (time > duration)
			return;

		for (Keyframe frame : runningFrames)
			stepFrame(frame);

		for (ImmutablePair<Integer, Keyframe> frame : frames)
			if (frame.getA() == time) {
				initFrame(frame.getB());
				runningFrames.add(frame.getB());
			}

		time++;
	}

	private void initFrame(Keyframe b) {
		// TODO Auto-generated method stub

	}

	private void stepFrame(Keyframe frame) {
		// TODO Auto-generated method stub

	}

}
