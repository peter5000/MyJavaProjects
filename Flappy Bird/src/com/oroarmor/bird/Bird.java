package com.oroarmor.bird;

import com.oroarmor.network.*;

import processing.core.*;

public class Bird {
	float yVel;
	float y;
	float r;
	boolean alive;
	PApplet p;
	NeurNet network;
	float fitness;

	public Bird(PApplet _p) {
		p = _p;
		y = p.height / 2;
		r = p.width / 30;
		alive = true;
		int[] thing = { 4, 4, 4, 1 };
		network = new NeurNet(thing);
	}

	void update() {
		y += yVel;
		yVel += (float) 0.7 * p.height / p.displayHeight;
		y = PApplet.constrain(y, r / 2, p.height - r / 2);
		if (y - r < 0 || y + r > p.height) {
			alive = false;
		}
	}

	void jump() {
		yVel = -10 * p.height / p.displayHeight;
	}

	void draw() {
		if (!alive) {
			return;
		} else {
			p.fill(255, 255, 255, 50);
		}
		p.ellipse(p.width / 7, y, r, r);
	}

	void drawBest() {
		p.fill(0, 0, 255, 100);
		if (!alive) {
			p.fill(255, 0, 0, 50);
		}
		p.ellipse(p.width / 7, y, r, r);
	}

	public void checkCollision(Pipe pipe) {
		if (pipe.x < p.width / 7 + r && pipe.x + pipe.width > p.width / 7 - r) {
			if (y - r < pipe.yTop || y + r > pipe.yTop + pipe.spacing) {
				alive = false;
			}
		}
	}

	public void network(Pipe nearestPipe) {
		// TODO Auto-generated method stub
		float[] inputs = { this.y, p.width / 7, nearestPipe.yTop + nearestPipe.spacing - this.y,
				this.y - nearestPipe.yTop };
		if (network.feedforward(inputs)[0] > 0.5) {
			jump();
		}
	}

	public static Bird cross(Bird bird, Bird bird2) {
		// TODO Auto-generated method stub
		Bird newBird = new Bird(bird.p);
		int[] thing = { 4, 4, 4, 1 };
		NeurNet avNetwork = averageNetworks(bird.network, bird2.network, thing);

		NeurNet randomNet = new NeurNet(thing);

		NeurNet newNetwork = averageNetworks(avNetwork, randomNet, thing);

		for (int i = 0; i < 5; i++) {
			newNetwork = averageNetworks(avNetwork, newNetwork, thing);
		}

		newBird.network = newNetwork;

		return newBird;
	}

	private static NeurNet averageNetworks(NeurNet one, NeurNet two, int[] hidden) {
		NeurNet newNetwork = new NeurNet(hidden);
		newNetwork.weights_ih = averageMatrix(one.weights_ih, two.weights_ih);

		for (int i = 0; i < newNetwork.weights_hh.length; i++) {

			newNetwork.weights_hh[i] = averageMatrix(one.weights_hh[i], two.weights_hh[i]);
		}
		newNetwork.weights_ho = averageMatrix(one.weights_ho, two.weights_ho);

		// bais

		Matrix[] temp2 = new Matrix[one.bias_h.length];
		for (int i = 0; i < temp2.length; i++) {
			temp2[i] = averageMatrix(one.bias_h[i], two.bias_h[i]);
		}
		newNetwork.bias_h = temp2;
		newNetwork.bias_o = averageMatrix(one.bias_o, two.bias_o);

		return newNetwork;
	}

	private static Matrix averageMatrix(Matrix one, Matrix two) {
		Matrix newMatrix = new Matrix(one.rows, one.cols);

		newMatrix.add(one);
		newMatrix.add(two);
		newMatrix.mult((float) 0.5);

		return newMatrix;
	}

	public void calcFitness(Pipe pipe, int genTime) {
		fitness = ((float) genTime) / 30f + 1f / (Math.abs(y - (pipe.yTop + pipe.spacing / 2)));
	}

	public float getFitness() {
		return fitness;
	}
}
