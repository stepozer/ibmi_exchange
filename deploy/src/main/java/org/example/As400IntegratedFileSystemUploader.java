package org.example;

import com.ibm.as400.access.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Загружает указанный файл на сервер IBMi в интегрированную файловую систему для оследующей компиляции
 */
public class As400IntegratedFileSystemUploader {
    private FTPClient client;

    public As400IntegratedFileSystemUploader(String serverHost, String serverUser, String serverPass) {
        client = createClient(serverHost, serverUser, serverPass);
    }

    public void uploadFile(String localFilePath, String serverFilePath) {
        ConsoleLogger.info("  Upload file " + localFilePath + " > " + serverFilePath);

        try {
            var fis = new FileInputStream(localFilePath);
            client.storeFile(serverFilePath, fis);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void logout() {
        try {
            client.logout();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private FTPClient createClient(String serverHost, String serverUser, String serverPass) {
        try {
            FTPClient client = new FTPClient();
            client.connect(serverHost);
            client.login(serverUser, serverPass);
            client.enterLocalPassiveMode(); //passive mode - IMPORTANT
            client.setFileType(FTP.ASCII);
            return client;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
