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

---

## 🚀 Step-by-Step Implementation Guide

### 1. Infrastructure Setup (Docker Compose)
Create a `docker-compose.yml` file to spin up Loki, Promtail, and Grafana.

```yaml
version: '3.8'

networks:
  monitoring:
    driver: bridge

services:
  loki:
    image: grafana/loki:3.0.0
    container_name: loki
    ports:
      - "3100:3100"
    command: -config.file=/etc/loki/local-config.yaml
    networks:
      - monitoring

  promtail:
    image: grafana/promtail:3.0.0
    container_name: promtail
    volumes:
      - /var/log/apps:/var/log/apps
      - ./promtail-config.yml:/etc/promtail/config.yml
    command: -config.file=/etc/promtail/config.yml
    networks:
      - monitoring
---

### 🏃 Execution & Operation Playbook
Follow this exact sequence to start up your stack and clean your system memory boundaries.

### Step 1: Fire up Environment Dependencies
Ensure Ollama is running and has the target model pulled, then boot up your background telemetry cluster:

```bash
   ollama run llama3
   docker compose up -d

### Step 2: Spin Up the Processing Layer (Order Matters)
Start the MCP Server Execution Engine on port 8081 first. It will declare the schema metadata definitions.

Start the AI Chat Client application on port 8011. Upon context initializing, it hooks up to the server via SSE to register your remote tools.

⚠️ Important Senior Dev Tip: If you change your code on the server or find the LLM stuck looping on an old explanation, restart the AI Client App to clear its in-memory conversation context stack.

---

### Step 3: Run Diagnostic Queries via cURL
Open a separate command window terminal and interact with your logs using natural language:

Query 1: Review Runtime Exceptions
```bash
   curl "http://localhost:8011/ai/ask?prompt=Scan+the+logs+now+for+any+RuntimeException+and+list+the+details"
