package org.jqassistant.contrib.plugin.jira.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Property;
import org.jqassistant.contrib.plugin.jira.model.basic.Jira;
import org.jqassistant.contrib.plugin.jira.model.basic.JiraDescription;
import org.jqassistant.contrib.plugin.jira.model.basic.JiraID;
import org.jqassistant.contrib.plugin.jira.model.basic.JiraName;

@Label("Jira-Issue-Type")
public interface JiraIssueType extends Jira, JiraID, JiraName, JiraDescription {

    @Property("isSubtask")
    boolean isSubtask();
    void setSubtask(boolean subtask);

    @Property("iconUri")
    String getIconUri();
    void setIconUri(String iconUri);
}
