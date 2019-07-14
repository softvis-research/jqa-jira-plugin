package org.jqassistant.contrib.plugin.jira.jdom;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@link XMLParser} class contains two functions which parse the configuration file for the Jira plugin.
 * Therefore, JDOM is used as the XML library of choice.
 *
 * @see <a href="http://www.jdom.org">JDOM</a>
 */
public class XMLParser {

    private static final String URL_ELEMENT_NAME = "url";

    private static final String PROJECTS_ELEMENT_NAME = "projects";

    private static final String CREDENTIALS_ELEMENT_NAME = "credentials";

    private static final String USERNAME_ELEMENT_NAME = "username";
    private static final String PASSWORD_ELEMENT_NAME = "password";

    private static final String PROJECT_KEY_ELEMENT_NAME = "key";

    /**
     * Parses the plugin configuration file.
     *
     * @param inputStream The InputStream that shall be used.
     * @return The Jira plugin configuration.
     * @throws JDOMException If XML parsing failed.
     * @throws IOException   If reading the config file failed.
     */
    public XMLJiraPluginConfiguration parseConfiguration(InputStream inputStream) throws JDOMException, IOException {

        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(inputStream);

        String jiraUrl = this.parseJiraUrl(document);
        XMLCredentials xmlCredentials = this.parseCredentials(document);
        List<XMLJiraProject> xmlJiraProjectList = this.parseJiraProjects(document);

        return new XMLJiraPluginConfiguration(jiraUrl, xmlCredentials, xmlJiraProjectList);
    }


    private String parseJiraUrl(Document document) {

        return document.getRootElement().getChildText(URL_ELEMENT_NAME);
    }

    private XMLCredentials parseCredentials(Document document) {

        Element credentialsElement = document.getRootElement().getChild(CREDENTIALS_ELEMENT_NAME);

        String username = credentialsElement.getChildText(USERNAME_ELEMENT_NAME);
        String password = credentialsElement.getChildText(PASSWORD_ELEMENT_NAME);

        return new XMLCredentials(username, password);
    }

    private List<XMLJiraProject> parseJiraProjects(Document document) {

        List<XMLJiraProject> xmlJiraProjectList = new ArrayList<>();

        for (Element jiraProject : document.getRootElement().getChild(PROJECTS_ELEMENT_NAME).getChildren()) {

            String key = jiraProject.getChildText(PROJECT_KEY_ELEMENT_NAME);

            xmlJiraProjectList.add(new XMLJiraProject(key));
        }

        return xmlJiraProjectList;
    }
}
