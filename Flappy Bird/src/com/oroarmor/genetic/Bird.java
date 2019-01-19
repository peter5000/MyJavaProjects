package com.oroarmor.genetic;

import java.util.ArrayList;
import com.oroarmor.bird.Pipe;
import com.oroarmor.network.*;
import processing.core.PApplet;

public class Bird extends GeneticCreature {

	public NeurNet brain;
	public int parentNum;
	PApplet p;
	public float fitness;

	float y;
	float yVel;
	float r;
	boolean alive;
	int[] brainConfig;
	int jumpCooldown;

	public Bird(int[] _brainConfig, int _parentNum, PApplet _p) {
		super(_brainConfig, _parentNum);
		brainConfig = _brainConfig;
		brain = new NeurNet(brainConfig);
		parentNum = _parentNum;
		p = _p;
		setup();
	}

	public void setup() {
		y = p.height / 2;
		yVel = 0;
		r = p.width / 30;
		alive = true;
		fitness = 0;
	}

	public void run(float[] inputs) {
		update();
		inputs[0] = y;
		inputs[1] = p.width / 7;
		inputs[2] = inputs[2] - y;
		inputs[3] = y - inputs[3];
		draw();
		float thought = brain.feedforward(inputs)[0];
		if (thought > 0.5) {
			jump();
		}
	}

	void update() {
		y += yVel;
		yVel += (float) 0.7 * p.height / p.displayHeight;
		y = PApplet.constrain(y, r / 2, p.height - r / 2);
		if (y - r < 0 || y + r > p.height) {
			alive = false;
		}
		jumpCooldown--;
	}

	void jump() {
		if (jumpCooldown < 0) {
			yVel = -10 * p.height / p.displayHeight;
			jumpCooldown = 5;
		}
	}

	void draw() {
		if (!alive) {
			p.fill(255, 0, 0, 20);
			return;
		} else {
			p.fill(255, 255, 255, 50);
		}
		p.ellipse(p.width / 7, y, r, r);
	}

	public void setPApplet(PApplet _p) {
		p = _p;
	}

	public Bird copy() {
		return new Bird(brainConfig, parentNum, p);
	}

	public boolean check(Object o) {
		if (o instanceof Pipe) {
			return checkCollision((Pipe) o);
		}
		return false;
	}

	public boolean checkCollision(Pipe pipe) {
		if (pipe.x < p.width / 7 + r && pipe.x + pipe.width > p.width / 7 - r) {
			if (y - r < pipe.yTop || y + r > pipe.yTop + pipe.spacing) {
				alive = false;
				return true;
			}
		}
		return false;
	}

	public void calculateFitness(Object o, float[] fitnessInfo) {
		Pipe pipe = (Pipe) o;
		fitness = ((float) fitnessInfo[0]) + (1f / (Math.abs(y - (pipe.yTop + pipe.spacing / 2))));
	}

	public float getFitness() {
		return fitness;
	}

	public Bird cross(ArrayList<GeneticCreature> nextGen, float[] fitnessPercent) {
		Bird newBird = new Bird(brainConfig, parentNum, p);
		int[] thing = { 4, 4, 4, 1 };
		Bird b1 = null;
		Bird b2 = null;
		float ran = p.random(1);
		float ran2 = p.random(1);
		for (int j = nextGen.size() - 1; j > 0; j--) {
			if (ran > fitnessPercent[j]) {
				b1 = (Bird) nextGen.get(j);
			}
			if (ran2 > fitnessPercent[j]) {
				b2 = (Bird) nextGen.get(j);
			}
			if (b1 != null && b2 != null) {
				break;
			}
		}
		if (b1 == null) {
			b1 = (Bird) nextGen.get(0);
		}
		if (b2 == null) {
			b2 = (Bird) nextGen.get(0);
		}
		NeurNet avNetwork = averageNetworks(b1.brain, b2.brain, thing);
		NeurNet randomNet = new NeurNet(thing);
		NeurNet newNetwork = averageNetworks(avNetwork, randomNet, thing);
		for (int i = 0; i < 5; i++) {
			newNetwork = averageNetworks(avNetwork, newNetwork, thing);
		}
		newBird.parentNum = b1.parentNum;
		newBird.brain = newNetwork;
		return newBird;
	}

	private static NeurNet averageNetworks(NeurNet one, NeurNet two, int[] hidden) {
		NeurNet newNetwork = new NeurNet(hidden);
		newNetwork.weights_ih = averageMatrix(one.weights_ih, two.weights_ih);
		for (int i = 0; i < newNetwork.weights_hh.length; i++) {
			newNetwork.weights_hh[i] = averageMatrix(one.weights_hh[i], two.weights_hh[i]);
		}
		newNetwork.weights_ho = averageMatrix(one.weights_ho, two.weights_ho);
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

	@Override
	public void randomize() {
	}

	@Override
	public void setParent(int iding) {
	}

}
