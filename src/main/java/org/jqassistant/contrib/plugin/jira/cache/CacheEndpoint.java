package org.jqassistant.contrib.plugin.jira.cache;

import com.atlassian.jira.rest.client.api.domain.*;
import com.buschmais.jqassistant.core.store.api.Store;
import org.joda.time.DateTime;
import org.jqassistant.contrib.plugin.jira.ids.*;
import org.jqassistant.contrib.plugin.jira.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.jqassistant.contrib.plugin.jira.utils.TimeConverter.convertTime;

public class CacheEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheEndpoint.class);

    private final Store store;
    private DescriptorCache descriptorCache;

    public CacheEndpoint(Store store) {

        this.store = store;
        this.descriptorCache = new DescriptorCache();
    }

    public JiraProject findOrCreateProject(Project project) {

        ProjectID projectID = ProjectID.builder().jiraId(project.getId()).build();

        JiraProject jiraProject = descriptorCache.get(projectID);

        if (jiraProject == null) {

            jiraProject = store.create(JiraProject.class);
            jiraProject.setSelf(project.getSelf().toString());
            jiraProject.setJiraId(project.getId());
            jiraProject.setKey(project.getKey());
            jiraProject.setName(project.getName());
            jiraProject.setDescription(project.getDescription());

            if (project.getUri() != null) {
                jiraProject.setUri(project.getUri().toString());
            }

            descriptorCache.put(jiraProject, projectID);
        }

        return jiraProject;
    }

    public JiraIssue findOrCreateIssue(Issue issue) {

        IssueID issueID = IssueID.builder().jiraId(issue.getId()).build();

        JiraIssue jiraIssue = descriptorCache.get(issueID);

        if (jiraIssue == null) {

            jiraIssue = store.create(JiraIssue.class);
            jiraIssue.setSelf(issue.getSelf().toString());
            jiraIssue.setJiraId(issue.getId());

            jiraIssue.setKey(issue.getKey());
            jiraIssue.setSummary(issue.getSummary());
            jiraIssue.setDescription(issue.getDescription());

            jiraIssue.setCreationDate(convertTime(issue.getCreationDate()));
            jiraIssue.setUpdateDate(convertTime(issue.getUpdateDate()));

            jiraIssue.setDueDate(convertTime(issue.getDueDate()));

            descriptorCache.put(jiraIssue, issueID);
        }

        return jiraIssue;
    }


    public JiraUser findOrCreateUser(User user) {

        UserID userID = UserID.builder().name(user.getName()).build();

        JiraUser jiraUser = descriptorCache.get(userID);

        if (jiraUser == null) {

            jiraUser = store.create(JiraUser.class);

            jiraUser.setSelf(user.getSelf().toString());
            jiraUser.setDisplayName(user.getDisplayName());

            jiraUser.setEmailAddress(user.getEmailAddress());
            jiraUser.setName(user.getName());
            jiraUser.setActive(user.isActive());

            descriptorCache.put(jiraUser, userID);
        }

        return jiraUser;
    }

    public JiraVersion findOrCreateVersion(Version version) {

        VersionID versionID = VersionID.builder().jiraId(version.getId()).build();

        JiraVersion jiraVersion = descriptorCache.get(versionID);

        if (jiraVersion == null) {

            jiraVersion = store.create(JiraVersion.class);

            jiraVersion.setSelf(version.getSelf().toString());
            jiraVersion.setJiraId(version.getId());

            jiraVersion.setDescription(version.getDescription());
            jiraVersion.setName(version.getName());
            jiraVersion.setArchived(version.isArchived());
            jiraVersion.setReleased(version.isReleased());
            jiraVersion.setReleaseDate(convertTime(version.getReleaseDate()));

            descriptorCache.put(jiraVersion, versionID);
        }

        return jiraVersion;
    }

    public JiraComponent findOrCreateComponent(Component component) {

        ComponentID componentID = ComponentID.builder().jiraId(component.getId()).build();

        JiraComponent jiraComponent = descriptorCache.get(componentID);

        if (jiraComponent == null) {

            jiraComponent = store.create(JiraComponent.class);

            jiraComponent.setSelf(component.getSelf().toString());
            jiraComponent.setJiraId(component.getId());

            jiraComponent.setDescription(component.getDescription());
            jiraComponent.setName(component.getName());

            descriptorCache.put(jiraComponent, componentID);
        }

        return jiraComponent;
    }

    public JiraComponent findComponentOrThrowException(BasicComponent basicComponent) {

        ComponentID componentID = ComponentID.builder().jiraId(basicComponent.getId()).build();

        JiraComponent jiraComponent = descriptorCache.get(componentID);

        if (jiraComponent == null) {
            throw new IllegalArgumentException("We can't find a JiraComponent with ID: " + componentID);
        }

        return jiraComponent;
    }

    public JiraIssueType findOrCreateIssueType(IssueType issueType) {

        IssueTypeID issueTypeID = IssueTypeID.builder().jiraId(issueType.getId()).build();

        JiraIssueType jiraIssueType = descriptorCache.get(issueTypeID);

        if (jiraIssueType == null) {

            jiraIssueType = store.create(JiraIssueType.class);

            jiraIssueType.setSelf(issueType.getSelf().toString());
            jiraIssueType.setJiraId(issueType.getId());

            jiraIssueType.setDescription(issueType.getDescription());
            jiraIssueType.setName(issueType.getName());

            jiraIssueType.setSubtask(issueType.isSubtask());

            if (issueType.getIconUri() != null) {
                jiraIssueType.setIconUri(issueType.getIconUri().toString());
            }

            descriptorCache.put(jiraIssueType, issueTypeID);
        }

        return jiraIssueType;
    }

    public JiraPriority findOrCreatePriority(Priority priority) {

        PriorityID priorityID = PriorityID.builder().jiraId(priority.getId()).build();

        JiraPriority jiraPriority = descriptorCache.get(priorityID);

        if (jiraPriority == null) {

            jiraPriority = store.create(JiraPriority.class);

            jiraPriority.setSelf(priority.getSelf().toString());
            jiraPriority.setJiraId(priority.getId());

            jiraPriority.setDescription(priority.getDescription());
            jiraPriority.setName(priority.getName());

            jiraPriority.setStatusColor(priority.getStatusColor());

            if (priority.getIconUri() != null) {
                jiraPriority.setIconUri(priority.getIconUri().toString());
            }

            descriptorCache.put(jiraPriority, priorityID);
        }

        return jiraPriority;
    }

    public JiraPriority findPriorityOrThrowException(BasicPriority basicPriority) {

        PriorityID priorityID = PriorityID.builder().jiraId(basicPriority.getId()).build();

        JiraPriority jiraPriority = descriptorCache.get(priorityID);

        if (jiraPriority == null) {
            throw new IllegalArgumentException("We can't find a JiraPriority with ID: " + priorityID);
        }

        return jiraPriority;
    }

    public JiraStatus findOrCreateStatus(Status status) {

        StatusID statusID = StatusID.builder().jiraId(status.getId()).build();

        JiraStatus jiraStatus = descriptorCache.get(statusID);

        if (jiraStatus == null) {

            jiraStatus = store.create(JiraStatus.class);

            jiraStatus.setSelf(status.getSelf().toString());
            jiraStatus.setJiraId(status.getId());

            jiraStatus.setDescription(status.getDescription());
            jiraStatus.setName(status.getName());

            if (status.getIconUrl() != null) {
                jiraStatus.setIconUri(status.getIconUrl().toString());
            }

            descriptorCache.put(jiraStatus, statusID);
        }

        return jiraStatus;
    }

    public JiraComment findOrCreateComment(Comment comment) {

        CommentID commentID = CommentID.builder().jiraId(comment.getId()).build();

        JiraComment jiraComment = descriptorCache.get(commentID);

        if (jiraComment == null) {

            jiraComment = store.create(JiraComment.class);

            jiraComment.setSelf(comment.getSelf().toString());
            jiraComment.setJiraId(comment.getId());

            jiraComment.setCreationDate(convertTime(comment.getCreationDate()));
            jiraComment.setUpdateDate(convertTime(comment.getUpdateDate()));

            jiraComment.setBody(comment.getBody());


            descriptorCache.put(jiraComment, commentID);
        }

        return jiraComment;
    }


    public JiraIssueLink createIssueLink(IssueLink issueLink) {

        JiraIssueLink jiraIssueLink = store.create(JiraIssueLink.class);

        jiraIssueLink.setName(issueLink.getIssueLinkType().getName());
        jiraIssueLink.setDescription(issueLink.getIssueLinkType().getDescription());

        jiraIssueLink.setTargetIssueKey(issueLink.getTargetIssueKey());
        jiraIssueLink.setTargetIssueUri(issueLink.getTargetIssueUri().toString());

        return jiraIssueLink;
    }

    public JiraIssue findIssueOrThrowException(IssueID issueId) {

        JiraIssue jiraIssue = descriptorCache.get(issueId);

        if (jiraIssue == null) {
            throw new IllegalArgumentException("We can't find a JiraIssue with ID: " + issueId);
        }

        return jiraIssue;
    }
}
