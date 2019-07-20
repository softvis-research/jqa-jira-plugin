package org.jqassistant.contrib.plugin.jira.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Property;

import java.time.ZonedDateTime;

@Label("Jira-Version")
public interface JiraVersion extends Jira, JiraID {

    @Property("name")
    String getName();
    void setName(String name);

    @Property("description")
    String getDescription();
    void setDescription(String description);

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
