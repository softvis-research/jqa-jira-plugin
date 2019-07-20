package org.jqassistant.contrib.plugin.jira.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Property;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import org.jqassistant.contrib.plugin.jira.model.basic.Jira;
import org.jqassistant.contrib.plugin.jira.model.basic.JiraAuditInformation;
import org.jqassistant.contrib.plugin.jira.model.basic.JiraDescription;
import org.jqassistant.contrib.plugin.jira.model.basic.JiraID;

import java.time.ZonedDateTime;
import java.util.List;

@Label("Jira-Issue")
public interface JiraIssue extends Jira, JiraID, JiraAuditInformation, JiraDescription {

    @Property("key")
    String getKey();
    void setKey(String key);

    @Property("summary")
    String getSummary();
    void setSummary(String summary);

    @Property("dueDate")
    ZonedDateTime getDueDate();
    void setDueDate(ZonedDateTime dueDate);

    @Relation("REPORTED_BY")
    JiraUser getReporter();
    void setReporter(JiraUser jiraUser);

    @Relation("ASSIGNED_TO")
    JiraUser getAssignee();
    void setAssignee(JiraUser jiraUser);

    @Relation("CONCERNES")
    List<JiraComponent> getComponents();

    @Relation("IS_OF_TYPE")
    JiraIssueType getIssueType();
    void setIssueType(JiraIssueType jiraIssueType);

    @Relation("IS_OF_PRIORITY")
    JiraPriority getPriority();
    void setPriority(JiraPriority jiraPriority);

    @Relation("HAS_STATUS")
    JiraStatus getStatus();
    void setStatus(JiraStatus jiraStatus);

    @Relation("HAS_COMMENT")
    List<JiraComment> getComments();
}
