package com.github.superzhc.nifi.services;

import com.github.superzhc.nifi.services.api.IMyControllerServiceTemplate;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.annotation.lifecycle.OnDisabled;
import org.apache.nifi.annotation.lifecycle.OnEnabled;
import org.apache.nifi.annotation.lifecycle.OnShutdown;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.components.Validator;
import org.apache.nifi.controller.AbstractControllerService;
import org.apache.nifi.controller.ConfigurationContext;
import org.apache.nifi.controller.ControllerServiceInitializationContext;
import org.apache.nifi.reporting.InitializationException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@Tags({"test"})
@CapabilityDescription("测试自定义服务")
public class MyControllerServiceTemplate extends AbstractControllerService implements IMyControllerServiceTemplate {
    /* 定义属性 */
    public static final PropertyDescriptor MY_PROPERTY = new PropertyDescriptor.Builder()
            .name("customProperty")
            .description("自定义属性")
            .required(true)
            .addValidator(Validator.INVALID)
            .defaultValue("1")
            .build();

    @Override
    protected void init(ControllerServiceInitializationContext config) throws InitializationException {
    }

    @Override
    protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        return Collections.unmodifiableList(Arrays.asList(MY_PROPERTY));
    }

    @OnEnabled
    public void onConfigured(final ConfigurationContext context) {
    }

    @OnShutdown
    @OnDisabled
    public void cleanup() {
    }
}
