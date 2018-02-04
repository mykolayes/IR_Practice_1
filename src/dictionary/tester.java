/*Yeshchenko Mykola, FI-2*/
package dictionary;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.List;
import java.util.Scanner;

import dictionary.PDFReader;


public class tester {
	static String file_name = "", file_format = ".pdf", file_path = "src/books/";
	static List<String> words_one_book;
	//static ArrayList<String> words = new ArrayList<String>();
	
	static TreeMap<String,TreeMap<Integer, ArrayList<Integer>>> wordAppearances = new TreeMap<String,TreeMap<Integer, ArrayList<Integer>>>();
	static TreeMap<String,TreeMap<Integer, ArrayList<Integer>>> wordAppearancesTwo = new TreeMap<String,TreeMap<Integer, ArrayList<Integer>>>();
	static TreeMap<String, ArrayList<Integer>> wordAppearancesMatrix = new TreeMap<String, ArrayList<Integer>>();

	public static void main(String[] args) {
		//wordAppearances = PDFReader.createDictionaryPositional(file_name,  file_format,  file_path,  words_one_book,  wordAppearances);
		wordAppearancesTwo = PDFReader.createDictionaryBiwords(file_name,  file_format,  file_path,  words_one_book,  wordAppearancesTwo);
		
		wordAppearancesMatrix = PDFReader.createMatrixPositional(wordAppearances);
		/*
		PDFReader.outputToTxt(wordAppearances);
		System.out.println("Output file created and filled.");
		try(Scanner scan = new Scanner(System.in)){		
			System.out.println("Enter a word to be found: ");
			String toBeFound = scan.next(); //e.g. "episode/affectionate"
			 
			ArrayList<Integer> foundIn = PDFReader.Find(wordAppearances, toBeFound);
			if (!foundIn.isEmpty()){
				System.out.println("Given word was found in following documents: " + foundIn);
			}
			else {
				System.out.println("Given word was not found.");
			}
	
			System.out.println("Enter a word to be found: ");
			toBeFound = scan.next(); //e.g. "banker" - all but 7 and 9.
			 
			foundIn = PDFReader.Not(wordAppearances, toBeFound);
			if (!foundIn.isEmpty()){
				System.out.println("Given word was not found in following documents: " + foundIn);
			}
			else {
				System.out.println("Given word was found in all the documents.");
			}
	
			System.out.println(" ~'AND'~ Enter a first word to be found: ");
			String toBeFoundOne = scan.next(); //e.g. "affection"
			 
			System.out.println(" ~'AND'~ Enter a second word to be found: ");
			String toBeFoundTwo = scan.next(); //e.g. "assign"
			 
			foundIn = PDFReader.And(wordAppearances, toBeFoundOne, toBeFoundTwo);
			if (!foundIn.isEmpty()){
				System.out.println("Given words were found in following documents: " + foundIn);
			}
			else {
				System.out.println("Given words were not found.");
			}
			
			System.out.println(" ~'OR'~ Enter a first word to be found: ");
			toBeFoundOne = scan.next(); //e.g. "august" - 3, 4
			 
			System.out.println(" ~'OR'~ Enter a second word to be found: ");
			toBeFoundTwo = scan.next(); //e.g. "augment" - 5, 6, 7, 9
			 
			foundIn = PDFReader.Or(wordAppearances, toBeFoundOne, toBeFoundTwo);
			if (!foundIn.isEmpty()){
				System.out.println("Given words were found in following documents: " + foundIn);
			}
			else {
				System.out.println("Given words were not found.");
			}
		}
		System.out.println("End of the testing program. Thank you!");
		*/
	}

}
