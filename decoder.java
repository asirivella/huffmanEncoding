import java.io.*;
import java.util.*;

public class decoder {
	public static Node head = new Node("*", 0);
	public static void constructDecodeTree(String fileName){
		try{
			Scanner scan = new Scanner(new FileReader(fileName));
			while(scan.hasNextLine()){
				Node node = head;
				String s = scan.nextLine();
				String key = s.split(" ")[0];
				String val = s.split(" ")[1];
				for(int i = 0; i < val.length(); i++){
					if(val.charAt(i) == '0'){
						if(i < val.length() - 1){
							if(node.left == null) node.left = new Node("*", 0);
							node = node.left;
						}else{
							node.left = new Node(key, 0);
						}
					}else{
						if(i < val.length() - 1){
							if(node.right == null)	node.right = new Node("*", 0);
							node = node.right;
						}else{
							node.right = new Node(key, 0);
						}
					}
				}
			}
			scan.close();
		}catch(Exception e){ 
			System.out.println(e);
		}
	}
	public static void main(String[] args){
		long tStart = System.currentTimeMillis();
		String encodedFileName = args[0];
		String codeTableFileName = args[1];
		constructDecodeTree(codeTableFileName);
		System.out.println("Decode Tree created");

		long tEnd = System.currentTimeMillis();
		long tDelta = tEnd - tStart;
		double elapsedSeconds = tDelta / 1000.0;
		System.out.println("Time to decode huffman codes: " + elapsedSeconds);
		
		try{
			
			File encodedFile = new File(encodedFileName);
	        InputStream input = new BufferedInputStream(new FileInputStream(encodedFile));
	        PrintWriter writer = new PrintWriter("decode.txt", "UTF-8");
	        byte[] byteArray = new byte[(int)encodedFile.length()];
	        int readCount = 0;
	        while(readCount < byteArray.length){
	            int bytesGrabbed = input.read(byteArray, readCount, byteArray.length - readCount);
	            if (bytesGrabbed > 0){
	                readCount = readCount + bytesGrabbed;
	            }
	        }
	        input.close();
	        
			Node node = head;
			for(int n = 0; n < byteArray.length; n++){
				int i = 0;
				int str = byteArray[n] & 0xFF;
	            String s = String.format("%08d", Integer.parseInt(Integer.toBinaryString(str)));
	            while(i < s.length()){
	            	while(node != null && node.left != null && node.right != null && i < s.length()){
						if(s.charAt(i++) == '0'){
							node = node.left;
						}else{
							node = node.right;
						}
					}
	            	if(!node.data.equals("*")){
						writer.println(node.data);
						node = head;
					}
	            }
				
			}
			writer.close();
		}catch(Exception e){
			System.out.println(e);
		}
		
		tEnd = System.currentTimeMillis();
		tDelta = tEnd - tStart;
		elapsedSeconds = tDelta / 1000.0;
		System.out.println("Time to end the complete program: " + elapsedSeconds);
	}
}
