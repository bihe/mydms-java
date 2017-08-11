package net.binggl.mydms.features.startpage.models;

public class VersionInfo {
    private String artefactId;
    private String buildNumber;
    private String version;

    public String getArtefactId() {
        return artefactId;
    }

    public void setArtefactId(String artefactId) {
        this.artefactId = artefactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(String buildNumber) {
        this.buildNumber = buildNumber;
    }

    public String getVersionString() {
        return String.format("%s-%s-%s", artefactId, version, buildNumber);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VersionInfo versionInfo = (VersionInfo) o;

        if (getArtefactId() != null ? !getArtefactId().equals(versionInfo.getArtefactId()) : versionInfo.getArtefactId() != null)
            return false;
        if (getVersion() != null ? !getVersion().equals(versionInfo.getVersion()) : versionInfo.getVersion() != null)
            return false;
        return getBuildNumber() != null ? getBuildNumber().equals(versionInfo.getBuildNumber()) : versionInfo.getBuildNumber() == null;
    }

    @Override
    public int hashCode() {
        int result = getArtefactId() != null ? getArtefactId().hashCode() : 0;
        result = 31 * result + (getVersion() != null ? getVersion().hashCode() : 0);
        result = 31 * result + (getBuildNumber() != null ? getBuildNumber().hashCode() : 0);
        return result;
    }
}
