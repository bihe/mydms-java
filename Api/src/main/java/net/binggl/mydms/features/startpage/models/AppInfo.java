package net.binggl.mydms.features.startpage.models;

public class AppInfo {

    private UserInfo userInfo;
    private VersionInfo versionInfo;

    public AppInfo(UserInfo userInfo, VersionInfo versionInfo) {
        this.userInfo = userInfo;
        this.versionInfo = versionInfo;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public VersionInfo getVersionInfo() {
        return versionInfo;
    }

    public void setVersionInfo(VersionInfo versionInfo) {
        this.versionInfo = versionInfo;
    }
}
