package org.jqassistant.contrib.plugin.jira.scanner;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;
import org.hamcrest.CoreMatchers;
import org.jqassistant.contrib.plugin.jira.jjrc.JiraRestClientWrapper;
import org.jqassistant.contrib.plugin.jira.jjrc.MockedJiraRestClientWrapper;
import org.jqassistant.contrib.plugin.jira.jjrc.MockedProject;
import org.jqassistant.contrib.plugin.jira.jjrc.MockedServerInfo;
import org.jqassistant.contrib.plugin.jira.model.JiraServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.jqassistant.contrib.plugin.jira.utils.TimeConverter.convertTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ScannerTestIT extends AbstractPluginIT {

    @BeforeAll
    public static void setTestEnvironmentVariables() {
        System.setProperty(JiraRestClientWrapper.class.getName(), MockedJiraRestClientWrapper.class.getName());
    }

    @Test
    public void scanJira() {

        store.beginTransaction();

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("scanner/jira-plugin-configuration.xml")).getFile());

        Descriptor descriptor = getScanner().scan(file, "/scanner/jira-plugin-configuration.xml", DefaultScope.NONE);

        assertThat(descriptor, CoreMatchers.instanceOf(JiraServer.class));

        JiraServer jiraServer = (JiraServer) descriptor;
        assertEquals(MockedServerInfo.BASE_URI.toString(), jiraServer.getBaseUri());
        assertEquals(MockedServerInfo.VERSION, jiraServer.getVersion());
        assertEquals(MockedServerInfo.BUILD_NUMBER, jiraServer.getBuildNumber());
        assertEquals(convertTime(MockedServerInfo.BUILD_DATE), jiraServer.getBuildDate());
        assertEquals(convertTime(MockedServerInfo.SERVER_TIME), jiraServer.getServerTime());
        assertEquals(MockedServerInfo.SCM_INFO, jiraServer.getScmInfo());
        assertEquals(MockedServerInfo.SERVER_TITLE, jiraServer.getServerTitle());

        assertEquals(1, jiraServer.getProjects().size());
        assertNotNull(jiraServer.getProjects().get(0).getLead());

        TestResult testResult = query(
                "MATCH\n" +
                        "    (js:Server)-[:DEFINES_PROJECT]->(p:Project)\n" +
                        "RETURN\n" +
                        "    p.key, js.serverTitle");

        assertEquals(1, testResult.getColumn("p.key").size());
        assertEquals(MockedProject.KEY, testResult.getColumn("p.key").get(0));

        store.commitTransaction();
    }
}
