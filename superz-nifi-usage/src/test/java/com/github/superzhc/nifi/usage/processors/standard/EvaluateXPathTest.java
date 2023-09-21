package com.github.superzhc.nifi.usage.processors.standard;

import org.apache.nifi.processors.standard.EvaluateXPath;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.List;

public class EvaluateXPathTest {
    TestRunner runner;

    @BeforeEach
    public void setUp() {
        runner = TestRunners.newTestRunner(EvaluateXPath.class);
    }

    @Test
    public void smzdm_rss_faxian_item() throws Exception {
        runner.setProperty(EvaluateXPath.DESTINATION, EvaluateXPath.DESTINATION_ATTRIBUTE);
        runner.setProperty("title", "/item/guid");
        runner.setProperty("url", "/item/title");
        runner.setProperty("pubDate", "/item/pubDate");
        runner.setProperty("link", "/item/link");
        runner.setProperty("focus_pic", "/item/focus_pic");
        runner.setProperty("description", "/item/description");
        runner.setProperty("content", "/item/*[local-name()='encoded']/text()");

        runner.enqueue(Paths.get(this.getClass().getResource("/smzdm_rss_faxian_item.xml").toURI()));

        runner.run();
        runner.assertQueueEmpty();

        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(EvaluateXPath.REL_MATCH);
        MockFlowFile flowFile = flowFiles.get(0);
        System.out.println(flowFile.getAttributes());
    }
}
