package dev.tauri.jsg.api.util.math;

import java.util.function.Predicate;

public class MathRange implements Predicate<Float> {
	
	public float start;
    public float end;

	public MathRange(float start, float end) {
		this.start = start;
		this.end = end;
	}
	
	@Override
	public boolean test(Float x) {
		return x >= start && x <= end;
	}
	
}
