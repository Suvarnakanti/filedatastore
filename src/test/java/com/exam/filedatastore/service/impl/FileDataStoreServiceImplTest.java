package com.exam.filedatastore.service.impl;

import org.junit.Test;

import com.exam.filedatastore.model.InputModel;

public class FileDataStoreServiceImplTest {

	FileDataStoreServiceImpl fileDataStoreServiceImpl;

	/**
	 * Test method for
	 * {@link com.exam.filedatastore.service.impl.FileDataStoreServiceImpl#save(com.exam.filedatastore.model.InputModel)}.
	 */
	@Test
	public void testSave() {
		fileDataStoreServiceImpl = new FileDataStoreServiceImpl();
		InputModel inputModel = new InputModel();
		inputModel.setValue("value1");
		fileDataStoreServiceImpl.save(inputModel);

		inputModel = new InputModel();
		inputModel.setFileName("file1");
		inputModel.setKey("key1");
		inputModel.setTimeToLive("600");
		inputModel.setValue("value1");
		fileDataStoreServiceImpl.save(inputModel);

	}

	/**
	 * Test method for
	 * {@link com.exam.filedatastore.service.impl.FileDataStoreServiceImpl#fetch(com.exam.filedatastore.model.InputModel)}.
	 */
	@Test
	public void testFetch() {
		fileDataStoreServiceImpl = new FileDataStoreServiceImpl();
		InputModel inputModel = new InputModel();
		inputModel.setFileName("file1");
		fileDataStoreServiceImpl.fetch(inputModel);

		inputModel = new InputModel();
		inputModel.setFileName("file1");
		inputModel.setKey("key1");
		fileDataStoreServiceImpl.fetch(inputModel);
	}

	/**
	 * Test method for
	 * {@link com.exam.filedatastore.service.impl.FileDataStoreServiceImpl#deleted(com.exam.filedatastore.model.InputModel)}.
	 */
	@Test
	public void testDeleted() {
		fileDataStoreServiceImpl = new FileDataStoreServiceImpl();
		InputModel inputModel = new InputModel();
		inputModel.setFileName("file1");
		fileDataStoreServiceImpl.deleted(inputModel);

		inputModel = new InputModel();
		inputModel.setFileName("file1");
		inputModel.setKey("key1");
		fileDataStoreServiceImpl.fetch(inputModel);
	}

}
