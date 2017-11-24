package net.binggl.mydms.features.startpage.models;

public class AppInfoBuilder {
    private UserInfo userInfo;
    private VersionInfo versionInfo;

    public AppInfoBuilder setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
        return this;
    }

    public AppInfoBuilder setVersionInfo(VersionInfo versionInfo) {
        this.versionInfo = versionInfo;
        return this;
    }

    public AppInfo build() {
        return new AppInfo(userInfo, versionInfo);
    }
}