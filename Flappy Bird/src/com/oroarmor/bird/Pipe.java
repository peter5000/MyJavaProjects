package com.oroarmor.bird;

import processing.core.*;

public class Pipe {
	public float yTop;
	public float x;
	public float width;
	public float spacing;

	PApplet p;

	public Pipe(PApplet _p, float _x) {
		p = _p;
		x = _x;
		width = p.width / 13;
		yTop = p.random(p.height / 5, p.height * 3 / 5);
		spacing = p.height / 4;
	}

	public void draw() {
		p.fill(255);
		p.rect(x, 0, width, yTop);
		p.rect(x, yTop + spacing, width, p.height);
	}

	public void move() {
		x -= p.width / 300;
		if (x < -spacing + 20) {
			x = p.width / 3 * 10;
		}
	}

}
