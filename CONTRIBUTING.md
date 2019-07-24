# Contribute

## Local Jira Server

As of today, 2nd of June 2019, there is no official docker image available via 
[Atlassian](https://hub.docker.com/u/atlassian). 
[Dave Chevell](https://community.atlassian.com/t5/user/viewprofilepage/user-id/792201) a member of the *Atlassian* team
maintains unofficial images which can be found here: https://hub.docker.com/r/dchevell/jira-software.
For the plugin the current version 
([8.2](https://confluence.atlassian.com/jirakb/jira-build-and-version-numbers-reference-347341143.html)) 
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
    dchevell/jira-software:8.2-ubuntu
```

Now **Jira** is available on [http://localhost:8080](http://localhost:8080). If your persistence folder was empty before
you need to complete the setup process once.

For more information on how to configure the docker setup please see the 
[dockerhub repository page](https://hub.docker.com/r/dchevell/jira-software).

## Rate Limits

To gather the data from **Jira**, the REST API is queried via the 
[Jira Java REST client](https://bitbucket.org/atlassian/jira-rest-java-client/src/master/). As the rate limit mechanism
is hardly documented tests have been added which can be used to try to provoke certain errors. 

The `LocalRateLimitsTest` and `CloudRateLimitsTest` are pretty much identical. The only difference is that the 
`LocalRateLimitsTest` contains username, password and URL hardcoded. The `CloudRateLimitsTest` on the other side can be 
configured via the following environment variables: `JIRA_USERNAME`, `JIRA_PASSWORD` and `JIRA_URL`. One of the tests
will be removed later after it won't be used anymore. TODO.

### Test Results

Both *local* and *cloud* instances of **Jira** didn't throw any errors while handling `3 * 500 + 1000` requests 
in under 5 minutes.

### Sources of Information

#### Hipchat

We found a documentation for another software product from **Atlassian** named **Hipchat**. According to this 
documentation there is a rate limit from 500 requests per 5 minutes: 
https://developer.atlassian.com/server/hipchat/hipchat-rest-api-rate-limits/

The tests from above show that this rule does not apply for **Jira**.

#### Community Question 1

There is a community question from the 1st of february 2014 which can be found 
[here](https://community.atlassian.com/t5/Answers-Developer-Questions/Does-JIRA-limit-the-rate-that-REST-API-requests-are-handled/qaq-p/555943).
A user asks about the performance of the REST API. The reply does not mention that a rate limit exists.

#### Community Question 2

[This](https://community.developer.atlassian.com/t/are-there-rate-limits-for-jira-cloud-apis/4317/22) is 
the most concrete and recent (2019) post about rate limits we found. Here is the interesting part:

> In Jira Cloud we use rate limiting for REST API to protect our customers’ sites (aka tenants) resources. 
  Current implementation isn’t based on REST API consumer, it is based on concurrent requests made across all 
  consumers of single tenant [...].
  
So there is some kind of rate limit mechanism but it only triggers if multiple clients are doing requests
at the same time. Furthermore, there doesn't seem to be a way to implement a simple waiting mechanism as the REST API
does not provide the necessary headers. Probably, the mechanism will change in the future:

> We are actively working on per consumer rate limiting which should help us provide meaningful information in 
  response headers. We expect it to be delivered in couple of quarters.
  
This does only apply to the *cloud* version of **Jira**.