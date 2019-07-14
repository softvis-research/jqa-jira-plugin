package org.jqassistant.contrib.plugin.jira.cache;


import org.jqassistant.contrib.plugin.jira.ids.ProjectID;
import org.jqassistant.contrib.plugin.jira.model.JiraProject;

import java.util.HashMap;

/**
 * This class caches descriptor instances which have already been created.
 * <p>
 * For more information see {@link CacheEndpoint} which is the public accessible interface for this cache.
 */
class DescriptorCache {

    private HashMap<ProjectID, JiraProject> projects;

    DescriptorCache() {

        projects = new HashMap<>();
    }

    JiraProject get(ProjectID projectID) {

        return projects.get(projectID);
    }

    void put(JiraProject jiraProject, ProjectID projectID) {

        if (!projects.containsKey(projectID)) {
            projects.put(projectID, jiraProject);
        }
    }
}