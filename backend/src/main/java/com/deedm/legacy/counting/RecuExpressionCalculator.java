package com.deedm.legacy.counting;
 
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * ֧�ּӼ��˳����ݡ���������������׳˵ĵ��Ʊ��ʽ��ֵ�㷨
 * @version 1.0
 */
public class RecuExpressionCalculator {
	
    // ��¼���ʽ�Ƿ���Ϲ淶
    static boolean isStandard = true;
    
    // ��������ȼ�map
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
    
    public static String executeRecuExpression(String input, String inputA, String inputB, String inputN) {
    	boolean flag; // true��ʾδ֪������Ϊ1, false��ʾΪ2
    	int countsA = 0, countsB = 0, n = Integer.parseInt(inputN);
    	String result = null;
    	String initialExpression = input;
    	// ����a��b���������ж�δ֪���ĸ���
    	for (int i = 0; i < input.length(); i++) {
    		if (input.charAt(i) == 'a') {
    			countsA++;
    		} else if (input.charAt(i) == 'b') {
    			countsB++;
    		}
    	}
    	// Ϊ����ͳһ���㣬����һ��δ֪���������Ĭ��ʹ��δ֪��a���û���������b���滻Ϊa,���ǽ��������b��ֵ����,��֤�û��߼�����
    	if (countsA == 0 && countsB > 0) {
    		initialExpression = initialExpression.replace('b', 'a');
    		inputA = inputB;
    	}
    	// �ж��Ǽ���δ֪��������������������
    	if (countsA > 0 && countsB > 0) {
    		flag = false;
    	} else {
			flag = true;
		}
    
    	if (flag) { // һ��δ֪�������
    		result = inputA;
    		// ��������n��
        	for (int i = 2; i <= n; i++) {
        		// �滻���ʽ�еĳ�ʼ�����ַ�a
        		input = initialExpression.replaceAll("a", "(" + inputA + ")");
        		input = input.replaceAll("n", "(" + i + ")");
        		result = executeExpression(input);
        		// ����ʵ����Ҳ����ֱ�ӵ���ExprCalculator�еļ��㺯��������
        		// ����Ϊ�˺�������ģ�����в�ͬ���޸ģ����ǽ����㺯������������д������
        		// result = ExprCalculator.executeExpression(input);
        		if (result.equals("error")) {
        			return result;
        		} else {
        			inputA = result;
        		}
        	}
    	} else { // ����δ֪�������
    		if (n == 1) result = inputA;
    		else if (n == 2) {
				result = inputB;
			}
    		// ��������n��
        	for (int i = 3; i <= n; i++) {
        		// �滻���ʽ�еĳ�ʼ�����ַ�a��b
        		input = initialExpression.replaceAll("a", "(" + inputA + ")");
        		input = input.replaceAll("b", "(" + inputB + ")");
        		input = input.replaceAll("n", "(" + i + ")");
        		result = executeExpression(input);
        		// result = ExprCalculator.executeExpression(input);
        		if (result.equals("error")) {
        			return result;
        		} else {
        			inputA = inputB;
        			inputB = result;
        		}
        	}
    	}
    
    	return result;
    }
    
    /**
     * ������ʽ�ַ��������ؼ�����
     * @param expression ���ʽ�ַ���
     * @return ���ؼ�����
     */
    public static String executeExpression(String expression) {
        
    	// ��ʼ��isStandard
    	isStandard = true;
        
        Stack<String> optStack = new Stack<>(); // �����ջ
        Stack<BigDecimal> numStack = new Stack<>(); // ��ֵջ����ֵ��BigDecimal�洢���㣬���⾫�ȼ�������
        StringBuilder curNumBuilder = new StringBuilder(16); // ��ǰ���ڶ�ȡ�е���ֵ�ַ�׷����
        
        // ȥ���ַ����е���Ч�ո�
        expression = expression.replaceAll(" ", "");
        
        // �����ȡ�ַ���������������жϲ�����ּ���
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
           
            if ((c >= '0' && c <= '9') || c == '.' || (c == '-' && i == 0) || (c == '-' && expression.charAt(i-1) == '(')) { // ֧����������������С���ĺϲ�
                curNumBuilder.append(c); // ������ȡһ����ֵ�ĸ����ַ�
            } else {
                if (curNumBuilder.length() > 0) {
                	if (curNumBuilder.length() == 1 && curNumBuilder.charAt(0) == '-') return "error";
                    // ���׷������ֵ��˵��֮ǰ��ȡ���ַ�����ֵ�����Ҵ�ʱ�Ѿ�������ȡ��һ����ֵ
                    numStack.push(new BigDecimal(curNumBuilder.toString()));
                    curNumBuilder.delete(0, curNumBuilder.length());
                }
            	
                String curOpt = String.valueOf(c);
                if (optStack.empty() && !curOpt.equals("!")) { // �׳˱Ƚ����⣬ֻ��Ҫһ��������
                    // �����ջջ��Ϊ����ֱ����ջ
                    optStack.push(curOpt);
                } else {
                    if (curOpt.equals("(") || curOpt.equals("P") || curOpt.equals("C") || curOpt.equals(",")) {
                        // ��ǰ�����Ϊ�����Ż�C��P��ֱ���������ջ
                        optStack.push(curOpt);
                    } else if (curOpt.equals(")")) {
                        // ��ǰ�����Ϊ�����ţ����������ڵ��ֱ��ʽ���м���
                        directCalc(optStack, numStack, true);
                    } else if (curOpt.equals("!")) {
                    	if (numStack.empty()) return "error";
                    	// ��ǰ�����Ϊ�׳��������ȡ��ֵջջ�������н׳�����
                    	String str = numStack.peek().toString();
                    	int num = numStack.pop().intValue();
                    	BigInteger result = CombCalculator.factorialBigInteger(num);
                    	inputValidation(str, str);
                    	numStack.push(new BigDecimal(result));
                    } else if (curOpt.equals("=")) { // �����������еȺ���Ҫ��UIManager�е�������ʽ���
                        // ��ǰ�����Ϊ�Ⱥţ������������ʽʣ����㣬�������ܵļ�����
                        directCalc(optStack, numStack, false);
                        if (!isStandard || numStack.empty()) {
                        	numStack.clear();
                        	optStack.clear();
                        	return "error";
                        }
                        return numStack.pop().toString();
                    } else {
                        // ��ǰ�����Ϊ�Ӽ��˳���֮һ��Ҫ��ջ��������Ƚϣ��ж��Ƿ�Ҫ����һ�ζ�Ԫ����
                        compareAndCalc(optStack, numStack, curOpt);
                    }
                }
            }
        }
 
        // ���ʽ�����ԵȺŽ�β�ĳ���
        if (curNumBuilder.length() > 0) {
        	if (curNumBuilder.length() == 1 && curNumBuilder.charAt(0) == '-') return "error";
    	    // ���׷������ֵ��˵��֮ǰ��ȡ���ַ�����ֵ�����Ҵ�ʱ�Ѿ�������ȡ��һ����ֵ
            numStack.push(new BigDecimal(curNumBuilder.toString()));
    	}
        directCalc(optStack, numStack, false);
        if (!isStandard || numStack.empty()) {
        	return "error";
        }
        return numStack.pop().toString();
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
     * @param numStack ��ֵջ
     * @param curOpt ��ǰ�����
     */
    public static void compareAndCalc(Stack<String> optStack, Stack<BigDecimal> numStack, 
    		String curOpt) {
        // �Ƚϵ�ǰ�������ջ������������ȼ�
        String peekOpt = optStack.peek();
        int priority = getPriority(peekOpt, curOpt);
        if (priority >= 0) {
            // ջ����������ȼ����ͬ��������һ�ζ�Ԫ����
            String opt = optStack.pop(); // ��ǰ������������
            
            // ���ʽ���淶���¿�ջ�쳣
            if (numStack.size() < 2) {
            	isStandard = false;
            	return;
            }
            
            BigDecimal num2 = numStack.pop(); // ��ǰ���������ֵ2
            BigDecimal num1 = numStack.pop(); // ��ǰ���������ֵ1
            BigDecimal bigDecimal = floatingPointCalc(opt, num1, num2);
            
            // ������������������ջ
            numStack.push(bigDecimal);
            
            // ������ջ�����������������Ҫ�ٴδ���һ�αȽ��ж��Ƿ���Ҫ�ٴζ�Ԫ����
            if (optStack.empty()) {
                optStack.push(curOpt);
            } else {
                compareAndCalc(optStack, numStack, curOpt);
            }
        } else {
            // ��ǰ��������ȼ��ߣ���ֱ����ջ
            optStack.push(curOpt);
        }
    }
    
    /**
     * ���������ź͵Ⱥ�ִ�е���������������ݹ���㣩
     * @param optStack �����ջ
     * @param numStack ��ֵջ
     * @param isBracket true��ʾΪ�������ͼ���
     */
    public static void directCalc(Stack<String> optStack, Stack<BigDecimal> numStack, 
    		boolean isBracket) {
    	if (optStack.empty()) return; // û�в�����ʱֱ���˳�
        String opt = optStack.pop(); // ��ǰ������������
        
        // ����(-1) (1)��������ֻ��һ����������ǺϷ���,ֱ�Ӳ����к�������
        if (opt.equals("(")) return;
        
        // ���ʽ���淶���¿�ջ�쳣
        if (numStack.size() < 2) {
        	isStandard = false;
        	return;
        }
        
        BigDecimal num2 = numStack.pop(); // ��ǰ���������ֵ2
        BigDecimal num1 = numStack.pop(); // ��ǰ���������ֵ1
        
        if (opt.equals(",")) {
            // ���ʽ���淶���¿�ջ�쳣
            if (optStack.size() < 2) {
            	isStandard = false;
            	return;
            }
        	
        	// �ݶ�����������������������������Ǳ��ʽ������ô������ջ��","������������P����C
        	opt = optStack.pop();
        	opt = optStack.pop();
        	optStack.push("(");
        }
        
        BigDecimal bigDecimal = floatingPointCalc(opt, num1, num2);
        
        // ������������������ջ
        numStack.push(bigDecimal);
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
                directCalc(optStack, numStack, isBracket);
            }
        } else {
            if (!optStack.empty()) {
                // �Ⱥ�����ֻҪջ�л���������ͼ�������
                directCalc(optStack, numStack, isBracket);
            }
        }
    }
    
    /**
     * ����ʧ���ȵĶ�Ԫ���㣬֧�ָ߾��ȼ���
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
                resultBigDecimal = bigDecimal1.divide(bigDecimal2, 10, BigDecimal.ROUND_HALF_DOWN); // ע��˴��÷�
                break;
            case "^":
            	if (bigDecimal2.intValue() >= 0) resultBigDecimal = bigDecimal1.pow(bigDecimal2.intValue());
            	else {
            		resultBigDecimal = bigDecimal1.pow((-1)*bigDecimal2.intValue());
            		BigDecimal tmpBigDecimal = new BigDecimal(1);
            		resultBigDecimal = tmpBigDecimal.divide(resultBigDecimal, 10, BigDecimal.ROUND_HALF_DOWN); // ע��˴��÷�
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
    
    
    public static void main(String[] args) {
    	// ������������
    	
//    	Scanner in = new Scanner(System.in);
//      System.out.println(executeExpression(in.nextLine()));
//      in.close();
    }
    
    private RecuExpressionCalculator(){};
    
}

