package org.example;

public class As400PFSourceFile extends As400AbstractSourceFile implements As400SourceFile {
    private String remoteLibraryFile;
    private String remoteMBRPath;
    private String recordName;

    As400PFSourceFile(String localPath, String remotePath, String remoteLibrary, String remoteLibraryFile, String remoteMBRPath, String recordName) {
        super(localPath, remotePath, remoteLibrary);

        this.remoteLibraryFile = remoteLibraryFile;
        this.remoteMBRPath = remoteMBRPath;
        this.recordName = recordName;
    }

    public String getRemoteMBRPath() {
        return remoteMBRPath;
    }

    public String getRecordName() {
        return recordName;
    }

    public String getRemoteLibraryFile() {
        return remoteLibraryFile;
    }
}
