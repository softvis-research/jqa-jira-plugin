package org.jqassistant.contrib.plugin.jira.jjrc.mock;

import com.atlassian.jira.rest.client.api.OptionalIterable;
import com.atlassian.jira.rest.client.api.domain.*;
import org.parboiled.common.StringUtils;

import java.net.URI;
import java.util.Collections;

public class MockedProject {

    public static final long ID = 1000;
    public static final URI SELF = URI.create("http://localhost:8372/project/" + ID);
    public static final String KEY = "PX";
    public static final String NAME = "Random Project Name";
    public static final String DESCRIPTION = "Puh, that's a lot of text.";

    Project retrieveProject(String key, BasicUser basicUser, BasicComponent basicComponent, Version version, IssueType issueType) {

        if (StringUtils.isEmpty(key) || !key.equals(KEY)) {
            throw new IllegalArgumentException("You are trying to load a project which is not mocked. KEY: " + key);
        }

        return new Project(Collections.emptyList(), SELF, KEY, ID, NAME,
                DESCRIPTION, basicUser, SELF, Collections.singletonList(version), Collections.singletonList(basicComponent),
                new OptionalIterable<>(Collections.singletonList(issueType)), Collections.emptyList());
    }

    BasicProject retrieveBasicProject() {
        return new BasicProject(SELF, KEY, ID, NAME);
    }
}
