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

public class JGitUsageExample {
	private static final String USERNAME="johnywalker01";
	private static final String PASSWORD="gituser@2018";
	private static final String REMOTE_URI="https://github.com/johnywalker01/git0-test-01.git";
	
	public static void run() throws IOException, IllegalStateException, GitAPIException {
		File localPath = File.createTempFile("JGitTestRepository", "");

		// delete repository before running this
		Files.delete(localPath.toPath());

		try {
			// Create the git repository with init
//			Git git = Git.init().setDirectory(localPath).call();
//			System.out.println("Created repository: " + git.getRepository().getDirectory());
			
			// This code would allow to access an existing repository
			localPath = new File("");
			localPath = new File(localPath.getAbsoluteFile().getParent() + "/.git");
			System.out.println("AbsolutePath " + localPath.getAbsolutePath());
			Git git = Git.open(localPath);
			System.out.println("Checking repository: " + git.getRepository().getDirectory());
			
			File myFile = new File(git.getRepository().getDirectory().getParent(), "testfile");
			if (!myFile.createNewFile()) {
				throw new IOException("Could not create file " + myFile);
			}

			// run the add-call
			git.add().addFilepattern("testfile").call();

			git.commit().setMessage("Initial commit").call();
			System.out.println("Committed file " + myFile + " to repository at " + git.getRepository().getDirectory());
			
			// Create a few branches for testing
			for (int i = 0; i < 10; i++) {
				git.checkout().setCreateBranch(true).setName("new-branch" + i).call();
			}
			// List all branches
			List<Ref> call = git.branchList().call();
			for (Ref ref : call) {
				System.out.println("Branch: " + ref + " " + ref.getName() + " " + ref.getObjectId().getName());
			}

			// Create a few new files
			for (int i = 0; i < 10; i++) {
				File f = new File(git.getRepository().getDirectory().getParent(), "testfile" + i);
				f.createNewFile();
				if (i % 2 == 0) {
					git.add().addFilepattern("testfile" + i).call();
				}
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

			// Find the head for the repository
			ObjectId lastCommitId = git.getRepository().resolve(Constants.HEAD);
			System.out.println("Head points to the following commit :" + lastCommitId.getName());
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
