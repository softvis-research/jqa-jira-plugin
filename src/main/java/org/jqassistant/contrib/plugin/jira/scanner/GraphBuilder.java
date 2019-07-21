package org.jqassistant.contrib.plugin.jira.scanner;

import com.atlassian.jira.rest.client.api.domain.*;
import org.jqassistant.contrib.plugin.jira.cache.CacheEndpoint;
import org.jqassistant.contrib.plugin.jira.ids.IssueID;
import org.jqassistant.contrib.plugin.jira.jdom.XMLJiraPluginConfiguration;
import org.jqassistant.contrib.plugin.jira.jdom.XMLJiraProject;
import org.jqassistant.contrib.plugin.jira.jjrc.DefaultJiraRestClientWrapper;
import org.jqassistant.contrib.plugin.jira.jjrc.JiraRestClientWrapper;
import org.jqassistant.contrib.plugin.jira.jjrc.mock.MockedJiraRestClientWrapper;
import org.jqassistant.contrib.plugin.jira.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.HashMap;

import static org.jqassistant.contrib.plugin.jira.utils.TimeConverter.convertTime;

public class GraphBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphBuilder.class);

    // FIXME
    public static String TEST_ENV = "JQASSISTANT_JIRA_PLUGIN_TEST";

    private final JiraRestClientWrapper jiraRestClientWrapper;
    private final CacheEndpoint cacheEndpoint;

    private final HashMap<IssueID, Iterable<IssueLink>> issueLinkCashe;

    GraphBuilder(XMLJiraPluginConfiguration xmlJiraPluginConfiguration, CacheEndpoint cacheEndpoint) {

        this.issueLinkCashe = new HashMap<>();
        this.cacheEndpoint = cacheEndpoint;

        String url = xmlJiraPluginConfiguration.getUrl();
        String username = xmlJiraPluginConfiguration.getCredentials().getUser();
        String password = xmlJiraPluginConfiguration.getCredentials().getPassword();

        if (System.getenv(TEST_ENV) != null) {
            jiraRestClientWrapper = new MockedJiraRestClientWrapper();
        } else {
            jiraRestClientWrapper = new DefaultJiraRestClientWrapper(url, username, password);
        }
    }

    void startTraversal(final JiraServer jiraServer,
                        final XMLJiraPluginConfiguration xmlJiraPluginConfiguration) {

        ServerInfo serverInfo = jiraRestClientWrapper.retrieveServerInfo();
        jiraServer.setBaseUri(serverInfo.getBaseUri().toString());
        jiraServer.setVersion(serverInfo.getVersion());
        jiraServer.setBuildNumber(serverInfo.getBuildNumber());
        jiraServer.setBuildDate(convertTime(serverInfo.getBuildDate()));
        jiraServer.setServerTime(convertTime(serverInfo.getServerTime()));
        jiraServer.setScmInfo(serverInfo.getScmInfo());
        jiraServer.setServerTitle(serverInfo.getServerTitle());

        for (Priority priority : jiraRestClientWrapper.retrievePriorities()) {

            JiraPriority jiraPriority = cacheEndpoint.findOrCreatePriority(priority);
            jiraServer.getPriorities().add(jiraPriority);
        }

        for (Status status : jiraRestClientWrapper.retrieveStatuses()) {

            JiraStatus jiraStatus = cacheEndpoint.findOrCreateStatus(status);
            jiraServer.getStatuses().add(jiraStatus);
        }

        for (XMLJiraProject xmlJiraProject : xmlJiraPluginConfiguration.getProjects()) {

            Project project = jiraRestClientWrapper.retrieveProject(xmlJiraProject.getKey());
            JiraProject jiraProject = cacheEndpoint.findOrCreateProject(project);

            jiraServer.getProjects().add(jiraProject);

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
            resolveIssues(jiraProject);
        }

        resolveIssueLinks();
    }

    private void resolveComponentsForProject(JiraProject jiraProject, Iterable<BasicComponent> basicComponentList) {

        for (BasicComponent basicComponent : basicComponentList) {

            Component component = jiraRestClientWrapper.retrieveComponent(basicComponent.getSelf());
            JiraComponent jiraComponent = cacheEndpoint.findOrCreateComponent(component);

            User componentLead = jiraRestClientWrapper.retrieveUser(component.getLead().getSelf());
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

        User projectLeadUser = jiraRestClientWrapper.retrieveUser(projectLeadSelf);

        JiraUser jiraUser = cacheEndpoint.findOrCreateUser(projectLeadUser);
        jiraProject.setLead(jiraUser);
    }

    private void resolveIssues(JiraProject jiraProject) {

        for (Issue issue : jiraRestClientWrapper.retrieveIssues(jiraProject.getKey())) {

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

            for (Comment comment : issue.getComments()) {
                commentLevel(jiraIssue, comment);
            }

            if (issue.getAffectedVersions() != null) {
                for (Version version : issue.getAffectedVersions()) {

                    JiraVersion jiraVersion = cacheEndpoint.findOrCreateVersion(version);
                    jiraIssue.getAffectedVersions().add(jiraVersion);
                }
            }

            if (issue.getFixVersions() != null) {
                for (Version version : issue.getFixVersions()) {

                    JiraVersion jiraVersion = cacheEndpoint.findOrCreateVersion(version);
                    jiraIssue.getFixedVersions().add(jiraVersion);
                }
            }

            if (issue.getIssueLinks() != null) {
                IssueID issueID = IssueID.builder().jiraId(jiraIssue.getJiraId()).build();
                issueLinkCashe.put(issueID, issue.getIssueLinks());
            }
        }
    }

    private void commentLevel(JiraIssue jiraIssue, Comment comment) {

        JiraComment jiraComment = cacheEndpoint.findOrCreateComment(comment);

        if (comment.getAuthor() != null) {

            User commentAuthor = jiraRestClientWrapper.retrieveUser(comment.getAuthor().getSelf());
            JiraUser jiraUser = cacheEndpoint.findOrCreateUser(commentAuthor);
            jiraComment.setAuthor(jiraUser);
        }

        if (comment.getUpdateAuthor() != null) {

            User commentUpdateAuthor = jiraRestClientWrapper.retrieveUser(comment.getUpdateAuthor().getSelf());
            JiraUser jiraUser = cacheEndpoint.findOrCreateUser(commentUpdateAuthor);
            jiraComment.setUpdateAuthor(jiraUser);
        }

        jiraIssue.getComments().add(jiraComment);
    }

    private void resolveIssueLinks() {

        for (IssueID issueID : issueLinkCashe.keySet()) {

            for (IssueLink issueLink : issueLinkCashe.get(issueID)) {

                JiraIssueLink jiraIssueLink = cacheEndpoint.createIssueLink(issueLink);

                // This solution is a bit hacky.
                // Have a look at IssueID.java to understand why this is necessary.
                String targetIssueUri = issueLink.getTargetIssueUri().toString();
                long targetIssueId = Long.valueOf(targetIssueUri.substring(targetIssueUri.lastIndexOf('/') + 1));

                IssueID targetIssueID = IssueID.builder().jiraId(targetIssueId).build();
                jiraIssueLink.setTargetIssue(cacheEndpoint.findIssueOrThrowException(targetIssueID));

                cacheEndpoint.findIssueOrThrowException(issueID).getIssueLinks().add(jiraIssueLink);
            }
        }
    }
}
