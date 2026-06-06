package com.mcp_server.service;

import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class LokiLogService {

    private final WebClient webClient;
    private final String labelKey;
    private final String labelValue;

    public LokiLogService(
            @Value("${loki.url:http://localhost:3100}") String lokiUrl,
            @Value("${loki.label-key:job}") String labelKey, 
            @Value("${loki.label-value:springboot}") String labelValue) { 

        this.webClient = WebClient.builder().baseUrl(lokiUrl).build();
        this.labelKey = labelKey;
        this.labelValue = labelValue;
    }

    @McpTool(name = "analyzeBusinessExceptionsFromLoki", 
             description = "Queries the centralized Grafana Loki log server to find application logs, initializations, warnings, or runtime exceptions.")
    public String analyzeBusinessExceptionsFromLoki(
            @McpToolParam(description = "The search phrase or log message pattern to scan for.") 
            String exceptionPattern) {

        String logQLQuery = "{" + labelKey + "=\"" + labelValue + "\"} |= \"" + exceptionPattern + "\"";

        try {
            System.out.println("[MCP SERVER] Processing Tool Request for pattern: " + exceptionPattern);
            System.out.println("[MCP SERVER] Formatted LogQL: " + logQLQuery);

            // FIX: Swapped out /query for /query_range to look back across historical log records
            JsonNode response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/loki/api/v1/query_range") // Looking back in time
                            .queryParam("query", logQLQuery)
                            .queryParam("limit", 50)
                            .build())
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            System.out.println("[MCP SERVER] Raw response received from Loki successfully.");
            return parseLokiResponse(response, logQLQuery);

        } catch (org.springframework.web.reactive.function.client.WebClientResponseException ex) {
            System.err.println("[MCP SERVER] HTTP Error from Loki: " + ex.getStatusCode() + " - " + ex.getResponseBodyAsString());
            return "Loki API returned an error: " + ex.getStatusCode() + ". Check your container network configuration.";
        } catch (Exception e) {
            System.err.println("[MCP SERVER] Internal Tool Exception: " + e.getMessage());
            return "Failed to connect to the Loki cluster instance. Internal Error: " + e.getMessage();
        }
    }

    private String parseLokiResponse(JsonNode response, String currentQuery) {
        if (response == null) {
            return "Empty payload response received from Loki server.";
        }

        StringBuilder collectedLogs = new StringBuilder();
        JsonNode results = response.path("data").path("result");

        if (results.isArray() && !results.isEmpty()) {
            for (JsonNode streamNode : results) {
                JsonNode valuesNode = streamNode.path("values");
                if (valuesNode.isArray()) {
                    for (JsonNode valueEntry : valuesNode) {
                        if (valueEntry.isArray() && valueEntry.size() >= 2) {
                            collectedLogs.append(valueEntry.get(1).asText()).append("\n");
                        }
                    }
                }
            }
        }

        if (collectedLogs.isEmpty()) {
            System.out.println("[MCP SERVER] Query executed fine, but 0 lines matched in Loki.");
            return "The query executed successfully, but no recent matching log entries were found in Loki for: " + currentQuery;
        }

        return collectedLogs.toString();
    }
}