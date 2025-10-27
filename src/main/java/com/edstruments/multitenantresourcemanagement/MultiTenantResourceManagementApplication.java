package com.edstruments.multitenantresourcemanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
@SpringBootApplication
@EnableCaching
@EnableJpaAuditing
public class MultiTenantResourceManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(MultiTenantResourceManagementApplication.class, args);
	}

}

