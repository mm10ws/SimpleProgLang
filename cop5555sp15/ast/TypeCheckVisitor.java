package cop5555sp15.ast;

import static cop5555sp15.TokenStream.Kind.*;
import static cop5555sp15.TokenStream.Kind;
import cop5555sp15.TypeConstants;
import cop5555sp15.symbolTable.SymbolTable;

public class TypeCheckVisitor implements ASTVisitor, TypeConstants {

	@SuppressWarnings("serial")
	public static class TypeCheckException extends Exception {
		ASTNode node;

		public TypeCheckException(String message, ASTNode node) {
			super(node.firstToken.lineNumber + ":" + message);
			this.node = node;
		}
	}

	SymbolTable symbolTable;

	public TypeCheckVisitor(SymbolTable symbolTable) {
		this.symbolTable = symbolTable;
	}

	boolean check(boolean condition, String message, ASTNode node)
			throws TypeCheckException {
		if (condition)
			return true;
		throw new TypeCheckException(message, node);
	}

	/**
	 * Ensure that types on left and right hand side are compatible.
	 */
	@Override
	public Object visitAssignmentStatement(
			AssignmentStatement assignmentStatement, Object arg)
			throws Exception {
		// throws Exception {
		// throw new UnsupportedOperationException("not yet implemented");
		String t1 = (String) assignmentStatement.lvalue.visit(this, arg);
		String t2 = (String) assignmentStatement.expression.visit(this, arg);

		//System.out.println(t1 + " " + t2);
		// !assignmentStatement.lvalue.type
		// .equals(assignmentStatement.expression.expressionType)
		if (!t1.equals(t2)) {
			if (t1.startsWith("Ljava/util/List")
					&& t2.equals("Ljava/util/ArrayList")) {
				// pass
			} else {
				throw new TypeCheckVisitor.TypeCheckException(
						"Lvalue type not the same as Rvalue",
						assignmentStatement);
			}

		}

		return null;
	}

	/**
	 * Ensure that both types are the same, save and return the result type
	 */
	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression,
			Object arg) throws Exception {
		// throw new UnsupportedOperationException("not yet implemented");
		String type0 = (String) binaryExpression.expression0.visit(this, arg);
		String type1 = (String) binaryExpression.expression1.visit(this, arg);
		Kind testOp = binaryExpression.op.kind;
		if (type0.equals(type1)) {
			if (type0.equals(intType)) {
				if (testOp == TIMES || testOp == PLUS || testOp == DIV
						|| testOp == MINUS) {
					binaryExpression.setType(intType);
					return intType;
				} else if (testOp == EQUAL || testOp == NOTEQUAL
						|| testOp == GE || testOp == LE || testOp == GT
						|| testOp == LT) {
					binaryExpression.setType(booleanType);
					return booleanType;
				} else {
					throw new TypeCheckVisitor.TypeCheckException(
							"Illegal type for operator", binaryExpression);
				}
			} else if (type0.equals(stringType)) {
				if (testOp == NOTEQUAL || testOp == EQUAL) {
					binaryExpression.setType(booleanType);
					return booleanType;
				} else if (testOp == PLUS) {
					binaryExpression.setType(stringType);
					return stringType;
				} else {
					throw new TypeCheckVisitor.TypeCheckException(
							"Illegal type for operator", binaryExpression);
				}
			} else if (type0.equals(booleanType)) {
				if (testOp == AND || testOp == BAR || testOp == EQUAL
						|| testOp == NOTEQUAL) {
					binaryExpression.setType(booleanType);
					return booleanType;
				} else {
					throw new TypeCheckVisitor.TypeCheckException(
							"Illegal type for operator", binaryExpression);
				}
			} else {
				throw new TypeCheckVisitor.TypeCheckException("",
						binaryExpression);
				// need to implement
			}
		} else {
			throw new TypeCheckVisitor.TypeCheckException(
					"Binary expression type mismatch", binaryExpression);
		}

	}

	/**
	 * Blocks define scopes. Check that the scope nesting level is the same at
	 * the end as at the beginning of block
	 */
	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		int numScopes = symbolTable.enterScope();
		// visit children
		for (BlockElem elem : block.elems) {
			elem.visit(this, arg);
		}
		int numScopesExit = symbolTable.leaveScope();
		check(numScopesExit > 0 && numScopesExit == numScopes,
				"unbalanced scopes", block);
		return null;
	}

	/**
	 * Sets the expressionType to booleanType and returns it
	 * 
	 * @param booleanLitExpression
	 * @param arg
	 * @return
	 * @throws Exception
	 */
	@Override
	public Object visitBooleanLitExpression(
			BooleanLitExpression booleanLitExpression, Object arg)
			throws Exception {
		// throw new UnsupportedOperationException("not yet implemented");
		booleanLitExpression.setType(booleanType);
		return booleanType;
	}

	/**
	 * A closure defines a new scope Visit all the declarations in the
	 * formalArgList, and all the statements in the statementList construct and
	 * set the JVMType, the argType array, and the result type
	 * 
	 * @param closure
	 * @param arg
	 * @return
	 * @throws Exception
	 */
	@Override
	public Object visitClosure(Closure closure, Object arg) throws Exception {
		throw new UnsupportedOperationException("not yet implemented");
	}

	/**
	 * Make sure that the name has not already been declared and insert in
	 * symbol table. Visit the closure
	 */
	@Override
	public Object visitClosureDec(ClosureDec closureDec, Object arg) {
		throw new UnsupportedOperationException("not yet implemented");
	}

	/**
	 * Check that the given name is declared as a closure Check the argument
	 * types The type is the return type of the closure
	 */
	@Override
	public Object visitClosureEvalExpression(
			ClosureEvalExpression closureExpression, Object arg)
			throws Exception {
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public Object visitClosureExpression(ClosureExpression closureExpression,
			Object arg) throws Exception {
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public Object visitExpressionLValue(ExpressionLValue expressionLValue,
			Object arg) throws Exception {
		// throw new UnsupportedOperationException("not yet implemented");
		expressionLValue.expression.visit(this, arg);
		if (expressionLValue.expression.expressionType.equals(intType)) {
			VarDec tmp = (VarDec) symbolTable
					.lookup(expressionLValue.identToken.getText());

			if (tmp.type.getJVMType().equals("Ljava/util/List<Z>;")) {
				expressionLValue.type = booleanType;
			} else if (tmp.type.getJVMType().equals(
					"Ljava/util/List<Ljava/lang/String;>;")) {
				expressionLValue.type = stringType;
			} 
			else if (tmp.type.getJVMType().equals(
					"Ljava/util/List<Ljava/util/List<I>;>;")) {
				expressionLValue.type = "Ljava/util/List<I>;";
			}
			else if (tmp.type.getJVMType().equals(
					"Ljava/util/List<Ljava/util/List<Z>;>;")) {
				expressionLValue.type = "Ljava/util/List<Z>;";
			}
			
			else {
				expressionLValue.type = intType;
			}

		} else {
			throw new TypeCheckVisitor.TypeCheckException(
					"the expression of the expressionLValue is not an int",
					expressionLValue);
		}

		return expressionLValue.type; // change this for other lists
	}

	@Override
	public Object visitExpressionStatement(
			ExpressionStatement expressionStatement, Object arg)
			throws Exception {
		throw new UnsupportedOperationException("not yet implemented");
	}

	/**
	 * Check that name has been declared in scope Get its type from the
	 * declaration.
	 * 
	 */
	@Override
	public Object visitIdentExpression(IdentExpression identExpression,
			Object arg) throws Exception {
		// throw new UnsupportedOperationException("not yet implemented");
		if (this.symbolTable.lookup(identExpression.identToken.getText()) != null) {
			VarDec vd = (VarDec) this.symbolTable
					.lookup(identExpression.identToken.getText());
			identExpression.expressionType = vd.type.getJVMType();
		} else {
			throw new TypeCheckVisitor.TypeCheckException(
					"ident expression not in symbol table", identExpression);
		}

		return identExpression.expressionType;

	}

	@Override
	public Object visitIdentLValue(IdentLValue identLValue, Object arg)
			throws Exception {
		// throw new UnsupportedOperationException("not yet implemented");
		// System.out.println(identLValue.identToken.getText());
		if (this.symbolTable.lookup(identLValue.identToken.getText()) != null) {
			VarDec vd = (VarDec) this.symbolTable.lookup(identLValue.identToken
					.getText());
			identLValue.type = vd.type.getJVMType();
		} else {
			throw new TypeCheckVisitor.TypeCheckException(
					"cannot find ident l value in symbol table", identLValue);
		}

		return identLValue.type;
	}

	@Override
	public Object visitIfElseStatement(IfElseStatement ifElseStatement,
			Object arg) throws Exception {
		// throw new UnsupportedOperationException("not yet implemented");
		ifElseStatement.expression.visit(this, arg);
		if (!ifElseStatement.expression.expressionType.equals(booleanType)) {
			throw new TypeCheckVisitor.TypeCheckException(
					"the guard is not a boolean", ifElseStatement);
		} else {
			ifElseStatement.ifBlock.visit(this, arg);
			ifElseStatement.elseBlock.visit(this, arg);
		}
		return null;
	}

	/**
	 * expression type is boolean
	 */
	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg)
			throws Exception {
		// throw new UnsupportedOperationException("not yet implemented");
		ifStatement.expression.visit(this, arg);
		if (!ifStatement.expression.expressionType.equals(booleanType)) {
			throw new TypeCheckVisitor.TypeCheckException(
					"the guard is not a boolean", ifStatement);
		} else {
			ifStatement.block.visit(this, arg);

		}
		return null;
	}

	/**
	 * expression type is int
	 */
	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression,
			Object arg) throws Exception {
		intLitExpression.setType(intType);
		return intType;
	}

	@Override
	public Object visitKeyExpression(KeyExpression keyExpression, Object arg)
			throws Exception {
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public Object visitKeyValueExpression(
			KeyValueExpression keyValueExpression, Object arg) throws Exception {
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public Object visitKeyValueType(KeyValueType keyValueType, Object arg)
			throws Exception {
		throw new UnsupportedOperationException("not yet implemented");
	}

	// visit the expressions (children) and ensure they are the same type
	// the return type is "Ljava/util/ArrayList<"+type0+">;" where type0 is the
	// type of elements in the list
	// this should handle lists of lists, and empty list. An empty list is
	// indicated by "Ljava/util/ArrayList;".
	@Override
	public Object visitListExpression(ListExpression listExpression, Object arg)
			throws Exception {
		// throw new UnsupportedOperationException("not yet implemented");
		boolean first = true;
		boolean same = true;
		String testType = "";
		for (Expression e : listExpression.expressionList) {
			if (first) {
				testType = (String) e.visit(this, arg);
				first = false;
			} else {
				same = ((String) e.visit(this, arg)).equals(testType) && same;
			}
		}

		if (same != true) {
			throw new TypeCheckVisitor.TypeCheckException(
					"the list objects are not of the same type", listExpression);
		}
		if (testType.equals("")) {
			listExpression.expressionType = emptyList;
		} else {
			listExpression.expressionType = testType;
		}

		if (testType.equals("")) {
			return emptyList;
		} else {
			return "Ljava/util/List<" + listExpression.expressionType + ">;";
		}

		// return listExpression.expressionType;
	}

	/** gets the type from the enclosed expression */
	@Override
	public Object visitListOrMapElemExpression(
			ListOrMapElemExpression listOrMapElemExpression, Object arg)
			throws Exception {
		// throw new UnsupportedOperationException("not yet implemented");
		listOrMapElemExpression.expression.visit(this, arg);
		if (listOrMapElemExpression.expression.expressionType.equals(intType)) {
			VarDec tmp = (VarDec) symbolTable
					.lookup(listOrMapElemExpression.identToken.getText());

			if (tmp.type.getJVMType().equals("Ljava/util/List<Z>;")) {
				listOrMapElemExpression.expressionType = booleanType;
			} else if (tmp.type.getJVMType().equals(
					"Ljava/util/List<Ljava/lang/String;>;")) {
				listOrMapElemExpression.expressionType = stringType;
			} else if (tmp.type.getJVMType().equals(
					"Ljava/util/List<Ljava/util/List<I>;>;")) {

				listOrMapElemExpression.expressionType = "Ljava/util/List<I>;";
			} else if (tmp.type.getJVMType().equals(
					"Ljava/util/List<Ljava/util/List<Z>;>;")) {

				listOrMapElemExpression.expressionType = "Ljava/util/List<Z>;";
			} else if (tmp.type.getJVMType().equals(
					"Ljava/util/List<Ljava/util/List<Ljava/lang/String;>;>;")) {

				listOrMapElemExpression.expressionType = "Ljava/util/List<Ljava/lang/String;>;";
			} else {
				listOrMapElemExpression.expressionType = intType;
			}

		} else {
			throw new TypeCheckVisitor.TypeCheckException(
					"the expression of the listOrMapElemExpression is not an int",
					listOrMapElemExpression);
		}

		return listOrMapElemExpression.expressionType;
	}

	@Override
	public Object visitListType(ListType listType, Object arg) throws Exception {
		// throw new UnsupportedOperationException("not yet implemented");
		listType.type.visit(this, arg);
		return listType.type.getJVMType();
	}

	@Override
	public Object visitMapListExpression(MapListExpression mapListExpression,
			Object arg) throws Exception {
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public Object visitPrintStatement(PrintStatement printStatement, Object arg)
			throws Exception {
		printStatement.expression.visit(this, null);
		return null;
	}

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		if (arg == null) {
			program.JVMName = program.name;
		} else {
			program.JVMName = arg + "/" + program.name;
		}
		// ignore the import statement
		if (!symbolTable.insert(program.name, null)) {
			throw new TypeCheckException("name already in symbol table",
					program);
		}
		program.block.visit(this, true);
		return null;
	}

	@Override
	public Object visitQualifiedName(QualifiedName qualifiedName, Object arg) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Checks that both expressions have type int.
	 * 
	 * Note that in spite of the name, this is not in the Expression type
	 * hierarchy.
	 */
	@Override
	public Object visitRangeExpression(RangeExpression rangeExpression,
			Object arg) throws Exception {
		throw new UnsupportedOperationException("not yet implemented");
	}

	// nothing to do here
	@Override
	public Object visitReturnStatement(ReturnStatement returnStatement,
			Object arg) throws Exception {
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public Object visitSimpleType(SimpleType simpleType, Object arg)
			throws Exception {
		// throw new UnsupportedOperationException("not yet implemented");
		return simpleType;
	}

	@Override
	public Object visitSizeExpression(SizeExpression sizeExpression, Object arg)
			throws Exception {
		// throw new UnsupportedOperationException("not yet implemented");
		String sizeType = (String) sizeExpression.expression.visit(this, arg);
		sizeExpression.expressionType = intType;

		if (!sizeType.startsWith("Ljava/util/List")) {
			throw new TypeCheckVisitor.TypeCheckException(
					"Illegal type for operator", sizeExpression);
		}
		return intType;
	}

	@Override
	public Object visitStringLitExpression(
			StringLitExpression stringLitExpression, Object arg)
			throws Exception {
		// throw new UnsupportedOperationException("not yet implemented");
		stringLitExpression.setType(stringType);
		return stringType;
	}

	/**
	 * if ! and boolean, then boolean else if - and int, then int else error
	 */
	@Override
	public Object visitUnaryExpression(UnaryExpression unaryExpression,
			Object arg) throws Exception {
		unaryExpression.expression.visit(this, arg);
		// unaryExpression.expressionType =
		// unaryExpression.expression.expressionType;
		if (unaryExpression.op.getText().equals("!")
				&& unaryExpression.expression.expressionType
						.equals(booleanType)) {
			unaryExpression.expressionType = booleanType;
		} else if (unaryExpression.op.getText().equals("-")
				&& unaryExpression.expression.expressionType.equals(intType)) {
			unaryExpression.expressionType = intType;
		} else {
			throw new TypeCheckVisitor.TypeCheckException(
					"Illegal type for operator in unary expression",
					unaryExpression);
		}
		// throw new UnsupportedOperationException("not yet implemented");
		return unaryExpression.expressionType;
	}

	@Override
	public Object visitUndeclaredType(UndeclaredType undeclaredType, Object arg)
			throws Exception {
		throw new UnsupportedOperationException(
				"undeclared types not supported");
	}

	@Override
	public Object visitValueExpression(ValueExpression valueExpression,
			Object arg) throws Exception {
		throw new UnsupportedOperationException("not yet implemented");
	}

	/**
	 * check that this variable has not already been declared in the same scope.
	 */
	@Override
	public Object visitVarDec(VarDec varDec, Object arg) throws Exception {
		// throw new UnsupportedOperationException("not yet implemented");
		if (this.symbolTable.insert(varDec.identToken.getText(), varDec)) {
			// success
			// SimpleType st = (SimpleType) varDec.visit(this, arg);
			// varDec.type = st;
			varDec.type.visit(this, arg);
		} else {

			throw new TypeCheckVisitor.TypeCheckException(
					"Variable with same name already in scope", varDec);

		}

		return varDec.type.getJVMType();
	}

	/**
	 * All checking will be done in the children since grammar ensures that the
	 * rangeExpression is a rangeExpression.
	 */
	@Override
	public Object visitWhileRangeStatement(
			WhileRangeStatement whileRangeStatement, Object arg)
			throws Exception {
		throw new UnsupportedOperationException("not yet implemented");

	}

	@Override
	public Object visitWhileStarStatement(
			WhileStarStatement whileStarStatement, Object arg) throws Exception {
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg)
			throws Exception {
		// throw new UnsupportedOperationException("not yet implemented");

		whileStatement.expression.visit(this, arg);
		if (!whileStatement.expression.expressionType.equals(booleanType)) {
			throw new TypeCheckVisitor.TypeCheckException(
					"the guard is not a boolean", whileStatement);
		} else {
			whileStatement.block.visit(this, arg);

		}
		return null;
	}

}
