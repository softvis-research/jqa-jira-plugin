package org.jqassistant.contrib.plugin.jira.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Property;
import org.jqassistant.contrib.plugin.jira.model.basic.Jira;
import org.jqassistant.contrib.plugin.jira.model.basic.JiraDescription;
import org.jqassistant.contrib.plugin.jira.model.basic.JiraID;
import org.jqassistant.contrib.plugin.jira.model.basic.JiraName;

import java.time.ZonedDateTime;

@Label("Version")
public interface JiraVersion extends Jira, JiraID, JiraName, JiraDescription {

    @Property("isArchived")
    boolean isArchived();
    void setArchived(boolean archived);

    @Property("isReleased")
    boolean isReleased();
    void setReleased(boolean released);

    @Property("releaseDate")
    ZonedDateTime getReleaseDate();
    void setReleaseDate(ZonedDateTime releaseDate);
}
