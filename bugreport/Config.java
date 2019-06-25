package bugCrawler;

public class Config {
	public static final Long MAX_ITEM = (long)100000;
	
	private String project;
	private String baseURL;
	private Long total;
	private Long begin;
	private String jksPath;
	
	
	/**
	 * Constructor of Config
	 * @param project
	 * @param baseURL
	 */
	public Config(String project, String baseURL) {
		this.project = project;
		this.baseURL = baseURL;
		this.total = MAX_ITEM;
		this.begin = null;
		this.setJksPath(null);
	}

	/**
	 * Constructor of Config
	 * @param project
	 * @param baseURL
	 * @param total
	 */
	public Config(String project, String baseURL, Long total) {
		this.project = project;
		this.baseURL = baseURL;
		this.total = total;
		this.begin = null;
		this.setJksPath(null);
	}
	
	
	/**
	 * @param total the total to set
	 */
	public void setTotal(Long total) {
		this.total = total;
	}


	/**
	 * @return the project
	 */
	public String getProject() {
		return project;
	}
	/**
	 * @return the baseURL
	 */
	public String getBaseURL() {
		return baseURL;
	}
	/**
	 * @return the total
	 */
	public Long getTotal() {
		return total;
	}

	/**
	 * @param begin the begin to set
	 */
	public void setBegin(Long begin) {
		this.begin = begin;
	}

	/**
	 * @return the begin
	 */
	public Long getBegin() {
		return begin;
	}

	/**
	 * @param jksPath the jksPath to set
	 */
	public void setJksPath(String jksPath) {
		this.jksPath = jksPath;
	}

	/**
	 * @return the jksPath
	 */
	public String getJksPath() {
		return jksPath;
	}
	
	
}
