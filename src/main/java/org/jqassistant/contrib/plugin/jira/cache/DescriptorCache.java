package org.jqassistant.contrib.plugin.jira.cache;


import org.jqassistant.contrib.plugin.jira.ids.IssueID;
import org.jqassistant.contrib.plugin.jira.ids.ProjectID;
import org.jqassistant.contrib.plugin.jira.model.JiraIssue;
import org.jqassistant.contrib.plugin.jira.model.JiraProject;

import java.util.HashMap;

/**
 * This class caches descriptor instances which have already been created.
 * <p>
 * For more information see {@link CacheEndpoint} which is the public accessible interface for this cache.
 */
class DescriptorCache {

    private HashMap<ProjectID, JiraProject> projects;
    private HashMap<IssueID, JiraIssue> issues;

    DescriptorCache() {

        projects = new HashMap<>();
        issues = new HashMap<>();
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
}