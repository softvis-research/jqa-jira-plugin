package org.jqassistant.contrib.plugin.jira.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Property;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import org.jqassistant.contrib.plugin.jira.model.basic.Jira;
import org.jqassistant.contrib.plugin.jira.model.basic.JiraDescription;
import org.jqassistant.contrib.plugin.jira.model.basic.JiraID;
import org.jqassistant.contrib.plugin.jira.model.basic.JiraName;

import java.util.List;

@Label("Jira-Project")
public interface JiraProject extends Jira, JiraID, JiraName, JiraDescription {

    @Property("key")
    String getKey();
    void setKey(String key);

    @Property("uri")
    String getUri();
    void setUri(String uri);

    @Relation("CONTAINS")
    List<JiraIssue> getIssues();

    @Relation("LEAD_BY")
    JiraUser getLead();
    void setLead(JiraUser jiraUser);

    @Relation("HAS_VERSION")
    List<JiraVersion> getVersions();

    @Relation("HAS_COMPONENT")
    List<JiraComponent> getComponents();

    @Relation("DEFINES")
    List<JiraIssueType> getIssueTypes();
}
