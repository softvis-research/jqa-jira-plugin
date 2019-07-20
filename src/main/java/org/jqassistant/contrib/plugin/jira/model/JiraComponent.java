package org.jqassistant.contrib.plugin.jira.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Property;
import com.buschmais.xo.neo4j.api.annotation.Relation;

import java.time.ZonedDateTime;

@Label("Jira-Component")
public interface JiraComponent extends Jira, JiraID, JiraDescription {

    @Property("name")
    String getName();
    void setName(String name);

    @Relation("LEAD_BY")
    JiraUser getLeader();
    void setLeader(JiraUser jiraUser);
}
