package org.jqassistant.contrib.plugin.jira.jjrc.mock;

import com.atlassian.jira.rest.client.api.domain.BasicPriority;
import com.atlassian.jira.rest.client.api.domain.Priority;
import com.atlassian.jira.rest.client.api.domain.Status;

import java.net.URI;

public class MockedPriority {

    public static final long ID = 7816;
    public static final URI SELF = URI.create("http://localhost:8372/priority/" + ID);
    public static final URI ICON_URI = URI.create("http://localhost:8372/priority/icon/foobar");
    public static final String NAME = "OPEN";
    public static final String DESCRIPTION = "This is a description for a priority.";
    public static final String STATUS_COLOR = "#FF0000";

    Priority retrievePriority() {
        return new Priority(SELF, ID, NAME, STATUS_COLOR, DESCRIPTION, ICON_URI);
    }

    BasicPriority retrieveBasicPriority() {
        return new BasicPriority(SELF, ID, NAME);
    }
}
