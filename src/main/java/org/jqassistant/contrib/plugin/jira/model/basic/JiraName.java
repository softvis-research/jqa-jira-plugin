package org.jqassistant.contrib.plugin.jira.model.basic;

import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.xo.neo4j.api.annotation.Property;

public interface JiraName extends Descriptor {

    @Property("name")
    String getName();
    void setName(String name);
}
