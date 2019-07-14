package org.jqassistant.contrib.plugin.jira.scanner;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Project;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import org.jqassistant.contrib.plugin.jira.cache.CacheEndpoint;
import org.jqassistant.contrib.plugin.jira.jdom.XMLJiraPluginConfiguration;
import org.jqassistant.contrib.plugin.jira.jdom.XMLJiraProject;
import org.jqassistant.contrib.plugin.jira.model.JiraPluginConfigurationFile;
import org.jqassistant.contrib.plugin.jira.model.JiraProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

class GraphBuilder {

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
        }
    }
}
