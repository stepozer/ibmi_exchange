package org.example;

public class As400SourceFile {
    private String localPath;
    private String remotePath;

    As400SourceFile(String localPath, String remotePath) {
        this.localPath = localPath;
        this.remotePath = remotePath;
    }

    public String getLocalPath() {
        return localPath;
    }

    public String getRemotePath() {
        return remotePath;
    }
}
