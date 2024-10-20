---
title: MacOS
parent: Setup
grand_parent: Getting Started
---

<!-- TOC -->

- [Install Homebrew](#install-homebrew)
  - [Shell Completions](#shell-completions)
- [Install Git](#install-git)
  - [Install Hub](#install-hub)
  - [SSH Keys](#ssh-keys)
- [Install jEnv](#install-jenv)
  - [Plugins](#plugins)
- [Install JDK](#install-jdk)
- [Install NVM](#install-nvm)
- [Install Yarn](#install-yarn)
- [Install Podman](#install-podman)

<!-- /TOC -->

# Overview

This document is intended to provide the quickest path to a fully functional
development environment for developers planning to contribute to the Check-Ins
project using MacOS.

# Install Homebrew

Homebrew is the missing package manager for macOS. For complete information on using Homebrew, visit https://brew.sh

The fastest path to installing Homebrew is to run the following from the command line:

```shell
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install.sh)"
```

Before the script above, it's recommended you review the script contents at https://raw.githubusercontent.com/Homebrew/install/master/install.sh. This will ensure you understand what the script is doing before running it.

If you run into any hiccups during installation, visit https://brew.sh/ for more information.

## Shell Completions (optional)

For the most recent instructions for enabling command shell auto-completions,
see the [Homebrew Shell Completion Instructions](https://github.com/Homebrew/brew/blob/master/docs/Shell-Completion.md)
for [bash](https://github.com/Homebrew/brew/blob/master/docs/Shell-Completion.md#configuring-completions-in-bash),
[zsh](https://github.com/Homebrew/brew/blob/master/docs/Shell-Completion.md#configuring-completions-in-zsh), and
[fish](https://github.com/Homebrew/brew/blob/master/docs/Shell-Completion.md#configuring-completions-in-fish).

# Install Git

Git is a distributed version control system that is used by the Check-Ins project. It is available via Homebrew and may already be installed on your Mac.

To check if git is installed, run the following command:

```shell
git --version
```

If git is not yet installed on your Mac, you may be prompted to install the XCode command line tools. To install XCode command line tools run command `xcode-select --install`.

You can install git via Homebrew:

```shell
brew install git
```

## Install Hub

Since this project is hosted on GitHub, using [Hub](https://github.com/github/hub) will make many common tasks more convenient. You can install hub via Homebrew by running the following command:

```shell
brew install hub
```

Hub is an alternative to the [GitHub CLI](https://cli.github.com/). If you prefer to use the GitHub CLI, you can install it with `brew install gh`. A list of differences between the two can be found in the [GitHub CLI documentation](https://github.com/cli/cli/blob/trunk/docs/gh-vs-hub.md). If you choose to use the GitHub CLI, skip the following steps for creating a git alias.

You may wish to use `hub` as a drop-in replacement for `git`. To do this, you can create a git alias.

### Create a git alias (optional)

If your default shell is zsh (macOS default) then add the following line to your `~/.zshrc` file. For bash, add the line to
your `.bash_profile` or `.bashrc` file.

```shell
alias git=hub
```

## SSH Keys

If you haven't already, you will want to [configure git to use ssh for connecting to GitHub](https://docs.github.com/en/free-pro-team@latest/github/authenticating-to-github/connecting-to-github-with-ssh). If you're using [gpg-agent for SSH authentication](https://blogs.gentoo.org/mgorny/2018/05/12/on-openpgp-gnupg-key-management/), ensure your GPG keys are added to the agent.

# Install JDK

JDK is required to build and run the Check-Ins project. You can install a specific version of the JDK using SDKMAN. First, install SDKMAN by running the following command:

```shell
curl -s "https://get.sdkman.io" | bash
```

Before running the above command, it's recommended you review the script contents at https://get.sdkman.io. This will ensure you understand what the script is doing before running it.

Then, install the JDK with:

```shell
sdk install java 21.0.4-tem
sdk use java 21.0.4-tem
sdk default java 21.0.4-tem
```

We also support using [GraalVM Community Edition](https://github.com/oracle/graal/) to build native executables. In order to test the GraalVM-built native executables, you will need to install GraalVM as well:

```shell
sdk install java 21.0.2-graalce
```

You can switch to the GraalVM compiler by running:

```shell
sdk use java 21.0.2-graalce
```

Please note JDK `21` is the latest version at the time of writing. See the `.java-version` file in the project root for the version of the JDK required for this project. You will also find versions in `server/Dockerfile`.

# Install NVM

> **Note:** If you have previously installed NVM, you should update it to the latest version. As an alternative to NVM, the [Fast Node Manager](https://github.com/Schniz/fnm) can be used as well.

To install or update NVM, run the following from the command line:

```shell
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.40.1/install.sh | bash
```

Where `v0.40.1` is the latest version of NVM at the time of writing. You can find the latest version on the [NVM Releases page](https://github.com/nvm-sh/nvm/releases) on GitHub.

After installing NVM, you will need to restart your terminal or run the following command to start using NVM:

```shell
source ~/.nvm/nvm.sh
```

See the [NVM GitHub page](github.com/nvm-sh/nvm/) for additional help and information.

## Enable NVM Shell Completions (optional)

Add the following to the appropriate shell config (.zshrc, etc.)

```shell
export NVM_DIR="$HOME/.nvm"
[ -s "$NVM_DIR/nvm.sh" ] && \. "$NVM_DIR/nvm.sh"  # This loads nvm
[ -s "$NVM_DIR/bash_completion" ] && \. "$NVM_DIR/bash_completion"  # This loads nvm bash_completion
```

## Install Correct Node Version

Install and use the correct node version for the project.

```shell
nvm install 22.9 #this will download the latest patch version
nvm use 22.9 #this will use the latest patch version
```

If you are not working on other node projects, you may want to set this as your default. Otherwise, you will need to make sure you are using the correct version everytime you restart your terminal.
To set your default, please use the following:

```shell
nvm alias default 22.9 #this will set the latest patch version to default
```

# Install Yarn

```shell
brew install yarn
```

You should now have `yarn` available on your command line. Check the version with:

```shell
yarn --version
```

You should be using Yarn 1.22.19 or later.

# Install Podman

> If you are using Docker Desktop, you can skip this section, and set an environment variable via `export USE_DOCKER_DESKTOP=1` in your shell profile.
> This will enable Ryuk for cleaning up old containers under testing.

This project uses [Podman](https://podman.io/) for containerization. Podman is a daemonless container engine for developing, managing, and running Open Container Initiative containers. It is an alternative to Docker and is available without fee for macOS via Homebrew.

To install Podman, run:

```shell
brew install podman
```

To install [podman-compose](https://github.com/containers/podman-compose), run:

```shell
brew install podman-compose
```
