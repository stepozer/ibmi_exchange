package org.example;

import java.util.ArrayList;
import java.util.List;

public class As400SpooledFileParser {
    public List<String> extractRPECompilationErrors(String text) {
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
}
