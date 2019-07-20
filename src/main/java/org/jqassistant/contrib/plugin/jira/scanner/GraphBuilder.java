package org.jqassistant.contrib.plugin.jira.scanner;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.Project;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import org.jqassistant.contrib.plugin.jira.cache.CacheEndpoint;
import org.jqassistant.contrib.plugin.jira.jdom.XMLJiraPluginConfiguration;
import org.jqassistant.contrib.plugin.jira.jdom.XMLJiraProject;
import org.jqassistant.contrib.plugin.jira.model.JiraIssue;
import org.jqassistant.contrib.plugin.jira.model.JiraPluginConfigurationFile;
import org.jqassistant.contrib.plugin.jira.model.JiraProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

class GraphBuilder {

    private static final String JQL_ISSUE_QUERY = "project=%s";

    private static final Set<String> ALL_FIELDS = Collections.singleton("*all");

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphBuilder.class);

    private final JiraRestClient jiraRestClient;
    private final CacheEndpoint cacheEndpoint;

    GraphBuilder(XMLJiraPluginConfiguration xmlJiraPluginConfiguration, CacheEndpoint cacheEndpoint) {

        this.cacheEndpoint = cacheEndpoint;

        String url = xmlJiraPluginConfiguration.getUrl();
        String username = xmlJiraPluginConfiguration.getCredentials().getUser();
        String password = xmlJiraPluginConfiguration.getCredentials().getPassword();

        jiraRestClient = new AsynchronousJiraRestClientFactory()
                .createWithBasicHttpAuthentication(URI.create(url), username, password);
    }

    void startTraversal(final JiraPluginConfigurationFile jiraPluginConfigurationFile,
                        final XMLJiraPluginConfiguration xmlJiraPluginConfiguration) {

        for (XMLJiraProject xmlJiraProject : xmlJiraPluginConfiguration.getProjects()) {

            Project project = jiraRestClient.getProjectClient().getProject(xmlJiraProject.getKey()).claim();
            JiraProject jiraProject = cacheEndpoint.findOrCreateProject(project);

            jiraPluginConfigurationFile.getProjects().add(jiraProject);

            issueLevel(jiraProject);
        }
    }

    private void issueLevel(JiraProject jiraProject) {

        SearchResult searchResultIssuesForThisProject = jiraRestClient.getSearchClient().searchJql(String.format(JQL_ISSUE_QUERY, jiraProject.getKey()), -1, null, ALL_FIELDS).claim();

        for(Issue issue:searchResultIssuesForThisProject.getIssues()) {

            JiraIssue jiraIssue = cacheEndpoint.findOrCreateIssue(issue);

            jiraProject.getIssues().add(jiraIssue);
        }
    }
}
