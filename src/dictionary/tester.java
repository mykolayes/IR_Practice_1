package dictionary;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

import org.apache.pdfbox.*;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import dictionary.PDFReader;


public class tester {
	static String file_name = "", file_format = ".pdf", file_path = "src/books/";
	static List<String> words_one_book;
	//static ArrayList<String> words = new ArrayList<String>();
	
	static HashMap<String,ArrayList<Integer>> wordAppearances = new HashMap<String,ArrayList<Integer>>();
/*
	public static void main(String[] args) {
		words = PDFReader.createDictionary(file_name,  file_format,  file_path,  words_one_book,  words);
		Set<String> set = new HashSet<>();
		set.addAll(words);
		words = new ArrayList<String>();
		words.addAll(set);
		Collections.sort(words);
		//Set<String> set = new HashSet<String>(Arrays.asList(words));
		//words = set.toArray(new String[set.size()]);
		//Arrays.sort(words);
	    //System.out.println("");
		PDFReader.outputToTxt(words);
	}
*/
	public static void main(String[] args) {
		wordAppearances = PDFReader.createDictionary(file_name,  file_format,  file_path,  words_one_book,  wordAppearances);
		
		PDFReader.outputToTxt(wordAppearances);
		System.out.println("Output file created and filled.");
		
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter a word to be found: ");
		String toBeFound = scan.next(); //e.g. "episode/affectionate"
		ArrayList<Integer> foundIn = PDFReader.Find(wordAppearances, toBeFound);
		if (!foundIn.isEmpty()){
			System.out.println("Given word was found in following documents: " + foundIn);
		}
		else {
			System.out.println("Given word was not found.");
		}

		scan = new Scanner(System.in);
		System.out.println(" ~'AND'~ Enter a first word to be found: ");
		String toBeFoundOne = scan.next(); //e.g. "affection"
		scan = new Scanner(System.in);
		System.out.println(" ~'AND'~ Enter a second word to be found: ");
		String toBeFoundTwo = scan.next(); //e.g. "assign"
		foundIn = PDFReader.And(wordAppearances, toBeFoundOne, toBeFoundTwo);
		if (!foundIn.isEmpty()){
			System.out.println("Given words were found in following documents: " + foundIn);
		}
		else {
			System.out.println("Given words were not found.");
		}
		
		scan = new Scanner(System.in);
		System.out.println(" ~'OR'~ Enter a first word to be found: ");
		toBeFoundOne = scan.next(); //e.g. "august"
		scan = new Scanner(System.in);
		System.out.println(" ~'OR'~ Enter a second word to be found: ");
		toBeFoundTwo = scan.next(); //e.g. "augment"
		foundIn = PDFReader.Or(wordAppearances, toBeFoundOne, toBeFoundTwo);
		if (!foundIn.isEmpty()){
			System.out.println("Given words were found in following documents: " + foundIn);
		}
		else {
			System.out.println("Given words were not found.");
		}
		
	}

}
