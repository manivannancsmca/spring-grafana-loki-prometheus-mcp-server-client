# Local AI Log Diagnostics Agent (Spring AI + Model Context Protocol + Grafana Loki)

An enterprise-grade, decoupled architecture leveraging the **Model Context Protocol (MCP)** and a local **Ollama LLM** to analyze, query, and summarize central logging infrastructure managed by **Grafana Loki** in real-time using natural language.

---

## 🏗️ System Architecture

This solution splits responsibilities into two decoupled Spring Boot 3.x microservices communicating over Model Context Protocol (MCP) using a Server-Sent Events (SSE) transport pipeline:

1. **AI Client Orchestrator (`ai-mcp-client` : Port 8011)** - Powered by Spring AI and a local Ollama LLM instance.
   - Manages the user conversational memory context window.
   - Dynamically binds and delegates executable tools requested by the LLM down to the remote server.
2. **Business Core / MCP Server (`mcp-server` : Port 8081)**
   - Exposes capabilities to the protocol via `@McpTool`.
   - Connects to your live Dockerized Grafana Loki cluster utilizing optimized LogQL range filters (`/loki/api/v1/query_range`).
