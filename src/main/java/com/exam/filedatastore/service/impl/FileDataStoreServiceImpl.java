package com.exam.filedatastore.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.exam.filedatastore.model.InputModel;
import com.exam.filedatastore.service.FileDataStoreService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Service
public class FileDataStoreServiceImpl implements FileDataStoreService {

	private final Logger LOGGER = LoggerFactory.getLogger(FileDataStoreServiceImpl.class);

	String filePath = "";
	Set<String> fileNames = null;
	Map<String, String> fileValues = new ConcurrentHashMap<String, String>();

	@Value("${spring.application.name}")
	private String appName;

	@Value("${size.validation}")
	private String sizeValidationMessage;

	@Value("${key.validation}")
	private String keyValidationMessage;

	@Value("${file.validation}")
	private String fileValidationMessage;

	@Value("${key.notpresent.validation}")
	private String keyNotPresentMessage;

	@Value("${key.deleted}")
	private String keyDeletedMessage;

	@Value("${default.timetolive}")
	private int defaultTimetolive;

	@Value("${file.size.check}")
	private long fileSizeCheck;

	{
		filePath = StringUtils.remove(System.getProperty("user.dir"), appName) + File.separator + "uploadedFiles";
		LOGGER.info("File Path ==>> " + filePath);
		File file = new File(filePath);
		if (!file.exists()) {
			if (file.mkdir()) {
				LOGGER.info("Directory is created!");
			} else {
				LOGGER.info("Failed to create directory!");
			}
		} else {
			LOGGER.info("Directory already exits!");
		}
		List<String> tempFileNames = Arrays.asList(file.list());
		fileNames = new HashSet<>(tempFileNames);
		LOGGER.info("File Names present in the Directory ==>> " + fileNames);
	}

	@Override
	public String save(InputModel inputModel) {

		LOGGER.info("Request Coming in Save From User ==>> " + inputModel);
		boolean result = false;
		String output = null;
		Map<String, String> fileContentInKeyValuePair = new LinkedHashMap<String, String>();
		if (Objects.nonNull(inputModel)) {

			// Time To Live Checking
			String timeToLive = null;
			Instant instant = Instant.now();
			long timeStampSeconds = instant.getEpochSecond();
			if (StringUtils.isNotBlank(inputModel.getTimeToLive())) {
				timeToLive = String.valueOf(timeStampSeconds + Long.valueOf(inputModel.getTimeToLive()));
			} else {
				timeToLive = String.valueOf(timeStampSeconds + defaultTimetolive); // In Minutes
			}
			LOGGER.info("timeToLive ==>> " + timeToLive);

			// Check User provides Key or Not
			String key = null;
			if (StringUtils.isNotBlank(inputModel.getKey())) {
				// Check Key exists or not
				key = inputModel.getKey() + "-" + timeToLive;
			} else {
				key = UUID.randomUUID().toString();
				key = key.replaceAll("-", "");
				key = key.substring(0, 20);
				LOGGER.info("Generate Key ==>> " + key);
				key = key + "-" + timeToLive;
			}
			LOGGER.info("Key with timeToLive ==>> " + key);

			// Check FileName Present or Not
			String fileName = null;
			if (StringUtils.isNotBlank(inputModel.getFileName())) {
				fileName = inputModel.getFileName();
			} else {
				fileName = UUID.randomUUID().toString();
				fileName = fileName.replaceAll("-", "");
				fileName = fileName.substring(0, 20);
				LOGGER.info("Generate FileName ==>> " + key);
			}
			fileName = fileName + ".json";
			LOGGER.info("FileName ==>> " + fileName);
			String file = filePath + File.separator + fileName;
			LOGGER.info("FileName with Path ==>> " + file);

			// File Name Given By User is valid or not
			if (fileNames.contains(fileName)) {

				String fileContent = null;
				if (!fileValues.containsKey(fileName)) {
					// Read from file freshly
					try (RandomAccessFile reader = new RandomAccessFile(file, "r");
							FileChannel channel = reader.getChannel()) {
						// Check file size
						LOGGER.info("File Size (In Bytes) ==>> " + channel.size());
						if (channel.size() > fileSizeCheck) {
							output = sizeValidationMessage;
						}
						ByteBuffer buff = ByteBuffer.allocate((int) channel.size());
						channel.read(buff);
						buff.flip();
						fileContent = new String(buff.array());
						fileValues.put(fileName, fileContent);
						LOGGER.info("fileValues ==>> " + fileValues);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					// use old content
					fileContent = fileValues.get(fileName);
				}
				LOGGER.info("File Content ==>> " + fileContent);
				// Check User provides Key or Not
				if (StringUtils.isNotBlank(inputModel.getKey())) {
					fileContentInKeyValuePair = readValueFromFile(fileContent);
					LOGGER.info("File Content In KeyValuePair ==>> " + fileContentInKeyValuePair);
					Set<String> onlyKeys = fileContentInKeyValuePair.keySet().parallelStream()
							.map(keys -> keys.split("-")[0]).collect(Collectors.toSet());
					LOGGER.info("Only Keys ==>> " + onlyKeys);
					if (!onlyKeys.isEmpty()) {
						if (onlyKeys.contains(inputModel.getKey())) {
							output = keyValidationMessage;
						}
					}
				}

			}

			if (output == null) {
				fileContentInKeyValuePair.put(key, inputModel.getValue());
				result = saveToFile(fileContentInKeyValuePair, file, fileName);
			}

			if (result) {
				output = "Key " + inputModel.getKey() + " saved successfully in the file named " + fileName;
			}
			LOGGER.info("output ==>> " + output);
		}

		return output;
	}

	/**
	 * This method is used to read the value from file and store it in a MAP if the
	 * timeToLive present in Key is still active
	 * 
	 * @param fileContent
	 * @return Map<String, String>
	 */
	private Map<String, String> readValueFromFile(String fileContent) {

		Map<String, String> treeMap = new TreeMap<String, String>();
		if (StringUtils.isNotBlank(fileContent)) {
			Map<String, String> fileContentInKeyValuePair = new LinkedHashMap<String, String>();
			Gson g = new Gson();
			fileContentInKeyValuePair = g.fromJson(fileContent, LinkedHashMap.class);
			if (!fileContentInKeyValuePair.isEmpty()) {
				fileContentInKeyValuePair.entrySet().parallelStream().forEach(e -> {
					LOGGER.info("readValueFromFile Key ==>> " + e.getKey() + " readValueFromFile Value ==>> "
							+ e.getValue());
					String timeToLive = e.getKey().split("-")[1];
					Instant instant = Instant.now();
					long timeStampSeconds = instant.getEpochSecond();
					LOGGER.info("TimeToLive present in Key ==>> " + timeToLive);
					LOGGER.info("Current timeStamp in Seconds ==>> " + timeStampSeconds);
					if (timeStampSeconds < Long.parseLong(timeToLive)) {
						treeMap.put(e.getKey(), e.getValue());
					}
				});
			}
		}
		return treeMap;
	}

	/**
	 * This method is used to save the MAP in file
	 * 
	 * @param fileContentInKeyValuePair
	 * @param file
	 * @return boolean
	 */
	private synchronized boolean saveToFile(Map<String, String> fileContentInKeyValuePair, String file,
			String fileName) {

		try {
			File targetFile = new File(file);
			if (targetFile.exists()) {
				targetFile.delete();
			}
			JsonObject obj = new JsonObject();
			if (!fileContentInKeyValuePair.isEmpty()) {
				fileContentInKeyValuePair.entrySet().parallelStream().forEach(e -> {
					LOGGER.info("saveToFile Key ==>> " + e.getKey() + " saveToFile Value ==>> " + e.getValue());
					obj.addProperty(e.getKey(), e.getValue());
				});
			}

			try (FileWriter fw = new FileWriter(targetFile)) {
				fw.write(obj.toString());
				fileNames.add(fileName);
				fileValues.remove(fileName);
				fileValues.put(fileName, obj.toString());
				LOGGER.info("fileValues in saveToFile ==>> " + fileValues);
				LOGGER.info("File Names present in the Directory ==>> " + fileNames);
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public String fetch(InputModel inputModel) {

		LOGGER.info("Request Coming in Fetch From User ==>> " + inputModel);
		Map<String, String> fileContentInKeyValuePair = new LinkedHashMap<String, String>();
		String output = null;
		if (Objects.nonNull(inputModel)) {
			String fileName = null;
			// Check FileName Present or Not
			if (StringUtils.isNotBlank(inputModel.getFileName())) {
				fileName = inputModel.getFileName();
				fileName = fileName + ".json";
				LOGGER.info("FileName ==>> " + fileName);
				String file = filePath + File.separator + fileName;
				LOGGER.info("FileName with Path ==>> " + file);

				// File Name Given By User is valid or not
				if (fileNames.contains(fileName)) {

					String fileContent = null;
					if (!fileValues.containsKey(fileName)) {
						// Read from file freshly
						try (RandomAccessFile reader = new RandomAccessFile(file, "r");
								FileChannel channel = reader.getChannel()) {
							ByteBuffer buff = ByteBuffer.allocate((int) channel.size());
							channel.read(buff);
							buff.flip();
							fileContent = new String(buff.array());
							fileValues.put(fileName, fileContent);
							LOGGER.info("fileValues ==>> " + fileValues);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						// use old content
						fileContent = fileValues.get(fileName);
					}
					fileContent = toPrettyFormat(fileContent);
					LOGGER.info("File Content ==>> " + fileContent);
					fileContentInKeyValuePair = readValueFromFile(fileContent);
					LOGGER.info("File Content In KeyValuePair ==>> " + fileContentInKeyValuePair);
					// Check User provides Key or Not
					if (StringUtils.isNotBlank(inputModel.getKey())) {
						Set<String> onlyKeys = fileContentInKeyValuePair.keySet().parallelStream()
								.map(keys -> keys.split("-")[0]).collect(Collectors.toSet());
						LOGGER.info("Only Keys ==>> " + onlyKeys);
						if (!onlyKeys.isEmpty()) {
							if (onlyKeys.contains(inputModel.getKey())) {
								JsonObject obj = new JsonObject();
								fileContentInKeyValuePair.entrySet().parallelStream().forEach(e -> {
									LOGGER.info("fetch Key ==>> " + e.getKey() + " fetch Value ==>> " + e.getValue());
									if (StringUtils.equals(e.getKey().split("-")[0], inputModel.getKey())) {
										obj.addProperty(inputModel.getKey(), e.getValue());
									}
								});
								output = obj.toString();
							} else {
								output = keyNotPresentMessage;
							}
						} else {
							output = keyNotPresentMessage;
						}
					} else {
						JsonObject obj = new JsonObject();
						fileContentInKeyValuePair.entrySet().parallelStream().forEach(e -> {
							System.out.println("fetch Key ==>> " + e.getKey() + " fetch Value ==>> " + e.getValue());
							obj.addProperty(e.getKey().split("-")[0], e.getValue());
						});
						output = obj.toString();
						output = toPrettyFormat(output);
					}

				} else {
					output = fileValidationMessage;
				}
			} else {
				output = fileValidationMessage;
			}
		}
		LOGGER.info("output ==>> " + output);
		return output;
	}

	@Override
	public String deleted(InputModel inputModel) {

		LOGGER.info("Request Coming in Delete From User ==>> " + inputModel);
		Map<String, String> fileContentInKeyValuePair = new LinkedHashMap<String, String>();
		String output = null;
		if (Objects.nonNull(inputModel)) {
			String fileName = null;
			// Check FileName Present or Not
			if (StringUtils.isNotBlank(inputModel.getFileName())) {
				fileName = inputModel.getFileName();
				fileName = fileName + ".json";
				LOGGER.info("FileName ==>> " + fileName);
				String file = filePath + File.separator + fileName;
				LOGGER.info("FileName with Path ==>> " + file);

				// File Name Given By User is valid or not
				if (fileNames.contains(fileName)) {

					String fileContent = null;
					if (!fileValues.containsKey(fileName)) {
						// Read from file freshly
						try (RandomAccessFile reader = new RandomAccessFile(file, "r");
								FileChannel channel = reader.getChannel()) {
							ByteBuffer buff = ByteBuffer.allocate((int) channel.size());
							channel.read(buff);
							buff.flip();
							fileContent = new String(buff.array());
							fileValues.put(fileName, fileContent);
							LOGGER.info("fileValues ==>> " + fileValues);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						// use old content
						fileContent = fileValues.get(fileName);
					}
					fileContent = toPrettyFormat(fileContent);
					LOGGER.info("File Content ==>> " + fileContent);

					// Check User provides Key or Not
					if (StringUtils.isNotBlank(inputModel.getKey())) {
						fileContentInKeyValuePair = readValueFromFile(fileContent);
						LOGGER.info("File Content In KeyValuePair ==>> " + fileContentInKeyValuePair);
						Set<String> onlyKeys = fileContentInKeyValuePair.keySet().parallelStream()
								.map(keys -> keys.split("-")[0]).collect(Collectors.toSet());
						LOGGER.info("Only Keys ==>> " + onlyKeys);
						if (!onlyKeys.isEmpty()) {
							if (onlyKeys.contains(inputModel.getKey())) {
								for (Map.Entry<String, String> e : fileContentInKeyValuePair.entrySet()) {
									if (e.getKey().split("-")[0].equals(inputModel.getKey())) {
										LOGGER.info("Removing Key ==>> " + e.getKey());
										fileContentInKeyValuePair.remove(e.getKey());
										break;
									}
								}
								boolean result = saveToFile(fileContentInKeyValuePair, file, fileName);
								if (result) {
									output = keyDeletedMessage;
								}
							} else {
								output = keyNotPresentMessage;
							}
						} else {
							output = keyNotPresentMessage;
						}
					} else {
						output = keyNotPresentMessage;
					}

				} else {
					output = fileValidationMessage;
				}
			} else {
				output = fileValidationMessage;
			}
		}
		LOGGER.info("output ==>> " + output);
		return output;
	}

	/**
	 * Convert a JSON string to pretty print version
	 * @param jsonString
	 * @return String
	 */
	private static String toPrettyFormat(String jsonString) {

		JsonParser parser = new JsonParser();
		JsonObject json = parser.parse(jsonString).getAsJsonObject();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String prettyJson = gson.toJson(json);

		return prettyJson;
	}
}
