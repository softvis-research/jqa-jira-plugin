# jQAssistant Jira Plugin

This is a [Jira](https://www.atlassian.com/software/jira) parser for [jQAssistant](https://jqassistant.org/). 
It enables jQAssistant to scan and to analyze data from **Jira**.

## Contribute

### Local Jira Server

As of today, 2nd of June 2019, there is no official docker image available via 
[atlassian](https://hub.docker.com/u/atlassian). 
[Dave Chevell](https://community.atlassian.com/t5/user/viewprofilepage/user-id/792201) a member of the *atlassian* team
maintains unofficial images which can be found here: https://hub.docker.com/r/dchevell/jira-core.
For the plugin the current version 
([8.2.1](https://confluence.atlassian.com/jirakb/jira-build-and-version-numbers-reference-347341143.html)) 
of **Jira** is used.

To create a new docker container on your machine first create a folder to persist your data:

```shell
mkdir jira-docker-data
```

Then run:

```shell
docker run \
    -v `pwd`/jira-docker-data:/var/atlassian/application-data/jira \
    --name="jira" \
    -p 8080:8080 \
    -d \
    dchevell/jira-core:8.2.1-ubuntu
```

Now **Jira** is available on [http://localhost:8080](http://localhost:8080).

For more information on how to configure the docker setup please see the 
[dockerhub repository page](https://hub.docker.com/r/dchevell/jira-core).

