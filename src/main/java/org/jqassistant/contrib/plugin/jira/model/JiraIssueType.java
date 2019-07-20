package org.jqassistant.contrib.plugin.jira.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Property;

import java.time.ZonedDateTime;

@Label("Jira-Issue-Type")
public interface JiraIssueType extends Jira, JiraID, JiraDescription {

    @Property("name")
    String getName();
    void setName(String name);

    @Property("isSubtask")
    boolean isSubtask();
    void setSubtask(boolean subtask);

    @Property("iconUri")
    String getIconUri();
    void setIconUri(String iconUri);
}
