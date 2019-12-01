package org.jqassistant.contrib.plugin.jira.jdom;

import org.jdom2.JDOMException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class XMLParsingTest {

    private static final String TEST_CONFIGURATION_FILE_NAME = "jdom/jira-plugin-configuration.xml";

    @Test
    public void when_pluginConfigurationFileWasParsed_resultIsCorrect() throws JDOMException, IOException {

        XMLParser xmlParser = new XMLParser();

        XMLJiraPluginConfiguration xmlJiraPluginConfiguration = xmlParser.parseConfiguration(
                this.getClass().getClassLoader().getResourceAsStream(TEST_CONFIGURATION_FILE_NAME));

        assertEquals("http://localhost:32763", xmlJiraPluginConfiguration.getUrl());

        assertEquals("testuser", xmlJiraPluginConfiguration.getCredentials().getUser());
        assertEquals("secret", xmlJiraPluginConfiguration.getCredentials().getPassword());


        assertEquals(2, xmlJiraPluginConfiguration.getProjects().size());
        assertEquals("Project X", xmlJiraPluginConfiguration.getProjects().get(0).getKey());
        assertEquals("Project Y", xmlJiraPluginConfiguration.getProjects().get(1).getKey());
    }
}
