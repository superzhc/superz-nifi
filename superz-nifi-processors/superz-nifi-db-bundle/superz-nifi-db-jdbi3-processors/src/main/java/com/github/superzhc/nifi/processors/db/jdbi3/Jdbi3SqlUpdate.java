package com.github.superzhc.nifi.processors.db.jdbi3;

import com.github.superzhc.nifi.services.api.Jdbi3Service;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.util.StandardValidators;
import org.jdbi.v3.core.Jdbi;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Jdbi3SqlUpdate extends Jdbi3BaseProcessor {

    public static final PropertyDescriptor SQL = new PropertyDescriptor.Builder()
            .name("DML SQL")
            .description("支持命名占位符，示例：insert into test(name) values(:name)")
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
        if (null == flowFile) {
            return;
        }

        // 数据类型只有字符串类型，需修复
        final Map<String, String> params = new HashMap<>();

        Map<String, String> attributes = flowFile.getAttributes();
        params.putAll(attributes);

        for (Map.Entry<PropertyDescriptor, String> property : context.getProperties().entrySet()) {
            if (!property.getKey().isDynamic()) {
                continue;
            }

            String value = context.getProperty(property.getKey()).evaluateAttributeExpressions(flowFile).getValue();

            params.put(property.getKey().getName(), value);
        }

        Jdbi3Service jdbi3Service = context.getProperty(JDBI_SERVICE).asControllerService(Jdbi3Service.class);
        Jdbi jdbi = jdbi3Service.getJdbi();

        final String sql = context.getProperty(SQL).getValue();

        jdbi.useHandle(handle -> {
            handle.createUpdate(sql)
                    .bindMap(params)
                    .execute()
            ;
        });
    }
}
