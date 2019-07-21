package org.jqassistant.contrib.plugin.jira.scanner;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;
import org.hamcrest.CoreMatchers;
import org.jqassistant.contrib.plugin.jira.jjrc.MockedJiraRestClientWrapper;
import org.jqassistant.contrib.plugin.jira.model.JiraServer;
import org.jqassistant.contrib.plugin.jira.utils.EnvironmentOverrider;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.Objects;

import static org.jqassistant.contrib.plugin.jira.utils.TimeConverter.convertTime;
import static org.junit.Assert.*;

public class ScannerTest extends AbstractPluginIT {

    @BeforeClass
    public static void setTestEnvironmentVariables() throws Exception {
        EnvironmentOverrider.setTestEnvironmentVariables();
    }

    @Test
    public void scanJira() {

        store.beginTransaction();

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("scanner/jira-plugin-configuration.xml")).getFile());

        Descriptor descriptor = getScanner().scan(file, "/scanner/jira-plugin-configuration.xml", DefaultScope.NONE);

        assertThat(descriptor, CoreMatchers.instanceOf(JiraServer.class));

        JiraServer jiraServer = (JiraServer) descriptor;
        assertEquals(MockedJiraRestClientWrapper.SERVER_BASE_URI.toString(), jiraServer.getBaseUri());
        assertEquals(MockedJiraRestClientWrapper.SERVER_VERSION, jiraServer.getVersion());
        assertEquals(MockedJiraRestClientWrapper.SERVER_BUILD_NUMBER, jiraServer.getBuildNumber());
        assertEquals(convertTime(MockedJiraRestClientWrapper.SERVER_BUILD_DATE), jiraServer.getBuildDate());
        assertEquals(convertTime(MockedJiraRestClientWrapper.SERVER_SERVER_TIME), jiraServer.getServerTime());
        assertEquals(MockedJiraRestClientWrapper.SERVER_SCM_INFO, jiraServer.getScmInfo());
        assertEquals(MockedJiraRestClientWrapper.SERVER_SERVER_TITLE, jiraServer.getServerTitle());

        assertEquals(1, jiraServer.getProjects().size());
        assertNotNull(jiraServer.getProjects().get(0).getLead());

        TestResult testResult = query(
                "MATCH\n" +
                        "    (js:`Jira-Server`)-[:SPECIFIES]->(p:`Jira-Project`)\n" +
                        "RETURN\n" +
                        "    p.key, js.serverTitle");

        assertEquals(1, testResult.getColumn("p.key").size());
        assertEquals("Project X", testResult.getColumn("p.key").get(0));

        store.commitTransaction();
    }
}
