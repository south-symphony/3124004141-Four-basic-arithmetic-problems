package main;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 作业批改器：读取题目文件和答案文件，计算正确答案并比对
 */
public class AnswerGrader {
    private final String exerciseFile;
    private final String answerFile;

    public AnswerGrader(String exerciseFile, String answerFile) {
        this.exerciseFile = exerciseFile;
        this.answerFile = answerFile;
    }

    public void grade() throws IOException {
        List<String> exercises = readLines(exerciseFile);
        List<String> userAnswers = readLines(answerFile);

        List<Integer> correct = new ArrayList<>();
        List<Integer> wrong = new ArrayList<>();

        for (int i = 0; i < exercises.size(); i++) {
            String exprLine = exercises.get(i).trim();
            String ansLine = userAnswers.get(i).trim();

            // ========== 修改：去掉题号前缀 ==========
            int dotIndex = exprLine.indexOf('、');
            if (dotIndex != -1) {
                exprLine = exprLine.substring(dotIndex + 1).trim();
            }
            // 去掉末尾的等号
            if (exprLine.endsWith("=")) {
                exprLine = exprLine.substring(0, exprLine.length() - 1).trim();
            }

            // 去掉答案的题号前缀
            dotIndex = ansLine.indexOf('、');
            if (dotIndex != -1) {
                ansLine = ansLine.substring(dotIndex + 1).trim();
            }

            Fraction correctAnswer = calculate(exprLine);
            Fraction userAnswer = parseFraction(ansLine);

            if (correctAnswer.equals(userAnswer)) {
                correct.add(i + 1);
            } else {
                wrong.add(i + 1);
            }
        }

        writeGradeResult(correct, wrong);
    }

    // 计算表达式的值
    private Fraction calculate(String expr) {
        // 简单的表达式求值：使用双栈法
        Stack<Fraction> numStack = new Stack<>();
        Stack<Character> opStack = new Stack<>();

        int i = 0;
        char[] chars = expr.toCharArray();

        while (i < chars.length) {
            char c = chars[i];

            if (c == ' ') {
                i++;
                continue;
            }

            if (c == '(') {
                opStack.push(c);
                i++;
            } else if (c == ')') {
                while (opStack.peek() != '(') {
                    computeTop(numStack, opStack);
                }
                opStack.pop(); // 弹出'('
                i++;
            } else if (isOperator(c)) {
                while (!opStack.isEmpty() && opStack.peek() != '('
                        && priority(opStack.peek()) >= priority(c)) {
                    computeTop(numStack, opStack);
                }
                opStack.push(c);
                i++;
            } else {
                // 读取数字或分数
                int j = i;
                while (j < chars.length && chars[j] != ' ' && chars[j] != ')') {
                    j++;
                }
                String numStr = new String(chars, i, j - i);
                numStack.push(parseFraction(numStr));
                i = j;
            }
        }

        while (!opStack.isEmpty()) {
            computeTop(numStack, opStack);
        }

        return numStack.pop();
    }

    private void computeTop(Stack<Fraction> numStack, Stack<Character> opStack) {
        Fraction b = numStack.pop();
        Fraction a = numStack.pop();
        char op = opStack.pop();

        switch (op) {
            case '+': numStack.push(a.add(b)); break;
            case '-': numStack.push(a.subtract(b)); break;
            case '×': numStack.push(a.multiply(b)); break;
            case '÷': numStack.push(a.divide(b)); break;
        }
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '×' || c == '÷';
    }

    private int priority(char op) {
        switch (op) {
            case '+': case '-': return 1;
            case '×': case '÷': return 2;
            default: return 0;
        }
    }

    // 解析分数字符串
    private Fraction parseFraction(String s) {
        if (s.contains("'")) {
            // 带分数
            String[] parts = s.split("'");
            long integer = Long.parseLong(parts[0]);
            String[] frac = parts[1].split("/");
            long num = Long.parseLong(frac[0]);
            long den = Long.parseLong(frac[1]);
            return new Fraction(integer * den + num, den);
        } else if (s.contains("/")) {
            String[] frac = s.split("/");
            long num = Long.parseLong(frac[0]);
            long den = Long.parseLong(frac[1]);
            return new Fraction(num, den);
        } else {
            return new Fraction(Long.parseLong(s));
        }
    }

    private List<String> readLines(String filename) throws IOException {
        List<String> lines = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
        String line;
        while ((line = reader.readLine()) != null) {
            if (!line.trim().isEmpty()) {
                lines.add(line);
            }
        }
        reader.close();
        return lines;
    }

    private void writeGradeResult(List<Integer> correct, List<Integer> wrong) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("Grade.txt"), "UTF-8"));

        writer.write("Correct: " + correct.size() + " (" + listToString(correct) + ")");
        writer.newLine();
        writer.write("Wrong: " + wrong.size() + " (" + listToString(wrong) + ")");

        writer.close();
    }

    private String listToString(List<Integer> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(list.get(i));
        }
        return sb.toString();
    }
}
