---
name: mcp-researcher
description: >
  Investigates and researches everything needed before installing an MCP (Model Context Protocol) server,
  then applies the configuration and verifies it works. Use this skill whenever the user wants to research,
  investigate, explore, evaluate, or install an MCP server. Triggers include: "quiero instalar un MCP de...",
  "investigar MCP", "buscar MCP para...", "como instalo el MCP de...", "que necesito para el MCP de...",
  "research MCP", "find MCP for...", "what MCP exists for...", "explore MCP options for...", "sigo sin ver
  el MCP de...", "no me aparece el MCP", "instalar MCP", "agregar MCP", "configurar MCP", or any mention
  of wanting to add/connect/use an external service through MCP. Also use this when the user mentions
  a specific MCP server name and wants to know how to set it up (e.g., "MCP de PostgreSQL", "Supabase MCP",
  "GitHub MCP server"). This skill handles the FULL lifecycle: research → configure → verify.
version: 2.0.0
---

# MCP Researcher — Research, Install & Verify

## Purpose

This skill guides through a structured process to research, install, and verify an MCP server. It goes beyond just researching — it applies the configuration and confirms it works, preventing the common pattern of "installed but not working".

The skill produces: a research report, applies the configuration to `.mcp.json`, and verifies the server appears and responds.

## Critical Rules — Common Mistakes to AVOID

These rules exist because of real failures. Follow them strictly.

### Rule 1: Remote HTTP servers REQUIRE `"type": "http"`
Claude Code's `.mcp.json` schema REQUIRES `"type"` for remote servers. Omitting it causes a schema validation error: "Does not adhere to MCP server configuration schema".

```json
// CORRECT — type is required for remote HTTP servers
"supabase": {
  "type": "http",
  "url": "https://mcp.supabase.com/mcp"
}

// WRONG — missing "type" fails schema validation in Claude Code
"supabase": {
  "url": "https://mcp.supabase.com/mcp"
}
```

OAuth is handled automatically by Claude Code after the server connects — adding `"type"` does NOT break OAuth. The browser login flow still opens on first use.

### Rule 2: Windows uses `npx.cmd`, not `npx`
On Windows, the `command` field for stdio servers MUST use `npx.cmd`:

```json
// CORRECT on Windows
"shadcn": {
  "command": "npx.cmd",
  "args": ["-y", "shadcn@latest", "mcp"]
}

// WRONG on Windows — will fail silently
"shadcn": {
  "command": "npx",
  "args": ["-y", "shadcn@latest", "mcp"]
}
```

**Detection**: Check the platform with `uname -s` or look at the shell environment. If it's Windows (win32/MINGW/MSYS), always use `npx.cmd`.

### Rule 3: Always wrap servers inside `"mcpServers"` in `.mcp.json`
The `.mcp.json` file at project root requires the `"mcpServers"` wrapper:

```json
// CORRECT — .mcp.json format
{
  "mcpServers": {
    "server-name": { ... }
  }
}

// WRONG — missing wrapper, servers won't load
{
  "server-name": { ... }
}
```

### Rule 4: Preserve existing servers when adding new ones
Always READ the existing `.mcp.json` first. Never overwrite — merge the new server into the existing `"mcpServers"` object.

### Rule 5: Validate JSON before writing
After constructing the new config, validate it's proper JSON before writing. A single trailing comma or missing quote will break ALL MCP servers in the file.

## Permissions — Web Research

This skill has **explicit permission to fetch any URL** needed for research purposes:
- GitHub repositories (READMEs, source code, releases, issues)
- npm/PyPI package pages
- Official vendor documentation sites
- Claude Code / Anthropic docs
- Any third-party blog, forum, or Stack Overflow post related to the MCP server
- GitHub raw content (`raw.githubusercontent.com`)
- Any URL the user provides directly

Use `WebFetch` freely on any URL without asking the user for permission first. The goal is thorough, multi-source research — do not limit fetching to only "known" sites.

## Research Workflow

Follow these phases in order. Each phase builds on the previous one.

### Phase 1: Identify the MCP Server

Start by clarifying exactly which MCP server the user needs.

1. **Ask what service/tool** they want to connect (e.g., PostgreSQL, GitHub, Slack, Supabase, filesystem)
2. **Search for the official MCP server** using WebSearch:
   - Search: `"MCP server" + "<service-name>" site:github.com`
   - Search: `"<service-name>" MCP server Claude Code configuration`
   - Search: `"<service-name>" MCP server npm OR pypi`
3. **Identify the canonical source** — prefer in this order:
   - Official MCP servers from `github.com/modelcontextprotocol/servers`
   - First-party servers from the service vendor (e.g., Supabase, Asana, GitHub)
   - Well-maintained community servers (check stars, recent commits, open issues)
4. **Fetch the README** from the repo using WebFetch to get authoritative details
5. **Also fetch the official docs page** if the vendor has one (e.g., `supabase.com/docs/guides/getting-started/mcp`)

Present findings to the user before moving on:
```
Found: @supabase/mcp-server-supabase
Source: github.com/supabase-community/supabase-mcp
Type: First-party (maintained by Supabase)
Transport: Remote HTTP with OAuth
Package: URL-based — no npm install needed
```

### Phase 2: Prerequisites and Dependencies

Research what's needed to run this MCP server.

1. **Determine transport type FIRST** — this decides everything else:
   - **Remote URL (HTTP/SSE)**: No local install needed, just a URL
   - **stdio (local process)**: Needs runtime (Node.js, Python, etc.)
2. **Runtime requirements** — Only for stdio servers: Node.js version, Python version, etc.
3. **Authentication method**:
   - OAuth (browser-based, automatic) — most hosted services
   - API token (manual, in headers) — some REST APIs
   - Connection string (in args/env) — databases
   - None — some local tools
4. **Network** — Local only? Needs internet? Specific ports?

Check the current environment for compatibility:
```bash
# Check platform (CRITICAL for npx vs npx.cmd)
uname -s 2>/dev/null || echo "Windows"

# Check available runtimes (only needed for stdio servers)
node --version 2>/dev/null
npx --version 2>/dev/null
python --version 2>/dev/null
docker --version 2>/dev/null
```

Report:
```
Prerequisites Check:
  [OK] Platform: Windows — will use npx.cmd for stdio servers
  [OK] Node.js v22.15.0
  [INFO] Transport: Remote HTTP — no local runtime needed
  [INFO] Auth: OAuth — browser login on first use
```

### Phase 3: Configuration Research

Gather the EXACT configuration. This is the most critical phase — wrong fields cause silent failures.

1. **Fetch configuration examples** from:
   - The server's README/docs (WebFetch on the repo)
   - The vendor's official docs page
   - Claude Code MCP docs at `https://docs.anthropic.com/en/docs/claude-code/mcp`
2. **Determine the correct config pattern** based on transport:

   **Pattern A: Remote servers with OAuth** (Supabase, Asana, GitHub, etc.)
   ```json
   {
     "mcpServers": {
       "server-name": {
         "url": "https://mcp.service.com/mcp"
       }
     }
   }
   ```
   CRITICAL: Do NOT add `"type"`, `"transport"`, or `"headers"` for OAuth servers. Claude Code handles OAuth automatically. Adding `"type": "http"` or `"type": "sse"` causes silent failures.

   **Pattern B: Remote servers with token auth** (CI environments, custom APIs)
   ```json
   {
     "mcpServers": {
       "server-name": {
         "type": "http",
         "url": "https://api.example.com/mcp",
         "headers": {
           "Authorization": "Bearer ${API_TOKEN}"
         }
       }
     }
   }
   ```
   Only use `"type"` when you need explicit `"headers"` for token-based auth (no OAuth).

   **Pattern C: Local stdio servers (npx/node/python)**
   ```json
   {
     "mcpServers": {
       "server-name": {
         "command": "npx.cmd",
         "args": ["-y", "@scope/mcp-server-name", "...args"],
         "env": { "KEY": "value" }
       }
     }
   }
   ```
   On Windows: use `npx.cmd`. On macOS/Linux: use `npx`.

   **Pattern D: Local SSE servers** (only for self-hosted SSE endpoints)
   ```json
   {
     "mcpServers": {
       "server-name": {
         "transport": "sse",
         "url": "http://localhost:8081/sse"
       }
     }
   }
   ```
   Use `"transport"` (not `"type"`) only for local SSE servers where auto-detection from URL alone won't work.

3. **Cross-reference multiple sources** — Compare the README, vendor docs, and any GitHub issues to confirm the exact format
4. **Find common gotchas** — search for issues/discussions:
   - `"<mcp-server-name>" + "not working" OR "error" OR "troubleshooting" Claude Code`
5. **Identify optional parameters** — query params, flags, or env vars that customize behavior

### Phase 4: Compatibility Check

Verify compatibility with the user's specific environment.

1. **Read existing `.mcp.json`** — Use the Read tool to get the current config. If it doesn't exist, you'll create it
2. **Validate existing JSON** — Check the current file is valid JSON before modifying
3. **Check for conflicts** — Is there already a server with the same name?
4. **Check for port conflicts** if the server needs specific ports
5. **Verify credentials availability** — Are required env vars already configured?
6. **OS-specific checks**:
   - Windows: `command` must use `.cmd` extensions (`npx.cmd`, `node.cmd`)
   - Windows: Use forward slashes in paths within JSON
   - macOS/Linux: Check file permissions for custom scripts
7. **Check the project's existing setup** — Does docker-compose already provide the service?

### Phase 5: Apply Configuration

This phase actually writes the configuration. Do NOT skip this phase.

1. **Construct the new `.mcp.json`** content by merging the new server into existing servers
2. **Validate the JSON** mentally — check for:
   - No trailing commas
   - All strings properly quoted
   - Correct nesting inside `"mcpServers"`
   - No duplicate keys
3. **Write the file** using the Edit tool (preferred) or Write tool (if creating new)
4. **Read the file back** to confirm it was written correctly
5. **Present the change to the user** — show what was added

Example merge:
```
BEFORE:
{
  "mcpServers": {
    "shadcn": { "command": "npx.cmd", "args": [...] }
  }
}

AFTER:
{
  "mcpServers": {
    "shadcn": { "command": "npx.cmd", "args": [...] },
    "supabase": { "url": "https://mcp.supabase.com/mcp" }
  }
}
```

### Phase 6: Verify Installation

This phase confirms the MCP server is working. NEVER skip this phase.

#### Step 1: Inform the user about restart requirement
```
To activate the new MCP server:
1. Exit Claude Code (Ctrl+C or type /exit)
2. Restart Claude Code in the same project directory
3. Run /mcp to see the server list
```

#### Step 2: Post-restart verification checklist
Tell the user what to check after restarting:
```
After restarting, verify:
- [ ] Run /mcp — the new server should appear in the list
- [ ] Server status should show "connected" or "ready"
- [ ] For OAuth servers: a browser window will open on first tool use — complete the login
- [ ] Try a basic tool call to confirm it works
```

#### Step 3: Provide a test command
Give the user a specific tool call to test:
```
To test, try asking me:
- Supabase: "list my Supabase tables" or "search Supabase docs for authentication"
- PostgreSQL: "list schemas in my database"
- GitHub: "list my recent pull requests"
- Filesystem: "list files in /path/to/dir"
```

#### Step 4: Troubleshooting guide
If the server doesn't appear after restart:

```
Troubleshooting:
1. Check .mcp.json is valid JSON:
   - Run: cat .mcp.json | python -m json.tool
   - Fix any syntax errors

2. Check server-specific issues:
   - Remote servers: Verify the URL is accessible (curl the URL)
   - stdio servers: Verify the command exists (which npx.cmd)
   - OAuth servers: Clear cached tokens and retry

3. Debug mode:
   - Run: claude --debug
   - Look for MCP-related error messages

4. Common fixes:
   - Windows: Change "npx" to "npx.cmd"
   - Remote OAuth: Remove "type" field, keep only "url"
   - JSON errors: Remove trailing commas, check quotes
   - Restart: Some changes require a full restart, not just /mcp
```

## Research Report Template

After completing all phases, present the summary:

```
======================================
  MCP SERVER INSTALLED
  Server: <name>
  Date: <current date>
======================================

SOURCE
  Repository: <url>
  Maintainer: <official/community>

TRANSPORT
  Type: <remote URL | stdio | sse>
  Auth: <OAuth | Token | Env vars | None>

CONFIGURATION APPLIED
  File: .mcp.json
  <show the JSON that was added>

TOOLS PROVIDED
  <numbered list of main tools>

NEXT STEPS
  1. Restart Claude Code
  2. Run /mcp to verify
  3. Test with: "<example command>"

OPTIONAL PARAMETERS
  <list any optional query params or flags>

TROUBLESHOOTING
  <top 3 most common issues>
======================================
```

## Known MCP Servers Quick Reference

Pre-validated configurations for popular MCP servers:

### Supabase (Remote, OAuth)
```json
"supabase": {
  "url": "https://mcp.supabase.com/mcp"
}
```
Optional query params: `?read_only=true`, `?project_ref=<ref>`, `?features=database,docs`

### GitHub (Remote, OAuth)
```json
"github": {
  "url": "https://mcp.github.com/sse"
}
```

### Asana (Remote, OAuth)
```json
"asana": {
  "url": "https://mcp.asana.com/sse"
}
```

### PostgreSQL (stdio)
```json
"postgres": {
  "command": "npx.cmd",
  "args": ["-y", "@modelcontextprotocol/server-postgres", "postgresql://user:pass@host:port/db"]
}
```
Note: Use `npx.cmd` on Windows, `npx` on macOS/Linux.

### Filesystem (stdio)
```json
"filesystem": {
  "command": "npx.cmd",
  "args": ["-y", "@modelcontextprotocol/server-filesystem", "/allowed/path"]
}
```

### shadcn (stdio)
```json
"shadcn": {
  "command": "npx.cmd",
  "args": ["-y", "shadcn@latest", "mcp"]
}
```

## Important Notes

- Always verify information from multiple sources — MCP is evolving fast and docs can be outdated
- Prefer `npx -y` for npm-based servers so they auto-install without global installation
- On Windows, ALWAYS use `npx.cmd` instead of `npx` for stdio servers
- For remote OAuth servers, NEVER add `"type"` — only use `"url"`
- If the user's project already has the target service (e.g., PostgreSQL via docker-compose), reuse that connection string
- After configuring, ALWAYS inform the user they need to restart Claude Code
- Save the research report to `generate/doc/` only if the user explicitly asks for it
