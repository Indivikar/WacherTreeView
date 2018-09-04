package app.models;

public class ExistFiles {

	
	private String fileName;
	private String replaceOrIgnore;
	private String filePath;
	private String fileSize;
	
	public ExistFiles(String fileName, String replaceOrIgnore, String filePath, String fileSize) {
		this.fileName = fileName;
		this.replaceOrIgnore = replaceOrIgnore;
		this.filePath = filePath;
		this.fileSize = fileSize;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getReplaceOrIgnore() {
		return replaceOrIgnore;
	}

	public void setReplaceOrIgnore(String replaceOrIgnore) {
		this.replaceOrIgnore = replaceOrIgnore;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}



}
