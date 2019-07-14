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
cp target/jqa-jira-plugin-*.jar run/jqassistant-commandline-neo4jv3-1.6.0/plugins/

# Scan the test project
run/jqassistant-commandline-neo4jv3-1.6.0/bin/jqassistant.sh scan -f run/jira-plugin-configuration.xml

# Start a Neo4J server
run/jqassistant-commandline-neo4jv3-1.6.0/bin/jqassistant.sh server