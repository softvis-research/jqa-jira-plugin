package org.jqassistant.contrib.plugin.jira.jjrc.mock;

import com.atlassian.jira.rest.client.api.domain.BasicComponent;
import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.domain.Component;

import java.net.URI;

public class MockedComponent {

    public static final long ID = 3000;
    public static final URI SELF = URI.create("http://localhost:8372/component/" + ID);
    public static final String NAME = "DevOps";
    public static final String DESCRIPTION = "This is a description for a component.";

    Component retrieveComponent(URI uri, BasicUser basicUser) {

        if (uri == null || !uri.equals(SELF)) {
            throw new IllegalArgumentException("You are trying to load a component which is not mocked. URI: " + uri);
        }

        return new Component(SELF, ID, NAME, DESCRIPTION, basicUser);
    }

    BasicComponent retrieveBasicComponent() {

        return new BasicComponent(SELF, ID, NAME, DESCRIPTION);
    }
}
