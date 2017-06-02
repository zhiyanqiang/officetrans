package com.netpower.library.util.convert;

import java.io.File;

public abstract interface PDFConverter {
	public abstract boolean doc2pdf(File docFile, File pdfFile);
}
