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
		System.out.println("输入需要爬取项目名：");
		project = scan.nextLine();
		System.out.println("输入需要爬取项目的bugzilla的基地址：");
		baseURL = scan.nextLine();
		System.out.println("输入爬取网站的jks文件地址（no代表不加入jks文件，此时有可能不能访问）");
		jksPath = scan.nextLine();
		System.out.println("输入此次任务截止编号:");
		total = scan.nextLong();
		System.out.println("输入此次任务开始爬取的编号（０代表自动从上次停止处爬取,第一次默认从10000开始爬取）：");
		begin = scan.nextLong();
		scan.close();
		
		Config config = new Config(project,baseURL,total);
		if(begin != 0)
			config.setBegin(begin);
		if(!jksPath.equals("no")){
			System.out.println("加入证书"+jksPath);
			config.setJksPath(jksPath);
		}
		Task task = new Task(config);
		new Thread(task).start();
	}

}
