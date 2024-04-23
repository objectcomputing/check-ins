<!-- TOC -->

- [Contributing](#contributing)
  - [Branches](#branches)
  - [Commits](#commits)
    - [Use the Imperative](#use-the-imperative)
    - [Use the Body to Explain the Background and Reasoning, not the Implementation](#use-the-body-to-explain-the-background-and-reasoning-not-the-implementation)
    - [Subject Line Standard Terminology](#subject-line-standard-terminology)
  - [Documentation](#documentation)
  - [Code Review](#code-review)
  - [Deploy](#deploy)
- [Task Workflow](#task-workflow)
- [Definition of Done](#definition-of-done)
- [Technologies Used and Dev Setup](#technologies-used-and-dev-setup)

<!-- /TOC -->

# Contributing

<a id="markdown-contributing" name="contributing"></a>
_Detailed expectations for those contributing to this repository. A documented branching strategy is recommended. Trunk based development as default._

This project uses a [git-flow based development](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow) branching strategy.

[Trunk-based Development vs. Git Flow](https://www.toptal.com/software/trunk-based-development-git-flow)

## Branches

<a id="markdown-branches" name="branches"></a>
_Branch naming must be consistent and must be in [kebab-case](https://en.toolpage.org/tool/kebabcase). The following pattern is recommended -_

- feature-[Issue Number]/\* - Feature additions
- bugfix-[Issue Number]/\* - Bug fixes
- chore-[Issue Number]/\* - Chore tasks
- doc-[Issue Number]/\* - Documentation tasks
- release/[release-version] - Release versioning

For example,

- feature-27/setup-postgresql
- release/1.0

## Commits

<a id="markdown-commits" name="commits"></a>
_Consistent, verbose commits reduce the need for additional documentation. May be used for generating changelogs. Detailed instructions must be included either through a documented specification or custom documentation._

This project uses the [conventional commit](https://www.conventionalcommits.org/en/v1.0.0-beta.4/) specification for commits.

https://github.com/talos-systems/conform

### Use the Imperative

<a id="markdown-use-the-imperative" name="use-the-imperative"></a>
_Consistent tense in commit messages._

In keeping with the standard output of git itself, all commit subject lines must be written using the imperative:

Your commit subject line must be able to complete the sentence

> If applied, this commit will ...

If it doesn't, it's non-conformant. The body may use any style you want.

### Use the Body to Explain the Background and Reasoning, not the Implementation

<a id="markdown-use-the-body-to-explain-the-background-and-reasoning-not-the-implementation" name="use-the-body-to-explain-the-background-and-reasoning-not-the-implementation"></a>

_Especially_ if the diff is rather large or extremely clustered, you can save all fellow developers some time by explaining _why_ you did _what_.

When possible the addition of screenshots, recorded gifs or other visual media is recommended.

### Subject Line Standard Terminology

<a id="markdown-subject-line-standard-terminology" name="subject-line-standard-terminology"></a>

| First Word | Meaning                                              |
| ---------- | ---------------------------------------------------- |
| Add        | Create a capability e.g. feature, test, dependency.  |
| Cut        | Remove a capability e.g. feature, test, dependency.  |
| Fix        | Fix an issue e.g. bug, typo, accident, misstatement. |
| Bump       | Increase the version of something e.g. dependency.   |
| Make       | Change the build process, or tooling, or infra.      |
| Start      | Begin doing something; e.g. create a feature flag.   |
| Stop       | End doing something; e.g. remove a feature flag.     |
| Refactor   | A code change that MUST be just a refactoring.       |
| Reformat   | Refactor of formatting, e.g. omit whitespace.        |
| Optimize   | Refactor of performance, e.g. speed up code.         |
| Document   | Refactor of documentation, e.g. help files.          |

## Documentation

<a id="markdown-documentation" name="documentation"></a>
_Documentation must be treated as an integral aspect of development. This section describes expectations for documentation rigor and placement._

In cases where the impact of a change is such that it is affects a fundamental component of the system, additional documentation is needed above what is presented in commit messages. This is by discretion, but it is advised to err on the side of more documentation than less.

In these cases, documentation should be added as markdown files within the project directory.

## Code Review

<a id="markdown-code-review" name="code-review"></a>
_Expectations for conducting code reviews, the merger of code and subesquent tasks_

Pull requests require a minimum of one reviewer to accept changes prior to merge. It is the responsibility of the person submitting the pull request to schedule a code review.

[Google Engineering - Code Review Developer Guide](https://google.github.io/eng-practices/review/)

## Deploy

<a id="markdown-deploy" name="deploy"></a>
_Deployment steps from the perspective of an engineer required to deploy to all of the available evironments._

Once a pull request is merged the change will be automatically deployed to the appropriate environment.

# Task Workflow

<a id="markdown-task-workflow" name="task-workflow"></a>
_Description of how tasks are tracked through project management software and expectations of different roles._

![Task Workflow Diagram](task%20workflow.png)

# Definition of Done

<a id="markdown-definition-of-done" name="definition-of-done"></a>
A task is complete when

- All acceptance criteria have been met
- Implementation has been code reviewed
- All automated tests pass (e.g: Unit, end-to-end testing)
- All manual tests pass (e.g: Functional, user interface testing)
- Necessary documentation is reviewed
- Implementation is deployed to the appropriate environment

# Technologies Used and Dev Setup

<a id="markdown-technologies-used-and-dev-setup" name="technologies-used-and-dev-setup"></a>
The following technologies are required for development

- Node.js
- Java
- Micronaut
- Podman or Docker
- Gradle

For instructions on setting up your development environment, see [Setting up your environment](https://objectcomputing.github.io/check-ins/getting-started/setup/).
