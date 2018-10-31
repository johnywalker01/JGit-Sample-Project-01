package com.shark.gitPack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

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
	public static void execute(String filePath) {
		try {
			Git git = initGit(filePath);
			createGitIgnore(filePath);
			commitToLocalRepo(filePath, git);
			checkStatus(git);
			pushToRemoteRepo(git, GitConstants.REMOTE_URI, GitConstants.USERNAME, GitConstants.PASSWORD);
			// fetchToLocalRepo(git, GitConstants.REMOTE_URI, GitConstants.USERNAME, GitConstants.PASSWORD);
			// Git git = cloneToLocalRepo(GitConstants.REMOTE_URI, filePath);

		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

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
			checkStatus(git);
			pushToRemoteRepo(git, GitConstants.REMOTE_URI_2, GitConstants.USERNAME, GitConstants.PASSWORD);
			// fetchToLocalRepo(git, GitConstants.REMOTE_URI, GitConstants.USERNAME, GitConstants.PASSWORD);
			// Git git = cloneToLocalRepo(GitConstants.REMOTE_URI, filePath);

		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Initialize Git folder, Commit the changes and Push the changes into Git
	 * 
	 * @param filePath
	 */
	public static void doICP(String filePath, String remoteRepo) {
		try {
			Git git = initGit(filePath);
			createGitIgnore(filePath);
			checkStatus(git);
			commitToLocalRepo(filePath, git);
			pushToRemoteRepo(git, remoteRepo, GitConstants.USERNAME, GitConstants.PASSWORD);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initialize a git folder, either at relative path or at a specific location read from sonar.properties file.
	 * 
	 * @param filePath in {@link String} eg. <code>C:\test-folder</code>
	 * @param extensionArray in {@link String} Array eg. <code>{"properties"}</code>
	 */
	public static void initGitFile(String filePath, String[] extensionArray) {
		try {
			List<File> fileList = FileUtils.getFilteredFiles(filePath, extensionArray);

			if (fileList.size() > 0) {
				String gitPath = getGitPath(fileList);
				Git git = initGit(gitPath);
			}
			else {
				System.out.println("properties file missing at location : " + filePath);
			}

		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String getGitPath(List<File> fileList) throws FileNotFoundException, IOException {
		String projectBaseDir = FileUtils.readFromPropertiesFile(fileList.get(0).getAbsolutePath(), "sonar.projectBaseDir");
		String sonarSources = FileUtils.readFromPropertiesFile(fileList.get(0).getAbsolutePath(), "sonar.sources");
		if (projectBaseDir.isEmpty()) {
			projectBaseDir = (new File("").getAbsolutePath());
		}

		if (!sonarSources.isEmpty()) {
			char[] p2 = sonarSources.toCharArray();
			System.out.println("-- first " + p2[0]);
			if (p2[0] == '.') {
				sonarSources = (sonarSources.length() > 1) ? (sonarSources = sonarSources.substring(1, sonarSources.length() - 1)) : "";
				System.out.println("sonarSources " + sonarSources);
			}
		}
		String gitPath = projectBaseDir + sonarSources;
		if (!gitPath.isEmpty()) {
			char[] p3 = gitPath.toCharArray();
			System.out.println("-- git last " + p3[p3.length - 1]);
			if (p3[p3.length - 1] == '.') {
				gitPath = gitPath.substring(0, gitPath.length() - 2);
				gitPath += "\\";
			}
		}
		System.out.println("gitPath " + gitPath);
		return gitPath;
	}

	/**
	 * Create a git ignore file (<i>.gitignore</i>) in the provided file path.
	 * 
	 * @param filePath
	 */
	private static void createGitIgnore(String filePath) {
		try {
			File gitIgnoreFile = new File(filePath, ".gitignore");
			if (!gitIgnoreFile.exists()) {
				if (!gitIgnoreFile.createNewFile()) { throw new IOException("Could not create file " + gitIgnoreFile); }

				File binFolder = new File(filePath, "bin");
				if (binFolder.exists() && binFolder.isDirectory()) {
					WriteFile data = new WriteFile(gitIgnoreFile.getAbsolutePath(), true);
					data.writeToFile("/bin");
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initialize Git folder in filePath or retrieve Git details from filePath; and then return the {@link Git} object.
	 * 
	 * @param filePath in {@link String} eg. <i>C:\test-folder</i>
	 * @return {@link Git} object.
	 */
	private static Git initGit(String filePath) {
		File localPath = new File(filePath);
		File gitPath = new File(filePath + "\\.git");

		Git git = null;
		try {
			if (gitPath.exists()) {
				git = Git.open(gitPath);
				System.out.println("Retrieved repository : " + git.getRepository().getDirectory());
			}
			else {
				// Create the git repository with init
				git = Git.init().setDirectory(localPath).call();
				System.out.println("Created repository : " + git.getRepository().getDirectory());
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return git;
	}

	private static void commitToLocalRepo(String filePath, Git git) throws Exception {
		/**
		 * for selective filtering of files add some extension values like java for *.java files for selecting whole files
		 * recursively inside a folder, just enter an empty String array.
		 */
		// String[] extensionArray = { "java", "css" };
		String[] extensionArray = {};

		boolean doCommit = false;
		if (extensionArray.length > 0) {
			List<File> fileList = FileUtils.getFilteredFiles(filePath, extensionArray);
			List<String> relFileNames = FileUtils.getRelativeNameOfFiles(fileList, filePath);
			for (String file : relFileNames) {
				// run the add-call
				System.out.println("Adding file " + file);
				git.add().addFilepattern(file).call();
			}
			doCommit = fileList.size() > 0 ? true : false;
		}
		else {
			git.add().addFilepattern(".").call();
			System.out.println("Adding all files.");
			doCommit = true;
		}

		if (doCommit) {

			// get Calendar instance
			Calendar now = Calendar.getInstance();

			// get current TimeZone using getTimeZone method of Calendar class
			TimeZone timeZone = now.getTimeZone();

			String commitMsg = "New  commit\n" + "Current TimeZone is : " + timeZone.getDisplayName() + "\n " + new Date().getTime();

			// commit files into Git local Repo.
			git.commit().setMessage(commitMsg).call();
			System.out.println("Committed  to repository at " + git.getRepository().getDirectory());
		}
	}

	/**
	 * Print Git status in console
	 * 
	 * @param git as {@link Git}
	 * @param printAdded set <code>true</code> for printing files with Status, <code>added</code>
	 * @param printUntracked set <code>true</code> for printing files with Status, <code>untracked</code>
	 */
	private static void checkStatus(Git git) {
		try {
			Status status = git.status().call();

			Set<String> added = status.getAdded();
			for (String add : added) {
				System.out.println("Added: " + add);
			}
			
			Set<String> untracked = status.getUntracked();
			for (String untrack : untracked) {
				System.out.println("Untracked: " + untrack);
			}
			
			Set<String> changed = status.getChanged();
			for (String change : changed) {
				System.out.println("Changed: " + change);
			}
			
			Set<String> modified = status.getModified();
			for (String modify : modified) {
				System.out.println("Modified: " + modify);
			}
			
			Set<String> removed = status.getRemoved();
			for (String remove : removed) {
				System.out.println("Removed: " + remove);
			}
		}
		catch (NoWorkTreeException | IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Push to remote repository.
	 *
	 * @param git as {@link Git}
	 * @param remoteUri as {@link String}
	 * @param username as {@link String}
	 * @param password as {@link String}
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

		}
		catch (Exception e) {
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
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	// private static Git pullToLocalRepo(String remoteUri, String username, String password) throws Exception {
	// try {
	// CloneCommand cloneCommand = Git.cloneRepository();
	//
	// List<RefSpec> specs = new ArrayList<RefSpec>();
	// specs.add(new RefSpec("+refs/heads/*:refs/remotes/origin/*"));
	// specs.add(new RefSpec("+refs/tags/*:refs/tags/*"));
	// specs.add(new RefSpec("+refs/notes/*:refs/notes/*"));
	//
	// cloneCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password));
	// cloneCommand.setURI(remoteUri);
	//
	// Git git = cloneCommand.call();
	// System.out.println("----- PULL Result -----\n" + git.toString());
	// return git;
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// throw new Exception(e);
	// }
	// }

//	private static Git cloneToLocalRepo(String remoteUri, String filePath) throws Exception {
//		try {
//			File baseFile = new File(filePath);
//			System.out.println("Base folder " + baseFile.getParent());
//
//			CloneCommand cloneCommand = Git.cloneRepository();
//			cloneCommand.setURI(remoteUri);
//
//			File cloneFile = new File(baseFile.getParent(), "new-branch");
//			if (!cloneFile.exists()) {
//				if (!cloneFile.mkdir()) { throw new IOException("Could not create folder " + cloneFile); }
//			}
//			System.out.println("Branch folder " + cloneFile.getAbsolutePath());
//
//			cloneCommand.setDirectory(cloneFile);
//			// for printing progress of the cloning
//			cloneCommand.setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out)));
//
//			Git git = cloneCommand.call();
//			System.out.println("----- CLONE-ing Completed -----");
//			return git;
//
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//			throw new Exception(e);
//		}
//	}

	public static Git cloneToLocalRepo(String remoteUri, String filePath) {
		Git git = null;
		try {
			File cloneDir = new File(filePath);
			System.out.println("Clone folder " + cloneDir);
			if (!cloneDir.exists()) {
				if (!cloneDir.mkdir()) { throw new IOException("Could not create folder " + cloneDir); }
			}

			CloneCommand cloneCommand = Git.cloneRepository();
			cloneCommand.setURI(remoteUri);
			cloneCommand.setDirectory(cloneDir);

			// for printing progress of the cloning
			cloneCommand.setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out)));

			git = cloneCommand.call();
			System.out.println("----- CLONE-ing Completed -----");

		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return git;
	}

}
