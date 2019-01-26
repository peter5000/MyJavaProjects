package com.oroarmor.genetic;

import java.util.*;

public class GeneticAlgorithim<T extends GeneticCreature> {

	public ArrayList<T> currentGen;
	public ArrayList<T> nextGen;
	public int genNum;
	int numCreatures;

	public GeneticAlgorithim(int _numCreatures, T creatureType) {
		currentGen = new ArrayList<T>();
		nextGen = new ArrayList<T>();
		numCreatures = _numCreatures;
		for (int i = 0; i < _numCreatures; i++) {
			add(creatureType, i);
		}
		setup();
		genNum = 0;

	}

	private void setup() {
		for (T creature : currentGen) {
			creature.setup();
		}

	}

	@SuppressWarnings("unchecked")
	private void add(T t, int iding) {
		t.randomize();
		if (t.parentNum == -1) {
			t.setParent(iding);
		}
		T newCreature = (T) t.copy();
		newCreature.randomize();
		currentGen.add(newCreature);
	}

	public void run(float[] inputs) {

		for (T creature : currentGen) {
			creature.run(inputs.clone());
		}
		for (T creature : nextGen) {
			creature.run(inputs.clone());
		}
	}

	public void check(Object o, float[] addlFitnessComponents) {
		// TODO Auto-generated method stub
		for (T creature : currentGen) {
			if (creature.check(o)) {
				creature.calculateFitness(o, addlFitnessComponents);
				nextGen.add(creature);
			}
		}
		currentGen.removeAll(nextGen);
		if (currentGen.size() == 0) {
			evolve(o, addlFitnessComponents);
		}
	}

	@SuppressWarnings("unchecked")
	public void evolve(Object o, float[] addlFitnessComponents) {

		if (currentGen.size() != 0) {
			for (T creature : currentGen) {
				creature.calculateFitness(o, addlFitnessComponents);
			}
		}

		nextGen.sort(Comparator.comparing(GeneticCreature::getFitness));

		float totalFitness = 0;

		for (int i = 0; i < nextGen.size(); i++) {
			totalFitness += nextGen.get(i).getFitness();
		}

		float[] fitnessPercent = new float[nextGen.size()];

		for (int i = 0; i < nextGen.size(); i++) {
			fitnessPercent[i] = nextGen.get(i).getFitness() / totalFitness;
		}

		currentGen.clear();

		for (int i = 0; i < numCreatures; i++) {
			currentGen.add((T) nextGen.get(0).cross((ArrayList<GeneticCreature>) nextGen, fitnessPercent, genNum));
		}
		genNum++;
		nextGen.clear();
	}

}
