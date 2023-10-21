package com.university.schedule.service;

import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DataSchemaService {

	private Flyway flyway;

	public void clean() {
		flyway.clean();
		flyway.migrate();
	}

	@Autowired
	private void setFlyway(Flyway flyway){
		this.flyway = flyway;
	}

}
