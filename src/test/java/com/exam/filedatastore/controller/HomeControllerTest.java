package com.exam.filedatastore.controller;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.exam.filedatastore.model.InputModel;
import com.exam.filedatastore.service.FileDataStoreService;

@RunWith(MockitoJUnitRunner.class)
public class HomeControllerTest {

	@Mock
	private FileDataStoreService fileDataStoreService;

	@InjectMocks
	private HomeController homeController;

	/**
	 * Test method for
	 * {@link com.exam.filedatastore.controller.HomeController#home(java.util.Map)}.
	 */
	@Test
	public void testHome() {
		Map<String, Object> model = new HashMap<>();
		model.put("message", "home");
		homeController.home(model);
	}

	/**
	 * Test method for
	 * {@link com.exam.filedatastore.controller.HomeController#create(java.util.Map)}.
	 */
	@Test
	public void testCreate() {
		Map<String, Object> model = new HashMap<>();
		model.put("message", "create");
		homeController.create(model);
	}

	/**
	 * Test method for
	 * {@link com.exam.filedatastore.controller.HomeController#save(java.util.Map, com.exam.filedatastore.model.InputModel)}.
	 */
	@Test
	public void testSave() {
		InputModel inputModel = new InputModel();
		Map<String, Object> model = new HashMap<>();
		inputModel.setValue("value1");
		model.put("message", "save");
		model.put("output", "output");
		Mockito.when(fileDataStoreService.save(inputModel)).thenReturn("Saved");
		homeController.save(model, inputModel);
	}

	/**
	 * Test method for
	 * {@link com.exam.filedatastore.controller.HomeController#read(java.util.Map)}.
	 */
	@Test
	public void testRead() {
		Map<String, Object> model = new HashMap<>();
		model.put("message", "read");
		homeController.read(model);
	}

	/**
	 * Test method for
	 * {@link com.exam.filedatastore.controller.HomeController#fetch(java.util.Map, com.exam.filedatastore.model.InputModel)}.
	 */
	@Test
	public void testFetch() {
		InputModel inputModel = new InputModel();
		Map<String, Object> model = new HashMap<>();
		inputModel.setFileName("file1");
		model.put("message", "fetch");
		model.put("output", "output");
		Mockito.when(fileDataStoreService.fetch(inputModel)).thenReturn("fetch");
		homeController.fetch(model, inputModel);
	}

	/**
	 * Test method for
	 * {@link com.exam.filedatastore.controller.HomeController#delete(java.util.Map)}.
	 */
	@Test
	public void testDelete() {
		Map<String, Object> model = new HashMap<>();
		model.put("message", "delete");
		homeController.delete(model);
	}

	/**
	 * Test method for
	 * {@link com.exam.filedatastore.controller.HomeController#deleted(java.util.Map, com.exam.filedatastore.model.InputModel)}.
	 */
	@Test
	public void testDeleted() {
		InputModel inputModel = new InputModel();
		Map<String, Object> model = new HashMap<>();
		inputModel.setFileName("file1");
		model.put("message", "delete");
		model.put("output", "output");
		Mockito.when(fileDataStoreService.deleted(inputModel)).thenReturn("Deleted");
		homeController.deleted(model, inputModel);
	}

}
