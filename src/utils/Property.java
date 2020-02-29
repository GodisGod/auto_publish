package utils;

import org.apache.http.util.TextUtils;

public class Property {
    private String moduleName;
    private String version;
    private String log;

    public Property(String moduleName, String version) {
        this.moduleName = moduleName;
        this.version = version;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getLog() {
        return log;
    }


    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }



    public boolean isRelease() {
        if (!isValid()) {
            return true;
        }
        String codes[] = version.split("\\.");
        return codes[1].equals("1");
    }

    public int getAppCode(){
        if (!isValid()) {
            return -1;
        }
        String codes[] = version.split("\\.");
        return Integer.parseInt(codes[0]);
    }


    /**
     * 截取版本号的几位
     * @param start true 从前面开始截取
     * @param num 位数
     * @return
     */
    public int versionCode(boolean start,int num){
        if(num>5){
            num=5;
        }
        if(!isValid()){
            return -1;
        }
        String code="-1";
        String resultVersion[]= version.split("\\.");
        for (int i=0;i<num;i++){
            if(!start){
                code+=resultVersion[i];
            }else{
                code+=resultVersion[resultVersion.length-i-1];
            }
        }
//        if(!start){
//             code = resultVersion.substring(resultVersion.length()-num,resultVersion.length());
//        }else{
//            code = resultVersion.substring(0,num);
//        }
        return Integer.parseInt(code);
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getFullVersion() {

        return version;
    }


    /**
     * 返回升级之后的版本号
     *
     * @return
     */
    public String getUpgradeVersion(boolean release) {
        if (!isValid()) {
            return version;
        }
        String codes[] = version.split("\\.");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0, len = codes.length; i < len; i++) {
            int code = Integer.parseInt(codes[i]);
            if (i == 1) {
                code = release ? 1 : 0;
            }
            if (i == len - 1) {
                if(release){//只有release升级code,普通的不升级
                    code = code + 1;
                }
                stringBuilder.append(code);
            } else {
                stringBuilder.append(code).append(".");
            }
        }
        return stringBuilder.toString();
    }


    public String toString(boolean suffix) {
        return moduleName + "=" + (suffix ? getFullVersion() : version);
    }

    /**
     * 有效规则
     * 1.必须五位数
     * 2.检验第一位是否在有效版本里面
     * @return
     */
    public boolean isValid() {
        if (TextUtils.isEmpty(version) || TextUtils.isEmpty(moduleName)) {
            return false;
        }
        String versionCode[] = version.split("\\.");

        if (versionCode.length != 5) {//新版本version 必须五位
            return false;
        }
        for (int i = 0; i < versionCode.length; i++) {
            String code = versionCode[i];

            try {
                int cd = Integer.parseInt(code);
                if (i == 0) {
                    if (!Constant.checkCode(cd)) {
                        return false;
                    }
                }
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    public boolean isOldValid(){
        if (TextUtils.isEmpty(version) || TextUtils.isEmpty(moduleName)) {
            return false;
        }
        String versionCode[] = version.split("\\.");
        if (versionCode.length==0){
            return false;
        }
        for (int i = 0; i < versionCode.length; i++) {
            String code = versionCode[i];
            try {
                int cd = Integer.parseInt(code);
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    /**
     * 新规则下的api
     *
     * @return
     */
    private boolean isValidNew() {
        return false;
    }
}
