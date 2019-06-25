package bugCrawler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;

import javax.net.ssl.SSLHandshakeException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Task implements Runnable {
	
	public static final String ROOT_DIR = "./data/";
	public static final String RECORD_FILE = "record";
	public static final String XML_DIR = "bugs";
	public static final String HISTORY_DIR = "history";
	private static int exception = 0;
	
	private Config config;
	private Long begin;
	private Long done;
	private File recordFile;
	private File dirFile;
	private File bugsFile;
	private File historyFile;
	/**
	 * Constructor of Task
	 * @param config
	 * @throws IOException 
	 */
	public Task(Config config) throws IOException{
		this.config = config;
		String saveDir = ROOT_DIR + config.getProject();

		dirFile = new File(saveDir);
		if(dirFile.exists()){
			this.recordFile = new File(dirFile,RECORD_FILE);
			getRecord();
			bugsFile = new File(dirFile,XML_DIR);
			historyFile = new File(dirFile,HISTORY_DIR);
		}else{
			dirFile.mkdirs();
			this.recordFile = new File(dirFile,RECORD_FILE);
			this.recordFile.createNewFile();
			this.begin =(long)10000;
			this.done = (long)0;
			setRecord();
			bugsFile = new File(dirFile,XML_DIR);
			bugsFile.mkdir();
			historyFile = new File(dirFile,HISTORY_DIR);
			historyFile.mkdir();
		}
		
		if(this.config.getBegin() != null) 
			this.begin = this.config.getBegin();
		if(this.config.getJksPath()!=null) 
			System.setProperty("javax.net.ssl.trustStore", this.config.getJksPath());
		
	}
	
	/*
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		long success = 0;
		long id = 0;
		long total = config.getTotal();
		System.out.println("Begin["+new Date()+"]:任务开始，开始bug编号："+this.begin+",到"+total+"为止");
		try{
			for(;this.begin <= total;){
				id= this.begin;
				if(isValidBug(id)){
					saveXml(id);
					saveHistory(id);
//					System.out.println("bug"+id+"下载成功");
					success++;
					this.begin++;
					this.done ++;
				}else{
					this.begin ++;
				}
				if(this.begin % 20 == 0)
					setRecord();
			}
			setRecord();
			System.out.println("End["+new Date()+"]:此次任务完成"+success+"，所有任务共完成收集"+this.done+",下次任务开始编号"+this.begin);
		}catch(SSLHandshakeException e){
			System.out.println("Exception:无法通过验证，请输入JKS文件");
		}catch(IOException e){
//			e.printStackTrace();
			try {
				setRecord();
				System.out.println("Exception["+new Date()+"]:抓取"+id+"异常中断,"+"此次任务完成:"+success+",任务将稍后自动重启");
				Thread.sleep(10000);
				
				if(this.config.getBegin() != null && this.begin == this.config.getBegin()){
					if(Task.exception ++ >= 3){
						System.out.println("Exception["+new Date()+"]:任务"+this.begin + "中断3次，跳过此bug");
						config.setBegin(this.begin + 1);
						Task.exception = 0;
					}
				}else{
					config.setBegin(this.begin);
					Task.exception = 0;
				}
//				config.setTotal(config.getTotal() - count);
				Task mozillaTask = new Task(config);
				new Thread(mozillaTask).start();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * @title isValidBug
	 * @description 
	 * @param id
	 * @return
	 * @throws IOException
	 */
	private boolean isValidBug(long id) throws IOException{
		String xmlURL = config.getBaseURL() + "show_bug.cgi?ctype=xml&id=" + id;
		Document doc = Jsoup.connect(xmlURL).get();
		Element bugTag = doc.getElementsByTag("bug").first();
		String error = bugTag.attr("error");
		if(error== null || error.equals("")){
//			System.out.println(xmlURL + "是有效bug");
			return true;
		}else{
			System.out.println(xmlURL + "是无效bug");
			return false;
		}
	}
	
	/**
	 * @title saveXml
	 * @description 
	 * @param id
	 * @throws IOException 
	 */
	private void saveXml(long id) throws IOException{
		String xmlURL = config.getBaseURL() + "show_bug.cgi?ctype=xml&id=" + id;
		URL bugXmlURL = new URL(xmlURL);
		DataInputStream dis = new DataInputStream(bugXmlURL.openStream());
		OutputStream ops = new FileOutputStream(new File(bugsFile,""+id+".xml"));
		byte buff[] = new byte[1024];
		int len = -1;
		while((len = dis.read(buff))!=-1){
			ops.write(buff,0,len);
		}
		dis.close();
		ops.close();
//		System.out.println(xmlURL + "下载xml成功");
	}
	
	
	private void saveHistory(long id) throws IOException{
		String historyURL = config.getBaseURL() + "show_activity.cgi?id=" + id;
//		Document doc = Jsoup.connect(historyURL).get();
//		Element tableTag = doc.select("#bugzilla-body table").first();
//		DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File(historyFile,""+id+".txt")));
//		dos.writeChars(tableTag.html());
//		dos.close();
//		System.out.println(tableURL + "下载table成功");
		URL bugHistroyURL = new URL(historyURL);
		DataInputStream dis = new DataInputStream(bugHistroyURL.openStream());
		OutputStream ops = new FileOutputStream(new File(historyFile,""+id+".html"));
		byte buff[] = new byte[1024];
		int len = -1;
		while((len = dis.read(buff))!=-1){
			ops.write(buff,0,len);
		}
		dis.close();
		ops.close();
	}
	/**
	 * @throws IOException 
	 * @title setRecord
	 * @description
	 */
	private void setRecord() throws IOException{
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(recordFile));
		dos.writeLong(this.begin);
		dos.writeLong(this.done);
		dos.close();
	}
	
	/**
	 * @throws IOException 
	 * @title getRecord
	 * @description
	 */
	private void getRecord() throws IOException{
		DataInputStream din = new DataInputStream(new FileInputStream(recordFile));
		this.begin = din.readLong();
		this.done = din.readLong();
		din.close();
	}
}
