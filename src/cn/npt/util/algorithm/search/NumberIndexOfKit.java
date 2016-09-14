package cn.npt.util.algorithm.search;

import java.util.List;

/**
 * 查找元素在有序队列(升序)中的位置(左开右闭区间)
 * @author Leonardo
 *
 */
public class NumberIndexOfKit {

	/**
	 * 查找m在list中的位置。eg:3在[1,4,7]索引为1，在[1,3,5]索引为1.条件遵循左开右闭区间
	 * @param x
	 * @param t
	 * @return
	 */
	public static<T extends Number&Comparable<T>> int indexOf(List<T> x,T t){
		return binarySearch(x, 0, x.size()-1, t);
	}
	private static<T extends Number&Comparable<T>> int binarySearch(List<T> list,int start,int end,T m){
		if(start==end){
			if(start==0){
				return start;
			}
			else if(start==list.size()-1){
				return start+1;
			}
			return start;
		}
		int mid=(start+end)/2;
		int cflag1=list.get(mid).compareTo(m);
		int cflag2=list.get(mid+1).compareTo(m);
		
		if(cflag1==-1){
			if(cflag2>=0){
				return mid+1;
			}
			else{
				return binarySearch(list, mid+1, end, m);
			}
		}
		else if(cflag1==0){
			return mid;
		}
		else{
			return binarySearch(list, start, mid, m);
		}
		
	}
	/*public static void main(String[] args) {
		List<Integer> list=Arrays.asList(1,3,6,9,12);
		System.out.println(indexOf(list, 1));
		System.out.println(indexOf(list, 2));
		System.out.println(indexOf(list, 3));
		System.out.println(indexOf(list, 4));
		System.out.println(indexOf(list, 6));
		System.out.println(indexOf(list, 7));
		System.out.println(indexOf(list, 9));
		System.out.println(indexOf(list, 10));
		System.out.println(indexOf(list, 12));
		System.out.println(indexOf(list, 14));
	}*/
}
