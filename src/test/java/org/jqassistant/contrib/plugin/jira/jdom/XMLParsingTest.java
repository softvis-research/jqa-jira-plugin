package org.jqassistant.contrib.plugin.jira.jdom;

import org.jdom2.JDOMException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class XMLParsingTest {

    private static final String TEST_CONFIGURATION_FILE_NAME = "jdom/jira-plugin-configuration.xml";
    private static final String TEST_CONFIGURATION_WITHOUT_CREDENTIALS_FILE_NAME = "jdom/jira-plugin-configuration-without-credentials.xml";

    private XMLParser xmlParser = new XMLParser();

    @Test
    public void when_pluginConfigurationFileWasParsed_resultIsCorrect() throws JDOMException, IOException {

        XMLJiraPluginConfiguration xmlJiraPluginConfiguration = xmlParser.parseConfiguration(
                this.getClass().getClassLoader().getResourceAsStream(TEST_CONFIGURATION_FILE_NAME));

        assertEquals("http://localhost:32763", xmlJiraPluginConfiguration.getUrl());

        Optional<XMLCredentials> credentials = xmlJiraPluginConfiguration.getCredentials();
        assertTrue(credentials.isPresent());

        assertEquals("testuser", xmlJiraPluginConfiguration.getCredentials().get().getUser());
        assertEquals("secret", xmlJiraPluginConfiguration.getCredentials().get().getPassword());


        assertEquals(2, xmlJiraPluginConfiguration.getProjects().size());
        assertEquals("Project X", xmlJiraPluginConfiguration.getProjects().get(0).getKey());
        assertEquals("Project Y", xmlJiraPluginConfiguration.getProjects().get(1).getKey());
    }

    @Test
    public void when_pluginConfigurationWithoutCredentialsWasParsed_resultIsCorrect() throws JDOMException, IOException {

        XMLJiraPluginConfiguration xmlJiraPluginConfiguration = xmlParser.parseConfiguration(
                this.getClass().getClassLoader().getResourceAsStream(TEST_CONFIGURATION_WITHOUT_CREDENTIALS_FILE_NAME));

        assertEquals("http://localhost:32763", xmlJiraPluginConfiguration.getUrl());
        assertFalse(xmlJiraPluginConfiguration.getCredentials().isPresent());
        assertEquals(2, xmlJiraPluginConfiguration.getProjects().size());
        assertEquals("Project X", xmlJiraPluginConfiguration.getProjects().get(0).getKey());
        assertEquals("Project Y", xmlJiraPluginConfiguration.getProjects().get(1).getKey());
    }
}
