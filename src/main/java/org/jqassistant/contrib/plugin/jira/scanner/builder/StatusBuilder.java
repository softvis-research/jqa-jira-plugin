package org.jqassistant.contrib.plugin.jira.scanner.builder;

import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.Status;
import org.jqassistant.contrib.plugin.jira.cache.CacheEndpoint;
import org.jqassistant.contrib.plugin.jira.jjrc.JiraRestClientWrapper;
import org.jqassistant.contrib.plugin.jira.model.JiraServer;
import org.jqassistant.contrib.plugin.jira.model.JiraStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatusBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatusBuilder.class);

    private final CacheEndpoint cacheEndpoint;
    private final JiraRestClientWrapper jiraRestClientWrapper;

    public StatusBuilder(CacheEndpoint cacheEndpoint, JiraRestClientWrapper jiraRestClientWrapper) {
        this.cacheEndpoint = cacheEndpoint;
        this.jiraRestClientWrapper = jiraRestClientWrapper;
    }

    public void handleStatuses(JiraServer jiraServer) {

        Iterable<Status> statuses;

        try {
            statuses = jiraRestClientWrapper.retrieveStatuses();
        } catch (RestClientException e) {
            LOGGER.warn("An error occured while retrieving statuses:", e);
            return;
        }

        for (Status status : statuses) {

            JiraStatus jiraStatus = cacheEndpoint.findOrCreateStatus(status);
            jiraServer.getStatuses()
                    .add(jiraStatus);
        }
    }
}
