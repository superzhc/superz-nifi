package com.github.superzhc.nifi.processors.xml;

import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class SplitXmlByXPathTest {
    TestRunner runner;

    @BeforeEach
    public void setUp() {
        runner = TestRunners.newTestRunner(SplitXmlByXPath.class);
    }

    @Test
    public void smzdm_faxian_rss() throws Exception {
        runner.setProperty(SplitXmlByXPath.SPLIT_XPATH_EXPRESSION, "/rss/channel/item");

        Map<String, String> incomingAttributes = new HashMap<>();
        incomingAttributes.put("date", "Thu, 21 Sep 2023 05:48:25 GMT");
        incomingAttributes.put("content-type", "application/rss+xml");
        incomingAttributes.put("charset", "UTF-8");
        incomingAttributes.put("mime.type", "application/rss+xml");
        runner.enqueue(Paths.get(this.getClass().getResource("/smzdm_rss_faxian.xml").toURI()), incomingAttributes);

        runner.run();
        runner.assertQueueEmpty();

        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(SplitXmlByXPath.REL_SPLIT);
        MockFlowFile flowFile = flowFiles.get(1);
        System.out.println(flowFile.getAttributes());
        System.out.println(flowFile.getContent());
    }
}