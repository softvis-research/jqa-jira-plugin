package org.jqassistant.contrib.plugin.jira.utils;

import org.jqassistant.contrib.plugin.jira.jjrc.mock.MockedJiraRestClientWrapper;
import org.jqassistant.contrib.plugin.jira.scanner.GraphBuilder;
import org.jqassistant.contrib.plugin.jira.scanner.ScannerTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;

import static org.jqassistant.contrib.plugin.jira.scanner.GraphBuilder.TEST_ENV;

public abstract class EnvironmentOverrider {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScannerTest.class);

    public static void setTestEnvironmentVariables() throws Exception {

        LOGGER.info("Overriding environment variables inside JVM to make the Jira plugin use the mocked service.");

        Map<String, String> environmentVariables = Collections.singletonMap(TEST_ENV, "true");
        setEnvironmentVariables(environmentVariables);
    }
    public static void setEmptyEnvironmentVariables() throws Exception {

        LOGGER.info("Resetting environment variables inside JVM to make the Jira plugin use the default service.");

        setEnvironmentVariables(Collections.emptyMap());
    }

    /**
     * This is a rather hacky approach to make {@link GraphBuilder} use the
     * {@link MockedJiraRestClientWrapper}
     * instead of the {@link org.jqassistant.contrib.plugin.jira.jjrc.DefaultJiraRestClientWrapper}.
     * <p>
     * According to the stackoverflow post this hack does not modify the actual environment but just those in the JVM:
     * https://stackoverflow.com/questions/318239/how-do-i-set-environment-variables-from-java
     */
    @SuppressWarnings({"unchecked"})
    private static void setEnvironmentVariables(Map<String, String> environmentVariables ) throws Exception {

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
