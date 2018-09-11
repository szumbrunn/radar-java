package com.abilium.radar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ojalgo.function.implementation.PrimitiveFunction;
import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.PrimitiveMatrix;
import org.ojalgo.matrix.BasicMatrix.Factory;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.scalar.Scalar;

/**
 * Implementation according to the paper of J. Li, et al (2017) 
 * Radar: Residual Analysis for Anomaly Detection in Attributed Networks 
 * 
 * Calculates anomaly rates for given graph
 * @author S. Zumbrunn
 *
 */
public class RadarImpl {

	private static Factory<BasicMatrix> matrixFactory = PrimitiveMatrix.FACTORY;
	private static PhysicalStore.Factory<Double, PrimitiveDenseStore> storeFactory =
            PrimitiveDenseStore.FACTORY;
	
	/**
	 * Wrapper method to calculate ordered score list with
	 * @param X the attribute matrix of size nxm
	 * @param A the adjacency matrix of size nxn
	 * @param alpha (hyperparameter)
	 * @param beta (hyperparameter)
	 * @param gamma (hyperparameter)
	 * @param niters maximum number of iterations
	 * @return ordered score list
	 */
	public static List<Node> scoreFromRadar(BasicMatrix X, BasicMatrix A, double alpha, double beta, double gamma, int niters, int m) {
		int n = X.getColDim();
		BasicMatrix colVector = matrixFactory.makeZero(n, 1).add(1); // make 1 row vector
		
    	BasicMatrix R = RadarImpl.radar(X, A, alpha, beta, gamma, niters);

		List<Double> scoreList = R.multiplyElements(R)
								.multiplyRight(colVector)
								.toPrimitiveStore().asList();
		List<Node> score = new ArrayList<>();
		for(int i=0;i<scoreList.size();i++) {
 			score.add(new Node(i,scoreList.get(i)));
		}
		Collections.sort(score);
		if(m>score.size()) {
			m = score.size();
		}
		return score.subList(0, m);
	}
	
	/**
	 * Computes the anomaly score for a given graph with 
	 * @param X the attribute matrix of size nxm
	 * @param A the adjacency matrix of size nxn
	 * @param alpha (hyperparameter)
	 * @param beta (hyperparameter)
	 * @param gamma (hyperparameter)
	 * @param niters maximum number of iterations
	 * @return anomaly score as matrix
	 */
	public static BasicMatrix radar(BasicMatrix X, BasicMatrix A, double alpha, double beta, double gamma, int niters) {
		
		int n = X.getRowDim();
		int m = X.getColDim();
		BasicMatrix vector = matrixFactory.makeZero(n, 1).add(1); // make 1 row vector
		BasicMatrix vectorCol = matrixFactory.makeZero(m, 1).add(1); // make 1 row vector
		
		BasicMatrix D = makeDiagonal(A.multiplyRight(vector));
		
		BasicMatrix L = D.subtract(A);
		
		BasicMatrix Dr = matrixFactory.makeEye(n, n);
		BasicMatrix Dw = matrixFactory.makeEye(n, n);
		
		BasicMatrix R = matrixFactory.makeEye(n, n)
									.add(Dr.multiply(beta))
									.add(L.multiply(gamma))
									.invert()
									.multiplyRight(X);
		
		List<Double> obj = new ArrayList<>();
		for(int i=0; i<niters; i++) {
			// update w
			BasicMatrix W = X.multiplyRight(X.transpose())
							.add(Dw.multiply(alpha))
									.invert()
									.multiplyRight(X.multiplyRight(X.transpose())
													.subtract(X.multiplyRight(R.transpose())));
			PhysicalStore<Double> Wtmp = W.multiplyElements(W)
										.multiplyRight(vector)
										.toPrimitiveStore();
			Wtmp.modifyAll(PrimitiveFunction.SQRT);
			PhysicalStore<Double> WtmpCopy = Wtmp.copy();
			WtmpCopy.modifyAll(PrimitiveFunction.INVERT);
			Dw = makeDiagonal(PrimitiveMatrix.FACTORY.instantiate(WtmpCopy).multiply(0.5));

			// update r
			R = matrixFactory.makeEye(n, n)
					.add(Dr.multiply(beta))
					.add(L.multiply(gamma))
					.invert()
					.multiplyRight(X.subtract(W.transpose()
												.multiplyRight(X)));
			
			PhysicalStore<Double> Rtmp = R.multiplyElements(R)
										.multiplyRight(vectorCol)
										.toPrimitiveStore();
			Rtmp.modifyAll(PrimitiveFunction.SQRT);
			PhysicalStore<Double> RtmpCopy = Rtmp.copy();
			RtmpCopy.modifyAll(PrimitiveFunction.INVERT);
			Dr = makeDiagonal(PrimitiveMatrix.FACTORY.instantiate(RtmpCopy).multiply(0.5));
			obj.add(X.subtract(W.transpose().multiplyRight(X)).subtract(R).getFrobeniusNorm().power(2)
					.add(sum(Wtmp).multiply(alpha).getReal())
					.add(sum(Rtmp).multiply(beta).getReal())
					.add(R.transpose().multiplyRight(L).multiplyRight(R).getTrace().multiply(gamma).getReal()).getReal());
			if(i >= 2 && (Math.abs(obj.get(i)-obj.get(i-1)))<0.001) {
				break;
			}
		}
		
		return R;
	}
	
	/**
	 * Compute the sum over a vector
	 * @param matrix
	 * @return sum
	 */
	@SuppressWarnings("unchecked")
	private static <N> Scalar<? super N> sum(PhysicalStore<? super N> vector) {	
		Scalar<? super N> scalar = (Scalar<? super N>) storeFactory.getStaticOne();
		List<N> list = (List<N>) vector.asList();
		for(int i=0;i<list.size();i++) {
			scalar.add(list.get(i));
		}
		return scalar;
	}
	
	/**
	 * Convert row-vector to diagonal matrix
	 * @param rowVector
	 * @return diagonal matrix
	 */
	private static BasicMatrix makeDiagonal(BasicMatrix rowVector) {
		PhysicalStore<Double> matrix = storeFactory.makeZero(rowVector.getRowDim(), rowVector.getRowDim());
		PhysicalStore<Double> store = rowVector.toPrimitiveStore();
		List<Double> list = store.asList();
		for(int i=0;i<list.size(); i++) {
			for(int j=0;j<list.size();j++) {
				if(i==j)	
					matrix.set(i, j, list.get(i));
			}
		}
		return PrimitiveMatrix.FACTORY.instantiate(matrix);
	}
	
	/**
	 * Print matrix dimensions for debugging purposes
	 * @param bm
	 */
	public static void printSize(BasicMatrix bm) {
		System.out.println(bm.getRowDim() + "x" + bm.getColDim());
	}
	
    /**
     * Print matrix for debugging purposes
     * @param bm
     */
    public static void printMatrix(BasicMatrix bm) {
    	PhysicalStore<Double> store = bm.toPrimitiveStore();
    	for(int i=0;i<store.getRowDim();i++) {
    		for(int j=0;j<store.getColDim();j++) {
    			System.out.print(store.get(i, j)+" ");
    		}
    		System.out.println("");
    	}
    }

}
