import java.io.*;
import java.net.URL;

public class Main {

    public static void main(String[] args) {
        String jarUrl = "https://static.longfish.site/gomoku/oop-java-1.0.1.jar";
        File tempJar = null;

        try {
            tempJar = File.createTempFile("tempJar", ".jar");
            tempJar.deleteOnExit(); // 确保程序结束时删除文件
            downloadJar(jarUrl, tempJar);
            runJarWithSystemCommand(tempJar);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 删除临时文件（如果 deleteOnExit 不生效）
            if (tempJar != null && tempJar.exists()) {
                boolean d = tempJar.delete();
                if (d) System.out.println("bye");
            }
        }
    }

    /**
     * 下载 JAR 包到临时文件
     *
     * @param jarUrl    JAR 包的 URL
     * @param outputJar 输出的临时文件
     * @throws IOException ex
     */
    private static void downloadJar(String jarUrl, File outputJar) throws IOException {
        try (InputStream in = new URL(jarUrl).openStream();
             OutputStream out = new FileOutputStream(outputJar)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }

    /**
     * 使用系统命令运行 JAR 包
     *
     * @param jarFile JAR 文件路径
     * @throws IOException ex
     */
    private static void runJarWithSystemCommand(File jarFile) throws IOException {
        // 构造命令
        String javaExecutable = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        String command = String.format("%s -jar %s", javaExecutable, jarFile.getAbsolutePath());

        // 使用 ProcessBuilder 启动
        ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
        processBuilder.redirectErrorStream(true); // 合并错误流到标准输出流
        Process process = processBuilder.start();

        // 打印运行输出
        try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }

        // 等待进程结束
        try {
            int exitCode = process.waitFor();
            System.out.println("process exited with code: " + exitCode);
        } catch (InterruptedException e) {
            System.err.println("execution interrupted.");
            Thread.currentThread().interrupt();
        }
    }
}
