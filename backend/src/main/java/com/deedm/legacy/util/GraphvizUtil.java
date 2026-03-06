package com.deedm.legacy.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Graphviz工具类，用于生成DOT文件到PNG图片
 * @author Deedm Team
 */
public class GraphvizUtil {

    public static String errorMessage = null;
    private static volatile CommandAuditHook commandAuditHook;

    public interface CommandAuditHook {
        void onCommandExecuted(String command, String args, int exitCode, long durationMs, String error);
    }

    public static void setCommandAuditHook(CommandAuditHook hook) {
        commandAuditHook = hook;
    }

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
        long start = System.currentTimeMillis();

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
            long durationMs = System.currentTimeMillis() - start;
            if (exitCode != 0) {
                errorMessage = "Graphviz execution failed with exit code " + exitCode + "\nOutput: " + output.toString();
                notifyCommandAudit(dotProgram, String.join(" ", args), exitCode, durationMs, errorMessage);
                return false;
            }

            // 检查输出文件是否存在
            if (!Files.exists(Paths.get(pngFileName))) {
                errorMessage = "Output file was not created: " + pngFileName;
                notifyCommandAudit(dotProgram, String.join(" ", args), exitCode, durationMs, errorMessage);
                return false;
            }

            notifyCommandAudit(dotProgram, String.join(" ", args), exitCode, durationMs, null);
            return true;

        } catch (InterruptedException exc) {
            Thread.currentThread().interrupt();
            errorMessage = "Graphviz execution was interrupted: " + exc.getMessage();
            notifyCommandAudit(dotProgram, String.join(" ", args), -1, System.currentTimeMillis() - start, errorMessage);
            return false;
        } catch (IOException exc) {
            errorMessage = "IO error during Graphviz execution: " + exc.getMessage();
            notifyCommandAudit(dotProgram, String.join(" ", args), -1, System.currentTimeMillis() - start, errorMessage);
            return false;
        }
    }

    /**
     * 检查Graphviz是否可用
     * @return Graphviz是否可用
     */
    public static boolean isGraphvizAvailable() {
        long start = System.currentTimeMillis();
        try {
            ProcessBuilder pb = new ProcessBuilder("dot", "-V");
            pb.redirectErrorStream(true);
            Process process = pb.start();
            int exitCode = process.waitFor();
            notifyCommandAudit("dot", "dot -V", exitCode, System.currentTimeMillis() - start, exitCode == 0 ? null : "dot -V failed");
            return exitCode == 0;
        } catch (Exception e) {
            notifyCommandAudit("dot", "dot -V", -1, System.currentTimeMillis() - start, e.getMessage());
            return false;
        }
    }

    private static void notifyCommandAudit(String command, String args, int exitCode, long durationMs, String error) {
        CommandAuditHook hook = commandAuditHook;
        if (hook == null) {
            return;
        }
        try {
            hook.onCommandExecuted(command, args, exitCode, durationMs, error);
        } catch (Exception ignored) {
            // 审计失败不影响主流程
        }
    }
}
