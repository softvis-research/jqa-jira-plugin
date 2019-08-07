package org.jqassistant.contrib.plugin.jira.scanner.builder;

import com.atlassian.jira.rest.client.api.domain.Subtask;
import org.jqassistant.contrib.plugin.jira.cache.CacheEndpoint;
import org.jqassistant.contrib.plugin.jira.ids.IssueID;
import org.jqassistant.contrib.plugin.jira.model.JiraIssue;

import java.util.HashMap;
import java.util.Map;

public class SubtaskRelationBuilder {

    private final CacheEndpoint cacheEndpoint;

    private final Map<IssueID, Iterable<Subtask>> subtaskCashe;

    public SubtaskRelationBuilder(CacheEndpoint cacheEndpoint) {

        this.subtaskCashe = new HashMap<>();
        this.cacheEndpoint = cacheEndpoint;
    }

    public void handleSubtaskRelations() {

        for (IssueID issueID : subtaskCashe.keySet()) {

            for (Subtask subtask : subtaskCashe.get(issueID)) {

                // This solution is a bit hacky.
                // Have a look at IssueID.java to understand why this is necessary.
                String targetIssueUri = subtask.getIssueUri()
                        .toString();
                long targetIssueId = Long.valueOf(targetIssueUri.substring(targetIssueUri.lastIndexOf('/') + 1));
                IssueID targetIssueID = IssueID.builder()
                        .jiraId(targetIssueId)
                        .build();
                JiraIssue targetIssue = cacheEndpoint.findIssueOrThrowException(targetIssueID);

                JiraIssue sourceIssue = cacheEndpoint.findIssueOrThrowException(issueID);
                sourceIssue.getSubtasks()
                        .add(targetIssue);
            }
        }
    }

    public void cache(IssueID issueID, Iterable<Subtask> subtasks) {
        this.subtaskCashe.put(issueID, subtasks);
    }
}
