package cn.npt.util.algorithm.polynomial;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SymbolMatcherKit {

	/**
	 * 把匹配的符号索引找出,并升序排列
	 * @param src
	 * @param leftFilter 
	 * @param rightFilter
	 * @return
	 */
	public static List<Integer> getFilterIndex(String src,char leftFilter,char rightFilter){
		List<Integer> result=new ArrayList<Integer>();
		Stack<Integer> stack=new Stack<Integer>();
		for(int i=0;i<src.length();i++){
			if(src.charAt(i)==leftFilter){
				stack.push(i);
			}
			else if(src.charAt(i)==rightFilter){
				if(!stack.empty()){
					result.add(stack.pop());
					result.add(i);
				}
			}
		}
		return result;
	}
	public static String filter(String src,char leftFilter,char rightFilter){
		List<Integer> filterIndex=getFilterIndex(src, leftFilter, rightFilter);
		//Collections.sort(filterIndex);
		StringBuilder sb=new StringBuilder();
		int start=0;
		for (Integer index : filterIndex) {
			sb.append(src.subSequence(start, index));
			start=index+1;
		}
		if(start<src.length()){
			sb.append(src.subSequence(start, src.length()));
		}
		return sb.toString();
	}
	
}
