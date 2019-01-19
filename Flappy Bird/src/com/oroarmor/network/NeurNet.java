package com.oroarmor.network;

import processing.core.*;

public class NeurNet {
	int input_nodes;
	int[] hidden_nodes;
	int output_nodes;

	public Matrix weights_ih;
	public Matrix[] weights_hh;
	public Matrix weights_ho;

	public Matrix[] bias_h;
	public Matrix bias_o;
	int times_trained;
	float learning_rate;

	public NeurNet(int[] _nodes) {
		int nodeSize = _nodes.length;

		input_nodes = _nodes[0];

		hidden_nodes = new int[nodeSize - 2];

		for (int i = 1; i < nodeSize - 1; i++) {
			hidden_nodes[i - 1] = _nodes[i];
		}

		output_nodes = _nodes[nodeSize - 1];

		weights_ih = new Matrix(hidden_nodes[0], input_nodes);
		weights_hh = new Matrix[hidden_nodes.length - 1];

		for (int i = 1; i < hidden_nodes.length; i++) {
			weights_hh[i - 1] = new Matrix(hidden_nodes[i], hidden_nodes[i - 1]);
			weights_hh[i - 1].randomize();
		}

		weights_ho = new Matrix(output_nodes, hidden_nodes[hidden_nodes.length - 1]);
		weights_ih.randomize();

		weights_ho.randomize();

		bias_h = new Matrix[hidden_nodes.length];

		for (int i = 0; i < hidden_nodes.length; i++) {
			bias_h[i] = new Matrix(hidden_nodes[i], 1);
			bias_h[i].randomize();
		}

		bias_o = new Matrix(output_nodes, 1);

		bias_o.randomize();
		learning_rate = 0.05f;
	}

	public float[] feedforward(float[] input_array) {
		Matrix inputs = Matrix.fromArray(input_array);
		Matrix[] hidden = new Matrix[hidden_nodes.length];

		hidden[0] = Matrix.mult(weights_ih, inputs);
		hidden[0].add(bias_h[0]);
		hidden[0] = Matrix.sigmoid(hidden[0]);

		for (int i = 1; i < hidden_nodes.length; i++) {
			hidden[i] = Matrix.mult(weights_hh[i - 1], hidden[i - 1]);
			hidden[i].add(bias_h[i]);
			hidden[i] = Matrix.sigmoid(hidden[i]);
		}

		Matrix output = Matrix.mult(weights_ho, hidden[hidden.length - 1]);
		output.add(bias_o);
		output = Matrix.sigmoid(output);

		return output.toArray();
	}

	void train(float[] input_array, float[] target_array) {

		Matrix inputs = Matrix.fromArray(input_array);

		Matrix[] hidden = new Matrix[hidden_nodes.length];

		hidden[0] = Matrix.mult(weights_ih, inputs);
		hidden[0].add(bias_h[0]);
		hidden[0] = Matrix.sigmoid(hidden[0]);

		for (int i = 1; i < hidden_nodes.length; i++) {
			hidden[i] = Matrix.mult(weights_hh[i - 1], hidden[i - 1]);

			hidden[i].add(bias_h[i]);

			hidden[i] = Matrix.sigmoid(hidden[i]);
		}

		Matrix outputs = Matrix.mult(weights_ho, hidden[hidden_nodes.length - 1]);
		outputs.add(bias_o);
		outputs = Matrix.sigmoid(outputs);

		Matrix target_values = Matrix.fromArray(target_array);

		Matrix output_errors = Matrix.subtract(target_values, outputs);

		Matrix gradients = Matrix.dsigmoid(outputs);
		gradients.mult(output_errors);
		gradients.mult(learning_rate);

		Matrix hidden_T = Matrix.transpose(hidden[0]);
		Matrix weight_ho_deltas = Matrix.mult(gradients, hidden_T);

		weights_ho.add(weight_ho_deltas);

		bias_o.add(gradients);

		Matrix who_t = Matrix.transpose(weights_ho);
		Matrix hidden_errors = Matrix.mult(who_t, output_errors);

		for (int i = hidden_nodes.length - 2; i > -1; i--) {

			Matrix hidden_gradient = Matrix.dsigmoid(hidden[i]);
			hidden_gradient.mult(hidden_errors);
			hidden_gradient.mult(learning_rate);

			Matrix weight_hh_deltas = Matrix.mult(hidden_gradient, Matrix.transpose(hidden[i]));

			weights_hh[i].add(weight_hh_deltas);

			bias_h[i].add(hidden_gradient);

		}

		Matrix inputs_T = Matrix.transpose(inputs);

		Matrix hidden_gradient = Matrix.dsigmoid(hidden[0]);
		hidden_gradient.mult(hidden_errors);
		hidden_gradient.mult(learning_rate);

		Matrix weight_ih_deltas = Matrix.mult(hidden_gradient, inputs_T);

		weights_ih.add(weight_ih_deltas);
		bias_h[0].add(hidden_gradient);

		times_trained += 1;

		// learning_rate*=0.9999999999;
		// learning_rate = (float) ((float) 1 / Math.log(times_trained) * 10);
	}

	public void draw(PApplet p, float[] input) {
		// TODO Auto-generated method stub
		Matrix inputs = Matrix.fromArray(input);
		Matrix[] hidden = new Matrix[hidden_nodes.length];

		hidden[0] = Matrix.mult(weights_ih, inputs);
		hidden[0].add(bias_h[0]);
		hidden[0] = Matrix.sigmoid(hidden[0]);

		for (int i = 1; i < hidden_nodes.length; i++) {
			hidden[i] = Matrix.mult(weights_hh[i - 1], hidden[i - 1]);
			hidden[i].add(bias_h[i]);
			hidden[i] = Matrix.sigmoid(hidden[i]);
		}

		Matrix output = Matrix.mult(weights_ho, hidden[hidden.length - 1]);
		output.add(bias_o);
		output = Matrix.sigmoid(output);

		// output.percent();

		int num_rows = 2 + hidden_nodes.length + 1;
		p.stroke(0);

		for (int i = 1; i < num_rows - 1; i++) {

			if (i == 1) {
				for (int j = 0; j < weights_ih.rows; j++) {
					for (int k = 0; k < weights_ih.cols; k++) {
						if (weights_ih.matrix[j][k] > 0) {
							p.stroke(255);
						} else {
							p.stroke(0);
						}
						int w = (int) Math.abs(weights_ih.matrix[j][k]) + 2;

						if (w > 10) {
							w = 10;
						}

						p.strokeWeight(w);
						p.line((p.width / num_rows) * (i), (p.height / (input_nodes + 1)) * (k + 1),
								(p.width / num_rows) * (i + 1), (p.height / (hidden_nodes[i] + 1)) * (j + 1));
					}

				}
			} else if (i == num_rows - 2) {
				for (int j = 0; j < weights_ho.rows; j++) {
					for (int k = 0; k < weights_ho.cols; k++) {
						if (weights_ho.matrix[j][k] > 0) {
							p.stroke(255);
						} else {
							p.stroke(0);
						}
						int w = (int) Math.abs(weights_ho.matrix[j][k]) + 2;

						if (w > 10) {
							w = 10;
						}

						p.strokeWeight(w);

						p.line((p.width / num_rows) * (i),
								(p.height / (hidden_nodes[hidden_nodes.length - 1] + 1)) * (k + 1),
								(p.width / num_rows) * (i + 1), (p.height / (output_nodes + 1)) * (j + 1));
					}

				}
			} else {
				for (int j = 0; j < weights_hh[i - 2].rows; j++) {
					for (int k = 0; k < weights_hh[i - 2].cols; k++) {
						if (weights_hh[i - 2].matrix[j][k] > 0) {
							p.stroke(255);
						} else {
							p.stroke(0);
						}

						int w = (int) Math.abs(weights_hh[i - 2].matrix[j][k]) + 2;

						if (w > 10) {
							w = 10;
						}

						p.strokeWeight(w);

						p.line((p.width / num_rows) * (i), (p.height / (hidden_nodes[i - 2] + 1)) * (k + 1),
								(p.width / num_rows) * (i + 1), (p.height / (hidden_nodes[i - 2] + 1)) * (j + 1));
					}

				}
			}

			// p.line((p.width/num_rows)*(i),0,(p.width/num_rows)*(i),p.height);
		}
		p.strokeWeight(1);
		p.stroke(0);
		for (int i = 1; i < num_rows; i++) {

			if (i == 1) {
				for (int j = 0; j < input_nodes; j++) {
					p.fill(input[j] * 255);
					p.ellipse((p.width / num_rows) * (i), (p.height / (input_nodes + 1)) * (j + 1), 20, 20);
				}
			} else if (i == num_rows - 1) {
				for (int j = 0; j < output_nodes; j++) {
					p.fill(output.matrix[j][0] * 255);
					p.ellipse((p.width / num_rows) * (i), (p.height / (output_nodes + 1)) * (j + 1), 20, 20);
				}
			} else {
				for (int j = 0; j < hidden_nodes[i - 2]; j++) {
					p.fill(hidden[i - 2].matrix[j][0] * 255);
					p.ellipse((p.width / num_rows) * (i), (p.height / (hidden_nodes[i - 2] + 1)) * (j + 1), 20, 20);
				}
			}

			// p.line((p.width/num_rows)*(i),0,(p.width/num_rows)*(i),p.height);
		}

	}

	public void randomize() {
		weights_ih.randomize();
		for (Matrix weight_hh : weights_hh) {
			weight_hh.randomize();
		}
		weights_ho.randomize();
	}

}
