package org.jqassistant.contrib.plugin.jira.ids;

import lombok.Builder;
import lombok.EqualsAndHashCode;

@Builder
@EqualsAndHashCode
public class CommentID {

    private long jiraId;
}
