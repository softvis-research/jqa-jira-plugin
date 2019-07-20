package org.jqassistant.contrib.plugin.jira.cache;

import com.atlassian.jira.rest.client.api.domain.*;
import com.buschmais.jqassistant.core.store.api.Store;
import org.joda.time.DateTime;
import org.jqassistant.contrib.plugin.jira.ids.*;
import org.jqassistant.contrib.plugin.jira.model.*;
import org.neo4j.cypher.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

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

    /**
     * This solution was found here:
     * <p>
     * https://stackoverflow.com/questions/28877981/how-to-convert-from-org-joda-time-datetime-to-java-time-zoneddatetime
     * <p>
     * As stated in the post this solution seems to be faster than <code>dateTime.toGregorianCalendar().toZonedDateTime();</code>
     *
     * @param dateTime The joda-time time which shall be converted.
     * @return The same time as ZonedDateTime.
     */
    private ZonedDateTime convertTime(DateTime dateTime) {

        if (dateTime == null) {
            return null;
        }

        return ZonedDateTime.ofLocal(
                LocalDateTime.of(
                        dateTime.getYear(),
                        dateTime.getMonthOfYear(),
                        dateTime.getDayOfMonth(),
                        dateTime.getHourOfDay(),
                        dateTime.getMinuteOfHour(),
                        dateTime.getSecondOfMinute(),
                        dateTime.getMillisOfSecond() * 1_000_000),
                ZoneId.of(dateTime.getZone().getID(), ZoneId.SHORT_IDS),
                ZoneOffset.ofTotalSeconds(dateTime.getZone().getOffset(dateTime) / 1000));
    }

}
