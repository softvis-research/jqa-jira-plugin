package org.jqassistant.contrib.plugin.jira.scanner;

import com.atlassian.jira.rest.client.api.domain.*;
import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.core.scanner.api.Scope;
import com.buschmais.jqassistant.plugin.common.api.scanner.filesystem.FileResource;
import org.jqassistant.contrib.plugin.jira.cache.CacheEndpoint;
import org.jqassistant.contrib.plugin.jira.cache.EntityNotFoundException;
import org.jqassistant.contrib.plugin.jira.ids.IssueID;
import org.jqassistant.contrib.plugin.jira.jdom.XMLJiraPluginConfiguration;
import org.jqassistant.contrib.plugin.jira.jdom.XMLJiraProject;
import org.jqassistant.contrib.plugin.jira.jjrc.DefaultJiraRestClientWrapper;
import org.jqassistant.contrib.plugin.jira.jjrc.JiraRestClientWrapper;
import org.jqassistant.contrib.plugin.jira.jjrc.mock.MockedJiraRestClientWrapper;
import org.jqassistant.contrib.plugin.jira.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

import static org.jqassistant.contrib.plugin.jira.utils.TimeConverter.convertTime;

/**
 * The GraphBuilder gets build only once per execution of the Jira plugin
 * {@link JiraScannerPlugin#scan(FileResource, String, Scope, Scanner)} method.
 */
public class GraphBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphBuilder.class);

    // FIXME: We should find a better solution to switch between the mocked and the default implementation.
    // After fixing this we can also move the mock classes to the test package.
    public static String TEST_ENV = "JQASSISTANT_JIRA_PLUGIN_TEST";

    private final JiraRestClientWrapper jiraRestClientWrapper;
    private final CacheEndpoint cacheEndpoint;

    private final HashMap<IssueID, Iterable<IssueLink>> issueLinkCashe;
    private final HashMap<IssueID, Iterable<Subtask>> subtaskCashe;

    GraphBuilder(XMLJiraPluginConfiguration xmlJiraPluginConfiguration, CacheEndpoint cacheEndpoint) {

        this.issueLinkCashe = new HashMap<>();
        this.subtaskCashe = new HashMap<>();
        this.cacheEndpoint = cacheEndpoint;

        String url = xmlJiraPluginConfiguration.getUrl();
        String username = xmlJiraPluginConfiguration.getCredentials()
                .getUser();
        String password = xmlJiraPluginConfiguration.getCredentials()
                .getPassword();

        if (System.getenv(TEST_ENV) != null) {
            jiraRestClientWrapper = new MockedJiraRestClientWrapper();
        } else {
            jiraRestClientWrapper = new DefaultJiraRestClientWrapper(url, username, password);
        }
    }

    void startTraversal(final JiraServer jiraServer, final XMLJiraPluginConfiguration xmlJiraPluginConfiguration) {

        ServerInfo serverInfo = jiraRestClientWrapper.retrieveServerInfo();
        jiraServer.setBaseUri(serverInfo.getBaseUri()
                .toString());
        jiraServer.setVersion(serverInfo.getVersion());
        jiraServer.setBuildNumber(serverInfo.getBuildNumber());
        jiraServer.setBuildDate(convertTime(serverInfo.getBuildDate()));
        jiraServer.setServerTime(convertTime(serverInfo.getServerTime()));
        jiraServer.setScmInfo(serverInfo.getScmInfo());
        jiraServer.setServerTitle(serverInfo.getServerTitle());

        for (Priority priority : jiraRestClientWrapper.retrievePriorities()) {

            JiraPriority jiraPriority = cacheEndpoint.findOrCreatePriority(priority);
            jiraServer.getPriorities()
                    .add(jiraPriority);
        }

        for (Status status : jiraRestClientWrapper.retrieveStatuses()) {

            JiraStatus jiraStatus = cacheEndpoint.findOrCreateStatus(status);
            jiraServer.getStatuses()
                    .add(jiraStatus);
        }

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

            resolveComponentsForProject(jiraProject, project.getComponents());
            resolveLeaderForProject(jiraProject, project.getLead());
            resolveIssues(jiraProject);
        }

        resolveIssueLinks();
        resolveSubtaskRealations();
    }

    private void resolveComponentsForProject(JiraProject jiraProject, Iterable<BasicComponent> basicComponentList) {

        for (BasicComponent basicComponent : basicComponentList) {

            Component component = jiraRestClientWrapper.retrieveComponent(basicComponent.getSelf());
            JiraComponent jiraComponent = cacheEndpoint.findOrCreateComponent(component);

            if (component.getLead() != null) {

                JiraUser jiraUser = findInCacheOrLoadFromJira(component.getLead());
                jiraComponent.setLeader(jiraUser);
            }

            jiraProject.getComponents()
                    .add(jiraComponent);
        }
    }

    /**
     * The {@link BasicUser} which is part of the {@link Project} is not enough as it misses some fields like
     * "emailAddress" or "active". Therefore, we have to load the complete {@link User} separately.
     */
    private void resolveLeaderForProject(JiraProject jiraProject, BasicUser basicUser) {

        JiraUser jiraUser = findInCacheOrLoadFromJira(basicUser);
        jiraProject.setLead(jiraUser);
    }

    private void resolveIssues(JiraProject jiraProject) {

        int batchSize = 25;
        int currentStartIndex = 0;

        LOGGER.info(String.format("Loading issues from index %s to %s ...", currentStartIndex, currentStartIndex + batchSize));
        SearchResult searchResult = jiraRestClientWrapper.retrieveIssues(jiraProject.getKey(), batchSize, currentStartIndex);

        while (currentStartIndex < searchResult.getTotal()) {

            for (Issue issue : searchResult.getIssues()) {

                LOGGER.info(String.format("Processing issue with KEY: '%s'", issue.getKey()));
                JiraIssue jiraIssue = cacheEndpoint.findOrCreateIssue(issue);

                jiraProject.getIssues()
                        .add(jiraIssue);

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
                    jiraIssue.getComponents()
                            .add(jiraComponent);
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
                        jiraIssue.getAffectedVersions()
                                .add(jiraVersion);
                    }
                }

                if (issue.getFixVersions() != null) {
                    for (Version version : issue.getFixVersions()) {

                        JiraVersion jiraVersion = cacheEndpoint.findOrCreateVersion(version);
                        jiraIssue.getFixedVersions()
                                .add(jiraVersion);
                    }
                }

                if (issue.getIssueLinks() != null) {
                    IssueID issueID = IssueID.builder()
                            .jiraId(jiraIssue.getJiraId())
                            .build();
                    issueLinkCashe.put(issueID, issue.getIssueLinks());
                }

                if (issue.getSubtasks() != null) {
                    IssueID issueID = IssueID.builder()
                            .jiraId(jiraIssue.getJiraId())
                            .build();
                    subtaskCashe.put(issueID, issue.getSubtasks());
                }
            }

            currentStartIndex += batchSize;
            LOGGER.info(String.format("Loading issues from index %s to %s ...", currentStartIndex, currentStartIndex + batchSize));
            searchResult = jiraRestClientWrapper.retrieveIssues(jiraProject.getKey(), batchSize, currentStartIndex);
        }

        LOGGER.info("Finished loading issues.");
    }

    private void commentLevel(JiraIssue jiraIssue, Comment comment) {

        JiraComment jiraComment = cacheEndpoint.findOrCreateComment(comment);

        if (comment.getAuthor() != null) {

            JiraUser jiraUser = findInCacheOrLoadFromJira(comment.getAuthor());
            jiraComment.setAuthor(jiraUser);
        }

        if (comment.getUpdateAuthor() != null) {

            JiraUser jiraUser = findInCacheOrLoadFromJira(comment.getUpdateAuthor());
            jiraComment.setUpdateAuthor(jiraUser);
        }

        jiraIssue.getComments()
                .add(jiraComment);
    }

    private void resolveIssueLinks() {

        for (IssueID issueID : issueLinkCashe.keySet()) {

            for (IssueLink issueLink : issueLinkCashe.get(issueID)) {

                try {
                    // Always create the IssueLink, even if the target can not be resolved.
                    JiraIssueLink jiraIssueLink = cacheEndpoint.createIssueLink(issueLink);
                    cacheEndpoint.findIssueOrThrowException(issueID)
                            .getIssueLinks()
                            .add(jiraIssueLink);

                    // This solution is a bit hacky.
                    // Have a look at IssueID.java to understand why this is necessary.
                    String targetIssueUri = issueLink.getTargetIssueUri()
                            .toString();
                    long targetIssueId = Long.valueOf(targetIssueUri.substring(targetIssueUri.lastIndexOf('/') + 1));

                    // This can throw an exception as the target issue does not need to be part of the project.
                    IssueID targetIssueID = IssueID.builder()
                            .jiraId(targetIssueId)
                            .build();
                    JiraIssue targetIssue = cacheEndpoint.findIssueOrThrowException(targetIssueID);

                    jiraIssueLink.setTargetIssue(targetIssue);

                } catch (EntityNotFoundException e) {

                    LOGGER.warn(String.format("Creating a link between issues failed with message: '%s'. Here is the 'IssueLink' object: " +
                                    "'%s'. This can happen as issue links can point at issues which have not been loaded, e.g. " +
                                    "if they are in other projects.",
                            issueLink.toString(), e.getMessage()));
                }
            }
        }
    }

    private void resolveSubtaskRealations() {

        for (IssueID issueID : subtaskCashe.keySet()) {

            for (Subtask subtask : subtaskCashe.get(issueID)) {

                // This solution is a bit hacky.
                // Have a look at IssueID.java to understand why this is necessary.
                String targetIssueUri = subtask.getIssueUri()
                        .toString();
                long targetIssueId = Long.valueOf(targetIssueUri.substring(targetIssueUri.lastIndexOf('/') + 1));
                IssueID targetIssueID = IssueID.builder()
                        .jiraId(targetIssueId)
                        .build();
                JiraIssue targetIssue = cacheEndpoint.findIssueOrThrowException(targetIssueID);

                JiraIssue sourceIssue = cacheEndpoint.findIssueOrThrowException(issueID);
                sourceIssue.getSubtasks()
                        .add(targetIssue);
            }
        }
    }

    private JiraUser findInCacheOrLoadFromJira(BasicUser basicUser) {

        if (cacheEndpoint.isUserAlreadyCached(basicUser)) {

            return cacheEndpoint.findUserOrThrowException(basicUser);
        }

        User userInJira = jiraRestClientWrapper.retrieveUser(basicUser.getSelf());
        return cacheEndpoint.findOrCreateUser(userInJira);
    }
}
