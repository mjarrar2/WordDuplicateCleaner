import java.awt.HeadlessException;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.util.Iterator;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.TreeSet;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;

import Implication.Implication;

/**
 * @author Sina Institute - 2016
 * http://www.sina.birzeit.edu
 */

public class Main {

	public static void main(String args[]) {
		//--- Input and Output Files ---
		String dir = System.getProperty("user.dir");
		System.out.println("Working Directory : " + dir);
		String _inputFile = dir + "/input.txt";
		String _outputFile = dir + "/output.txt";
		System.out.println("input file name : " + _inputFile + "\noutput file name : " + _outputFile);

		//System.out.println(getImplicationWithPreferredWord("محمد", "أحمد", false, false, false, false));
		//System.out.println(duplicateCleaner("كَدٌّ | كَدّ", " | ", false, false, false, false));
		duplicateCleanerFromFile(_inputFile, _outputFile, " | ", false, false, false, false); //String _inputFile, String _outputFile, String delimiter, boolean ignoreLastLetterDiacs, boolean ignoreShadda, boolean ignorefirstLetterHamza, boolean ignoreAL
	}

	public static int checkImplication(String word1, String word2, boolean ignoreLastLetterDiacs, boolean ignoreShadda, boolean ignorefirstLetterHamza, boolean ignoreAL) {
		if(word1 == null || word1 == "" || word1.compareTo("") == 0 || word2 == null || word2 == "" || word2.compareTo("") == 0)
			return -1; //ERROR : Invalid input
		String tmpWord1 = word1, tmpWord2 = word2;
		if( ignoreLastLetterDiacs && (tmpWord1.endsWith("\u064E") || tmpWord1.endsWith("\u064F") || tmpWord1.endsWith("\u0650") || tmpWord1.endsWith("\u0652") 
			|| tmpWord1.endsWith("\u064B") || tmpWord1.endsWith("\u064C") || tmpWord1.endsWith("\u064D") || tmpWord1.endsWith("\u0640") || tmpWord1.endsWith("\u06EA")) ) {
			//Fatha//Dhama//Kasra//Sukun//Fathatan//Dhamatan//Kasratan//Tatwil ( ـ )//Arabic Empty Centre Low Stop 06EA ( ۪  )
			tmpWord1 = tmpWord1.substring(0, tmpWord1.length()-1);
		}
		if( ignoreShadda ) {
			tmpWord1 = tmpWord1.replaceAll("\u0651", ""); //Shadda
		}
		if( ignorefirstLetterHamza && tmpWord1.startsWith("\u0621") ) {
			tmpWord1 = tmpWord1.substring(1); //Hamza ( ء )
		}
		if( ignoreAL && tmpWord1.length() > 3 && tmpWord1.startsWith("\u0627\u0644") ) {
			//Remove ( ال ) from beginning of the word if the word is longer than 3 characters
			tmpWord1 = tmpWord1.substring(2);
		}
		
		if( ignoreLastLetterDiacs && (tmpWord2.endsWith("\u064E") || tmpWord2.endsWith("\u064F") || tmpWord2.endsWith("\u0650") || tmpWord2.endsWith("\u0652") 
			|| tmpWord2.endsWith("\u064B") || tmpWord2.endsWith("\u064C") || tmpWord2.endsWith("\u064D") || tmpWord2.endsWith("\u0640") || tmpWord2.endsWith("\u06EA")) ) {
			//Fatha//Dhama//Kasra//Sukun//Fathatan//Dhamatan//Kasratan//Tatwil ( ـ )//Arabic Empty Centre Low Stop 06EA ( ۪  )
			tmpWord2 = tmpWord2.substring(0, tmpWord2.length()-1);
		}
		if( ignoreShadda ) {
			tmpWord2 = tmpWord2.replaceAll("\u0651", ""); //Shadda
		}
		if( ignorefirstLetterHamza && tmpWord2.startsWith("\u0621") ) {
			tmpWord2 = tmpWord2.substring(1); //Hamza ( ء )
		}
		if( ignoreAL && tmpWord2.length() > 3 && tmpWord2.startsWith("\u0627\u0644") ) {
			//Remove ( ال ) from beginning of the word if the word is longer than 3 characters
			tmpWord2 = tmpWord2.substring(2);
		}

		Implication j = new Implication(tmpWord1, tmpWord2);
		String[] result = j.getImplicationDistance().split("\t");
		//"[Word 1]\t[Word 2]\t[Implication Result]\t[Result Certainty]\t[Implication Score]\t[Implication Distance]\t[Count of Conflicts]"
		//System.out.println(tmpWord1 + "\t" + tmpWord2 + "\t" + result[0] + "\t" + result[1] + "\t" + result[2] + "\t" + result[3] + "\t" + result[4]);

		//Word 1 implies Word 2 ---> REMOVE Word 1, but merge the diacritics on the LAST letter only
		if(result[2].equals("1"))
			return 1;

		//Word 2 implies Word 1 ---> REMOVE Word 2, but merge the diacritics on the LAST letter only
		if(result[2].equals("2"))
			return 2;

		//The words are an exact match ---> The preferred one which ends with either Fatha or Dhamatan
		if(result[2].equals("3")) {//result[2].equals("3") && result[3].equals("0")
			if(word1.endsWith("\u064E") || word1.endsWith("\u064C"))
				return 2; //REMOVE Word 2
			return 1;
		}

		//SCORE=0 and CONFLICTS=0 then the words imply each other ---> We merge all diacritics
		if(result[2].equals("0") && result[4].equals("0"))
			return 3;

		//Uncertain ---> KEEP BOTH WORDS
		return 0;
		/*
		score 0 and conflicts !=0 when exact same letters but with conflicting diacs.
		distance is calculated when no conflicts but one word has diacs where the other doesn't.
		*/
	}

	public static String getImplicationWithPreferredWord(String word1, String word2, boolean ignoreLastLetterDiacs, boolean ignoreShadda, boolean ignorefirstLetterHamza, boolean ignoreAL) {
		if(word1 == null || word1 == "" || word1.compareTo("") == 0 || word2 == null || word2 == "" || word2.compareTo("") == 0)
			return "ERROR : Invalid input";
		String tmpWord1 = word1, tmpWord2 = word2;
		if( ignoreLastLetterDiacs && (tmpWord1.endsWith("\u064E") || tmpWord1.endsWith("\u064F") || tmpWord1.endsWith("\u0650") || tmpWord1.endsWith("\u0652") 
			|| tmpWord1.endsWith("\u064B") || tmpWord1.endsWith("\u064C") || tmpWord1.endsWith("\u064D") || tmpWord1.endsWith("\u0640") || tmpWord1.endsWith("\u06EA")) ) {
			//Fatha//Dhama//Kasra//Sukun//Fathatan//Dhamatan//Kasratan//Tatwil ( ـ )//Arabic Empty Centre Low Stop 06EA ( ۪  )
			tmpWord1 = tmpWord1.substring(0, tmpWord1.length()-1);
		}
		if( ignoreShadda ) {
			tmpWord1 = tmpWord1.replaceAll("\u0651", ""); //Shadda
		}
		if( ignorefirstLetterHamza && tmpWord1.startsWith("\u0621") ) {
			tmpWord1 = tmpWord1.substring(1); //Hamza ( ء )
		}
		if( ignoreAL && tmpWord1.length() > 3 && tmpWord1.startsWith("\u0627\u0644") ) {
			//Remove ( ال ) from beginning of the word if the word is longer than 3 characters
			tmpWord1 = tmpWord1.substring(2);
		}
		
		if( ignoreLastLetterDiacs && (tmpWord2.endsWith("\u064E") || tmpWord2.endsWith("\u064F") || tmpWord2.endsWith("\u0650") || tmpWord2.endsWith("\u0652") 
			|| tmpWord2.endsWith("\u064B") || tmpWord2.endsWith("\u064C") || tmpWord2.endsWith("\u064D") || tmpWord2.endsWith("\u0640") || tmpWord2.endsWith("\u06EA")) ) {
			//Fatha//Dhama//Kasra//Sukun//Fathatan//Dhamatan//Kasratan//Tatwil ( ـ )//Arabic Empty Centre Low Stop 06EA ( ۪  )
			tmpWord2 = tmpWord2.substring(0, tmpWord2.length()-1);
		}
		if( ignoreShadda ) {
			tmpWord2 = tmpWord2.replaceAll("\u0651", ""); //Shadda
		}
		if( ignorefirstLetterHamza && tmpWord2.startsWith("\u0621") ) {
			tmpWord2 = tmpWord2.substring(1); //Hamza ( ء )
		}
		if( ignoreAL && tmpWord2.length() > 3 && tmpWord2.startsWith("\u0627\u0644") ) {
			//Remove ( ال ) from beginning of the word if the word is longer than 3 characters
			tmpWord2 = tmpWord2.substring(2);
		}

		Implication j = new Implication(tmpWord1, tmpWord2);
		String implicationResult = j.getImplicationDistance();
		String preferredWords = "";
		String[] result = implicationResult.split("\t");
		//"[Word 1]\t[Word 2]\t[Implication Result]\t[Result Certainty]\t[Implication Score]\t[Implication Distance]\t[Count of Conflicts]"
		//System.out.println(tmpWord1 + "\t" + tmpWord2 + "\t" + result[0] + "\t" + result[1] + "\t" + result[2] + "\t" + result[3] + "\t" + result[4]);

		//[SCORE=1]: Word 1 implies Word 2 ---> REMOVE Word 1, but merge the diacritics on the LAST letter only
		if(result[2].equals("1"))
			preferredWords = tmpWord2;

		//[SCORE=2]: Word 2 implies Word 1 ---> REMOVE Word 2, but merge the diacritics on the LAST letter only
		else if(result[2].equals("2"))
			preferredWords = tmpWord1;

		//[SCORE=3]: The words are an exact match ---> The preferred one which ends with either Fatha or Dhamatan
		else if(result[2].equals("3")) {
			if(word1.endsWith("\u064E") || word1.endsWith("\u064C"))
				preferredWords = tmpWord1; //REMOVE Word 2
			preferredWords = tmpWord2;
		}

		//[SCORE=0 and CONFLICTS=0]: then the words imply each other ---> We merge all diacritics
		else if(result[2].equals("0") && result[4].equals("0"))
			preferredWords = tmpWord1 + "\t" + tmpWord2;

		//Uncertain ---> KEEP BOTH WORDS
		else
			preferredWords = tmpWord1 + "\t" + tmpWord2;
		/*
		score 0 and conflicts !=0 when exact same letters but with conflicting diacs.
		distance is calculated when no conflicts but one word has diacs where the other doesn't.
		*/
		implicationResult = implicationResult + "\t" + preferredWords;
		return implicationResult;
	}

	public static String duplicateCleaner(String _input, String delimiter, boolean ignoreLastLetterDiacs, boolean ignoreShadda, boolean ignorefirstLetterHamza, boolean ignoreAL) {
		MergeWordDiacritics _mergeWordDiacritics = new MergeWordDiacritics();
		WordCleaner _wordCleaner = new WordCleaner();
		int i=0;
		if(_input == null || _input == "" || _input.compareTo("") == 0)
			return "ERROR : Invalid input";
		String escapedDelimiter = "\\" + delimiter.trim();
		//Convert to UTF8 for Arabic support
		String _inputUTF8 = null;
		byte[] utf8Bytes;
		try {
			utf8Bytes = _input.trim().getBytes("UTF8");
			_inputUTF8 = new String(utf8Bytes, "UTF8");
		} catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		//Remove extra whitespaces
		_inputUTF8 = _inputUTF8.replaceAll("\\s+"," ").trim();
		/*_inputUTF8 = _inputUTF8.replaceAll(" "+escapedDelimiter+" ", escapedDelimiter);
		_inputUTF8 = _inputUTF8.replaceAll(" "+escapedDelimiter, escapedDelimiter);
		_inputUTF8 = _inputUTF8.replaceAll(escapedDelimiter+" ", escapedDelimiter);*/

		String[] tmpWords = _inputUTF8.split(escapedDelimiter);//Arrays.asList(_inputUTF8.split(escapedDelimiter));
		LinkedList<String> words = new LinkedList<String>();
		for(String w : tmpWords)
			if(!words.contains( _wordCleaner.wordCleaner(w.trim()) ))
				words.add( _wordCleaner.wordCleaner(w.trim()) );
		tmpWords = null;

		String word1, word2;
		boolean mergedFlag;
		for(i=0; i<words.size(); i++) {
			word1 = words.get(i);
			mergedFlag = false;
			for(int j=i+1; j<words.size() && !mergedFlag; j++) {
				word2 = words.get(j);
				int res = checkImplication(word1, word2, ignoreLastLetterDiacs, ignoreShadda, ignorefirstLetterHamza, ignoreAL);

				//Word 1 implies Word 2 ---> REMOVE Word 1, but merge the diacritics on the LAST letter only
				if(res==1) {
					String merged = _mergeWordDiacritics.mergeLastLetterDiacritics(word2, word1);
					words.set(j, merged);
					words.remove(i);
					mergedFlag = true;
					i = i-1;
				}

				//Word 2 implies Word 1 ---> REMOVE Word 2, but merge the diacritics on the LAST letter only
				if(res==2) {
					String merged = _mergeWordDiacritics.mergeLastLetterDiacritics(word1, word2);
					words.set(i, merged);
					words.remove(j);
					j = j-1;
				}

				//SCORE=0 and CONFLICTS=0 then the words imply each other ---> We merge all diacritics
				if(res==3) {
					String merged = _mergeWordDiacritics.mergeWordDiacritics(word1, word2);
					words.remove(i);
					mergedFlag = true;
					words.set(i, merged);
					i = i-1;
				}
			}
		}

		String result = "";
		int counter = 0;
		for(String o : words) {
			if(counter > 0)
				result += delimiter;
			result += o;
			counter++;
		}
		return result;
	}

	public static void duplicateCleanerFromFile(String _inputFile, String _outputFile, String delimiter, boolean ignoreLastLetterDiacs, boolean ignoreShadda, boolean ignorefirstLetterHamza, boolean ignoreAL) {
		BufferedWriter out = null;
		try {
			FileInputStream inputStream = new FileInputStream(_inputFile);
			InputStreamReader inputReader = new InputStreamReader(inputStream, "utf-8");
			BufferedReader in = new BufferedReader(inputReader);

			FileOutputStream outputStream = new FileOutputStream(_outputFile);
			OutputStreamWriter outputReader = new OutputStreamWriter(outputStream, "utf-8");
			out = new BufferedWriter(outputReader);
			int count = 1;
			String line;
			while ((line = in.readLine()) != null && line.compareTo("") != 0) {
				count++;
				//System.out.println("Line : " + count++);
				String[] fields = line.split("#");
				out.append(fields[0] + "#" + fields[1] + "#" + duplicateCleaner(fields[1], delimiter, ignoreLastLetterDiacs, ignoreShadda, ignorefirstLetterHamza, ignoreAL) + "\n");
				out.flush();
			}
			System.out.println("Lines read : " + (count-1));
			out.flush();
			in.close();
			out.close();
			System.out.println("Write to file " + _outputFile + " is  now complete");
		} catch (HeadlessException | IOException e2) {
			try {
				if (out != null)
					out.flush();
			} catch (IOException e) {
				System.out.println("File flush error");
			}
			System.out.println("File error or cancellation");
		}
	}
}