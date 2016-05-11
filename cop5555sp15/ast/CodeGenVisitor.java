package cop5555sp15.ast;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.objectweb.asm.*;

import static cop5555sp15.TokenStream.Kind.*;
import cop5555sp15.TokenStream.Kind;
import cop5555sp15.TypeConstants;

public class CodeGenVisitor implements ASTVisitor, Opcodes, TypeConstants {

	ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
	// Because we used the COMPUTE_FRAMES flag, we do not need to
	// insert the mv.visitFrame calls that you will see in some of the
	// asmifier examples. ASM will insert those for us.
	// FYI, the purpose of those instructions is to provide information
	// about what is on the stack just before each branch target in order
	// to speed up class verification.
	FieldVisitor fv;
	String className;
	String classDescriptor;
	Hashtable<String, Object> symtbl = new Hashtable<String, Object>();

	// This class holds all attributes that need to be passed downwards as the
	// AST is traversed. Initially, it only holds the current MethodVisitor.
	// Later, we may add more attributes.
	static class InheritedAttributes {
		public InheritedAttributes(MethodVisitor mv) {
			super();
			this.mv = mv;
		}

		MethodVisitor mv;
	}

	@Override
	public Object visitAssignmentStatement(
			AssignmentStatement assignmentStatement, Object arg)
			throws Exception {
		// throw new UnsupportedOperationException(
		// "code generation not yet implemented");

		MethodVisitor mv = ((InheritedAttributes) arg).mv;

		Object n = assignmentStatement.lvalue.visit(this, arg);
		// System.out.println(n);
		if (n.getClass().equals(ArrayList.class)) {
			ArrayList<Object> args = (ArrayList<Object>) (n);
			String name = (String) args.get(0);
			int i = (Integer) args.get(1);
			ListExpression l = (ListExpression) symtbl.get(name);

			if (l.expressionType.equals(booleanType)) {

				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, className, name,
						"Ljava/util/ArrayList;");
				mv.visitLdcInsn(i);
				assignmentStatement.expression.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean",
						"valueOf", "(Z)Ljava/lang/Boolean;", false);
				// mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList",
				// "set", "(ILjava/lang/Object;)Ljava/lang/Object;", false);
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "add",
						"(ILjava/lang/Object;)V", false);

				// mv.visitInsn(POP);
			} else if (l.expressionType.equals(stringType)) {

				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, className, name,
						"Ljava/util/ArrayList;");
				mv.visitLdcInsn(i);
				assignmentStatement.expression.visit(this, arg);
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "add",
						"(ILjava/lang/Object;)V", false);
				// mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean",
				// "valueOf", "(Z)Ljava/lang/Boolean;", false);
				// // mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList",
				// "set", "(ILjava/lang/Object;)Ljava/lang/Object;", false);
				// mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList",
				// "add", "(ILjava/lang/Object;)V", false);
			}
			
			else if (l.expressionType.equals("Ljava/util/List<I>;")) {

				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, className, name,
						"Ljava/util/ArrayList;");
				mv.visitLdcInsn(i);
				assignmentStatement.expression.visit(this, arg);
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "add",
						"(ILjava/lang/Object;)V", false);
				// mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean",
				// "valueOf", "(Z)Ljava/lang/Boolean;", false);
				// // mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList",
				// "set", "(ILjava/lang/Object;)Ljava/lang/Object;", false);
				// mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList",
				// "add", "(ILjava/lang/Object;)V", false);
			}

			else {
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, className, name,
						"Ljava/util/ArrayList;");
				mv.visitLdcInsn(i);
				assignmentStatement.expression.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer",
						"valueOf", "(I)Ljava/lang/Integer;", false);
				// mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList",
				// "set", "(ILjava/lang/Object;)Ljava/lang/Object;", false);
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "add",
						"(ILjava/lang/Object;)V", false);
				// mv.visitInsn(POP);
			}

			return null;
		}
		mv.visitVarInsn(ALOAD, 0);
		Object i = assignmentStatement.expression.visit(this, arg);

		// System.out.println(i);
		String eType = intType;
		if (i.getClass().equals(Integer.class)) {
			eType = intType;

		} else if (i.getClass().equals(Boolean.class)) {
			eType = booleanType;

		}
		// else if (i.getClass().equals(ListExpression.class)) {
		// if(((ListExpression)i).expressionType.equals(intType)){
		// eType = intType;
		// }
		// else{
		// eType = emptyList+";";
		// }
		// }
		else {

			eType = emptyList + ";"; // add more later
		}

		mv.visitFieldInsn(PUTFIELD, className, (String) n, eType);

		if (i.getClass().equals(ListExpression.class)) {
			if (((ListExpression) i).expressionType.equals(booleanType)) {
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, className, (String) n,
						"Ljava/util/ArrayList;");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList",
						"clear", "()V", false);

				for (Expression e : ((ListExpression) i).expressionList) {
					mv.visitVarInsn(ALOAD, 0);
					mv.visitFieldInsn(GETFIELD, className, (String) n,
							"Ljava/util/ArrayList;");
					e.visit(this, arg);
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean",
							"valueOf", "(Z)Ljava/lang/Boolean;", false);
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList",
							"add", "(Ljava/lang/Object;)Z", false);
					mv.visitInsn(POP);
				}
			} else if (((ListExpression) i).expressionType.equals(stringType)) {
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, className, (String) n,
						"Ljava/util/ArrayList;");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList",
						"clear", "()V", false);

				for (Expression e : ((ListExpression) i).expressionList) {
					mv.visitVarInsn(ALOAD, 0);
					mv.visitFieldInsn(GETFIELD, className, (String) n,
							"Ljava/util/ArrayList;");
					e.visit(this, arg);
					// mv.visitMethodInsn(INVOKESTATIC, "java/lang/String",
					// "valueOf", "(Z)Ljava/lang/Boolean;", false);
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList",
							"add", "(Ljava/lang/Object;)Z", false);
					mv.visitInsn(POP);
				}
			} else if (((ListExpression) i).expressionType
					.equals("Ljava/util/List<I>;")) {
				// System.out.println(((ListExpression)i).expressionList);
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, className, (String) n,
						"Ljava/util/ArrayList;");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList",
						"clear", "()V", false);

				for (Expression e : ((ListExpression) i).expressionList) {
					mv.visitVarInsn(ALOAD, 0);
					mv.visitFieldInsn(GETFIELD, className, (String) n,
							"Ljava/util/ArrayList;");
					e.visit(this, arg);
					// mv.visitMethodInsn(INVOKESTATIC, "java/lang/String",
					// "valueOf", "(Z)Ljava/lang/Boolean;", false);
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList",
							"add", "(Ljava/lang/Object;)Z", false);
					mv.visitInsn(POP);
				}
			}

			else if (((ListExpression) i).expressionType
					.equals("Ljava/util/List<Z>;")) {
				// System.out.println(((ListExpression)i).expressionList);
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, className, (String) n,
						"Ljava/util/ArrayList;");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList",
						"clear", "()V", false);

				for (Expression e : ((ListExpression) i).expressionList) {
					mv.visitVarInsn(ALOAD, 0);
					mv.visitFieldInsn(GETFIELD, className, (String) n,
							"Ljava/util/ArrayList;");
					e.visit(this, arg);
					// mv.visitMethodInsn(INVOKESTATIC, "java/lang/String",
					// "valueOf", "(Z)Ljava/lang/Boolean;", false);
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList",
							"add", "(Ljava/lang/Object;)Z", false);
					mv.visitInsn(POP);
				}
			}

			else if (((ListExpression) i).expressionType
					.equals("Ljava/util/List<Ljava/lang/String;>;")) {
				// System.out.println(((ListExpression)i).expressionList);
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, className, (String) n,
						"Ljava/util/ArrayList;");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList",
						"clear", "()V", false);

				for (Expression e : ((ListExpression) i).expressionList) {
					mv.visitVarInsn(ALOAD, 0);
					mv.visitFieldInsn(GETFIELD, className, (String) n,
							"Ljava/util/ArrayList;");
					e.visit(this, arg);
					// mv.visitMethodInsn(INVOKESTATIC, "java/lang/String",
					// "valueOf", "(Z)Ljava/lang/Boolean;", false);
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList",
							"add", "(Ljava/lang/Object;)Z", false);
					mv.visitInsn(POP);
				}
			} else {
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, className, (String) n,
						"Ljava/util/ArrayList;");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList",
						"clear", "()V", false);

				for (Expression e : ((ListExpression) i).expressionList) {
					mv.visitVarInsn(ALOAD, 0);
					mv.visitFieldInsn(GETFIELD, className, (String) n,
							"Ljava/util/ArrayList;");
					e.visit(this, arg);
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer",
							"valueOf", "(I)Ljava/lang/Integer;", false);
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList",
							"add", "(Ljava/lang/Object;)Z", false);
					mv.visitInsn(POP);
				}
			}

		}

		if (symtbl.containsKey(n)) { // gotta make sure variables of different
										// types can still have same name
			symtbl.replace((String) n, i);
		} else {
			symtbl.put((String) n, i);
		}
		return null;
	}

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression,
			Object arg) throws Exception {
		// throw new UnsupportedOperationException(
		// "code generation not yet implemented");
		MethodVisitor mv = ((InheritedAttributes) arg).mv;
		Object o1 = binaryExpression.expression0.visit(this, arg);
		Object o2 = binaryExpression.expression1.visit(this, arg);
		if (binaryExpression.op.kind == PLUS) {
			if (binaryExpression.expressionType == intType) {
				mv.visitInsn(IADD);
				if(o1 == null){
					o1 = new Integer(0);
				}
				if(o2 == null){
					o2 = new Integer(0);
				}
				if(o1.getClass().equals(ListExpression.class)){
					o1 = new Integer(0);
				}
				if(o2.getClass().equals(ListExpression.class)){
					o2 = new Integer(0);
				}
				return (Integer) o1 + (Integer) o2;
			} else {
				if(o1 == null){
					o1 = "";
				}
				if(o2 == null){
					o2 = "";
				}
				if(o1.getClass().equals(ListExpression.class)){
					o1 = "";
				}
				if(o2.getClass().equals(ListExpression.class)){
					o2 = "";
				}
				mv.visitInsn(POP);
				mv.visitInsn(POP);
				mv.visitLdcInsn((String) o1 + (String) o2);
				return (String) o1 + (String) o2;
			}
		} else if (binaryExpression.op.kind == TIMES) {
			mv.visitInsn(IMUL);
			if(o1 == null){
				o1 = new Integer(0);
			}
			if(o2 == null){
				o2 = new Integer(0);
			}
			if(o1.getClass().equals(ListExpression.class)){
				o1 = new Integer(0);
			}
			if(o2.getClass().equals(ListExpression.class)){
				o2 = new Integer(0);
			}
			return (Integer) o1 * (Integer) o2;
		} else if (binaryExpression.op.kind == MINUS) {
			mv.visitInsn(ISUB);
			if(o1 == null){
				o1 = new Integer(0);
			}
			if(o2 == null){
				o2 = new Integer(0);
			}
			if(o1.getClass().equals(ListExpression.class)){
				o1 = new Integer(0);
			}
			if(o2.getClass().equals(ListExpression.class)){
				o2 = new Integer(0);
			}
			return (Integer) o1 - (Integer) o2;
		} else if (binaryExpression.op.kind == DIV) {
			mv.visitInsn(IDIV);
			if(o1 == null){
				o1 = new Integer(0);
			}
			if(o2 == null){
				o2 = new Integer(0);
			}
			if(o1.getClass().equals(ListExpression.class)){
				o1 = new Integer(0);
			}
			if(o2.getClass().equals(ListExpression.class)){
				o2 = new Integer(0);
			}
			return (Integer) o1 / (Integer) o2;
		} else if (binaryExpression.op.kind == AND) {
			// mv.visitInsn(POP);
			// mv.visitInsn(POP);
			// mv.visitLdcInsn((boolean) o1 && (boolean) o2);
			mv.visitInsn(IAND);
			if(o1 == null){
				o1 =  false;
			}
			if(o2 == null){
				o2 = false;
			}
			return (boolean) o1 && (boolean) o2;

		} else if (binaryExpression.op.kind == BAR) {
			// mv.visitInsn(POP);
			// mv.visitInsn(POP);
			// mv.visitLdcInsn((boolean) o1 || (boolean) o2);
			mv.visitInsn(IOR);
			if(o1 == null){
				o1 =  false;
			}
			if(o2 == null){
				o2 = false;
			}
			return (boolean) o1 || (boolean) o2;

		} else if (binaryExpression.op.kind == EQUAL) {
			//mv.visitInsn(POP);
			//mv.visitInsn(POP);
			//binaryExpression.expression0.expressionType.equals(intType)
			if (binaryExpression.expression0.expressionType.equals(intType)) {
				if(o1 == null){
					o1 = new Integer(0);
				}
				if(o2 == null){
					o2 = new Integer(0);
				}
				Label l1 = new Label();
				Label l2 = new Label();
				mv.visitJumpInsn(IF_ICMPEQ, l2);
				mv.visitInsn(ICONST_0);
				mv.visitJumpInsn(GOTO, l1);
				mv.visitLabel(l2);
				mv.visitInsn(ICONST_1);
				mv.visitLabel(l1);

				
//				mv.visitLdcInsn((Integer)o1 ==  (Integer)o2);
				return (Integer) o1 == (Integer) o2;
			} else if (binaryExpression.expression0.expressionType.equals(stringType)) {
				if(o1 == null){
					o1 = "";
				}
				if(o2 == null){
					o2 = "";
				}
				mv.visitInsn(POP);
				mv.visitInsn(POP);
//				Label l1 = new Label();
//				Label l2 = new Label();
//				mv.visitJumpInsn(IF_ICMPEQ, l2);
//				mv.visitInsn(ICONST_0);
//				mv.visitJumpInsn(GOTO, l1);
//				mv.visitLabel(l2);
//				mv.visitInsn(ICONST_1);
//				mv.visitLabel(l1);
				mv.visitLdcInsn(((String) o1).equals((String) o2));
				return ((String) o1).equals((String) o2);
			} else if (binaryExpression.expression0.expressionType.equals(booleanType)) {
				if(o1 == null){
					o1 = new Boolean(false);
				}
				if(o2 == null){
					o2 = new Boolean(false);
				}
				Label l1 = new Label();
				Label l2 = new Label();
				mv.visitJumpInsn(IF_ICMPEQ, l2);
				mv.visitInsn(ICONST_0);
				mv.visitJumpInsn(GOTO, l1);
				mv.visitLabel(l2);
				mv.visitInsn(ICONST_1);
				mv.visitLabel(l1);
				
				//mv.visitLdcInsn((Boolean) o1 == (Boolean) o2);
				return (Boolean) o1 == (Boolean) o2;
			}
		} else if (binaryExpression.op.kind == NOTEQUAL) {
			
			if (binaryExpression.expression0.expressionType.equals(intType)) {
				if(o1 == null){
					o1 = new Integer(0);
				}
				if(o2 == null){
					o2 = new Integer(0);
				}
				Label l1 = new Label();
				Label l2 = new Label();
				mv.visitJumpInsn(IF_ICMPNE, l2);
				mv.visitInsn(ICONST_0);
				mv.visitJumpInsn(GOTO, l1);
				mv.visitLabel(l2);
				mv.visitInsn(ICONST_1);
				mv.visitLabel(l1);
				
				//mv.visitLdcInsn((Integer) o1 != (Integer) o2);
				return (Integer) o1 != (Integer) o2;
			} else if (binaryExpression.expression0.expressionType.equals(stringType)) {
				mv.visitInsn(POP);
				mv.visitInsn(POP);
				if(o1 == null){
					o1 = "";
				}
				if(o2 == null){
					o2 = "";
				}
				mv.visitLdcInsn((!((String) o1).equals((String) o2)));
				return (!((String) o1).equals((String) o2));
			} else if (binaryExpression.expression0.expressionType.equals(booleanType)) {
				if(o1 == null){
					o1 = new Boolean(false);
				}
				if(o2 == null){
					o2 = new Boolean(false);
				}
				Label l1 = new Label();
				Label l2 = new Label();
				mv.visitJumpInsn(IF_ICMPNE, l2);
				mv.visitInsn(ICONST_0);
				mv.visitJumpInsn(GOTO, l1);
				mv.visitLabel(l2);
				mv.visitInsn(ICONST_1);
				mv.visitLabel(l1);
				
				//mv.visitLdcInsn((Boolean) o1 != (Boolean) o2);
				return (Boolean) o1 != (Boolean) o2;
			}
		} else if (binaryExpression.op.kind == LT) {
			if(o1 == null){
				o1 = new Integer(0);
			}
			if(o2 == null){
				o2 = new Integer(0);
			}
			if(o1.getClass().equals(ListExpression.class)){
				o1 = new Integer(0);
			}
			if(o2.getClass().equals(ListExpression.class)){
				o2 = new Integer(0);
			}
			Label l1 = new Label();
			Label l2 = new Label();
			mv.visitJumpInsn(IF_ICMPLT, l2);
			mv.visitInsn(ICONST_0);
			mv.visitJumpInsn(GOTO, l1);
			mv.visitLabel(l2);
			mv.visitLdcInsn(ICONST_1);
			mv.visitLabel(l1);

			return (Integer) o1 < (Integer) o2;
		} else if (binaryExpression.op.kind == GT) {
			if(o1 == null){
				o1 = new Integer(0);
			}
			if(o2 == null){
				o2 = new Integer(0);
			}
			if(o1.getClass().equals(ListExpression.class)){
				o1 = new Integer(0);
			}
			if(o2.getClass().equals(ListExpression.class)){
				o2 = new Integer(0);
			}
			Label l1 = new Label();
			Label l2 = new Label();
			mv.visitJumpInsn(IF_ICMPGT, l2);
			mv.visitInsn(ICONST_0);
			mv.visitJumpInsn(GOTO, l1);
			mv.visitLabel(l2);
			mv.visitLdcInsn(ICONST_1);
			mv.visitLabel(l1);

			return (Integer) o1 > (Integer) o2;
		} else if (binaryExpression.op.kind == LE) {
			if(o1 == null){
				o1 = new Integer(0);
			}
			if(o2 == null){
				o2 = new Integer(0);
			}
			if(o1.getClass().equals(ListExpression.class)){
				o1 = new Integer(0);
			}
			if(o2.getClass().equals(ListExpression.class)){
				o2 = new Integer(0);
			}
			Label l1 = new Label();
			Label l2 = new Label();
			mv.visitJumpInsn(IF_ICMPLE, l2);
			mv.visitInsn(ICONST_0);
			mv.visitJumpInsn(GOTO, l1);
			mv.visitLabel(l2);
			mv.visitLdcInsn(ICONST_1);
			mv.visitLabel(l1);

			return (Integer) o1 <= (Integer) o2;
		} else if (binaryExpression.op.kind == GE) {
			if(o1 == null){
				o1 = new Integer(0);
			}
			if(o2 == null){
				o2 = new Integer(0);
			}
			if(o1.getClass().equals(ListExpression.class)){
				o1 = new Integer(0);
			}
			if(o2.getClass().equals(ListExpression.class)){
				o2 = new Integer(0);
			}
			Label l1 = new Label();
			Label l2 = new Label();
			mv.visitJumpInsn(IF_ICMPGE, l2);
			mv.visitInsn(ICONST_0);
			mv.visitJumpInsn(GOTO, l1);
			mv.visitLabel(l2);
			mv.visitLdcInsn(ICONST_1);
			mv.visitLabel(l1);

			return (Integer) o1 >= (Integer) o2;
		}

		else {
			// to implement
		}

		return null;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		for (BlockElem elem : block.elems) {
			elem.visit(this, arg);
		}
		return null;
	}

	@Override
	public Object visitBooleanLitExpression(
			BooleanLitExpression booleanLitExpression, Object arg)
			throws Exception {
		// throw new UnsupportedOperationException(
		// "code generation not yet implemented");
		MethodVisitor mv = ((InheritedAttributes) arg).mv;
		mv.visitLdcInsn(booleanLitExpression.value);
		return booleanLitExpression.value;
	}

	@Override
	public Object visitClosure(Closure closure, Object arg) throws Exception {
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

	@Override
	public Object visitClosureDec(ClosureDec closureDeclaration, Object arg)
			throws Exception {
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

	@Override
	public Object visitClosureEvalExpression(
			ClosureEvalExpression closureExpression, Object arg)
			throws Exception {
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

	@Override
	public Object visitClosureExpression(ClosureExpression closureExpression,
			Object arg) throws Exception {
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

	@Override
	public Object visitExpressionLValue(ExpressionLValue expressionLValue,
			Object arg) throws Exception {
		// throw new UnsupportedOperationException(
		// "code generation not yet implemented");
		MethodVisitor mv = ((InheritedAttributes) arg).mv;
		Integer i = (Integer) expressionLValue.expression.visit(this, arg);
		ArrayList<Object> a = new ArrayList<Object>();
		a.add(expressionLValue.identToken.getText());
		a.add(i);
		mv.visitInsn(POP);
		return a;
	}

	@Override
	public Object visitExpressionStatement(
			ExpressionStatement expressionStatement, Object arg)
			throws Exception {
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression,
			Object arg) throws Exception {
		// throw new UnsupportedOperationException(
		// "code generation not yet implemented");
		MethodVisitor mv = ((InheritedAttributes) arg).mv;
		// System.out.println("here" + identExpression.expressionType);
		Object o = symtbl.get(identExpression.identToken.getText());
		if (identExpression.expressionType.startsWith("Ljava/util/")) {

			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, className,
					identExpression.identToken.getText(),
					"Ljava/util/ArrayList;");

			// mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "size",
			// "()I", false);

		} else {
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, className,
					identExpression.identToken.getText(),
					identExpression.expressionType);
		}

		// gotta
		// make
		// sure
		// get
		// the
		// var
		// with
		// the
		// right
		// type
		return o;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identLValue, Object arg)
			throws Exception {
		// throw new UnsupportedOperationException(
		// "code generation not yet implemented");
		MethodVisitor mv = ((InheritedAttributes) arg).mv;
		return identLValue.identToken.getText();
	}

	@Override
	public Object visitIfElseStatement(IfElseStatement ifElseStatement,
			Object arg) throws Exception {
		// throw new UnsupportedOperationException(
		// "code generation not yet implemented");
		MethodVisitor mv = ((InheritedAttributes) arg).mv;
		Object guard = ifElseStatement.expression.visit(this, arg);
		Label l1 = new Label();
		Label l2 = new Label();
		mv.visitJumpInsn(IFEQ, l2);
		ifElseStatement.ifBlock.visit(this, arg);
		mv.visitJumpInsn(GOTO, l1);
		mv.visitLabel(l2);		
		ifElseStatement.elseBlock.visit(this, arg);
		mv.visitLabel(l1);
		
		
//		mv.visitInsn(POP);
//
//		if ((Boolean) guard == true) {
//			ifElseStatement.ifBlock.visit(this, arg);
//		} else {
//			ifElseStatement.elseBlock.visit(this, arg);
//		}
		return null;
	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg)
			throws Exception {
		// throw new UnsupportedOperationException(
		// "code generation not yet implemented");
		MethodVisitor mv = ((InheritedAttributes) arg).mv;
		
		Object guard = ifStatement.expression.visit(this, arg);
		Label l1 = new Label();		
		mv.visitJumpInsn(IFEQ, l1);
		ifStatement.block.visit(this, arg);
		mv.visitJumpInsn(GOTO, l1);		
		mv.visitLabel(l1);
//		Object guard = ifStatement.expression.visit(this, arg);
//		mv.visitInsn(POP);
//
//		if ((Boolean) guard == true) {
//			ifStatement.block.visit(this, arg);
//		}
		return null;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression,
			Object arg) throws Exception {
		MethodVisitor mv = ((InheritedAttributes) arg).mv; // this should be the
															// first statement
															// of all visit
															// methods that
															// generate
															// instructions
		mv.visitLdcInsn(intLitExpression.value);
		return intLitExpression.value;
	}

	@Override
	public Object visitKeyExpression(KeyExpression keyExpression, Object arg)
			throws Exception {
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

	@Override
	public Object visitKeyValueExpression(
			KeyValueExpression keyValueExpression, Object arg) throws Exception {
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

	@Override
	public Object visitKeyValueType(KeyValueType keyValueType, Object arg)
			throws Exception {
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

	@Override
	public Object visitListExpression(ListExpression listExpression, Object arg)
			throws Exception {
		// throw new UnsupportedOperationException(
		// "code generation not yet implemented");
		MethodVisitor mv = ((InheritedAttributes) arg).mv;

		mv.visitTypeInsn(NEW, "java/util/ArrayList");
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>",
				"()V", false);

		// mv.visitFieldInsn(PUTFIELD, className, "l1", "Ljava/util/List;");
		return listExpression;
	}

	@Override
	public Object visitListOrMapElemExpression(
			ListOrMapElemExpression listOrMapElemExpression, Object arg)
			throws Exception {
		// throw new UnsupportedOperationException(
		// "code generation not yet implemented");
		MethodVisitor mv = ((InheritedAttributes) arg).mv;
		ListExpression l = (ListExpression) symtbl
				.get(listOrMapElemExpression.identToken.getText());
		// System.out.println(l.expressionType);
		if (l.expressionType.equals(booleanType)) {

			// mv.visitVarInsn(ALOAD, 0);
			// mv.visitFieldInsn(GETFIELD, "Test", "a",
			// "Ljava/util/ArrayList;");
			// mv.visitInsn(ICONST_0);
			// mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "get",
			// "(I)Ljava/lang/Object;", false);

			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, className,
					listOrMapElemExpression.identToken.getText(),
					"Ljava/util/ArrayList;");
			listOrMapElemExpression.expression.visit(this, arg);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "get",
					"(I)Ljava/lang/Object;", false);
			mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean",
					"booleanValue", "()Z", false);
			// mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean",
			// "intValue", "()I", false);
			// return new Boolean(true);
		} else if (l.expressionType.equals(stringType)) {

			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, className,
					listOrMapElemExpression.identToken.getText(),
					"Ljava/util/ArrayList;");
			listOrMapElemExpression.expression.visit(this, arg);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "get",
					"(I)Ljava/lang/Object;", false);
			mv.visitTypeInsn(CHECKCAST, "java/lang/String");
			// mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean",
			// "booleanValue", "()Z", false);
			// mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean",
			// "intValue", "()I", false);
			// return "";
		} else if (l.expressionType.equals(intType)) {
			// System.out.println("second");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, className,
					listOrMapElemExpression.identToken.getText(),
					"Ljava/util/ArrayList;");
			listOrMapElemExpression.expression.visit(this, arg);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "get",
					"(I)Ljava/lang/Object;", false);
			mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue",
					"()I", false);
			// return new Integer(0);
		} else {
			// System.out.println("first");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, className,
					listOrMapElemExpression.identToken.getText(),
					"Ljava/util/ArrayList;");
			Integer i = (Integer) listOrMapElemExpression.expression.visit(
					this, arg);

			IdentExpression tmp = (IdentExpression) (l.expressionList.get(i));
			l = (ListExpression) symtbl.get(tmp.identToken.getText());
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "get",
					"(I)Ljava/lang/Object;", false);
			mv.visitTypeInsn(CHECKCAST, "java/util/ArrayList");
			// mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer",
			// "intValue", "()I", false);
			// return new ArrayList<Object>();
		}

		return l;

	}

	@Override
	public Object visitListType(ListType listType, Object arg) throws Exception {
		// throw new UnsupportedOperationException(
		// "code generation not yet implemented");
		return listType.type.getJVMType();
	}

	@Override
	public Object visitMapListExpression(MapListExpression mapListExpression,
			Object arg) throws Exception {
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

	@Override
	public Object visitPrintStatement(PrintStatement printStatement, Object arg)
			throws Exception {
		MethodVisitor mv = ((InheritedAttributes) arg).mv;
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(printStatement.firstToken.getLineNumber(), l0);
		mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out",
				"Ljava/io/PrintStream;");
		printStatement.expression.visit(this, arg); // adds code to leave value
													// of expression on top of
													// stack.
													// Unless there is a good
													// reason to do otherwise,
													// pass arg down the tree
		String etype = printStatement.expression.getType();
		// System.out.println(etype);
		if (etype.equals("I") || etype.equals("Z")
				|| etype.equals("Ljava/lang/String;")) {
			String desc = "(" + etype + ")V";
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
					desc, false);

			// mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream",
			// "println",
			// "(" + "Ljava/lang/Object;" + ")V", false);
		} else
			// throw new UnsupportedOperationException(
			// "printing list or map not yet implemented");

			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
					"(I)V", false);
		return null;
	}

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		className = program.JVMName;
		classDescriptor = 'L' + className + ';';
		cw.visit(52, // version
				ACC_PUBLIC + ACC_SUPER, // access codes
				className, // fully qualified classname
				null, // signature
				"java/lang/Object", // superclass
				new String[] { "cop5555sp15/Codelet" } // implemented interfaces
		);
		cw.visitSource(null, null); // maybe replace first argument with source
									// file name

		// create init method
		{
			MethodVisitor mv;
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(3, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>",
					"()V", false);
			mv.visitInsn(RETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", classDescriptor, null, l0, l1, 0);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}

		// generate the execute method
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "execute", // name of top
																	// level
																	// method
				"()V", // descriptor: this method is parameterless with no
						// return value
				null, // signature. This is null for us, it has to do with
						// generic types
				null // array of strings containing exceptions
				);
		mv.visitCode();
		Label lbeg = new Label();
		mv.visitLabel(lbeg);
		mv.visitLineNumber(program.firstToken.lineNumber, lbeg);
		program.block.visit(this, new InheritedAttributes(mv));
		mv.visitInsn(RETURN);
		Label lend = new Label();
		mv.visitLabel(lend);
		mv.visitLocalVariable("this", classDescriptor, null, lbeg, lend, 0);
		mv.visitMaxs(0, 0); // this is required just before the end of a method.
							// It causes asm to calculate information about the
							// stack usage of this method.
		mv.visitEnd();

		cw.visitEnd();
		return cw.toByteArray();
	}

	@Override
	public Object visitQualifiedName(QualifiedName qualifiedName, Object arg) {
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

	@Override
	public Object visitRangeExpression(RangeExpression rangeExpression,
			Object arg) throws Exception {
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

	@Override
	public Object visitReturnStatement(ReturnStatement returnStatement,
			Object arg) throws Exception {
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

	@Override
	public Object visitSimpleType(SimpleType simpleType, Object arg)
			throws Exception {
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

	@Override
	public Object visitSizeExpression(SizeExpression sizeExpression, Object arg)
			throws Exception {
		// throw new UnsupportedOperationException(
		// "code generation not yet implemented");
		MethodVisitor mv = ((InheritedAttributes) arg).mv;
		sizeExpression.expression.visit(this, arg);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "size", "()I",
				false);
		return null;
	}

	@Override
	public Object visitStringLitExpression(
			StringLitExpression stringLitExpression, Object arg)
			throws Exception {
		// throw new UnsupportedOperationException(
		// "code generation not yet implemented");
		MethodVisitor mv = ((InheritedAttributes) arg).mv;
		mv.visitLdcInsn(stringLitExpression.value);
		return stringLitExpression.value;
	}

	@Override
	public Object visitUnaryExpression(UnaryExpression unaryExpression,
			Object arg) throws Exception {
		// throw new UnsupportedOperationException(
		// "code generation not yet implemented");
		MethodVisitor mv = ((InheritedAttributes) arg).mv;
		if (unaryExpression.op.getText().equals("!")) {
			Object o = unaryExpression.expression.visit(this, arg);
			Label l1 = new Label();
			Label l2 = new Label();
			mv.visitJumpInsn(IFNE, l1);
			mv.visitInsn(ICONST_1);
			mv.visitJumpInsn(GOTO, l2);
			mv.visitLabel(l1);
			mv.visitInsn(ICONST_0);
			mv.visitLabel(l2);

			if(o == null){
				o = new Boolean(false);
			}

			return !(Boolean) o;
		} else {
			Object o = unaryExpression.expression.visit(this, arg);
			mv.visitInsn(POP);
			if(o == null){
				o = new Integer(0);
			}
			mv.visitLdcInsn(-(Integer) o);
			return -(Integer) o;
		}
	}

	@Override
	public Object visitValueExpression(ValueExpression valueExpression,
			Object arg) throws Exception {
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

	@Override
	public Object visitVarDec(VarDec varDec, Object arg) throws Exception {
		// throw new UnsupportedOperationException(
		// "code generation not yet implemented");
		// start here
		MethodVisitor mv = ((InheritedAttributes) arg).mv;

		// varDec.type.visit(this, arg);
		// System.out.println("Hello" + varDec.type.);
		if (varDec.type.getJVMType().startsWith("Ljava/util/")) {

			if (varDec.type.getJVMType().equals("Ljava/util/List<Z>;")) {
				fv = cw.visitField(0, varDec.identToken.getText(),
						"Ljava/util/ArrayList;",
						"Ljava/util/ArrayList<Ljava/lang/Boolean;>;", null);

			} else if (varDec.type.getJVMType().equals("Ljava/util/List<I>;")) {
				fv = cw.visitField(0, varDec.identToken.getText(),
						"Ljava/util/ArrayList;",
						"Ljava/util/ArrayList<Ljava/lang/Integer;>;", null);
			} else if (varDec.type.getJVMType().equals(
					"Ljava/util/List<Ljava/util/List<I>;>;")) {

				fv = cw.visitField(0, varDec.identToken.getText(),
						"Ljava/util/ArrayList;",
						"Ljava/util/ArrayList<Ljava/util/ArrayList<I>;>;", null);
			} else {
				fv = cw.visitField(0, varDec.identToken.getText(),
						"Ljava/util/ArrayList;",
						"Ljava/util/ArrayList<Ljava/lang/String;>;", null);
			}
			fv.visitEnd();

		} else {
			
			
			fv = cw.visitField(0, varDec.identToken.getText(),
					varDec.type.getJVMType(), null, null);
			fv.visitEnd();
//			mv.visitVarInsn(ALOAD, 0);
//			mv.visitLdcInsn(0);
//			mv.visitFieldInsn(PUTFIELD, className, varDec.identToken.getText(), intType);
			
		}
		// fv = cw.visitField(0, varDec.identToken.getText(),
		// varDec.type.getJVMType(), null, null);
		
		
		

		return null;
	}

	@Override
	public Object visitWhileRangeStatement(
			WhileRangeStatement whileRangeStatement, Object arg)
			throws Exception {
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

	@Override
	public Object visitWhileStarStatement(WhileStarStatement whileStarStatment,
			Object arg) throws Exception {
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg)
			throws Exception {
		// throw new UnsupportedOperationException(
		// "code generation not yet implemented");
		/**
		 * While loops
		 * 
		 * Type checking: guard is a boolean
		 * 
		 * Code generation: goto L1 L2: body L1: evaluate guard IFNE L2
		 * 
		 */
		MethodVisitor mv = ((InheritedAttributes) arg).mv;
		// Object guard = whileStatement.expression.visit(this, arg);
		// mv.visitInsn(POP);
		//
		// while((Boolean)guard == true){
		// whileStatement.block.visit(this, arg);
		// guard = whileStatement.expression.visit(this, arg);
		// mv.visitInsn(POP);
		// }
		Label l1 = new Label();
		mv.visitJumpInsn(GOTO, l1);
		Label l2 = new Label();
		mv.visitLabel(l2);
		whileStatement.block.visit(this, arg);
		mv.visitLabel(l1);
		Object guard = whileStatement.expression.visit(this, arg);
		// mv.visitInsn(POP);
		// if((boolean)guard){
		// mv.visitInsn(ICONST_1);
		// }
		// else{
		// mv.visitInsn(ICONST_0);
		// }
		mv.visitJumpInsn(IFNE, l2);

		return null;
	}

	@Override
	public Object visitUndeclaredType(UndeclaredType undeclaredType, Object arg)
			throws Exception {
		throw new UnsupportedOperationException(
				"code generation not yet implemented");
	}

}
