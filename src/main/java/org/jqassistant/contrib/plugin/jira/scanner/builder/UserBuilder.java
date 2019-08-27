package org.jqassistant.contrib.plugin.jira.scanner.builder;

import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.domain.User;
import org.jqassistant.contrib.plugin.jira.cache.CacheEndpoint;
import org.jqassistant.contrib.plugin.jira.jjrc.JiraRestClientWrapper;
import org.jqassistant.contrib.plugin.jira.model.JiraUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserBuilder.class);

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

        User userInJira;
        try {
            userInJira = jiraRestClientWrapper.retrieveUser(basicUser.getSelf());

        } catch (RestClientException e) {
            LOGGER.warn(String.format("An error occured while retrieving an user with self link: '%s'", basicUser.getSelf()), e);
            return null;
        }

        return cacheEndpoint.findOrCreateUser(userInJira);
    }
}
