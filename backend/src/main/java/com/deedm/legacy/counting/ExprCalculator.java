package com.deedm.legacy.counting;
 
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Ö§³Ö¼Ó¼õ³Ë³ý¡¢ÃÝ¡¢×éºÏÊýÅÅÁÐÊý¡¢½×³ËµÄ±í´ïÊ½ÇóÖµËã·¨
 * @version 1.0
 */
public class ExprCalculator {
	
    // ¼ÇÂ¼±í´ïÊ½ÊÇ·ñ·ûºÏ¹æ·¶
    static boolean isStandard = true;
    
    // ÔËËã·ûÓÅÏÈ¼¶map
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
     * ÊäÈë±í´ïÊ½×Ö·û´®£¬·µ»Ø¼ÆËã½á¹û
     * @param expression ±í´ïÊ½×Ö·û´®
     * @return ·µ»Ø¼ÆËã½á¹û
     */
    public static String executeExpression(String expression) {
    	// ³õÊ¼»¯isStandard
    	isStandard = true;
 
        Stack<String> optStack = new Stack<>(); // ÔËËã·ûÕ»
        Stack<BigDecimal> numStack = new Stack<>(); // ÊýÖµÕ»£¬ÊýÖµÒÔBigDecimal´æ´¢¼ÆËã£¬±ÜÃâ¾«¶È¼ÆËãÎÊÌâ
        StringBuilder curNumBuilder = new StringBuilder(16); // µ±Ç°ÕýÔÚ¶ÁÈ¡ÖÐµÄÊýÖµ×Ö·û×·¼ÓÆ÷
        
        // È¥³ý×Ö·û´®ÖÐµÄÎÞÐ§¿Õ¸ñ
        expression = expression.replaceAll(" ", "");
        
        // Öð¸ö¶ÁÈ¡×Ö·û£¬²¢¸ù¾ÝÔËËã·ûÅÐ¶Ï²ÎÓëºÎÖÖ¼ÆËã
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
           
            if ((c >= '0' && c <= '9') || c == '.' || (c == '-' && i == 0) || (c == '-' && expression.charAt(i-1) == '(')) { // Ö§³ÖÕý¸ºÕûÊý¡¢Õý¸ºÐ¡ÊýµÄºÏ²¢
                curNumBuilder.append(c); // ³ÖÐø¶ÁÈ¡Ò»¸öÊýÖµµÄ¸÷¸ö×Ö·û
            } else {
                if (curNumBuilder.length() > 0) {
                	if (curNumBuilder.length() == 1 && curNumBuilder.charAt(0) == '-') return "error";
                    // Èç¹û×·¼ÓÆ÷ÓÐÖµ£¬ËµÃ÷Ö®Ç°¶ÁÈ¡µÄ×Ö·ûÊÇÊýÖµ£¬¶øÇÒ´ËÊ±ÒÑ¾­ÍêÕû¶ÁÈ¡ÍêÒ»¸öÊýÖµ
                    numStack.push(new BigDecimal(curNumBuilder.toString()));
                    curNumBuilder.delete(0, curNumBuilder.length());
                }
            	
                String curOpt = String.valueOf(c);
                if (optStack.empty() && !curOpt.equals("!")) { // ½×³Ë±È½ÏÌØÊâ£¬Ö»ÐèÒªÒ»¸ö²Ù×÷Êý
                    // ÔËËã·ûÕ»Õ»¶¥Îª¿ÕÔòÖ±½ÓÈëÕ»
                    optStack.push(curOpt);
                } else {
                    if (curOpt.equals("(") || curOpt.equals("P") || curOpt.equals("C") || curOpt.equals(",")) {
                        // µ±Ç°ÔËËã·ûÎª×óÀ¨ºÅ»òC»òP£¬Ö±½ÓÈëÔËËã·ûÕ»
                        optStack.push(curOpt);
                    } else if (curOpt.equals(")")) {
                        // µ±Ç°ÔËËã·ûÎªÓÒÀ¨ºÅ£¬´¥·¢À¨ºÅÄÚµÄ×Ö±í´ïÊ½½øÐÐ¼ÆËã
                        directCalc(optStack, numStack, true);
                    } else if (curOpt.equals("!")) {
                    	if (numStack.empty()) return "error";
                    	// µ±Ç°ÔËËã·ûÎª½×³ËÔËËã·û£¬È¡ÊýÖµÕ»Õ»¶¥Êý½øÐÐ½×³ËÔËËã
                    	String str = numStack.peek().toString();
                    	int num = numStack.pop().intValue();
                    	BigInteger result = CombCalculator.factorialBigInteger(num);
                    	inputValidation(str, str);
                    	numStack.push(new BigDecimal(result));
                    } else if (curOpt.equals("=")) { // Èç¹ûÔÊÐí×îºóÓÐµÈºÅÐèÒªÔÚUIManagerÖÐµÄÕýÔò±í´ïÊ½Ìí¼Ó
                        // µ±Ç°ÔËËã·ûÎªµÈºÅ£¬´¥·¢Õû¸ö±í´ïÊ½Ê£Óà¼ÆËã£¬²¢·µ»Ø×ÜµÄ¼ÆËã½á¹û
                        directCalc(optStack, numStack, false);
                        if (!isStandard || numStack.empty()) {
                        	numStack.clear();
                        	optStack.clear();
                        	return "error";
                        }
                        return numStack.pop().toString();
                    } else {
                        // µ±Ç°ÔËËã·ûÎª¼Ó¼õ³Ë³ýÃÝÖ®Ò»£¬ÒªÓëÕ»¶¥ÔËËã·û±È½Ï£¬ÅÐ¶ÏÊÇ·ñÒª½øÐÐÒ»´Î¶þÔª¼ÆËã
                        compareAndCalc(optStack, numStack, curOpt);
                    }
                }
            }
        }
 
        // ±í´ïÊ½²»ÊÇÒÔµÈºÅ½áÎ²µÄ³¡¾°
        if (curNumBuilder.length() > 0) {
        	if (curNumBuilder.length() == 1 && curNumBuilder.charAt(0) == '-') return "error";
    	    // Èç¹û×·¼ÓÆ÷ÓÐÖµ£¬ËµÃ÷Ö®Ç°¶ÁÈ¡µÄ×Ö·ûÊÇÊýÖµ£¬¶øÇÒ´ËÊ±ÒÑ¾­ÍêÕû¶ÁÈ¡ÍêÒ»¸öÊýÖµ
            numStack.push(new BigDecimal(curNumBuilder.toString()));
    	}
        directCalc(optStack, numStack, false);
        if (!isStandard || numStack.empty()) {
        	return "error";
        }
        return numStack.pop().toString();
    }
    
    /**
     * priority = 0  ±íÊ¾Á½¸öÔËËã·ûÍ¬¼¶±ð
     * priority > 0  µÚÒ»¸öÔËËã·û¼¶±ð¸ß
     * priority < 0  µÚ¶þ¸öÔËËã·û¼¶±ð¸ß
     * @param opt1
     * @param opt2
     * @return
     */
    public static int getPriority(String opt1, String opt2) {
        int priority = OPT_PRIORITY_MAP.get(opt1) - OPT_PRIORITY_MAP.get(opt2);
        return priority;
    }
    
    /**
     * ÄÃµ±Ç°ÔËËã·ûºÍÕ»¶¥ÔËËã·û¶Ô±È£¬Èç¹ûÕ»¶¥ÔËËã·ûÓÅÏÈ¼¶¸ßÓÚ»òÍ¬¼¶ÓÚµ±Ç°ÔËËã·û£¬
     * ÔòÖ´ÐÐÒ»´Î¶þÔªÔËËã£¨µÝ¹é±È½Ï²¢¼ÆËã£©£¬·ñÔòµ±Ç°ÔËËã·ûÈëÕ»
     * @param optStack ÔËËã·ûÕ»
     * @param numStack ÊýÖµÕ»
     * @param curOpt µ±Ç°ÔËËã·û
     */
    public static void compareAndCalc(Stack<String> optStack, Stack<BigDecimal> numStack, 
    		String curOpt) {
        // ±È½Ïµ±Ç°ÔËËã·ûºÍÕ»¶¥ÔËËã·ûµÄÓÅÏÈ¼¶
        String peekOpt = optStack.peek();
        int priority = getPriority(peekOpt, curOpt);
        if (priority >= 0) {
            // Õ»¶¥ÔËËã·ûÓÅÏÈ¼¶´ó»òÍ¬¼¶£¬´¥·¢Ò»´Î¶þÔªÔËËã
            String opt = optStack.pop(); // µ±Ç°²ÎÓë¼ÆËãÔËËã·û
            
            // ±í´ïÊ½²»¹æ·¶µ¼ÖÂ¿ÕÕ»Òì³£
            if (numStack.size() < 2) {
            	isStandard = false;
            	return;
            }
            
            BigDecimal num2 = numStack.pop(); // µ±Ç°²ÎÓë¼ÆËãÊýÖµ2
            BigDecimal num1 = numStack.pop(); // µ±Ç°²ÎÓë¼ÆËãÊýÖµ1
            BigDecimal bigDecimal = floatingPointCalc(opt, num1, num2);
            
            // ¼ÆËã½á¹ûµ±×ö²Ù×÷ÊýÈëÕ»
            numStack.push(bigDecimal);
            
            // ÔËËãÍêÕ»¶¥»¹ÓÐÔËËã·û£¬Ôò»¹ÐèÒªÔÙ´Î´¥·¢Ò»´Î±È½ÏÅÐ¶ÏÊÇ·ñÐèÒªÔÙ´Î¶þÔª¼ÆËã
            if (optStack.empty()) {
                optStack.push(curOpt);
            } else {
                compareAndCalc(optStack, numStack, curOpt);
            }
        } else {
            // µ±Ç°ÔËËã·ûÓÅÏÈ¼¶¸ß£¬ÔòÖ±½ÓÈëÕ»
            optStack.push(curOpt);
        }
    }
    
    /**
     * Óöµ½ÓÒÀ¨ºÅºÍµÈºÅÖ´ÐÐµÄÁ¬Ðø¼ÆËã²Ù×÷£¨µÝ¹é¼ÆËã£©
     * @param optStack ÔËËã·ûÕ»
     * @param numStack ÊýÖµÕ»
     * @param isBracket true±íÊ¾ÎªÀ¨ºÅÀàÐÍ¼ÆËã
     */
    public static void directCalc(Stack<String> optStack, Stack<BigDecimal> numStack, 
    		boolean isBracket) {
    	if (optStack.empty()) return; // Ã»ÓÐ²Ù×÷·ûÊ±Ö±½ÓÍË³ö
        String opt = optStack.pop(); // µ±Ç°²ÎÓë¼ÆËãÔËËã·û
        
        // ¿¼ÂÇ(-1) (1)µÈÀ¨ºÅÄÚÖ»ÓÐÒ»¸öÊýµÄÇé¿öÊÇºÏ·¨µÄ,Ö±½Ó²»½øÐÐºóÐø¼ÆËã
        if (opt.equals("(")) return;
        
        // ±í´ïÊ½²»¹æ·¶µ¼ÖÂ¿ÕÕ»Òì³£
        if (numStack.size() < 2) {
        	isStandard = false;
        	return;
        }
        
        BigDecimal num2 = numStack.pop(); // µ±Ç°²ÎÓë¼ÆËãÊýÖµ2
        BigDecimal num1 = numStack.pop(); // µ±Ç°²ÎÓë¼ÆËãÊýÖµ1
        
        if (opt.equals(",")) {
            // ±í´ïÊ½²»¹æ·¶µ¼ÖÂ¿ÕÕ»Òì³£
            if (optStack.size() < 2) {
            	isStandard = false;
            	return;
            }
        	
        	// ÔÝ¶¨ÅÅÁÐÊýºÍ×éºÏÊý²ÎÊý¾ùÊÇÕûÊý£¨·Ç±í´ïÊ½£©£¬ÄÇÃ´²Ù×÷·ûÕ»ÖÐ","ÏòÏÂÁ½²ãÔòÊÇP»òÕßC
        	opt = optStack.pop();
        	opt = optStack.pop();
        	optStack.push("(");
        }
        
        BigDecimal bigDecimal = floatingPointCalc(opt, num1, num2);
        
        // ¼ÆËã½á¹ûµ±×ö²Ù×÷ÊýÈëÕ»
        numStack.push(bigDecimal);
        if (isBracket) {
             // ±í´ïÊ½²»¹æ·¶µ¼ÖÂ¿ÕÕ»Òì³£
            if (optStack.empty()) {
            	isStandard = false;
            	return;
            }
            if ("(".equals(optStack.peek())) {
                // À¨ºÅÀàÐÍÔòÓö×óÀ¨ºÅÍ£Ö¹¼ÆËã£¬Í¬Ê±½«×óÀ¨ºÅ´ÓÕ»ÖÐÒÆ³ý
                optStack.pop();
            } else {
                directCalc(optStack, numStack, isBracket);
            }
        } else {
            if (!optStack.empty()) {
                // µÈºÅÀàÐÍÖ»ÒªÕ»ÖÐ»¹ÓÐÔËËã·û¾Í¼ÌÐø¼ÆËã
                directCalc(optStack, numStack, isBracket);
            }
        }
    }
    
    /**
     * ²»¶ªÊ§¾«¶ÈµÄ¶þÔªÔËËã£¬Ö§³Ö¸ß¾«¶È¼ÆËã
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
                resultBigDecimal = bigDecimal1.divide(bigDecimal2, 10, BigDecimal.ROUND_HALF_DOWN); // ×¢Òâ´Ë´¦ÓÃ·¨
                break;
            case "^":
            	if (bigDecimal2.intValue() >= 0) resultBigDecimal = bigDecimal1.pow(bigDecimal2.intValue());
            	else {
            		resultBigDecimal = bigDecimal1.pow((-1)*bigDecimal2.intValue());
            		BigDecimal tmpBigDecimal = new BigDecimal(1);
            		resultBigDecimal = tmpBigDecimal.divide(resultBigDecimal, 10, BigDecimal.ROUND_HALF_DOWN); // ×¢Òâ´Ë´¦ÓÃ·¨
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
     * ÅÐ¶Ï¼ÆËã¹ý³ÌÖÐ!¡¢C¡¢PµÄÊäÈëºÏ·¨ÐÔ
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
    	// ¼¸¸ö²âÊÔÊý¾Ý
    	
//    	Scanner in = new Scanner(System.in);
//      System.out.println(executeExpression(in.nextLine()));
//      in.close();
    }
    
    private ExprCalculator(){};
    
}


