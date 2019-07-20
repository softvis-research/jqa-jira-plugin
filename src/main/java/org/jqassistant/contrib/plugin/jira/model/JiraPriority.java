package org.jqassistant.contrib.plugin.jira.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Property;
import org.jqassistant.contrib.plugin.jira.model.basic.Jira;
import org.jqassistant.contrib.plugin.jira.model.basic.JiraDescription;
import org.jqassistant.contrib.plugin.jira.model.basic.JiraID;
import org.jqassistant.contrib.plugin.jira.model.basic.JiraName;

@Label("Jira-Priority")
public interface JiraPriority extends Jira, JiraID, JiraName, JiraDescription {

    @Property("statusColor")
    String getStatusColor();
    void setStatusColor(String statusColor);

    @Property("iconUrl")
    String getIconUrl();
    void setIconUrl(String iconUrl);
}
