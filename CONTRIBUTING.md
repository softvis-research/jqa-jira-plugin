# Contribute

Thank you for contributing to the **jQAssistant Jira plugin**!

Please feel free to implement features, fix bugs and refactor existing code. We will make sure to review merge requests.
If you need support you can create a ticket on **GitHub**.

## Getting Started

Let's give you a short introduction on how to get started.

### Local Execution

First, let's make sure that you can execute the Jira plugin locally against a test Jira instance.

#### Download jQAssistant

1. Create a folder `run` inside the project.
2. Download the jQAssistant command line distribution  [here](https://jqassistant.org/get-started/).
3. Extract it to the `run` folder.

#### Bash Script for Development 

Testing your local changes to the plugin requires the following steps:

* Build the plugin with `mvn clean package`.
* Put the resulting `jqa-jira-plugin-*.jar` into the **jQAssistant** plugins folder.
* Execute jQAssistant scan against a Jira server.
* Start the **neo4j** server to check the results in your browser.

If you have a shell you can use the following bash script `run.sh`:

```bash
#!/usr/bin/env bash

# Remove the current build
echo 'Removing ./target ...'
rm -r target

# Build the plugin
mvn clean package

# Remove the existing database
echo 'Removing ./jqassistant ...'
rm -r jqassistant

echo 'Copying plugin jar into jQAssistant CLI ...'
cp target/jqa-jira-plugin-*.jar run/jqassistant-commandline-neo4jv3-1.8.0/plugins/

# Scan the test project
run/jqassistant-commandline-neo4jv3-1.8.0/bin/jqassistant.sh scan -f run/jira-plugin-configuration.xml

# Start a neo4j server
run/jqassistant-commandline-neo4jv3-1.8.0/bin/jqassistant.sh server
```

#### Configuration for Test Server

Next you need a configuration for a test server. 
Email us if you want to be added to our test server https://jqa-jira-plugin.atlassian.net/. 
Here is a sample configuration. Simply add your Jira email, and an Atlassian access token.

```xml
<jira-configuration>

    <url>https://jqa-jira-plugin.atlassian.net/</url>

    <api-token>
        <email>{{email}}</email>
        <token>{{token}}</token>
    </api-token>

    <projects>
        <project>
            <key>TP1</key>
        </project>
    </projects>

</jira-configuration>
```

#### Test Local Setup

That's it. Now try your local setup by running `./run.sh` in a shell or execute the commands from above by hand.
Finally, you can explore and query the database at http://localhost:7474.

### Plugin Overview

Next, let us have a look at some classes that are relevant for understanding the Jira plugin.

#### `Descriptor`

A **descriptor** is a Java representation of a node or a relation in the neo4j database. All descriptors specific
to the Jira plugin can be found in the `model` package.

If you want to extend the graph model you have to design your nodes and relations in descriptors.
Furthermore, make sure to register new descriptors in `resources/META-INF/jqassistant-plugin.xml`. 
Otherwise, jQAssistant will throw an error.

#### `JiraScannerPlugin`

The `JiraScannerPlugin` is the entry point for the Jira plugin. The `scan(FileResource, String, Scope, Scanner)` method gets 
the XML configuration as the first parameter. Furthermore, it initializes the cache (`CacheEndpoint`) and triggers the
`GraphBuilder`, the central class in the Jira plugin.

Most likely, you don't have to change the `JiraScannerPlugin`.

#### `CacheEndpoint`

Some descriptors have to be cached so that we can reference them later. To access the cache use the `CacheEndpoint`.
Internally it uses the `DescriptorCache` to save entities in maps, one for each descriptor type.

If you want to add a new descriptor, you have to modify both the `CacheEndpoint` and the `DescriptorCache`.

Each descriptor needs an ID. For ID examples have a look at the `ids` package.

#### `GraphBuilder`

*The naming is a bit confusing as it does NOT correspond to the building pattern.*

The `GraphBuilder` is where all the magic happens.
It calls multiple sub-builders in a specific order. The sub-builders are located in the package `scanner.builder`.

```java
void startTraversal(final JiraServer jiraServer, final XMLJiraPluginConfiguration xmlJiraPluginConfiguration) {

        this.serverInfoBuilder.handleServerInfo(jiraServer);

        this.priorityBuilder.handlePriorities(jiraServer);
        this.statusBuilder.handleStatuses(jiraServer);

        this.projectBuilder.handleProjects(jiraServer, xmlJiraPluginConfiguration);

        this.issueLinkBuilder.handleIssueLinks();
        this.subtaskRelationBuilder.handleSubtaskRelations();
    }
```

If you want to add new descriptors to the graph model it could make sense to write your own sub-builder and call it in the method above.

#### `JiraRestClientWrapper`

To communicate against the Jira API we use `com.atlassian.jira.jira-rest-java-client-core`, an API client provided by 
the **Atlassian** team. For testing purposes we created a layer of abstraction, so we can mock API calls in our tests.
This layer of abstraction is the `JiraRestClientWrapper`.

If you need information that is currently not available in the plugin, extend both the `JiraRestClientWrapper` and the 
`DefaultJiraRestClientWrapper`.

#### `ScannerTestIT`

There are a few basic tests available at the moment. The most important one is the `ScannerTestIT` as it tests the actual plugin.
The tests do not query a Jira API. Instead the `MockedJiraRestClientWrapper` is used to provide test data.

#### Other Questions

If you have any other questions feel free to open a ticket on GitHub.

## Further Information

### Rate Limits

To gather the data from **Jira**, the REST API is queried via the 
[Jira Java REST client](https://bitbucket.org/atlassian/jira-rest-java-client/src/master/). As the rate limit mechanism
is hardly documented tests have been added which can be used to try to provoke certain errors. 

The `LocalRateLimitsTest` and `CloudRateLimitsTest` are pretty much identical. The only difference is that the 
`LocalRateLimitsTest` contains username, password and URL hardcoded. The `CloudRateLimitsTest` on the other side can be 
configured via the following environment variables: `JIRA_USERNAME`, `JIRA_PASSWORD` and `JIRA_URL`. One of the tests
will be removed later after it won't be used anymore. TODO.


#### Test Results

Both *local* and *cloud* instances of **Jira** didn't throw any errors while handling `3 * 500 + 1000` requests 
in under 5 minutes.

#### Sources of Information

##### Hipchat

We found a documentation for another software product from **Atlassian** named **Hipchat**. According to this 
documentation there is a rate limit from 500 requests per 5 minutes: 
https://developer.atlassian.com/server/hipchat/hipchat-rest-api-rate-limits/

The tests from above show that this rule does not apply for **Jira**.

##### Community Question 1

There is a community question from the 1st of february 2014 which can be found 
[here](https://community.atlassian.com/t5/Answers-Developer-Questions/Does-JIRA-limit-the-rate-that-REST-API-requests-are-handled/qaq-p/555943).
A user asks about the performance of the REST API. The reply does not mention that a rate limit exists.

##### Community Question 2

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