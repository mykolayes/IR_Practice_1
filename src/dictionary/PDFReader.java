package dictionary;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;


public class PDFReader {
	/*
	public static String convertPDFToTxt(String filePath) {
        byte[] thePDFFileBytes = readFileAsBytes(filePath);
        PDDocument pddDoc = PDDocument.load(thePDFFileBytes);
        PDFTextStripper reader = new PDFTextStripper();
        String pageText = reader.getText(pddDoc);
        pddDoc.close();
        return pageText;
}

private static byte[] readFileAsBytes(String filePath) {
        FileInputStream inputStream = new FileInputStream(filePath);
        return IOUtils.toByteArray(inputStream);
}
*/
	static String getText(File pdfFile) throws IOException {
		PDDocument doc = new PDDocument();
		String txt;
		try {
	    doc = PDDocument.load(pdfFile);
	    PDFTextStripper retrievedText = new PDFTextStripper();
	    txt = retrievedText.getText(doc);
		}
		finally {
		   if( doc != null )
		   {
		      doc.close();
		   }
		}
	    return txt;
	}
}
