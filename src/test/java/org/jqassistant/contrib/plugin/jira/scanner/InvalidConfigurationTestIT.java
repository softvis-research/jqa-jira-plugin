package org.jqassistant.contrib.plugin.jira.scanner;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Objects;

public class InvalidConfigurationTestIT extends AbstractPluginIT {

    private static final String INVALID_CONFIGURATION_FILE = "scanner/invalid/jira-plugin-configuration.xml";

    @Test
    public void when_configurationForPluginIsInvalid_theExceptionGetsHandled() {

        store.beginTransaction();

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(INVALID_CONFIGURATION_FILE)).getFile());

        getScanner().scan(file, INVALID_CONFIGURATION_FILE, DefaultScope.NONE);

        store.commitTransaction();
    }
}
