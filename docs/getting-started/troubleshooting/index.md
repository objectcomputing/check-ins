---
title: Troubleshooting
parent: Getting Started
nav_order: 3
---

## Running the Server

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

## Connecting to the Database

Your database is running in a container. To connect to the database, use the following command:

```shell
podman exec -it $(podman ps -q --filter name=checkinsdb) psql -U postgres -d checkinsdb
```
(The subcommand `podman ps -q --filter name=checkinsdb` returns the ID of the container named `checkinsdb` which is running the database.)

## Finding the Container ID

To find the ID of running containers, you can use the `ps` command as below:

```shell
podman ps
```

## Rebuilding the database

Sometimes when switching between branches, you will start getting migration errors from Flyway that look similar to:

```shell
Migration checksum mismatch for migration version 108
-> Applied to database : -976138718
-> Resolved locally    : -377557062
Either revert the changes to the migration, or run repair to update the schema history.
```

Or you may see errors like:

```shell
13:24:52.282 [main] ERROR io.micronaut.runtime.Micronaut - Error starting Micronaut server: Bean definition [javax.sql.DataSource] could not be loaded: Script R__Load_testing_data.sql failed
--------------------------------------
SQL State  : 23503
Error Code : 0
Message    : ERROR: update or delete on table "team" violates foreign key constraint "kudos_teamid_fkey" on table "kudos"
```

With errors about tables that should not exist.
These are usually caused by one of the branches adding a migration that conflicts with the current branch that you are on.

The easiest resolution is to rebuild your database from scratch.
To do this, stop any container commands you have running in the terminal, and then you can run the following commands:

```shell
podman compose rm -f # This will stop and delete the database container
podman compose up    # This will recreate and start the database container
```

## Misc

_Note - If you are getting a error of
`org.postgresql.util.PSQLException: FATAL: database "checkinsdb" does not exist`
you will need to delete your local copy of postgres and only use the docker version_

## I'm using Docker, not Podman!

That's fine.
All the commands above work with Docker as well, you just need to replace `podman` with `docker`.
