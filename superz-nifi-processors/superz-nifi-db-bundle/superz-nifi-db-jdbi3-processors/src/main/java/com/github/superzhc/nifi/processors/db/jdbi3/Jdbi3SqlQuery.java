package com.github.superzhc.nifi.processors.db.jdbi3;

import com.github.superzhc.nifi.services.api.Jdbi3Service;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.flowfile.attributes.CoreAttributes;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.util.StandardValidators;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 使用 jdbi3 模板机制来进行数据库操作
 */
@Tags({"sql", "jdbi3"})
public class Jdbi3SqlQuery extends Jdbi3BaseProcessor {
    public static final PropertyDescriptor SQL = new PropertyDescriptor.Builder()
            .name("查询 SQL")
            .description("支持命名占位符，示例：select * from test where id=:id")
            .required(true)
            .addValidator(StandardValidators.NON_BLANK_VALIDATOR)
            .build();

    @Override
    protected List<PropertyDescriptor> getCustomSupportedPropertyDescription() {
        return Arrays.asList(SQL);
    }

    @Override
    public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
        // 1. 数据类型只有字符串类型，需修复
        // 2. 动态属性 el 表达式支持操作，查询操作可以不需要上游的 FlowFile 的
        // 3. 命名占位符的值是列表需要处理
        final Map<String, String> params = new HashMap<>();
        for (Map.Entry<PropertyDescriptor, String> property : context.getProperties().entrySet()) {
            if (!property.getKey().isDynamic()) {
                continue;
            }

            params.put(property.getKey().getName(), property.getValue());
        }

        FlowFile flowFile = session.get();
        if (null != flowFile) {
            Map<String, String> attributes = flowFile.getAttributes();
            params.putAll(attributes);
        }

        // 4. FlowFile 的内容是否需要进行解析？

        final String sql = context.getProperty(SQL).getValue();

        FlowFile newFlowFile = session.create();
        try (Handle handle = getJdbi().open()) {
            List<Map<String, Object>> data = handle.createQuery(sql)
                    .bindMap(params)
                    .mapToMap()
                    .list();

            final String content = MAPPER.writeValueAsString(data);
            session.write(newFlowFile, output -> {
                output.write(content.getBytes("UTF-8"));
                output.flush();
            });
            session.putAttribute(newFlowFile, CoreAttributes.MIME_TYPE.key(), "application/json");
            session.transfer(newFlowFile, SUCCESS);
        } catch (Exception e) {
            session.putAttribute(newFlowFile, "message", e.getMessage());
            session.transfer(newFlowFile, FAILED);
        }
    }
}
