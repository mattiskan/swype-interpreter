package TrieSwype;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Map;

public class Tests {
	private final static double NOT_FOUND = -1;
	public static void main(String[] args) {
		TestConfig config = new TestConfig();
		//config.showGraphics = true;
		/*//createTest(config);
		TestResult result = createTest(config);
		for (TestResult.WordResult word : result.allWords) {
			System.out.printf("%-25s %2.5f   %s\n", word.realWord+":",
					word.ratio, (word.ratio<0)?"PASS": "FAILED: "+ word.otherWord);
		}
		System.out.printf("Correct: %d Failed: %d Total: %d SuccessRatio: %f\n", result.getCorectWordCount(), 
				result.getFailedWordCount(),result.getTotalWordCount(), result.getCorectWordCount()/(double)result.getTotalWordCount());
		*/
		AllOptimizations();
		noCurveOptimizations();
		AllOptimizationsDoubleLetter();
		AllOptimizationsNoDoubleLetter();
		NoOptimizations();
		onlyCurveOptimizations();
	}
	
	private static void printResult(TestResult r, String name) {
		String pStr = "Test: %s Correct: %d Failed: %d Total: %d SuccessRatio: %f Top Four: %d\n";
		System.out.printf(pStr, name, r.getCorectWordCount(), r.getFailedWordCount(),
				r.getTotalWordCount(), r.getCorectWordCount()/(double)r.getTotalWordCount(), r.topFour.size());

	}
	
	private static void NoOptimizations() {
		try {
			TestConfig config = new TestConfig();
			config.optimizeWords = false;
			config.curveDistance = false;
			TestResult result = createTest(config);
			PrintWriter out = new PrintWriter("testdata/nooptimizations.data");
			for (TestResult.WordResult word : result.allWords) {
				out.println(word.ratio);
			}
			out.flush();
			out.close();
			printResult(result, "No optimizations");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void AllOptimizations() {
		try {
			TestConfig config = new TestConfig();
			TestResult result = createTest(config);
			PrintWriter out = new PrintWriter("testdata/alloptimizations.data");
			/*for (TestResult.WordResult word : result.allWords) {
				out.println(word.ratio);
			}*/
			//Collections.sort(result.correctWord);
			//Collections.sort(result.failedWord);
			for (TestResult.WordResult word : result.correctWord) {
				out.println(word.ratio);
			}
			for (TestResult.WordResult word : result.failedWord) {
				out.println(word.ratio);
			}
			out.flush();
			out.close();
			printResult(result, "All");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void noCurveOptimizations() {
		try {
			TestConfig config = new TestConfig();
			config.curveDistance = false;
			TestResult result = createTest(config);
			PrintWriter out = new PrintWriter("testdata/nocurveoptimizations.data");
			for (TestResult.WordResult word : result.allWords) {
				out.println(word.ratio);
			}
			out.flush();
			out.close();
			printResult(result, "No curve");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void onlyCurveOptimizations() {
		try {
			TestConfig config = new TestConfig();
			config.curveDistance = true;
			config.optimizeWords = false;
			TestResult result = createTest(config);
			PrintWriter out = new PrintWriter("testdata/onlycurveoptimizations.data");
			for (TestResult.WordResult word : result.allWords) {
				out.println(word.ratio);
			}
			out.flush();
			out.close();
			printResult(result, "Only curve");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void AllOptimizationsNoDoubleLetter() {
		try {
			TestConfig config = new TestConfig();
			config.fileFilter = new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return !pathname.getName().matches(".*([a-zåäö])\\1.*") &&
							pathname.getName().endsWith("_0.json");
				}
				
			};
			TestResult result = createTest(config);
			PrintWriter out = new PrintWriter("testdata/alloptimizations_nodoubleletter.data");
			for (TestResult.WordResult word : result.allWords) {
				out.println(word.ratio);
			}
			out.flush();
			out.close();
			printResult(result, "No double letters");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void AllOptimizationsDoubleLetter() {
		try {
			TestConfig config = new TestConfig();
			config.fileFilter = new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.getName().matches(".*([a-zåäö])\\1.*") &&
							pathname.getName().endsWith("_0.json");
				}
				
			};
			TestResult result = createTest(config);
			PrintWriter out = new PrintWriter("testdata/alloptimizations_doubleletter.data");
			for (TestResult.WordResult word : result.allWords) {
				out.println(word.ratio);
			}
			out.flush();
			out.close();
			printResult(result, "Only double letters");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private static TestResult createTest(TestConfig config) {
		TestResult testResult = new TestResult();
		File[] dir = new File(config.wordDir).listFiles(config.fileFilter);
		Interpreter inter = new Interpreter("files/ordlista-stor.txt");

		for(File file : dir){
			//System.out.println("\n"+file.getPath());
			Map<Double, String> result = inter.Interpret(file, config);

			String realWord = inter.data.getWord();
			
			double realWordValue = -1;
			double failWordValue = -1;
			String failWord = null;
			int counter = 0;
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
				if (realWordValue == NOT_FOUND)
					counter++;		
			}
			if(realWordValue == NOT_FOUND && !inter.trie.checkWord(realWord)){
				continue;
			}
			
			TestResult.WordResult wResult = new TestResult.WordResult(realWord, failWord, counter);
			if (realWordValue != NOT_FOUND  && failWordValue != NOT_FOUND) {
				double kvot = realWordValue/failWordValue;
				
				if (kvot<1) {
					wResult.ratio = (failWordValue/realWordValue-1)*100;
					testResult.addCorrectWord(wResult);
				} else {
					wResult.ratio = -(realWordValue/failWordValue-1)*100;
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
