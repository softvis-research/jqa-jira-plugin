package org.jqassistant.contrib.plugin.jira.scanner;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.core.scanner.api.ScannerPlugin;
import com.buschmais.jqassistant.core.scanner.api.Scope;
import com.buschmais.jqassistant.plugin.common.api.model.FileDescriptor;
import com.buschmais.jqassistant.plugin.common.api.scanner.AbstractScannerPlugin;
import com.buschmais.jqassistant.plugin.common.api.scanner.filesystem.FileResource;
import lombok.extern.slf4j.Slf4j;
import org.jqassistant.contrib.plugin.jira.model.JiraPluginConfigurationFile;

/**
 * The {@link JiraScannerPlugin} class is the centerpiece of the Jira plugin.
 * <p>
 * It specifies which files shall be processed by the plugin (see {@link #accepts(FileResource, String, Scope)}).
 * Furthermore, it starts the scan process with its {@link #scan(FileResource, String, Scope, Scanner)} method.
 */
@Slf4j
@ScannerPlugin.Requires(FileDescriptor.class)
public class JiraScannerPlugin extends AbstractScannerPlugin<FileResource, JiraPluginConfigurationFile> {

    private static final String JQASSISTANT_PLUGIN_JIRA_FILENAME = "jqassistant.plugin.jira.filename";

    private String jiraFileName = "jira-plugin-configuration.xml";

    /**
     * This method checks if the user wants to override the default configuration file name
     * by setting a property.
     */
    @Override
    protected void configure() {
        super.configure();

        if (getProperties().containsKey(JQASSISTANT_PLUGIN_JIRA_FILENAME)) {
            jiraFileName = (String) getProperties().get(JQASSISTANT_PLUGIN_JIRA_FILENAME);
        }
        log.info("Jira plugin looks for files named '{}'.", jiraFileName);
    }


    /**
     * This methods ensures that only the configuration file for the plugin will be processed.
     *
     * @param item  The file that shall be checked.
     * @param path  The path of the file.
     * @param scope The current jQAssistant scope.
     * @return True if the file matches a certain suffix, otherwise False.
     */
    @Override
    public boolean accepts(FileResource item, String path, Scope scope) {
        boolean accepted = path.toLowerCase().endsWith(jiraFileName);

        if (accepted) {
            log.debug("Jira plugin accepted file '{}'.", path);
        }
        return accepted;
    }

    /**
     * This function is called for every file which got accepted by the plugin.
     * <p>
     * First, it parses the XML configuration file.
     * Afterwards, it uses the Jira project information to start making calls against the Jira REST API.
     *
     * @param item    The current accepted file which must be a valid configuration file.
     * @param path    The path of the configuration file.
     * @param scope   The current jQAssistant scope.
     * @param scanner The jQAssistant scanner which will be used to extract the main descriptor and the jQAssistant
     *                store.
     * @return The main descriptor which can specify multiple repositories.
     */
    @Override
    public JiraPluginConfigurationFile scan(FileResource item, String path, Scope scope, Scanner scanner) {

        log.debug("Jira plugin scans file '{}'.", path);

        // Create the root descriptor
        FileDescriptor fileDescriptor = scanner.getContext().getCurrentDescriptor();
        return scanner
                .getContext()
                .getStore()
                .addDescriptorType(fileDescriptor, JiraPluginConfigurationFile.class);
    }
}

