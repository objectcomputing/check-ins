<!-- TOC -->

- [Overview](#overview)
- [Install Git](#install-git)
    - [Install Hub](#install-hub)
    - [SSH Keys](#ssh-keys)
- [Install jEnv](#install-jenv)
    - [Plugins](#plugins)
- [Install OpenJDK](#install-openjdk)
- [Install NVM](#install-nvm)
- [Install Yarn](#install-yarn)
- [Install Podman](#install-podman)

<!-- /TOC -->

# Overview

This document is intended to provide the quickest path to a fully functional
development environment for developers planning to contribute to the Check-Ins
project using Windows. All commands listed below are intended to be run in Windows PowerShell.

# Install Git

We recommend you install git using winget:

```
winget install --id Git.Git -e --source winget
```

Alternatively, you can download and run the appropriate [installer](https://git-scm.com/download/win) read the installer prompts carefully, as they will impact how you interact with git projects. Pay particular attention to the promp about the default editor. If you are not already familiar with Vim, you may be better served by chosing a simpler editor for now. (Vim is great...just not particularly intuitive.)

***Note: It is important that you close and relaunch Windows Powershell after this step.***

## SSH Keys

If you haven't already, you will want to [configure git to use ssh for connecting to GitHub](https://docs.github.com/en/free-pro-team@latest/github/authenticating-to-github/connecting-to-github-with-ssh).

# Clone the Project

We suggest that you check the project out into a `projects` directory or similar in your home directory. To create a `projects` directory and switch to it, you can run the following commmands:

```shell
cd ~
mkdir projects
cd projects
```

Next clone the git project, this will create a directory called `check-ins` inside your newly created `projects` directory.

```shell
git clone git@github.com:objectcomputing/check-ins.git
```

# Install OpenJDK

Install OpenJDK 14 with winget:

```shell
winget install -e --id ojdkbuild.openjdk.14.jdk
```

Alternatively, you can extract the zip file [found here](https://jdk.java.net/java-se-ri/14). This approach will require you to setup the necessary environment variables on your own.

# Install NVM

Install nvm via winget:

```shell
winget install -e --id CoreyButler.NVMforWindows
```

Alternatively, you can follow the instructions [found here](https://github.com/coreybutler/nvm-windows#installation--upgrades). 

***Note: It is important that you close and relaunch Windows Powershell after this step.***

# Install Node/NPM using NVM

```shell
nvm install latest
```

Now, tell NVM to use the version that you just installed

```shell
nvm use latest
```

# Install Yarn

```shell
npm install --global yarn
```

# Install Podman

This project uses [Podman](https://podman.io/) for containerization.

To install Podman with winget, run:

```shell
winget install -e --id RedHat.Podman
```

This will install the Windows Linux Subsystem (WSL) if it has not yet been installed and will require a restart.
**Note: If you have already installed the WSL,*** then you will need to close and relaunch your Windows PowerShell after the
podman installation in order to continue.

## Initialize a Podman Machine

```shell
podman machine init
```

## Start the Podman Machine

```shell
podman machine start
```

# Getting podman-compose

In order to install podman-compose, you need Python 3 first. So, we will start there.

## Install Python 3

```shell
 winget install -e --id Python.Python.3.11
```

***Note: It is important that you close and relaunch Windows Powershell after this step.***

## Install podman-compose

To install [podman-compose](https://github.com/containers/podman-compose), run:

```shell
pip3 install podman-compose
```

# Run Your First Build

***Note: The following commands should be run on the windows command line, not in Windows PowerShell.***

First, let's get back to where you cloned the project:

```shell
cd ~/projects/check-ins
```

Now, run a clean assembly of the project:

```shell
gradlew clean assemble
```

You are now ready to contribute to Check-Ins!
