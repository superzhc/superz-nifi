package com.github.superzhc.nifi.services.jdbi3;

import com.github.superzhc.nifi.services.api.Jdbi3Service;
import org.apache.nifi.annotation.lifecycle.OnEnabled;
import org.apache.nifi.components.AllowableValue;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.components.Validator;
import org.apache.nifi.controller.AbstractControllerService;
import org.apache.nifi.controller.ConfigurationContext;
import org.apache.nifi.processor.util.StandardValidators;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Jdbi3 extends AbstractControllerService implements Jdbi3Service {

    /* 支持的数据库 */
    public static final AllowableValue DRIVER_MYSQL5 = new AllowableValue("com.mysql.jdbc.Driver", "MySQL5");
    public static final AllowableValue DRIVER_SQLITE3 = new AllowableValue("org.sqlite.JDBC", "SQLite3");
    public static final AllowableValue DRIVER_POSTGRES = new AllowableValue("org.postgresql.Driver", "Postgres");
    public static final PropertyDescriptor DRIVER = new PropertyDescriptor.Builder()
            .name("数据库类型")
            .description("数据库类型")
            .required(true)
            .allowableValues(DRIVER_MYSQL5, DRIVER_POSTGRES, DRIVER_SQLITE3)
            .build();

    public static final PropertyDescriptor URL = new PropertyDescriptor.Builder()
            .name("连接地址")
            .description("示例：jdbc:mysql://127.0.0.1:3306/test?useSSL=false")
            .required(true)
            .addValidator(StandardValidators.NON_BLANK_VALIDATOR)
            .build();

    public static final PropertyDescriptor USERNAME = new PropertyDescriptor.Builder()
            .name("用户名")
            .addValidator(Validator.VALID)
            .build();

    public static final PropertyDescriptor PASSWORD = new PropertyDescriptor.Builder()
            .name("密码")
            .addValidator(Validator.VALID)
            .build();

    private volatile Jdbi jdbi;

    @Override
    protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        final List<PropertyDescriptor> properties = new ArrayList<>(2);
        properties.add(DRIVER);
        properties.add(URL);
        properties.add(USERNAME);
        properties.add(PASSWORD);
        return properties;
    }

    @OnEnabled
    public void onConfigured(final ConfigurationContext context) {
        // String driverName = context.getProperty(DRIVER).getValue();
        String url = context.getProperty(URL).getValue();
        String username = context.getProperty(USERNAME).getValue();
        String password = context.getProperty(PASSWORD).getValue();

        Properties properties = new Properties();
        if (null != username && username.trim().length() > 0) {
            properties.put("user", username);

            if (null != password && password.trim().length() > 0) {
                properties.put("password", password);
            }
        }
        jdbi = Jdbi.create(url, properties);

        // 插件支持操作
        jdbi.installPlugin(new SqlObjectPlugin());
    }

    @Override
    public Jdbi getJdbi() {
        return jdbi;
    }
}
