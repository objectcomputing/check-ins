<!-- TOC -->

- [Check-ins](#check-ins)
- [Project Links](#project-links)
- [Setup](#setup)
    - [directory.json](#directory-json)
    - [credentials.json](#credentials-json)
    - [Running the application](#running-the-application)
- [Contributing](#contributing)

<!-- /TOC -->
[![Quality gate](https://sonarcloud.io/api/project_badges/quality_gate?project=oci-labs_check-ins)](https://sonarcloud.io/dashboard?id=oci-labs_check-ins)
![Gradle Build - Develop](https://github.com/oci-labs/check-ins/workflows/Gradle%20Build%20-%20Develop/badge.svg)

# Check-ins
<a id="markdown-check-ins" name="check-ins"></a>
This web application is written in [Micronaut](https://micronaut.io) for uploading files and tracking skill set of team members. This application will also be used for PDL checkins and to auto-generate resumes from the skill set.

# Project Links
<a id="markdown-project-links" name="project-links"></a>
**Project Document:** \*TBA\*

**Project Board:** [Board](https://github.com/oci-labs/check-ins/projects/1)

**Environments:** \*TBA\*

# Setup
<a id="markdown-setup" name="setup"></a>
There are two files required to run the application successfully. Both of which must be created and placed in
`src/main/resources/secrets`

### directory.json
<a id="markdown-directory-json" name="directory-json"></a>
This is a simple JSON file containing the identifier for the Google Drive folder into which the uploaded files are to be deposited.

```json
{
    "upload-directory-id": "GOOGLE_DRIVE_FOLDER_ID"
}
```

### credentials.json
<a id="markdown-credentials-json" name="credentials-json"></a>
This JSON file should create the generated credentials for a service account that has access to write to the identified Google Drive folder. Information on configuring GCP service account credentials can be [found here](https://cloud.google.com/iam/docs/creating-managing-service-account-keys).

Note: Be sure that the target Google Drive folder has edit access granted to the service account.

### Running the application
<a id="markdown-running-the-application" name="running-the-application"></a>
#### Installs
- [Docker](https://docs.docker.com/get-docker/)

#### Building
1. Start the database in a Docker container by running `docker-compose up` in a terminal.
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
4. Open the browser to run the application at `http://localhost:8080`
5. Access swagger-UI at - `http://localhost:8080/swagger-ui`

# Contributing
<a id="markdown-contributing" name="contributing"></a>
[Contributing](./CONTRIBUTING.md)



