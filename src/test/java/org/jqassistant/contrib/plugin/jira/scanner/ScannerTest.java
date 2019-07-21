package org.jqassistant.contrib.plugin.jira.scanner;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;
import org.hamcrest.CoreMatchers;
import org.jqassistant.contrib.plugin.jira.jjrc.MockedJiraRestClientWrapper;
import org.jqassistant.contrib.plugin.jira.model.JiraServer;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import static org.jqassistant.contrib.plugin.jira.scanner.GraphBuilder.TEST_ENV;
import static org.jqassistant.contrib.plugin.jira.utils.TimeConverter.convertTime;
import static org.junit.Assert.*;

public class ScannerTest extends AbstractPluginIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScannerTest.class);

    @Test
    public void scanJira() {

        store.beginTransaction();

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("jira-plugin-configuration.xml")).getFile());

        Descriptor descriptor = getScanner().scan(file, "/jira-plugin-configuration.xml", DefaultScope.NONE);

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

    /**
     * This is a rather hacky approach to make {@link GraphBuilder} use the
     * {@link org.jqassistant.contrib.plugin.jira.jjrc.MockedJiraRestClientWrapper}
     * instead of the {@link org.jqassistant.contrib.plugin.jira.jjrc.DefaultJiraRestClientWrapper}.
     * <p>
     * According to the stackoverflow post this hack does not modify the actual environment but just those in the JVM:
     * https://stackoverflow.com/questions/318239/how-do-i-set-environment-variables-from-java
     */
    @BeforeClass
    @SuppressWarnings({"unchecked"})
    public static void setTestEnvironmentVariables() throws Exception {

        LOGGER.info("Overriding environment variables inside JVM to make the Jira plugin use the mocked service.");

        Map<String, String> environmentVariables = Collections.singletonMap(TEST_ENV, "true");

        try {
            Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
            Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
            theEnvironmentField.setAccessible(true);
            Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
            env.putAll(environmentVariables);
            Field theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
            theCaseInsensitiveEnvironmentField.setAccessible(true);
            Map<String, String> cienv = (Map<String, String>) theCaseInsensitiveEnvironmentField.get(null);
            cienv.putAll(environmentVariables);
        } catch (NoSuchFieldException e) {
            Class[] classes = Collections.class.getDeclaredClasses();
            Map<String, String> env = System.getenv();
            for (Class cl : classes) {
                if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
                    Field field = cl.getDeclaredField("m");
                    field.setAccessible(true);
                    Object obj = field.get(env);
                    Map<String, String> map = (Map<String, String>) obj;
                    map.clear();
                    map.putAll(environmentVariables);
                }
            }
        }
    }
}
