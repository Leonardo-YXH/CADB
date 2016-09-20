package cn.npt.util.algorithm.signal.wavelet;
public enum Wavelet {
	/**
	 * db6缩放函数和小波函数是Daubechies开发的几种小波之一
	 * <br>order=[4:2:20]
	 */
	Daubechies,
	/**
	 * 哈尔小波
	 * <br>order=2
	 */
	Haar, 
	/**
	 * order=18
	 */
	Beylkin, 
	/**
	 * order=24
	 */
	Vaidyanathan, 
	/**
	 * coif2缩放函数和coif2小波函数是Daubechies应R. Coifman的请求而开发的几种小波之一
	 * <br>order=[6:6:30]
	 */
	Coiflet, 
	/**
	 * Sym6缩放函数和小波函数是symlets的简写，是Daubechies提议开发的几种对称小波之一
	 * <br>order=[4:1:10]
	 */
	Symmlet, 
	/**
	 * order=[1,3]
	 */
	Battle
}
