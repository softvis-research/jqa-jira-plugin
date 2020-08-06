package org.jqassistant.contrib.plugin.jira.jjrc;

import com.atlassian.jira.rest.client.api.StatusCategory;

import java.net.URI;

public class MockedStatusCategory {

    public static final long ID = 35876389;
    public static final URI SELF = URI.create("http://localhost:8372/status-category/" + ID); // FIXME: This link is random.
    public static final String NAME = "Status";
    public static final String KEY = "This is a description for a status.";
    public static final String COLOR_NAME = "red";

    StatusCategory retrieveStatusCategory() {
        return new StatusCategory(SELF, NAME, ID, KEY, COLOR_NAME);
    }
}
