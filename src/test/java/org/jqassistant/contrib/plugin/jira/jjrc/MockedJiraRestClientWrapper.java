package org.jqassistant.contrib.plugin.jira.jjrc;

import com.atlassian.jira.rest.client.api.domain.*;

import java.net.URI;
import java.util.Collections;

public class MockedJiraRestClientWrapper implements JiraRestClientWrapper {

    private final MockedServerInfo mockedServerInfo;
    private final MockedComponent mockedComponent;
    private final MockedProject mockedProject;
    private final MockedUser mockedUser;
    private final MockedVersion mockedVersion;
    private final MockedStatus mockedStatus;
    private final MockedPriority mockedPriority;
    private final MockedIssueType mockedIssueType;
    private final MockedIssue mockedIssue;
    private final MockedComment mockedComment;
    private final MockedIssueLink mockedIssueLink;

    public MockedJiraRestClientWrapper() {

        this.mockedServerInfo = new MockedServerInfo();
        this.mockedComponent = new MockedComponent();
        this.mockedProject = new MockedProject();
        this.mockedUser = new MockedUser();
        this.mockedVersion = new MockedVersion();
        this.mockedStatus = new MockedStatus();
        this.mockedPriority = new MockedPriority();
        this.mockedIssue = new MockedIssue();
        this.mockedIssueType = new MockedIssueType();
        this.mockedComment = new MockedComment();
        this.mockedIssueLink = new MockedIssueLink();
    }

    @Override
    public ServerInfo retrieveServerInfo() {
        return mockedServerInfo.retrieveServerInfo();
    }

    @Override
    public Iterable<Priority> retrievePriorities() {
        return Collections.singletonList(mockedPriority.retrievePriority());
    }

    @Override
    public Iterable<Status> retrieveStatuses() {
        return Collections.singletonList(mockedStatus.retrieveStatus());
    }

    @Override
    public Project retrieveProject(String key) {

        return mockedProject.retrieveProject(key, mockedUser.retrieveBasicUser(),
                mockedComponent.retrieveBasicComponent(), mockedVersion.retrieveVersion(), mockedIssueType.retrieveIssueType());
    }

    @Override
    public Component retrieveComponent(URI uri) {

        return mockedComponent.retrieveComponent(uri, mockedUser.retrieveBasicUser());
    }

    @Override
    public User retrieveUser(URI uri) {
        return mockedUser.retrieveUser(uri);
    }

    @Override
    public SearchResult retrieveIssues(String projectKey, int maxResults, int startAt) {

        if (startAt > 0) {
            return new SearchResult(0, 0, 0, Collections.emptyList());
        }

        Iterable<Issue> issues = Collections.singletonList(
                mockedIssue.retrieveIssue(
                        mockedProject.retrieveBasicProject(),
                        mockedIssueType.retrieveIssueType(),
                        mockedStatus.retrieveStatus(),
                        mockedPriority.retrieveBasicPriority(),
                        mockedUser.retrieveUser(MockedUser.SELF),
                        mockedVersion.retrieveVersion(),
                        mockedComponent.retrieveBasicComponent(),
                        mockedComment.retrieveComment(mockedUser.retrieveBasicUser()),
                        mockedIssueLink.retrieveIssueLink()));

        return new SearchResult(1, 0, 1, issues);
    }
}
