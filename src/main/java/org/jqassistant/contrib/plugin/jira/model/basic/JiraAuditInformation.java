package org.jqassistant.contrib.plugin.jira.model.basic;

import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.xo.neo4j.api.annotation.Property;

import java.time.ZonedDateTime;

public interface JiraAuditInformation extends Descriptor {

    @Property("creationDate")
    ZonedDateTime getCreationDate();
    void setCreationDate(ZonedDateTime creationDate);

    @Property("updateDate")
    ZonedDateTime getUpdateDate();
    void setUpdateDate(ZonedDateTime updateDate);
}
