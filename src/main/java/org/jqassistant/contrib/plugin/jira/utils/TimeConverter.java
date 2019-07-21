package org.jqassistant.contrib.plugin.jira.utils;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import org.joda.time.DateTime;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * {@link JiraRestClient} uses joda-time while <b>jQAssistant</b> uses the Java 8 time.
 *
 * This helper class has been written to convert them.
 */
public abstract class TimeConverter {

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
    public static ZonedDateTime convertTime(DateTime dateTime) {

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
