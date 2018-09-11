package com.abilium.radar;

import java.util.Arrays;
import java.util.List;

import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.BasicMatrix.Factory;
import org.ojalgo.matrix.PrimitiveMatrix;

public class Radar {
    
	/**
	 * Demonstration method on how to use this library
	 * @param args
	 */
    public static void main(String[] args) {
    	
    	Factory<BasicMatrix> matrixFactory = PrimitiveMatrix.FACTORY;
    	
    	// Adjacency matrix for a graph having 4 nodes and 4 edges
    	double[][] dA = {
                {0.0, 1.0, 0.0, 1.0},
                {1.0, 0.0, 1.0, 0.0},
                {0.0, 1.0, 0.0, 1.0},
                {1.0, 0.0, 1.0, 0.0}
            };
        
    	// Attribute matrix holding values per node
        double[][] dX = {
                {4.0, 1.0},
                {5.0, 1.0},
                {3.0, 1.0},
                {5.0, 1.0}
            };
		
        // hyperparameters
        double alpha = 0.01;
        double beta = 0.01;
        double gamma = 0.1;
        
        BasicMatrix X = matrixFactory.rows(dX);
        BasicMatrix A = matrixFactory.rows(dA);
        
        // define number of maximum iterations
        int niters = 20;
    	
        // return top m instances
        int m = 2;
        
        // get the anomaly score list for the graph
		List<Node> score = RadarImpl.scoreFromRadar(X, A, alpha, beta, gamma, niters, m);
    	
		// print the score list
		System.out.println(Arrays.toString(score.toArray()));
  	}

}
