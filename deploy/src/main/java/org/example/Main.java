package org.example;

import com.ibm.as400.access.*;
import java.io.*;
import java.lang.*;
import java.beans.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        var PROJECT_LOCAL_ROOT = System.getenv("PROJECT_LOCAL_ROOT");
        var PROJECT_REMOTE_ROOT = "/home/STEPOZER";
        var SERVER_HOST = System.getenv("SERVER_HOST");
        var SERVER_USER = System.getenv("SERVER_USER");
        var SERVER_PASS = System.getenv("SERVER_PASS");

        List<As400SourceFile> files = new ArrayList<As400SourceFile>();

        // Демо приложения
        //files.add(new As400SourceFile(PROJECT_LOCAL_ROOT + "/hello/hw.rpgle", PROJECT_REMOTE_ROOT + "/hw.rpgle", "STEPOZER1"));
        //files.add(new As400SourceFile(PROJECT_LOCAL_ROOT + "/hello/demoarr.rpgle", PROJECT_REMOTE_ROOT + "/demoarr.rpgle", "STEPOZER1"));
        //files.add(new As400SourceFile(PROJECT_LOCAL_ROOT + "/hello/demosum.rpgle", PROJECT_REMOTE_ROOT + "/demosum.rpgle", "STEPOZER1"));

        // Курсы валют
        files.add(new As400SourceFile(PROJECT_LOCAL_ROOT + "/exchange/XCURRENCY.PF", PROJECT_REMOTE_ROOT + "/XCURRENCY.PF", "STEPOZER1"));

        var as400IFSUploader = new As400IntegratedFileSystemUploader(SERVER_HOST, SERVER_USER, SERVER_PASS);
        var as400Conn = new AS400(SERVER_HOST, SERVER_USER, SERVER_PASS.toCharArray());
        var as400Cmd = new CommandCall(as400Conn);

        ConsoleLogger.info("Start file uploading");

        for (var srcFile : files) {
            as400IFSUploader.uploadFile(srcFile.getLocalPath(), srcFile.getRemotePath());
        }

        ConsoleLogger.info("All files uploaded");
        as400IFSUploader.logout();

        System.out.println("Start compilation");

        for (var srcFile : files) {
            var fileName = srcFile.getFileName();
            var fileExtension = srcFile.getFileExtension();

            if (Objects.equals(fileExtension, "rpgle")) {
                compileRPGProgram(as400Conn, as400Cmd, srcFile);
            } else if (Objects.equals(fileExtension, "PF")) {
                compilePFFile(as400Conn, as400Cmd, srcFile);
            } else if (Objects.equals(fileExtension, "DSPF")) {
                compileDSPFFile(as400Conn, as400Cmd, srcFile);
            }
        }

        ConsoleLogger.info("End");
    }

    public static void compileRPGProgram(AS400 as400Conn, CommandCall as400Cmd, As400SourceFile file) {
        executeCommand(
            as400Conn,
            as400Cmd,
            "CRTBNDRPG PGM(" + file.getRemoteLibrary() + "/" + file.getFileName() +") SRCSTMF('" + file.getRemotePath() + "') OPTION(*EVENTF) DBGVIEW(*SOURCE) TGTRLS(*CURRENT) TGTCCSID(*JOB)",
            file.getFileName().toUpperCase()
        );
    }

    public static void compilePFFile(AS400 as400Conn, CommandCall as400Cmd, As400SourceFile file) {
        executeCommand(
            as400Conn,
            as400Cmd,
            "QSYS/CPYFRMSTMF FROMSTMF('/home/STEPOZER/XCURRENCY.PF') TOMBR('/QSYS.LIB/STEPOZER1.LIB/HELLO.FILE/XCURRENCY.MBR') MBROPT(*REPLACE) STMFCCSID(1208) DBFCCSID(*FILE)",
            file.getFileName().toUpperCase()
        );

        executeCommand(
            as400Conn,
            as400Cmd,
            "CHGPFM FILE(STEPOZER1/HELLO) MBR(XCURRENCY) SRCTYPE(PF)",
            file.getFileName().toUpperCase()
        );

        executeCommand(
            as400Conn,
            as400Cmd,
            "CRTPF FILE(STEPOZER1/RCURRENCY) SRCFILE(STEPOZER1/HELLO) SRCMBR(" + file.getFileName() + ")",
            file.getFileName().toUpperCase()
        );
    }

    public static void compileDSPFFile(AS400 as400Conn, CommandCall as400Cmd, As400SourceFile file) {
        executeCommand(
            as400Conn,
            as400Cmd,
            "ADDPFM FILE(STEPOZER1/HELLO) MBR(" + file.getFileName() + ") SRCTYPE(DSPF)",
            file.getFileName().toUpperCase()
        );
        executeCommand(
            as400Conn,
            as400Cmd,
            "CRTDSPF FILE(STEPOZER1/" + file.getFileName() + ") SRCFILE(STEPOZER1/HELLO) SRCMBR(" + file.getFileName() + ") OPTION(*EVENTF) RSTDSP(*NO) REPLACE(*YES)",
            file.getFileName().toUpperCase()
        );
    }

    public static void executeCommand(AS400 as400Conn, CommandCall as400Cmd, String command, String spooledFileName) {
        ConsoleLogger.info("● Execute command " + command);

        try {
            var isSuccess = as400Cmd.run(command);

            AS400Message[] messageList;
            messageList = as400Cmd.getMessageList();
            for (AS400Message message : messageList) {
                ConsoleLogger.info("  " + message.getText());
            }

            if (!isSuccess) {
                var content = (new As400SpooledFileFinder()).fetchFileContent(as400Conn, as400Cmd, spooledFileName);
                var errorMessages = (new As400SpooledFileParser()).extractRPECompilationErrors(content);
                errorMessages.forEach( (n) -> {
                    ConsoleLogger.info("    " + n);
                } );
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

        ConsoleLogger.info("");
    }
}