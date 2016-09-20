package cn.npt.util.algorithm.signal.wavelet;

import java.util.ArrayList;

/**
 * 离散小波变换
 * @author leonardo
 *
 */
public class DiscreteWavelet {

	/**
	 * @param signal
	 *            a double[] with length = even power of two
	 * @param wavelet
	 *            Haar, Daubechies, etc. (see OrthogonalFilters.java)
	 * @param order
	 *            e.g Daubechies 8 has order = 8
	 * @param L
	 *            coarsest scale to include in the transform
	 * @param direction
	 *            forward = transform, reverse = inverse transform
	 * @return forward or reverse discrete wavelet transform
	 * @throws Exception
	 *             Invalid wavelet parameters
	 */
	public static double[] transform(double[] signal, Wavelet wavelet,
			int order, int L, Direction direction) throws Exception {
		if (direction == Direction.forward) {
			return forwardDwt(signal, wavelet, order, L);
		} else {
			return inverseDwt(signal, wavelet, order, L);
		}
	}

	public static enum Direction {
		forward, reverse
	}

	/**
	 * 
	 * @param signal
	 *            a double[] with length = even power of two
	 * @param wavelet
	 *            Haar, Daubechies, etc.
	 * @param order
	 *            e.g Daubechies 8 has order = 8
	 * @param L
	 *            coarsest scale
	 * @return Forward DWT
	 * @throws Exception
	 *             Invalid wavelet parameters
	 */
	public static double[] forwardDwt(double[] signal, Wavelet wavelet,
			int order, int L) throws Exception {
		int n = signal.length;
		if (!isValidChoices(wavelet, order, L, n)) {
			throw new Exception(
					"Invalid wavelet /order/scale/signal-length combination.");
		}
		double[] dWT = MatrixOps.deepCopy(signal);
		int log2n = (int) (Math.log(n) / Math.log(2));
		int iterations = log2n - L;
		int subLength = n;
		double[] H = OrthogonalFilters.getLowPass(wavelet, order);
		double[] G = OrthogonalFilters.getHighPass(H);
		for (int i = 0; i < iterations; i++) {
			subLength = n / (int) (Math.pow(2, i));
			double[][] QMF = makeQMFMatrix(subLength, H, G);
			double[] subResult = new double[subLength];
			subResult = subCopy(dWT, subResult, subLength);
			double[] temp = MatrixOps.multiply(QMF, subResult);
			dWT = subCopy(temp, dWT, subLength);
		}
		return dWT;
	}

	/**
	 * Parameters should be the same values used during the forward transform.
	 * 
	 * @param signal
	 *            a double[] with length = even power of two
	 * @param wavelet
	 *            Haar, Daubechies, etc.
	 * @param order
	 *            e.g Daubechies 8 has order = 8
	 * @param L
	 *            coarsest scale
	 * @return reconstructed signal by inverse DWT
	 * @throws Exception
	 */
	public static double[] inverseDwt(double[] signal, Wavelet wavelet,
			int order, int L) throws Exception {
		int n = signal.length;
		if (!isValidChoices(wavelet, order, L, n)) {
			throw new Exception(
					"Invalid wavelet /order/scale/signal-length combination.");
		}
		int log2n = (int) (Math.log(n) / Math.log(2));
		int subLength;
		double[] preserveCopy = new double[signal.length];
		preserveCopy = subCopy(signal, preserveCopy, signal.length);
		double[] H = OrthogonalFilters.getLowPass(wavelet, order);
		double[] G = OrthogonalFilters.getHighPass(H);
		for (int i = L + 1; i <= log2n; i++) {
			subLength = (int) (Math.pow(2, i));
			double[][] QMF = makeQMFMatrix(subLength, H, G);
			QMF = MatrixOps.transpose(QMF);
			double[] subResult = new double[subLength];
			subCopy(signal, subResult, subLength);
			subResult = MatrixOps.multiply(QMF, subResult);
			signal = subCopy(subResult, signal, subLength);
		}
		double[] iDWT = new double[n];
		iDWT = subCopy(signal, iDWT, n);
		signal = preserveCopy;
		return iDWT;
	}

	/**
	 * Make a quadrature mirror matrix
	 * 
	 * @param scale
	 * @param H
	 *            low pass filter
	 * @param G
	 *            high pass filter
	 * @return QMF[scale][scale]
	 */
	private static double[][] makeQMFMatrix(int scale, double[] H, double[] G) {
		int filterLen = H.length;
		int skip = 0;
		double[][] QMF = new double[scale][scale];
		for (int i = 0; i < (scale / 2); i++) {
			for (int j = 0; j < filterLen; j++) {
				int location = j + skip;
				if (location > scale - 1)// wrap
				{
					location = location - (scale);
				}
				QMF[i][location] = H[j];
			}
			skip += 2;
		}
		skip = scale - 1;
		for (int i = scale - 1; i >= (scale / 2); i--) {
			for (int j = filterLen - 1; j >= 0; j--) {
				int location = -j + skip;
				if (location < 0) {
					location += scale;
				}
				QMF[i][location] = G[filterLen - j - 1];
			}
			skip -= 2;
		}
		return QMF;
	}

	/**
	 * Multi-resolution analysis See: Mallet, A Wavelet Tour of Signal
	 * Processing, the Sparse Way, 2008, pp. 170-172
	 * 
	 * @param signal
	 *            a double[] with length = even power of two
	 * @param wavelet
	 *            Haar, Daubechies, etc.
	 * @param order
	 *            e.g Daubechies 8 has order = 8
	 * @param L
	 *            coarsest scale
	 * @return An ArrayList(Object) result such that: result.get(0) is an
	 *         ArrayList<double[]> holding multi-resolution scale data from the
	 *         finest scale to the coarsest scale terminating with the
	 *         approximation curve; and result.get(1) is an array of j values
	 *         corresponding to the scales used to create the data where: scale
	 *         = 2^-j and the terminating value is set to "0" for the
	 *         approximation scale.
	 * @throws Exception
	 *             Invalid wavelet parameters
	 */
	public static ArrayList<Object> mRA(double[] signal, Wavelet wavelet,
			int order, int L) throws Exception {
		ArrayList<Object> result = new ArrayList<Object>();
		int n = signal.length;
		if (!isValidChoices(wavelet, order, L, n)) {
			throw new Exception(
					"Invalid wavelet /order/scale/signal-length combination.");
		}
		int J = (int) (Math.log(n) / Math.log(2));
		double[] dwt = forwardDwt(signal, wavelet, order, L);
		ArrayList<double[]> mRA = new ArrayList<double[]>();
		for (int j = (J - 1); j >= L; j--) {
			double[] w = new double[n];
			int[] dyad = dyad(j);
			for (int k = dyad[0]; k <= dyad[dyad.length - 1]; k++) {
				w[k - 1] = dwt[k - 1];
			}
			mRA.add(inverseDwt(w, wavelet, order, L));
		}
		// All frequencies lower than those revealed at L
		double[] w = new double[n];
		int limit = (int) Math.pow(2, L);
		for (int i = 0; i < limit; i++) {
			w[i] = dwt[i];
		}
		mRA.add(inverseDwt(w, wavelet, order, L));

		int[] scalesUsed = new int[mRA.size()];
		int scaleCounter = 0;
		for (int j = (J - 1); j >= L; j--) {
			int[] dyad = dyad(j);
			scalesUsed[scaleCounter] = (int) (Math.log(dyad.length) / Math
					.log(2));
			scaleCounter++;
		}
		// Next line: 0 is a dummy value for the approximation carrying all
		// lower frequencies corresponding to scales larger than 2^-L
		scalesUsed[scaleCounter] = 0;
		result.add(mRA);
		result.add(scalesUsed);
		return result;
	}

	/**
	 * 
	 * @param k
	 * @return An int[2^k] holding values from 2^k +1 to 2^(k + 1) e.g. dyad(2)
	 *         = {5, 6, 7, 8,}, dyad(3) = {9, 10, 11, 12, 13, 14, 15, 16}
	 */
	public static int[] dyad(int k) {
		int[] dyad = null;
		if (k == 0) {
			dyad = new int[1];
			dyad[0] = 2;
			return dyad;
		}
		int lower = (int) Math.pow(2, k);
		int upper = (int) Math.pow(2, k + 1);
		int dyadLength = upper - lower;
		dyad = new int[dyadLength];
		for (int i = lower; i < upper; i++) {
			dyad[i - lower] = i + 1;
		}
		return dyad;
	}

	/**
	 * Example: If source = {5, 6, 7}, destination = {1, 2, 3, 4}, and count = 2
	 * Then result = {5, 6, 3, 4}
	 * 
	 * @return the first count elements in source overwrite the first count
	 *         elements in the result.
	 */
	private static double[] subCopy(double[] source, double[] destination,
			int count) {
		for (int i = 0; i < count; i++) {
			destination[i] = source[i];
		}
		return destination;
	}

	/**
	 * 
	 * @param x
	 *            the sequence to pad
	 * @return If necessary, expanded sequence such that its length is an even
	 *         power of 2 by adding additional zero values.
	 */
	public static double[] padPow2(double[] x) {
		int sizeIn = x.length;
		double log2N = Math.log(sizeIn) / Math.log(2);
		double ceiling = Math.ceil(log2N);
		if (log2N < ceiling) {
			log2N = ceiling;
			int sizePad = (int) Math.pow(2, log2N);
			double[] padX = new double[sizePad];
			for (int i = 0; i < sizePad; i++) {
				if (i < sizeIn) {
					padX[i] = x[i];
				} else {
					padX[i] = 0;
				}
			}
			return padX;
		} else {
			return x;
		}
	}

	/**
	 * 
	 * @param xy
	 *            A double[][] where xy[0] = x and xy[1] = f(x)
	 * @return If necessary, expanded sequence such that its length is an even
	 *         power of 2 by adding additional zero values.
	 */
	public static double[][] padPow2(double[][] xy) {
		int sizeIn = xy[0].length;
		double log2N = Math.log(sizeIn) / Math.log(2);
		double ceiling = Math.ceil(log2N);
		if (log2N < ceiling) {
			log2N = ceiling;
			int sizePad = (int) Math.pow(2, log2N);
			double[][] padXY = new double[2][sizePad];
			double dx = padXY[0][1] - padXY[0][0];
			for (int i = 0; i < sizePad; i++) {
				if (i < sizeIn) {
					padXY[0][i] = xy[0][i];
					padXY[1][i] = xy[1][i];
				} else {
					padXY[0][i] = padXY[0][i - 1] + dx;
					padXY[1][i] = 0;
				}
			}
			return padXY;
		} else {
			return xy;
		}
	}

	/**
	 * Wavelet/param/scale/size sanity check
	 * 
	 * @param wavelet
	 * @param order
	 * @param L
	 * @param signalLength
	 * @return true only if the wavelet/order exists in OrthogonalFileters.java
	 *         and the signal is long enough to be analyzed at the chosen
	 *         coarsest scale L.
	 */
	private static boolean isValidChoices(Wavelet wavelet, int order, int L,
			int signalLength) {
		try {
			ArrayList<Integer> validParams = OrthogonalFilters
					.validParameters(wavelet);
			boolean isValid = false;
			for (int i = 0; i < validParams.size(); i++) {
				if (validParams.get(i) == Integer.valueOf(order)) {
					ArrayList<Integer> validScales;
					validScales = OrthogonalFilters.validScales(order, wavelet,
							signalLength);

					for (int j = 0; j < validScales.size(); j++) {
						if (validScales.get(j) == Integer.valueOf(L)) {
							isValid = true;
							break;
						}
					}
					if (isValid) {
						break;
					}
				}
			}
			return isValid;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	/**
	 * This method may be used to test the reliability of perfect reconstruction
	 * using each of the orthogonal filters provided in OrthogonalFilters.java
	 * for all valid orders provided.
	 * 
	 * @throws Exception
	 *             Invalid wavelet parameters (be sure your test signal is long
	 *             enough)
	 */
//	public static void relativeErrors() throws Exception {
//		File file = FileOps.openDialog(System.getProperty("user.dir"));
//		double[][] testFile = MatrixOps.transpose((double[][]) FileOps
//				.openMatrix(file));
//		double[] testFn = testFile[1];
//		int fnLen = testFn.length;
//		double[] idwt;
//		double norm = MatrixOps.vector2Norm(testFn);
//		for (Wavelet wavelet : Wavelet.values()) {
//			ArrayList<Integer> params = OrthogonalFilters
//					.validParameters(wavelet);
//			for (Integer param : params) {
//				Integer coarsest = OrthogonalFilters.validScales(param,
//						wavelet, fnLen).get(0);
//				double[] dwt = transform(testFn, wavelet, param, coarsest,
//						Direction.forward);
//				idwt = transform(dwt, wavelet, param, coarsest,
//						Direction.reverse);
//				double resid = MatrixOps.vector2Norm(MatrixOps.add(testFn,
//						MatrixOps.scale(-1.0, idwt)));
//				double relResid = resid / norm;
//				StringBuilder sb = new StringBuilder("Testing ");
//				sb.append(wavelet + " " + param + " Relative residual: \n");
//				sb.append(relResid + "\n\n");
//				System.out.print(sb.toString());
//				// The relative residuals should be near zero
//			}
//		}
//	}
	
	public static void main(String[] args) {
		double[] X=new double[]{2,5,8,9,7,4,-1,-1};
		//double[] X=new double[]{9,7,3,5};
		try {
			double[] Y=transform(X, Wavelet.Haar, 2, 1, Direction.forward);
			for(double y:Y){
				y/=1.414213562373094;
				System.out.println(y);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
