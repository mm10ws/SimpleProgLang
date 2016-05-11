package cop5555sp15;

import java.io.File;
import java.util.List;

public class testCodeletBuilder{
	
//	public static void main(String args[]) throws Exception{
//		String source = "class A {def xi: int;  \n print xi; }";
//		//File f = new File("t.txt");
//		Codelet codelet = CodeletBuilder.newInstance(source);
//		codelet.execute();
//		CodeletBuilder.setInt(codelet, "xi", 3);
//		codelet.execute();
//
//	}
	public static void main(String[] args) throws Exception{
		String source = "class CallExecuteTwice{\n"
		+ "def i1: int;\n"
		+ "if (i1 == 0){print \"first time\";}\n"
		+ "else {print \"second time\";};\n"
		+ "}";
		Codelet codelet = CodeletBuilder.newInstance(source);
		codelet.execute();
		int i1 = CodeletBuilder.getInt(codelet, "i1");
		System.out.println(i1);
		CodeletBuilder.setInt(codelet, "i1", i1+2);
		System.out.println(CodeletBuilder.getInt(codelet, "i1"));
		codelet.execute();
		}

	
}