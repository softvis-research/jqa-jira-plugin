package org.jqassistant.contrib.plugin.jira.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Property;
import com.buschmais.xo.neo4j.api.annotation.Relation;

import java.util.List;

@Label("Jira-User")
public interface JiraUser extends Jira {

    @Property("self")
    String getSelf();
    void setSelf(String self);

    @Property("displayName")
    String getDisplayName();
    void setDisplayName(String displayName);

    @Property("emailAddress")
    String getEmailAddress();
    void setEmailAddress(String emailAddress);

    @Property("name")
    String getName();
    void setName(String name);

    @Property("active")
    boolean isActive();
    void setActive(boolean active);
}
