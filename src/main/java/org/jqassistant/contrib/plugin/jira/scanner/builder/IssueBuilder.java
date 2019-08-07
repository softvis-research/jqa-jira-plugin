package org.jqassistant.contrib.plugin.jira.scanner.builder;

import com.atlassian.jira.rest.client.api.domain.*;
import org.jqassistant.contrib.plugin.jira.cache.CacheEndpoint;
import org.jqassistant.contrib.plugin.jira.ids.IssueID;
import org.jqassistant.contrib.plugin.jira.jjrc.JiraRestClientWrapper;
import org.jqassistant.contrib.plugin.jira.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IssueBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(IssueBuilder.class);

    private final CacheEndpoint cacheEndpoint;
    private final JiraRestClientWrapper jiraRestClientWrapper;

    private final CommentBuilder commentBuilder;
    private final IssueLinkBuilder issueLinkBuilder;
    private final SubtaskRelationBuilder subtaskRelationBuilder;

    public IssueBuilder(CacheEndpoint cacheEndpoint, JiraRestClientWrapper jiraRestClientWrapper, CommentBuilder commentBuilder, IssueLinkBuilder issueLinkBuilder, SubtaskRelationBuilder subtaskRelationBuilder) {
        this.cacheEndpoint = cacheEndpoint;
        this.jiraRestClientWrapper = jiraRestClientWrapper;
        this.commentBuilder = commentBuilder;
        this.issueLinkBuilder = issueLinkBuilder;
        this.subtaskRelationBuilder = subtaskRelationBuilder;
    }

    void handleIssues(JiraProject jiraProject) {

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
                    this.commentBuilder.handleComment(jiraIssue, comment);
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
                    this.issueLinkBuilder.cache(issueID, issue.getIssueLinks());
                }

                if (issue.getSubtasks() != null) {
                    IssueID issueID = IssueID.builder()
                            .jiraId(jiraIssue.getJiraId())
                            .build();
                    this.subtaskRelationBuilder.cache(issueID, issue.getSubtasks());
                }
            }

            currentStartIndex += batchSize;
            LOGGER.info(String.format("Loading issues from index %s to %s ...", currentStartIndex, currentStartIndex + batchSize));
            searchResult = jiraRestClientWrapper.retrieveIssues(jiraProject.getKey(), batchSize, currentStartIndex);
        }

        LOGGER.info("Finished loading issues.");
    }
}
