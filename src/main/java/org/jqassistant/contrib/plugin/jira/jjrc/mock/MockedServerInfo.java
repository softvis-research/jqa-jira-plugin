package org.jqassistant.contrib.plugin.jira.jjrc.mock;

import com.atlassian.jira.rest.client.api.domain.ServerInfo;
import org.joda.time.DateTime;

import java.net.URI;

public class MockedServerInfo {

    public static final URI BASE_URI = URI.create("http://localhost:8372");
    public static final String VERSION = "v0.1";
    public static final int BUILD_NUMBER = 1;
    public static final DateTime BUILD_DATE = DateTime.now();
    public static final DateTime SERVER_TIME = DateTime.now();
    public static final String SCM_INFO = "scmInfo";
    public static final String SERVER_TITLE = "Server Title";

    ServerInfo retrieveServerInfo() {

        return new ServerInfo(BASE_URI, VERSION, BUILD_NUMBER, BUILD_DATE,
                SERVER_TIME, SCM_INFO, SERVER_TITLE);
    }
}
