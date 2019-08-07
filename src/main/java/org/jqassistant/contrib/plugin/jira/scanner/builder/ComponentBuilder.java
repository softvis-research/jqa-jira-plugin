package org.jqassistant.contrib.plugin.jira.scanner.builder;

import com.atlassian.jira.rest.client.api.domain.BasicComponent;
import com.atlassian.jira.rest.client.api.domain.Component;
import org.jqassistant.contrib.plugin.jira.cache.CacheEndpoint;
import org.jqassistant.contrib.plugin.jira.jjrc.JiraRestClientWrapper;
import org.jqassistant.contrib.plugin.jira.model.JiraComponent;
import org.jqassistant.contrib.plugin.jira.model.JiraProject;
import org.jqassistant.contrib.plugin.jira.model.JiraUser;

public class ComponentBuilder {

    private final CacheEndpoint cacheEndpoint;
    private final JiraRestClientWrapper jiraRestClientWrapper;
    private final UserBuilder userBuilder;

    public ComponentBuilder(CacheEndpoint cacheEndpoint, JiraRestClientWrapper jiraRestClientWrapper, UserBuilder userBuilder) {
        this.jiraRestClientWrapper = jiraRestClientWrapper;
        this.cacheEndpoint = cacheEndpoint;
        this.userBuilder = userBuilder;
    }

    void handleComponents(JiraProject jiraProject, Iterable<BasicComponent> basicComponentList) {

        for (BasicComponent basicComponent : basicComponentList) {

            Component component = jiraRestClientWrapper.retrieveComponent(basicComponent.getSelf());
            JiraComponent jiraComponent = cacheEndpoint.findOrCreateComponent(component);

            if (component.getLead() != null) {

                JiraUser jiraUser = this.userBuilder.findUserInCacheOrLoadItFromJira(component.getLead());
                jiraComponent.setLeader(jiraUser);
            }

            jiraProject.getComponents()
                    .add(jiraComponent);
        }
    }
}
