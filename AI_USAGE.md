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

## 1. GitHub Project Configuration

### 1.1 Workflow Generation
#### Entry details
| Entry details | Value |
|---|---|
| **Date** | 12-07-2026|
| **Phase** | 1 (Documentation) |
| **Objective** | Create the GitHub Actions Workflows to automate moving issues to the appropriate column when they are created/ selected/ completed|
| **AI Tool** | ChatGPT |
| **Tool version** | GPT-5.6 Sol |

#### Tool Configuration

| Setting | Value |
|---|---|
| **Model** | GPT-5.6 Thinking |
| **Reasoning level** | High |
| **Interaction mode** | Chat |
| **Agent mode** | Disabled |
| **Plan mode** | Disabled |
| **Web search** | As needed |
| **Connected tools** | None used |

#### Usage
After consulting the GitHub documentation on issues and how they can be closed automatically through commits or pull requests, the relevant information was provided to the AI. The AI was then asked whether it was also possible to reflect these changes in a GitHub Project.

Once this possibility was confirmed, the three required workflows were described, and the AI was asked to generate them. The resulting workflows had to be reviewed and adapted manually, particularly to replace project-specific and personal configuration details.

## 2. UI Design in Figma

### 2.1 Modifying the given mockup
#### Entry details
| Entry details | Value |
|---|---|
| **Date** | 27-07-2026|
| **Phase** | 1 (Documentation) |
| **Objective** | Modify the UI previously done to make it more appealing |
| **AI Tool** | ChatGPT |
| **Tool version** | GPT-5.6 Sol |

#### Tool Configuration

| Setting | Value |
|---|---|
| **Model** | GPT-5.6 Thinking |
| **Reasoning level** | High |
| **Interaction mode** | Chat |
| **Agent mode** | Disabled |
| **Plan mode** | Disabled |
| **Web search** | As needed |
| **Connected tools** | None used |

#### Usage
A screenshot of the UI made using Figma was given to the AI and asked what elements should be changed to make the interface more visually appealing to users. Then, it was asked to the AI to detail some of the properties of these elements (color, shadow and border) to be able to change them in the Figma prototype.

> This entry has also been used for the profile and home pages

### 2.2 Chart distribution
#### Entry details

| Entry details    | Value                                                                             |
| ---------------- | --------------------------------------------------------------------------------- |
| **Date**         | 02-08-2026                                                                        |
| **Phase**        | 1 (Documentation)                                                                 |
| **Objective**    | Determine the most appropriate distribution of the application's analytics charts |
| **AI Tool**      | ChatGPT                                                                           |
| **Tool version** | GPT-5.5 (Anonymous version)                                                       |

#### Tool Configuration

| Setting              | Value         |
| -------------------- | ------------- |
| **Model**            | GPT-5.5       |
| **Reasoning level**  | Not specified |
| **Interaction mode** | Chat          |
| **Agent mode**       | Disabled      |
| **Plan mode**        | Disabled      |
| **Web search**       | Not used      |
| **Connected tools**  | None used     |

#### Usage

The AI was asked how the application's statistical charts should be distributed across its different pages. Since several entities have associated statistics, the initial idea was to display all of them in the Analytics section. However, the AI was also asked whether it would make sense to repeat the user-related charts on the user's profile page.

The AI advised against duplicating the same charts in both sections, as this could make the interface repetitive and create uncertainty about which page should be considered the main source of analytical information. Instead, it suggested adding a summary card to the user profile containing the most relevant general statistics and a direct link to the Analytics page, where users can access the complete set of charts and more detailed information.

This proposal was adopted and the statistics summary card was added to the user profile UI prototype.

### 2.3 Icons generation
#### Entry details
| Entry details | Value |
| --- | --- |
| __Date__ | 08-2026 |
| __Phase__ | 1 (Documentation) |
| __Objective__ | Generate a consistent set of icons for the different actions and elements used throughout the Music Fever interface |
| __AI Tool__ | ChatGPT |
| __Tool Version__ | GPT-5.6 Sol |

#### Tool Configuration
| Setting              | Value            |
| -------------------- | ---------------- |
| **Model**            | GPT-5.6 Thinking |
| **Reasoning level**  | High             |
| **Interaction mode** | Chat             |
| **Agent mode**       | Disabled         |
| **Plan mode**        | Disabled         |
| **Web search**       | Not required     |
| **Connected tools**  | Image generation |

#### Usage
The AI was used to generate several icons required throughout the Music Fever interface. The desired action or functionality of each icon was described to the AI, together with the visual style and colour palette of the application.

The generated icons included, among others, actions such as play, add to queue, play next, add to playlist, edit, search, and icons representing different statistics and administrative information.

Several iterations were performed when necessary to improve the semantic clarity of the icons and ensure that similar actions could be easily distinguished. The AI was also instructed to maintain a consistent visual language based on Music Fever's lilac and purple colour palette and to generate the icons with transparent backgrounds so they could be directly incorporated into the interface mock-ups.

### 2.4 Privacy and Terms Pages
#### Entry Details
| Entry details    | Value                                                                                                                                                         |
| ---------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Date**         | 29-08-2026                                                                                                                                                    |
| **Phase**        | 1 (Documentation)                                                                                                                                 |
| **Objective**    | Generate the initial content for the Privacy Policy and Terms of Service pages according to the functionality and data management requirements of Music Fever |
| **AI Tool**      | ChatGPT                                                                                                                                                       |
| **Tool version** | GPT-5.6 Sol                                                                                                                                                   |
#### Tool Configuration
| Setting              | Value                                                                   |
| -------------------- | ----------------------------------------------------------------------- |
| **Model**            | GPT-5.6 Thinking                                                        |
| **Reasoning level**  | High                                                                    |
| **Interaction mode** | Chat                                                                    |
| **Agent mode**       | Disabled                                                                |
| **Plan mode**        | Disabled                                                                |
| **Web search**       | Used when necessary to verify legal and third-party service information |
| **Connected tools**  | None used                                                               |

#### Usage
The AI was asked to generate the initial content for the Privacy Policy and Terms of Service pages of Music Fever.

Before generating the documents, the AI was explicitly instructed to ask all the questions required to understand how the application works and what information it processes. The questions covered aspects such as:

- User account information stored by Music Fever.
- Profile pictures and account creation dates.
- Spotify authentication and playback requirements.
- Current and future use of the Apple Music API.
- Data stored from Rooms and their participants.
- Data retained for anonymous or guest users.
- Music requests submitted by users and their associated status.
- Private and administrator-created public playlists.
- Personal and Room statistics.
- Cookies and analytics.
- Advertising and payments.
- Minimum user age.
- Account and personal-data deletion.
- Acceptable use of the application.
- Applicable Spanish and European data-protection legislation.

After receiving this information, the AI generated structured drafts for both pages, including sections related to personal data collection, external services, Rooms, guest users, music requests, playlists, statistics, data retention, GDPR rights, security, acceptable use, third-party services, intellectual property, service availability, account termination, liability and governing law.

The generated text was subsequently incorporated into the corresponding Music Fever interface mock-ups and may be reviewed or adapted before any real-world deployment of the application.