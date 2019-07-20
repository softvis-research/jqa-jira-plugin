package org.jqassistant.contrib.plugin.jira.model;

import com.buschmais.jqassistant.plugin.common.api.model.FileDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import org.jqassistant.contrib.plugin.jira.model.basic.Jira;

import java.util.List;

@Label("Jira-Plugin-Configuration-File")
public interface JiraPluginConfigurationFile extends Jira, FileDescriptor {

    @Relation("SPECIFIES")
    List<JiraProject> getProjects();

    @Relation("SPECIFIES")
    List<JiraPriority> getPriorities();

    @Relation("SPECIFIES")
    List<JiraStatus> getStatuses();
}
