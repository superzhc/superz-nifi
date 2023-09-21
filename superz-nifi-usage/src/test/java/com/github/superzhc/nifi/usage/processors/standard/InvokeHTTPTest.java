package com.github.superzhc.nifi.usage.processors.standard;

import org.apache.nifi.processors.standard.InvokeHTTP;
import org.apache.nifi.processors.standard.http.HttpMethod;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class InvokeHTTPTest {

    TestRunner runner;

    @BeforeEach
    public void setUp() {
        runner = TestRunners.newTestRunner(new InvokeHTTP());
        runner.setNonLoopConnection(false);
    }

    @Test
    public void test() {
        runner.setProperty(InvokeHTTP.SOCKET_IDLE_CONNECTIONS, Integer.toString(0));
        runner.setProperty(InvokeHTTP.HTTP_METHOD, HttpMethod.GET.name());
        runner.setProperty(InvokeHTTP.HTTP_URL, "http://faxian.smzdm.com/feed");

        runner.run();

        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(InvokeHTTP.RESPONSE);
        System.out.println(flowFiles.size());
        for (MockFlowFile flowFile : flowFiles) {
            System.out.println(flowFile.getAttributes());
            System.out.println("=======================华丽分割线=====================");
            System.out.println(flowFile.getContent());
            System.out.println("\n\n\n");
        }
    }
}
