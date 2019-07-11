package bugCrawler;

import java.io.IOException;
import java.util.Scanner;

public class MainController {

	/**
	 * @title main
	 * @description
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		String project;
		String baseURL;
		Long total;
		Long begin;
		String jksPath;
		
		Scanner scan = new Scanner(System.in);
		System.out.println("Project Name:");
		project = scan.nextLine();
		System.out.println("Corresponding bugzilla base URL:");
		baseURL = scan.nextLine();
		System.out.println("jks file url (input no if you cannot fine it)");
		jksPath = scan.nextLine();
		System.out.println("End report ID:");
		total = scan.nextLong();
		System.out.println("Start report ID (0 means continue, 10000 by default):");
		begin = scan.nextLong();
		scan.close();
		
		Config config = new Config(project,baseURL,total);
		if(begin != 0)
			config.setBegin(begin);
		if(!jksPath.equals("no")){
			System.out.println("Add jks file"+jksPath);
			config.setJksPath(jksPath);
		}
		Task task = new Task(config);
		new Thread(task).start();
	}

}
