package com.netpower.library.util.file;

/**
 * 文件头信息枚举
 * 
 * @author 黄奎
 * 
 */
public enum FileTypeHeadInfo {
	
	/**
	 * MS Word/Excel. 2003
	 * 金山的 dot、wps也是这个头信息
	 */
	DOC_XLS_PPT("D0CF11E0"),
	
	/**
	 * MS Word/Excel. 2007
	 * 微软2007以后生成的Office文件都是采用zip压缩方式，所以文件头信息504B0304是一样的
	 */
	DOCX_XLSX_PPTX_ZIP("504B0304"),
	
	/**
	 * Adobe Acrobat.
	 */
	PDF("255044462D312E"),
	
	/**
	 * JEPG.
	 */
	JPEG("FFD8FF"),
	
	/**
	 * PNG.
	 */
	PNG("89504E47"),
	
	/**
	 * GIF.
	 */
	GIF("47494638"),
	
	/**
	 * ZIP Archive.
	 */
	ZIP("504B0304"),
	
	/**
	 * RAR Archive.
	 */
	RAR("52617221"),
	
	/**
	 * 7z压缩文件.
	 */
	Z7Z("377ABCAF271C"),
	
	/**
	 * LOG日志文件.
	 */
	LOG("2A2A2A2020496E73"),
	
	/**
	 * TIFF.
	 */
	TIFF("49492A00"),
	
	/**
	 * Windows Bitmap.
	 */
	BMP("424D"),
	
	/**
	 * CAD.
	 */
	DWG("41433130"),
	
	/**
	 * Adobe Photoshop.
	 */
	PSD("38425053"),
	
	/**
	 * Rich Text Format.
	 */
	RTF("7B5C727466"),
	
	/**
	 * XML.
	 */
	XML("3C3F786D6C"),
	
	/**
	 * HTML.
	 */
	HTML("68746D6C3E"),
	
	/**
	 * Email [thorough only].
	 */
	EML("44656C69766572792D646174653A"),
	
	/**
	 * Outlook Express.
	 */
	DBX("CFAD12FEC5FD746F"),
	
	/**
	 * Outlook (pst).
	 */
	PST("2142444E"),
	
	/**
	 * MS Access.
	 */
	MDB("5374616E64617264204A"),
	
	/**
	 * WordPerfect.
	 */
	WPD("FF575043"),
	
	/**
	 * Postscript.
	 */
	EPS("252150532D41646F6265"),
	
	/**
	 * Quicken.
	 */
	QDF("AC9EBD8F"),
	
	/**
	 * Windows Password.
	 */
	PWL("E3828596"),
	
	/**
	 * Wave.
	 */
	MP3("494433"),
	
	/**
	 * Wave.
	 */
	WAV("57415645"),
	
	/**
	 * AVI.
	 */
	AVI("41564920"),
	
	/**
	 * Real Audio.
	 */
	RAM("2E7261FD"),
	
	/**
	 * Real Media.
	 */
	RM("2E524D46"),
	
	/**
	 * Real Media RMVB.
	 */
	RMVB("2E524D46"),
	
	/**
	 * MPEG (mpg).
	 */
	MPG("000001BA"),
	
	/**
	 * Quicktime.
	 */
	MOV("6D6F6F76"),
	
	/**
	 * Windows Media.
	 */
	ASF("3026B2758E66CF11"),
	
	/**
	 * MIDI.
	 */
	MID("4D546864");
	
	private String value = "";
	
	/**
	 * Constructor.
	 * 
	 * @param type 
	 */
	private FileTypeHeadInfo(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
