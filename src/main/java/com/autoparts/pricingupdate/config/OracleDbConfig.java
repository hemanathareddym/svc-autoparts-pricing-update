package com.autoparts.pricingupdate.config;

import java.util.HashMap;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@PropertySource({"classpath:db.properties"})
@EnableJpaRepositories(basePackages = "com.autoparts.pricingupdate.repository.ora", entityManagerFactoryRef = "oraEntityManager", transactionManagerRef = "oraTransactionManager")
@Profile("!tc")
public class OracleDbConfig {

    @Autowired
    private Environment env;

    public OracleDbConfig() {
        super();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean oraEntityManager() {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(oraDataSource());
        em.setPackagesToScan("com.autoparts.pricingupdate.model.ora");

        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        final HashMap<String, Object> properties = new HashMap<String, Object>();
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
        properties.put("hibernate.dialect", env.getProperty("spring.ora-datasource.database-platform"));
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean
    @ConfigurationProperties(prefix="spring.ora-datasource")
    public DataSource oraDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource("jdbc:oracle:thin:@localhost:1521:ORCL", "SYSTEM", "Adarsh1!");
        dataSource.setDriverClassName("oracle.jdbc.OracleDriver");
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager oraTransactionManager() {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(oraEntityManager().getObject());
        return transactionManager;
    }

}
