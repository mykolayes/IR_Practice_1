/*Yeshchenko Mykola, FI-2*/
package dictionary;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TreeMap;
import java.util.List;
import java.util.Scanner;
import java.util.Map.Entry;

import dictionary.PDFReader;


public class tester {
	static String file_name = "", file_format = ".pdf", file_path = /*"src/books/"*/
			"C:/Users/Sergey/Downloads/Dev/IR/gutenberg_txt/gutenberg_txt/gutenberg/1/0/0/0/10007"
			//"C:/Users/Sergey/Downloads/Dev/IR/gutenberg_txt/gutenberg_txt/gutenberg/1/0"
			//"C:/Users/Nikolya/Downloads/IR/gutenberg_txt/gutenberg_txt/gutenberg/1/0/0/0/10007"
			//"C:/Users/Nikolya/Downloads/IR/gutenberg_txt"
			//"C:/Users/Nikolya/Downloads/IR/gutenberg_txt/gutenberg_txt/gutenberg/1/0/0/0"
			//"C:/Users/Nikolya/Downloads/IR/gutenberg_txt/gutenberg_txt/gutenberg/1/1/7/7" // /11775
			;
	
	/*
	static List<String> words_one_book;
	//static ArrayList<String> words = new ArrayList<String>();
	
	static TreeMap<String,TreeMap<Integer, ArrayList<Integer>>> wordAppearances = new TreeMap<String,TreeMap<Integer, ArrayList<Integer>>>();
	static TreeMap<String,TreeMap<Integer, ArrayList<Integer>>> wordAppearancesTwo = new TreeMap<String,TreeMap<Integer, ArrayList<Integer>>>();
	static TreeMap<String, ArrayList<Integer>> wordAppearancesMatrix = new TreeMap<String, ArrayList<Integer>>();
	static ArrayList<Integer> searchRes = new ArrayList<Integer>();
	static TreeMap<Integer, ArrayList<Integer>> searchResTwo = new TreeMap<Integer, ArrayList<Integer>>();
	static String toBeF;
	static TreeMap<String, ArrayList<String>> TriGramIndex = new TreeMap<String, ArrayList<String>>();
	static TreeMap<String, ArrayList<String>> PermutermIndex = new TreeMap<String, ArrayList<String>>();
	 */
	public static void main(String[] args) {
		//wordAppearances = PDFReader.createDictionaryPositional(file_name,  file_format,  file_path,  words_one_book,  wordAppearances);
		//wordAppearancesTwo = PDFReader.createDictionaryBiwords(file_name,  file_format,  file_path,  words_one_book,  wordAppearancesTwo);

		//wordAppearancesMatrix = PDFReader.createMatrixPositional(wordAppearances);
		/*
		PDFReader.outputToTxt(wordAppearances);
		System.out.println("Output file created and filled.");
		*/
		//try(Scanner scan = new Scanner(System.in)){		
			/*
			System.out.println("Enter a word to be found: ");
			String toBeFound = scan.next(); //e.g. "episode/affectionate"
			*/
			
			
			/** Biwords */
			//wordAppearancesTwo = PDFReader.createDictionaryBiwords(file_name,  file_format,  file_path,  words_one_book,  wordAppearancesTwo);
			/*
			System.out.println("Enter a phrase to be found: ");
			toBeF = scan.nextLine();
			searchRes = PDFReader.FindBiwordsPhrasal(wordAppearancesTwo, toBeF);
			if (!searchRes.isEmpty()){
				System.out.println("Given phrase was found in following documents: " + searchRes);
			}
			else {
				System.out.println("Given phrase was not found.");
			}
			*/
			
			/*
			System.out.println("Enter words to be found nearby: ");
			toBeF = scan.nextLine();
			System.out.println("Enter how close they should be to each other: ");
			int margin = scan.nextInt();
			searchResTwo = PDFReader.FindBiwordsNear(wordAppearancesTwo, toBeF, margin);
			for (int i = 0; i < searchResTwo.size(); i++){
				ArrayList<Integer> inThisDoc = new ArrayList<Integer>(searchResTwo.get(i));
				if (!inThisDoc.isEmpty()){
					System.out.println("Given words were found nearby in document #" + i + ": " + inThisDoc);
				}
				
				//else {
				//	System.out.println("Given words were not found in document #" + i + ".");
				//}
			}
			*/
			
			/** Positional */
			//wordAppearances = PDFReader.createDictionaryPositional(file_name,  file_format,  file_path,  words_one_book,  wordAppearances);
			
			/*
			System.out.println("Enter a phrase to be found: ");
			toBeF = scan.nextLine();
			searchRes = PDFReader.FindPositionalPhrasal(wordAppearances, toBeF);
			if (!searchRes.isEmpty()){
				System.out.println("Given phrase was found in following documents: " + searchRes);
			}
			else {
				System.out.println("Given phrase was not found.");
			}
			*/
			
			//PDFReader.outputToTxtPositional(wordAppearances);
		/*	
			//PRACTICE 4 3-GRAM & PERMUTERM INDICES + PERMUTERM INDEX JOKER SEARCH
			//TriGramIndex = PDFReader.generateThreeGramIndices(wordAppearances);
			PermutermIndex = PDFReader.generatePermutermIndices(wordAppearances);
			System.out.println("Enter a word with joker(-s) to be found: "); //g*ts*i
			toBeF = scan.nextLine();
			searchRes = PDFReader.findPermutermVocab(wordAppearances, PermutermIndex, toBeF); //"m*n"
			System.out.println("Given word was found in following documents: " + searchRes);
		*/	
			 //PRACTICE 3 TEST BLOCK FOR POSITIONAL 'NEAR' SEARCH
	/*		
			System.out.println("Enter words to be found nearby: ");
			toBeF = scan.nextLine();
			System.out.println("Enter proximity (how close they should be to each other): ");
			int margin = scan.nextInt();
			searchResTwo = PDFReader.FindPositionalNear(wordAppearances, toBeF, margin);
			for (int i = 0; i < searchResTwo.size(); i++){
				ArrayList<Integer> inThisDoc = new ArrayList<Integer>(searchResTwo.get(i));
				if (!inThisDoc.isEmpty()){
					System.out.println("Given words were found nearby in document #" + i + ": " + inThisDoc);
				}
				
				//else {
				//	System.out.println("Given words were not found in document #" + i + ".");
				//}
			}
	*/		
			 /**Practice 5 */
			/*
			boolean x1 = true;
			boolean x2 = false;
			System.out.println("true looks like: " + x1);
			System.out.println("false looks like: " + x2);
			
			String currWordToBeOutput = "";
			if (currWordToBeOutput.isEmpty()){
				System.out.println("Yeah, it is empty.");
			}
			*/
			/*
		
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
			System.out.println(timeStamp);
			
			ArrayList<File> textFilesNames = new ArrayList<File>();
			PDFReader.getFilesNames(file_path, textFilesNames);
			System.out.println("The amount of files to be indexed is: " + PDFReader.numOfDocs);
			
		    FileWriter writer;
			try {
				writer = new FileWriter("Stats" + ".txt");
					writer.write("Time of beginning: " + timeStamp + System.lineSeparator());
					writer.write("The amount of files to be indexed is (bytes): " + PDFReader.numOfDocs + System.lineSeparator());
		    writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			PDFReader.createTxtDictionary(textFilesNames); //textFilesNames
			//PDFReader.createTxtDictionary(pathsx);
			timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
			System.out.println(timeStamp);
			
			File finIndex = new File("finalIndex.txt");
			long indSize = finIndex.length();
			
			BufferedWriter writer2;
			try {
				writer2 = new BufferedWriter(new FileWriter("Stats" + ".txt", true));
				writer2.write("Time of ending: " + timeStamp + System.lineSeparator());
				writer2.write("Size of the final index file is: " + indSize);
				writer2.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		*/
			/**Practice 6 */
		/*
		ArrayList<File> textFilesNames = new ArrayList<File>();
		PDFReader.getFilesNames(file_path, textFilesNames);
		System.out.println("The amount of files to be indexed is: " + PDFReader.numOfDocs);
		PDFReader.createTxtDictionarySuppressed(textFilesNames); //textFilesNames
		
		//PDFReader.createTxtDictionary(textFilesNames); //textFilesNames
			*/
		/**Practice 7 */
		
		//ArrayList<File> textFilesNames = new ArrayList<File>();
		PDFReader.getFilesNamesNew(file_path); //, textFilesNames
		System.out.println("The amount of files to be indexed is: " + PDFReader.numOfDocs);
		PDFReader.createTxtDictionaryZoned(); //textFilesNames
		TreeMap<String, ArrayList<Integer>> myIndex = new TreeMap<String, ArrayList<Integer>>();
		myIndex = PDFReader.readIndexFromFile();
		ArrayList<Integer> res = PDFReader.FindZoned(myIndex, "heartbeating");
		System.out.println(res);
			
			
			
			 /*
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
			*/
		//} //close the scanner
		System.out.println("End of the testing program. Thank you!");
		
	}

}
