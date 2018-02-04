/*Yeshchenko Mykola, FI-2*/
package dictionary;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.PatternSyntaxException;

import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;


public class PDFReader {
	static int numOfDocs;
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
	
	static TreeMap<String,TreeMap<Integer, ArrayList<Integer>>> createDictionaryPositional(String file_name, String file_format, String file_path, List<String> words_one_book, TreeMap<String,TreeMap<Integer, ArrayList<Integer>>> wordAppearances) {
		Integer wordsPosCounter;
		try {
			numOfDocs = (int) Files.list(Paths.get(file_path)).count();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for (int i = 0; i < numOfDocs; i++){ 
			file_name = file_path + i + file_format;
			wordsPosCounter = 1;
			try {
			    String text = PDFReader.getText(new File(file_name));
			    text = text.replaceAll("[^a-zA-Z \t\n'-]+","");
			    text = text.toLowerCase(); 
			    //^ Locale.ENGLISH
			    try {
			       //words = text.split("\\s+");
			       words_one_book = Arrays.asList(text.split("\\s+"));
			    } catch (PatternSyntaxException ex) {
			        // 
			    }
			    for (String s : words_one_book) {
			    	//s.toLowerCase();
			    	//stem here
			    	Stemmer stmmr = new Stemmer();
			    	char[] s_arr = s.toCharArray();
			    	int s_length = s.length();
			    	stmmr.add(s_arr, s_length);
			    	stmmr.stem();
			    	s = stmmr.toString();
			    	//ArrayList<Integer> value = wordAppearances.get(s);
			    	TreeMap<Integer, ArrayList<Integer>> idsAndPositions = wordAppearances.get(s);
			    	if (idsAndPositions != null){
				    	ArrayList<Integer> positions = idsAndPositions.get(i);
					    if (positions != null /*&& !value.containsKey(wordsPosCounter)*/) {
					    	positions.add(wordsPosCounter);
					    } else { /*probably not needed */
					    	positions = new ArrayList<Integer>();
					    	positions.add(wordsPosCounter);
					    	idsAndPositions.put(i, positions);
					    }
			    	}
			    	else{
			    		idsAndPositions = new TreeMap<Integer, ArrayList<Integer>>();
			    		ArrayList<Integer> positions = new ArrayList<Integer>();
			    		positions.add(wordsPosCounter);
			    		idsAndPositions.put(i, positions);
			    		wordAppearances.put(s, idsAndPositions);
			    	}
				    wordsPosCounter++;
			    }

			} catch (IOException e) {
			    e.printStackTrace();
			}
		
		}
		return wordAppearances;
	}
	
	static TreeMap<String,TreeMap<Integer, ArrayList<Integer>>> createDictionaryBiwords(String file_name, String file_format, String file_path, List<String> words_one_book, TreeMap<String,TreeMap<Integer, ArrayList<Integer>>> wordAppearances) {
		Integer wordsPosCounter;
		try {
			numOfDocs = (int) Files.list(Paths.get(file_path)).count();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for (int i = 0; i < numOfDocs; i++){ 
			file_name = file_path + i + file_format;
			wordsPosCounter = 1;
			try {
			    String text = PDFReader.getText(new File(file_name));
			    text = text.replaceAll("[^a-zA-Z ]+","");
			    text = text.toLowerCase(); 
			    //^ Locale.ENGLISH
			    try {
			       //words = text.split("\\s+");
			       words_one_book = Arrays.asList(text.split("\\s+"));
			    } catch (PatternSyntaxException ex) {
			        // 
			    }
			    for (int j = 0; j < words_one_book.size()-1; j++){
			    	//if (words_one_book.size() > j + 1){
				    	
				    //for (String s : words_one_book) {
				    	//s.toLowerCase();
				    	//stem here
				    	String sOne = words_one_book.get(j);
				    	String sTwo = words_one_book.get(j+1);
				    	Stemmer stmmr = new Stemmer();
				    	char[] s_arr = sOne.toCharArray();
				    	int s_length = sOne.length();
				    	stmmr.add(s_arr, s_length);
				    	stmmr.stem();
				    	sOne = stmmr.toString();
				    	
				    	s_arr = sTwo.toCharArray();
				    	s_length = sTwo.length();
				    	stmmr.add(s_arr, s_length);
				    	stmmr.stem();
				    	sTwo = stmmr.toString();
				    	String s = sOne + " " + sTwo;
				    	//ArrayList<Integer> value = wordAppearances.get(s);
				    	TreeMap<Integer, ArrayList<Integer>> idsAndPositions = wordAppearances.get(s);
				    	if (idsAndPositions != null){
					    	ArrayList<Integer> positions = idsAndPositions.get(i);
						    if (positions != null /*&& !value.containsKey(wordsPosCounter)*/) {
						    	positions.add(wordsPosCounter);
						    } else { /*probably not needed */
						    	positions = new ArrayList<Integer>();
						    	positions.add(wordsPosCounter);
						    	idsAndPositions.put(i, positions);
						    }
				    	}
				    	else{
				    		idsAndPositions = new TreeMap<Integer, ArrayList<Integer>>();
				    		ArrayList<Integer> positions = new ArrayList<Integer>();
				    		positions.add(wordsPosCounter);
				    		idsAndPositions.put(i, positions);
				    		wordAppearances.put(s, idsAndPositions);
				    	}
					    wordsPosCounter++;
			    	//}
			    }

			} catch (IOException e) {
			    e.printStackTrace();
			}
		
		}
		return wordAppearances;
	}
	
	static TreeMap<String,ArrayList<Integer>> createMatrixPositional(TreeMap<String,TreeMap<Integer, ArrayList<Integer>>> wordAppearances){
		TreeMap<String,ArrayList<Integer>> matrix = new TreeMap<String,ArrayList<Integer>>();
	    //TreeMap<Integer, List<MySpecialClass>> copy = new TreeMap<Integer, List<MySpecialClass>>();
	    for (Entry<String, TreeMap<Integer, ArrayList<Integer>>> iter : wordAppearances.entrySet())
	    {
	    	TreeMap<Integer, ArrayList<Integer>> currPos = iter.getValue();
	    	matrix.put(iter.getKey(), new ArrayList<Integer>(currPos.keySet()));
	    }
		//matrix.putAll(wordAppearances);
		ArrayList<Integer> eachElKeys;
			for (String key : matrix.keySet()){
				eachElKeys = new ArrayList<Integer>(matrix.get(key));
				matrix.get(key).clear();
				for (int j = 0; j < numOfDocs; j++){
					if (eachElKeys.contains(j)){
						matrix.get(key).add(1);
					}
					else{
						matrix.get(key).add(0);
					}
				}
			}
		
		return matrix;	
	}
/*	
	static void outputToTxt(TreeMap<String,ArrayList<Integer>> wordAppearances){
	    FileWriter writer;
		try {
			writer = new FileWriter("output.txt");
			StringBuilder sb = new StringBuilder();
			
			for (Map.Entry<String,ArrayList<Integer>> entry : wordAppearances.entrySet()) {
			    //System.out.println(entry.getKey()+" : "+entry.getValue());
			//}
			//for (String key: wordAppearances.keySet()){
				
				//for (Integer s : wordAppearances.get(key)) {
			    for (Integer s : entry.getValue()){
				    sb.append(s);
				    sb.append("\t");
				}

	    		writer.write(entry.getKey() + " : " + sb.toString() + System.lineSeparator());
	    		sb.setLength(0);
	    }
	    writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Job done, bois.");
	}
*/	
	static ArrayList<Integer> And(TreeMap<String,ArrayList<Integer>> wordAppearances, String one, String two){
		ArrayList<Integer> res = new ArrayList<Integer>();
		
		ArrayList<Integer> resOne = Find(wordAppearances, one);
		ArrayList<Integer> resTwo = Find(wordAppearances, two);
		if (!resOne.isEmpty() && !resTwo.isEmpty()){
		    HashSet<Integer> matches = new HashSet<Integer>();
		    for (Integer i : resOne)
		    	matches.add(i);
		    for (Integer i : resTwo) {
		        if (matches.contains(i))
		        	res.add(i);   
		    }
		}
		return res;	
	}
	
	static ArrayList<Integer> Or(TreeMap<String,ArrayList<Integer>> wordAppearances, String one, String two){
		ArrayList<Integer> res = new ArrayList<Integer>();
		
		ArrayList<Integer> resOne = Find(wordAppearances, one);
		ArrayList<Integer> resTwo = Find(wordAppearances, two);
		if (!resOne.isEmpty() || !resTwo.isEmpty()){
		    HashSet<Integer> matches = new HashSet<Integer>();
		    for (Integer i : resOne)
		    	matches.add(i);
		    for (Integer i : resTwo) 
		    	matches.add(i);
		    
		    for (Integer i : matches){
		    	res.add(i);
		    }
		}
		return res;	
	}
	
	static ArrayList<Integer> Not(TreeMap<String,ArrayList<Integer>> wordAppearances, String one){
		ArrayList<Integer> res = new ArrayList<Integer>();
		
		ArrayList<Integer> resOne = Find(wordAppearances, one);
		if (!resOne.isEmpty()){
			for (int i = 0; i < numOfDocs; i++){
				if (!resOne.contains(i)) 
					res.add(i);
			}
		}
		else {
			for (int i = 0; i < numOfDocs; i++){
				res.add(i);
			}
		}
		return res;	
	}
	
	static ArrayList<Integer> Find(TreeMap<String,ArrayList<Integer>> wordAppearances, String key){
    	Stemmer stmmr = new Stemmer();
    	char[] s_arr = key.toCharArray();
    	int s_length = key.length();
    	stmmr.add(s_arr, s_length);
    	stmmr.stem();
    	key = stmmr.toString();

		ArrayList<Integer> res = wordAppearances.get(key);
		
		if (res == null){
			res = new ArrayList<Integer>();
		}
		
		
		return res;		
	}
	/*
	static ArrayList<Integer> FindBiwords(TreeMap<String,TreeMap<Integer, ArrayList<Integer>>> wordAppearances, String key){
		String toBeFound = key;
		toBeFound = toBeFound.replaceAll("[^a-zA-Z ]+","");
		toBeFound = toBeFound.toLowerCase(); 
	   List<String> allWordsToBeFound = Arrays.asList(toBeFound.split("\\s+"));

		for (String s : allWordsToBeFound){
	    	Stemmer stmmr = new Stemmer();
	    	char[] s_arr = s.toCharArray();
	    	int s_length = s.length();
	    	stmmr.add(s_arr, s_length);
	    	stmmr.stem();
	    	s = stmmr.toString();
	    	//allWordsToBeFound.
		}
		boolean found = false;
		for (int i = 0; i < allWordsToBeFound.size()-1; i++){
			
		}
		ArrayList<Integer> res = wordAppearances.get(key);
		
		if (res == null){
			res = new ArrayList<Integer>();
		}
		
		
		return res;		
	}
	*/
	static void outputToTxt(TreeMap<String,ArrayList<Integer>> wordAppearances){
	    FileWriter writer;
		try {
			writer = new FileWriter("output.txt");
			StringBuilder sb = new StringBuilder();
			
			TreeMap<String,ArrayList<Integer>> sortedMap = new TreeMap<>();
			sortedMap.putAll(wordAppearances);

			for (Map.Entry<String,ArrayList<Integer>> entry : sortedMap.entrySet()) {
			    for (Integer s : entry.getValue()){
				    sb.append(s);
				    sb.append("\t");
				}

	    		writer.write(entry.getKey() + " : " + sb.toString() + System.lineSeparator());
	    		sb.setLength(0);
	    }
	    writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println("Job done, bois.");
	}

}
