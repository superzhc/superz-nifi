package com.github.superzhc.nifi.processors;

import com.github.superzhc.nifi.services.api.IMyControllerServiceTemplate;
import org.apache.nifi.annotation.behavior.WritesAttribute;
import org.apache.nifi.annotation.behavior.WritesAttributes;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.annotation.lifecycle.OnAdded;
import org.apache.nifi.annotation.lifecycle.OnRemoved;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.annotation.lifecycle.OnUnscheduled;
import org.apache.nifi.components.AllowableValue;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.components.ValidationContext;
import org.apache.nifi.components.ValidationResult;
import org.apache.nifi.expression.ExpressionLanguageScope;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.*;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.io.InputStreamCallback;
import org.apache.nifi.processor.io.OutputStreamCallback;
import org.apache.nifi.processor.io.StreamCallback;
import org.apache.nifi.processor.util.StandardValidators;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * @author superz
 * @create 2023/5/15 14:25
 **/
//// 不需要关注上下文
//@SideEffectFree
//// 支持批量
//@SupportsBatching
// 用于标记这个Processor的标签、关键字，可以用于搜索
@Tags("sueprz,demo")
//// 声明允许输入
//@InputRequirement(InputRequirement.Requirement.INPUT_ALLOWED)
// 定义输出属性
@WritesAttributes({
        @WritesAttribute(attribute = "", description = "")
        //...
})
// Processor详细描述
@CapabilityDescription("我的第一个自定义Processor")
public class MyProcessorTemplate extends AbstractProcessor {

    // region===================================Property===============================================================

    /*自定义Processor属性*/
    public static final PropertyDescriptor MY_PROPERTY = new PropertyDescriptor.Builder()
            .name("文本值")
            .description("文本输入")
            .expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES)
            .required(true)
            .defaultValue("")
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)// 必须要加该参数，不然processor用不了
            .build();

    /* 自定义属性-下拉框选择 */
    // 属性选项值定义
    public static final AllowableValue V1 = new AllowableValue("0", "v1", "option value:value1");
    public static final AllowableValue V2 = new AllowableValue("1", "v2", "option value:value2");
    // 配置选项值
    public static final PropertyDescriptor MY_OPTION_PROPERTY = new PropertyDescriptor.Builder()
            .name("下拉选择")
            .description("下拉框选择值")
            .required(true)
            .allowableValues(V1, V2)
            .defaultValue(V1.getValue())
            .build();

    /* 服务类型属性 */
    // 自定义服务类型属性
    public static final PropertyDescriptor MY_CUSTOM_SERVICE_PROPERTY = new PropertyDescriptor.Builder()
            .name("custom service")
            .description("自定义服务")
            .required(true)
            .identifiesControllerService(IMyControllerServiceTemplate.class)
            .build();
    // endregion================================Property===============================================================

    // region===================================Relationship===========================================================

    /*自定义Processor关系*/
    public static final Relationship SUCCESS = new Relationship.Builder()
            .name("success")
            .description("")
            .build();

    // endregion================================Relationship===========================================================


    private List<PropertyDescriptor> descriptors;
    private Set<Relationship> relationships;

    @Override
    protected void init(ProcessorInitializationContext context) {
        final List<PropertyDescriptor> descriptors = new ArrayList<>();
        descriptors.add(MY_PROPERTY);
        descriptors.add(MY_OPTION_PROPERTY);
        this.descriptors = Collections.unmodifiableList(descriptors);

        final Set<Relationship> relationships = new HashSet<>();
        relationships.add(SUCCESS);
        this.relationships = Collections.unmodifiableSet(relationships);
    }

    /**
     * Processor 支持的属性
     *
     * @return
     */
    @Override
    protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        return descriptors;
    }

    @Override
    public Set<Relationship> getRelationships() {
        return relationships;
    }

    /**
     * 动态属性
     *
     * @param propertyDescriptorName used to lookup if any property descriptors exist for that name
     * @return
     */
    @Override
    protected PropertyDescriptor getSupportedDynamicPropertyDescriptor(String propertyDescriptorName) {
        return new PropertyDescriptor.Builder()
                .name(propertyDescriptorName)
                .expressionLanguageSupported(ExpressionLanguageScope.NONE)
                // 属性验证
                // .addValidator(new XPathValidator())
                .required(false)
                .dynamic(true)
                .build();
    }

    /**
     * 用户自定义验证规则
     *
     * @param validationContext provides a mechanism for obtaining externally
     *                          managed values, such as property values and supplies convenience methods
     *                          for operating on those values
     * @return
     */
    @Override
    protected Collection<ValidationResult> customValidate(ValidationContext validationContext) {
        final List<ValidationResult> results = new ArrayList<>(super.customValidate(validationContext));

        // 添加验证规则
        // results.add(new ValidationResult.Builder().subject("XPaths").valid(false).explanation("Exactly one XPath must be set if using destination of " + DESTINATION_CONTENT).build());

        return results;
    }

    @OnAdded
    public void addProcessor() {
    }

    @OnRemoved
    public void removeProcessor() {
    }

    /**
     * 主要是用于Processor的一些一次性工作，比如初始化连接等。所以，用户应该将资源的初始化工作放在@onScheduled注解修饰的方法中
     */
    @OnScheduled
    public void setUp() {
        // System.out.printf("当前Processor触发运行时间：%s", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
    }

    @OnUnscheduled
    public void tearDown() {
        // System.out.printf("当前Processor运行结束时间：%s", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
    }

    @Override
    public void onTrigger(ProcessContext processContext, ProcessSession processSession) throws ProcessException {
        FlowFile flowFile = processSession.get();
        if (null == flowFile) {
            return;
        }

        // 获取指定属性值
        processContext.getProperty(MY_PROPERTY).getValue();

        // 获取定义的服务
        IMyControllerServiceTemplate myControllerServiceTemplate = processContext.getProperty(MY_CUSTOM_SERVICE_PROPERTY).asControllerService(IMyControllerServiceTemplate.class);

        // 获取动态属性
        for (final Map.Entry<PropertyDescriptor, String> entry : processContext.getProperties().entrySet()) {
            if (!entry.getKey().isDynamic()) {
                continue;
            }

            // do something
        }

        // 新增或更新属性
        flowFile = processSession.putAttribute(flowFile, "k1", "v1");

        /* 读取数据 */
        // 方式一：
        try (InputStream in = processSession.read(flowFile)) {

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 方式二：
        processSession.read(flowFile, new InputStreamCallback() {
            @Override
            public void process(InputStream in) throws IOException {
                // do something
            }
        });

        /* 写数据 */
        FlowFile newFlowFile = processSession.write(flowFile, new OutputStreamCallback() {
            @Override
            public void process(OutputStream out) throws IOException {
                // do something
            }
        });

        /* 读后就写 */
        FlowFile resultingFlowFile = processSession.write(flowFile, new StreamCallback() {
            @Override
            public void process(InputStream in, OutputStream out) throws IOException {
                // do something
            }
        });

        // 直接将原来的数据给转发出去
        processSession.transfer(newFlowFile, SUCCESS);

        /* 删除原始FlowFile */
        processSession.remove(flowFile);
    }

    // region===================================FlowFile===============================================================
    /* 复制 FlowFile */
    public void clone(ProcessSession session, FlowFile flowFile) {
        FlowFile cloneFlowFile = session.clone(flowFile);
    }
    // endregion================================FlowFile===============================================================

    // region===================================State==================================================================
    // endregion================================State==================================================================
}
