package org.jqassistant.contrib.plugin.jira.jjrc.mock;

import com.atlassian.jira.rest.client.api.domain.IssueType;

import java.net.URI;

public class MockedIssueType {

    public static final long ID = 8542;
    public static final String NAME = "BUG";
    public static final String DESCRIPTION = "This is a description for an issue type.";
    public static final URI SELF = URI.create("http://localhost:8372/issuetype/" + ID);
    public static final URI ICON_URI = URI.create("http://localhost:8372/issuetype/icon/foobar");
    public static final boolean IS_SUBTASK = false;

    IssueType retrieveIssueType() {
        return new IssueType(SELF, ID, NAME, IS_SUBTASK, DESCRIPTION, ICON_URI);
    }
}
