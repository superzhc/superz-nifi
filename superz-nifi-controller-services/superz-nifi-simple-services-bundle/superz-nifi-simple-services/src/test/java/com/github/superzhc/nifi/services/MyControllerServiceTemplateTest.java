package com.github.superzhc.nifi.services;

import org.apache.nifi.util.NoOpProcessor;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.jupiter.api.Test;

class MyControllerServiceTemplateTest {
    @Test
    public void test() throws Exception {
        MyControllerServiceTemplate service = new MyControllerServiceTemplate();

        TestRunner runner = TestRunners.newTestRunner(NoOpProcessor.class);
        // 添加 controller service
        runner.addControllerService(MyControllerServiceTemplate.class.getName(), service);
        // controller service 的属性设置
        runner.setProperty(service, MyControllerServiceTemplate.MY_PROPERTY, "");
        // controller service 设置成可用
        runner.enableControllerService(service);

        // 判断 controller service 是否验证通过
        runner.assertValid(service);
    }
}