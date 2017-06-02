package com.netpower.library.util.file;

/**
 * 文件类型
 * 
 * @author gzd
 * 
 */
public class File {

	private String filePath; // 文件物理路径
	private String fileUrl; // 文件虚拟路径
	private String fileShortUrl; // 文件缩略图路径
	
	private String fileName; // 文件原始名称
	private String fileSize; // 文件大小

	private String ico; // 文件样式图

	private Boolean isFile; // 是否为文件

	private FileOperatorType fileOperatorType; // 文件操作类型

	public File() {
	}

	public File(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return this.filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileUrl() {
		return this.fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public String getFileShortUrl() {
		return this.fileShortUrl;
	}

	public void setFileShortUrl(String fileShortUrl) {
		this.fileShortUrl = fileShortUrl;
	}

	public String getFileName() {
		if(this.fileName == null || "".equals(this.fileName)) {
			this.fileName = FileUtil.getFileName(this.filePath);
		}
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileSize() {
		return this.fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public String getIco() {
		this.ico = "other";
		if (getIsFile()) {
			this.ico = "dir";
		} else {
			String extName = FileUtil.getExtName(this.filePath).toLowerCase();
			if (FileType.DOCTYPES.contains(extName) || FileType.ZIPTYPES.contains(extName) || FileType.CODETYPES.contains(extName)) {
				this.ico = "wd";
				if("ppt".equals(extName) || "pptx".equals(extName) || "doc".equals(extName) || "docx".equals(extName)
						||"xls".equals(extName) || "xlsx".equals(extName) || "txt".equals(extName) || "pdf".equals(extName)
						|| "zip".equals(extName) || "rar".equals(extName)) {
					this.ico = extName;
				}
				if (extName.endsWith("x")) {
					this.ico = extName.substring(0, extName.length() - 1);
				}
			} else if (FileType.FLASHTYPES.contains(extName)) {
				this.ico = "flash";
			} else if (FileType.RADIOTYPES.contains(extName)) {
				this.ico = "sound";
			} else if (FileType.VIDEOTYPES.contains(extName)) {
				this.ico = "sp";
				if("avi".equals(extName) || "mp4".equals(extName) || "flv".equals(extName) || "wmv".equals(extName)
						|| "mov".equals(extName) || "mpg".equals(extName)) {
					this.ico = "video";
				}
			} else if (FileType.PICTYPES.contains(extName)) {
				this.ico = "pic";
			}
		}
		return this.ico;
	}

	public Boolean getIsFile() {
		this.isFile = FileUtil.isDir(this.filePath);
		return this.isFile;
	}

	public FileOperatorType getFileOperatorType() {
		this.fileOperatorType = FileOperatorType.OTHER;
		String extName = FileUtil.getExtName(this.filePath).toLowerCase();
		if (getIsFile()) {
			this.fileOperatorType = FileOperatorType.DIR;
		} else if(FileType.ZIPTYPES.contains(extName)) {
			this.fileOperatorType = FileOperatorType.COMPRESS;
		} else if (FileType.DOCTYPES.contains(extName) || FileType.CODETYPES.contains(extName)) {
			this.fileOperatorType = FileOperatorType.DOC;
		} else if (FileType.RADIOTYPES.contains(extName)
				|| FileType.VIDEOTYPES.contains(extName)
				|| FileType.FLASHTYPES.contains(extName)) {
			this.fileOperatorType = FileOperatorType.KINESCOPEMEDIA;
			if("mp4".equals(extName) || "flv".equals(extName)) {
				this.fileOperatorType = FileOperatorType.KINESCOPESWF;
			}
		} else if (FileType.PICTYPES.contains(extName)) {
			this.fileOperatorType = FileOperatorType.PIC;
		}
		return this.fileOperatorType;
	}

	public void setFileOperatorType(FileOperatorType fileOperatorType) {
		this.fileOperatorType = fileOperatorType;
	}
}
