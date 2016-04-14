package cn.npt.util.algorithm.polynomial;

import java.util.Stack;

public class StringExpression {

	public static String calculateExpression(String expression) {// 中缀表达式转换后缀表达式（逆波兰式）
		Stack<Object> s3 = new Stack<Object>();// 存放结果栈
		Stack<Character> s4 = new Stack<Character>();// 存放操作符栈
		int len = expression.length();//

		char c1;
		double number;
		int m, n = -1;
		for (int i = 0; i < len; i++) {
			c1 = expression.charAt(i);
			if (isOprator(c1) || (i == len - 1)){// 如果是运算符，将前面的数数字入s3栈，操作符入s4栈
				if (i == len - 1 && (!isOprator(c1)))// 当最后一位且不是操作符时，将前面的数压栈
					m = i + 1;
				else
					m = i;
				// 操作数入栈,向前遍历到下一个运算符，将中间的字符串转化为double
				for (int j = i - 1; j >= 0; j--) {
					if (isOprator(expression.charAt(j))) {
						n = j;
						break;
					}
					n = j - 1;
				}
				if (m != n + 1)// 只有当这两个值不等时中间才会有操作数
				{
					number = Double.parseDouble(expression.substring(n + 1, m));
					s3.push(number);
				}
				// 运算符入栈
				if (i == 0 && (c1 != '('))// 当表达式第一个字符就为运算符且不是左括号时，返回表达式错误
				{
					return "表达式错误！";
				} else if (isOprator(c1))// 且是操作符时
				{
					while (true) {
						if (s4.isEmpty() || s4.peek() == '(' || c1 == '(')// 如果栈为空或者栈顶元素为（或者c1为（时，则直接将运算符压入栈内
						{
							s4.push(c1);
							break;
						} else if (c1 == ')')// 当c1为）时，依次弹出s4中的运算符并压入s3，直到（，舍弃这一对括号
						{
							while (s4.peek() != '(') {
								s3.push(s4.pop());
								if (s4.isEmpty())// 弹出所有不为左括号之后堆栈为空，则表达式不合法
								{
									return "缺少左括号";
								}
							}
							s4.pop();// 弹出（
							break;
						} else {
							if (priorityCompare(c1, s4.peek()) == 1)// 判断优先级，优先级高入栈，优先级低将栈顶运算符压入s3
							{
								s4.push(c1);
								break;
							} else {
								s3.push(s4.pop());
							}
						}
					}
				}
			} else
				continue;

		}
		while (!s4.isEmpty())// 表达式结束后，依次将s4剩下的运算符压入s3
		{
			if ((char) s4.peek() == '(')
				return "缺少右括号";
			s3.push(s4.pop());
		}
		return count_result(s3);
	}

	private static int priorityCompare(char c1, char c2) {
		switch (c1) {
		case '+':
		case '-':
			return (c2 == '*' || c2 == '/' ? -1 : 0);
		case '*':
		case '/':
			return (c2 == '+' || c2 == '-' ? 1 : 0);
		}
		return 1;
	}

	// 判断字符是否为运算符，是为真，不是为假
	private static boolean isOprator(Object c) {
		try {
			char c1 = (Character) c;
			if (c1 == '+' || c1 == '-' || c1 == '*' || c1 == '/' || c1 == '('
					|| c1 == ')')
				return true;

		} catch (Exception e) {
			return false;
		}
		return false;
	}

	private static String count_result(Stack<Object> ob) {
		Stack<Object> s1 = new Stack<Object>();// 后缀表达式栈
		Stack<Double> s2 = new Stack<Double>();// 操作数栈
		// char c1;
		// Stack<Character> s3=new Stack<Character>();//操作符栈

		while (!ob.isEmpty())// 将传入的栈逆序压入
		{
			s1.push(ob.pop());
		}
		while (!s1.isEmpty()) {
			if (!isOprator(s1.peek()))// 遇到非操作符，压入s2栈
			{
				s2.push((Double) s1.pop());
			} else {
				s2.push(cout(s2.pop(), s2.pop(), (Character) s1.pop()));
			}
		}
		return Double.toString(s2.peek());
	}

	private static Double cout(double s1, double s2, char s3) {
		double result = 0;
		switch (s3) {
		case '+':
			result = s1 + s2;
			break;
		case '-':
			result = s2 - s1;
			break;
		case '*':
			result = s1 * s2;
			break;
		case '/':
			result = s2 / s1;
			break;
		}
		return result;
	}
}

