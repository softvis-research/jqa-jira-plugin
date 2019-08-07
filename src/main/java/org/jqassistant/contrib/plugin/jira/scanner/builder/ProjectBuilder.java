package org.jqassistant.contrib.plugin.jira.scanner.builder;

import com.atlassian.jira.rest.client.api.domain.*;
import org.jqassistant.contrib.plugin.jira.cache.CacheEndpoint;
import org.jqassistant.contrib.plugin.jira.jdom.XMLJiraPluginConfiguration;
import org.jqassistant.contrib.plugin.jira.jdom.XMLJiraProject;
import org.jqassistant.contrib.plugin.jira.jjrc.JiraRestClientWrapper;
import org.jqassistant.contrib.plugin.jira.model.*;

public class ProjectBuilder {

    private final CacheEndpoint cacheEndpoint;
    private final JiraRestClientWrapper jiraRestClientWrapper;
    private final ComponentBuilder componentBuilder;
    private final IssueBuilder issueBuilder;
    private final UserBuilder userBuilder;

    public ProjectBuilder(CacheEndpoint cacheEndpoint, JiraRestClientWrapper jiraRestClientWrapper, ComponentBuilder componentBuilder, IssueBuilder issueBuilder, UserBuilder userBuilder) {
        this.cacheEndpoint = cacheEndpoint;
        this.jiraRestClientWrapper = jiraRestClientWrapper;
        this.componentBuilder = componentBuilder;
        this.issueBuilder = issueBuilder;
        this.userBuilder = userBuilder;
    }


    public void handleProjects(JiraServer jiraServer, XMLJiraPluginConfiguration xmlJiraPluginConfiguration) {

        for (XMLJiraProject xmlJiraProject : xmlJiraPluginConfiguration.getProjects()) {

            Project project = jiraRestClientWrapper.retrieveProject(xmlJiraProject.getKey());
            JiraProject jiraProject = cacheEndpoint.findOrCreateProject(project);

            jiraServer.getProjects()
                    .add(jiraProject);

            for (Version version : project.getVersions()) {

                JiraVersion jiraVersion = cacheEndpoint.findOrCreateVersion(version);
                jiraProject.getVersions()
                        .add(jiraVersion);
            }

            for (IssueType issueType : project.getIssueTypes()) {

                JiraIssueType jiraIssueType = cacheEndpoint.findOrCreateIssueType(issueType);
                jiraProject.getIssueTypes()
                        .add(jiraIssueType);
            }

            this.componentBuilder.handleComponents(jiraProject, project.getComponents());
            this.resolveLeaderForProject(jiraProject, project.getLead());
            this.issueBuilder.handleIssues(jiraProject);
        }
    }

    /**
     * The {@link BasicUser} which is part of the {@link Project} is not enough as it misses some fields like
     * "emailAddress" or "active". Therefore, we have to load the complete {@link User} separately.
     */
    private void resolveLeaderForProject(JiraProject jiraProject, BasicUser basicUser) {

        JiraUser jiraUser = this.userBuilder.findUserInCacheOrLoadItFromJira(basicUser);
        jiraProject.setLead(jiraUser);
    }
}
