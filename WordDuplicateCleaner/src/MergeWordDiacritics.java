import java.awt.HeadlessException;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.util.Iterator;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.TreeSet;
import java.io.IOException;

public class MergeWordDiacritics {

	public MergeWordDiacritics() {
		
	}

	public String undiacritize(String word) {
		String input = word;
		input = input.replaceAll("\u064E", "");//Fatha
		input = input.replaceAll("\u064F", "");//Dhama
		input = input.replaceAll("\u0650", "");//Kasra
		input = input.replaceAll("\u0652", "");//Sukun
		input = input.replaceAll("\u064B", "");//Fathatan
		input = input.replaceAll("\u064C", "");//Dhamatan
		input = input.replaceAll("\u064D", "");//Kasratan
		input = input.replaceAll("\u0651", "");//Shadda
		input = input.replaceAll("\u0640", "");//Tatwil ( ـ )
		input = input.replaceAll("\u06EA", "");//Arabic Empty Centre Low Stop 06EA ( ۪  )

		//Replace ( أ ) ( آ ) ( إ ) with ( ا )
		input = input.replaceAll("\u0625", "\u0627");//Hamza_DOWN
		input = input.replaceAll("\u0622", "\u0627");//Madda ( آ )
		input = input.replaceAll("\u0623", "\u0627");//Hamza_UP

		input = input.replaceAll("\\s+"," ").trim();//Remove extra whitespaces
		//System.out.println("Word\t" + word + "\tUndiacritizedWord\t" + input);
		return input;
	}

	public boolean isLetter(char character) {
		//Fatha//Dhama//Kasra//Sukun//Fathatan//Dhamatan//Kasratan//Tatwil ( ـ )//Arabic Empty Centre Low Stop 06EA ( ۪  )
		//Shadda
		//( أ ) ( آ ) ( إ ) //Hamza_DOWN//Madda ( آ )//Hamza_UP
		char[] diacritics = {'\u064E','\u064F','\u0650','\u0652','\u064B','\u064C','\u064D','\u0640','\u06EA','\u0651','\u0625','\u0622','\u0623'};
		for(char c : diacritics)
			if(character == c)
				return false;
		return true;
	}

	public boolean isDiacritic(char character) {
		//Fatha//Dhama//Kasra//Sukun//Fathatan//Dhamatan//Kasratan//Tatwil ( ـ )//Arabic Empty Centre Low Stop 06EA ( ۪  )
		char[] diacritics = {'\u064E','\u064F','\u0650','\u0652','\u064B','\u064C','\u064D','\u0640','\u06EA'};
		for(char c : diacritics)
			if(character == c)
				return true;
		return false;
	}

	public boolean isShadda(char character) {
		//Shadda
		if(character == '\u0651')
			return true;
		return false;
	}

	public boolean isHamza(char character) {
		//( أ ) ( آ ) ( إ ) //Hamza_DOWN//Madda ( آ )//Hamza_UP
		if(character == '\u0625' || character == '\u0622' || character == '\u0623')
			return true;
		return false;
	}

	public char[][] createDiacriticsArray(String word, String undiacritizedWord) {
		char[] wordArray = word.toCharArray();
		char[] undiacritizedWordArray = undiacritizedWord.toCharArray();
		char[][] diacriticsArray = new char[4][undiacritizedWord.length()];
		int wordArrayPointer = 0, lettersPointer = 0;

		try {
			while(wordArrayPointer < word.length() && lettersPointer < undiacritizedWordArray.length) {
				//[0]:LETTERS [1]:DIACRITICS [2]:SHADDA [3]:HAMZA
				if(isLetter(wordArray[wordArrayPointer])) {
					diacriticsArray[0][lettersPointer] = wordArray[wordArrayPointer];
				}
				else if(isHamza(wordArray[wordArrayPointer])) {
					diacriticsArray[3][lettersPointer] = wordArray[wordArrayPointer];
					diacriticsArray[0][lettersPointer] = undiacritizedWordArray[lettersPointer];
				}
				else if(isShadda(wordArray[wordArrayPointer])) {
					diacriticsArray[2][lettersPointer] = wordArray[wordArrayPointer];
				}
				else if(isDiacritic(wordArray[wordArrayPointer])) {
					diacriticsArray[1][lettersPointer] = wordArray[wordArrayPointer];
					lettersPointer++;
				}
				//Check the following character, we jump to it if applicable
				int i = wordArrayPointer + 1;
				if( i < word.length() && !isDiacritic(wordArray[wordArrayPointer]) && (isLetter(wordArray[i]) || isHamza(wordArray[i])) )
					lettersPointer++;
				wordArrayPointer++;
			}
		} catch(Exception e) {
			System.out.println("ERROR creating diacritics array for word: ( " + word + " ) Make sure the Arabic word is spelled correctly, for example the word should not start with diacritic or have consecutive diacritics!");
		}
		return diacriticsArray;
	}

	public String mergeWordDiacritics(String word1, String word2) {
		//This method takes two similar Arabic words and merges their diacritics.
		//The words should NOT have conflicting diacritics!
		String undiacWord1 = undiacritize(word1);
		String undiacWord2 = undiacritize(word2);
		char[][] word1DiacArr = createDiacriticsArray(word1, undiacWord1);
		char[][] word2DiacArr = createDiacriticsArray(word2, undiacWord2);

		//Merge the diacritics
		if(!undiacWord1.equals(undiacWord2))
			return "ERROR merging word diacritics of word: ( " + word1 + " ) with word: ( " + word2 + " ). The words are not equal!";
		String result = "";
		try {
			for(int j = 0; j < undiacWord1.length(); j++) {
				//[0]:LETTERS//[3]:HAMZA
				if(word1DiacArr[3][j] != 0)
					result = result + word1DiacArr[3][j];
				else if(word2DiacArr[3][j] != 0)
					result = result + word2DiacArr[3][j];
				else
					result = result + word1DiacArr[0][j];

				//[2]:SHADDA
				if(word1DiacArr[2][j] != 0)
					result = result + word1DiacArr[2][j];
				else if(word2DiacArr[2][j] != 0)
					result = result + word2DiacArr[2][j];

				//[1]:DIACRITICS
				if(word1DiacArr[1][j] != 0)
					result = result + word1DiacArr[1][j];
				else if(word2DiacArr[1][j] != 0)
					result = result + word2DiacArr[1][j];
			}
		} catch(Exception e) {
			return "ERROR merging word diacritics of word: ( " + word1 + " ) with word: ( " + word2 + " )";
		}
		return result;
	}

	public String mergeLastLetterDiacritics(String preferredWord, String removedWord) {
		String result = preferredWord;
		//When preferredWord doesn't end with a diacritic, but removedWord has a diacritic on the last letter
		if( isLetter(preferredWord.charAt(preferredWord.length()-1)) && !isLetter(removedWord.charAt(removedWord.length()-1)) ) {
			result = result + removedWord.charAt(removedWord.length()-1);
		}
		return result;
	}

}