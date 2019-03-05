package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.domain.Execution;
import com.example.exception.APIException;
import com.example.service.ExecutionService;

@Component
@RestController
public class ExecutionController {

	@Autowired
	ExecutionService execService;
	
	@PostMapping(value="/Exec",  produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Execution> addOrder(@RequestBody Execution exec) throws APIException {
		
		Execution createdExec = execService.createExecution(exec);
		return new ResponseEntity<Execution>(createdExec, HttpStatus.OK);
		
	}
}
