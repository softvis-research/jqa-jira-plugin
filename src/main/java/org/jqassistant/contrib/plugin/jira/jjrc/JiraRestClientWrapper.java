package org.jqassistant.contrib.plugin.jira.jjrc;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.*;
import org.jqassistant.contrib.plugin.jira.scanner.GraphBuilder;

import java.net.URI;

/**
 * This interface provides a layer of abstraction between {@link JiraRestClient} and the {@link GraphBuilder}.
 *
 * It enables us to create a mock for the {@link JiraRestClient}.
 */
public interface JiraRestClientWrapper {

    ServerInfo retrieveServerInfo();

    Iterable<Priority> retrievePriorities();

    Iterable<Status> retrieveStatuses();

    Project retrieveProject(String key);

    Component retrieveComponent(URI uri);

    User retrieveUser(URI uri);

    Iterable<Issue> retrieveIssues(String projectKey);
}
