package com.mcp_server.service;

import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class BusinessLogService {

    // private static final String LOG_PATH = "C:\\Users\\windows\\Videos\\grafana-loki\\logs";

    // @McpTool(name = "analyzeLocalBusinessExceptions", 
    //          description = "Scans core application log records for a specific pattern or exception type and collects diagnostics.")
    // public String analyzeLocalBusinessExceptions(
    //         @McpToolParam(description = "The target phrase or exception string to search for (e.g. 'NullPointerException', 'SQLException')") 
    //         String exceptionPattern) {
        
    //     try (Stream<String> lines = Files.lines(Paths.get(LOG_PATH))) {
    //         String collectedErrors = lines
    //                 .filter(line -> line.contains("ERROR") && line.contains(exceptionPattern))
    //                 .limit(50) // Limit extraction footprint to match LLM buffer sizing smoothly
    //                 .collect(Collectors.joining("\n"));
            
    //         return collectedErrors.isEmpty() ? "No recent matching runtime exceptions discovered." : collectedErrors;
    //     } catch (IOException e) {
    //         return "Failed to parse system log file context: " + e.getMessage();
    //     }
    // }
}
