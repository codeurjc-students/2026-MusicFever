# 📅 Project Tracking
This document presents the different methods used to track, document, and illustrate the development process of the application.

## ✍️ Blog
To follow the development of this project, an account on the _Medium_ website will be created to write blog entries on each phase or when the application is updated. To access the blog you can click on the following [link](https://medium.com/@armingarc).

## 🐙 GitHub Project
The tasks management and planning will be performed through GitHub Projects. A board, similar to the ones used in Kanban projects, has been created with the following columns:
- __Backlog__: items in backlog are ideas for the project that are not ready to be picked up in the moment

- __Ready__: items are ready to be picked up and moved to _In Progress_

- __In Progress__: items that are being worked on at the moment

- __Pending Merge__: completed items in their specific pull request and awaiting tests or merge into main

- __Done__: complete items

The issues (tasks in GitHub) will be sorted onto these columns depending on the state of each one during the project development.

> [!NOTE]
> The issues on _Pending Merge_ actually are the completed sub-issues of the issue (parent) associated with the open pull request.

### Issue Automatic Status Updates
Issues in GitHub have a number associated with which they can be referenced in commits or pull requests to indicate when should they be closed. This allows issues to be closed automatically without having to use the GitHub interface; however, the changes will not be reflected on the GitHub Projects board.

Using the GitHub API GraphQL (GitHub API v4) the movement of the issues through the board can be automated when an issue is closed. By combining this with GitHub Actions, updates can be triggered when pushing a commit, opening a PR, etc. In this project, three workflows have been created to automate the main issue status transitions.

| Workflow          | Move                  | Description   |
| ----------------- | -----------------     | ------------- |
| issue-to-done     | Close issue to _Done_ | When an issue is closed, it is moved to the Done column. |
| issue-to-pending  | Close sub-issue to _Pending Merge_ | When a commit on a non-default branch references a sub-issue as completed, the sub-issue is moved to Pending Merge.|
| open-issues-status| Open issues to _Ready_/ _In Progress_| When an issue is reopened, it is moved to Ready. When a pull request associated with an issue is opened, the parent issue is moved to In Progress and its open sub-issues are moved to Ready.|

> [!NOTE]
> Some of the movements, as the Ready to In Progress move on sub-issues, have to be done manually because these are movements decided by the developer that do not depend on code or a file from the repository

The board can be viewed via the following [link](https://github.com/orgs/codeurjc-students/projects/50).

## 📱 Instagram
In addition to the blog mentioned above, an Instagram account has also been created to follow the project’s development from a different perspective. It will be used to share explanatory posts, provide updates and news, and offer a more behind-the-scenes look at the development process. The account can be found at the following [link](https://www.instagram.com/musicfever_2026/).

> [!IMPORTANT]
> The account is currently private in order to protect the project’s privacy, as it forms part of a Final Degree Project, and to preserve the integrity of the Spotify and Apple Music brands, even though their respective design and branding guidelines are being followed. Access therefore requires sending a follow request. However, there are currently no strict restrictions on accepting requests, and most will generally be approved. The account may be made public in the future.