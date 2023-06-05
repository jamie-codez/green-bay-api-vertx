# Green Bay API

Green Bay API for the green-bay property management using vertx, to achieve synchronous implementation for easy
scalability.

## Overview

This backend API was written using the Eclipse Vert.x plugin that promotes synchronous and Microservice implementations
of code, this helps improve scalability.
This projects aims at improving property management for the Green Bay Apartments management by managing both employee
data and tenants' data easily and conveniently hence reducing a lot of paper work and foot-work, and as a result
reducing operation costs and overhead cost for the management.

### Framework

The framework used to build this application is as follows:

- #### Java
- #### Kotlin (Makes up most of the code)
- #### Eclipse Vert.x
- #### MongoDB
- #### Docker
- #### Docker-Compose

### Environment

The environment used for build such a project is independent, and can easily be replicated in any x86 and som arm based
environments. That going, the environment used to build this project was as below:

- #### Operating System

The operating system used for this development was Linux (Ubuntu 22.04 LTS Build) more on this operating system can be
found on [Ubuntu](https://www.ubuntu.com)

- #### IDE and tools

The development IDE according me is just personal preference hell you can even use a common text editor to achieve the
same, but for me, I preferred using IntelliJ IDEA for development as it is more modern and has most of the tools I need
easily and conveniently on the IDE tool window. More on this tool can be found
on [IntelliJ](https://jetbrains.com/intellij)

- #### VCS

For VCS, I preferred to go with GitHub since it provides a free pipeline for automatic deployment to dockerhub and also
provides space for publishing versions of your code so that is being easily accessible to me in future readily without
setting up environment, I just download the code and deploy it if need be.

### Modules

- [ ] Authentication Module

This Module will be in-charge of all Authentication processes of the system, i.e. not limited to Registration and Login,
but also sending activation email to newly registered user, activation of the account, password reset for registered
users, generation of JWT for session handling when the user logs in, logout of logged-in users and lastly deletion of
user account and personal details upon request.

- [ ] Users Module

This module will be only accessible to Administrative users only since it will be used to manage tenants and very
sensitive information pertaining to the system and its users, these functions will be limited in regard to access
levels.

