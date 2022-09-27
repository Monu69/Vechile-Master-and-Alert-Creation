package com.elogist.vehicle_master_and_alert_creation.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.elogist.vehicle_master_and_alert_creation.repository.mssql",
        entityManagerFactoryRef = "mssqlEntityManager",
        transactionManagerRef = "mssqlTransactionManager"
)
public class PersistenceMSSQLConfiguration extends HikariConfig {

    @Autowired
    private Environment env;

//    @Value("${sqlserverx.datasource.url}")
//    private String gpsUrl;
//
//    @Value("${sqlserverx.datasource.username}")
//    String gpsUsername;
//
//    @Value("${sqlserverx.datasource.password}")
//    String gpsPassword;
//
//    @Value("${sqlserverx.datasource.driver-class-name}")
//    String gpsDriverClassName;
//
//    @Value("${sqlserverx.datasource.hikari.minimumIdle}")
//    int minimumIdle;
//
//    @Value("${sqlserverx.datasource.hikari.maximum-pool-size}")
//    int maxPoolSize;
//
//    @Value("${sqlserverx.datasource.schema}")
//    String gpsSchemma;

    @Bean
    @ConfigurationProperties(prefix = "spring.second-datasource")
    /* entityManagerFactoryRef */
    public LocalContainerEntityManagerFactoryBean mssqlEntityManager() {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(mssqlDataSource());
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


//    @Bean("vehicleM1M2DataSource")
//    public DataSource getDataSource() {
//        HikariConfig config=new HikariConfig();
//        config.setJdbcUrl(gpsUrl);
//        config.setUsername(gpsUsername);
//        config.setPassword(gpsPassword);
//        config.setDriverClassName(gpsDriverClassName);
//        config.setMinimumIdle(minimumIdle);
//        config.setMaximumPoolSize(maxPoolSize);
//
//        return new HikariDataSource(config);
//    }

    @Bean
    public DataSource mssqlDataSource() {
        DriverManagerDataSource dataSource
                = new DriverManagerDataSource();
        dataSource.setDriverClassName(
                env.getProperty("spring.second-datasource.driver-class-name"));
        dataSource.setUrl(env.getProperty("spring.second-datasource.url"));
        dataSource.setUsername(env.getProperty("spring.second-datasource.username"));
        dataSource.setPassword(env.getProperty("spring.second-datasource.password"));
        dataSource.setSchema(env.getProperty("spring.second-datasource.schema"));
        return  dataSource;
    }

//    @Bean("vehicleJDBCTemplate")
//    @Qualifier("vehicleM1M2DataSource")
//    @Autowired
//    public JdbcTemplate getGPSTemplate(DataSource dataSource)
//    {
//        return new JdbcTemplate(dataSource);
//    }
//
    @Bean
    /* transactionManagerRef */
    public PlatformTransactionManager mssqlTransactionManager() {

        JpaTransactionManager transactionManager
                = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(
                mssqlEntityManager().getObject());
        return transactionManager;
    }

}
