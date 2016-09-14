package cn.npt.util.algorithm.signal.zoomdlg;

import java.util.ArrayList;
import java.util.List;

/**
 * 细化分析
 * @author leonardo
 *
 */
public class ZoomDlg {

	/**
	 * 
	 * @param sampleFreq 采样频率
	 * @param centerFreq 中心频率
	 * @param zoom 细化倍数
	 * @return 上截止频率，下截止频率
	 */
	public static List<Double> zoomdlg(double sampleFreq,double centerFreq,double zoom){
		List<Double> rs=new ArrayList<Double>();
		double zoom1=zoom*4;
		zoom1=sampleFreq/zoom1;
		double upFreq2=sampleFreq/2;
		double upFreq1=centerFreq+zoom1;
		if(upFreq1>upFreq2){
			rs.add(upFreq2);
		}
		else{
			rs.add(upFreq1);
		}
		
		double downFreq2=centerFreq-zoom1;
		double downFreq1=-upFreq2;
		if(downFreq1>downFreq2){
			rs.add(downFreq1);
		}
		else{
			rs.add(downFreq2);
		}
		return rs;
	}
}
