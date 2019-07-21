package org.jqassistant.contrib.plugin.jira.scanner;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;
import org.jqassistant.contrib.plugin.jira.utils.EnvironmentOverrider;
import org.junit.Test;

import java.io.File;
import java.util.Objects;

public class NoConnectionTest extends AbstractPluginIT {

    private static final String CONFIGURATION_FILE = "scanner/jira-plugin-configuration.xml";

    @Test
    public void when_noConnectionToTheServerIsPossible_theExceptionGetsHandled() throws Exception {

        // This scan fails because it tries to use the DefaultJiraRestClientWrapper instead of the
        // MockedJiraRestClientWrapper.
        EnvironmentOverrider.setEmptyEnvironmentVariables();

        store.beginTransaction();

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(CONFIGURATION_FILE)).getFile());

        getScanner().scan(file, CONFIGURATION_FILE, DefaultScope.NONE);

        store.commitTransaction();
    }
}
