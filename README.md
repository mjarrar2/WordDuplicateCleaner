# WordDuplicateCleaner
WordDuplicateCleaner is a program that takes a set of delimited Arabic words and smartly removes duplicates, regardless on how they are diacritized. This program uses ImpliCheck program https://github.com/SinaInstitute/ImpliCheck

An online tool of this program:

http://ontology.birzeit.edu/tools/duplicateCleaner.html
OR to process large input go to: http://ontology.birzeit.edu/tools/duplicateCleanerFromFile.html

INPUT [input.txt]

This file contains sets of words to be processed by the program, each line represents a set of words to remove duplicates from. Each line must be structured as follows: [SetID#SetOfWords] where SetID and SetOfWords delimited by # sign. Words in SetOfWords are separated by any character provided by the user, the default is |

NOTE:

The program provides additional parameters to work with: [String delimiter, boolean ignoreLastLetterDiacs, boolean ignoreShadda, boolean ignorefirstLetterHamza, boolean ignoreAL]

OUTPUT [output.txt]

The output format is as follows: [SetID#SetOfWords#CleanedSetOfWords] where SetID and SetOfWords are the original user input, and CleanedSetOfWords is the set of words after removing duplicates
