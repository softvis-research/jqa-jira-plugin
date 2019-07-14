package org.jqassistant.contrib.plugin.jira.model;

import com.buschmais.jqassistant.plugin.common.api.model.FileDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

import java.util.List;

@Label("Jira-Plugin-Configuration-File")
public interface JiraPluginConfigurationFile extends Jira, FileDescriptor {

    @Relation("SPECIFIES_PROJECT")
    List<JiraProject> getProjects();
}
