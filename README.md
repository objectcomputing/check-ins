# Overview
This repository contains a micronaut application for uploading files and tracking skill set of team members. This application will also be used for PDL checkins and auto generated resumes from the skill set.

# Getting Started
To build this module, execute : `./gradlew assembleServerAndClient`. Please run this command twice. 
Once the build is successful then execute: `./gradlew run` and open the browser to run the application at `http://localhost:8080`.
# Google Drive Upload
Simple uploader application written in [Micronaut](https://micronaut.io) and targeted at GCP for uploading files to a limited access Google Drive folder.

## Configuration
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