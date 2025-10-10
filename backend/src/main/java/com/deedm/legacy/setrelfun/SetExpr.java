package com.deedm.legacy.setrelfun;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

 
/**
 * 支持集合运算表达式求值算法
 * @version 1.0
 */
public class SetExpr {
	
    // 记录表达式是否符合规范
    static boolean isStandard = true;
    
    // 运算符优先级map
    private static final Map<String, Integer> OPT_PRIORITY_MAP = new HashMap<String, Integer>() {
		private static final long serialVersionUID = 1L;
		//        private static final long serialVersionUID = 6968472606692771458L;
        {
            put("(", 0);
            put("-", 2); 
            put("ca", 5); // \cap
            put("cu", 5); // \cup
            put("op", 5); // \oplus
            put(")", 10);
        }
    };
    
    /**
     * 输入集合运算表达式字符串，返回计算结果
     * @param expression 表达式字符串
     * @param U 全集
     * @param A 集合A
     * @param B 集合B
     * @param flag 记录上一层递归运算中，isStandard表达式合法性
     * @return 返回计算结果
     */
    public static Set executeExpression(String expression, Set U, Set A, Set B, boolean flag) {
    	// 初始化isStandard
    	isStandard = flag;
    	
    	char[] setErrorElements = SetrelfunUtil.extractSetElements("e,r,r,o,r", false);
    	Set errorMessageSet = new Set(setErrorElements);
    	if (!isStandard) {
    		return errorMessageSet;
    	}

        Stack<String> optStack = new Stack<>(); // 运算符栈
        Stack<Set> setStack = new Stack<>(); // 集合栈，以Set集合形式存储
        
        // 去除字符串中的无效空格
        expression = expression.replaceAll(" ", "");
        
        // 逐个读取字符，并根据运算符判断参与何种计算，对于\cap \cup \oplus \overline来说，各取其中的'\'和后两个字符作为操作符标识，分别取\ca,\cu,\op,\ov
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            String curOpt = String.valueOf(c);
            if (c == '\\') {
            	curOpt = String.valueOf(expression.charAt(i+1)) + String.valueOf(expression.charAt(i+2));
            }
            if (c == 'A') {
            	setStack.push(A);
            } else if (c == 'B') {
        	   setStack.push(B);
            } else if (optStack.empty() && (curOpt.equals("-") || (c == '\\' && !curOpt.equals("ov")))) { // 对于\overline要先进行{}中式子的计算，不直接入操作符栈
            	optStack.push(curOpt);
            } else if (curOpt.equals("(")) { // 对于
            	optStack.push("(");
            } else if (curOpt.equals(")")) {
                // 当前运算符为右括号，触发括号内的字表达式进行计算
                directCalc(optStack, setStack, U, true);
            } else if (curOpt.equals("ov")) { // 对于\overline先递归计算{}内的结果，再进行\overline的计算
            	if (i+9 >= expression.length() || expression.charAt(i+9) != '{') {
            		isStandard = false; // 传递给上一递归层
            		return errorMessageSet;
            	}
            	int endIndex = getBraceMatchingIndex(expression, i+9);
            	if (endIndex == -1) {
            		isStandard = false; // 传递给上一递归层
            		return errorMessageSet;
            	}
            	Set tmpSet = executeExpression(expression.substring(i+10, endIndex-1), U, A, B, isStandard);
            	Set resultTmpSet = U.subtract(tmpSet);
            	resultTmpSet = U.getSubsetForCharFunction(U.getCharFunctionForSubset(resultTmpSet));
            	if (resultTmpSet.equalsTo(errorMessageSet)) {
            		isStandard = false; // 传递给上一递归层
            		return errorMessageSet;
            	}
            	setStack.push(resultTmpSet);
            	i = endIndex;
            } else if (curOpt.equals("-") || curOpt.equals("ca") || curOpt.equals("cu") || curOpt.equals("op")){ // 剩余为交、并、差、对称差
            	compareAndCalc(optStack, setStack, curOpt, U);
			}
        }
            
        directCalc(optStack, setStack, U, false);
        if (!isStandard || setStack.empty()) {
        	return errorMessageSet;
        }
        return setStack.pop();
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
     * @param setStack 集合栈
     * @param curOpt 当前运算符
     * @param U 全集
     */
    public static void compareAndCalc(Stack<String> optStack, Stack<Set> setStack, 
    		String curOpt, Set U) {
        // 比较当前运算符和栈顶运算符的优先级
        String peekOpt = optStack.peek();
        int priority = getPriority(peekOpt, curOpt);
        if (priority >= 0) {
            // 栈顶运算符优先级大或同级，触发一次二元运算
            String opt = optStack.pop(); // 当前参与计算运算符
            
            // 表达式不规范导致空栈异常
            if (setStack.size() < 2) {
            	isStandard = false;
            	return;
            }
            
            Set B = setStack.pop(); // // 当前参与计算集合二
            Set A = setStack.pop(); // 当前参与计算集合一
            Set resultSet = setCalculate(A, B, U, opt);
            
            
            // 计算结果入栈
            setStack.push(resultSet);
            
            // 运算完栈顶还有运算符，则还需要再次触发一次比较判断是否需要再次二元计算
            if (optStack.empty()) {
                optStack.push(curOpt);
            } else {
                compareAndCalc(optStack, setStack, curOpt, U);
            }
        } else {
            // 当前运算符优先级高，则直接入栈
            optStack.push(curOpt);
        }
    }
    
    /**
     * 遇到右括号和等号执行的连续计算操作（递归计算）
     * @param optStack 运算符栈
     * @param setStack 集合栈
     * @param U 全集 
     * @param isBracket true表示为括号类型计算,false表示整个式子的最终计算
     */
    public static void directCalc(Stack<String> optStack, Stack<Set> setStack,
    		Set U, boolean isBracket) {
    	if (optStack.empty()) return; // 没有操作符时直接退出
        String opt = optStack.pop(); // 当前参与计算运算符
        
        // 考虑(A) (B)等括号内只有一个集合的情况是合法的,直接不进行后续计算
        if (opt.equals("(")) return;
        
        // 表达式不规范导致空栈异常
        if (setStack.size() < 2) {
        	isStandard = false;
        	return;
        }
        
        Set B = setStack.pop(); // 当前参与计算集合二
        Set A = setStack.pop(); // 当前参与计算集合一
        Set resultSet = setCalculate(A, B, U, opt);       
   
        // 计算结果当做操作数入栈
        setStack.push(resultSet);
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
                directCalc(optStack, setStack, U, isBracket);
            }
        } else {
            if (!optStack.empty()) {
                // 等号类型只要栈中还有运算符就继续计算
                directCalc(optStack, setStack, U, isBracket);
            }
        }
    }
    
    /**
     * 计算两集合之间的交、并、差、对称差
     * @param A 集合A
     * @param B 集合B
     * @param opt 运算符
     * @param U 全集
     * @return 当前操作符计算结果集合
     */
    public static Set setCalculate(Set A, Set B, Set U, String opt) {
    	Set resultSet = null;
    	switch (opt) {
		case "ca":
			resultSet = A.intersection(B);
			break;
		case "cu":
			resultSet = A.union(B);
			break;
		case "-":
			resultSet = A.subtract(B);
			break;
		case "op":
			resultSet = A.symmetricDifference(B);
			break;
		default:
			break;
		}
    	resultSet = U.getSubsetForCharFunction(U.getCharFunctionForSubset(resultSet));
    	return resultSet;
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
    
    /**
     * 用于字符串括号、花括号等的匹配，输入左括号，返回对应有括号的下标索引
     */
    private static int getBraceMatchingIndex(String str, int beginIndex) {
    	int endIndex;
    	String left = String.valueOf(str.charAt(beginIndex));
    	String right = "";
    	switch (left) {
			case "(":
				right = ")";
				break;
			case "[":
				right = "]";
				break;
			case "{":
				right = "}";
				break;
			default:
				break;
		}
    	Stack<String> aStack = new Stack<>();
    	aStack.push(left);
		for (endIndex = beginIndex+1; !aStack.empty() && endIndex < str.length(); endIndex++) { // 括号匹配
			if (String.valueOf(str.charAt(endIndex)).equals(left)) {
				aStack.push(left);
			} else if (String.valueOf(str.charAt(endIndex)).equals(right)) {
				aStack.pop();
			}
		}
		if (endIndex == str.length() && !aStack.empty()) return -1;
    	return endIndex;
    }
    
    public static void main(String[] args) {
    	// 几个测试数据
//    	Scanner in = new Scanner(System.in);
//      System.out.println();
//      in.close();
    }
    
    private SetExpr(){};
    
}

