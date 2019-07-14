package org.jqassistant.contrib.plugin.jira.model;

import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.xo.neo4j.api.annotation.Property;

public interface JiraID extends Descriptor {

    @Property("jiraId")
    long getJiraId();
    void setJiraId(long jiraId);

    @Property("self")
    String getSelf();
    void setSelf(String self);
}
