import java.util.Scanner;
import java.util.Iterator;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.regex.*;

public class WordCleaner {

	public WordCleaner() {
		
	}

	public String clean(String _word) {
		//Remove Tanween except from the end
		String result = _word;

		result = result.replaceAll("\u0640", "");//Tatwil ( ـ )
		result = result.replaceAll("\u06EA", "");//Arabic Empty Centre Low Stop 06EA ( ۪  )

		//Replace Diac+Shadda with Shadda+Diac
		result = result.replaceAll("\u064E\u0651", "\u0651\u064E");//FathaShadda
		result = result.replaceAll("\u064F\u0651", "\u0651\u064F");//DhamaShadda
		result = result.replaceAll("\u0650\u0651", "\u0651\u0650");//KasraShadda
		result = result.replaceAll("\u0652\u0651", "\u0651\u0652");//SukunShadda
		result = result.replaceAll("\u064B\u0651", "\u0651\u064B");//FathatanShadda
		result = result.replaceAll("\u064C\u0651", "\u0651\u064C");//DhamatanShadda
		result = result.replaceAll("\u064D\u0651", "\u0651\u064D");//KasratanShadda
		result = result.replaceAll("\u0651\u0651", "\u0651\u0651");//ShaddaShadda

		result = result.replaceAll("(\u064B)(?!$)", "");//Fathatan
		result = result.replaceAll("(\u064C)(?!$)", "");//Dhamatan
		result = result.replaceAll("(\u064D)(?!$)", "");//Kasratan

		return result;
	}

	public String wordCleaner(String _input) {
		String input = _input.replaceAll("\\s+"," ").trim();//Remove extra whitespaces
		String [] words = input.split(" ");//Split the array delimited by " "
		String result = "";//Initialize empty result String
		for(String word : words) {
			word = word.trim();//Trim leading and trailing whitespaces
			String cleanedWord = clean(word);
			if(cleanedWord != null && cleanedWord != "" && cleanedWord != " " && !cleanedWord.isEmpty())
				result = result + " " + cleanedWord;
		}
		result = result.replaceAll("\\s+"," ").trim();//Remove extra whitespaces
		return result;
	}

}