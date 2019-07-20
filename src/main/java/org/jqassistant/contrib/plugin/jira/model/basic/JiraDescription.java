package org.jqassistant.contrib.plugin.jira.model.basic;

import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.xo.neo4j.api.annotation.Property;

import java.time.ZonedDateTime;

public interface JiraDescription extends Descriptor {

    @Property("description")
    String getDescription();
    void setDescription(String description);
}
