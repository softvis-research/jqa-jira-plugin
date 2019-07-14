package org.jqassistant.contrib.plugin.jira.cache;

import com.atlassian.jira.rest.client.api.domain.Project;
import com.buschmais.jqassistant.core.store.api.Store;
import org.jqassistant.contrib.plugin.jira.ids.ProjectID;
import org.jqassistant.contrib.plugin.jira.model.JiraProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheEndpoint.class);

    private final Store store;
    private DescriptorCache descriptorCache;

    public CacheEndpoint(Store store) {

        this.store = store;
        this.descriptorCache = new DescriptorCache();
    }

    public JiraProject findOrCreateProject(Project project) {

        ProjectID projectID = ProjectID.builder().jiraId(project.getId()).build();

        JiraProject jiraProject = descriptorCache.get(projectID);

        if (jiraProject == null) {

            jiraProject = store.create(JiraProject.class);
            jiraProject.setSelf(project.getSelf().toString());
            jiraProject.setJiraId(project.getId());
            jiraProject.setKey(project.getKey());
            jiraProject.setName(project.getName());
            jiraProject.setDescription(project.getDescription());

            if (project.getUri() != null) {
                jiraProject.setUri(project.getUri().toString());
            }

            descriptorCache.put(jiraProject, projectID);
        }

        return jiraProject;
    }
}
