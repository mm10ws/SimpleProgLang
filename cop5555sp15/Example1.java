package cop5555sp15;

import java.io.File;
import java.util.List;

public class Example1{
	

	public static void main(String[] args) throws Exception{
//		This program calculates the GCD of two numbers
//		Initially the numbers are both zero
//		Then they are set to 26 and 13 using the codeletBuilder setInt method
//		
//		expected output:
//			The GCD is 
//			0
//			a = 0
//			b = 0
//			a = 26
//			b = 13
//			The GCD is 
//			13



			
		
		String source = "class GCD{\n"
		+ "def a: int;\n"
		+ "def b: int;\n"
		+ "while (a != b){\n"
		
		+ "if (a > b){a = a - b;}\n"
		+ "else {b = b - a;};\n"
		
		+ "};\n"
		+ "print \"The GCD is: \";"
		+ "print a;"
		+ "}";
		

		Codelet codelet = CodeletBuilder.newInstance(source);
		codelet.execute();
		int a = CodeletBuilder.getInt(codelet, "a");
		int b = CodeletBuilder.getInt(codelet, "b");
		System.out.println("a = " + a);
		System.out.println("b = " + b);
		CodeletBuilder.setInt(codelet, "a", 26); //change a or b to calculate GCD of different numbers
		CodeletBuilder.setInt(codelet, "b", 13);
		a = CodeletBuilder.getInt(codelet, "a");
		b = CodeletBuilder.getInt(codelet, "b");
		System.out.println("a = " + a);
		System.out.println("b = " + b);
		codelet.execute();
		
	}

	
}