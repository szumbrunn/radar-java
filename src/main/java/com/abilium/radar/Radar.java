package com.abilium.radar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.BasicMatrix.Factory;
import org.ojalgo.matrix.PrimitiveMatrix;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.netio.BasicLogger;

public class Radar {
    public boolean someLibraryMethod() {
        return true;
    }
    
    
    
    public static void main(String[] args) {
    	
    	Factory<BasicMatrix> matrixFactory = PrimitiveMatrix.FACTORY;
    	
    	double[][] dA = {
                {0.0, 1.0, 0.0, 1.0},
                {1.0, 0.0, 1.0, 0.0},
                {0.0, 1.0, 0.0, 1.0},
                {1.0, 1.0, 1.0, 1.0}
            };
        
        double[][] dX = {
                {4.0, 1.0},
                {5.0, 1.0},
                {3.0, 1.0},
                {5.0, 1.0}
            };
		
        double alpha = 0.01;
        double beta = 0.01;
        double gamma = 0.1;
        
        BasicMatrix X = matrixFactory.rows(dX);
        BasicMatrix A = matrixFactory.rows(dA);
        
        int niters = 20;
    	
    	
		List<Double> score = RadarImpl.scoreFromRadar(X, A, alpha, beta, gamma, niters);
    	
		System.out.println(Arrays.toString(score.toArray()));
    	//BasicMatrix bm = RadarImpl.makeDiagonal(matrixFactory.makeRowVector(list));
  	}

}
