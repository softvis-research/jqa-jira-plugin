package org.jqassistant.contrib.plugin.jira.scanner;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.*;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import org.jqassistant.contrib.plugin.jira.cache.CacheEndpoint;
import org.jqassistant.contrib.plugin.jira.jdom.XMLJiraPluginConfiguration;
import org.jqassistant.contrib.plugin.jira.jdom.XMLJiraProject;
import org.jqassistant.contrib.plugin.jira.model.*;
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

        for (Priority priority : jiraRestClient.getMetadataClient().getPriorities().claim()) {

            JiraPriority jiraPriority = cacheEndpoint.findOrCreatePriority(priority);
            jiraPluginConfigurationFile.getPriorities().add(jiraPriority);
        }

        for (Status status : jiraRestClient.getMetadataClient().getStatuses().claim()) {

            JiraStatus jiraStatus = cacheEndpoint.findOrCreateStatus(status);
            jiraPluginConfigurationFile.getStatuses().add(jiraStatus);
        }

        for (XMLJiraProject xmlJiraProject : xmlJiraPluginConfiguration.getProjects()) {

            Project project = jiraRestClient.getProjectClient().getProject(xmlJiraProject.getKey()).claim();
            JiraProject jiraProject = cacheEndpoint.findOrCreateProject(project);

            jiraPluginConfigurationFile.getProjects().add(jiraProject);

            for (Version version : project.getVersions()) {

                JiraVersion jiraVersion = cacheEndpoint.findOrCreateVersion(version);
                jiraProject.getVersions().add(jiraVersion);
            }

            for (IssueType issueType : project.getIssueTypes()) {

                JiraIssueType jiraIssueType = cacheEndpoint.findOrCreateIssueType(issueType);
                jiraProject.getIssueTypes().add(jiraIssueType);
            }

            resolveComponentsForProject(jiraProject, project.getComponents());
            resolveLeaderForProject(jiraProject, project.getLead().getSelf());
            issueLevel(jiraProject);
        }
    }

    private void resolveComponentsForProject(JiraProject jiraProject, Iterable<BasicComponent> basicComponentList) {

        for (BasicComponent basicComponent : basicComponentList) {

            Component component = jiraRestClient.getComponentClient().getComponent(basicComponent.getSelf()).claim();
            JiraComponent jiraComponent = cacheEndpoint.findOrCreateComponent(component);

            User componentLead = jiraRestClient.getUserClient().getUser(component.getLead().getSelf()).claim();
            JiraUser jiraUser = cacheEndpoint.findOrCreateUser(componentLead);
            jiraComponent.setLeader(jiraUser);

            jiraProject.getComponents().add(jiraComponent);
        }
    }

    /**
     * The {@link BasicUser} which is part of the {@link Project} is not enough as it misses some fields like "name".
     * Therefore, we load the user separately.
     */
    private void resolveLeaderForProject(JiraProject jiraProject, URI projectLeadSelf) {

        User projectLeadUser = jiraRestClient.getUserClient().getUser(projectLeadSelf).claim();

        JiraUser jiraUser = cacheEndpoint.findOrCreateUser(projectLeadUser);
        jiraProject.setLeader(jiraUser);
    }

    private void issueLevel(JiraProject jiraProject) {

        SearchResult searchResultIssuesForThisProject = jiraRestClient.getSearchClient().searchJql(String.format(JQL_ISSUE_QUERY, jiraProject.getKey()), -1, null, ALL_FIELDS).claim();

        for (Issue issue : searchResultIssuesForThisProject.getIssues()) {

            JiraIssue jiraIssue = cacheEndpoint.findOrCreateIssue(issue);

            jiraProject.getIssues().add(jiraIssue);

            if (issue.getAssignee() != null) {
                JiraUser assignee = cacheEndpoint.findOrCreateUser(issue.getAssignee());
                jiraIssue.setAssignee(assignee);
            }

            if (issue.getReporter() != null) {
                JiraUser reporter = cacheEndpoint.findOrCreateUser(issue.getReporter());
                jiraIssue.setReporter(reporter);
            }

            // We already loaded every component for the current project.
            for (BasicComponent basicComponent : issue.getComponents()) {
                JiraComponent jiraComponent = cacheEndpoint.findComponentOrThrowException(basicComponent);
                jiraIssue.getComponents().add(jiraComponent);
            }

            // We already loaded every component for the current project but we can use the default method
            // as we have not requesting overhead like for components.
            JiraIssueType jiraIssueType = cacheEndpoint.findOrCreateIssueType(issue.getIssueType());
            jiraIssue.setIssueType(jiraIssueType);

            if (issue.getPriority() != null) {

                JiraPriority jiraPriority = cacheEndpoint.findPriorityOrThrowException(issue.getPriority());
                jiraIssue.setPriority(jiraPriority);
            }

            JiraStatus jiraStatus = cacheEndpoint.findOrCreateStatus(issue.getStatus());
            jiraIssue.setStatus(jiraStatus);
        }
    }
}
