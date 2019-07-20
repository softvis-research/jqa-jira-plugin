package org.jqassistant.contrib.plugin.jira.ids;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Builder
@EqualsAndHashCode
@ToString
public class IssueID {

    // We tried to use the self URI as ID to be able to parse an issue link into a target URI later.
    // Unfortunately, this doesn't work: The link had the target URI (http://localhost:8080/rest/api/2/issue/10101)
    // while the self link pointed at the URI (http://localhost:8080/rest/api/latest/issue/10101).
    private long jiraId;
}
