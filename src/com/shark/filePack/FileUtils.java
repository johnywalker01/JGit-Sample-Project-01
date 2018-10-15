package com.shark.filePack;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

	/**
	 * List out all files inside a Directory, <b>Non-recursive</b>
	 * 
	 * @param filePath       as {@link File}
	 * @param extensionArray as {@link String} Array. eg. <code>{"txt","pdf"}</code>
	 * @return {@link File} Array of listed files in the <b>filepath</b>
	 */
	public static File[] getFilteredFiles(File filePath, String[] extensionArray) {
		FilenameFilter filter = new MyFileFilterClass(extensionArray);

		// a list that contains only files with the specified extension
		File[] fileList = filePath.listFiles(filter);

		if (fileList.length > 0) {
			System.out.println("Containing files with the use of filter are:");

			for (File file : fileList) {
				System.out.println(file.getAbsolutePath());
			}
		} else {
			System.out.println("There are not such files into the " + filePath);
		}

		return fileList;
	}
	
	/**
	 * Recursive search
	 */
	public static List<File> getFilteredFiles(String directory, String[] extensionArray) {
		List<String> allFiles = FileUtils.getAllFileNames(directory);
		List<String> filtfileNames = FileUtils.getFilteredFileNames(allFiles, extensionArray);

		List<File> files = new ArrayList<>();
		for (String fileName : filtfileNames) {
			files.add(new File(fileName));
		}
		return files;
	}

	public static List<String> getRelativeNameOfFiles(List<File> all, String absolutePath) {
		List<String> relFileNames = new ArrayList<>();
		for (File file : all) {
			String absFileName = file.getAbsolutePath();
			if (absolutePath != null) {

				// removes the absolute directory path
				String relativeFileName = absFileName.replace(absolutePath, "");

				// replace the '\' to '/'
				relativeFileName = relativeFileName.replaceAll("\\\\", "/");

				// Storing the the result.
				// removing the beginning '\' from the relative file name.
				relFileNames.add(relativeFileName.substring(1, relativeFileName.length()));
			}
		}

		return relFileNames;
	}

	public static List<String> getFilteredFileNames(List<String> allFiles, String[] extensionArray) {
		List<String> files = new ArrayList<>();

		for (String file : allFiles) {
			for (String name : extensionArray) {
				if (file.toLowerCase().endsWith(name)) {
					files.add(file);
				}
			}
		}
		if (extensionArray.length == 0) {
			return allFiles;
		} else {
			return files;
		}
	}

	public static List<String> getAllFileNames(String directory) {

		List<String> files = new ArrayList<>();

		File[] faFiles = new File(directory).listFiles();
		for (File file : faFiles) {
			if (file.getName().matches("^(.*?)")) {
				files.add(file.getAbsolutePath());
			}
			if (file.isDirectory()) {
				files.addAll(getAllFileNames(file.getAbsolutePath()));
			}
		}
		return files;
	}

}
