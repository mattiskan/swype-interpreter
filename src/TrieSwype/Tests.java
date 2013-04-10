package TrieSwype;

import java.io.File;
import java.io.FileFilter;
import java.util.Map;

public class Tests {
	private final static double NOT_FOUND = -1;
	public static void main(String[] args) {
		TestConfig config = new TestConfig();
		//config.showGraphics = true;
		//createTest(config);
		TestResult result = createTest(config);
		for (TestResult.WordResult word : result.allWords) {
			System.out.printf("%-25s %2.5f   %s\n", word.realWord+":",
					word.ratio, (word.ratio<1)?"PASS": "FAILED: "+ word.otherWord);
		}
		System.out.printf("Correct: %d Failed: %d Total: %d SuccessRatio: %f\n", result.getCorectWordCount(), 
				result.getFailedWordCount(),result.getTotalWordCount(), result.getCorectWordCount()/(double)result.getTotalWordCount());
	}
	
	private static TestResult createTest(TestConfig config) {
		TestResult testResult = new TestResult();
		File[] dir = new File(config.wordDir).listFiles(config.fileFilter);
		Interpreter inter = new Interpreter("files/ordlista-stor.txt");
		int counter = 0;
		for(File file : dir){
			//System.out.println("\n"+file.getPath());
			Map<Double, String> result = inter.Interpret(file, config);

			String realWord = inter.data.getWord();
			
			double realWordValue = -1;
			double failWordValue = -1;
			String failWord = null;
			for (Map.Entry<Double, String> entry : result.entrySet()) {
				if (entry.getValue().equals(realWord)) {
					realWordValue = entry.getKey();
				} else if(failWord==null) {
					failWord = entry.getValue();
					failWordValue = entry.getKey();
				}
				if (realWordValue != NOT_FOUND  && failWordValue != NOT_FOUND){
					break;
				}
			}
			if(realWordValue == NOT_FOUND && !inter.trie.checkWord(realWord)){
				continue;
			}
			TestResult.WordResult wResult = new TestResult.WordResult(realWord, failWord);
			if (realWordValue != NOT_FOUND  && failWordValue != NOT_FOUND) {
				double kvot = realWordValue/failWordValue;
				wResult.ratio = kvot;
				if (kvot<1) {
					testResult.addCorrectWord(wResult);
				} else {
					testResult.addFailedWord(wResult);
				}
			} else if (realWordValue == NOT_FOUND  && failWordValue == NOT_FOUND) {
				wResult.ratio = 1;
				testResult.addWrongWord(wResult);
			} else if (realWordValue == NOT_FOUND) {
				wResult.ratio = 0;
				testResult.addWrongWord(wResult);
			} else {
				wResult.ratio = 0;
				testResult.addCorrectWord(wResult);
			}
		}
		return testResult;
	}
	
}
