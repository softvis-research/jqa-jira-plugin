package org.jqassistant.contrib.plugin.jira.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Property;
import com.buschmais.xo.neo4j.api.annotation.Relation;

import java.time.ZonedDateTime;

@Label("Jira-Issue")
public interface JiraIssue extends Jira, JiraID, JiraAuditInformation {

    @Property("key")
    String getKey();
    void setKey(String key);

    @Property("summary")
    String getSummary();
    void setSummary(String summary);

    @Property("description")
    String getDescription();
    void setDescription(String description);

    @Property("dueDate")
    ZonedDateTime getDueDate();
    void setDueDate(ZonedDateTime dueDate);

    @Relation("REPORTED_BY")
    JiraUser getReporter();
    void setReporter(JiraUser jiraUser);

    @Relation("ASSIGNED_TO")
    JiraUser getAssignee();
    void setAssignee(JiraUser jiraUser);
}
