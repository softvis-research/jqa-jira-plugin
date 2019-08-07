package org.jqassistant.contrib.plugin.jira.scanner.builder;

import com.atlassian.jira.rest.client.api.domain.Comment;
import org.jqassistant.contrib.plugin.jira.cache.CacheEndpoint;
import org.jqassistant.contrib.plugin.jira.model.JiraComment;
import org.jqassistant.contrib.plugin.jira.model.JiraIssue;
import org.jqassistant.contrib.plugin.jira.model.JiraUser;

public class CommentBuilder {

    private CacheEndpoint cacheEndpoint;
    private UserBuilder userBuilder;

    public CommentBuilder(CacheEndpoint cacheEndpoint, UserBuilder userBuilder) {

        this.cacheEndpoint = cacheEndpoint;
        this.userBuilder = userBuilder;
    }

    void handleComment(JiraIssue jiraIssue, Comment comment) {

        JiraComment jiraComment = cacheEndpoint.findOrCreateComment(comment);

        if (comment.getAuthor() != null) {

            JiraUser jiraUser = this.userBuilder.findUserInCacheOrLoadItFromJira(comment.getAuthor());
            jiraComment.setAuthor(jiraUser);
        }

        if (comment.getUpdateAuthor() != null) {

            JiraUser jiraUser = this.userBuilder.findUserInCacheOrLoadItFromJira(comment.getUpdateAuthor());
            jiraComment.setUpdateAuthor(jiraUser);
        }

        jiraIssue.getComments()
                .add(jiraComment);
    }
}
