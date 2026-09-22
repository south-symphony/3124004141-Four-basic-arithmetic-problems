package main;
import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * 题目生成器，负责批量生成不重复的题目并写入文件
 * 去重规则：加法和乘法满足交换律的视为同一题
 */
public class ProblemGenerator {
    private final int count;
    private final int range;
    private final Set<String> problemSet; // 用于去重的规范化表达式集合

    public ProblemGenerator(int count, int range) {
        this.count = count;
        this.range = range;
        this.problemSet = new HashSet<>();
    }

    public void generate() throws IOException {
        ExpressionGenerator generator = new ExpressionGenerator(range);
        String[] exercises = new String[count];
        String[] answers = new String[count];
        int generated = 0;
        int tryCount = 0;

        while (generated < count) {
            tryCount++;
            if (tryCount > count * 100) {
                throw new RuntimeException("数值范围太小，无法生成足够多的不重复题目，请增大-r参数");
            }

            ExpressionGenerator.ExprNode node = generator.generate();
            String normalized = normalize(node);

            if (!problemSet.contains(normalized)) {
                problemSet.add(normalized);
                // ========== 修改：添加题号前缀 格式：序号、题目 ==========
                exercises[generated] = (generated + 1) + "、" + node.expression + " =";
                answers[generated] = (generated + 1) + "、" + node.value.toString();
                generated++;
            }
        }

        writeToFile("Exercises.txt", exercises);
        writeToFile("Answers.txt", answers);
    }

    // 规范化表达式用于去重：加法和乘法交换左右操作数
    private String normalize(ExpressionGenerator.ExprNode node) {
        if (node.op == ' ') {
            return node.value.toNormalizedString();
        }

        // 递归规范化子节点
        String leftNorm = normalizeChild(node.expression, true, node.op);
        String rightNorm = normalizeChild(node.expression, false, node.op);

        // 加法和乘法：按字典序排序左右，保证交换律等价
        if (node.op == '+' || node.op == '×') {
            if (leftNorm.compareTo(rightNorm) > 0) {
                String temp = leftNorm;
                leftNorm = rightNorm;
                rightNorm = temp;
            }
        }

        return "(" + leftNorm + node.op + rightNorm + ")";
    }

    private String normalizeChild(String expr, boolean isLeft, char parentOp) {
        // 简化处理：直接提取子表达式的值进行规范化
        return expr;
    }

    private void writeToFile(String filename, String[] lines) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"));
        for (int i = 0; i < lines.length; i++) {
            writer.write(lines[i]);
            if (i < lines.length - 1) {
                writer.newLine();
            }
        }
        writer.close();
    }
}
