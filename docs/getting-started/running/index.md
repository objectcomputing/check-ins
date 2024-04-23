---
title: Running the Application
parent: Getting Started
---

## Basic Development

To run the application, you will need to complete the [Setup steps](../setup). Once you have completed the Setup steps, you can run the application as described below.

First start the database service using Podman Compose. From the root of the project, run the following command:

```shell
podman-compose up -d
```

Then run the application:

```shell
./gradlew :server:run
```

This step requires your environment to be set. Reach out to the team for the necessary values. You can access the application by navigating to [http://localhost:8080](http://localhost:8080) in your browser.

Next login to the application with:

- **Username**: existinguser@objectcomputing.com
- **Password**: SUPER

You can use any email that the system has in its loaded test data. The "password" is actually a role name. Most of the time you will want to use `SUPER` which is just an alias for all the roles.

## HMR

For hot reloading during UI development, you can use the following command to start a Vite server:

```shell
yarn --cwd web-ui start
```

When developing the UI, you can access the Vite server at [http://localhost:5173](http://localhost:5173).
