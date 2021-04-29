/**
 * @author Anirbit Ghosh
 * Student ID: 2439281g
 */

package detectors;

import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BreakStmt;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.EmptyStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * Class to detect Useless Control flow  using Visitor design pattern.
 * Extends the abstract VoidVisitorAdapter class to use the Visitor object to traverse the nodes in the given code.
 * Takes parameter of type List(Breakpoints) to carry state between traversals
 */
public class UselessControlFlowDetector extends VoidVisitorAdapter<List<Breakpoints>> {

	
	/**
	 * Override the original visit() method to take as the first argument of type IfStmt to traverse through the If Statements in the code.
	 * Takes the parametrised type for states for each node visited as the second parameter, which is a list of Breakpoints objects.
	 */
	@Override
	public void visit(IfStmt ifStatement, List<Breakpoints> collector) {
		super.visit(ifStatement, collector);

		//Collect the basic data of the node being visited
		//Name of the parent class 
		String className = ifStatement.findAncestor(MethodDeclaration.class).get().findAncestor(ClassOrInterfaceDeclaration.class).get().getNameAsString();
		
		//Name of method being enclosing the Control Flow Statement being traversed through
		String methodName = ifStatement.findAncestor(MethodDeclaration.class).get().getNameAsString();
		
		//Beginning and ending line numbers of the current method
		int begin = ifStatement.getRange().get().begin.line;
		int end = ifStatement.getRange().get().end.line;
		
		//Get a list of nodes under the statement being visited.
		//Gives a list with the condition and body of the if statement 
		List<Node> topLevelNodes = ifStatement.getChildNodes();

		//A boolean variable to indicate if the node being visited matches our requirements for being a Useless Control Flow Statement 
		//Initialised as false
		boolean isUseless = false;
		
		//If body of IfStmt is empty, then it is immediately useless
		if(topLevelNodes.size() < 2) {
			isUseless = true;
			
		}else { 
			//get the nodes of the body of the IfStmt
			List<Node> subNodes = topLevelNodes.get(1).getChildNodes();
			if(subNodes.isEmpty()) {
				end = topLevelNodes.get(1).getRange().get().end.line;
				isUseless = true;
				
			}else { 
				//Go through the nodes of the body of the IfStmt
				 for(Node n : subNodes) {
					 //If the body only contains comments, it is useless
					 if(n.getClass().equals(Comment.class) || n.getClass().equals(LineComment.class)) {
						 isUseless = true;
						 end = n.getRange().get().end.line;
						 
					 }else {
						 //if it contains anything other than comments, it is no longer useless
						 isUseless = false;
						 break;
					 }
				 }				 
			}
		}
		
		//if useless, add a breakpoint object with the relevant data to the collector 
		if (isUseless) {
			Breakpoints bp = new Breakpoints(className, methodName, begin, end);
			collector.add(bp);
		}

	}

	
	/**
	 * Override the original visit() method to take as the first argument of type WhileStmt to traverse through the while Statements in the code.
	 * Takes the parametrised type for states for each node visited as the second parameter, which is a list of Breakpoints objects.
	 */
	@Override
	public void visit(WhileStmt whileStatement, List<Breakpoints> collector) {
		super.visit(whileStatement, collector);

		//Collect the basic data of the node being visited
		//Name of the parent class 
		String className = whileStatement.findAncestor(MethodDeclaration.class).get().findAncestor(ClassOrInterfaceDeclaration.class).get().getNameAsString();
		
		//Name of method being enclosing the Control Flow Statement being traversed through
		String methodName = whileStatement.findAncestor(MethodDeclaration.class).get().getNameAsString();
		
		//Beginning and ending line numbers of the current method
		int begin = whileStatement.getRange().get().begin.line;
		int end = whileStatement.getRange().get().end.line;
		
		//A boolean variable to indicate if the node being visited matches our requirements for being a Useless Control Flow Statement 
		//Initialised as false
		boolean isUseless = false;

		//Get a list of nodes under the statement being visited.
		//Gives a list with the condition and body of the while statement 
		List<Node> nodes = whileStatement.getChildNodes();
		
		//Get a list of the nodes under the body of the while Statement
		List<Node> subNodes = nodes.get(1).getChildNodes();
		
		//If body is empty it is immediately an useless statement 
		if(subNodes.isEmpty()) {
			isUseless = true;
			
			//get new line number where the statement ends
			end = nodes.get(0).getRange().get().end.line;
			
		} else { 
			//Go through the nodes of the body of the whileStmt
			for(Node n : subNodes) {
				//if there are only comments then it is an Useless statement
				if(n.getClass().equals(LineComment.class) || n.getClass().equals(Comment.class)) {
					isUseless = true;
					end = n.getRange().get().end.line;
				
				}else {
					//if there is anything other a comment, the statement is no longer useless
					isUseless = false;
					break;
				}
			}
			
		}
		
		//if useless, add a breakpoint object with the relevant data to the collector 
		if (isUseless) {
			Breakpoints bp = new Breakpoints(className, methodName, begin, end);
			collector.add(bp);
		}
	}

	
	
	/**
	 * Override the original visit() method to take as the first argument of type ForStmt to traverse through the For Statements in the code.
	 * Takes the parametrised type for states for each node visited as the second parameter, which is a list of Breakpoints objects.
	 */
	@Override
	public void visit(ForStmt forStatement, List<Breakpoints> collector) {
		super.visit(forStatement, collector);

		//Collect the basic data of the node being visited
		//Name of the parent class 
		String className = forStatement.findAncestor(MethodDeclaration.class).get().findAncestor(ClassOrInterfaceDeclaration.class).get().getNameAsString();
		
		//Name of method being enclosing the Control Flow Statement being traversed through
		String methodName = forStatement.findAncestor(MethodDeclaration.class).get().getNameAsString();
		
		//Beginning and ending line numbers of the current method
		int begin = forStatement.getRange().get().begin.line;
		int end = forStatement.getRange().get().end.line;
		
		//A boolean variable to indicate if the node being visited matches our requirements for being a Useless Control Flow Statement 
		//Initialised as false
		boolean isUseless = false;

		//Get a list of nodes under the statement being visited.
		//Gives a list with the variable initialisation, continuation test, update step and the loop body
		List<Node> topLevelNodes = forStatement.getChildNodes();
		
		//Get a list of the nodes under the body of the for loop Statement
		List<Node> bodyNodes = topLevelNodes.get(3).getChildNodes();
		
		//If the body is empty, it is immediately useless
		if(bodyNodes.isEmpty()) {
			isUseless = true;
			
		}else {
			//If the body is not empty, iterate through each node to check
			for (Node n : bodyNodes) {
				//If the body only contains comments, it is useless
				if(n.getClass().equals(LineComment.class) || n.getClass().equals(Comment.class)) {
					isUseless = true;
					
				}else { 
					//if the body has any expression other than comments, it is no longer useless
					isUseless = false;
					break;
				}
			}
		}

		//if useless, add a breakpoint object with the relevant data to the collector 
		if (isUseless) {
			Breakpoints bp = new Breakpoints(className, methodName, begin, end);
			collector.add(bp);
		}
	}


	
	/**
	 * Override the original visit() method to take as the first argument of type DoStmt to traverse through the Do Statements in the code.
	 * Takes the parametrised type for states for each node visited as the second parameter, which is a list of Breakpoints objects.
	 */
	@Override
	public void visit(DoStmt doStatement, List<Breakpoints> collector) {
		super.visit(doStatement, collector);

		//Collect the basic data of the node being visited
		//Name of the parent class 
		String className = doStatement.findAncestor(MethodDeclaration.class).get().findAncestor(ClassOrInterfaceDeclaration.class).get().getNameAsString();
		
		//Name of method being enclosing the Control Flow Statement being traversed through
		String methodName = doStatement.findAncestor(MethodDeclaration.class).get().getNameAsString();
		
		//Beginning and ending line numbers of the current method
		int begin = doStatement.getRange().get().begin.line;
		int end = doStatement.getRange().get().end.line;

		//Get a list of nodes under the statement being visited.
		//Gives a list with the body of the do statement and the while loop condition
		List<Node> nodes = doStatement.getChildNodes();
		
		//If the body is empty, it is immediately useless
		boolean isUseless = nodes.get(0).getChildNodes().isEmpty();
		
		//Get the list of nodes under the body of the do Statement 
		List<Node> subNodes = nodes.get(0).getChildNodes();
		
		//If body is not empty
		if(!isUseless) {
			//Iterate through each node in the body block
			for(Node subNode : subNodes) {
				//If the body contains only comments, it is an useless statement
				if(subNode.getClass().equals(LineComment.class) || subNode.getClass().equals(Comment.class)) {
					isUseless = true;
					
				}else { 
					//if the body contains any expression other than a comment, it is no longer useless
					isUseless = false;
					break;
				}
			}
		}

		//if useless, add a breakpoint object with the relevant data to the collector 
		if (isUseless) {
			Breakpoints bp = new Breakpoints(className, methodName, begin, end);
			collector.add(bp);
		}
	}

	
	/**
	 * Override the original visit() method to take as the first argument of type SwitchStmt to traverse through the Switch Statements in the code.
	 * Takes the parametrised type for states for each node visited as the second parameter, which is a list of Breakpoints objects.
	 */
	@Override
	public void visit(SwitchStmt switchStmt, List<Breakpoints> collector) {
		super.visit(switchStmt, collector);
		
		//Collect the basic data of the node being visited
		//Name of the parent class 
		String className = switchStmt.findAncestor(MethodDeclaration.class).get().findAncestor(ClassOrInterfaceDeclaration.class).get().getNameAsString();
		
		//Name of method being enclosing the Control Flow Statement being traversed through
		String methodName = switchStmt.findAncestor(MethodDeclaration.class).get().getNameAsString();
		
		//Beginning and ending line numbers of the current method
		int begin = switchStmt.getRange().get().begin.line;
		int end = switchStmt.getRange().get().end.line;
		
		//Get a list of nodes under the statement being visited.
		//Gives a list with the operator and the cases
		List<Node> topLevelNodes = switchStmt.getChildNodes();
		
		//If the switch statement has no cases or operators, it is immediately useless
		boolean isUseless = topLevelNodes.isEmpty();
		
		//If the switch statement only has an operator and no cases it is useless
		if(topLevelNodes.size() == 1) {
			isUseless = true;
			
			//if useless, add a breakpoint object with the relevant data to the collector 
			Breakpoints bp = new Breakpoints(className, methodName, begin, end);
			collector.add(bp);
			
		} else { 
			//Iterate through the cases of the switch statements
			for(int i = 1; i < topLevelNodes.size(); i++) {
				//get the nodes under each case
				List<Node> subNodes = topLevelNodes.get(i).getChildNodes();
				
				
				//If the switch statement case Node is empty with nothing but a operator expression, then it is useless
				if(subNodes.size() <= 1) { 
					isUseless = true;
					
				}else {
					for(Node subNode : subNodes) {
						//System.out.println(subNode + subNode.getClass().getSimpleName());
						//if the node is the operator for the case, go to the next node 
						if(subNode.getClass().equals(CharLiteralExpr.class) || subNode.getClass().equals(IntegerLiteralExpr.class) || subNode.getClass().equals(BooleanLiteralExpr.class) || subNode.getClass().equals(StringLiteralExpr.class)) {
							continue;
						
						//if the nodes are anything other than comments or a break statement or an empty statement then that case statement is not useless
						}else if(!(subNode.getClass().equals(LineComment.class) || subNode.getClass().equals(Comment.class) || subNode.getClass().equals(BreakStmt.class) || subNode.getClass().equals(EmptyStmt.class))) {
							isUseless = false;
							break;
						
						//If the cases don't have any useful expression statement it is useless
						}else {
							isUseless = true;
						}
					}
				}

				//if useless, add a breakpoint object with the relevant data to the collector 
				if(isUseless) {
					//Get the start and end line numbers of the switch case that is useless
					end = topLevelNodes.get(i).getRange().get().end.line;
					begin = topLevelNodes.get(i).getRange().get().begin.line;
					
					Breakpoints bp = new Breakpoints(className, methodName, begin, end);
					collector.add(bp);
				}
				
				
			}	
		}
	}
		
	
}
