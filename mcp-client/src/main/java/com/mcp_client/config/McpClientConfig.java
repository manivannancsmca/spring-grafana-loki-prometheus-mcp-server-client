package com.mcp_client.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;

@Configuration
public class McpClientConfig {

    @Bean
    public McpSyncClient mcpSyncClient() {

        HttpClientSseClientTransport transport = HttpClientSseClientTransport
                .builder("http://localhost:8013/mcp/sse")
                .build();

        McpSyncClient client = McpClient.sync(transport)
                .build();

        client.initialize();

        return client;
    }

    @Bean
    public ChatClient chatClient(ChatModel chatModel, McpSyncClient mcpSyncClient) {
        var toolCallbackProvider = new SyncMcpToolCallbackProvider(mcpSyncClient);

        return ChatClient.builder(chatModel)
                .defaultSystem(
                        """
                                You are a Senior Engineering Support Companion. Your job is to resolve problem diagnostics utilizing registered remote MCP tools.

                                CRITICAL INSTRUCTIONS FOR TOOL USAGE:
                                1. Runtime exceptions, bugs, and failures can be logged under 'ERROR' or 'WARN' log levels depending on how Spring Boot handles them.
                                2. If a developer asks for "exceptions" or general problems, execute the `analyzeBusinessExceptionsFromLoki` tool using broad terms like 'RuntimeException' or 'Exception' directly, rather than just searching for the word 'ERROR'.
                                3. NEVER use wildcard characters like '*' or '%' in the tool parameters.
                                """)
                .defaultToolCallbacks(toolCallbackProvider.getToolCallbacks())
                .build();
    }
}