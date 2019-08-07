package org.jqassistant.contrib.plugin.jira.jjrc;

import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.domain.Comment;
import org.joda.time.DateTime;

import java.net.URI;

public class MockedComment {

    public static final long ID = 1963;
    public static final URI SELF = URI.create("http://localhost:8372/comment/" + ID);

    public static final String BODY = "Body of a comment.";

    public static final DateTime CREATION_DATE = DateTime.now();
    public static final DateTime UPDATE_DATE = DateTime.now();

    Comment retrieveComment(BasicUser authorAndUpdateAuthor) {

        return new Comment(SELF, BODY, authorAndUpdateAuthor, authorAndUpdateAuthor, CREATION_DATE, UPDATE_DATE, null, ID);
    }
}
