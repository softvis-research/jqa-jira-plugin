# jQAssistant Jira Plugin

This is a [Jira](https://www.atlassian.com/software/jira) parser for [jQAssistant](https://jqassistant.org/). 
It enables jQAssistant to scan and to analyze data from **Jira**.

## Getting Started

Download the jQAssistant command line tool for your system: [jQAssistant - Get Started](https://jqassistant.org/get-started/).

Next download the latest version from the release tab. Put the `jqa-jira-plugin-*.jar` into the plugins folder of the jQAssistant command
 line tool.
 
Finally, you need to configure the plugin via XML. The file must be named `jira-plugin-configuration.xml`:
 
```xml
<jira-configuration>

    <url>http://dummy-url.com</url>
    <credentials>
        <username>login-name-for-jira</username>
        <password>jira-password</password>
    </credentials>

    <projects>
        <project>
            <key>PX</key>
        </project>        
        <project>
            <key>PY</key>
        </project>
    </projects>

</jira-configuration>
```

IMPORTANT:
Please note that the username for Jira must be the one you can find in the profile section in Jira. The Jira UI allows
you to use your email as login name as well. This is not possible when querying the Jira REST API!

Now scan your configuration and wait for the plugin to finish:

```bash
jqassistant-commandline-neo4jv3-1.6.0/bin/jqassistant.sh scan -f jira-plugin-configuration.xml
```

You can then start a local Neo4j server to start querying the database at [http://localhost:7474](http://localhost:7474):

```bash
jqassistant-commandline-neo4jv3-1.6.0/bin/jqassistant.sh server
```


## Model

![Neo4J model for the jQAssistant Jira plugin](./model.jpg)

## Supported Jira Versions

Unfortunately, we did not find any documentation which Jira versions are supported by the [JIRA REST Java Client](https://mvnrepository.com/artifact/com.atlassian.jira/jira-rest-java-client-api/5.1.1-e0dd194).
According to the [BitBucket repository description](https://bitbucket.org/atlassian/jira-rest-java-client/src/master/) 
every version newer than JIRA 4.2  is supported:

> Java client library (useful for any JVM languages) which allows to communicate with JIRA via its new REST API (JIRA 4.2 and newer).

The README.md references a [Wiki entry](https://ecosystem.atlassian.net/wiki/spaces/JRJC/overview) from 2010. 
This again references an [ATLASSIAN Marketplace page](https://marketplace.atlassian.com/apps/39474/rest-java-client-for-jira/version-history) which is out of date since 2013.

If it does not work with your Jira instance please open an **Issue** and write your Jira version in there.

## Performance

A real benchmark does not make sense as the performance of the plugin depends on the performance of the Jira instance. Nevertheless, we 
want to provide some numbers which we received while testing the plugin against available Jira instances.

### Instance 1

The first Jira instance we scanned had the following entity counts:

| Entity Name | Count |
|-------------|-------|
| Issue       | ~7400 |
| User        | ~50   |
| Issue Link  | ~1400 |

The scan took a total of 11 minutes.

## Rate Limits

When running against a Jira cloud instance there could be an issue with rate limits. For more information please see the [CONTRIBUTING.md](CONTRIBUTING.md).

## Contribute

We really appreciate your help! If you want to contribute please have a look at the [CONTRIBUTING.md](CONTRIBUTING.md).
