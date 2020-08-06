package org.jqassistant.contrib.plugin.jira.jdom;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Getter
public class XMLJiraPluginConfiguration {

    private String url;
    private Optional<XMLCredentials> credentials;
    private Optional<XMLApiToken> apiToken;
    private List<XMLJiraProject> projects;
}
