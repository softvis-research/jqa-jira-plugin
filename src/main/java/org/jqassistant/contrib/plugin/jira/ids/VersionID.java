package org.jqassistant.contrib.plugin.jira.ids;

import lombok.Builder;
import lombok.EqualsAndHashCode;

@Builder
@EqualsAndHashCode
public class VersionID {

    private long jiraId;
}
