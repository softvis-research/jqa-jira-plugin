package org.jqassistant.contrib.plugin.jira.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Property;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import org.jqassistant.contrib.plugin.jira.model.basic.*;

import java.time.ZonedDateTime;
import java.util.List;

@Label("Issue-Link")
public interface JiraIssueLink extends Jira, JiraName, JiraDescription {

    @Property("direction")
    String getDirection();
    void setDirection(String direction);

    @Property("targetIssueKey")
    String getTargetIssueKey();
    void setTargetIssueKey(String targetIssueKey);

    @Property("targetIssueUri")
    String getTargetIssueUri();
    void setTargetIssueUri(String targetIssueUri);

    @Relation("POINTS_AT")
    JiraIssue getTargetIssue();
    void setTargetIssue(JiraIssue jiraTargetIssue);
}
