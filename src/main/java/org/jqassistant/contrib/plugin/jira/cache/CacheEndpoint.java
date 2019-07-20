package org.jqassistant.contrib.plugin.jira.cache;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.Project;
import com.buschmais.jqassistant.core.store.api.Store;
import org.joda.time.DateTime;
import org.jqassistant.contrib.plugin.jira.ids.IssueID;
import org.jqassistant.contrib.plugin.jira.ids.ProjectID;
import org.jqassistant.contrib.plugin.jira.model.JiraIssue;
import org.jqassistant.contrib.plugin.jira.model.JiraProject;
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
