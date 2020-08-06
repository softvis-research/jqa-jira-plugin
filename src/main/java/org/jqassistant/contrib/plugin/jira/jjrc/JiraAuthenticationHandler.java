package org.jqassistant.contrib.plugin.jira.jjrc;

import com.atlassian.httpclient.api.Request;
import com.atlassian.jira.rest.client.api.AuthenticationHandler;
import org.jqassistant.contrib.plugin.jira.jdom.XMLApiToken;

import java.util.Base64;

public class JiraAuthenticationHandler implements AuthenticationHandler {

    private final String email;
    private final String token;

    JiraAuthenticationHandler(XMLApiToken xmlApiToken) {
        email = xmlApiToken.getEmail();
        token = xmlApiToken.getToken();
    }

    @Override
    public void configure(Request.Builder builder) {

        String source = email + ":" + token;
        String base64 = Base64.getEncoder().encodeToString(source.getBytes());
        String header = "Basic " + base64;
        builder.setHeader("Authorization", header);
    }
}
