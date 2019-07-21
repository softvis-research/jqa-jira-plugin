package org.jqassistant.contrib.plugin.jira.jjrc.mock;

import com.atlassian.jira.rest.client.api.domain.Version;

import java.net.URI;

public class MockedVersion {

    public static final long ID = 3710;
    public static final URI SELF = URI.create("http://localhost:8372/version/" + ID);
    public static final String NAME = "v0.1";
    public static final String DESCRIPTION = "This is a description for a version.";
    public static final boolean IS_ARCHIVED = false;
    public static final boolean IS_RELEASED = false;

    Version retrieveVersion() {

        return new Version(SELF, ID, NAME, DESCRIPTION, IS_ARCHIVED, IS_RELEASED, null);
    }
}
