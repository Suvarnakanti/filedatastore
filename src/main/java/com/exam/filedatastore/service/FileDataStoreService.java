package com.exam.filedatastore.service;

import com.exam.filedatastore.model.InputModel;

public interface FileDataStoreService {

	String save(InputModel inputModel);

	String fetch(InputModel inputModel);

	String deleted(InputModel inputModel);

}
