# AI Usage in Music Fever Development

## Entry Structure
- Date
- Phase
- Objective
- AI Tool
- Version of the tool
- Tool Configuration (model, level of reasoning, agent mode, chat, plan, ...)
- How it has been used
- Tool's Complementation: plugins, skills, MCP servers, tools, ...
- Context files about AI usage on the proyect (CLAUDE.md or similar)
- If there has been usage of tools that relay on files for the development based on AI (for example, Spec Driven Development tools) it should be indicated where on the repository these files are and how had been used.

Examples for the _How it has been used_ part:
- When an error ocurred, the PROMPT will be copied and asked the AI to indicate the posible motives for the error to happen
- The AI will be given a general description of the problem, then generate the mayority of the code.