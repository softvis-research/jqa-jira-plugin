package org.jqassistant.contrib.plugin.jira.jjrc;

import com.atlassian.jira.rest.client.api.domain.*;

import java.net.URI;

public interface JiraRestClientWrapper {

    ServerInfo retrieveServerInfo();

    Iterable<Priority> retrievePriorities();

    Iterable<Status> retrieveStatuses();

    Project retrieveProject(String key);

    Component retrieveComponent(URI uri);

    User retrieveUser(URI uri);

    Iterable<Issue> retrieveIssues(String projectKey);
}
