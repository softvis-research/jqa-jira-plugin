package org.jqassistant.contrib.plugin.jira.cache;


import org.jqassistant.contrib.plugin.jira.ids.*;
import org.jqassistant.contrib.plugin.jira.model.*;

import java.util.HashMap;
import java.util.Map;

/**
 * This class caches descriptor instances which have already been created.
 * <p>
 * For more information see {@link CacheEndpoint} which is the public accessible interface for this cache.
 */
class DescriptorCache {

    private Map<ProjectID, JiraProject> projects;
    private Map<IssueID, JiraIssue> issues;
    private Map<UserID, JiraUser> users;
    private Map<VersionID, JiraVersion> versions;
    private Map<ComponentID, JiraComponent> components;
    private Map<IssueTypeID, JiraIssueType> issueTypes;
    private Map<PriorityID, JiraPriority> priorities;
    private Map<StatusID, JiraStatus> statuses;
    private Map<CommentID, JiraComment> comments;

    DescriptorCache() {

        projects = new HashMap<>();
        issues = new HashMap<>();
        users = new HashMap<>();
        versions = new HashMap<>();
        components = new HashMap<>();
        issueTypes = new HashMap<>();
        priorities = new HashMap<>();
        statuses = new HashMap<>();
        comments = new HashMap<>();
    }

    JiraProject get(ProjectID projectID) {

        return projects.get(projectID);
    }

    void put(JiraProject jiraProject, ProjectID projectID) {

        if (!projects.containsKey(projectID)) {
            projects.put(projectID, jiraProject);
        }
    }

    JiraIssue get(IssueID issueID) {

        return issues.get(issueID);
    }

    void put(JiraIssue jiraIssue, IssueID issueID) {

        if (!issues.containsKey(issueID)) {
            issues.put(issueID, jiraIssue);
        }
    }

    JiraUser get(UserID userID) {

        return users.get(userID);
    }

    void put(JiraUser jiraUser, UserID userID) {

        if (!users.containsKey(userID)) {
            users.put(userID, jiraUser);
        }
    }

    JiraVersion get(VersionID versionID) {

        return versions.get(versionID);
    }

    void put(JiraVersion jiraVersion, VersionID versionID) {

        if (!versions.containsKey(versionID)) {
            versions.put(versionID, jiraVersion);
        }
    }

    JiraComponent get(ComponentID componentID) {

        return components.get(componentID);
    }

    void put(JiraComponent jiraComponent, ComponentID componentID) {

        if (!components.containsKey(componentID)) {
            components.put(componentID, jiraComponent);
        }
    }

    JiraIssueType get(IssueTypeID issueTypeID) {

        return issueTypes.get(issueTypeID);
    }

    void put(JiraIssueType jiraIssueType, IssueTypeID issueTypeID) {

        if (!issueTypes.containsKey(issueTypeID)) {
            issueTypes.put(issueTypeID, jiraIssueType);
        }
    }

    JiraPriority get(PriorityID priorityID) {

        return priorities.get(priorityID);
    }

    void put(JiraPriority jiraPriority, PriorityID priorityID) {

        if (!priorities.containsKey(priorityID)) {
            priorities.put(priorityID, jiraPriority);
        }
    }

    JiraStatus get(StatusID statusID) {

        return statuses.get(statusID);
    }

    void put(JiraStatus jiraStatus, StatusID statusID) {

        if (!statuses.containsKey(statusID)) {
            statuses.put(statusID, jiraStatus);
        }
    }

    JiraComment get(CommentID commentID) {

        return comments.get(commentID);
    }

    void put(JiraComment jiraComment, CommentID commentID) {

        if (!comments.containsKey(commentID)) {
            comments.put(commentID, jiraComment);
        }
    }
}