package com.deedm.legacy.counting;
 
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * 支持加减乘除、幂、组合数排列数、阶乘的表达式求值算法
 * @version 1.0
 */
public class ExprCalculator {
	
    // 记录表达式是否符合规范
    static boolean isStandard = true;
    
    // 运算符优先级map
    private static final Map<String, Integer> OPT_PRIORITY_MAP = new HashMap<String, Integer>() {
		private static final long serialVersionUID = 1L;
		//        private static final long serialVersionUID = 6968472606692771458L;
        {
            put("(", 0);
            put("+", 2);
            put("-", 2);
            put("*", 3);
            put("/", 3);
            put("^", 7);
            put("P", 8);
            put("C", 8);
            put(",", 9);
            put(")", 10);
            put("=", 20);
        }
    };
    
    /**
     * 输入表达式字符串，返回计算结果
     * @param expression 表达式字符串
     * @return 返回计算结果
     */
    public static String executeExpression(String expression) {
    	// 初始化isStandard
    	isStandard = true;
 
        Stack<String> optStack = new Stack<>(); // 运算符栈
        Stack<BigDecimal> numStack = new Stack<>(); // 数值栈，数值以BigDecimal存储计算，避免精度计算问题
        StringBuilder curNumBuilder = new StringBuilder(16); // 当前正在读取中的数值字符追加器
        
        // 去除字符串中的无效空格
        expression = expression.replaceAll(" ", "");
        
        // 逐个读取字符，并根据运算符判断参与何种计算
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
           
            if ((c >= '0' && c <= '9') || c == '.' || (c == '-' && i == 0) || (c == '-' && expression.charAt(i-1) == '(')) { // 支持正负整数、正负小数的合并
                curNumBuilder.append(c); // 持续读取一个数值的各个字符
            } else {
                if (curNumBuilder.length() > 0) {
                	if (curNumBuilder.length() == 1 && curNumBuilder.charAt(0) == '-') return "error";
                    // 如果追加器有值，说明之前读取的字符是数值，而且此时已经完整读取完一个数值
                    numStack.push(new BigDecimal(curNumBuilder.toString()));
                    curNumBuilder.delete(0, curNumBuilder.length());
                }
            	
                String curOpt = String.valueOf(c);
                if (optStack.empty() && !curOpt.equals("!")) { // 阶乘比较特殊，只需要一个操作数
                    // 运算符栈栈顶为空则直接入栈
                    optStack.push(curOpt);
                } else {
                    if (curOpt.equals("(") || curOpt.equals("P") || curOpt.equals("C") || curOpt.equals(",")) {
                        // 当前运算符为左括号或C或P，直接入运算符栈
                        optStack.push(curOpt);
                    } else if (curOpt.equals(")")) {
                        // 当前运算符为右括号，触发括号内的字表达式进行计算
                        directCalc(optStack, numStack, true);
                    } else if (curOpt.equals("!")) {
                    	if (numStack.empty()) return "error";
                    	// 当前运算符为阶乘运算符，取数值栈栈顶数进行阶乘运算
                    	String str = numStack.peek().toString();
                    	int num = numStack.pop().intValue();
                    	BigInteger result = CombCalculator.factorialBigInteger(num);
                    	inputValidation(str, str);
                    	numStack.push(new BigDecimal(result));
                    } else if (curOpt.equals("=")) { // 如果允许最后有等号需要在UIManager中的正则表达式添加
                        // 当前运算符为等号，触发整个表达式剩余计算，并返回总的计算结果
                        directCalc(optStack, numStack, false);
                        if (!isStandard || numStack.empty()) {
                        	numStack.clear();
                        	optStack.clear();
                        	return "error";
                        }
                        return numStack.pop().toString();
                    } else {
                        // 当前运算符为加减乘除幂之一，要与栈顶运算符比较，判断是否要进行一次二元计算
                        compareAndCalc(optStack, numStack, curOpt);
                    }
                }
            }
        }
 
        // 表达式不是以等号结尾的场景
        if (curNumBuilder.length() > 0) {
        	if (curNumBuilder.length() == 1 && curNumBuilder.charAt(0) == '-') return "error";
    	    // 如果追加器有值，说明之前读取的字符是数值，而且此时已经完整读取完一个数值
            numStack.push(new BigDecimal(curNumBuilder.toString()));
    	}
        directCalc(optStack, numStack, false);
        if (!isStandard || numStack.empty()) {
        	return "error";
        }
        return numStack.pop().toString();
    }
    
    /**
     * priority = 0  表示两个运算符同级别
     * priority > 0  第一个运算符级别高
     * priority < 0  第二个运算符级别高
     * @param opt1
     * @param opt2
     * @return
     */
    public static int getPriority(String opt1, String opt2) {
        int priority = OPT_PRIORITY_MAP.get(opt1) - OPT_PRIORITY_MAP.get(opt2);
        return priority;
    }
    
    /**
     * 拿当前运算符和栈顶运算符对比，如果栈顶运算符优先级高于或同级于当前运算符，
     * 则执行一次二元运算（递归比较并计算），否则当前运算符入栈
     * @param optStack 运算符栈
     * @param numStack 数值栈
     * @param curOpt 当前运算符
     */
    public static void compareAndCalc(Stack<String> optStack, Stack<BigDecimal> numStack, 
    		String curOpt) {
        // 比较当前运算符和栈顶运算符的优先级
        String peekOpt = optStack.peek();
        int priority = getPriority(peekOpt, curOpt);
        if (priority >= 0) {
            // 栈顶运算符优先级大或同级，触发一次二元运算
            String opt = optStack.pop(); // 当前参与计算运算符
            
            // 表达式不规范导致空栈异常
            if (numStack.size() < 2) {
            	isStandard = false;
            	return;
            }
            
            BigDecimal num2 = numStack.pop(); // 当前参与计算数值2
            BigDecimal num1 = numStack.pop(); // 当前参与计算数值1
            BigDecimal bigDecimal = floatingPointCalc(opt, num1, num2);
            
            // 计算结果当做操作数入栈
            numStack.push(bigDecimal);
            
            // 运算完栈顶还有运算符，则还需要再次触发一次比较判断是否需要再次二元计算
            if (optStack.empty()) {
                optStack.push(curOpt);
            } else {
                compareAndCalc(optStack, numStack, curOpt);
            }
        } else {
            // 当前运算符优先级高，则直接入栈
            optStack.push(curOpt);
        }
    }
    
    /**
     * 遇到右括号和等号执行的连续计算操作（递归计算）
     * @param optStack 运算符栈
     * @param numStack 数值栈
     * @param isBracket true表示为括号类型计算
     */
    public static void directCalc(Stack<String> optStack, Stack<BigDecimal> numStack, 
    		boolean isBracket) {
    	if (optStack.empty()) return; // 没有操作符时直接退出
        String opt = optStack.pop(); // 当前参与计算运算符
        
        // 考虑(-1) (1)等括号内只有一个数的情况是合法的,直接不进行后续计算
        if (opt.equals("(")) return;
        
        // 表达式不规范导致空栈异常
        if (numStack.size() < 2) {
        	isStandard = false;
        	return;
        }
        
        BigDecimal num2 = numStack.pop(); // 当前参与计算数值2
        BigDecimal num1 = numStack.pop(); // 当前参与计算数值1
        
        if (opt.equals(",")) {
            // 表达式不规范导致空栈异常
            if (optStack.size() < 2) {
            	isStandard = false;
            	return;
            }
        	
        	// 暂定排列数和组合数参数均是整数（非表达式），那么操作符栈中","向下两层则是P或者C
        	opt = optStack.pop();
        	opt = optStack.pop();
        	optStack.push("(");
        }
        
        BigDecimal bigDecimal = floatingPointCalc(opt, num1, num2);
        
        // 计算结果当做操作数入栈
        numStack.push(bigDecimal);
        if (isBracket) {
             // 表达式不规范导致空栈异常
            if (optStack.empty()) {
            	isStandard = false;
            	return;
            }
            if ("(".equals(optStack.peek())) {
                // 括号类型则遇左括号停止计算，同时将左括号从栈中移除
                optStack.pop();
            } else {
                directCalc(optStack, numStack, isBracket);
            }
        } else {
            if (!optStack.empty()) {
                // 等号类型只要栈中还有运算符就继续计算
                directCalc(optStack, numStack, isBracket);
            }
        }
    }
    
    /**
     * 不丢失精度的二元运算，支持高精度计算
     * @param opt
     * @param bigDecimal1
     * @param bigDecimal2
     * @return
     */
    public static BigDecimal floatingPointCalc(String opt, BigDecimal bigDecimal1, 
    		BigDecimal bigDecimal2) {
        BigDecimal resultBigDecimal = new BigDecimal(0);
        switch (opt) {
            case "+":
                resultBigDecimal = bigDecimal1.add(bigDecimal2);
                break;
            case "-":
                resultBigDecimal = bigDecimal1.subtract(bigDecimal2);
                break;
            case "*":
                resultBigDecimal = bigDecimal1.multiply(bigDecimal2);
                break;
            case "/":
                resultBigDecimal = bigDecimal1.divide(bigDecimal2, 10, BigDecimal.ROUND_HALF_DOWN); // 注意此处用法
                break;
            case "^":
            	if (bigDecimal2.intValue() >= 0) resultBigDecimal = bigDecimal1.pow(bigDecimal2.intValue());
            	else {
            		resultBigDecimal = bigDecimal1.pow((-1)*bigDecimal2.intValue());
            		BigDecimal tmpBigDecimal = new BigDecimal(1);
            		resultBigDecimal = tmpBigDecimal.divide(resultBigDecimal, 10, BigDecimal.ROUND_HALF_DOWN); // 注意此处用法
            	}
            	break;
            case "C":
            	int n1 = bigDecimal1.intValue(), m1 = bigDecimal2.intValue();
//            	int result1 = CombCalculator.choose(n1, m1);
            	BigInteger result1 = CombCalculator.chooseBigInteger(n1, m1);
            	resultBigDecimal = new BigDecimal(result1);
            	inputValidation(bigDecimal1.toString(), bigDecimal2.toString());
            	break;
            case "P":
            	int n2 = bigDecimal1.intValue(), m2 = bigDecimal2.intValue();
//            	int result2 = CombCalculator.permutation(n2, m2);
            	BigInteger result2 = CombCalculator.permutationBigInteger(n2, m2);
            	resultBigDecimal = new BigDecimal(result2);
            	inputValidation(bigDecimal1.toString(), bigDecimal2.toString());
            	break;
            default:
                break;
        }
        return resultBigDecimal;
    }
    
    /**
     * 判断计算过程中!、C、P的输入合法性
     * @param input1
     * @param input2
     */
    static void inputValidation(String input1, String input2) {
    	try {
    		if (Integer.parseInt(input1) < 0 || Integer.parseInt(input2) < 0) {
    			isStandard = false;
        		return;
    		}
    	} catch (Exception exc) {
			// TODO: handle exception
    		isStandard = false;
    		return;
		}
    	
    }
    
    
    public static void main(String[] args) {
    	// 几个测试数据
    	
//    	Scanner in = new Scanner(System.in);
//      System.out.println(executeExpression(in.nextLine()));
//      in.close();
    }
    
    private ExprCalculator(){};
    
}


