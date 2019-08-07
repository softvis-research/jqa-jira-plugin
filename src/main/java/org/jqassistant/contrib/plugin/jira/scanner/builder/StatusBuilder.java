package org.jqassistant.contrib.plugin.jira.scanner.builder;

import com.atlassian.jira.rest.client.api.domain.Status;
import org.jqassistant.contrib.plugin.jira.cache.CacheEndpoint;
import org.jqassistant.contrib.plugin.jira.jjrc.JiraRestClientWrapper;
import org.jqassistant.contrib.plugin.jira.model.JiraServer;
import org.jqassistant.contrib.plugin.jira.model.JiraStatus;

public class StatusBuilder {

    private final CacheEndpoint cacheEndpoint;
    private final JiraRestClientWrapper jiraRestClientWrapper;

    public StatusBuilder(CacheEndpoint cacheEndpoint, JiraRestClientWrapper jiraRestClientWrapper) {
        this.cacheEndpoint = cacheEndpoint;
        this.jiraRestClientWrapper = jiraRestClientWrapper;
    }

    public void handleStatuses(JiraServer jiraServer) {

        for (Status status : jiraRestClientWrapper.retrieveStatuses()) {

            JiraStatus jiraStatus = cacheEndpoint.findOrCreateStatus(status);
            jiraServer.getStatuses()
                    .add(jiraStatus);
        }
    }
}
