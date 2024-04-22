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
- [Install OpenJDK](#install-openjdk)
- [Install NVM](#install-nvm)
- [Install Yarn](#install-yarn)
- [Install Podman](#install-podman)

<!-- /TOC -->

# Overview

This document is intended to provide the quickest path to a fully functional
development environment for developers planning to contribute to the Check-Ins
project using MacOS.

# Install Homebrew

For complete information on using Homebrew, visit https://brew.sh

The fastest path to installing Homebrew is to run the following from the command line in your terminal window:

```shell
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install.sh)"
```

## Shell Completions

For the most recent instructions for enabling command shell auto-completions,
see the [Homebrew Shell Completion Instructions](https://github.com/Homebrew/brew/blob/master/docs/Shell-Completion.md)
for [bash](https://github.com/Homebrew/brew/blob/master/docs/Shell-Completion.md#configuring-completions-in-bash),
[zsh](https://github.com/Homebrew/brew/blob/master/docs/Shell-Completion.md#configuring-completions-in-zsh), and
[fish](https://github.com/Homebrew/brew/blob/master/docs/Shell-Completion.md#configuring-completions-in-fish)

# Install Git

From the commandline run:

```shell
git --version
```

If git is not yet installed on your Mac, you will be prompted to install the XCode command line tools.

Alternatively, you can install git via Homebrew:

```shell
brew install git
```

## Install Hub

Since this project is hosted on GitHub, using [Hub](https://github.com/github/hub) will make many common tasks more
convenient. You can install hub via Homebrew:

```shell
brew install hub
```

### Create a git alias (optional)

If your default shell is zsh (OSX Default) then add the following line to your `~/.zshrc` file. For bash, add the line to
your `.bash_profile` or `.bashrc` file.

```shell
alias git=hub
```

## SSH Keys

If you haven't already, you will want to [configure git to use ssh for connecting to GitHub.](https://docs.github.com/en/free-pro-team@latest/github/authenticating-to-github/connecting-to-github-with-ssh)

# Install OpenJDK

Install OpenJDK 14 with Homebrew:

```shell
brew install --cask AdoptOpenJDK/openjdk/adoptopenjdk14
```

# Install jEnv

jEnv is a version manager for the JDK. It can be installed with Homebrew by running the following on the command line:

```shell
brew install jenv
```

As instructed in the `==> Caveats` section of the Homebrew installation output, if your default shell is zsh
(OSX Default) then add the following lines to your `~/.zshrc` file. For bash, add the lines to your `.bash_profile` or
`.bashrc` file.

```shell
export PATH="$HOME/.jenv/bin:$PATH"
eval "$(jenv init -)"
```

## Plugins

If your default shell is zsh (OSX Default) then add the following lines to your `~/.zshrc` file. For bash, add the lines
to your `.bash_profile` or `.bashrc` file.

```shell
jenv enable-plugin export
jenv enable-plugin gradle
jenv enable-plugin maven
```

## Register JDKs

List your JDKs using:

```shell
/usr/libexec/java_home -V
```

This will produce output similar to:

```shell
Matching Java Virtual Machines (1):
    14.0.2, x86_64:     "AdoptOpenJDK 14"       /Library/Java/JavaVirtualMachines/adoptopenjdk-14.jdk/Contents/Home
```

Create your `.jenv` directories with:

```shell
mkdir ~/.jenv
mkdir ~/.jenv/versions
```

You can register each JDK with `jenv add <your_jdk_path>`. For example:

```shell
jenv add /Library/Java/JavaVirtualMachines/adoptopenjdk-14.jdk/Contents/Home
```

## Using jEnv

You can list all registered JDKS with:

```shell
jenv versions
```

Set the system-wide Java version by doing:

```shell
jenv global 14.0
```

Set a project-wide Java version by doing:

```shell
jenv local 14.0
```

Set a shell instance Java version by doing:

```shell
jenv shell 14.0
```

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
brew install yarn
```

# Install Podman

This project uses [Podman](https://podman.io/) for containerization.

To install Podman, run:

```shell
brew install podman
```

To install [podman-compose](https://github.com/containers/podman-compose), run:

```shell
brew install podman-compose
``
```

