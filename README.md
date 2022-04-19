[![Gradle Build & Deploy - Develop](https://github.com/objectcomputing/check-ins/actions/workflows/gradle-build-development.yml/badge.svg)](https://github.com/objectcomputing/check-ins/actions/workflows/gradle-build-development.yml)
[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-2.1-4baaaa.svg)](CODE_OF_CONDUCT.md)

<!-- TOC -->

- [Check-ins](#check-ins)
- [Project Links](#project-links)
- [Setup](#setup)
    - [directory.json](#directoryjson)
    - [credentials.json](#credentialsjson)
    - [Running the application](#running-the-application)
- [Contributing](#contributing)

<!-- /TOC -->

# Check-ins
This web application is written in [Micronaut](https://micronaut.io) for uploading files and tracking skill set of team members. This application will also be used for PDL checkins and to auto-generate resumes from the skill set.

# Project Links
**Project Document:** \*TBA\*

**Project Board:** [Board](https://github.com/objectcomputing/check-ins/projects)

**Environments:** \*TBA\*

# Setup
There are two files required to run the application successfully. Both of which must be created and placed in
`src/main/resources/secrets`

### directory.json
This is a simple JSON file containing the identifier for the Google Drive folder into which the uploaded files are to be deposited.

```json
{
    "upload-directory-id": "GOOGLE_DRIVE_FOLDER_ID"
}
```

### credentials.json
This JSON file should create the generated credentials for a service account that has access to write to the identified Google Drive folder. Information on configuring GCP service account credentials can be [found here](https://cloud.google.com/iam/docs/creating-managing-service-account-keys).

Note: Be sure that the target Google Drive folder has edit access granted to the service account.

## Running the application

#### Installs
- [Podman](https://podman.io/)
- [Podman-Compose](https://github.com/containers/podman-compose)

#### Building
1. Start the database in a Podman container:
    * Initialize and start a Podman VM:
        ```shell
        $ podman machine init
        $ podman machine start 
        ```
    * Start the Podman container:
        ```shell
        $ podman-compose up
        ```
2. In a different terminal, execute the following commands : 
    * On Bash/Zsh -
        ```sh
        $ OAUTH_CLIENT_ID=<Insert_Client_ID> OAUTH_CLIENT_SECRET=<Insert_Client_Secret> MICRONAUT_ENVIRONMENTS=local ./gradlew build
        ```
        ```sh
        $ ./gradlew assemble
        ```
        ```sh
        $ OAUTH_CLIENT_ID=<Insert_Client_ID> OAUTH_CLIENT_SECRET=<Insert_Client_Secret> MICRONAUT_ENVIRONMENTS=local ./gradlew run
        ```
    
    * On Powershell/Command-Line -
        Set the following environment variables -
        ```sh
        MICRONAUT_ENVIRONMENTS=local
        OAUTH_CLIENT_ID=<Client_ID>
        OAUTH_CLIENT_SECRET=<Client_Secret>
        ```
        Build and run the application - 
        ```sh
        $ gradlew build
        ```
        ```sh
        $ gradlew assemble
        ```
        ```sh
        $ gradlew run
        ```
3. Open the browser to run the application at `http://localhost:8080`
4. Access swagger-UI at - `http://localhost:8080/swagger-ui`

# Contributing
[Contributing](./CONTRIBUTING.md)



