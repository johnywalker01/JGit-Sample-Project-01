package com.shark.gitPack;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.shark.filePack.FileUtils;
import com.shark.filePack.WriteFile;

public class JGitUsageExample3 {

	/**
	 * This function will <b>create</b> a Git repository folder and then commit the files either selectively or as a whole.
	 * 
	 * @param filePath as {@link String} eg. <i>"C:\\JGit-test-folder"</i>
	 */
	public static void run(String filePath) {
		try {
			Git git = initGit(filePath);
			createGitIgnore(filePath);
			commitToLocalRepo(filePath, git);
			printStatus(git, false, false);
			pushToRemoteRepo(git, GitConstants.REMOTE_URI_2, GitConstants.USERNAME, GitConstants.PASSWORD);
			// fetchToLocalRepo(git, GitConstants.REMOTE_URI, GitConstants.USERNAME, GitConstants.PASSWORD);
			// Git git = cloneToLocalRepo(GitConstants.REMOTE_URI, filePath);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void createGitIgnore(String filePath) {
		try {
			File gitIgnoreFile = new File(filePath, ".gitignore");
			if (!gitIgnoreFile.exists()) {
				if (!gitIgnoreFile.createNewFile()) {
					throw new IOException("Could not create file " + gitIgnoreFile);
				}

				File binFolder = new File(filePath, "bin");
				if (binFolder.exists() && binFolder.isDirectory()) {
					WriteFile data = new WriteFile(gitIgnoreFile.getAbsolutePath(), true);
					data.writeToFile("/bin");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Git initGit(String filePath) {
		File localPath = new File(filePath);
		// Create the git repository with init
		Git git = Git.init().setDirectory(localPath).call();
		System.out.println("Created repository: " + git.getRepository().getDirectory());
		return git;
	}

	private static void commitToLocalRepo(String filePath, Git git) throws Exception {
		// TODO Auto-generated method stub
		/**
		 * for selective filtering of files add some extension values like java for *.java files for selecting whole files recursively inside a
		 * folder, just enter an empty String array.
		 */

//		String[] extensionArray = { "java", "css" };
		String[] extensionArray = { };

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
			// commit files into Git local Repo.
			git.commit().setMessage("Test commit").call();
			System.out.println("Committed  to repository at " + git.getRepository().getDirectory());
		}
	}

	/**
	 * Print Git status in console
	 * 
	 * @param git            as {@link Git}
	 * @param printAdded     set <code>true</code> for printing files with Status, <code>added</code>
	 * @param printUntracked set <code>true</code> for printing files with Status, <code>untracked</code>
	 */
	private static void printStatus(Git git, boolean printAdded, boolean printUntracked) {
		try {
			Status status = git.status().call();

			if (printAdded) {
				Set<String> added = status.getAdded();
				for (String add : added) {
					System.out.println("Added: " + add);
				}
			}
			if (printUntracked) {
				Set<String> untracked = status.getUntracked();
				for (String untrack : untracked) {
					System.out.println("Untracked: " + untrack);
				}
			}

		} catch (NoWorkTreeException | IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Push to remote repository.
	 *
	 * @param git       as {@link Git}
	 * @param remoteUri as {@link String}
	 * @param username  as {@link String}
	 * @param password  as {@link String}
	 */
	private static void pushToRemoteRepo(Git git, String remoteUri, String username, String password) {
		try {
			PushCommand pushCommand = git.push();
			pushCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password));
			pushCommand.setRemote(remoteUri);
			// for printing the progress.
			pushCommand.setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out)));
			pushCommand.call();
			System.out.println("----- PUSH-ing Completed -----");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void fetchToLocalRepo(Git git, String remoteUri, String username, String password) {
		try {
			FetchCommand fetch = git.fetch();

			List<RefSpec> specs = new ArrayList<RefSpec>();
			specs.add(new RefSpec("+refs/heads/*:refs/remotes/origin/*"));
			specs.add(new RefSpec("+refs/tags/*:refs/tags/*"));
			specs.add(new RefSpec("+refs/notes/*:refs/notes/*"));

			fetch.setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password));
			fetch.setRemote(remoteUri);

			fetch.setRefSpecs(specs);
			// for printing the progress.
			fetch.setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out)));
			
			fetch.call();
			System.out.println("----- FETCH-ing Completed -----");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	private static Git pullToLocalRepo(String remoteUri, String username, String password) throws Exception {
//		try {
//			CloneCommand cloneCommand = Git.cloneRepository();
//
//			List<RefSpec> specs = new ArrayList<RefSpec>();
//			specs.add(new RefSpec("+refs/heads/*:refs/remotes/origin/*"));
//			specs.add(new RefSpec("+refs/tags/*:refs/tags/*"));
//			specs.add(new RefSpec("+refs/notes/*:refs/notes/*"));
//
//			cloneCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password));
//			cloneCommand.setURI(remoteUri);
//
//			Git git = cloneCommand.call();
//			System.out.println("----- PULL Result -----\n" + git.toString());
//			return git;
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new Exception(e);
//		}
//	}

	private static Git cloneToLocalRepo(String remoteUri, String filePath) throws Exception {
		try {
			File baseFile = new File(filePath);
			System.out.println("Base folder " + baseFile.getParent());

			CloneCommand cloneCommand = Git.cloneRepository();
			cloneCommand.setURI(remoteUri);

			File cloneFile = new File(baseFile.getParent(), "new-branch");
			if(!cloneFile.exists()) {
				if (!cloneFile.mkdir()) {
					throw new IOException("Could not create folder " + cloneFile);
				}
			}
			System.out.println("Branch folder " + cloneFile.getAbsolutePath());

			cloneCommand.setDirectory(cloneFile);
			// for printing progress of the cloning
			cloneCommand.setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out)));

			Git git = cloneCommand.call();
			System.out.println("----- CLONE-ing Completed -----");
			return git;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}
	}

}