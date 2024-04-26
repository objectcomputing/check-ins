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

The UI will be available on port 8080 once the application is running. The UI is served from the `web-ui` directory. The UI is built using Vite, a modern build tool for frontend development. The UI is written in JavaScript and uses React and Vitest.

## Node Version

When developing against the UI, please use the latest Node LTS. A `.nvmrc` file is provided in the `web-ui` directory to help you manage the Node version. You can use the following command to switch to the expected Node version:

```shell
nvm install --lts
nvm use --lts
```

Or `cd` to the `web-ui` directory and run `nvm use` to be prompted to install the expected Node version as configured in the `.nvmrc` file.

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

Or simply `cd` to the `web-ui` directory and run `yarn test`. Tests using Snapshots are likely to fail with component updates. Those which fail to meet their Snapshot will be marked as failed. You can update the Snapshot by running the following command:

```shell
yarn --cwd web-ui test -u # or simply `yarn test` followed by `u`
```

Testing Library is installed in the UI project. You can find more information about Testing Library [here](https://testing-library.com/docs/react-testing-library/intro/).

# Running the Server

## Running Tests

To skip building the UI when running unit tests in the Server application add the environment variable `SKIP_WEB_UI=true` to your system or run configuration.