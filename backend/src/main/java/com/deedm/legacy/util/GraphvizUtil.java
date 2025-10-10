package com.deedm.legacy.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Graphviz工具类，用于生成DOT文件到PNG图片
 * @author Deedm Team
 */
public class GraphvizUtil {

    public static String errorMessage = null;

    /**
     * 调用dot命令生成PNG文件
     * @param dotFileName DOT文件路径
     * @param pngFileName PNG输出文件路径
     * @param isTree 是否为树形结构（使用dot布局）
     * @return 是否生成成功
     */
    public static boolean generatePNGFile(String dotFileName, String pngFileName, boolean isTree) {
        final String dotProgram = "dot";
        final String graphFileFormat = "png";
        final String layout = isTree ? "dot" : "dot"; // 对于AST都使用dot布局

        String[] args = { dotProgram, "-K" + layout, "-T" + graphFileFormat, dotFileName, "-o", pngFileName };

        try {
            ProcessBuilder pb = new ProcessBuilder(args);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // 读取输出和错误信息
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                errorMessage = "Graphviz execution failed with exit code " + exitCode + "\nOutput: " + output.toString();
                return false;
            }

            // 检查输出文件是否存在
            if (!Files.exists(Paths.get(pngFileName))) {
                errorMessage = "Output file was not created: " + pngFileName;
                return false;
            }

            return true;

        } catch (InterruptedException exc) {
            Thread.currentThread().interrupt();
            errorMessage = "Graphviz execution was interrupted: " + exc.getMessage();
            return false;
        } catch (IOException exc) {
            errorMessage = "IO error during Graphviz execution: " + exc.getMessage();
            return false;
        }
    }

    /**
     * 检查Graphviz是否可用
     * @return Graphviz是否可用
     */
    public static boolean isGraphvizAvailable() {
        try {
            ProcessBuilder pb = new ProcessBuilder("dot", "-V");
            pb.redirectErrorStream(true);
            Process process = pb.start();
            return process.waitFor() == 0;
        } catch (Exception e) {
            return false;
        }
    }
}