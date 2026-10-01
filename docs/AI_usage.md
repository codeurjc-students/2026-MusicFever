# AI Usage in Music Fever Development

## Entry Structure
#### Entry Details
| Entry details | Value |
| --- | --- |
| **Date** | ... |
| **Phase** | 2 (Prepare the repository) |
| **Objective** | ... |
| **AI Tool** | ChatGPT |
| **Tool version** |... |

#### Tool Configuration
| Setting | Value |
| --- | --- |
| **Model** | ... |
| **Reasoning level** | ... |
| **Interaction mode** | ... |
| **Agent mode** | ... |
| **Plan mode** | ... |
| **Web search** | ... |
| **Connected tools** | ... |

#### Usage
[Resumen del por qué te he pedido ayuda o en que te he pedido ayuda. Corto pero descriptivo. No tiene que ser técnico, es una documentación de por qué se utiliza la IA]

## 0. Through the Whole Project
### 0.1 Translation
#### Entry Details
| Entry details | Value |
| --- | --- |
| **Date** | Throughout the entire project |
| **Phase** | All phases |
| **Objective** | Improve the clarity and quality of the project documentation written in English |
| **AI Tool** | ChatGPT |
| **Tool version** | GPT-5.6 |

#### Tool Configuration
| Setting | Value |
| --- | --- |
| **Model** | GPT-5.6 Sol |
| **Reasoning level** | Default |
| **Interaction mode** | Chat |
| **Agent mode** | Disabled |
| **Plan mode** | Disabled |
| **Web search** | Disabled |
| **Connected tools** | None |

#### Usage
The AI has been used throughout the project to translate README documentation from Spanish into English and to review and correct English expressions, improving grammar, wording, and clarity while preserving the original technical meaning.

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
| Entry details    | Value       |
| ---------------- | -------- |
| **Date**         | 29-08-2026    |
| **Phase**        | 1 (Documentation)     |
| **Objective**    | Generate the initial content for the Privacy Policy and Terms of Service pages according to the functionality and data management requirements of Music Fever |
| **AI Tool**      | ChatGPT   |
| **Tool version** | GPT-5.6 Sol  |
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


## 3. Workflow Design
### 3.1 Help implementing the workflows
#### Entry details

| Entry details    | Value   |
| ---------------- | --------------------- |
| **Date**         | 18-09-2026  |
| **Phase**        | 2 (Prepare the repository) |
| **Objective**    | Define the organization of backend and frontend tests (unit, integration and E2E) and the corresponding GitHub Actions workflow commands to run frontend and backend unit tests independently |
| **AI Tool**      | ChatGPT     |
| **Tool version** | GPT-5.6 Sol  |

#### Tool Configuration

| Setting              | Value                    |
| -------------------- | ------------------------ |
| **Model**            | GPT-5.6 Sol              |
| **Reasoning level**  | Not explicitly specified |
| **Interaction mode** | Chat                     |
| **Agent mode**       | Disabled                 |
| **Plan mode**        | Disabled                 |
| **Web search**       | Not used                 |
| **Connected tools**  | None used                |

#### Usage
The AI was used to clarify where to locate the frontend test folders and which relative paths and commands should be used in GitHub Actions so that the corresponding test jobs run correctly.

### 3.2 Configurate coverage analysis
#### Entry Details
| Entry details    | Value  |
| ---------------- | -------------- |
| **Date**         | 21-09-2026   |
| **Phase**        | 2 (Prepare the repository) |
| **Objective**    | Configure unit-test coverage for backend and frontend, organize unit/integration/E2E tests, and adapt the Basic Quality Check workflow so that only unit tests are executed while Sonar analyzes both backend and frontend coverage |
| **AI Tool**      | ChatGPT  |
| **Tool version** | GPT-5.6 Sol |

#### Tool Configuration
| Setting              | Value                                                         |
| -------------------- | ------------------------------------------------------------- |
| **Model**            | GPT-5.6 Sol                                                   |
| **Reasoning level**  | Not explicitly specified                                      |
| **Interaction mode** | Chat                                                          |
| **Agent mode**       | Disabled                                                      |
| **Plan mode**        | Disabled                                                      |
| **Web search**       | Used                                                          |
| **Connected tools**  | None connected; GitHub plugin was suggested but not connected |

#### Usage
The AI was used to configure code coverage and test execution for the repository's backend and frontend. For the Maven/Spring Boot backend, JaCoCo was configured to generate coverage reports and Maven Surefire was restricted to tests located under the unit package so that the Basic Quality Check does not execute integration or E2E tests. Meanwhile, for the Angular frontend, the existing Vitest setup was extended with @vitest/coverage-v8.

It also suggested to create a root-level sonar-project.properties configuration was introduced so that a single Sonar analysis can process both parts of the application. The backend contributes the JaCoCo XML report and the frontend contributes the LCOV report, allowing backend and frontend coverage to be represented within the same Sonar project.

## 4. Minimal Functionality (Implementation)
### 4.1 Connect H2-Database 
#### Entry Details
| Entry details    | Value  |
| ---------------- | -------------- |
| **Date**         | 25-09-2026   |
| **Phase**        | 2 (Prepare the repository) |
| **Objective**    | Configure access to the H2 database during development and adapt Spring Security so that the H2 console can be used correctly |
| **AI Tool**      | ChatGPT  |
| **Tool version** | GPT-5.6 Sol |

#### Tool Configuration
| Setting              | Value                                                          |
| -------------------- | -------------------------------------------------------------- |
| **Model**            | GPT-5.6 Sol                                                    |
| **Reasoning level**  | Not explicitly specified                                       |
| **Interaction mode** | Chat                                                           |
| **Agent mode**       | Disabled                                                       |
| **Plan mode**        | Disabled                                                       |
| **Web search**       | Used                                                           |
| **Connected tools**  | None connected                                                 |

#### Usage
The AI was used to help configure and access the H2 development database in Spring Boot, mainly resolving issues related to the H2 console and Spring Security permissions.

### 4.2 Configurate CORS
#### Entry Details
| Entry details | Value |
| --- | --- |
| **Date** | 27-09-2026 |
| **Phase** | 2 (Prepare the repository) |
| **Objective** | Configure CORS and Spring Security so the Angular frontend can access the REST API while keeping selected endpoints protected by authentication |
| **AI Tool** | ChatGPT |
| **Tool version** | GPT-5.6 Sol |

#### Tool Configuration
| Setting | Value |
| --- | --- |
| **Model** | GPT-5.6 Sol |
| **Reasoning level** | Not explicitly specified |
| **Interaction mode** | Chat |
| **Agent mode** | Disabled |
| **Plan mode** | Disabled |
| **Web search** | Not used |
| **Connected tools** | None connected |

#### Usage
The AI was asked for help configuring the application's Spring Security setup so that requests from the frontend could be received correctly.

### 4.3 Help with Angular
#### Entry Details
| Entry details | Value |
| --- | --- |
| **Date** | 27-09-2026 |
| **Phase** | 2 (Prepare the repository) |
| **Objective** | Implement the Angular frontend logic required to retrieve a list of tracks from the backend REST API and display them using reusable components |
| **AI Tool** | ChatGPT |
| **Tool version** | GPT-5.6 Sol |

#### Tool Configuration
| Setting | Value |
| --- | --- |
| **Model** | GPT-5.6 Sol |
| **Reasoning level** | Not explicitly specified |
| **Interaction mode** | Chat |
| **Agent mode** | Disabled |
| **Plan mode** | Disabled |
| **Web search** | Not used |
| **Connected tools** | None connected |

#### Usage
The AI was asked for help understanding how to organize and structure the Angular frontend, as well as for assistance implementing some basic frontend tasks due to limited prior experience with Angular.

## 5. Minimal Functionality (Testing)
### 5.1 Testing with Rest-Assure
#### Entry Details
| Entry details | Value |
| --- | --- |
| **Date** | 28-09-2026 |
| **Phase** | 2 (Prepare the repository) |
| **Objective** | Implement an E2E test for the REST API to verify that the example Track data can be retrieved correctly. |
| **AI Tool** | ChatGPT |
| **Tool version** | GPT-5.6 Sol |

#### Tool Configuration
| Setting | Value |
| --- | --- |
| **Model** | GPT-5.6 Sol |
| **Reasoning level** | Default |
| **Interaction mode** | Chat |
| **Agent mode** | Disabled |
| **Plan mode** | Disabled |
| **Web search** | Not used |
| **Connected tools** | None |

#### Usage
I asked for help understanding the basic REST Assured syntax and how to use it with Spring Boot for an E2E test. The conversation covered `when()`, `get()`, `then()`, `Response`, `Hamcrest` matchers such as `hasItem` and how to verify that predefined Track data stored in H2 is returned by the REST API. Then it was asked how to connect the database with the test and it explained how to use `@SpringBootTest` with a random port.

### 5.2 Testing with TestContainers
#### Entry Details
| Entry details | Value |
| --- | --- |
| **Date** | 2026-09-28 |
| **Phase** | 2 (Prepare the repository) |
| **Objective** | Configure and understand the server-side integration testing setup using Testcontainers and PostgreSQL, while keeping H2 where appropriate. |
| **AI Tool** | ChatGPT |
| **Tool version** | GPT-5.6 Sol |

#### Tool Configuration
| Setting | Value |
| --- | --- |
| **Model** | GPT-5.6 Sol |
| **Reasoning level** | Default |
| **Interaction mode** | Chat |
| **Agent mode** | Not used |
| **Plan mode** | Not used |
| **Web search** | Used occasionally to verify current Spring Boot and Testcontainers configuration details |
| **Connected tools** | None |

#### Usage
The AI was used to clarify the integration testing strategy for the backend and to configure Testcontainers with PostgreSQL. It also assisted in reviewing Maven dependencies, configuring the integration test class to provide a PostgreSQL database through Testcontainers, handling dependency injection, preparing test data with `@BeforeEach`, and diagnosing errors found during test execution.

The test itself was initially implemented without AI assistance. Afterwards, the AI was asked to review the implementation and suggest possible improvements, which resulted in a cleaner and more robust version of the test.

### 5.3 Help implementing testing with Vitest
#### Entry Details
| Entry details | Value |
| --- | --- |
| **Date** | 29/09/2026 |
| **Phase** | 2 (Prepare the repository) |
| **Objective** | Configure and understand client-side testing for the Angular frontend, including unit and client-server integration tests. |
| **AI Tool** | ChatGPT |
| **Tool version** | GPT-5.6 Sol |

#### Tool Configuration
| Setting | Value |
| --- | --- |
| **Model** | GPT-5.6 Sol |
| **Reasoning level** | Default |
| **Interaction mode** | Conversational guidance |
| **Agent mode** | Not used |
| **Plan mode** | Not used |
| **Web search** | Not used |
| **Connected tools** | None |

#### Usage
The AI was used to understand and configure the Angular client testing workflow. The assistance focused on clarifying the role of components, services, inputs, TestBed and fixtures, how to replace services with mocks using Vitest, how to inspect the rendered DOM, and how to implement both a unit test for the track list and an integration test against the real REST API.

### 5.4 Describing an error
#### Entry Details
| Entry details | Value |
| --- | --- |
| **Date** | 29/09/2026 |
| **Phase** | 2 (Prepare the repository) |
| **Objective** | Solve an error when executing backend tests|
| **AI Tool** | ChatGPT |
| **Tool version** | GPT-5.6 Sol |

#### Tool Configuration
| Setting | Value |
| --- | --- |
| **Model** | GPT-5.6 Sol |
| **Reasoning level** | Default |
| **Interaction mode** | Conversational guidance |
| **Agent mode** | Not used |
| **Plan mode** | Not used |
| **Web search** | Not used |
| **Connected tools** | None |

#### Usage
The following error encountered during test execution was provided to ChatGPT for troubleshooting:
```terminal
[ERROR] Surefire is going to kill self fork JVM. The exit has elapsed 30 seconds after System.exit(0).
```
The AI helped identify the integration test as the source of the issue and suggested closing the Spring test context after the test class using @DirtiesContext.

### 5.5 Configurate Environments
#### Entry Details
| Entry details | Value |
| --- | --- |
| __Date__ | 2026-10-01 |
| __Phase__ | 2 (Prepare the repository) |
| __Objective__ | Configure separate execution environments for development, unit testing, integration/E2E testing, and system testing, with the appropriate database setup for each environment. |
| __AI Tool__ | ChatGPT |
| __Tool version__ | GPT-5.6 Sol |

#### Tool Configuration
| Setting | Value |
| --- | --- |
| __Model__ | GPT-5.6 Sol |
| __Reasoning level__ | Default / adaptive |
| __Interaction mode__ | Conversational assistance |
| __Agent mode__ | Not used |
| __Plan mode__ | Step-by-step guidance |
| __Web search__ | Used occasionally to verify current Spring Boot/Testcontainers configuration details |
| __Connected tools__ | None |

#### Usage
The AI was used to guide the configuration of the project execution environments and testing setup. The assistance focused on separating development, unit, integration/E2E, and system-test environments; configuring PostgreSQL, H2, and Testcontainers appropriately; adapting Spring profiles; and updating the CI workflow so each type of test runs against the correct database without interfering with the others.