package com.github.superzhc.nifi.processors.db.jdbi3;

import com.github.superzhc.nifi.services.api.Jdbi3Service;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.components.Validator;
import org.apache.nifi.expression.ExpressionLanguageScope;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.Relationship;

import java.util.*;

public abstract class Jdbi3BaseProcessor extends AbstractProcessor {
    public static final PropertyDescriptor JDBI_SERVICE = new PropertyDescriptor.Builder()
            .name("Jdbi3 实例")
            .required(true)
            .identifiesControllerService(Jdbi3Service.class)
            .build();

    public static final Relationship SUCCESS = new Relationship.Builder()
            .name("成功")
            .build();

    public static final Relationship FAILED = new Relationship.Builder()
            .name("失败")
            .build();

    @Override
    protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        List<PropertyDescriptor> propertyDescriptors = new ArrayList<>();
        propertyDescriptors.add(JDBI_SERVICE);

        List<PropertyDescriptor> customPropertyDescriptors = getCustomSupportedPropertyDescription();
        if (null != customPropertyDescriptors && customPropertyDescriptors.size() > 0) {
            propertyDescriptors.addAll(customPropertyDescriptors);
        }

        return Collections.unmodifiableList(propertyDescriptors);
    }

    protected List<PropertyDescriptor> getCustomSupportedPropertyDescription() {
        return null;
    }

    /**
     * 对于 Jdbi3 的服务来说，考虑到该组件提供的命名占位符，此处保证一定支持动态属性
     *
     * @param propertyDescriptorName used to lookup if any property descriptors exist for that name
     * @return
     */
    @Override
    final protected PropertyDescriptor getSupportedDynamicPropertyDescriptor(String propertyDescriptorName) {
        return new PropertyDescriptor.Builder()
                .name(propertyDescriptorName)
                .expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES)
                .addValidator(Validator.VALID)
                .dynamic(true)
                .build();
    }

    @Override
    public Set<Relationship> getRelationships() {
        Set<Relationship> relationships = new HashSet<>();
        relationships.add(SUCCESS);
        relationships.add(FAILED);

        List<Relationship> customRelationships = getCustomRelationships();
        if (null != customRelationships && customRelationships.size() > 0) {
            relationships.addAll(customRelationships);
        }

        return Collections.unmodifiableSet(relationships);
    }

    public List<Relationship> getCustomRelationships() {
        return null;
    }
}
