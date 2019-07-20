package org.jqassistant.contrib.plugin.jira.model;

import com.buschmais.jqassistant.plugin.common.api.model.FileDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Property;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import org.jqassistant.contrib.plugin.jira.model.basic.Jira;

import java.time.ZonedDateTime;
import java.util.List;

@Label("Jira-Server")
public interface JiraServer extends Jira, FileDescriptor {

    @Property("baseUri")
    String getBaseUri();
    void setBaseUri(String baseUri);

    @Property("version")
    String getVersion();
    void setVersion(String version);

    @Property("buildNumber")
    long getBuildNumber();
    void setBuildNumber(long buildNumber);

    @Property("buildDate")
    ZonedDateTime getBuildDate();
    void setBuildDate(ZonedDateTime buildDate);

    @Property("serverTime")
    ZonedDateTime getServerTime();
    void setServerTime(ZonedDateTime serverTime);

    @Property("scmInfo")
    String getScmInfo();
    void setScmInfo(String scmInfo);

    @Property("serverTitle")
    String getServerTitle();
    void setServerTitle(String serverTitle);

    @Relation("SPECIFIES")
    List<JiraProject> getProjects();

    @Relation("SPECIFIES")
    List<JiraPriority> getPriorities();

    @Relation("SPECIFIES")
    List<JiraStatus> getStatuses();
}
