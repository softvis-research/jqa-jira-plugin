package org.jqassistant.contrib.plugin.jira.jdom;

import org.jdom2.JDOMException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class XMLParsingTest {

    private static final String TEST_CONFIGURATION_FILE_NAME = "jdom/jira-plugin-configuration.xml";

    @Test
    public void when_pluginConfigurationFileWasParsed_resultIsCorrect() throws JDOMException, IOException {

        XMLParser xmlParser = new XMLParser();

        XMLJiraPluginConfiguration xmlJiraPluginConfiguration = xmlParser.parseConfiguration(
                this.getClass().getClassLoader().getResourceAsStream(TEST_CONFIGURATION_FILE_NAME));

        Assert.assertEquals("http://localhost:32763", xmlJiraPluginConfiguration.getUrl());

        Assert.assertEquals("testuser", xmlJiraPluginConfiguration.getCredentials().getUser());
        Assert.assertEquals("secret", xmlJiraPluginConfiguration.getCredentials().getPassword());


        Assert.assertEquals(2, xmlJiraPluginConfiguration.getProjects().size());
        Assert.assertEquals("Project X", xmlJiraPluginConfiguration.getProjects().get(0).getKey());
        Assert.assertEquals("Project Y", xmlJiraPluginConfiguration.getProjects().get(1).getKey());
    }
}
