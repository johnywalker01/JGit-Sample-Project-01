package com.shark.gitPack;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class GitUtil {
	public static void fetchGiRepo_1() throws IOException, InvalidRemoteException, TransportException, GitAPIException {
		Git git = Git.cloneRepository().setURI("https://github.com/eclipse/jgit.git").setDirectory(new File("/path/to/repo")).call();
		System.out.println(git);
	}

	public static void fetchGiRepo() throws IOException {
		File localPath = new File("/repository2/");
		Git git = Git.open(localPath);
		System.out.println(git);
	}

	@SuppressWarnings("unused")
	public static void openLocalRepo() throws IOException {
		
		File localPath = new File("");
//		System.out.println("Path " + localPath.getPath());
//		System.out.println("Parent " + localPath.getAbsoluteFile().getParent());

		localPath = new File(localPath.getAbsoluteFile().getParent() + "/.git");
		System.out.println("AbsolutePath " + localPath.getAbsolutePath());

		FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
		Repository repository = repositoryBuilder.setGitDir(localPath).readEnvironment() // scan environment GIT_* variables
				.findGitDir() // scan up the file system tree
				.setMustExist(true)
				.build();

		System.out.println(repository.toString());
		
		System.out.println("END of openLocalRepo");
	}

}
