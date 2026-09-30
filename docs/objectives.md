# 🎯 Web Application Objectives
This section outlines the different objectives of the application. They are classified into two categories: _functional_ and _technical_ objectives.

## ⚙️ Functional Objectives
Music Fever aims to be a music management and listening platform that allows users to search for and organize their favorite songs into their own playlists. The main novelty that this application brings compared to its competitors is the possibility of creating collaborative and fair playback queues, where all participants can contribute songs without monopolizing the queue. In addition, Music Fever allows users to view both personal statistics and statistics related to the rooms they have created.

The main functional objectives are:

* **User management:** the application will manage anonymous users, registered users, and administrators, controlling the limitations of each role and the actions they are allowed to perform.
* **Music search:** search for artists, albums, and songs available in the system.
* **Account management:** users will be able to manage their private profile information, log out, and delete their account if they choose to do so.
* **Creation of and participation in rooms:** users will be able to join a room, with only one active room allowed at a time, and they will be able to create one if they meet the required account conditions.
* **Collaborative queue management:** the room will automatically manage the priority of each song added by a user, preventing any single user from monopolizing the queue.
* **Statistics visualization:** the application will collect data and calculate general and personal statistics that will be made available to the user.
* **Request management:** users will be able to send requests to administrators to load songs that are not yet available in the application.
* **Administrator functionalities:** administrators will manage requests and search external APIs directly for data that has not yet been loaded into the system.

## 🛠️ Technical Objectives
From a technical perspective, the objective is to design a maintainable and modular full-stack application capable of connecting to external APIs in order to retrieve the required data and reproduce songs. Collaborative queues will synchronize their state in real time across all connected devices, allowing all participants to know what is currently playing and their estimated position in the queue.

The main technical objectives are:

* **Full-stack implementation:** frontend implemented in Angular and backend implemented in Spring Boot.
* **Real-time communication:** use of WebSockets to synchronize devices within a room in real time.
* **Integration with external APIs:** integration with external APIs, especially Spotify.
* **Authentication and authorization:** implementation of authentication and authorization mechanisms for role management, together with OAuth authentication for connecting Spotify accounts.
* **Data persistence and integrity:** guarantee data persistence and integrity through an application-specific database.
* **Code quality and maintainability:** implementation of unit, integration and E2E tests to verify the correct behavior of the application, together with SonarCloud to continuously monitor code quality.
* **Responsive design:** development of a responsive interface to ensure comfortable use across different devices.
* **Development workflow**: use of GitHub Flow as the development methodology, organizing work through feature branches and pull requests before integrating changes into the main branch.
* **Continuous Integration and Continuous Deployment:** automation of integration and deployment processes through GitHub Actions, with deployment on Railway.

[<-- Back to README](../README.md)