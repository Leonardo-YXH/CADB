package cn.npt.util.el;

import java.util.List;

import com.greenpineyu.fel.FelEngine;
import com.greenpineyu.fel.FelEngineImpl;
import com.greenpineyu.fel.function.CommonFunction;
import com.greenpineyu.fel.function.Function;
/**
 * 预先注册好一些常用的数学函数(目前可用的有sqrt,sin,cos,tan,atan,pow,abs,and,or,not,max,min,avg,sum),单例模式
 * @author leonardo_yang
 * @version v2014-12-01 1.0
 */
public class FelEngineHelp {

	/**
	 * el执行引擎
	 */
	private static FelEngine engine;
	private FelEngineHelp(){
		
	}
	/**
	 * 获取FelEngine的实例,其中这个实例是预先注册了一些常用的数学函数
	 * @return
	 */
	public static FelEngine getInstance(){
		if(engine==null){
			engine=new FelEngineImpl();
		}
		preAddFun();
		return engine;
	}
	/**
	 * 注册数学函数
	 */
	private static void preAddFun(){
		//注册sqrt函数
		Function fun = new CommonFunction() {
			@Override
			public String getName() {
				return "sqrt";
			}

			/* 
			 * 调用系统函数
			 */
			@Override
			public Object call(Object[] arguments) {
				return Math.sqrt(Double.parseDouble(arguments[0].toString()));
			}

		};
		engine.addFun(fun);
		
		//注册sin函数
		fun = new CommonFunction() {
			@Override
			public String getName() {
				return "sin";
			}

			/* 
			 * 调用系统函数
			 */
			@Override
			public Object call(Object[] arguments) {
				return Math.sin(Double.parseDouble(arguments[0].toString()));
			}

		};
		engine.addFun(fun);
		
		//注册cos函数
		fun = new CommonFunction() {
			@Override
			public String getName() {
				return "cos";
			}

			/* 
			 * 调用系统函数
			 */
			@Override
			public Object call(Object[] arguments) {
				return Math.cos(Double.parseDouble(arguments[0].toString()));
			}

		};
		engine.addFun(fun);
		//注册tan函数
		fun = new CommonFunction() {
			@Override
			public String getName() {
				return "tan";
			}
			
			/* 
			 * 调用系统函数
			 */
			@Override
			public Object call(Object[] arguments) {
				return Math.tan(Double.parseDouble(arguments[0].toString()));
			}
			
		};
		engine.addFun(fun);
		//注册atan函数
		fun = new CommonFunction() {
			@Override
			public String getName() {
				return "atan";
			}
			
			/* 
			 * 调用系统函数
			 */
			@Override
			public Object call(Object[] arguments) {
				return Math.atan(Double.parseDouble(arguments[0].toString()));
			}
			
		};
		engine.addFun(fun);
		//注册pow函数
		fun = new CommonFunction() {
			@Override
			public String getName() {
				return "pow";
			}
			
			/* 
			 * 调用系统函数
			 */
			@Override
			public Object call(Object[] arguments) {
				return Math.pow(Double.parseDouble(arguments[0].toString()),Double.parseDouble(arguments[1].toString()));
			}
			
		};
		engine.addFun(fun);
		//注册abs函数
		fun = new CommonFunction() {
			@Override
			public String getName() {
				return "abs";
			}
			
			/* 
			 * 调用系统函数
			 */
			@Override
			public Object call(Object[] arguments) {
				return Math.abs(Double.parseDouble(arguments[0].toString()));
			}
			
		};
		engine.addFun(fun);
		//注册not函数
		fun = new CommonFunction() {
			@Override
			public String getName() {
				return "not";
			}
			
			/* 
			 * 调用系统函数
			 */
			@Override
			public Object call(Object[] arguments) {
				double param=Double.parseDouble(arguments[0].toString());
				return param==0?true:false;
			}
			
		};
		engine.addFun(fun);
		//注册or函数
		fun = new CommonFunction() {
			@Override
			public String getName() {
				return "or";
			}
			
			/* 
			 * 调用系统函数
			 */
			@Override
			public Object call(Object[] arguments) {
				for(Object argv:arguments){
					if(Double.parseDouble(argv.toString())!=0){
						return true;
					}
				}
				return false;
			}
			
		};
		engine.addFun(fun);
		//注册and函数
		fun = new CommonFunction() {
			@Override
			public String getName() {
				return "and";
			}
			
			/* 
			 * 调用系统函数
			 */
			@Override
			public Object call(Object[] arguments) {
				for(Object argv:arguments){
					if(Double.parseDouble(argv.toString())==0){
						return false;
					}
				}
				return true;
			}
			
		};
		engine.addFun(fun);
		//注册max函数
		fun = new CommonFunction() {
			@Override
			public String getName() {
				return "max";
			}
			
			@SuppressWarnings("unchecked")
			@Override
			public Object call(Object[] arguments) {
				double max=Double.MIN_VALUE;
				if(arguments.length>0){
					if(arguments[0] instanceof List){
						List<Object> argvL=(List<Object>) arguments[0];
						if(argvL.size()>0){
							max=Double.parseDouble(argvL.get(0).toString());
							for(int j=1;j<argvL.size();j++){
								double a=Double.parseDouble(argvL.get(j).toString());
								if(max<a){
									max=a;
								}
							}
						}
					}
					else{
						max=Double.parseDouble(arguments[0].toString());
					}
					for (int i=1; i<arguments.length;i++) {
						if(arguments[i] instanceof List){
							List<Object> argvL=(List<Object>) arguments[i];
							for(int j=0;j<argvL.size();j++){
								double a=Double.parseDouble(argvL.get(j).toString());
								if(max<a){
									max=a;
								}
							}
							
						}
						else{
							double t=Double.parseDouble(arguments[i].toString());
							if(t>max){
								max=t;
							}
						}
					}
					
				}
				return max;
			}
			
		};
		engine.addFun(fun);
		//注册min函数
		fun = new CommonFunction() {
			@Override
			public String getName() {
				return "min";
			}
			
			/* 
			 * 调用系统函数
			 */
			@SuppressWarnings("unchecked")
			@Override
			public Object call(Object[] arguments) {
				double min=Double.MAX_VALUE;
				if(arguments.length>0){
					if(arguments[0] instanceof List){
						List<Object> argvL=(List<Object>) arguments[0];
						if(argvL.size()>0){
							min=Double.parseDouble(argvL.get(0).toString());
							for(int j=1;j<argvL.size();j++){
								double a=Double.parseDouble(argvL.get(j).toString());
								if(min>a){
									min=a;
								}
							}
						}
					}
					else{
						min=Double.parseDouble(arguments[0].toString());
					}
					for (int i=1; i<arguments.length;i++) {
						if(arguments[i] instanceof List){
							List<Object> argvL=(List<Object>) arguments[i];
							for(int j=0;j<argvL.size();j++){
								double a=Double.parseDouble(argvL.get(j).toString());
								if(min>a){
									min=a;
								}
							}
							
						}
						else{
							double t=Double.parseDouble(arguments[i].toString());
							if(t<min){
								min=t;
							}
						}
					}
					
				}
				return min;
			}
			
		};
		engine.addFun(fun);
		//注册sum函数
		fun = new CommonFunction() {
			@Override
			public String getName() {
				return "sum";
			}
			
			/* 
			 * 调用系统函数
			 */
			@SuppressWarnings("unchecked")
			@Override
			public Object call(Object[] arguments) {
				double sum=0;
				for(Object argv:arguments){
					if(argv instanceof List){
						List<Object> argvL=(List<Object>) argv;
						for (Object object : argvL) {
							double t=Double.parseDouble(object.toString());
							sum+=t;
						}
					}
					else{
						double t=Double.parseDouble(argv.toString());
						sum+=t;
					}
				}
				return sum;
			}
			
		};
		engine.addFun(fun);
		//注册avg函数
		fun = new CommonFunction() {
			@Override
			public String getName() {
				return "avg";
			}
			
			/* 
			 * 调用系统函数
			 */
			@SuppressWarnings("unchecked")
			@Override
			public Object call(Object[] arguments) {
				double sum=0;
				int size=0;
				for(Object argv:arguments){
					if(argv instanceof List){
						List<Object> argvL=(List<Object>) argv;
						for (Object object : argvL) {
							double t=Double.parseDouble(object.toString());
							sum+=t;
							size++;
						}
					}
					else{
						double t=Double.parseDouble(argv.toString());
						sum+=t;
						size++;
					}
				}
				if(size>0){
					return sum/size;
				}
				return sum;
			}
			
		};
		engine.addFun(fun);
	}
}
