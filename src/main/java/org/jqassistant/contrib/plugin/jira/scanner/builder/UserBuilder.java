package org.jqassistant.contrib.plugin.jira.scanner.builder;

import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.domain.User;
import org.jqassistant.contrib.plugin.jira.cache.CacheEndpoint;
import org.jqassistant.contrib.plugin.jira.jjrc.JiraRestClientWrapper;
import org.jqassistant.contrib.plugin.jira.model.JiraUser;

public class UserBuilder {

    private CacheEndpoint cacheEndpoint;
    private JiraRestClientWrapper jiraRestClientWrapper;

    public UserBuilder(CacheEndpoint cacheEndpoint, JiraRestClientWrapper jiraRestClientWrapper) {

        this.cacheEndpoint = cacheEndpoint;
        this.jiraRestClientWrapper = jiraRestClientWrapper;
    }

    JiraUser findUserInCacheOrLoadItFromJira(BasicUser basicUser) {

        if (cacheEndpoint.isUserAlreadyCached(basicUser)) {

            return cacheEndpoint.findUserOrThrowException(basicUser);
        }

        User userInJira = jiraRestClientWrapper.retrieveUser(basicUser.getSelf());
        return cacheEndpoint.findOrCreateUser(userInJira);
    }
}
