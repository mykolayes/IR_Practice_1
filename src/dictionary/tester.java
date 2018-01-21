package dictionary;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

import org.apache.pdfbox.*;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import dictionary.PDFReader;


public class tester {
	static String file_name = "", file_format = ".pdf", file_path = "src/books/";
	//static String[] words;
	static List<String> words_one_book;
	static ArrayList<String> words = new ArrayList<String>();
	public static void main(String[] args) {
		for (int i = 0; i < 10; i++){ //< 10
			file_name = file_path + i + file_format;
		
			try {
			    String text = PDFReader.getText(new File(file_name));
			    //text = text.replace(".", "");
			    text = text.replaceAll("[^a-zA-Z ]+","");
			    text.toLowerCase(); //Locale.ENGLISH
			    try {
			       //words = text.split("\\s+");
			       words_one_book = Arrays.asList(text.split("\\s+"));
			    } catch (PatternSyntaxException ex) {
			        // 
			    }
			    words.addAll(words_one_book);
			    System.out.println("");
			    //System.out.println("Text in PDF: " + text);
			} catch (IOException e) {
			    e.printStackTrace();
			}
		
		}
		Set<String> set = new HashSet<>();
		set.addAll(words);
		words = new ArrayList<String>();
		words.addAll(set);
		Collections.sort(words);
		//Set<String> set = new HashSet<String>(Arrays.asList(words));
		//words = set.toArray(new String[set.size()]);
		//Arrays.sort(words);
	    System.out.println("");
	    
	    FileWriter writer;
		try {
			writer = new FileWriter("output.txt");
	    //for(String str: words) {
	    	for (int i = 0; i < words.size(); i ++){
	      writer.write(i + " " + words.get(i) + System.lineSeparator());
	    }
	    writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

}
