/**
 * @author Anirbit Ghosh
 * Student ID: 2439281g
 */

package detectors;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.VoidVisitor;

/**
 * Class to instantiate the detectors and test code from a given file 
 */
public class Driver {
	
	//Field to store the path of the file with code being tested 
	//private static final String FILE_PATH = "/Users/anirbit/Desktop/AllData/Java workspace/coursework2 /src/test/testcode/Calculator.java";
	private static String FILE_PATH;
	
	public static void main(String args[]) {
		FILE_PATH = args[0];
		
		try { 
			//Initialise compilation unit to be parsed with Javaparser
			CompilationUnit cu = JavaParser.parse(new FileInputStream(FILE_PATH));
			
			//Instantiate the first visitor object, that detects Useless Control Flow 
			VoidVisitor<List<Breakpoints>> controlFlowVisitor = new UselessControlFlowDetector();
			
			//Create a list of Breakpoint object instances to collect the behaviour data
			List<Breakpoints> controlFlowCollector = new ArrayList<Breakpoints>();
			
			//Execute the Visit method
			controlFlowVisitor.visit(cu, controlFlowCollector);
			
			//Header statements to make output more readable and organized
			System.out.println("Useless Control Flows found: ");
			System.out.println("");
			
			//Print each Breakpoints object instance stored in the collector list
			controlFlowCollector.forEach(m ->{
				System.out.println(m);
			});
			
			
			
			
			//Instantiate the second visitor object, that detects Recursive methods
			VoidVisitor<List<Breakpoints>> recursionVisitor = new RecursionDetector();
			
			//Create a list of Breakpoints object instances to store the observed behaviour data
			List<Breakpoints> recursionCollector = new ArrayList<Breakpoints>();
			
			//Execute the visit method 
			recursionVisitor.visit(cu, recursionCollector);
			
			//Headers to make output more readable 
			System.out.println("");
			System.out.println("Recursion found:");
			System.out.println("");
			
			//Print each breakpoints object instance stored in the collector list 
			recursionCollector.forEach(m ->{
				System.out.println(m);
			});
			
			
			
		}
		//Catch the exception thrown in the event the file name provided is incorrect
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
