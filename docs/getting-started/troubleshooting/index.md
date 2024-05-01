---
title: Troubleshooting
parent: Getting Started
---

### Running the Server

If you see the following error when running the server:

```shell
> Task :server:compileJava FAILED
```

Clean the project by running the following command from the root directory:

```shell
./gradlew clean assemble
```

Then try running the server again. If necessary, you can also try restarting the Gradle daemon:

```shell
./gradlew --stop
```

### Connecting to the Database

Your database is running in a Podman container. To connect to the database, use the following command:

```shell
podman exec -it 19a4d6a64439 psql -U postgres -d checkinsdb
```

Where `19a4d6a64439` is the container ID. You can find the container ID by running the following command:

```shell
podman ps
```

### Misc

_Note - If you are getting a error of
`org.postgresql.util.PSQLException: FATAL: database "checkinsdb" does not exist`
you will need to delete your local copy of postgres and only use the docker version_
