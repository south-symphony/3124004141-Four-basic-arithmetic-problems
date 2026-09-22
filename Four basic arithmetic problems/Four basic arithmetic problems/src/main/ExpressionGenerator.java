package main;

import java.util.Random;

/**
 * 递归生成四则运算表达式，控制运算符数量不超过3个
 * 保证减法不出现负数，除法结果为真分数
 */
public class ExpressionGenerator {
    private final int range;      // 数值范围
    private final Random random;
    private final int maxOps;     // 最大运算符数

    public ExpressionGenerator(int range) {
        if (range < 1) {
            throw new IllegalArgumentException("数值范围必须大于等于1");
        }
        this.range = range;
        this.random = new Random();
        this.maxOps = 3;
    }

    // 生成一个表达式节点，返回表达式字符串和对应的值
    public ExprNode generate() {
        return generateExpression(maxOps);
    }

    private ExprNode generateExpression(int remainingOps) {
        if (remainingOps == 0 || (random.nextBoolean() && remainingOps < maxOps)) {
            // 生成操作数
            return generateOperand();
        }

        // 随机选择运算符
        int opIndex = random.nextInt(4);
        char op = "+-×÷".charAt(opIndex);

        // 左右子表达式分配运算符数量
        int leftOps = random.nextInt(remainingOps);
        int rightOps = remainingOps - 1 - leftOps;

        ExprNode left = generateExpression(leftOps);
        ExprNode right = generateExpression(rightOps);

        Fraction result;
        switch (op) {
            case '+':
                result = left.value.add(right.value);
                break;
            case '-':
                // 保证减法结果非负
                if (!left.value.isGreaterOrEqual(right.value)) {
                    // 交换左右
                    ExprNode temp = left;
                    left = right;
                    right = temp;
                }
                result = left.value.subtract(right.value);
                break;
            case '×':
                result = left.value.multiply(right.value);
                break;
            case '÷':
                // 保证除数不为0且结果为真分数，最多重试20次
                int tryCount = 0;
                boolean divideOk = false;
                while (tryCount < 20) {
                    boolean valid = right.value.getNumerator() != 0
                            && left.value.divide(right.value).isProperFraction();
                    if (valid) {
                        divideOk = true;
                        break;
                    }
                    right = generateOperand();
                    tryCount++;
                }

                if (divideOk) {
                    result = left.value.divide(right.value);
                } else {
                    // 无法生成合法除法，降级为加法
                    op = '+';
                    result = left.value.add(right.value);
                }
                break;
            default:
                throw new IllegalStateException("未知运算符");
        }

        // 【修复】去掉 remainingOps < maxOps 的错误限制，严格按优先级判断是否加括号
        String leftExpr = left.expression;
        String rightExpr = right.expression;

        if (needParentheses(op, left.op, true)) {
            leftExpr = "(" + left.expression + ")";
        }
        if (needParentheses(op, right.op, false)) {
            rightExpr = "(" + right.expression + ")";
        }

        String expr = leftExpr + " " + op + " " + rightExpr;
        return new ExprNode(expr, result, op);
    }

    /**
     * 判断子表达式是否需要加括号
     * @param outerOp 外层运算符
     * @param innerOp 内层（子表达式）运算符
     * @param isLeft 是否是左子表达式
     */
    private boolean needParentheses(char outerOp, char innerOp, boolean isLeft) {
        // 操作数（没有运算符）不需要括号
        if (innerOp == ' ') return false;

        int outerPriority = getPriority(outerOp);
        int innerPriority = getPriority(innerOp);

        // 内层优先级更低，必须加括号
        if (innerPriority < outerPriority) {
            return true;
        }

        // 优先级相等的情况（左结合运算符）
        if (innerPriority == outerPriority) {
            // 减法和除法：右边的子式必须加括号（左结合，右结合会改变结果）
            // 加法和乘法：满足结合律，左右都不用加括号
            if (!isLeft && (outerOp == '-' || outerOp == '÷')) {
                return true;
            }
        }

        return false;
    }

    private int getPriority(char op) {
        switch (op) {
            case '+': case '-': return 1;
            case '×': case '÷': return 2;
            default: return 0;
        }
    }

    // 生成操作数（自然数或真分数）
    private ExprNode generateOperand() {
        // 范围小于2时，只能生成自然数，无法生成真分数
        if (range < 2 || random.nextBoolean()) {
            // 自然数：0 ~ range-1
            long num = Math.abs(random.nextInt(range));
            Fraction f = new Fraction(num);
            return new ExprNode(String.valueOf(num), f, ' ');
        } else {
            // 真分数：分母范围 2 ~ range-1
            int denominatorBound = range - 1;
            // 分母至少为2
            long denominator = random.nextInt(denominatorBound - 1) + 2;
            // 分子范围 1 ~ denominator-1
            long numerator = random.nextInt((int) (denominator - 1)) + 1;

            // 30%概率生成带分数（范围足够大时）
            if (random.nextDouble() < 0.3 && range > 2) {
                long integer = random.nextInt(range - 1) + 1;
                numerator = integer * denominator + numerator;
            }

            Fraction f = new Fraction(numerator, denominator);
            return new ExprNode(f.toString(), f, ' ');
        }
    }

    // 表达式节点：包含表达式字符串、值、根运算符
    public static class ExprNode {
        public String expression;
        public Fraction value;
        public char op; // 根运算符，操作数为' '

        public ExprNode(String expression, Fraction value, char op) {
            this.expression = expression;
            this.value = value;
            this.op = op;
        }
    }
}
