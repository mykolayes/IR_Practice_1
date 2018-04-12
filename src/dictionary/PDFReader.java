/*Yeshchenko Mykola, FI-2*/
package dictionary;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Math.log;

import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;


public class PDFReader {
	static Integer numOfDocs;
	static int currBlock;
	static long maxBlockSize;
	
	static double titleWeight = 0.6;
	static double bodyWeight = 0.4;
	
	static ArrayList<File> filesNew = new ArrayList<File>();
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
	
	static void getFilesNames(String directoryName, ArrayList<File> files) {
	    File directory = new File(directoryName);

	    // get all the files from a directory
	    File[] fList = directory.listFiles();
	    for (File file : fList) {
	        if (file.isFile()) {
	            files.add(file);
	        } else if (file.isDirectory()) {
					getFilesNames(file.getAbsolutePath(), files);
	        }
	    }
	    numOfDocs = files.size();
	    return;
	}
	
	static void getFilesNamesNew(String directoryName) {
	    File directory = new File(directoryName);

	    // get all the files from a directory
	    File[] fList = directory.listFiles();
	    for (File file : fList) {
	        if (file.isFile()) {
	            filesNew.add(file);
	        } else if (file.isDirectory()) {
	        	getFilesNamesNew(file.getAbsolutePath());
	        }
	    }
	    numOfDocs = filesNew.size();
	    return;
	}
	
	static void getFilesNamesTwo(String directoryName, ArrayList<Path> pathsx) {
		try (Stream<Path> paths = Files.walk(Paths.get(directoryName))) {
		    paths
		        .filter(Files::isRegularFile)
		        .forEach(currPath -> pathsx.add(currPath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		numOfDocs = pathsx.size();
	    return;
	}
	
	static void outputChunk(TreeMap<String, ArrayList<Integer>> wordsOneFile){
	    FileWriter writer;
		try {
			writer = new FileWriter("chunk" + currBlock + ".txt");
			
			for (Entry<String, ArrayList<Integer>> entry : wordsOneFile.entrySet()) {
				writer.write(entry.getKey() + " : " + entry.getValue().toString() + System.lineSeparator());
	    }
	    writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}

	
	static void createTxtDictionary(ArrayList<File> textFilesNames){ 
		//1. Initialise variables.
		  String file_name = "", file_format = ".txt";
		  long approxSpaceUsed = 0;
		  currBlock = 1;
		  maxBlockSize = 5242880; // 1 mb 1000000, !!!50 kb = 51200 b!!! 300000, 524288000 = 500 mb, 52428800 = 50 mb, 524288000/2 = 262144000  //Integer
		  TreeMap<String, ArrayList<Integer>> wordsOneFile = new TreeMap<String, ArrayList<Integer>>();
		  char[] s_arr;
		  int s_length;
  
		  Stemmer stmmr = new Stemmer();
		//2. Read all the files and create index.
		  for (int i = 0; i < numOfDocs; i++){
			  file_name = textFilesNames.get(i).getAbsolutePath();

			  try (FileInputStream inputStream = new FileInputStream(file_name)){

			  try (Scanner sc = new Scanner(inputStream)) {
				  while(sc.hasNext()){
					  	String word = sc.next();
				          word = word.toLowerCase();
				          word = word.replaceAll("[^a-z ]+","");
	
					
				          if (!word.isEmpty()){
					          //+stem
				        	  stmmr = new Stemmer();
						    	s_arr = word.toCharArray();
						    	s_length = word.length();
						    	stmmr.add(s_arr, s_length);
						    	stmmr.stem();
						    	word = stmmr.toString();

						    	if (approxSpaceUsed < maxBlockSize){ //6553600 1073741824
				        	  //if (Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() > 100000000){ // > 100 mb ram for jvm left	  
				        	  //check and add if not present
				        		  if (!wordsOneFile.containsKey(word)){
				        			  ArrayList<Integer> keys = new ArrayList<Integer>(50);
				        			  keys.add(i);
				        			  wordsOneFile.put(word, keys);	
				        			  approxSpaceUsed += 3;
				        		  }
				        		  else {
				        			  ArrayList<Integer> keys = wordsOneFile.get(word);
				        			  if (!keys.contains(i)){
				        				  keys.add(i);
				        			  }
				        			  approxSpaceUsed += 1;
				        		  }
				        	  }
				        	  else{
				        		//output to the next file and switch to a new treemap
				        		  outputChunk(wordsOneFile);
				        		  
				        		  approxSpaceUsed = 0;
				        		  currBlock++;
				        		  wordsOneFile.clear();
				        		  
				        		  //System.gc();
				        		  
				        		  //+add last word to the new treemap
			        			  ArrayList<Integer> keys = new ArrayList<Integer>(50);
			        			  keys.add(i);
			        			  wordsOneFile.put(word, keys);
				        	  }
				          }
			    	  }
			      }
			  } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
		  }
	
		  outputChunk(wordsOneFile); //last part		  
		  //now we need to create 1 index from separate files (chunks).
		  
		  /* MERGE */
		  
		  //System.gc();
		  
		  //create arr for readers and open all the chunks

		  BufferedReader[] arrChunksReaders = new BufferedReader[currBlock];
		  
		  //set buffer size here maybe so that we can backtrack to the beginning of the line easily (may be really long one)
		  //maybe +2,147,483,647 is too much for my index. => 483647 - current size limit for one line of the index in chunks.
		  //8192 is the default one.
		  int BufferSize = 8192;//Integer.MAX_VALUE - 2147400000; // - 2147000000
			  try {
				  for (int i = 0; i < currBlock; i++){
					arrChunksReaders[i] = new BufferedReader(new FileReader("chunk" + (i+1) + ".txt"), BufferSize);
				  }

	
		 
		  //create writer for finalIndex
		  FileWriter writer;
			try {
				writer = new FileWriter("finalIndex.txt");
		
			//start checking all the files line by line to:
			//1) find the first term in alphabetical order
			//2) combine all the found data about given word
			//3) output it to the final index
			//4) and go to next line (if exists) in those blocks, where we read the data
			
			//1)
			//bool to check whether all blocks(chunks) were parsed
			boolean finished = false;
			String currWordToBeOutput = "", currDocIDsToBeOutput = "";
			ArrayList<Integer> currIDsToBeOutputArr = new ArrayList<Integer>();
			
			while (!finished){
				//find next word to output
				for (int i = 0; i < currBlock; i++){
					try {
						//read and return the cursor to the beginning of the line
						arrChunksReaders[i].mark(BufferSize);
						String currLine = arrChunksReaders[i].readLine();
						arrChunksReaders[i].reset();
						if (currLine != null && !currLine.isEmpty()){
							
							if (currWordToBeOutput.isEmpty()){
								//initiate next word if it can not be compared yet (every first currWord after the prev. one was output + first in general)
								currWordToBeOutput = currLine.substring(0, currLine.indexOf(":")-1);
							}
							else{
								String currWordToBeCompared = currLine.substring(0, currLine.indexOf(":")-1);
								if (currWordToBeCompared.compareTo(currWordToBeOutput) < 0){
									currWordToBeOutput = currWordToBeCompared;
								}
							}
						}
						else {
							if (i == currBlock -1 && currWordToBeOutput == ""){
								finished = true;
							}
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
							
				}
				//check whether there are words left in chunks (bool finished)
				//if yes (finished == false) => output next word and make the currWord empty once again, so we can get the next term
				//if no - close the writer and all the readers.
				if (finished == true){
					writer.close();
					
					for (int i = 0; i < currBlock; i++){
						arrChunksReaders[i].close();
					}
				}
				else{
					for (int i = 0; i < currBlock; i++){
						try {
							//read and return the cursor to the beginning of the line
							//(read line without reset somewhere after if needed)!
							arrChunksReaders[i].mark(BufferSize);
							String currLine = arrChunksReaders[i].readLine();
							arrChunksReaders[i].reset();
							if (currLine != null && !currLine.isEmpty()){
								String currWordToBeCompared = currLine.substring(0, currLine.indexOf(":")-1);
								if (currWordToBeOutput.compareTo(currWordToBeCompared) == 0){
									currDocIDsToBeOutput = currLine.substring(currLine.indexOf(":")+3, currLine.indexOf("]")); //comma-separated docIDs
									currDocIDsToBeOutput = currDocIDsToBeOutput.replace(",", "");
							    	String[] docIDs = currDocIDsToBeOutput.split(" ");
							    	ArrayList<Integer> currDocIDsArr = new ArrayList<Integer>(); //array of existing docIDs for the word
							    	for (int j = 0; j < docIDs.length; j++){
							    		currDocIDsArr.add(Integer.parseInt(docIDs[j]));
							    	}
							    	//if empty - fill, if not - merge
							    	if (currIDsToBeOutputArr.isEmpty()){
							    		for (int j : currDocIDsArr){
							    			currIDsToBeOutputArr.add(j); //j.clone()
							    		}
							    	}
							    	else {
							    		//merge in a 'sorted res' way
								    	Set<Integer> mergedIDsSet = new LinkedHashSet<Integer>(currIDsToBeOutputArr);
								    	mergedIDsSet.addAll(currDocIDsArr);
								    	currIDsToBeOutputArr = new ArrayList<Integer>(mergedIDsSet);
							    	}
							    	
							    	//move to the next line as current term in this block was successfully processed
							    	arrChunksReaders[i].readLine();
								}

							}

						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
					//output and switch the values back to empty
		    		writer.write(currWordToBeOutput + " : " + currIDsToBeOutputArr.toString() + System.lineSeparator()); //new word
		    		currWordToBeOutput = "";
		    		currDocIDsToBeOutput = "";
		    		//clear arr of docIDs
		    		currIDsToBeOutputArr.clear();

				}
				//end of 'while(!finished)'. 
			}
			
			
			
			//EOL (end of life) for the writer, and then - all the readers (presumably).
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  return;
	}
/*	
	static void outputChunkSuppressed(TreeMap<String, ArrayList<Integer>> wordsOneFile){
	    FileWriter writer;
		try {
			writer = new FileWriter("chunk_supp" + currBlock + ".txt");
			
			for (Entry<String, ArrayList<Integer>> entry : wordsOneFile.entrySet()) {
				writer.write(entry.getKey() + " : " + entry.getValue().toString() + System.lineSeparator());
	    }
	    writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}

    private static byte[] createPositionVBC(int n) {
        if (n == 0) {
            return new byte[]{0};
        }
        int i = (int) (log(n) / log(128)) + 1;
        byte[] rv = new byte[i];
        int j = i - 1;
        do {
            rv[j--] = (byte) (n % 128);
            n /= 128;
        } while (j >= 0);
        rv[i - 1] += 128;
        return rv;
    }
 */   
	static void outputDictAndIndexSuppressed(TreeMap<String, ArrayList<Integer>> wordsOneFile){
	    FileWriter writer, writer2, writer3;
	    String nextWord, nextPosInterval;
	    byte[] posVBC;
	    int wordIndex = 0;
		try {
			writer = new FileWriter("finDictSuppressed.txt");
			writer2 = new FileWriter("finDictSuppressedPointers.txt");
			writer3 = new FileWriter("finIndexSuppressed.txt");
			//createPositionVBC(2097152);
			for (Entry<String, ArrayList<Integer>> entry : wordsOneFile.entrySet()) {
			//for (int i = 0; i < wordsOneFile.size(); i++){
				//next = entry.getKey() + " : " + entry.getValue().toString();
				nextWord = entry.getKey(); //  - space is unneeded as we maintain words' length in separate file.
				writer.write(nextWord);
				writer2.write(nextWord.length() + " "); 
				writer3.write(wordIndex+":");
				ArrayList<Integer> positions = entry.getValue();
				//for (Integer position : entry.getValue()){
				if (positions.size() > 1){
					for (int i = 0; i < positions.size()-1; i++){
						//posVBC = createPositionVBC(position);
						nextPosInterval = Integer.toBinaryString(positions.get(i+1)-positions.get(i));
						//nextPosInterval = Integer.toString(214577, 2);
						//int amountOfEightBitSeq = nextPosInterval.length() / 7;
						String allParts = "";
						boolean firstPart = true;
						while (nextPosInterval.length() > 7){						
							String currPart = nextPosInterval.substring(nextPosInterval.length()-7,nextPosInterval.length());
							nextPosInterval = nextPosInterval.substring(0,nextPosInterval.length()-7);
							if (firstPart){
								allParts = "1"+currPart;
								firstPart = false;
							}
							else{
								allParts = "0"+currPart+allParts;
							}
						}
						allParts = nextPosInterval + allParts;
						int neededLeadingZeros = 8 - nextPosInterval.length();
						for (int j = 0; j < neededLeadingZeros; j++){
							allParts = "0"+allParts;
						}
						if (firstPart){
							allParts = allParts.substring(1, allParts.length());
							allParts = "1"+allParts;
							//firstPart = false
						}
						writer3.write(allParts);
						/*
						for (int j = 0; j < posVBC.length; j++){
							writer3.write(posVBC[j]);
						}
						*/
					}
				}
				else{
					writer3.write("10000000"); //0-interval - this word was found only in 1 file total.
				}
				writer3.write(System.lineSeparator());
				wordIndex++;
	    }
	    writer.close();
	    writer2.close();
	    writer3.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}
	
	//@param ArrayList<File> textFilesNames - names of the files to be indexed.
	static void createTxtDictionarySuppressed(ArrayList<File> textFilesNames){
		  String file_name = "", file_format = ".txt";
		
		 //1. Initialise vars.
		  long approxSpaceUsed = 0;
		  currBlock = 1;
		  maxBlockSize = 5242880; // 1 mb 1000000, !!!50 kb = 51200 b!!! 300000, 524288000 = 500 mb, 52428800 = 50 mb, 524288000/2 = 262144000  //Integer
		  TreeMap<String, ArrayList<Integer>> wordsOneFile = new TreeMap<String, ArrayList<Integer>>();
		  char[] s_arr;
		  int s_length;
		  
		  Stemmer stmmr = new Stemmer();
		//2. Read all the files and create index.
		  for (int i = 0; i < numOfDocs; i++){

			  file_name = textFilesNames.get(i).getAbsolutePath();

			  try (FileInputStream inputStream = new FileInputStream(file_name)){

			  try (Scanner sc = new Scanner(inputStream)) {
				  while(sc.hasNext()){
					  	String word = sc.next();
				          word = word.toLowerCase();
				          word = word.replaceAll("[^a-z ]+","");
	
					
				          if (!word.isEmpty()){
					          //+stem
				        	  stmmr = new Stemmer();
						    	s_arr = word.toCharArray();
						    	s_length = word.length();
						    	stmmr.add(s_arr, s_length);
						    	stmmr.stem();
						    	word = stmmr.toString();
						    	//if (approxSpaceUsed < maxBlockSize){
				        	  //check and add if not present
				        		  if (!wordsOneFile.containsKey(word)){
				        			  ArrayList<Integer> keys = new ArrayList<Integer>(50);
				        			  keys.add(i);
				        			  wordsOneFile.put(word, keys);	
				        			  approxSpaceUsed += 3;
				        		  }
				        		  else {
				        			  ArrayList<Integer> keys = wordsOneFile.get(word);
				        			  if (!keys.contains(i)){
				        				  keys.add(i);
				        			  }
				        			  approxSpaceUsed += 1;
				        		  }
				        	  //}
						    	/*
				        	  else{
				        		//output to the next file and switch to a new treemap
				        		  outputChunkSuppressed(wordsOneFile);
	
				        		  approxSpaceUsed = 0;
				        		  currBlock++;
				        		  wordsOneFile.clear();
				        		  //wordsOneFile = new TreeMap<String, ArrayList<Integer>>();
				        		  
				        		  //System.gc();
				        		  
				        		  //+add last word to the new treemap
			        			  ArrayList<Integer> keys = new ArrayList<Integer>(50);
			        			  keys.add(i);
			        			  wordsOneFile.put(word, keys);
				        	  }
				        	  */
				          }
			    	  }
			      }
			  } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
		  }
	
		  outputDictAndIndexSuppressed(wordsOneFile); //last part		  
		  //now we need to create 1 index from separate files (chunks).
		  
		  /* MERGE */
/*		  
		  //System.gc();
		  
		  //create arr for readers and open all the chunks

		  BufferedReader[] arrChunksReaders = new BufferedReader[currBlock];
		  
		  //set buffer size here maybe so that we can backtrack to the beginning of the line easily (may be really long one)
		  //maybe +2,147,483,647 is too much for my index. => 483647 - current size limit for one line of the index in chunks.
		  //8192 is the default one.
		  int BufferSize = 8192;//Integer.MAX_VALUE - 2147400000; // - 2147000000
			  try {
				  for (int i = 0; i < currBlock; i++){
					arrChunksReaders[i] = new BufferedReader(new FileReader("chunk_supp" + (i+1) + ".txt"), BufferSize);
				  }

	
		 
		  //create writer for finalIndex
		  FileWriter writer;
			try {
				writer = new FileWriter("finalIndex_supp.txt");

		
			//start checking all the files line by line to:
			//1) find the first term in alphabetical order
			//2) combine all the found data about given word
			//3) output it to the final index
			//4) and go to next line (if exists) in those blocks, where we read the data
			
			//1)
			//an array of bools to indicate that those blocks were parsed
			//boolean[] chunksParsed = new boolean[currBlock];
			boolean finished = false;
			String currWordToBeOutput = "", currDocIDsToBeOutput = "";
			ArrayList<Integer> currIDsToBeOutputArr = new ArrayList<Integer>();
			
			while (!finished){
				//find next word to output
				for (int i = 0; i < currBlock; i++){
					try {
						//read and return the cursor to the beginning of the line
						arrChunksReaders[i].mark(BufferSize);
						String currLine = arrChunksReaders[i].readLine();
						arrChunksReaders[i].reset();
						if (currLine != null && !currLine.isEmpty()){ //TODO: tmp check if null needed
							
							if (currWordToBeOutput.isEmpty()){
								//initiate next word if it can not be compared yet (every first currWord after the prev. one was output + first in general)
								currWordToBeOutput = currLine.substring(0, currLine.indexOf(":")-1);
							}
							else{
								String currWordToBeCompared = currLine.substring(0, currLine.indexOf(":")-1);
								if (currWordToBeCompared.compareTo(currWordToBeOutput) < 0){
									currWordToBeOutput = currWordToBeCompared;
								}
							}
						}
						else {
							if (i == currBlock -1 && currWordToBeOutput == ""){
								finished = true;
							}
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
							
				}
				//check whether there are words left in chunks (bool finished)
				//if yes (finished == false) => output next word and make the currWord empty once again, so we can get the next term
				//if no - close the writer and all the readers.
				if (finished == true){
					writer.close();
					
					for (int i = 0; i < currBlock; i++){
						arrChunksReaders[i].close();
					}
				}
				else{
					for (int i = 0; i < currBlock; i++){
						try {
							//read and return the cursor to the beginning of the line
							//(read line without reset somewhere after if needed)!
							arrChunksReaders[i].mark(BufferSize);
							String currLine = arrChunksReaders[i].readLine();
							arrChunksReaders[i].reset();
							if (currLine != null && !currLine.isEmpty()){
								String currWordToBeCompared = currLine.substring(0, currLine.indexOf(":")-1);
								if (currWordToBeOutput.compareTo(currWordToBeCompared) == 0){
									currDocIDsToBeOutput = currLine.substring(currLine.indexOf(":")+3, currLine.indexOf("]")); //comma-separated docIDs
									currDocIDsToBeOutput = currDocIDsToBeOutput.replace(",", "");
							    	String[] docIDs = currDocIDsToBeOutput.split(" ");
							    	ArrayList<Integer> currDocIDsArr = new ArrayList<Integer>(); //array of existing docIDs for the word
							    	for (int j = 0; j < docIDs.length; j++){
							    		currDocIDsArr.add(Integer.parseInt(docIDs[j]));
							    	}
							    	//if empty - fill, if not - merge
							    	if (currIDsToBeOutputArr.isEmpty()){
							    		for (int j : currDocIDsArr){
							    			currIDsToBeOutputArr.add(j);
							    		}
							    	}
							    	else {
							    		
							    		//TODO: maybe check whether 2 arrays contain the same docIDs before merging them.
							    		
							    		//merge in a 'sorted res' way
								    	Set<Integer> mergedIDsSet = new LinkedHashSet<Integer>(currIDsToBeOutputArr);
								    	mergedIDsSet.addAll(currDocIDsArr);
								    	currIDsToBeOutputArr = new ArrayList<Integer>(mergedIDsSet);
							    	}
							    	
							    	//move to the next line as current term in this block was successfully processed
							    	arrChunksReaders[i].readLine();
								}
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
					//output and switch the values back to empty
		    		writer.write(currWordToBeOutput + " : " + currIDsToBeOutputArr.toString() + System.lineSeparator()); //new word
		    		currWordToBeOutput = "";
		    		currDocIDsToBeOutput = "";
		    		//clear arr of docIDs
		    		currIDsToBeOutputArr.clear();
				}
				//end of 'while(!finished)'. 
			}
			
			
			
			//EOL (end of life) for the writer, and then - all the readers (presumably).
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/
		  return;
	}
	
	static void createTxtDictionaryZoned(){ //ArrayList<File> textFilesNames
		  String file_name = "", file_format = ".txt";
		
		 //1. Initialise vars.
		  long approxSpaceUsed = 0;
		  currBlock = 1;
		  maxBlockSize = 5242880; // 1 mb 1000000, !!!50 kb = 51200 b!!! 300000, 524288000 = 500 mb, 52428800 = 50 mb, 524288000/2 = 262144000  //Integer
		  TreeMap<String, ArrayList<Integer>> wordsOneFile = new TreeMap<String, ArrayList<Integer>>();
		  char[] s_arr;
		  int s_length;
		  
		  Stemmer stmmrTitle = new Stemmer();
		  Stemmer stmmr = new Stemmer();
		//2. Read all the files and create index.
		  for (int i = 0; i < numOfDocs; i++){

			  file_name = filesNew.get(i).getAbsolutePath();

			  try (FileInputStream inputStream = new FileInputStream(file_name)){
				  
				  
			//Z1 - adding words from the title
				  List<String> wordsCurrTitle = new ArrayList<String>();
				  String currTitle = file_name.substring(file_name.lastIndexOf('\\')+1, file_name.lastIndexOf('.'));
				  currTitle = currTitle.replaceAll("[^a-z ]+"," "); //"[^a-zA-Z \t\n'-]+"," "
				  currTitle = currTitle.toLowerCase(); 
				    //^ Locale.ENGLISH
				    try {
				       //words = text.split("\\s+");
				       wordsCurrTitle = Arrays.asList(currTitle.split("\\s+"));
				    } catch (PatternSyntaxException ex) {
				        // 
				    }
				    for (String s : wordsCurrTitle) {
				    	if (!s.isEmpty()){
				    	  stmmrTitle = new Stemmer();
					    	s_arr = s.toCharArray();
					    	s_length = s.length();
					    	stmmrTitle.add(s_arr, s_length);
					    	stmmrTitle.stem();
					    	s = stmmrTitle.toString();
					    	s = s + "Z1";
					    	
			        		  if (!wordsOneFile.containsKey(s)){
			        			  ArrayList<Integer> keys = new ArrayList<Integer>(50);
			        			  keys.add(i);
			        			  wordsOneFile.put(s, keys);	
			        			  approxSpaceUsed += 3;
			        		  }
			        		  else {
			        			  ArrayList<Integer> keys = wordsOneFile.get(s);
			        			  if (!keys.contains(i)){
			        				  keys.add(i);
			        			  }
			        			  approxSpaceUsed += 1;
			        		  }
				    	}
				    }
				  

		    	

			//Z2 - adding words in file content
			  try (Scanner sc = new Scanner(inputStream)) {
				  while(sc.hasNext()){
					  	String word = sc.next();
				          word = word.toLowerCase();
				          word = word.replaceAll("[^a-z ]+","");
	
					
				          if (!word.isEmpty()){
					          //+stem
				        	  stmmr = new Stemmer();
						    	s_arr = word.toCharArray();
						    	s_length = word.length();
						    	stmmr.add(s_arr, s_length);
						    	stmmr.stem();
						    	word = stmmr.toString();
						    	
						    	word = word + "Z2";
						    	
						    	//if (approxSpaceUsed < maxBlockSize){
				        	  //check and add if not present
				        		  if (!wordsOneFile.containsKey(word)){
				        			  ArrayList<Integer> keys = new ArrayList<Integer>(50);
				        			  keys.add(i);
				        			  wordsOneFile.put(word, keys);	
				        			  approxSpaceUsed += 3;
				        		  }
				        		  else {
				        			  ArrayList<Integer> keys = wordsOneFile.get(word);
				        			  if (!keys.contains(i)){
				        				  keys.add(i);
				        			  }
				        			  approxSpaceUsed += 1;
				        		  }
				        	  //}
						    	/*
				        	  else{
				        		//output to the next file and switch to a new treemap
				        		  outputChunkSuppressed(wordsOneFile);
	
				        		  approxSpaceUsed = 0;
				        		  currBlock++;
				        		  wordsOneFile.clear();
				        		  //wordsOneFile = new TreeMap<String, ArrayList<Integer>>();
				        		  
				        		  //System.gc();
				        		  
				        		  //+add last word to the new treemap
			        			  ArrayList<Integer> keys = new ArrayList<Integer>(50);
			        			  keys.add(i);
			        			  wordsOneFile.put(word, keys);
				        	  }
				        	  */
				          }
			    	  }
			      }
			  } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
		  }
	
		  outputChunk(wordsOneFile); //last part		  
		  return;
	}
	
	static TreeMap<String, ArrayList<Integer>> readIndexFromFile(){
		TreeMap<String, ArrayList<Integer>> readIndex = new TreeMap<String, ArrayList<Integer>>();
		
		try (BufferedReader indexReader = new BufferedReader(new FileReader("chunk1" + ".txt"))){
			

		
		String currWordToBeOutput = "", currDocIDsToBeOutput = "";
		//ArrayList<Integer> currIDsToBeOutputArr = new ArrayList<Integer>();
		
		String currLine = indexReader.readLine();
		while (currLine != null){// && !currLine.isEmpty()
			currWordToBeOutput = currLine.substring(0, currLine.indexOf(":")-1);
			currDocIDsToBeOutput = currLine.substring(currLine.indexOf(":")+3, currLine.indexOf("]")); //comma-separated docIDs
			currDocIDsToBeOutput = currDocIDsToBeOutput.replace(",", "");
	    	String[] docIDs = currDocIDsToBeOutput.split(" ");
	    	ArrayList<Integer> currDocIDsArr = new ArrayList<Integer>(); //array of existing docIDs for the word
	    	for (int j = 0; j < docIDs.length; j++){
	    		currDocIDsArr.add(Integer.parseInt(docIDs[j]));
	    	}
	    	readIndex.put(currWordToBeOutput, currDocIDsArr);
			currLine = indexReader.readLine();
		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return readIndex;
	}
	
	static ArrayList<String> decodeFileNames(ArrayList<Integer> finalRes){
		ArrayList<String> names = new ArrayList<String>();
		for (int i = 0; i < finalRes.size(); i++){
			names.add(filesNew.get(finalRes.get(i)).toString());
		}
		return names;
	}
	
	static private <K,V extends Comparable<? super V>> SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
	    SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
	        new Comparator<Map.Entry<K,V>>() {
	            @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
	                int res = e2.getValue().compareTo(e1.getValue());
	                return res != 0 ? res : 1;
	            }
	        }
	    );
	    sortedEntries.addAll(map.entrySet());
	    return sortedEntries;
	}
	
	static ArrayList<Integer> FindZoned(TreeMap<String, ArrayList<Integer>> wordAppearances, String key){ //ArrayList<String>
		String toBeFound = key;
		toBeFound = toBeFound.replaceAll("[^a-zA-Z ]+","");
		toBeFound = toBeFound.toLowerCase(); 
	    List<String> allWordsToBeFound = Arrays.asList(toBeFound.split("\\s+"));
	   
	 //Key - docID, Value - weight on [0-1] scale.
	   TreeMap<Integer, Double> res = new TreeMap<Integer, Double>();
	   ArrayList<Integer> finalRes = new ArrayList<Integer>();
	   ArrayList<String> docNames = new ArrayList<String>();
	   
	   if (allWordsToBeFound.size() == 0){
		   //return docNames;
		   return finalRes;
	   }
	   
	   double oneTitleWordWeight = titleWeight/allWordsToBeFound.size();
	   double oneBodyWordWeight = bodyWeight/allWordsToBeFound.size();
	   
	   for (int i = 0; i < allWordsToBeFound.size(); i++){
		   String s = allWordsToBeFound.get(i);
	    	Stemmer stmmr = new Stemmer();
	    	char[] s_arr = s.toCharArray();
	    	int s_length = s.length();
	    	stmmr.add(s_arr, s_length);
	    	stmmr.stem();
	    	s = stmmr.toString();
	    	allWordsToBeFound.set(i, s);
		}	
		
		for (int i = 0; i < allWordsToBeFound.size(); i++){
			String currWordToBeFoundZ1 = allWordsToBeFound.get(i) + "Z1";			
			String currWordToBeFoundZ2 = allWordsToBeFound.get(i) + "Z2";
				//getting docIDs for given zones and analysing them
			    	ArrayList<Integer> idsZ1 = wordAppearances.get(currWordToBeFoundZ1);
			    	ArrayList<Integer> idsZ2 = wordAppearances.get(currWordToBeFoundZ2);
			    	//Z1
			    	if (idsZ1 != null){
			    		for (int j = 0; j < idsZ1.size(); j++){
			    			Integer currDocID = idsZ1.get(j);
			    			if (!res.containsKey(currDocID)){
			    				res.put(currDocID, oneTitleWordWeight);
			    			}
			    			else {
			    				double changedCoeff = res.get(currDocID);
			    				changedCoeff += oneTitleWordWeight;
			    				res.put(currDocID, changedCoeff);
			    			}
			    		}			    		
			    	}
			    	//Z2
			    	if (idsZ2 != null){
			    		for (int j = 0; j < idsZ2.size(); j++){
			    			Integer currDocID = idsZ2.get(j);
			    			if (!res.containsKey(currDocID)){
			    				res.put(currDocID, oneBodyWordWeight);
			    			}
			    			else {
			    				double changedCoeff = res.get(currDocID);
			    				changedCoeff += oneBodyWordWeight;
			    				res.put(currDocID, changedCoeff);
			    			}
			    		}			    		
			    	}
		}
		
		SortedSet<Entry<Integer, Double>> sortedRes = entriesSortedByValues(res);
		//sort by Keys and return (first 10) most relevant docIDs.
		int maxRelevantCounter = 0;
			for (Entry<Integer, Double> entry : sortedRes){
				if (maxRelevantCounter > 9){
					break;
				}
				finalRes.add(entry.getKey());
				maxRelevantCounter++;
			}
			
			//docNames = decodeFileNames(finalRes);
		return finalRes;	
			//return docNames;
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
	
	static TreeMap<String,TreeMap<Integer, ArrayList<Integer>>> createDictionaryPositionalReversed(String file_name, String file_format, String file_path, List<String> words_one_book, TreeMap<String,TreeMap<Integer, ArrayList<Integer>>> wordAppearances) {
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
			    	s = new StringBuilder(s).reverse().toString();
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
	
	static TreeMap<String,TreeMap<Integer, ArrayList<Integer>>> createDictionaryBiwordsReversed(String file_name, String file_format, String file_path, List<String> words_one_book, TreeMap<String,TreeMap<Integer, ArrayList<Integer>>> wordAppearances) {
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
				    	s = new StringBuilder(s).reverse().toString();
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
	

	static ArrayList<Integer> FindBiwordsPhrasal(TreeMap<String,TreeMap<Integer, ArrayList<Integer>>> wordAppearances, String key){
		String toBeFound = key;
		toBeFound = toBeFound.replaceAll("[^a-zA-Z ]+","");
		toBeFound = toBeFound.toLowerCase(); 
	   List<String> allWordsToBeFound = Arrays.asList(toBeFound.split("\\s+"));

	   for (int i = 0; i < allWordsToBeFound.size(); i++){
		   String s = allWordsToBeFound.get(i);
	    	Stemmer stmmr = new Stemmer();
	    	char[] s_arr = s.toCharArray();
	    	int s_length = s.length();
	    	stmmr.add(s_arr, s_length);
	    	stmmr.stem();
	    	s = stmmr.toString();
	    	allWordsToBeFound.set(i, s);
		}
		ArrayList<Integer> res = new ArrayList<Integer>();
		//boolean found = false;
		for (int i = 0; i < allWordsToBeFound.size() - 1; i++){
			ArrayList<Integer> resOneQuery = new ArrayList<Integer>();
			//resOneQuery = Find(wordAppearances, allWordsToBeFound.get(i));
			ArrayList<Integer> resTmp = new ArrayList<Integer>();
			String currBiWordToBeFound = allWordsToBeFound.get(i) + " " + allWordsToBeFound.get(i+1);
			    	TreeMap<Integer, ArrayList<Integer>> idsAndPositions = wordAppearances.get(currBiWordToBeFound);
			    	if (idsAndPositions != null){
				    	resOneQuery.addAll(idsAndPositions.keySet());
				    	if (!res.isEmpty()){
					    	for (int j = 0; j < numOfDocs; j++){
					    		if(res.contains(j) && resOneQuery.contains(j)){
					    			resTmp.add(j);
					    		}
					    	}
				    	}
				    	else {
				    		resTmp = resOneQuery;
				    	}
				//res.addAll(idsAndPositions.keySet());
				    	res = resTmp;
			    	}
		}
		//ArrayList<Integer> res = wordAppearances.get(key);
		
		return res;		
	}
	
	static ArrayList<Integer> FindPositionalPhrasal(TreeMap<String,TreeMap<Integer, ArrayList<Integer>>> wordAppearances, String key){
		String toBeFound = key;
		toBeFound = toBeFound.replaceAll("[^a-zA-Z ]+","");
		toBeFound = toBeFound.toLowerCase(); 
	   List<String> allWordsToBeFound = Arrays.asList(toBeFound.split("\\s+"));

	   for (int i = 0; i < allWordsToBeFound.size(); i++){
		   String s = allWordsToBeFound.get(i);
	    	Stemmer stmmr = new Stemmer();
	    	char[] s_arr = s.toCharArray();
	    	int s_length = s.length();
	    	stmmr.add(s_arr, s_length);
	    	stmmr.stem();
	    	s = stmmr.toString();
	    	allWordsToBeFound.set(i, s);
		}
		ArrayList<Integer> res = new ArrayList<Integer>();
		//boolean found = false;
		for (int i = 0; i < allWordsToBeFound.size(); i++){
			ArrayList<Integer> resOneQuery = new ArrayList<Integer>();
			//resOneQuery = Find(wordAppearances, allWordsToBeFound.get(i));
			ArrayList<Integer> resTmp = new ArrayList<Integer>();
			String currWordToBeFound = allWordsToBeFound.get(i);
			    	TreeMap<Integer, ArrayList<Integer>> idsAndPositions = wordAppearances.get(currWordToBeFound);
			    	if (idsAndPositions != null){
				    	resOneQuery.addAll(idsAndPositions.keySet());
				    	if (!res.isEmpty()){
					    	for (int j = 0; j < numOfDocs; j++){
					    		if(res.contains(j) && resOneQuery.contains(j)){
					    			resTmp.add(j);
					    		}
					    	}
				    	}
				    	else {
				    		resTmp = resOneQuery;
				    	}
				//res.addAll(idsAndPositions.keySet());
				    	res = resTmp;
			    	}
		}
		//ArrayList<Integer> res = wordAppearances.get(key);
		
		return res;		
	}
	/*
	static ArrayList<Integer> FindBiwordsNear(TreeMap<String,TreeMap<Integer, ArrayList<Integer>>> wordAppearances, String key){
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
		ArrayList<Integer> res = new ArrayList<Integer>();
		//boolean found = false;
		for (int i = 0; i < allWordsToBeFound.size() - 1; i++){
			ArrayList<Integer> resOneQuery = new ArrayList<Integer>();
			//resOneQuery = Find(wordAppearances, allWordsToBeFound.get(i));
			ArrayList<Integer> resTmp = new ArrayList<Integer>();
			String currBiWordToBeFound = allWordsToBeFound.get(i) + " " + allWordsToBeFound.get(i+1);
			    	TreeMap<Integer, ArrayList<Integer>> idsAndPositions = wordAppearances.get(currBiWordToBeFound);
			    	resOneQuery.addAll(idsAndPositions.keySet());
			    	if (!res.isEmpty()){
				    	for (int j = 0; j < numOfDocs; j++){
				    		if(res.contains(j) && resOneQuery.contains(j)){
				    			resTmp.add(j);
				    		}
				    	}
			    	}
			    	else {
			    		resTmp = resOneQuery;
			    	}
			//res.addAll(idsAndPositions.keySet());
			    	res = resTmp;
		}
		//ArrayList<Integer> res = wordAppearances.get(key);
		
		return res;		
	}
	*/
	static TreeMap<Integer, ArrayList<Integer>> FindBiwordsNear(TreeMap<String,TreeMap<Integer, ArrayList<Integer>>> wordAppearances, String key, int margin){
		String toBeFound = key;
		toBeFound = toBeFound.replaceAll("[^a-zA-Z ]+","");
		toBeFound = toBeFound.toLowerCase(); 
	   List<String> allWordsToBeFound = Arrays.asList(toBeFound.split("\\s+"));
	   
	   //int amountOfWordsToBeFound = allWordsToBeFound.size();
	   //int wordFoundYesNo[] = new int[amountOfWordsToBeFound];
	   
	   for (int i = 0; i < allWordsToBeFound.size(); i++){
		   String s = allWordsToBeFound.get(i);
	    	Stemmer stmmr = new Stemmer();
	    	char[] s_arr = s.toCharArray();
	    	int s_length = s.length();
	    	stmmr.add(s_arr, s_length);
	    	stmmr.stem();
	    	s = stmmr.toString();
	    	allWordsToBeFound.set(i, s);
		}
		//TreeMap<Integer, ArrayList<Integer>> nothingFound = new TreeMap<Integer, ArrayList<Integer>>();
		//boolean found = false;
		//String currWordToBeFound = allWordsToBeFound.get(0);
		String currWordToBeFound = allWordsToBeFound.get(0) + " " + allWordsToBeFound.get(1);
		TreeMap<Integer, ArrayList<Integer>> idsAndPositions = wordAppearances.get(currWordToBeFound); //global res
		
		//if (idsAndPositions.isEmpty()){ 
		if (idsAndPositions != null){ //if idsAndPositions is empty => no docs found.
			return idsAndPositions; 
		}
		
		for (int i = 1; i < allWordsToBeFound.size()-1; i++){
			
			//ArrayList<Integer> resOneQuery = new ArrayList<Integer>();
			
			currWordToBeFound = allWordsToBeFound.get(i) + " " + allWordsToBeFound.get(i+1);
			TreeMap<Integer, ArrayList<Integer>> idsAndPositionsCurrent = new TreeMap<Integer, ArrayList<Integer>>(wordAppearances.get(currWordToBeFound)); //current i-th word res
			
			TreeMap<Integer, ArrayList<Integer>> idsAndPositionsNew = new TreeMap<Integer, ArrayList<Integer>>(); //global intersected with current word res
			
			for (int j=0; j < numOfDocs; j++){ 
				if (idsAndPositionsCurrent.containsKey(j) && idsAndPositions.containsKey(j)){
					ArrayList<Integer> valuesCurrent = new ArrayList<Integer>(idsAndPositionsCurrent.get(j));
					ArrayList<Integer> valuesGlobal = new ArrayList<Integer>(idsAndPositions.get(j));
					for(int k=0; k<valuesCurrent.size(); k++){
						int coordCurr = valuesCurrent.get(k);
						for (int l=0; l<valuesGlobal.size(); l++){
							int coordGlobal = valuesGlobal.get(l);
							if (Math.abs(coordGlobal - coordCurr) <= margin){
								if (!idsAndPositionsNew.containsKey(j)){
									ArrayList<Integer> newKey = new ArrayList<Integer>();
									newKey.add(coordGlobal);
									idsAndPositionsNew.put(j, newKey);
								} else {
									ArrayList<Integer> existingKey = idsAndPositionsNew.get(j);
									existingKey.add(coordGlobal);
								}
							}
						}
					}
				}
			}		
			
			idsAndPositions = idsAndPositionsNew;
			
			if (idsAndPositions.isEmpty()){ //if idsAndPositions is empty => no docs found.
				return idsAndPositions;
			}

		}
		return idsAndPositions;		
	}
	
	
	static TreeMap<Integer, ArrayList<Integer>> FindPositionalNear(TreeMap<String,TreeMap<Integer, ArrayList<Integer>>> wordAppearances, String key, int margin){
		String toBeFound = key;
		toBeFound = toBeFound.replaceAll("[^a-zA-Z ]+","");
		toBeFound = toBeFound.toLowerCase(); 
	   List<String> allWordsToBeFound = Arrays.asList(toBeFound.split("\\s+"));
	   
	   //int amountOfWordsToBeFound = allWordsToBeFound.size();
	   //int wordFoundYesNo[] = new int[amountOfWordsToBeFound];
	   
	   for (int i = 0; i < allWordsToBeFound.size(); i++){
		   String s = allWordsToBeFound.get(i);
	    	Stemmer stmmr = new Stemmer();
	    	char[] s_arr = s.toCharArray();
	    	int s_length = s.length();
	    	stmmr.add(s_arr, s_length);
	    	stmmr.stem();
	    	s = stmmr.toString();
	    	allWordsToBeFound.set(i, s);
		}
		//TreeMap<Integer, ArrayList<Integer>> nothingFound = new TreeMap<Integer, ArrayList<Integer>>();
		//boolean found = false;
		String currWordToBeFound = allWordsToBeFound.get(0);
		TreeMap<Integer, ArrayList<Integer>> idsAndPositions = wordAppearances.get(currWordToBeFound); //global res
		
		//if (idsAndPositions.isEmpty()){ 
		if (idsAndPositions == null){ //if idsAndPositions is empty => no docs found.
			return idsAndPositions; 
		}
		
		for (int i = 1; i < allWordsToBeFound.size(); i++){
			
			//ArrayList<Integer> resOneQuery = new ArrayList<Integer>();
			
			currWordToBeFound = allWordsToBeFound.get(i);
			TreeMap<Integer, ArrayList<Integer>> idsAndPositionsCurrent = new TreeMap<Integer, ArrayList<Integer>>(wordAppearances.get(currWordToBeFound)); //current i-th word res
			
			TreeMap<Integer, ArrayList<Integer>> idsAndPositionsNew = new TreeMap<Integer, ArrayList<Integer>>(); //global intersected with current word res
			
			for (int j=0; j < numOfDocs; j++){ 
				if (idsAndPositionsCurrent.containsKey(j) && idsAndPositions.containsKey(j)){
					ArrayList<Integer> valuesCurrent = new ArrayList<Integer>(idsAndPositionsCurrent.get(j));
					ArrayList<Integer> valuesGlobal = new ArrayList<Integer>(idsAndPositions.get(j));
					for(int k=0; k<valuesCurrent.size(); k++){
						int coordCurr = valuesCurrent.get(k);
						for (int l=0; l<valuesGlobal.size(); l++){
							int coordGlobal = valuesGlobal.get(l);
							if (Math.abs(coordGlobal - coordCurr) <= margin){
								if (!idsAndPositionsNew.containsKey(j)){
									ArrayList<Integer> newKey = new ArrayList<Integer>();
									newKey.add(coordGlobal);
									idsAndPositionsNew.put(j, newKey);
								} else {
									ArrayList<Integer> existingKey = idsAndPositionsNew.get(j);
									existingKey.add(coordGlobal);
								}
							}
						}
					}
				}
			}		
			
			idsAndPositions = idsAndPositionsNew;
			
			if (idsAndPositions.isEmpty()){ //if idsAndPositions is empty => no docs found.
				return idsAndPositions;
			}

		}
		return idsAndPositions;		
	}
	
	static TreeMap<String, ArrayList<String>> generateThreeGramIndices(TreeMap<String,TreeMap<Integer, ArrayList<Integer>>> wordAppearances){
		TreeMap<String, ArrayList<String>> threeGrams = new TreeMap<String, ArrayList<String>>();
		for (String word : wordAppearances.keySet()) {
				String wordLocal = "$" + word + "$";
				for (int i =0; i < wordLocal.length()-2; i++){
					//int endIndex = i+2;
					String partTBA = wordLocal.substring(i, i+3);
					if (threeGrams.containsKey(partTBA)){
						ArrayList<String> listOfWordsWithThreeGram = threeGrams.get(partTBA);
						if (!listOfWordsWithThreeGram.contains(word)){
							listOfWordsWithThreeGram.add(word);
						}
					}
					else {
						ArrayList<String> firstWordForNewKey = new ArrayList<String>();
						firstWordForNewKey.add(word);
						threeGrams.put(partTBA, firstWordForNewKey);
					}	
				}
		}
	return threeGrams;
	}
	
	static TreeMap<String, ArrayList<String>> generatePermutermIndices(TreeMap<String,TreeMap<Integer, ArrayList<Integer>>> wordAppearances){
		TreeMap<String, ArrayList<String>> permutermIndices = new TreeMap<String, ArrayList<String>>();
		for (String word : wordAppearances.keySet()) {
			if(word.length()>0){
				String wordLocal = word + "$";
				for (int i =0; i < wordLocal.length(); i++){
					//String wordLocal = word + "$";
					String partTBA = wordLocal.substring(0, i);
					String wordTBA = wordLocal.substring(i).concat(partTBA);
					if (permutermIndices.containsKey(wordTBA)){
						ArrayList<String> listOfWordsWithThreeGram = permutermIndices.get(wordTBA);
						if (!listOfWordsWithThreeGram.contains(word)){
							listOfWordsWithThreeGram.add(word);
						}
					}
					else {
						ArrayList<String> firstWordForNewKey = new ArrayList<String>();
						firstWordForNewKey.add(word);
						permutermIndices.put(wordTBA, firstWordForNewKey);
					}	
				}
			}
		}
	return permutermIndices;
	}
	
	static ArrayList<Integer> findPermutermVocab(TreeMap<String,TreeMap<Integer, ArrayList<Integer>>> wordAppearances, TreeMap<String, ArrayList<String>> permIndex, String toBeFound){
		//String resWord = "";
		ArrayList<Integer> foundInDocs = new ArrayList<Integer>();
		/*
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("Some text");
		stringBuilder.append("Some text");
		stringBuilder.append("Some text");

		String finalString = stringBuilder.toString();
		*/
		String toBeFoundLocal = "$" + toBeFound;
		int subStrBeginIndex = toBeFoundLocal.lastIndexOf("*"); //last joker
		if (subStrBeginIndex == -1){
			return foundInDocs;
		}
		String movedPart = toBeFoundLocal.substring(subStrBeginIndex, toBeFoundLocal.length());
		int subStrEndIndex = toBeFoundLocal.indexOf("*"); //first joker
		toBeFoundLocal = toBeFoundLocal.substring(0, subStrEndIndex); // was till subStrBeginIndex
		toBeFoundLocal = movedPart.concat(toBeFoundLocal).replace("*", "");
		//became 'n$m' form
		//now we need to get all keys from permIndex which match the given pattern, and then combine their values
		
		//ArrayList<String> finalWordsToBeFound = permIndex.get(toBeFoundLocal);
		ArrayList<String> finalWordsToBeFound = new ArrayList<String>();
		
		 for (Entry<String, ArrayList<String>> entry : permIndex.entrySet()) {
		    //for (String s : entry.getKey()) {
			 String currKey = entry.getKey();
		        if (currKey.startsWith(toBeFoundLocal)) {
		            //System.out.println(entry.getKey());
		        	ArrayList<String> currWordsToBeFound = entry.getValue();
		        	for (String s : currWordsToBeFound){
		        		if (!finalWordsToBeFound.contains(s)){
		        			finalWordsToBeFound.add(s);
		        		}
		        	}
		            //break;
		        }
		    //}
		}
		//post-filtration of the found res-strings
		 ArrayList<String> postFinalWordsToBeFound = new ArrayList<String>();
		 //postFinalWordsToBeFound = finalWordsToBeFound;
		 String toBeFoundFilter = toBeFound;
		 int numOfJokers = toBeFoundFilter.length() - toBeFoundFilter.replace("*", "").length();
		 if (numOfJokers > 1 && !finalWordsToBeFound.isEmpty()){ //check whether there were > 1 joker in the initial query + if we found any suitable words to post-filter
			 String begOfTheWord = toBeFoundFilter.substring(0, toBeFoundFilter.indexOf("*"));
			 String endOfTheWord = toBeFoundFilter.substring(toBeFoundFilter.lastIndexOf("*")+1, toBeFoundFilter.length());
				 for (int i = 0; i < finalWordsToBeFound.size(); i++){
					 String currFoundWord = finalWordsToBeFound.get(i);
					 String currFoundWordMiddle = currFoundWord.substring(begOfTheWord.length(), currFoundWord.length() - endOfTheWord.length());
							 
							 
					 int currJokerPos = toBeFoundFilter.indexOf("*");
					 int nextJokerPos = toBeFoundFilter.indexOf("*", currJokerPos+1); //can go to -1, but we don't care as we have a fixated lastJokerPos
					 int lastJokerPos = toBeFoundFilter.lastIndexOf("*");
					 while (currJokerPos != lastJokerPos){
						 String templateStr = toBeFoundFilter.substring(currJokerPos+1, nextJokerPos); //letters between curr and next jokers
						 if (currFoundWordMiddle.contains(templateStr)){
							 currFoundWordMiddle = currFoundWordMiddle.substring(currFoundWordMiddle.indexOf(templateStr)+templateStr.length(), currFoundWordMiddle.length());
							 
							 currJokerPos = toBeFoundFilter.indexOf("*", currJokerPos+1);
							 nextJokerPos = toBeFoundFilter.indexOf("*", nextJokerPos+1);
							 if (nextJokerPos == -1){
								 postFinalWordsToBeFound.add(currFoundWord);
							 }
						 }
						 else {
							 //postFinalWordsToBeFound.remove(i);
							 break;
						 }
					 }
				 }
				 //finalWordsToBeFound = new ArrayList<String>(postFinalWordsToBeFound);
				 finalWordsToBeFound = postFinalWordsToBeFound;
		 }
		
		
		if (!finalWordsToBeFound.isEmpty()){
			for (int i = 0; i < finalWordsToBeFound.size(); i++){
				ArrayList<Integer> foundInDocsCurrentWord = FindPositionalPhrasal(wordAppearances, finalWordsToBeFound.get(i));
				if (!foundInDocsCurrentWord.isEmpty()){
					for (int j = 0; j < foundInDocsCurrentWord.size(); j++){
						Integer docID = foundInDocsCurrentWord.get(j);
						if (!foundInDocs.contains(docID)){
							foundInDocs.add(docID);
						}
					}
				}
			}
		}
		
		
		
		//foundInDocs = FindPositionalPhrasal(wordAppearances, resWord);
		return foundInDocs;
	}
	
	static void outputToTxtPositional(TreeMap<String,TreeMap<Integer, ArrayList<Integer>>> wordAppearances){
	    FileWriter writer;
		try {
			writer = new FileWriter("output.txt");
			//StringBuilder sb = new StringBuilder();
			
			//TreeMap<String,ArrayList<Integer>> sortedMap = new TreeMap<>();
			//sortedMap.putAll(wordAppearances);
			Integer wordID = 0;
			for (Entry<String, TreeMap<Integer, ArrayList<Integer>>> entry : wordAppearances.entrySet()) {
				/*
			    for (Integer s : entry.getValue()){
				    sb.append(s);
				    sb.append("\t");
				}
				*/
				//TreeMap<Integer, ArrayList<Integer>> docIDPositions = new TreeMap<Integer, ArrayList<Integer>>(entry.getValue());
				//for (Entry<Integer, ArrayList<Integer>> entryInner : entry.entrySet()) {
					
				//}
				 //ArrayList<Integer> positions = docIDPositions.getValue();
				//String listString = .stream().map(Object::toString).collect(Collectors.joining(", "));
			    //String listString = String.join(", ", docIDPositions.getValue());
			    wordID++;
	    		writer.write(wordID + " " + entry.getKey() + " : " + entry.getValue().toString() + System.lineSeparator());
	    		//sb.setLength(0);
	    }
	    writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println("Job done, bois.");
	}

}
