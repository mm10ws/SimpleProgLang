package cop5555sp15;

import java.io.File;
import java.util.List;

public class Example2{
	

	public static void main(String[] args) throws Exception{
//		This program calculates the sum of a list with elements from 1 to 10
//		the sum is returned by using the CodeletBuilder getInt method
//		
//		expected output:
//			sum = 55

			
		
		String source = "class Sum{\n"
		+ "def sum: int;\n"
		+ "def l: @[int];\n"
		+ "def i: int;\n"
		+ "l = @[1,2,3,4,5,6,7,8,9,10];"
		
		+ "while (i < size(l)){\n"
		+ "sum = sum + l[i];"
		+ "i = i + 1;"
		
		+ "};\n"
		
		+ "}";
		

		Codelet codelet = CodeletBuilder.newInstance(source);
		codelet.execute();
		int sum = CodeletBuilder.getInt(codelet, "sum");		
		System.out.println("sum = " + sum); 		
	}

	
}