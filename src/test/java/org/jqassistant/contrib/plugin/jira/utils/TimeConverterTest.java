package org.jqassistant.contrib.plugin.jira.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;


public class TimeConverterTest {

    /**
     * Covers the following problem:
     * <p>
     * Java creates a default constructor even for abstract classes.
     * To improve the code coverage for abstract classes this test was written.
     * <p>
     * See: https://sourceforge.net/p/cobertura/bugs/17/
     */
    @Test
    public void coverAbstractClass() {

        new TimeConverter() {
        };
    }

    @Test
    public void when_nullShallBeConverted_nullGetsReturned() {
        assertNull(TimeConverter.convertTime(null));
    }
}
