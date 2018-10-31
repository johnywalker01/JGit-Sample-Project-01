package com.shark.main;

import com.shark.gitPack.GitConstants;
import com.shark.gitPack.JGitUsageExample3;

public class EntryPoint {

	public static void main(String[] args) {
		System.out.println("Starting ... JGit Usage Example");

//		try {
//			GitUtil.openLocalRepo();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		JGitUsageExample.commitFile_test();
//		JGitUsageExample.pushToRemote_test();
//		JGitUsageExample.commitAndPushToRemote();
		
//		JGitUsageExample.commitToLocalRepo();
		
//		String[] extensionArray = { "txt", "pdf" };
//		File absFilePath = new File("C:\\\\Users\\\\1014821\\\\Downloads");
//		FileUtils.getFilteredFiles(absFilePath, extensionArray);
		
//		JGitUsageExample2.run("C:\\Users\\1014821\\Desktop\\test\\JGit-test-folder\\MyFxApp");
		
//		JGitUsageExample3.execute("C:\\Users\\1014821\\Desktop\\test\\JGit-test-folder\\MyFxApp");
		
//		JGitUsageExample3.run("C:\\Users\\1014821\\Desktop\\test\\JGit-test-folder\\JGit-Sample-Project-01");

//		String[] extsnArray = { "properties" };
//		JGitUsageExample3.initGitFile("C:\\Users\\1014821\\Desktop\\test\\JGit-test-folder\\custom-git-folder", extsnArray);

//		JGitUsageExample3.doICP("C:/Users/1014821/Desktop/files/Web-Pool/Angular2/test-app", GitConstants.REMOTE_URI_3);

		JGitUsageExample3.doICP("C:/Users/1014821/Desktop/test/JGit-test-folder/JGit-Sample-Project-01",GitConstants.REMOTE_URI_2);
		
//		JGitUsageExample3.cloneToLocalRepo(GitConstants.REMOTE_URI_3, "C:/Users/1014821/Desktop/files/Web-Pool/Angular2/test-app");

		System.out.println("END of JGit Usage Example");
	}

}
