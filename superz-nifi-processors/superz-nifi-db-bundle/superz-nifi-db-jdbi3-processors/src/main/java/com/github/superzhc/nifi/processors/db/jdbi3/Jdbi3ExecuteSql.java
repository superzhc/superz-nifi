package com.github.superzhc.nifi.processors.db.jdbi3;

import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.util.StandardValidators;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tags({"sql", "jdbi3"})
@CapabilityDescription("执行 SQL 语句")
public class Jdbi3ExecuteSql extends Jdbi3BaseProcessor {
    public static final PropertyDescriptor SQL = new PropertyDescriptor.Builder()
            .name("SQL")
            .description("Jdbi3 execute 不支持命名占位符")
            .required(true)
            .addValidator(StandardValidators.NON_BLANK_VALIDATOR)
            .build();

    @Override
    protected List<PropertyDescriptor> getCustomSupportedPropertyDescription() {
        return Arrays.asList(SQL);
    }

    @Override
    public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
        FlowFile flowFile = session.get();

        final String sql = context.getProperty(SQL).getValue();

//        Map<String, String> params = new HashMap<>();
//        for (Map.Entry<PropertyDescriptor, String> property : context.getProperties().entrySet()) {
//            if (!property.getKey().isDynamic()) {
//                continue;
//            }
//
//            params.put(property.getKey().getName(), property.getValue());
//        }
//
//        if (null != flowFile) {
//            Map<String, String> attributes = flowFile.getAttributes();
//            params.putAll(attributes);
//        }

        int result = getJdbi().withHandle(handle -> {
            return handle.execute(sql);
        });

        FlowFile newFlowFile = session.create(/*flowFile*/);
        session.putAttribute(newFlowFile, "result", String.valueOf(result));

        session.transfer(newFlowFile, SUCCESS);

        if (null != flowFile) {
            session.remove(flowFile);
        }
    }
}
