package org.jqassistant.contrib.plugin.jira.jjrc;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.*;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

/**
 * The default implementation for the {@link JiraRestClientWrapper} uses the {@link JiraRestClient} to
 * retrieve data from a Jira instance.
 *
 * All data is loaded synchronously.
 */
public class DefaultJiraRestClientWrapper implements JiraRestClientWrapper {

    private static final String JQL_ISSUE_QUERY = "project=%s";
    private static final Set<String> ALL_FIELDS = Collections.singleton("*all");

    private final JiraRestClient jiraRestClient;

    public DefaultJiraRestClientWrapper(String url, String username, String password) {

        jiraRestClient = new AsynchronousJiraRestClientFactory()
                .createWithBasicHttpAuthentication(URI.create(url), username, password);
    }

    @Override
    public ServerInfo retrieveServerInfo() {
        return jiraRestClient.getMetadataClient().getServerInfo().claim();
    }

    @Override
    public Iterable<Priority> retrievePriorities() {
        return jiraRestClient.getMetadataClient().getPriorities().claim();
    }

    @Override
    public Iterable<Status> retrieveStatuses() {
        return jiraRestClient.getMetadataClient().getStatuses().claim();
    }

    @Override
    public Project retrieveProject(String key) {
        return jiraRestClient.getProjectClient().getProject(key).claim();
    }

    @Override
    public Component retrieveComponent(URI uri) {
        return jiraRestClient.getComponentClient().getComponent(uri).claim();
    }

    @Override
    public User retrieveUser(URI uri) {
        return jiraRestClient.getUserClient().getUser(uri).claim();
    }

    @Override
    public SearchResult retrieveIssues(String projectKey, int maxResults, int startAt) {

        String jplQuery = String.format(JQL_ISSUE_QUERY, projectKey);
        return jiraRestClient
                .getSearchClient()
                .searchJql(jplQuery, maxResults, startAt, ALL_FIELDS)
                .claim();
    }
}
