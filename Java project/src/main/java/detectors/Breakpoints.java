/**
 * @author Anirbit Ghosh
 * Student ID: 2439281g
 */


package detectors;

/**
 * Class to collect the data of the observed behaviour
 * of Useless Control Flow statements and Recursive methods.
 */
public class Breakpoints {
	//fields to collect relevant information of the Control Flow being tested
	private String className;
	private int startLine;
	private int endLine;
	private String methodName;
	
	//Constructor to instantiate a Breakpoint object with the corresponding data fields
	public Breakpoints(String className,  String methodName, int startLine, int endLine) {
		
		this.className = className;
		this.methodName = methodName;
		this.startLine = startLine;
		this.endLine = endLine;
	}
	
	//Meaningful string representation of the collected data 
	@Override
	public String toString() {
		return "Class Name = " + className + ", " + "Method Name = " + methodName + ", " + "Start Line = " + startLine + ", " + 
				"End Line = " + endLine;
	}
	

}
