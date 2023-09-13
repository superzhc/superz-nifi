package com.github.superzhc.nifi.processors.db.jdbi3;

import com.github.superzhc.nifi.services.api.Jdbi3Service;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.util.StandardValidators;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.PreparedBatch;

import java.util.Arrays;
import java.util.List;

public class Jdbi3SqlBatch extends Jdbi3BaseProcessor {
    public static final PropertyDescriptor SQL = new PropertyDescriptor.Builder()
            .name("DML SQL")
            .description("支持命名占位符，示例：insert into test(name) values(:name)")
            .required(true)
            .addValidator(StandardValidators.NON_BLANK_VALIDATOR)
            .build();

    public static final PropertyDescriptor FETCH_SIZE = new PropertyDescriptor.Builder()
            .name("Size")
            .description("单次获取流文件数量")
            .addValidator(StandardValidators.POSITIVE_INTEGER_VALIDATOR)
            .defaultValue("50")
            .build();

    @Override
    protected List<PropertyDescriptor> getCustomSupportedPropertyDescription() {
        return Arrays.asList(SQL, FETCH_SIZE);
    }

    @Override
    public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
        int fetchSize = context.getProperty(FETCH_SIZE).asInteger();
        List<FlowFile> flowFiles = session.get(fetchSize);
        if (null == flowFiles || flowFiles.size() == 0) {
            return;
        }

        // 数据类型只有字符串类型，需修复
//        final Map<String, String> params = new HashMap<>();
//
//        Map<String, String> attributes = flowFile.getAttributes();
//        params.putAll(attributes);
//
//        for (Map.Entry<PropertyDescriptor, String> property : context.getProperties().entrySet()) {
//            if (!property.getKey().isDynamic()) {
//                continue;
//            }
//
//            params.put(property.getKey().getName(), property.getValue());
//        }

        Jdbi3Service jdbi3Service = context.getProperty(JDBI_SERVICE).asControllerService(Jdbi3Service.class);
        Jdbi jdbi = jdbi3Service.getJdbi();

        final String sql = context.getProperty(SQL).getValue();

        jdbi.useHandle(handle -> {
//            Batch batch = handle.createBatch();
//            // batch add 是每一条 sql
//            batch.execute();

            PreparedBatch batch = handle.prepareBatch(sql);
//            batch.bindMap(params).add();
            batch.execute();
        });
    }
}