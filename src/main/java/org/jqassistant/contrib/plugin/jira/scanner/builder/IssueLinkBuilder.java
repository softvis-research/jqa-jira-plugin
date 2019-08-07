package org.jqassistant.contrib.plugin.jira.scanner.builder;

import com.atlassian.jira.rest.client.api.domain.IssueLink;
import org.jqassistant.contrib.plugin.jira.cache.CacheEndpoint;
import org.jqassistant.contrib.plugin.jira.cache.EntityNotFoundException;
import org.jqassistant.contrib.plugin.jira.ids.IssueID;
import org.jqassistant.contrib.plugin.jira.model.JiraIssue;
import org.jqassistant.contrib.plugin.jira.model.JiraIssueLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class IssueLinkBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(IssueLinkBuilder.class);

    private CacheEndpoint cacheEndpoint;

    private final Map<IssueID, Iterable<IssueLink>> issueLinkCashe;

    public IssueLinkBuilder(CacheEndpoint cacheEndpoint) {

        this.cacheEndpoint = cacheEndpoint;
        this.issueLinkCashe = new HashMap<>();
    }

    public void handleIssueLinks() {

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

    public void cache(IssueID issueID, Iterable<IssueLink> issueLinks) {
        this.issueLinkCashe.put(issueID, issueLinks);
    }
}
