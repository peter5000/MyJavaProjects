package com.oroarmor.bird;

import processing.core.*;

public class Pipe {
	public float yTop;
	public float x;
	public float width;
	public float spacing;

	PApplet p;
	PImage pipe;
	PImage pipeBottom;

	public Pipe(PApplet _p, float _x) {
		p = _p;
		x = _x;
		width = p.width / 13;
		yTop = p.random(p.height / 5, p.height * 3 / 5);
		spacing = p.height / 4;
		pipe = p.loadImage("data/pipeBottom.png");
		pipeBottom = p.loadImage("data/pipe.png");
	}

	public void draw() {

		p.pushMatrix();
		p.translate(x + width, yTop);
		p.rotate(PApplet.PI);

		p.image(pipe, 0, 0, width, 250 * width / 28);
		p.popMatrix();

		p.image(pipeBottom, x, yTop + spacing, width, 250 * width / 28);

	}

	public void move() {
		x -= p.width / 300;
		if (x < -spacing + 20) {
			x = p.width / 3 * 10;
		}
	}

}
