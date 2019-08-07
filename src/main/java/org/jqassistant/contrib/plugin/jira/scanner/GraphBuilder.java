package org.jqassistant.contrib.plugin.jira.scanner;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.core.scanner.api.Scope;
import com.buschmais.jqassistant.plugin.common.api.scanner.filesystem.FileResource;
import org.jqassistant.contrib.plugin.jira.cache.CacheEndpoint;
import org.jqassistant.contrib.plugin.jira.jdom.XMLJiraPluginConfiguration;
import org.jqassistant.contrib.plugin.jira.jjrc.DefaultJiraRestClientWrapper;
import org.jqassistant.contrib.plugin.jira.jjrc.JiraRestClientWrapper;
import org.jqassistant.contrib.plugin.jira.jjrc.mock.MockedJiraRestClientWrapper;
import org.jqassistant.contrib.plugin.jira.model.JiraServer;
import org.jqassistant.contrib.plugin.jira.scanner.builder.*;

/**
 * The GraphBuilder gets build only once per execution of the Jira plugin
 * {@link JiraScannerPlugin#scan(FileResource, String, Scope, Scanner)} method.
 */
public class GraphBuilder {

    // FIXME: We should find a better solution to switch between the mocked and the default implementation.
    // After fixing this we can also move the mock classes to the test package.
    public static String TEST_ENV = "JQASSISTANT_JIRA_PLUGIN_TEST";

    private final ServerInfoBuilder serverInfoBuilder;
    private final PriorityBuilder priorityBuilder;
    private final StatusBuilder statusBuilder;
    private final IssueLinkBuilder issueLinkBuilder;
    private final SubtaskRelationBuilder subtaskRelationBuilder;
    private final ProjectBuilder projectBuilder;

    GraphBuilder(XMLJiraPluginConfiguration xmlJiraPluginConfiguration, CacheEndpoint cacheEndpoint) {

        JiraRestClientWrapper jiraRestClientWrapper = this.initializeJiraRestClientWrapper(xmlJiraPluginConfiguration);

        this.serverInfoBuilder = new ServerInfoBuilder(jiraRestClientWrapper);
        this.priorityBuilder = new PriorityBuilder(cacheEndpoint, jiraRestClientWrapper);
        this.statusBuilder = new StatusBuilder(cacheEndpoint, jiraRestClientWrapper);
        this.issueLinkBuilder = new IssueLinkBuilder(cacheEndpoint);
        this.subtaskRelationBuilder = new SubtaskRelationBuilder(cacheEndpoint);

        UserBuilder userBuilder = new UserBuilder(cacheEndpoint, jiraRestClientWrapper);
        CommentBuilder commentBuilder = new CommentBuilder(cacheEndpoint, userBuilder);
        IssueBuilder issueBuilder = new IssueBuilder(cacheEndpoint, jiraRestClientWrapper, commentBuilder, issueLinkBuilder, subtaskRelationBuilder);
        ComponentBuilder componentBuilder = new ComponentBuilder(cacheEndpoint, jiraRestClientWrapper, userBuilder);

        this.projectBuilder = new ProjectBuilder(cacheEndpoint, jiraRestClientWrapper, componentBuilder, issueBuilder, userBuilder);
    }

    private JiraRestClientWrapper initializeJiraRestClientWrapper(XMLJiraPluginConfiguration xmlJiraPluginConfiguration) {

        String url = xmlJiraPluginConfiguration.getUrl();
        String username = xmlJiraPluginConfiguration.getCredentials().getUser();
        String password = xmlJiraPluginConfiguration.getCredentials().getPassword();

        if (System.getenv(TEST_ENV) != null) {
            return new MockedJiraRestClientWrapper();
        } else {
            return new DefaultJiraRestClientWrapper(url, username, password);
        }
    }

    void startTraversal(final JiraServer jiraServer, final XMLJiraPluginConfiguration xmlJiraPluginConfiguration) {

        this.serverInfoBuilder.handleServerInfo(jiraServer);

        this.priorityBuilder.handlePriorities(jiraServer);
        this.statusBuilder.handleStatuses(jiraServer);

        this.projectBuilder.handleProjects(jiraServer, xmlJiraPluginConfiguration);

        this.issueLinkBuilder.handleIssueLinks();
        this.subtaskRelationBuilder.handleSubtaskRelations();
    }
}
