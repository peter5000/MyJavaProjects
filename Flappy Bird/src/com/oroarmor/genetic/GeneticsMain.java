package com.oroarmor.genetic;

import processing.core.PApplet;
import com.oroarmor.bird.*;

public class GeneticsMain extends PApplet {
	GeneticAlgorithim<Bird> test;
	Pipe[] pipes;

	int nearestPipe;
	int passedPipes = 0;
	int genNum = 0;
	int maxPipes = 10;

	public static void main(String[] args) {
		PApplet.main("com.oroarmor.genetic.GeneticsMain");
	}

	public void settings() {
		size(displayWidth / 2, displayHeight / 2);
	}

	public void setup() {
		imageMode(PApplet.CENTER);
		int[] brainConfig = { 4, 4, 4, 1 };
		test = new GeneticAlgorithim<Bird>(50, new Bird(brainConfig, -1, this));
		pipes = new Pipe[10];
		for (int i = 0; i < pipes.length; i++) {
			pipes[i] = new Pipe(this, i * (width / 3));

		}
		nearestPipe = 1;

	}

	public void draw() {
		// frameRate(1);
		background(0);
		float[] standardInputs = { 0f, 0f, pipes[nearestPipe].spacing + pipes[nearestPipe].yTop,
				pipes[nearestPipe].yTop };
		float[] GeneticInfo = { passedPipes, genNum };
		imageMode(PApplet.CORNER);
		for (Pipe pipe : pipes) {
			pipe.draw();
			pipe.move();

			test.check(pipe, GeneticInfo);
		}
		imageMode(PApplet.CENTER);
		test.run(standardInputs);

		if (pipes[nearestPipe].x < 0) {
			nearestPipe++;
			passedPipes++;
		}
		if (nearestPipe == pipes.length) {
			nearestPipe = 0;
		}

		if (genNum != test.genNum) {
			for (int i = 0; i < pipes.length; i++) {
				pipes[i] = new Pipe(this, i * (width / 3));

			}
			nearestPipe = 1;
			passedPipes = 0;
			genNum++;
		}

		if (passedPipes == maxPipes) {
			test.evolve(pipes[nearestPipe], GeneticInfo);
			maxPipes += 1;
		}
		text("Gen: " + genNum + ", GenTime: " + passedPipes + ", Still Alive: " + test.currentGen.size(), 50, 50);

	}

	public void mouseClicked() {
		for (Bird bird : test.currentGen) {
			bird.jump();
		}
	}

}
