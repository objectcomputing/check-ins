---
title: WindowsOS
parent: Setup
grand_parent: Getting Started
---

<!-- TOC -->

- [Install Chocolatey](#install-chocolatey)
- [Install Git](#install-git)
  - [Install Hub](#install-hub)
  - [SSH Keys](#ssh-keys)
- [Install jEnv](#install-jenv)
  - [Plugins](#plugins)
- [Install OpenJDK](#install-openjdk)
- [Install NVM](#install-nvm)
- [Install Yarn](#install-yarn)
- [Install Podman](#install-podman)
- [Install Jekyll](#install-jekyll)

<!-- /TOC -->

# Overview

This document is intended to provide the quickest path to a fully functional
development environment for developers planning to contribute to the Check-Ins
project using WindowsOS.

# Install Chocolatey (Optional)

Note: If you prefer to use Windows installers for installing required software, then you can skip installing Chocolatey.

Chocolatey is a package manager for Windows that automates the process of installing, updating, and configuring software.
It is similar to package managers like apt-get on Ubuntu or Homebrew on macOS.
For complete information on using Chocolatey, visit https://docs.chocolatey.org/en-us/getting-started

Installation guide: https://chocolatey.org/install

# Install Git

From the commandline run:

```shell
git --version
```

If git is not yet installed on your machine, then you can install via Chocolatey:

```shell
choco install git.install
```

Or use the latest installer: https://git-scm.com/download/win

## Install Hub

Since this project is hosted on GitHub, using [Hub](https://github.com/github/hub) will make many common tasks more
convenient. You can install hub via Chocolatey:

```shell
choco install hub
```

### Create a git alias (optional)

For bash, add the line to your `.bash_profile` or `.bashrc` or `.gitconfig` file.

```shell
alias git=hub
```

## SSH Keys

If you haven't already, you will want to [configure git to use ssh for connecting to GitHub.](https://docs.github.com/en/free-pro-team@latest/github/authenticating-to-github/connecting-to-github-with-ssh)

# Install OpenJDK

Install OpenJDK 17 with Chocolatey:

```shell
choco install openjdk --version=17.0.0
```

or download the laster installer: https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html

# Install NVM

In order to install or update NVM, you need to run the following from the command line:

```shell
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.37.0/install.sh | bash
```

## Enable NVM Shell Completion

Add the following to the appropriate shell config (.zshrc, etc.)

```shell
export NVM_DIR="$HOME/.nvm"
[ -s "$NVM_DIR/nvm.sh" ] && \. "$NVM_DIR/nvm.sh"  # This loads nvm
[ -s "$NVM_DIR/bash_completion" ] && \. "$NVM_DIR/bash_completion"  # This loads nvm bash_completion
```

# Install Yarn

```shell
choco install yarn
```

# Install Podman

This project uses [Podman](https://podman.io/) for containerization.

Install Podman Desktop: https://podman-desktop.io/

The setup process will ask if you want to install Podman and Podman Compose for the cli.
Be sure to do this.

# Install Jekyll

This project uses Jekyll for working with documentation:

https://jekyllrb.com/docs/installation/windows/
