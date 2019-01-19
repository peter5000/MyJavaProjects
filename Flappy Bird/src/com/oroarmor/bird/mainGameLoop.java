package com.oroarmor.bird;

import processing.core.*;

import java.text.DecimalFormat;
import java.util.*;

public class mainGameLoop extends PApplet {

	ArrayList<Bird> birds;
	Pipe[] pipes;
	ArrayList<Bird> nextBirds;
	int nearestPipe;
	int genTime = 0;

	int numBirds = 50;

	Bird bestBird;
	int numGen;
	float bestFitness = 0;
	int maxTime = 6000;

	public static void main(String[] args) {
		PApplet.main("com.oroarmor.bird.mainGameLoop");
	}

	public void settings() {
		size((int) (displayWidth / 1.5), (int) (displayHeight / 1.5));
	}

	public void setup() {
		birds = new ArrayList<Bird>();
		for (int i = 0; i < numBirds; i++) {
			birds.add(new Bird(this));
		}
		nextBirds = new ArrayList<Bird>();
		pipes = new Pipe[10];
		for (int i = 0; i < pipes.length; i++) {
			pipes[i] = new Pipe(this, i * (width / 3));

		}
		nearestPipe = 0;
	}

	public void draw() {

		drawScene();

		if (nextBirds.size() == numBirds || genTime == maxTime) {
			try {
				for (Bird bird : birds) {
					bird.calcFitness(pipes[nearestPipe], genTime);
					nextBirds.add(bird);
				}
			} catch (Exception e) {
				System.err.println(e);
			}

			try {
				nextGen();
			} catch (Exception e) {
				System.err.println(e);
			}
		}
		genTime++;
	}

	public void drawScene() {
		background(0);
		for (int i = 0; i < pipes.length; i++) {
			pipes[i].draw();
			pipes[i].move();
			for (int j = birds.size() - 1; j > -1; j--) {
				birds.get(j).checkCollision(pipes[i]);
				if (!birds.get(j).alive) {
					birds.get(j).calcFitness(pipes[i], genTime);
					nextBirds.add(birds.remove(j));
				}
			}
		}
		for (int i = 0; i < birds.size(); i++) {
			birds.get(i).update();
			birds.get(i).draw();
			try {
				birds.get(i).network(pipes[nearestPipe]);
			} catch (RuntimeException e) {
				System.err.println(e);
			}
		}
		// if(numGen > 0) {
		try {
			bestBird.update();
			bestBird.drawBest();
			bestBird.network(pipes[nearestPipe]);
			bestBird.checkCollision(pipes[nearestPipe]);
		} catch (Exception e) {

		}
		// }
		fill(255, 0, 0);
		textAlign(TOP, LEFT);
		textSize(20);
		DecimalFormat df = new DecimalFormat("#.###");
		text("Gen: " + numGen + ", GenTime: " + genTime + ", Best Fitness: " + bestFitness + ", Still Alive: "
				+ birds.size() + ", Est Fitness: " + df.format((float) genTime / 30), 50, 50);
		if (pipes[nearestPipe].x < 0) {
			nearestPipe++;
			if (nearestPipe > 9) {
				nearestPipe = 0;
			}
		}
	}

	public void nextGen() {
		birds.clear();

		nextBirds.sort(Comparator.comparing(Bird::getFitness));

		float totalFitness = 0;

		for (Bird bird : nextBirds) {
			totalFitness += bird.getFitness();
		}

		float[] percentOfTotal = new float[numBirds];
		int k = 0;
		for (Bird bird : nextBirds) {
			percentOfTotal[k] = bird.getFitness() / totalFitness;
			k++;
		}

		for (int i = 0; i < numBirds; i++) {
			float ran = random(1);
			float ran2 = random(1);

			Bird b1 = null;
			Bird b2 = null;
			for (int j = numBirds - 1; j > 0; j--) {
				if (ran > percentOfTotal[j]) {
					b1 = nextBirds.get(j);
				}
				if (ran2 > percentOfTotal[j]) {
					b2 = nextBirds.get(j);
				}

				if (b1 != null && b2 != null) {
					break;
				}
			}
			if (b1 == null) {
				b1 = nextBirds.get(0);
			}
			if (b2 == null) {
				b2 = nextBirds.get(0);
			}

			birds.add(Bird.cross(b1, b2));

		}

		if (nextBirds.get(numBirds - 1).fitness > bestFitness) {
			bestBird = nextBirds.get(numBirds - 1);
			bestFitness = bestBird.fitness;
		}

		if (genTime == maxTime) {
			bestBird = nextBirds.get(numBirds - 1);
			bestFitness = bestBird.fitness;
			maxTime+=300;
		}

		bestBird.y = height / 2;
		bestBird.yVel = 0;
		bestBird.alive = true;
		nextBirds.clear();

		for (int i = 0; i < pipes.length; i++) {
			pipes[i] = new Pipe(this, i * (width / 3));

		}
		nearestPipe = 0;
		genTime = 0;
		numGen++;
	}

	public void keyPressed() {
		for (Bird bird : birds) {
			bird.jump();
		}
	}
}
