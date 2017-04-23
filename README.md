# huffmanEncoding
This project is to develop a program that generates Huffman codes. With evaluating which of the following priority queue structures gives best performance: Binary Heap, 4-way heap, and Pairing Heap. Implemented in Java without use of any standard library container.


Language and Compiler Information:- 
 Java – version 1.8.0_65 
 Laptop specifications - Core i5 (2.3GHz), 8GB RAM & windows 10 
 Tested for input size of 1 million. 
 Directory structure: 

 Step to compile:  
	+ Javac encoder.java  
	Creates the following files:  
		- encoder.class 
		- Node.class 
		- Heap.class 
		- TestHeap.class 
		- MinHeap.class 
		- FourWayHeap.class 
		- PairingHeap.class 
		- PairingHeap$TreeNode.class 

	+ Javac decoder.java  
	Creates the following files:  
		- decoder.class 
 
 
 Steps to execute:  
	+ Java encoder <input_Filename/> 
	+ Java decoder <Code_table_filename/> <encoded_filename/> 
	+ Input_Filename: File with initial configuration for building a Huffman tree and encoding the contents accordingly. 
	+ Code_Table_filename: File with values and Huffman encoded value, space separated 
	+ Encoded_fileName: Binary file which contains all the value Huffman encoded. 
 
 