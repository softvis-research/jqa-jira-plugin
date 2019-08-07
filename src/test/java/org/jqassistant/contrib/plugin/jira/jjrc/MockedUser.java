package org.jqassistant.contrib.plugin.jira.jjrc;

import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.domain.User;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

public class MockedUser {

    public static final long ID = 2000;
    public static final URI SELF = URI.create("http://localhost:8372/user/" + ID);
    public static final String NAME = "User Name";
    public static final String DISPLAY_NAME = "User Display Name";
    public static final String EMAIL_ADDRESS = "test@mock.foo";
    public static final boolean IS_ACTIVE = true;
    public static final Map<String, URI> AVATAR_URIS = Collections.singletonMap(User.S48_48, URI.create("http://localhost:8372/avatar/foobaravatarmock"));

    User retrieveUser(URI uri) {

        if (uri == null || !uri.equals(SELF)) {
            throw new IllegalArgumentException("You are trying to load a user which is not mocked. URI: " + uri);
        }

        return new User(SELF, NAME, DISPLAY_NAME, EMAIL_ADDRESS, IS_ACTIVE,
                null, AVATAR_URIS, null);
    }

    BasicUser retrieveBasicUser() {
        return new BasicUser(SELF, NAME, DISPLAY_NAME);
    }
}
