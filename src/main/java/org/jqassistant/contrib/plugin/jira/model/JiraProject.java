package org.jqassistant.contrib.plugin.jira.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Property;
import com.buschmais.xo.neo4j.api.annotation.Relation;

import java.util.List;

@Label("Jira-Project")
public interface JiraProject extends Jira, JiraID {

    @Property("key")
    String getKey();
    void setKey(String key);

    @Property("name")
    String getName();
    void setName(String name);

    @Property("description")
    String getDescription();
    void setDescription(String description);

    @Property("uri")
    String getUri();
    void setUri(String uri);

    @Relation("CONTAINS")
    List<JiraIssue> getIssues();

    @Relation("LEAD_BY")
    JiraUser getUser();
    void setUser(JiraUser jiraUser);

    @Relation("HAS_VERSION")
    List<JiraVersion> getVersions();
}
