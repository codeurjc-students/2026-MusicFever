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

## GitHub Project Configuration

### Entry details
| Entry details | Value |
|---|---|
| **Date** | 12-07-2026|
| **Phase** | 1 (Documentation) |
| **Objective** | Create the GitHub Actions Workflows to automate moving issues to the appropriate column when they are created/ selected/ completed|
| **AI Tool** | ChatGPT |
| **Tool version** | GPT-5.6 Sol |

### Tool Configuration

| Setting | Value |
|---|---|
| **Model** | GPT-5.6 Thinking |
| **Reasoning level** | High |
| **Interaction mode** | Chat |
| **Agent mode** | Disabled |
| **Plan mode** | Disabled |
| **Web search** | As needed |
| **Connected tools** | None used |

### Usage
After consulting the GitHub documentation on issues and how they can be closed automatically through commits or pull requests, the relevant information was provided to the AI. The AI was then asked whether it was also possible to reflect these changes in a GitHub Project.

Once this possibility was confirmed, the three required workflows were described, and the AI was asked to generate them. The resulting workflows had to be reviewed and adapted manually, particularly to replace project-specific and personal configuration details.

## UI Design in Figma

### Entry details
| Entry details | Value |
|---|---|
| **Date** | 27-07-2026|
| **Phase** | 1 (Documentation) |
| **Objective** | Modify the UI previously done to make it more appealing |
| **AI Tool** | ChatGPT |
| **Tool version** | GPT-5.6 Sol |

### Tool Configuration

| Setting | Value |
|---|---|
| **Model** | GPT-5.6 Thinking |
| **Reasoning level** | High |
| **Interaction mode** | Chat |
| **Agent mode** | Disabled |
| **Plan mode** | Disabled |
| **Web search** | As needed |
| **Connected tools** | None used |

### Usage
A screenshot of the UI made using Figma was given to the AI and asked what elements should be changed to make the interface more visually appealing to users. Then, it was asked to the AI to detail some of the properties of these elements (color, shadow and border) to be able to change them in the Figma prototype.

### Entry details

| Entry details    | Value                                                                             |
| ---------------- | --------------------------------------------------------------------------------- |
| **Date**         | 02-08-2026                                                                        |
| **Phase**        | 1 (Documentation)                                                                 |
| **Objective**    | Determine the most appropriate distribution of the application's analytics charts |
| **AI Tool**      | ChatGPT                                                                           |
| **Tool version** | GPT-5.5 (Anonymous version)                                                       |

### Tool Configuration

| Setting              | Value         |
| -------------------- | ------------- |
| **Model**            | GPT-5.5       |
| **Reasoning level**  | Not specified |
| **Interaction mode** | Chat          |
| **Agent mode**       | Disabled      |
| **Plan mode**        | Disabled      |
| **Web search**       | Not used      |
| **Connected tools**  | None used     |

### Usage

The AI was asked how the application's statistical charts should be distributed across its different pages. Since several entities have associated statistics, the initial idea was to display all of them in the Analytics section. However, the AI was also asked whether it would make sense to repeat the user-related charts on the user's profile page.

The AI advised against duplicating the same charts in both sections, as this could make the interface repetitive and create uncertainty about which page should be considered the main source of analytical information. Instead, it suggested adding a summary card to the user profile containing the most relevant general statistics and a direct link to the Analytics page, where users can access the complete set of charts and more detailed information.

This proposal was adopted and the statistics summary card was added to the user profile UI prototype.
