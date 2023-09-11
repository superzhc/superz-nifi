package com.github.superzhc.nifi.processors;

import org.apache.nifi.components.state.Scope;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.ProcessorInitializationContext;
import org.apache.nifi.processor.exception.ProcessException;

import java.io.IOException;
import java.util.HashMap;

public class MyProcessorWithStateTemplate extends AbstractProcessor {
    private static final String STATE_NAME = "key";

    @Override
    protected void init(ProcessorInitializationContext context) {
        super.init(context);
    }

    @Override
    public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
        try {
            // 设置状态
            context.getStateManager().setState(new HashMap<>(),Scope.LOCAL);

            // 查询状态
            context.getStateManager().getState(Scope.LOCAL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
