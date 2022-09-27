package com.elogist.vehicle_master_and_alert_creation.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
/* specifies the path for properties */
//@PropertySource({"classpath:application.properties"})
/* basePackage : repository package
 * entityManagerFactory : pgsql's entity manager function name
 * transactionManager : pgsql's transaction manager function name */
@EnableJpaRepositories(
        basePackages = "com.elogist.vehicle_master_and_alert_creation.repository.postgresql",
        entityManagerFactoryRef = "pgsqlEntityManager",
        transactionManagerRef = "pgsqlTransactionManager"
)
public class PersistencePGSQLConfiguration {

    @Autowired
    private Environment env;

    @Primary
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    /* entityManagerFactoryRef */
    public LocalContainerEntityManagerFactoryBean pgsqlEntityManager() {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(pgsqlDataSource());
        em.setPackagesToScan(
                new String[] { "com.elogist.vehicle_master_and_alert_creation.models" });

        HibernateJpaVendorAdapter vendorAdapter
                = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto",
                env.getProperty("hibernate.hbm2ddl.auto"));
        properties.put("hibernate.dialect",
                env.getProperty("hibernate.dialect"));
        em.setJpaPropertyMap(properties);
        return em;
    }

    @Primary
    @Bean
    public DataSource pgsqlDataSource() {
        DriverManagerDataSource dataSource
                = new DriverManagerDataSource();
        dataSource.setDriverClassName(
                env.getProperty("spring.datasource.driver-class-name"));
        dataSource.setUrl(env.getProperty("spring.datasource.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.password"));

        return dataSource;
    }

    @Primary
    @Bean
    /* transactionManagerRef */
    public PlatformTransactionManager pgsqlTransactionManager() {

        JpaTransactionManager transactionManager
                = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(
                pgsqlEntityManager().getObject());
        return transactionManager;
    }

}
