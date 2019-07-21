package org.jqassistant.contrib.plugin.jira.jdom;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class XMLJiraPluginConfiguration {

    private String url;
    private XMLCredentials credentials;
    private List<XMLJiraProject> projects;
}
