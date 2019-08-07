package org.jqassistant.contrib.plugin.jira.jjrc;

import com.atlassian.jira.rest.client.api.domain.Status;

import java.net.URI;

public class MockedStatus {

    public static final long ID = 7856;
    public static final URI SELF = URI.create("http://localhost:8372/status/" + ID);
    public static final URI ICON_URL = URI.create("http://localhost:8372/status/icon/foobar");
    public static final String NAME = "OPEN";
    public static final String DESCRIPTION = "This is a description for a status.";

    Status retrieveStatus() {
        return new Status(SELF, ID, NAME, DESCRIPTION, ICON_URL);
    }
}
