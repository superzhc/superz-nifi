package com.github.superzhc.nifi.processors.xml;

import org.apache.nifi.annotation.behavior.InputRequirement;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.components.Validator;
import org.apache.nifi.expression.ExpressionLanguageScope;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.flowfile.attributes.CoreAttributes;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.InputStream;
import java.util.*;

import static org.apache.nifi.flowfile.attributes.FragmentAttributes.*;

@InputRequirement(InputRequirement.Requirement.INPUT_REQUIRED)
@Tags({"xml", "xpath"})
@CapabilityDescription("通过 XPath 分割 XML")
public class SplitXmlByXPath extends AbstractProcessor {

    public static final PropertyDescriptor SPLIT_XPATH_EXPRESSION = new PropertyDescriptor.Builder()
            .name("XPath")
            .description("通过 XPath 表达式获取节点组，对获取的节点组进行分割")
            .required(true)
            .addValidator(Validator.VALID)
            .build();

    public static final Relationship REL_SPLIT = new Relationship.Builder()
            .name("split")
            .build();

    @Override
    protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        return Collections.unmodifiableList(Arrays.asList(SPLIT_XPATH_EXPRESSION));
    }

    @Override
    public Set<Relationship> getRelationships() {
        Set<Relationship> relationships = new HashSet<>();
        relationships.add(REL_SPLIT);
        return Collections.unmodifiableSet(relationships);
    }

    @Override
    protected PropertyDescriptor getSupportedDynamicPropertyDescriptor(String propertyDescriptorName) {
        return new PropertyDescriptor.Builder()
                .name(propertyDescriptorName)
                .expressionLanguageSupported(ExpressionLanguageScope.NONE)
                .required(false)
                .dynamic(true)
                .addValidator(Validator.VALID)
                .build();
    }

    @Override
    public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
        FlowFile ff = session.get();
        if (null == ff) {
            return;
        }

        try (InputStream in = session.read(ff)) {
            SAXReader reader = new SAXReader();
            Document doc = reader.read(in);

            // 获取公用属性读取的数据
            Map<String, String> commonElements = new HashMap<>();
            for (Map.Entry<PropertyDescriptor, String> property : context.getProperties().entrySet()) {
                if (!property.getKey().isDynamic()) {
                    continue;
                }

                String actualValue = doc.valueOf(property.getValue());
                commonElements.put(property.getKey().getName(), actualValue);
            }

            String xpathExpression = context.getProperty(SPLIT_XPATH_EXPRESSION).getValue();
            List<Node> nodes = doc.selectNodes(xpathExpression);
            if (null == nodes || nodes.size() == 0) {
                return;
            }

            final String fragmentIdentifier = UUID.randomUUID().toString();
            for (int i = 0, size = nodes.size(); i < size; i++) {
                final Node node = nodes.get(i);
                Document nodeDoc = node.getDocument();
                for (Map.Entry<String, String> commonElement : commonElements.entrySet()) {
                    nodeDoc.addElement(commonElement.getKey()).addText(commonElement.getValue());
                }

                FlowFile split = session.create(ff);
                session.write(split, out -> {
                    XMLWriter writer = new XMLWriter(out);
                    writer.write(node);
                    writer.close();
                });
                split = session.putAttribute(split, FRAGMENT_ID.key(), fragmentIdentifier);
                split = session.putAttribute(split, FRAGMENT_INDEX.key(), Integer.toString(i));
                split = session.putAttribute(split, SEGMENT_ORIGINAL_FILENAME.key(), split.getAttribute(CoreAttributes.FILENAME.key()));
                split = session.putAttribute(split, FRAGMENT_COUNT.key(), Integer.toString(size));
                session.transfer(split, REL_SPLIT);
            }

        } catch (Exception e) {
            throw new ProcessException(e);
        }
    }
}
