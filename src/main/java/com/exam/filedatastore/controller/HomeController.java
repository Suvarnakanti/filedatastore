package com.exam.filedatastore.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.exam.filedatastore.model.InputModel;
import com.exam.filedatastore.service.FileDataStoreService;

@Controller
public class HomeController {

	@Autowired
	private FileDataStoreService fileDataStoreService;

	@GetMapping("/")
	public String home(Map<String, Object> model) {
		model.put("message", "home");
		return "home";
	}

	@GetMapping("/create")
	public String create(Map<String, Object> model) {
		model.put("message", "create");
		return "home";
	}

	@PostMapping("/save")
	public String save(Map<String, Object> model, InputModel inputModel) {

		String output = fileDataStoreService.save(inputModel);
		model.put("message", "save");
		model.put("output", output);

		return "home";
	}

	@GetMapping("/read")
	public String read(Map<String, Object> model) {
		model.put("message", "read");
		return "home";
	}

	@PostMapping("/fetch")
	public String fetch(Map<String, Object> model, InputModel inputModel) {
		model.put("message", "fetch");
		String output = fileDataStoreService.fetch(inputModel);
		model.put("output", output);
		return "home";
	}

	@GetMapping("/delete")
	public String delete(Map<String, Object> model) {
		model.put("message", "delete");
		return "home";
	}

	@PostMapping("/deleted")
	public String deleted(Map<String, Object> model, InputModel inputModel) {
		model.put("message", "deleted");
		String output = fileDataStoreService.deleted(inputModel);
		model.put("output", output);
		return "home";
	}

}