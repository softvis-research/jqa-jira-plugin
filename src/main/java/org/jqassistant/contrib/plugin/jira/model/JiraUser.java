package org.jqassistant.contrib.plugin.jira.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Property;
import org.jqassistant.contrib.plugin.jira.model.basic.Jira;
import org.jqassistant.contrib.plugin.jira.model.basic.JiraName;

@Label("User")
public interface JiraUser extends Jira, JiraName {

    @Property("self")
    String getSelf();
    void setSelf(String self);

    @Property("displayName")
    String getDisplayName();
    void setDisplayName(String displayName);

    @Property("emailAddress")
    String getEmailAddress();
    void setEmailAddress(String emailAddress);

    @Property("active")
    boolean isActive();
    void setActive(boolean active);
}
