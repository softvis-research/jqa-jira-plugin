package org.jqassistant.contrib.plugin.jira.jjrc;

import com.atlassian.jira.rest.client.api.OptionalIterable;
import com.atlassian.jira.rest.client.api.domain.*;
import org.joda.time.DateTime;
import org.parboiled.common.StringUtils;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

public class MockedJiraRestClientWrapper implements JiraRestClientWrapper {

    public static final URI SERVER_BASE_URI = URI.create("http://localhost:8372");
    public static final String SERVER_VERSION = "v0.1";
    public static final int SERVER_BUILD_NUMBER = 1;
    public static final DateTime SERVER_BUILD_DATE = DateTime.now();
    public static final DateTime SERVER_SERVER_TIME = DateTime.now();
    public static final String SERVER_SCM_INFO = "scmInfo";
    public static final String SERVER_SERVER_TITLE = "Server Title";

    public static final long PROJECT_ID = 1000;
    public static final URI PROJECT_SELF = SERVER_BASE_URI.resolve("project/" + PROJECT_ID);
    public static final String PROJECT_KEY = "Project X";
    public static final String PROJECT_NAME = "Random Project Name";
    public static final String PROJECT_DESCRIPTION = "Puh, that's a lot of text.";

    public static final long USER_ID = 1000;
    public static final URI USER_SELF = SERVER_BASE_URI.resolve("user/" + USER_ID);
    public static final String USER_NAME = "User Name";
    public static final String USER_DISPLAY_NAME = "User Display Name";
    public static final String USER_EMAIL_ADDRESS = "test@mock.foo";
    public static final boolean USER_IS_ACTIVE = true;
    public static final Map<String, URI> USER_AVATAR_URIS = Collections.singletonMap(User.S48_48, SERVER_BASE_URI.resolve("avatar/foobaravatarmock"));


    @Override
    public ServerInfo retrieveServerInfo() {

        return new ServerInfo(SERVER_BASE_URI, SERVER_VERSION, SERVER_BUILD_NUMBER, SERVER_BUILD_DATE,
                SERVER_SERVER_TIME, SERVER_SCM_INFO, SERVER_SERVER_TITLE);
    }

    @Override
    public Iterable<Priority> retrievePriorities() {
        return Collections.emptyList();
    }

    @Override
    public Iterable<Status> retrieveStatuses() {
        return Collections.emptyList();
    }

    @Override
    public Project retrieveProject(String key) {

        if (StringUtils.isEmpty(key) || !key.equals(PROJECT_KEY)) {
            throw new IllegalArgumentException("You are trying to load a project which is not mocked. KEY: " + key);
        }

        return new Project(Collections.emptyList(), PROJECT_SELF, PROJECT_KEY, PROJECT_ID, PROJECT_NAME,
                PROJECT_DESCRIPTION, mockBasicUser(), PROJECT_SELF, Collections.emptyList(), Collections.emptyList(),
                new OptionalIterable<>(Collections.emptyList()), Collections.emptyList());
    }

    private BasicUser mockBasicUser() {

        return new BasicUser(USER_SELF, USER_NAME, USER_DISPLAY_NAME);
    }

    @Override
    public Component retrieveComponent(URI uri) {
        return null;
    }

    @Override
    public User retrieveUser(URI uri) {
        return new User(USER_SELF, USER_NAME, USER_DISPLAY_NAME, USER_EMAIL_ADDRESS, USER_IS_ACTIVE,
                null, USER_AVATAR_URIS, null);
    }

    @Override
    public Iterable<Issue> retrieveIssues(String projectKey) {
        return Collections.emptyList();
    }
}
