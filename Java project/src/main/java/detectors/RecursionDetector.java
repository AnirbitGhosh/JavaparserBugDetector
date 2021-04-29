/**
 * @author Anirbit Ghosh
 * Student ID: 2439281g
 */

package detectors;

import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * Class to detect Recursive methods using Visitor design pattern.
 * Extends the abstract VoidVisitorAdapter class to use the Visitor object to traverse the nodes in the given code.
 * Takes parameter of type List(Breakpoints) to carry state between traversals
 */
public class RecursionDetector extends VoidVisitorAdapter<List<Breakpoints>> {
	
	/**
	 * Override the original visit() method to take as the first argument of type MethodDeclaration to traverse through the methods in the code.
	 * Takes the parametrised type for states for each node visited as the second parameter, which is a list of Breakpoints objects
	 */
	@Override
	public void visit(MethodDeclaration methodDeclaration, List<Breakpoints> collector) {
		super.visit(methodDeclaration, collector);
		
		//Collect the basic data of the node being visited
		//Name of the class enclosing the method 
		String className = methodDeclaration.findAncestor(ClassOrInterfaceDeclaration.class).get().getNameAsString();
		
		//Name of method being traversed through 
		String methodName = methodDeclaration.getNameAsString();
		
		//Beginning and ending line numbers of the current method
		int begin = methodDeclaration.getRange().get().begin.line;
		int end = methodDeclaration.getRange().get().end.line;
		
		//A boolean variable to indicate if the node being visited matches our requirements for being a recursive method
		boolean isRecursion = false;
		
		//Get a list of nodes under the MethodDeclaration being visited
		//Gives a list with the various descriptors of the method including access modifier, return type, method name, parameters
		//and the body of the method is the last node
		List<Node> topLevelNodes = methodDeclaration.getChildNodes();
		
		//Get a list of nodes under the last node of topLevelNodes, which is the body of the parent method
		List<Node> subNodes = topLevelNodes.get(topLevelNodes.size()-1).getChildNodes();
		
		for(Node node : subNodes) {
			//From the nodes of the body block of the method, check if any node is a comment and skip that node if it is 
			if(node.getClass().equals(Comment.class)|| node.getClass().equals(LineComment.class)) {
				continue;
			}
			
			//Get a list of methodCalls under each node in the body of the parent method
			List<MethodCallExpr> methodCalls = node.findAll(MethodCallExpr.class);
			
			//Check if a node has any methodCalls at all, if it does then check if the methodCall has the same name as the parent method
			//If a methodCall in the body of the parent method shares the same name as the parent method then it is a recursion,
			//and the boolean isRecursion is changed to True
			if(!methodCalls.isEmpty()) {
				for (MethodCallExpr method : methodCalls) {
					isRecursion = method.getNameAsString().equals(methodName);
					
					//if is Recursion is True after going through all the nodes of the body of the parent method, then that method is a recursive method
					if(isRecursion) {
						Breakpoints bp = new Breakpoints(className, methodName, begin, end);
						collector.add(bp);
						
					}
				}
			}
			
			
		}
	}

}
