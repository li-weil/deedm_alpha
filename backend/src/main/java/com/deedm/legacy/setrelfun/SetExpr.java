package com.deedm.legacy.setrelfun;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

 
/**
 * ֧�ּ���������ʽ��ֵ�㷨
 * @version 1.0
 */
public class SetExpr {
	
    // ��¼���ʽ�Ƿ���Ϲ淶
    static boolean isStandard = true;
    
    // ��������ȼ�map
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
     * ���뼯��������ʽ�ַ��������ؼ�����
     * @param expression ���ʽ�ַ���
     * @param U ȫ��
     * @param A ����A
     * @param B ����B
     * @param flag ��¼��һ��ݹ������У�isStandard���ʽ�Ϸ���
     * @return ���ؼ�����
     */
    public static Set executeExpression(String expression, Set U, Set A, Set B, boolean flag) {
    	// ��ʼ��isStandard
    	isStandard = flag;
    	
    	char[] setErrorElements = SetrelfunUtil.extractSetElements("e,r,r,o,r", false);
    	Set errorMessageSet = new Set(setErrorElements);
    	if (!isStandard) {
    		return errorMessageSet;
    	}

        Stack<String> optStack = new Stack<>(); // �����ջ
        Stack<Set> setStack = new Stack<>(); // ����ջ����Set������ʽ�洢
        
        // ȥ���ַ����е���Ч�ո�
        expression = expression.replaceAll(" ", "");
        
        // �����ȡ�ַ���������������жϲ�����ּ��㣬����\cap \cup \oplus \overline��˵����ȡ���е�'\'�ͺ������ַ���Ϊ��������ʶ���ֱ�ȡ\ca,\cu,\op,\ov
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
            } else if (optStack.empty() && (curOpt.equals("-") || (c == '\\' && !curOpt.equals("ov")))) { // ����\overlineҪ�Ƚ���{}��ʽ�ӵļ��㣬��ֱ���������ջ
            	optStack.push(curOpt);
            } else if (curOpt.equals("(")) { // ����
            	optStack.push("(");
            } else if (curOpt.equals(")")) {
                // ��ǰ�����Ϊ�����ţ����������ڵ��ֱ��ʽ���м���
                directCalc(optStack, setStack, U, true);
            } else if (curOpt.equals("ov")) { // ����\overline�ȵݹ����{}�ڵĽ�����ٽ���\overline�ļ���
            	if (i+9 >= expression.length() || expression.charAt(i+9) != '{') {
            		isStandard = false; // ���ݸ���һ�ݹ��
            		return errorMessageSet;
            	}
            	int endIndex = getBraceMatchingIndex(expression, i+9);
            	if (endIndex == -1) {
            		isStandard = false; // ���ݸ���һ�ݹ��
            		return errorMessageSet;
            	}
            	Set tmpSet = executeExpression(expression.substring(i+10, endIndex-1), U, A, B, isStandard);
            	Set resultTmpSet = U.subtract(tmpSet);
            	resultTmpSet = U.getSubsetForCharFunction(U.getCharFunctionForSubset(resultTmpSet));
            	if (resultTmpSet.equalsTo(errorMessageSet)) {
            		isStandard = false; // ���ݸ���һ�ݹ��
            		return errorMessageSet;
            	}
            	setStack.push(resultTmpSet);
            	i = endIndex;
            } else if (curOpt.equals("-") || curOpt.equals("ca") || curOpt.equals("cu") || curOpt.equals("op")){ // ʣ��Ϊ����������ԳƲ�
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
     * priority = 0  ��ʾ���������ͬ����
     * priority > 0  ��һ������������
     * priority < 0  �ڶ�������������
     * @param opt1
     * @param opt2
     * @return
     */
    public static int getPriority(String opt1, String opt2) {
        int priority = OPT_PRIORITY_MAP.get(opt1) - OPT_PRIORITY_MAP.get(opt2);
        return priority;
    }
    
    /**
     * �õ�ǰ�������ջ��������Աȣ����ջ����������ȼ����ڻ�ͬ���ڵ�ǰ�������
     * ��ִ��һ�ζ�Ԫ���㣨�ݹ�Ƚϲ����㣩������ǰ�������ջ
     * @param optStack �����ջ
     * @param setStack ����ջ
     * @param curOpt ��ǰ�����
     * @param U ȫ��
     */
    public static void compareAndCalc(Stack<String> optStack, Stack<Set> setStack, 
    		String curOpt, Set U) {
        // �Ƚϵ�ǰ�������ջ������������ȼ�
        String peekOpt = optStack.peek();
        int priority = getPriority(peekOpt, curOpt);
        if (priority >= 0) {
            // ջ����������ȼ����ͬ��������һ�ζ�Ԫ����
            String opt = optStack.pop(); // ��ǰ������������
            
            // ���ʽ���淶���¿�ջ�쳣
            if (setStack.size() < 2) {
            	isStandard = false;
            	return;
            }
            
            Set B = setStack.pop(); // // ��ǰ������㼯�϶�
            Set A = setStack.pop(); // ��ǰ������㼯��һ
            Set resultSet = setCalculate(A, B, U, opt);
            
            
            // ��������ջ
            setStack.push(resultSet);
            
            // ������ջ�����������������Ҫ�ٴδ���һ�αȽ��ж��Ƿ���Ҫ�ٴζ�Ԫ����
            if (optStack.empty()) {
                optStack.push(curOpt);
            } else {
                compareAndCalc(optStack, setStack, curOpt, U);
            }
        } else {
            // ��ǰ��������ȼ��ߣ���ֱ����ջ
            optStack.push(curOpt);
        }
    }
    
    /**
     * ���������ź͵Ⱥ�ִ�е���������������ݹ���㣩
     * @param optStack �����ջ
     * @param setStack ����ջ
     * @param U ȫ�� 
     * @param isBracket true��ʾΪ�������ͼ���,false��ʾ����ʽ�ӵ����ռ���
     */
    public static void directCalc(Stack<String> optStack, Stack<Set> setStack,
    		Set U, boolean isBracket) {
    	if (optStack.empty()) return; // û�в�����ʱֱ���˳�
        String opt = optStack.pop(); // ��ǰ������������
        
        // ����(A) (B)��������ֻ��һ�����ϵ�����ǺϷ���,ֱ�Ӳ����к�������
        if (opt.equals("(")) return;
        
        // ���ʽ���淶���¿�ջ�쳣
        if (setStack.size() < 2) {
        	isStandard = false;
        	return;
        }
        
        Set B = setStack.pop(); // ��ǰ������㼯�϶�
        Set A = setStack.pop(); // ��ǰ������㼯��һ
        Set resultSet = setCalculate(A, B, U, opt);       
   
        // ������������������ջ
        setStack.push(resultSet);
        if (isBracket) {
             // ���ʽ���淶���¿�ջ�쳣
            if (optStack.empty()) {
            	isStandard = false;
            	return;
            }
            if ("(".equals(optStack.peek())) {
                // ������������������ֹͣ���㣬ͬʱ�������Ŵ�ջ���Ƴ�
                optStack.pop();
            } else {
                directCalc(optStack, setStack, U, isBracket);
            }
        } else {
            if (!optStack.empty()) {
                // �Ⱥ�����ֻҪջ�л���������ͼ�������
                directCalc(optStack, setStack, U, isBracket);
            }
        }
    }
    
    /**
     * ����������֮��Ľ���������ԳƲ�
     * @param A ����A
     * @param B ����B
     * @param opt �����
     * @param U ȫ��
     * @return ��ǰ����������������
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
     * �жϼ��������!��C��P������Ϸ���
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
     * �����ַ������š������ŵȵ�ƥ�䣬���������ţ����ض�Ӧ�����ŵ��±�����
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
		for (endIndex = beginIndex+1; !aStack.empty() && endIndex < str.length(); endIndex++) { // ����ƥ��
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
    	// ������������
//    	Scanner in = new Scanner(System.in);
//      System.out.println();
//      in.close();
    }
    
    private SetExpr(){};
    
}

