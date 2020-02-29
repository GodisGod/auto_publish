package utils;

import java.util.HashMap;
import java.util.Map;

public class Constant {
    public static int SUCCESS = 0;
    public static int FAIL = 1;

    public static String PROJECT_PATH;
    public static String SNAPSHOT = "snapshot";
    public static String RELEASE = "release";

    public static String maven_groupId = "maven_groupId";
    public static String artifactory_contextUrl = "artifactory_contextUrl";
    public static String artifactory_release_repoKey = "artifactory_release_repoKey";
    public static String artifactory_snapshot_repoKey = "artifactory_snapshot_repoKey";
    public static String artifactory_user = "artifactory_user";
    public static String artifactory_password = "artifactory_password";

    public interface AppCode {
        int COMMON = 10;//通用
    }
    private static Map<Integer,Boolean> AppCodeMap=new HashMap<>();

   static  {
        AppCodeMap.put(AppCode.COMMON,true);
    }


    public static boolean checkCode(int code){
       return AppCodeMap.get(code);
    }

    public static void setProjectPath(String projectPath) {
        Constant.PROJECT_PATH = projectPath;
    }

    public static String getGradlePropertiePath() {
        return Constant.PROJECT_PATH + "/gradle.properties";
    }

    public static String getArtifactoryVersionPath() {
        return Constant.PROJECT_PATH + "/artifactory_version.properties";
    }

    public static String getArtiFactoryGradlePath() {
        return Constant.PROJECT_PATH + "/artifactory.gradle";
    }
   public static String getArtiFactoryLogGradlePath() {
        return Constant.PROJECT_PATH + "/log_artifactory.gradle";
    }

    public static String getModuleLogPath(String module, String version) {
        return String.format(Constant.PROJECT_PATH + "/%s/%s_log.log", module, version);
    }

    public static String getBuildGradlePath() {
        return Constant.PROJECT_PATH + "/build.gradle";
    }

    public static String getTempBuildGradlePath() {
        return Constant.PROJECT_PATH + "/.build.gradle";
    }
}
