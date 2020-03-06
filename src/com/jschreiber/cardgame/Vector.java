package com.jschreiber.cardgame;

public class Vector {

	private double x, y, magnitude;
	
	public Vector(double x, double y) {
		magnitude = Math.sqrt(x * x + y * y);
		if (magnitude > 0) {
			this.x = x/magnitude;
			this.y = y/magnitude;
		}
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getMagnitude() {
		return magnitude;
	}
	
}
