package org.jqassistant.contrib.plugin.jira.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Property;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import org.jqassistant.contrib.plugin.jira.model.basic.Jira;
import org.jqassistant.contrib.plugin.jira.model.basic.JiraAuditInformation;
import org.jqassistant.contrib.plugin.jira.model.basic.JiraID;

@Label("Comment")
public interface JiraComment extends Jira, JiraID, JiraAuditInformation {

    @Property("body")
    String getBody();
    void setBody(String body);

    @Relation("HAS_AUTHOR")
    JiraUser getAuthor();
    void setAuthor(JiraUser jiraUser);

    @Relation("HAS_UPDATE_AUTHOR")
    JiraUser getUpdateAuthor();
    void setUpdateAuthor(JiraUser jiraUser);
}
