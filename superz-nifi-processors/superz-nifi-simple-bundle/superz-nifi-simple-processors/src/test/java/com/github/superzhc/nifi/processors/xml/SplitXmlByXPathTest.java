package com.github.superzhc.nifi.processors.xml;

import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SplitXmlByXPathTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void test() throws Exception {
        TestRunner runner = TestRunners.newTestRunner(SplitXmlByXPath.class);
        runner.setProperty(SplitXmlByXPath.SPLIT_XPATH_EXPRESSION, "/rss/channel/item");
        runner.enqueue(new File("D:\\downloads\\chrome\\daecfd2b-6449-425a-af7b-625a7698a22b").toPath());
        runner.run();

        List<MockFlowFile> mockFlowFiles = runner.getFlowFilesForRelationship(SplitXmlByXPath.REL_SPLIT);
        MockFlowFile mockFlowFile = mockFlowFiles.get(0);
        String content = new String(runner.getContentAsByteArray(mockFlowFile));
        System.out.println(content);
    }
}