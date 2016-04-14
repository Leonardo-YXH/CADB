package cn.npt.util.algorithm.signal.filter;

import java.util.ArrayList;
import java.util.List;
/**
 * 滤波
 * @author Leonardo
 *
 */
public class SignalFilter {

	/**
	 * 滤波Y(n)=B(1)*X(n)+B(2)*X(n-1)+...+B(nb+1)*X(n-nb)
	 * 			-A(2)*Y(n-1)-...-A(na+1)*Y(n-na)
	 * <br>Y(i)=Σ
	 * <br>在matlab中下标从1开始
	 * <br>{Y(n)==0;X(n)==0 当n<0时},即前面的数用0填充
	 * @param X
	 * @param An 归一化后的A(0)==1
	 * @param Bn
	 * @return Y
	 */
	public static List<Double> filter(List<Double> X,List<Double> An,List<Double> Bn){
		List<Double> Y=new ArrayList<Double>();
		for(int i=0;i<X.size();i++){
			double y=0;
			for(int j=0;j<Bn.size();j++){
				int index=i-j;
				if(index>=0){
					y+=Bn.get(j)*X.get(index);
				}
			}
			for(int j=1;j<An.size();j++){
				int index=i-j;
				if(index>=0){
					y-=An.get(j)*Y.get(index);
				}
			}
			Y.add(y);
		}
		return Y;
		
	}
}
