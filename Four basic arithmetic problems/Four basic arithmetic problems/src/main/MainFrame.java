package main;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class MainFrame extends JFrame {
    private JTextField countField;
    private JTextField rangeField;
    private JTextField exerciseFileField;
    private JTextField answerFileField;
    private JTextArea logArea;

    public MainFrame() {
        setTitle("小学四则运算题目生成器");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 生成题目面板
        JPanel generatePanel = new JPanel();
        generatePanel.setBorder(BorderFactory.createTitledBorder("生成题目"));
        generatePanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        generatePanel.add(new JLabel("题目数量 (-n):"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        countField = new JTextField(10);
        countField.setText("10");
        generatePanel.add(countField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        generatePanel.add(new JLabel("数值范围 (-r):"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        rangeField = new JTextField(10);
        rangeField.setText("10");
        generatePanel.add(rangeField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton generateBtn = new JButton("生成题目");
        generateBtn.setPreferredSize(new Dimension(120, 30));
        generateBtn.addActionListener(e -> generateProblems());
        generatePanel.add(generateBtn, gbc);

        // 查看题目、查看答案按钮
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        JButton viewExeBtn = new JButton("查看题目");
        viewExeBtn.setPreferredSize(new Dimension(100, 25));
        viewExeBtn.addActionListener(e -> viewExerciseFile());
        generatePanel.add(viewExeBtn, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        JButton viewAnsBtn = new JButton("查看答案");
        viewAnsBtn.setPreferredSize(new Dimension(100, 25));
        viewAnsBtn.addActionListener(e -> viewAnswerFile());
        generatePanel.add(viewAnsBtn, gbc);

        // 批改作业面板
        JPanel gradePanel = new JPanel();
        gradePanel.setBorder(BorderFactory.createTitledBorder("批改作业"));
        gradePanel.setLayout(new GridBagLayout());

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        gradePanel.add(new JLabel("题目文件 (-e):"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        exerciseFileField = new JTextField(15);
        exerciseFileField.setText("Exercises.txt");
        gradePanel.add(exerciseFileField, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        JButton browseExeBtn = new JButton("浏览...");
        browseExeBtn.addActionListener(e -> browseFile(exerciseFileField));
        gradePanel.add(browseExeBtn, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gradePanel.add(new JLabel("答案文件 (-a):"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        answerFileField = new JTextField(15);
        answerFileField.setText("Answers.txt");
        gradePanel.add(answerFileField, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        JButton browseAnsBtn = new JButton("浏览...");
        browseAnsBtn.addActionListener(e -> browseFile(answerFileField));
        gradePanel.add(browseAnsBtn, gbc);

        // 按钮行：开始批改 + 查看Grade结果
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton gradeBtn = new JButton("开始批改");
        gradeBtn.setPreferredSize(new Dimension(100, 30));
        gradeBtn.addActionListener(e -> gradeAnswers());
        gradePanel.add(gradeBtn, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        gbc.gridwidth = 2;
        JButton viewGradeBtn = new JButton("查看Grade结果");
        viewGradeBtn.setPreferredSize(new Dimension(120, 30));
        viewGradeBtn.addActionListener(e -> viewGradeFile());
        gradePanel.add(viewGradeBtn, gbc);

        // 日志区域
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("运行日志 / 文件预览"));
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setPreferredSize(new Dimension(550, 180));
        logPanel.add(scrollPane, BorderLayout.CENTER);

        // 组装
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        topPanel.add(generatePanel);
        topPanel.add(gradePanel);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(logPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void generateProblems() {
        try {
            int count = Integer.parseInt(countField.getText().trim());
            int range = Integer.parseInt(rangeField.getText().trim());

            if (count <= 0) {
                JOptionPane.showMessageDialog(this, "题目数量必须大于0", "参数错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (range <= 0) {
                JOptionPane.showMessageDialog(this, "数值范围必须大于0", "参数错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            log("开始生成 " + count + " 道题目，数值范围: 0 ~ " + (range - 1));

            ProblemGenerator generator = new ProblemGenerator(count, range);
            generator.generate();

            log("生成完成！");
            log("题目已保存至: Exercises.txt");
            log("答案已保存至: Answers.txt");
            log("文件位置: " + new File(".").getAbsolutePath());

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "请输入有效的数字", "格式错误", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            log("生成失败: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "生成失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void gradeAnswers() {
        try {
            String exeFile = exerciseFileField.getText().trim();
            String ansFile = answerFileField.getText().trim();

            if (exeFile.isEmpty() || ansFile.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请选择题目文件和答案文件", "参数错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            log("开始批改...");
            log("题目文件: " + exeFile);
            log("答案文件: " + ansFile);

            AnswerGrader grader = new AnswerGrader(exeFile, ansFile);
            grader.grade();

            log("批改完成！");
            log("结果已保存至: Grade.txt");

        } catch (IOException e) {
            log("批改失败: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "批改失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 查看 Exercises.txt
    private void viewExerciseFile() {
        viewTextFile("Exercises.txt", "题目列表");
    }

    // 查看 Answers.txt
    private void viewAnswerFile() {
        viewTextFile("Answers.txt", "答案列表");
    }

    // 查看 Grade.txt
    private void viewGradeFile() {
        viewTextFile("Grade.txt", "批改结果");
    }

    // 通用文本文件读取方法（已修改：直接显示原文，不额外加行号）
    private void viewTextFile(String fileName, String title) {
        File file = new File(fileName);
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this,
                    fileName + " 文件不存在，请先执行对应操作",
                    "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), "UTF-8"))) {

            log("========== " + title + " ==========");
            String line;
            while ((line = reader.readLine()) != null) {
                // 直接显示文件原始内容，文件本身已自带题号
                log(line);
            }
            log("==============================");

        } catch (IOException e) {
            log("读取 " + fileName + " 失败: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "读取失败: " + e.getMessage(),
                    "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void browseFile(JTextField textField) {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("."));
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            textField.setText(chooser.getSelectedFile().getName());
        }
    }

    private void log(String msg) {
        logArea.append(msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}
