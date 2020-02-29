package utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PropertyUtil {


    /**
     * 读取gradle里面的配置文件
     *
     * @return
     */
    private static List<Property> readGradleProperties() {
        return readProperties(Constant.getGradlePropertiePath());
    }

    /**
     * 读取上传的版本号
     *
     * @return
     */
    private static List<Property> readArtiProperties() {
        return readProperties(Constant.getArtifactoryVersionPath());
    }

    /**
     * 获取本地配置文件
     * 过滤 不符合要求的配置
     *
     * @return
     */
    public static List<Property> readProperties() {
//        List<Property> gradleProperties = readGradleProperties();
        List<Property> artiProperties = readArtiProperties();
//        List<Property> result = new ArrayList<>();
//        for (Property gp : gradleProperties) {
//            for (Property ap : artiProperties) {
//                if (gp.getModuleName().equals(ap.getModuleName()) && gp.getVersion().equals(ap.getVersion())) {
//                    result.add(gp);
//                }
//            }
//        }
        return artiProperties;
    }


    private static List<Property> readProperties(String path) {
        List<Property> properties = new ArrayList<>();
        Properties util = new Properties();
        File f = new File(path);
        if (f.exists()) {
            try {
                util.load(new FileInputStream(f));
                for (Object key : util.keySet()) {
                    if (key instanceof String) {
                        String value = util.getProperty((String) key);
                        Property property = new Property((String) key, value);
                        if (property.isValid()) {
                            properties.add(property);
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
        return properties;
    }


    public static Config.State readState() {

        Properties properties = new Properties();
        File f = new File(Constant.getGradlePropertiePath());
        Config.State state = new Config.State();
        if (f.exists()) {
            try {
                properties.load(new FileInputStream(f));
                state.user = properties.getProperty(Constant.artifactory_user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return state;
    }


    public static Property findProperty(String module, String path) {
        List<Property> list = PropertyUtil.readProperties(path);
        for (Property property : list) {
            System.out.println(property.toString());
            if (property.getModuleName().equals(module)) {
                return property;
            }
        }
        return null;
    }

    /**
     * 修改某个model的版本号
     */
    private static boolean replaceProperties(String path, Property property, boolean suffix) {
        File f = new File(path);
        if (f.exists()) {
            try {
                StringBuilder stringBuffer = new StringBuilder();
                FileInputStream inputStream = new FileInputStream(f);
                InputStreamReader ir = new InputStreamReader(inputStream);
                LineNumberReader input = new LineNumberReader(ir);
                String line;
                while ((line = input.readLine()) != null) {
//                    line = line.replace(" ", "");
                    if (!line.startsWith("#")) {
                        String result[] = line.split("=");
                        if (result.length == 2) {
                            result[0] = result[0].replace(" ", "");
                            result[1] = result[1].replace(" ", "");
                            if (result[0].equals(property.getModuleName())) {//首先匹配是否要修改
                                line = property.toString(suffix);
                            }
                        }
                    }
                    stringBuffer.append(line).append("\n");
                }
                input.close();
                ir.close();
                inputStream.close();

                FileWriter writer = new FileWriter(f);
                BufferedWriter bw = new BufferedWriter(writer);
                bw.write(stringBuffer.toString());
                bw.close();
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    public static Property updateProperty(boolean deployRelease, Property property, String logStr){
        Property artiProperty = findProperty(property.getModuleName(), Constant.getArtifactoryVersionPath());
        if (artiProperty == null || !artiProperty.getVersion().equals(property.getVersion())) {//如果没有配置或者版本号不一致,那么不做处理
            return property;
        }
        Property upgradeProperty = new Property(property.getModuleName(), property.getVersion());
        upgradeProperty.setLog(logStr);
        //升级版本号
        upgradeProperty.setVersion(upgradeProperty.getUpgradeVersion(deployRelease));
        return upgradeProperty;
    }
    /**
     * 生成上传的日志
     */
    private static void generateLog(Property property) {
        try {
            File f = new File(Constant.getModuleLogPath(property.getModuleName(), property.getVersion()));
            f.delete();
            FileWriter writer = new FileWriter(f);
            BufferedWriter bw = new BufferedWriter(writer);
            bw.write(property.getLog());
            bw.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void writFile(File file, String content) throws IOException {
        FileWriter writer = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(writer);
        bw.write(content.toString());
        bw.close();
        writer.close();
    }


    /**
     * 升级结束
     *
     * @param success
     * @param property
     * @param upgradeProperty
     */
    public static void updateEnd(boolean success, Property property, Property upgradeProperty) {
        File bg = new File(Constant.getBuildGradlePath());
        File tbg = new File(Constant.getTempBuildGradlePath());
        File log = new File(Constant.getModuleLogPath(property.getModuleName(), property.getVersion()));
        File logUp = new File(Constant.getModuleLogPath(upgradeProperty.getModuleName(), upgradeProperty.getVersion()));
        if (log.exists()) {
            System.out.println("删除文件 :"+log.getAbsolutePath());
            log.delete();
        }
        if (logUp.exists()) {
            System.out.println("删除文件 :"+logUp.getAbsolutePath());
            logUp.delete();
        }
        if (tbg.exists()) {
            System.out.println("删除文件 :"+bg.getAbsolutePath());
            bg.delete();
            tbg.renameTo(bg);
        }
        File afg = new File(Constant.getArtiFactoryGradlePath());
        File logafg = new File(Constant.getArtiFactoryLogGradlePath());
        if (afg.exists()) {
            System.out.println("删除文件 :"+afg.getAbsolutePath());
            afg.delete();
        }
        if (logafg.exists()) {
            System.out.println("删除文件 :"+logafg.getAbsolutePath());
            logafg.delete();
        }
        if (success) {
            replaceProperties(Constant.getArtifactoryVersionPath(), upgradeProperty, true);
        }
    }


}
