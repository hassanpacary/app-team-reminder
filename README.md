# APP TEAM REMINDER
This application was developed during my internship periods between 2022 and 2023. Team Reminder is an application for scheduling tasks between teams.
## Table of Contents
1. [General info](#General-info)
2. [Technologies](#technologies)
3. [Installation](#installation)
## General-info
The project code is in English but the application was developed for a French target, so the interface is in French.
## Technologies
A list of technologies used within the project:
* [Java SE](https://openjdk.org/)
* [MySQL](https://www.mysql.com/fr/)
## Installation
Install repo from GitHub via SSH key.
```
$ git clone git@github.com:hassanpacary/app-team-reminder.git
```
once installed, create a `team-reminderdb` table in your localhost database. Then import the `team-reminderdb.sql` file available in project imported from GitHub.

Modify the fields `url`, `user` and `password` that are in DatabaseInteractions.DbConnection in order to be able to interact with your freshly initialized database on your localhost.

**Finaly launch the application from the class `AppLaunch`.**

**Administrator access code : `59875`**
