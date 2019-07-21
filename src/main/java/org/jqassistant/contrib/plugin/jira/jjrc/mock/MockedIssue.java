package org.jqassistant.contrib.plugin.jira.jjrc.mock;

import com.atlassian.jira.rest.client.api.domain.*;
import org.joda.time.DateTime;

import java.net.URI;
import java.util.Collections;
import java.util.HashSet;

public class MockedIssue {

    public static final long ID = 4000;
    public static final String SUMMARY = "Summary.";
    public static final String DESCRIPTION = "This is a description for an issue.";
    public static final URI SELF = URI.create("http://localhost:8372/issue/" + ID);
    public static final String KEY = "PX-1";

    public static final DateTime CREATION_DATE = DateTime.now();
    public static final DateTime UPDATE_DATE = DateTime.now();
    public static final DateTime DUE_DATE = DateTime.now();

    Issue retrieveIssue(BasicProject basicProject, IssueType issueType, Status status, BasicPriority basicPriority,
                        User reporterAndAssignee, Version affectedAndFixversion, BasicComponent basicComponent,
                        Comment comment, IssueLink issueLink) {

        return new Issue(SUMMARY, SELF, KEY, ID, basicProject, issueType, status, DESCRIPTION, basicPriority,
                null, Collections.emptyList(), reporterAndAssignee, reporterAndAssignee, CREATION_DATE,
                UPDATE_DATE, DUE_DATE, Collections.singletonList(affectedAndFixversion), Collections.singletonList(affectedAndFixversion),
                Collections.singletonList(basicComponent), null, Collections.emptyList(), Collections.singletonList(comment),
                null, Collections.singletonList(issueLink), null, Collections.emptyList(), null, Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList(), null, new HashSet<>()
        );
    }
}
