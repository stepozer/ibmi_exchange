package org.example;

import com.ibm.as400.access.*;
import org.apache.commons.net.ftp.FTPClient;

import java.io.*;
import java.lang.*;
import java.beans.*;

public class Main {
    public static final String[] SRC_FILES = {
        "demoarr.rpgle",
        "demosum.rpgle"
    };

    public static void main(String[] args) throws IOException {
        var SERVER_HOST = System.getenv("SERVER_HOST");
        var SERVER_USER = System.getenv("SERVER_USER");
        var SERVER_PASS = System.getenv("SERVER_PASS");
        var FTPClient = createFTPClient(SERVER_HOST, SERVER_USER, SERVER_PASS);
        var as400Conn = new AS400(SERVER_HOST, SERVER_USER, SERVER_PASS.toCharArray());
        var as400Cmd = new CommandCall(as400Conn);

        log("Start file uploading");

        for (var srcFile : Main.SRC_FILES) {
            uploadFile(FTPClient, System.getenv("PROJECT_LOCAL_ROOT") + "/hello/" + srcFile, "/home/STEPOZER/" + srcFile);
        }

        log("All files uploaded");
        FTPClient.logout();

        System.out.println("Start compilation");

        for (var srcFile : Main.SRC_FILES) {
            var fileName = srcFile.split("\\.")[0];
            executeCommand(
                as400Cmd,
                "CRTBNDRPG PGM(STEPOZER1/" + fileName +") SRCSTMF('/home/STEPOZER/" + srcFile + "') OPTION(*EVENTF) DBGVIEW(*SOURCE) TGTRLS(*CURRENT) TGTCCSID(*JOB)"
            );
        }

        log("End");
    }

    public static FTPClient createFTPClient(String serverHost, String serverUser, String serverPass) {
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

    public static void uploadFile(FTPClient client, String localFilePath, String serverFilePath) {
        log("  Upload file " + localFilePath + " > " + serverFilePath);

        try {
            var fis = new FileInputStream(localFilePath);
            client.storeFile(serverFilePath, fis);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void executeCommand(CommandCall as400Cmd, String command) {
        log("  Execute command " + command);

        try {
            as400Cmd.run(command);
            AS400Message[] messageList;
            messageList = as400Cmd.getMessageList();
            for (AS400Message message : messageList) {
                log("  " + message.getText());
            }
        } catch (
            AS400SecurityException |
            ErrorCompletingRequestException |
            IOException |
            InterruptedException |
            PropertyVetoException e
        ) {
            throw new RuntimeException(e);
        }

        log("");
    }

    public static void log(String message) {
        System.out.println(message);
    }
}