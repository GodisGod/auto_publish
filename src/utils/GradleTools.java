package utils;

import org.apache.http.util.TextUtils;

import javax.swing.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class GradleTools {

    private static GradleTools INSTANCE = new GradleTools();


    public static GradleTools instance() {
        return INSTANCE;
    }

    //存储每个线程的process
    private Map<Long, Process> processMap = new HashMap<>();
    //存储每个线程的status -1 禁用，0 正常
    private Map<Long, Integer> processStatus = new HashMap<>();

    public String getPlatformWithGradle() {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            return "gradlew ";
        }
        return "./gradlew ";
    }

    public String getC() {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            return "/C ";
        }
        return "-c";
    }

    public String getCommand() {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            return "cmd";
        }
        return "/bin/sh";
    }

    public boolean isMac() {
        String os = System.getProperty("os.name");
        return !os.toLowerCase().startsWith("win");
    }

    public String getCD() {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            return String.format("cd /d %s && ", Constant.PROJECT_PATH);
        }
        return String.format("cd %s && ", Constant.PROJECT_PATH);
    }

    /**
     * @param type      发布的版本
     * @param modelName 发布的module
     * @return
     */
    public ExecuteResult deploy(PUBLISH_TYPE type, String modelName, String logStr) {
        deployStart();
        processStatus.put(Thread.currentThread().getId(), 1);
//        Config.State state = Config.getInstance().getState();

        if (type == PUBLISH_TYPE.RELEASE && TextUtils.isEmpty(logStr)) {//校验版本日志
            log("正式版发布必须填写版本日志");
            deployEnd();
            processStatus.put(Thread.currentThread().getId(), 0);
            return new ExecuteResult(1, "", "请填写版本日志");
        }
//        logStr = String.format("[版本发布者:%s]\n\n%s", state.user, logStr);
        log("[启动打包流程]...");

        //todo
        String clean = ":%s:clean";
        String assembleReleaseAar = ":%s:assembleRelease";
        String assembleDebugAar = ":%s:assembleDebug";

        //1、clean
        ExecuteResult executeResult = execute(String.format(clean, modelName));//首先clean

        //2、debug,release.channel
        if (executeResult.isSuccess()) {//clean成功
            //打包apk
            if (type == PUBLISH_TYPE.DEBUG) {
                executeResult = execute(String.format(assembleDebugAar, modelName));//然后打包aar
            } else if (type == PUBLISH_TYPE.RELEASE) {
                executeResult = execute(String.format(assembleReleaseAar, modelName));//然后打包aar
            } else {

            }

            if (executeResult.isSuccess()) {
                //todo 打开文件夹
            }
        }

        if (!executeResult.isSuccess()) {
            if (processStatus.get(Thread.currentThread().getId()) < 0) {
                log("\n\n[---用户取消---]\n\n");
            }
        } else {
            log("\n\n[---执行成功---]\n\n");
        }

        deployEnd();
        processStatus.put(Thread.currentThread().getId(), 0);
        return executeResult;
    }

    public void log(String log) {
        if (executeListener != null) {
            executeListener.onExecute(log);
        }
    }

    private void deployEnd() {
        if (executeListener != null) {
            executeListener.onExecuteEnd();
        }
    }

    private void deployStart() {
        if (executeListener != null) {
            executeListener.onExecuteStart();
        }
    }

    public ExecuteResult execute(String command) {
        try {
            if (processStatus.get(Thread.currentThread().getId()) < 0) {

                return new ExecuteResult(1, "用户取消", "用户取消");
            }
            Process process = null;
            if (isMac()) {
                String cmds[] = new String[]{getCommand(), getC(), getCD() + getPlatformWithGradle() + command};
                process = Runtime.getRuntime().exec(cmds);

            } else {
                String cmd = getCommand() + " " + getC() + getCD() + getPlatformWithGradle() + command;
                process = Runtime.getRuntime().exec(cmd);
            }
            if (executeListener != null) {
//                executeListener.onExecute("\nExecute Full Command \n: [" + cmd+ "]\n");
                executeListener.onExecute("\nExecute Command \n: [" + command + "]\n");
            }

            processMap.put(Thread.currentThread().getId(), process);
            String msg = readStream(process.getInputStream(), executeListener);
            String error = readStream(process.getErrorStream(), executeListener);
            int result = process.waitFor();
            processMap.remove(Thread.currentThread().getId());
            return new ExecuteResult(result, msg, error);
        } catch (Exception e) {
            if (executeListener != null) {
                executeListener.onExecute(e.getMessage());
            }
            processMap.remove(Thread.currentThread().getId());
            return new ExecuteResult(1, "", e.getMessage());
        }
    }

    public String readStream(InputStream inputStream, ExecuteListener executeListener) {
        try {
            InputStreamReader ir = new InputStreamReader(inputStream);
            StringBuffer sb = new StringBuffer();

            LineNumberReader reader = new LineNumberReader(ir);

            Thread execThread = new Thread() {
                @Override
                public void run() {
                    try {

                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (executeListener != null) {
                                executeListener.onExecute(line);
                            }

                            sb.append(line);
                            sb.append("\n");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            execThread.start();
            execThread.join();

            return sb.toString();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    /**
     * 强行停止一次gradle任务
     *
     * @param key 停止线程的id
     */
    public void stopGradle(long key) {
        //停止某个线程的状态
        processStatus.put(key, -1);
        Process process = processMap.get(key);
        if (process != null) {
            process.destroy();
        }
    }

    public void stopAll() {
        for (long key : processStatus.keySet()) {
            stopGradle(key);
        }
    }

    private ExecuteListener executeListener;

    public void setExecuteListener(ExecuteListener executeListener) {
        this.executeListener = executeListener;
    }

    public interface ExecuteListener {
        void onExecuteStart();

        void onExecute(String line);

        void onExecuteEnd();
    }

    public static void main(String[] args) {
//        Constant.setProjectPath("/Users/liutao/longzhu/MyApplication2");
        //D:\APPHELPER\MyApplication
        //E:\sina\FinanceAppAndroid\SinaFinance
        Constant.setProjectPath("/Users/Administrator/IntelliJIDEAProjects/MyApplication");

        ExecuteResult result = GradleTools.instance().deploy(PUBLISH_TYPE.DEBUG, "app", "测试");
        System.out.println(result.getResult() + "");
        System.out.println(result.getMsg() + "");
        System.out.println(result.getErrorMsg() + "");
//        Property property = new Property("demo", "1.0.2");
//        PropertyUtil.replaceProperties(property);
    }

    public enum PUBLISH_TYPE {
        DEBUG,
        RELEASE,
        CHANNEL
    }

}
