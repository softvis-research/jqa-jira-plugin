package org.jqassistant.contrib.plugin.jira.scanner;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.core.scanner.api.Scope;
import com.buschmais.jqassistant.plugin.common.api.scanner.filesystem.FileResource;
import org.jqassistant.contrib.plugin.jira.cache.CacheEndpoint;
import org.jqassistant.contrib.plugin.jira.jdom.XMLJiraPluginConfiguration;
import org.jqassistant.contrib.plugin.jira.jjrc.DefaultJiraRestClientWrapper;
import org.jqassistant.contrib.plugin.jira.jjrc.JiraRestClientWrapper;
import org.jqassistant.contrib.plugin.jira.model.JiraServer;
import org.jqassistant.contrib.plugin.jira.scanner.builder.*;

import java.io.IOException;

/**
 * The GraphBuilder gets build only once per execution of the Jira plugin
 * {@link JiraScannerPlugin#scan(FileResource, String, Scope, Scanner)} method.
 */
public class GraphBuilder {

    private final ServerInfoBuilder serverInfoBuilder;
    private final PriorityBuilder priorityBuilder;
    private final StatusBuilder statusBuilder;
    private final IssueLinkBuilder issueLinkBuilder;
    private final SubtaskRelationBuilder subtaskRelationBuilder;
    private final ProjectBuilder projectBuilder;

    GraphBuilder(XMLJiraPluginConfiguration xmlJiraPluginConfiguration, CacheEndpoint cacheEndpoint) throws IOException {

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

    private JiraRestClientWrapper initializeJiraRestClientWrapper(XMLJiraPluginConfiguration xmlJiraPluginConfiguration) throws IOException {

        String restClientClass = System.getProperty(JiraRestClientWrapper.class.getName());
        if (restClientClass != null) {

            try {
                return (JiraRestClientWrapper) Thread.currentThread().getContextClassLoader().loadClass(restClientClass).newInstance();
            } catch (ReflectiveOperationException e) {
                throw new IOException("Cannot load class " + restClientClass);
            }

        } else {
            String url = xmlJiraPluginConfiguration.getUrl();
            String username = xmlJiraPluginConfiguration.getCredentials().getUser();
            String password = xmlJiraPluginConfiguration.getCredentials().getPassword();

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
