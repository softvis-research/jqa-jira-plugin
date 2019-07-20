package org.jqassistant.contrib.plugin.jira.cache;


import org.jqassistant.contrib.plugin.jira.ids.IssueID;
import org.jqassistant.contrib.plugin.jira.ids.ProjectID;
import org.jqassistant.contrib.plugin.jira.ids.UserID;
import org.jqassistant.contrib.plugin.jira.ids.VersionID;
import org.jqassistant.contrib.plugin.jira.model.JiraIssue;
import org.jqassistant.contrib.plugin.jira.model.JiraProject;
import org.jqassistant.contrib.plugin.jira.model.JiraUser;
import org.jqassistant.contrib.plugin.jira.model.JiraVersion;

import java.util.HashMap;

/**
 * This class caches descriptor instances which have already been created.
 * <p>
 * For more information see {@link CacheEndpoint} which is the public accessible interface for this cache.
 */
class DescriptorCache {

    private HashMap<ProjectID, JiraProject> projects;
    private HashMap<IssueID, JiraIssue> issues;
    private HashMap<UserID, JiraUser> users;
    private HashMap<VersionID, JiraVersion> versions;

    DescriptorCache() {

        projects = new HashMap<>();
        issues = new HashMap<>();
        users = new HashMap<>();
        versions = new HashMap<>();
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
}