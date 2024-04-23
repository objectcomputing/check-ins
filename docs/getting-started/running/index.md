---
title: Running the Application
parent: Getting Started
---

# Basic Development

To run the application, you will need to complete the [Setup steps](../setup). Once you have completed the Setup steps, you can run the application as described below.

First start the database service using Podman Compose. From the root of the project, run the following command:

```shell
podman-compose up -d
```

Then run the application:

```shell
./gradlew :server:run
```

This step requires your environment to be set. Reach out to the team for the `run.sh` file which, in addition to setting your environment, will execute the `:server:run` gradle task.

You can access the application by navigating to [http://localhost:8080](http://localhost:8080) in your browser.

Next login to the application with:

- **Username**: existinguser@objectcomputing.com
- **Password**: SUPER

You can use any email that the system has in its loaded test data. The "password" is actually a role name. Most of the time you will want to use `SUPER` which is just an alias for all the roles.

# Running the UI

## HMR

For hot reloading during UI development, you can use the following command to start a Vite server:

```shell
yarn --cwd web-ui start
```

Or simply `cd` to the `web-ui` directory and run `yarn start`. When developing the UI, you can access the Vite server at [http://localhost:5173](http://localhost:5173).

## Running Tests

To run the UI tests, use the following command:

```shell
yarn --cwd web-ui test
```

Or simply `cd` to the `web-ui` directory and run `yarn test`.
