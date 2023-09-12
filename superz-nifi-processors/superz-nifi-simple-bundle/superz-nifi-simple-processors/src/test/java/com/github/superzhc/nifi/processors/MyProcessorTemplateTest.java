package com.github.superzhc.nifi.processors;

import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MyProcessorTemplateTest {

    @Test
    public void test() {
        TestRunner runner = TestRunners.newTestRunner(MyProcessorTemplate.class);
        // 设置属性
        runner.setProperty(MyProcessorTemplate.MY_PROPERTY, "");

        // 设置 FlowFile
        runner.enqueue("");

        // 运行 Processor
        runner.run();

        // 获取运行后对应关系输出的FlowFile
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(MyProcessorTemplate.SUCCESS);
    }

}