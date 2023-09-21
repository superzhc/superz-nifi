package com.github.superzhc.nifi.usage.flow;

import com.github.superzhc.nifi.processors.xml.SplitXmlByXPath;
import org.apache.nifi.processors.standard.EvaluateXPath;
import org.apache.nifi.processors.standard.InvokeHTTP;
import org.apache.nifi.processors.standard.http.HttpMethod;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.jupiter.api.Test;

import java.util.List;

public class SMZDM {

    @Test
    public void test() {
        TestRunner runner1 = TestRunners.newTestRunner(InvokeHTTP.class);
        runner1.setNonLoopConnection(false);
        runner1.setProperty(InvokeHTTP.SOCKET_IDLE_CONNECTIONS, Integer.toString(0));
        runner1.setProperty(InvokeHTTP.HTTP_METHOD, HttpMethod.GET.name());
        runner1.setProperty(InvokeHTTP.HTTP_URL, "http://faxian.smzdm.com/feed");
        runner1.run();
        List<MockFlowFile> flowFiles = runner1.getFlowFilesForRelationship(InvokeHTTP.RESPONSE);
        MockFlowFile flowFile = flowFiles.get(0);

        TestRunner runner2 = TestRunners.newTestRunner(SplitXmlByXPath.class);
        runner2.setProperty(SplitXmlByXPath.SPLIT_XPATH_EXPRESSION, "/rss/channel/item");
        runner2.enqueue(flowFile.getData(), flowFile.getAttributes());
        runner2.run();
        List<MockFlowFile> flowFiles2 = runner2.getFlowFilesForRelationship(SplitXmlByXPath.REL_SPLIT);

        for (MockFlowFile flowFile2 : flowFiles2) {
            TestRunner runner3 = TestRunners.newTestRunner(EvaluateXPath.class);
            runner3.setProperty(EvaluateXPath.DESTINATION, EvaluateXPath.DESTINATION_ATTRIBUTE);
            runner3.setProperty("title", "/item/guid");
            runner3.setProperty("url", "/item/title");
            runner3.setProperty("pubDate", "/item/pubDate");
            runner3.setProperty("link", "/item/link");
            runner3.setProperty("focus_pic", "/item/focus_pic");
            runner3.setProperty("description", "/item/description");
            runner3.setProperty("content", "/item/*[local-name()='encoded']/text()");
            runner3.enqueue(flowFile2.getData(), flowFile2.getAttributes());
            runner3.run();
            List<MockFlowFile> flowFiles3 = runner3.getFlowFilesForRelationship(EvaluateXPath.REL_MATCH);
            for (MockFlowFile flowFile3 : flowFiles3) {
                System.out.println(flowFile3.getAttributes());
            }
        }
    }
}
