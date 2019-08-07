package org.jqassistant.contrib.plugin.jira.jjrc;

import com.atlassian.jira.rest.client.api.domain.IssueLink;
import com.atlassian.jira.rest.client.api.domain.IssueLinkType;

public class MockedIssueLink {

    public static final String NAME = "Issue Link Type 1";
    public static final String DESCRIPTION = "Description for an issue link type.";

    public static final IssueLinkType.Direction DIRECTION = IssueLinkType.Direction.INBOUND;

    IssueLink retrieveIssueLink() {

        return new IssueLink(MockedIssue.KEY, MockedIssue.SELF, new IssueLinkType(NAME, DESCRIPTION, DIRECTION));
    }
}
