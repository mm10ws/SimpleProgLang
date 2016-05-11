package cop5555sp15;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.util.List;

import cop5555sp15.ast.ASTNode;
import cop5555sp15.ast.CodeGenVisitor;
import cop5555sp15.ast.Program;
import cop5555sp15.ast.TypeCheckVisitor;
import cop5555sp15.ast.TypeCheckVisitor.TypeCheckException;
import cop5555sp15.symbolTable.SymbolTable;

public class CodeletBuilder {

	public static class DynamicClassLoader extends ClassLoader {
		public DynamicClassLoader(ClassLoader parent) {
			super(parent);
		}

		public Class<?> define(String className, byte[] bytecode) {
			return super.defineClass(className, bytecode, 0, bytecode.length);
		}
	};

	public static ASTNode parseCorrectInput(String input) {
		TokenStream stream = new TokenStream(input);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		Parser parser = new Parser(stream);
		// System.out.println();
		ASTNode ast = parser.parse();
		if (ast == null) {
			System.out.println("errors " + parser.getErrors());
		}
		assertNotNull(ast);
		return ast;
	}

	public static ASTNode parseCorrectInput(File file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		TokenStream stream = new TokenStream(br);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		Parser parser = new Parser(stream);
		// System.out.println();
		ASTNode ast = parser.parse();
		if (ast == null) {
			System.out.println("errors " + parser.getErrors());
		}
		assertNotNull(ast);
		return ast;
	}

	public static ASTNode typeCheckCorrectAST(ASTNode ast) throws Exception {
		SymbolTable symbolTable = new SymbolTable();
		TypeCheckVisitor v = new TypeCheckVisitor(symbolTable);
		try {
			ast.visit(v, null);
		} catch (TypeCheckException e) {
			System.out.println(e.getMessage());
			fail("no errors expected");
		}
		return ast;
	}

	public static byte[] generateByteCode(ASTNode ast) throws Exception {
		CodeGenVisitor v = new CodeGenVisitor();
		byte[] bytecode = (byte[]) ast.visit(v, null);
		// dumpBytecode(bytecode);
		return bytecode;
	}

	public static Codelet getCodelet(String name, byte[] bytecode)
			throws InstantiationException, IllegalAccessException,
			MalformedURLException, ClassNotFoundException {
		DynamicClassLoader loader = new DynamicClassLoader(Thread
				.currentThread().getContextClassLoader());
		Class<?> testClass = loader.define(name, bytecode);
		Codelet codelet = (Codelet) testClass.newInstance();

		// codelet.execute();
		return codelet;
	}

	public static Codelet newInstance(String source) throws Exception {
		Program program = (Program) parseCorrectInput(source);
		assertNotNull(program);
		typeCheckCorrectAST(program);
		byte[] bytecode = generateByteCode(program);
		assertNotNull(bytecode);
		// System.out.println("\nexecuting bytecode:");
		Codelet codelet = getCodelet(program.JVMName, bytecode);
		return codelet;
	}

	public static Codelet newInstance(File file) throws Exception {
		// The input source file must be placed in the root directory of the
		// project.

		Program program = (Program) parseCorrectInput(file);
		assertNotNull(program);
		typeCheckCorrectAST(program);
		byte[] bytecode = generateByteCode(program);
		assertNotNull(bytecode);
		// System.out.println("\nexecuting bytecode:");
		Codelet codelet = getCodelet(program.JVMName, bytecode);
		return codelet;
	}

	@SuppressWarnings("rawtypes")
	public static List getList(Codelet codelet, String name) throws Exception {
		Class<? extends Codelet> codeletClass = codelet.getClass();
		Field l1Field = codeletClass.getDeclaredField(name);
		l1Field.setAccessible(true);
		List l = (List) l1Field.get(codelet);
		return l;

	}

	public static int getInt(Codelet codelet, String name) throws Exception {
		Class<? extends Codelet> codeletClass = codelet.getClass();
		Field l1Field = codeletClass.getDeclaredField(name);
		l1Field.setAccessible(true);
		int i = (int) l1Field.get(codelet);		
		return i;

	}

	public static void setInt(Codelet codelet, String name, int value)
			throws Exception {
		Class<? extends Codelet> codeletClass = codelet.getClass();
		Field l1Field = codeletClass.getDeclaredField(name);
		l1Field.setAccessible(true);
		l1Field.set(codelet, value);

	}

	public static String getString(Codelet codelet, String name)
			throws Exception {
		Class<? extends Codelet> codeletClass = codelet.getClass();
		Field l1Field = codeletClass.getDeclaredField(name);
		l1Field.setAccessible(true);
		String s = (String) l1Field.get(codelet);
		return s;

	}

	public static void setString(Codelet codelet, String name, String value)
			throws Exception {
		Class<? extends Codelet> codeletClass = codelet.getClass();
		Field l1Field = codeletClass.getDeclaredField(name);
		l1Field.setAccessible(true);
		l1Field.set(codelet, value);

	}

	public static boolean getBoolean(Codelet codelet, String name)
			throws Exception {
		Class<? extends Codelet> codeletClass = codelet.getClass();
		Field l1Field = codeletClass.getDeclaredField(name);
		l1Field.setAccessible(true);
		boolean b = (boolean) l1Field.get(codelet);
		return b;
	}

	public static void setBoolean(Codelet codelet, String name, boolean value)
			throws Exception {
		Class<? extends Codelet> codeletClass = codelet.getClass();
		Field l1Field = codeletClass.getDeclaredField(name);
		l1Field.setAccessible(true);
		l1Field.set(codelet, value);

	}
}
