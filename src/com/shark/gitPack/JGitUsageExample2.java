package com.shark.gitPack;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.shark.filePack.FileUtils;

public class JGitUsageExample2 {
	private static final String USERNAME = "johnywalker01";
	private static final String PASSWORD = "gituser@2018";
	private static final String REMOTE_URI = "https://github.com/johnywalker01/git0-test-01.git";

	public static void run(String filePath) {
		try {
			File localPath = new File(filePath);
			// Create the git repository with init
			Git git = Git.init().setDirectory(localPath).call();
			System.out.println("Created repository: " + git.getRepository().getDirectory());

			String[] extensionArray = { "java", "css" };
//			String[] extensionArray = { };

			List<File> fileList = FileUtils.getFilteredFiles(filePath, extensionArray);
			List<String> relFileNames = FileUtils.getRelativeNameOfFiles(fileList, filePath);

			boolean doCommit = false;
			if (extensionArray.length > 0) {
				for (String file : relFileNames) {
					// run the add-call
					System.out.println("Adding file " + file);
					git.add().addFilepattern(file).call();
				}
				doCommit = fileList.size() > 0 ? true : false;
			} else {
				git.add().addFilepattern(".").call();
				System.out.println("Adding all files.");
				doCommit = true;
			}

			if (doCommit) {
				git.commit().setMessage("Test commit").call();
				System.out.println("Committed  to repository at " + git.getRepository().getDirectory());
			}
			
			Status status = git.status().call();

			Set<String> added = status.getAdded();
			for (String add : added) {
				System.out.println("Added: " + add);
			}
			
			Set<String> untracked = status.getUntracked();
			for (String untrack : untracked) {
				System.out.println("Untracked: " + untrack);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/*public static void run(String filePath) {
		try {
			// Create the git repository with init
			// This code would allow to access an existing repository
			File localPath = new File(filePath);
			Git git = Git.init().setDirectory(localPath).call();
			System.out.println("Created repository: " + git.getRepository().getDirectory());

//			localPath = new File(localPath.getAbsoluteFile().getParent() + "/.git");
//			System.out.println("AbsolutePath " + localPath.getAbsolutePath());

			// String[] extensionArray = { "txt", "pdf" };
			String[] extensionArray = { "css" };

			boolean doCommit = false;
			if (extensionArray.length > 0) {
				File[] fileList = FileUtils.getFilteredFiles(localPath, extensionArray);

				for (File file : fileList) {
					// run the add-call
					System.out.println("Adding file " + file.getName());
					git.add().addFilepattern(file.getName()).call();
				}
				doCommit = fileList.length > 0 ? true : false;
			} else {
				git.add().addFilepattern(".").call();
				System.out.println("Adding all files.");
				doCommit = true;
			}
			
			
			if (doCommit) {
				git.commit().setMessage("Test commit").call();
				System.out.println("Committed  to repository at " + git.getRepository().getDirectory());
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
	}*/

	public static void commitToLocalRepo() {
		try {
			// This code would allow to access an existing repository
			File localPath = new File("");
			localPath = new File(localPath.getAbsoluteFile().getParent() + "/.git");
			System.out.println("AbsolutePath " + localPath.getAbsolutePath());
			Git git = Git.open(localPath);
			System.out.println("Checking repository: " + git.getRepository().getDirectory());

//			String[] extensionArray = { "c1", "c2", "c3" };
//			File[] fileList = FileUtils.getFilteredFiles(new File(localPath.getParent()), extensionArray);

//			for (File file : fileList) {
//				// run the add-call
//				System.out.println("Adding file " + file.getName());
//				git.add().addFilepattern(file.getName()).call();
//			}
//
//			git.commit().setMessage("Test commit").call();
//			System.out.println("Committed  to repository at " + git.getRepository().getDirectory());

			Status status = git.status().call();

			Set<String> missing = status.getMissing();
			for (String miss : missing) {
				System.out.println("Missing: " + miss);
			}

			Set<String> added = status.getAdded();
			for (String add : added) {
				System.out.println("Added: " + add);
			}

			Set<String> untracked = status.getUntracked();
			for (String untrack : untracked) {
				System.out.println("Untracked: " + untrack);
			}

			pushTest(git, USERNAME, PASSWORD);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void commitAndPushToRemote() {

		try {
			// This code would allow to access an existing repository
			File localPath = new File("");
			localPath = new File(localPath.getAbsoluteFile().getParent() + "/.git");
			System.out.println("AbsolutePath " + localPath.getAbsolutePath());
			Git git = Git.open(localPath);
			System.out.println("Checking repository: " + git.getRepository().getDirectory());

			File myFile = new File(git.getRepository().getDirectory().getParent(), "testfile.rcc");
			if (!myFile.createNewFile()) {
				throw new IOException("Could not create file " + myFile);
			}
			// run the add-call
			git.add().addFilepattern(myFile.getName()).call();

			myFile = new File(git.getRepository().getDirectory().getParent(), "testfile.acc");
			if (!myFile.createNewFile()) {
				throw new IOException("Could not create file " + myFile);
			}
			// run the add-call
			git.add().addFilepattern(myFile.getName()).call();

			myFile = new File(git.getRepository().getDirectory().getParent(), "testfile.bcc");
			if (!myFile.createNewFile()) {
				throw new IOException("Could not create file " + myFile);
			}
			// run the add-call
			git.add().addFilepattern(myFile.getName()).call();

			git.commit().setMessage("Initial commit").call();
			System.out.println("Committed file " + myFile + " to repository at " + git.getRepository().getDirectory());

			pushTest(git, USERNAME, PASSWORD);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void pushToRemote_test() {

		try {
			// This code would allow to access an existing repository
			File localPath = new File("");
			localPath = new File(localPath.getAbsoluteFile().getParent() + "/.git");
			System.out.println("AbsolutePath " + localPath.getAbsolutePath());
			Git git = Git.open(localPath);
			System.out.println("Checking repository: " + git.getRepository().getDirectory());

			File myFile = new File(git.getRepository().getDirectory().getParent(), "testfile.txt");
			// run the add-call
			git.add().addFilepattern(myFile.getName()).call();

			myFile = new File(git.getRepository().getDirectory().getParent(), "testfile.java");

			// run the add-call
			git.add().addFilepattern(myFile.getName()).call();

			myFile = new File(git.getRepository().getDirectory().getParent(), "testfile.cpp");
			// run the add-call
			git.add().addFilepattern(myFile.getName()).call();

			pushTest(git, USERNAME, PASSWORD);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Push current state to remote repository.
	 *
	 * @param git      for {@link Git}
	 * @param username committer username
	 * @param password committer password
	 */
	public static void pushTest(Git git, String username, String password) {
		PushCommand pushCommand = git.push();
		pushCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password));
		pushCommand.setRemote(REMOTE_URI);
		Iterable<PushResult> resultIterable;
		try {
			resultIterable = pushCommand.call();
			System.out.println(" value " + resultIterable.toString());
		} catch (JGitInternalException | InvalidRemoteException e) {
			e.printStackTrace();
		}
	}

}
