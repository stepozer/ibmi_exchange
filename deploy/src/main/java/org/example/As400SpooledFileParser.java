package org.example;

import java.util.ArrayList;
import java.util.List;

/**
 * Вытаскивает из контента spooled файла перечень ошибок в виде списка строк
 */
public class As400SpooledFileParser {
    public List<String> extractCompilationErrors(String text) {
        if (text.contains("File attribute  . . . . . . . . . . . . . . . . . . :   Logical")) {
            return extractLFCompilationErrors(text);
        }

        return extractRPECompilationErrors(text);
    }

    private List<String> extractRPECompilationErrors(String text) {
        List<String> errorMessages = new ArrayList<>();
        String[] lines = text.split("\n");
        boolean inErrorSection = false;

        for (String line : lines) {
            if (line.contains("Msg id  Sv Number Message text")) {
                inErrorSection = true;
            } else if (line.contains("E N D   O F   M E S S A G E   S U M M A R Y")) {
                break;
            } else if (inErrorSection) {
                if (!line.trim().isEmpty()) {
                    errorMessages.add(line.trim());
                }
            }
        }

        return errorMessages;
    }

    private List<String> extractLFCompilationErrors(String text) {
        List<String> errorMessages = new ArrayList<>();
        String[] lines = text.split("\n");
        boolean inErrorSection = false;

        for (String line : lines) {
            if (line.contains("ID      Severity  Number")) {
                inErrorSection = true;
            } else if (line.contains("Message Summary")) {
                break;
            } else if (inErrorSection) {
                if (!line.trim().isEmpty()) {
                    errorMessages.add(line.trim());
                }
            }
        }

        return errorMessages;
    }
}
