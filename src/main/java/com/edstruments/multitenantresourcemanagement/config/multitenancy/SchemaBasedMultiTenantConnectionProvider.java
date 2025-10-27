package com.edstruments.multitenantresourcemanagement.config.multitenancy;

import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Provides schema-based connections dynamically depending on the tenant.
 */
@Component
public class SchemaBasedMultiTenantConnectionProvider extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

    @Autowired
    private DataSource dataSource;

    @Override
    protected DataSource selectAnyDataSource() {
        return dataSource;
    }

    // ⚠️ Hibernate 6 uses Object, not String
    @Override
    protected DataSource selectDataSource(Object tenantIdentifier) {
        return dataSource;
    }

    @Override
    public Connection getConnection(Object tenantIdentifier) throws SQLException {
        Connection connection = dataSource.getConnection();
        if (tenantIdentifier != null) {
            try {
                connection.setSchema(tenantIdentifier.toString());
            } catch (SQLException ex) {
                // fallback to public if schema not found
                connection.setSchema("public");
            }
        }
        return connection;
    }

    @Override
    public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }
}
