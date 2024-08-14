package org.example;

public class As400SourceFile {
    private String localPath;
    private String remotePath;
    private String remoteLibrary;

    As400SourceFile(String localPath, String remotePath, String remoteLibrary) {
        this.localPath = localPath;
        this.remotePath = remotePath;
        this.remoteLibrary = remoteLibrary;
    }

    public String getLocalPath() {
        return localPath;
    }

    public String getRemotePath() {
        return remotePath;
    }

    public String getRemoteLibrary() {
        return remoteLibrary;
    }

    public String getFileName() {
        return getFileNameWithExtension().split("\\.")[0];
    }

    public String getFileExtension() {
        return getFileNameWithExtension().split("\\.")[1];
    }

    private String getFileNameWithExtension() {
        return localPath.split("/")[localPath.split("/").length - 1];
    }
}
