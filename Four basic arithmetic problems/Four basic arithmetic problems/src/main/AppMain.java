package main;

import javax.swing.*;

public class AppMain {
    public static void main(String[] args) {
        // 命令行模式兼容
        if (args.length > 0) {
            runCommandLine(args);
            return;
        }

        // 图形界面模式
        SwingUtilities.invokeLater(() -> {
            try {
                // 使用系统外观
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // 忽略外观设置失败
            }
            new MainFrame().setVisible(true);
        });
    }

    private static void runCommandLine(String[] args) {
        try {
            // 解析 -n 和 -r 参数
            Integer n = null;
            Integer r = null;
            String eFile = null;
            String aFile = null;

            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "-n":
                        n = Integer.parseInt(args[++i]);
                        break;
                    case "-r":
                        r = Integer.parseInt(args[++i]);
                        break;
                    case "-e":
                        eFile = args[++i];
                        break;
                    case "-a":
                        aFile = args[++i];
                        break;
                }
            }

            // 批改模式
            if (eFile != null && aFile != null) {
                AnswerGrader grader = new AnswerGrader(eFile, aFile);
                grader.grade();
                System.out.println("批改完成，结果已保存到 Grade.txt");
                return;
            }

            // 生成模式
            if (r == null) {
                System.out.println("错误：必须指定 -r 参数（数值范围）");
                printHelp();
                System.exit(1);
            }

            int count = (n != null) ? n : 10;
            ProblemGenerator generator = new ProblemGenerator(count, r);
            generator.generate();
            System.out.println("生成完成！");
            System.out.println("题目: Exercises.txt");
            System.out.println("答案: Answers.txt");

        } catch (Exception e) {
            System.out.println("运行错误: " + e.getMessage());
            printHelp();
        }
    }

    private static void printHelp() {
        System.out.println("用法:");
        System.out.println("  生成题目: java -jar App.jar -n 数量 -r 范围");
        System.out.println("  批改作业: java -jar App.jar -e 题目文件 -a 答案文件");
        System.out.println("  图形界面: java -jar App.jar (无参数)");
    }
}
