package org.jqassistant.contrib.plugin.jira.scanner.builder;

import com.atlassian.jira.rest.client.api.domain.Priority;
import org.jqassistant.contrib.plugin.jira.cache.CacheEndpoint;
import org.jqassistant.contrib.plugin.jira.jjrc.JiraRestClientWrapper;
import org.jqassistant.contrib.plugin.jira.model.JiraPriority;
import org.jqassistant.contrib.plugin.jira.model.JiraServer;

public class PriorityBuilder {

    private final CacheEndpoint cacheEndpoint;
    private final JiraRestClientWrapper jiraRestClientWrapper;

    public PriorityBuilder(CacheEndpoint cacheEndpoint, JiraRestClientWrapper jiraRestClientWrapper) {
        this.cacheEndpoint = cacheEndpoint;
        this.jiraRestClientWrapper = jiraRestClientWrapper;
    }

    public void handlePriorities(JiraServer jiraServer) {

        for (Priority priority : jiraRestClientWrapper.retrievePriorities()) {

            JiraPriority jiraPriority = cacheEndpoint.findOrCreatePriority(priority);
            jiraServer.getPriorities().add(jiraPriority);
        }
    }
}
