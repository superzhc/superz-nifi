package com.github.superzhc.nifi.processors.db.jdbi3;

import org.apache.nifi.annotation.documentation.CapabilityDescription;
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

@Tags({"sql", "jdbi3"})
@CapabilityDescription("查询表数据")
public class Jdbi3QueryTable extends Jdbi3BaseProcessor {

    private static final String SQL_TEMPLATE = "SELECT * FROM <table> WHERE 1=1 <condition>";

    public static final PropertyDescriptor TABLE = new PropertyDescriptor.Builder()
            .name("表名")
            .required(true)
            .addValidator(StandardValidators.NON_BLANK_VALIDATOR)
            .build();

    // TODO：可配置成查询表数据行数
    // Q：各种数据库类型的取条数的语法不同，如何配置

    @Override
    protected List<PropertyDescriptor> getCustomSupportedPropertyDescription() {
        return Arrays.asList(TABLE);
    }

    @Override
    public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
        String table = context.getProperty(TABLE).getValue();

        FlowFile ff = session.create();

        try (Handle handle = getJdbi().open()) {
            List<Map<String, Object>> data = handle.createQuery(SQL_TEMPLATE)
                    .define("table", table)
                    .define("condition", "")
                    .mapToMap()
                    .list();

            final String content = MAPPER.writeValueAsString(data);
            session.write(ff, output -> {
                output.write(content.getBytes("UTF-8"));
                output.flush();
            });
            session.putAttribute(ff, CoreAttributes.MIME_TYPE.key(), "application/json");
            session.transfer(ff, SUCCESS);
        } catch (Exception e) {
            session.putAttribute(ff, "message", "查询失败：" + e.getMessage());
            session.transfer(ff, FAILED);
        }
    }
}
