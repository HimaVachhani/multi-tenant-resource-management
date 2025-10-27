package com.edstruments.multitenantresourcemanagement.config.multitenancy;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class HibernateConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private SchemaBasedMultiTenantConnectionProvider multiTenantConnectionProvider;

    @Autowired
    private CurrentTenantIdentifierResolver tenantIdentifierResolver;

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder) {
        Map<String, Object> props = new HashMap<>();

        // ✅ Use plain strings (works across Hibernate 6.x versions)
        props.put("hibernate.multiTenancy", "SCHEMA");
        props.put("hibernate.multi_tenant_connection_provider", multiTenantConnectionProvider);
        props.put("hibernate.tenant_identifier_resolver", tenantIdentifierResolver);
        props.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        props.put("hibernate.show_sql", true);
        props.put("hibernate.format_sql", true);

        return builder
                .dataSource(dataSource)
                .packages("com.edstruments.multitenantresourcemanagement.entity")
                .persistenceUnit("multiTenantPU")
                .properties(props)
                .build();
    }
}
