import java.io.*;
import java.util.*;

class encoder {
	public static Heap heap;
	
	public static void buildFrequencyTable(String fileName){
		try{
			Map<String, Integer> freqTable = new HashMap<String, Integer>();
			Scanner scan = new Scanner(new FileReader(fileName));
			while(scan.hasNext()){
				String s = scan.next();
				freqTable.put(s, freqTable.getOrDefault(s, 0) + 1);
			}
			scan.close();
			
			//Choose the type of heap you want to use.
			//heap = new TestHeap();
			heap  = new MinHeap(freqTable.size());
			//heap = new FourWayHeap(freqTable.size());
			//heap = new PairingHeap(freqTable.size());
			
			
			Iterator<Map.Entry<String,Integer>> iter = freqTable.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String,Integer> entry = iter.next();
				heap.insert(new Node(entry.getKey(), entry.getValue()));
			    iter.remove();
			}
			freqTable.clear();
			heap.meld();
		}catch(Exception e){ 
			System.out.println(e);
		}
	}
	public static Node buildTree(Heap heap){
		while(heap.size > 1){
			Node node1 = heap.removeMin();
			Node node2 = heap.removeMin();
			Node p = new Node("*", node1.freq + node2.freq);
			
			p.left = node1;
			p.right = node2;
			heap.insert(p);
		}
		return heap.removeMin();
	}
	public static void buildEncodeMap(Map<String, String> map, Node node, String s){
		if(node == null) return;
		
		if(node.left == null && node.right == null){
			map.put(node.data, s);
		}else{
			buildEncodeMap(map, node.left, s + "0");
			buildEncodeMap(map, node.right, s + "1");
		}
	}
	public static void print(Node node){
		if(node == null) return;
		System.out.println(node + " : L = (" + node.left + "); R = ("+ node.right + ")");
		print(node.left);
		print(node.right);
	}
	public static void main(String[] args){
		String fileName = args[0];
		long tStart = System.currentTimeMillis();
		
		buildFrequencyTable(fileName);
		Node headNode = buildTree(heap);
		Map<String, String> encodeMap = new HashMap<String, String>();
		buildEncodeMap(encodeMap, headNode, "");
		//print(headNode);
		
		long tEnd = System.currentTimeMillis();
		long tDelta = tEnd - tStart;
		double elapsedSeconds = tDelta / 1000.0;
		System.out.println("Time to create huffman codes: " + elapsedSeconds);
		
		try{
		    PrintWriter writer = new PrintWriter("code_table.txt", "UTF-8");
		    for(String key: encodeMap.keySet()){
		    	writer.println(key + " " + encodeMap.get(key));
		    }
		    writer.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		
		try{
	        OutputStream output = new BufferedOutputStream(new FileOutputStream("encoded.bin"));
			Scanner scan = new Scanner(new FileReader(fileName));
			StringBuilder outStr = new StringBuilder("");
			while(scan.hasNext()){
				String s = scan.next();
				String msg = encodeMap.get(s);
				outStr.append(msg);
				if(outStr.length() % 8 == 0){
	                for (int i = 0; i < outStr.length(); i += 8) {
	                    String byteX = outStr.substring(i, i + 8).toString();
	                    int byteToInt = 0xFF & Integer.parseInt(byteX, 2);
	                    output.write(byteToInt);
	                }
	                outStr = new StringBuilder("");
	            }
			}
			scan.close();
			output.close();
		}catch(Exception e){
			System.out.println(e);
		}
		
		tEnd = System.currentTimeMillis();
		tDelta = tEnd - tStart;
		elapsedSeconds = tDelta / 1000.0;
		System.out.println("Time to end the complete program: " + elapsedSeconds);
	}
}


class Node implements Comparator<Node>{
	int freq;
	String data;
	Node left, right;
	Node(){}
	Node(String d, int f){
		this.data = d;
		this.freq = f;
	}
	public String toString(){
		return this.data + ": " + this.freq;
	}
	
	@Override
	public int compare(Node n1, Node n2){
		return n1.freq - n2.freq;
	}
}


abstract class Heap {
	int size = 0;
	public abstract void meld();
	public abstract Node removeMin();
	public abstract void insert(Node n);
}
	
//Priority Queue for testing of encoder and decoder functionality.
class TestHeap extends Heap{
	Queue<Node> queue = new PriorityQueue(1, new Node());
	TestHeap(){ }
	@Override
	public void meld(){}
	public Node removeMin(){
		this.size = queue.size() - 1;
		return queue.poll();
	}
	public void insert(Node n){
		if(n != null){
			this.size++;
			queue.offer(n);
		}
	}
}

//Binary Heap: MinHeap implementation
class MinHeap extends Heap{
    private Node[] Heap;
    
    public MinHeap(int maxsize){
    	this.size = 0;
        Heap = new Node[maxsize + 1];
        Heap[0] = new Node("*", Integer.MIN_VALUE);
    }
 
    private int parent(int pos){
        return pos / 2;
    }
 
    private int leftChild(int pos){
        return (2 * pos);
    }
 
    private int rightChild(int pos){
        return (2 * pos) + 1;
    }
 
    private boolean isLeaf(int pos){
        if (pos >  size / 2  &&  pos <= size){ 
            return true;
        }
        return false;
    }
 
    private void swap(int fpos, int spos){
        Node tmp = Heap[fpos];
        Heap[fpos] = Heap[spos];
        Heap[spos] = tmp;
    }
    
    public void meld(){
    	
    }

    public Node removeMin(){
    	swap(1, this.size--);
        if(this.size > 0){
        	pushDown(1);
        }
        return Heap[size+1];
    }
    
    private void pushDown(int pos){
        int min;
        int leftChild = leftChild(pos);
        int rightChild = rightChild(pos);
        if (rightChild > this.size) {
              if (leftChild > this.size)
                    return;
              else
                    min = leftChild;
        } else {
              if (Heap[leftChild].freq <= Heap[rightChild].freq)
                    min = leftChild;
              else
                    min = rightChild;
        }
        if (Heap[pos].freq > Heap[min].freq) {
              swap(min, pos);
              pushDown(min);
        }
    }
 
    public void insert(Node element){
        Heap[++this.size] = element;
        shiftUp(this.size);
    }
    private void shiftUp(int pos) {
        int parent;
        if (pos != 0) {
              parent = parent(pos);
              if (Heap[parent].freq > Heap[pos].freq) {
            	  	swap(parent, pos);
                    shiftUp(parent);
              }
        }
    }
}


//4-way heap implementation
class FourWayHeap extends Heap{
	private Node[] Heap;
	
	public FourWayHeap(int maxsize){
		this.size = 0;
		Heap = new Node[maxsize];
		//Heap[0] = new Node("*", Integer.MIN_VALUE);
	}
	private int parent(int pos){
		return (pos - 1) / 4;
	}

	private int child(int pos, int i){
		return (4 * pos) + i;
	}

	private void swap(int fpos, int spos){
		Node tmp = Heap[fpos];
		Heap[fpos] = Heap[spos];
		Heap[spos] = tmp;
	}
  
	public void meld(){
		
	}

	public Node removeMin(){
		Node out = Heap[0];
		swap(0, --this.size);
		if(this.size > 0){
			pushDown(0);
		}
		return out;
	}  
	
	private void pushDown(int pos){
		int min = pos;
		if(min >= this.size) return;
		
		for(int i = 1; i <= 4; i++){
			int child = child(pos, i);
			if(child >= this.size) break;
			
			if(Heap[child].freq < Heap[min].freq){
				min = child;
			}
		}
		if (pos != min) {
            swap(min, pos);
            pushDown(min);
		}
	}

	public void insert(Node element){
        Heap[this.size] = element;
		int i = this.size;

        while(i > 0){
        	if(Heap[parent(i)].freq > Heap[i].freq){
        		swap(i, parent(i));
        	}
            i = parent(i);
        }
        this.size++;
	}
}


/*
class MinHeap extends Heap{
	TreeNode head;
	
	class TreeNode{
		Node node;
		TreeNode left, right;
		TreeNode(Node n){
			this.node = n;
		}
	}
	
	MinHeap(){	
		this.size = 1;
	}
	@Override
	public void meld(){
		this.head = heapify(head);
	}
	public void print(){
		print(head);
	}
	public void print(TreeNode node){
		if(node == null) return;
		System.out.println(node + " : L = (" + node.left + "); R = ("+ node.right + ")");
		print(node.left);
		print(node.right);
	}
	public TreeNode heapify(TreeNode node){
		if(node == null) return null;
		TreeNode leftTree = heapify(node.left);
		TreeNode rightTree = heapify(node.right);
		
		if(leftTree != null && rightTree != null){
			if(leftTree.node.freq < rightTree.node.freq){
				if(node.node.freq > leftTree.node.freq){
					Node tmp = leftTree.node;
					leftTree.node = node.node;
					node.node = tmp;
				}
			}else{
				if(node.node.freq > rightTree.node.freq){
					Node tmp = rightTree.node;
					rightTree.node = node.node;
					node.node = tmp;
				}
			}
		}else if(leftTree != null && leftTree.node.freq < node.node.freq){
			Node tmp = leftTree.node;
			leftTree.node = node.node;
			node.node = tmp;
		}else if(rightTree != null && rightTree.node.freq < node.node.freq){
			Node tmp = rightTree.node;
			rightTree.node = node.node;
			node.node = tmp;
		}else{
			// Do nothing
		}

		return node;
	}
	public Node removeMin(){
		if(head == null) return null;
		this.size--;
		Queue<TreeNode> queue = new LinkedList<TreeNode>();
		TreeNode tnode = head, prev = head;
		queue.offer(head);
		while(queue.size() > 0){
			tnode = queue.poll();
			if(tnode == null) continue;
			if(tnode.left != null) queue.offer(tnode.left);
			if(tnode.right != null) queue.offer(tnode.right);
			if(tnode.left == null && tnode.right == null) break;
			
			prev = tnode;
		}
		
		Node out = head.node;
		if(prev.right != null){
			head.node = prev.right.node;
			prev.right = null;
		}else if(prev.left != null){
			head.node = prev.left.node;
			prev.left = null;
		}else{
			
		}
		
		meld();
		return out;
	}
	
	public void insert(Node n){
		this.size++;
		if(head == null){
			head = new TreeNode(n);
			return;
		}
		
		Queue<TreeNode> queue = new LinkedList<TreeNode>();
		TreeNode tnode = head, prev = head;
		queue.offer(head);
		while(queue.size() > 0){
			tnode = queue.poll();
			if(tnode.left != null) queue.offer(tnode.left);
			if(tnode.right != null) queue.offer(tnode.right);
			if(tnode.left == null && tnode.right == null) break;
			
			prev = tnode;
		}
		
		if(prev.left == null){
			prev.left = new TreeNode(n);
		}else if(prev.right == null){
			prev.right = new TreeNode(n);
		}else{			
			tnode.left = new TreeNode(n);		
		}
		meld();
	}
}

class FourWayHeap extends Heap{
	TreeNode head;
	
	class TreeNode{
		Node node;
		List<TreeNode> child;
		TreeNode(Node n){
			this.node = n;
			this.child = new ArrayList<TreeNode>();
		}
	}
	
	FourWayHeap(){	}
	@Override
	public void meld(){
		head = heapify(head);
	}
	
	public TreeNode heapify(TreeNode node){
		if(node == null) return null;
		
		int min = Integer.MAX_VALUE, index = -1;
		List<TreeNode> subTree = new ArrayList<TreeNode>();
		for(int i = 0; i < node.child.size(); i++){
			TreeNode n = heapify(node.child.get(i));
			subTree.add(n);
			if(n != null){
				if(min > n.node.freq){
					min = n.node.freq;
					index = i;
				}
			}
		}
		if(index >= 0 
				&& subTree.get(index) != null 
				&& subTree.get(index).node.freq < node.node.freq){
			Node tmp = subTree.get(index).node;
			subTree.get(index).node = node.node;
			node.node = tmp;
		}

		return node;
	}
	
	public Node removeMin(){
		if(head == null) return null;
		this.size--;
		
		Queue<TreeNode> queue = new LinkedList<TreeNode>();
		TreeNode tnode = head, prev = head;
		queue.offer(head);
		while(queue.size() > 0){
			tnode = queue.poll();
			for(int i = 0; i < tnode.child.size(); i++) queue.offer(tnode.child.get(i));
			if(tnode.child.size() == 0) break;
			prev = tnode;
		}
		
		Node out = head.node;
		if(prev.child.size() > 0){
			head.node = prev.child.get(prev.child.size() - 1).node;
			prev.child.remove(prev.child.size() - 1);
		}else{
			head = null;
		}
		
		meld();
		return out;
	}
	
	public void insert(Node n){
		this.size++;
		if(head == null){
			head = new TreeNode(n);
			return;
		}
		
		Queue<TreeNode> queue = new LinkedList<TreeNode>();
		TreeNode tnode = head, prev = head;
		queue.offer(head);
		while(queue.size() > 0){
			tnode = queue.poll();
			for(int i = 0; i < tnode.child.size(); i++) queue.offer(tnode.child.get(i));
			if(tnode.child.size() == 0) break;
			prev = tnode;
		}
		
		if(prev.child.size() > 3) prev = tnode;
		prev.child.add(new TreeNode(n));
	}
}
*/

//Pairing Heap
class PairingHeap extends Heap{
	TreeNode Head;
	List<TreeNode> heap;
	
	class TreeNode{
		Node node;
		List<TreeNode> child;
		TreeNode(Node n){
			this.node = n;
			this.child = new ArrayList<TreeNode>();
		}
	}
	
	PairingHeap(){
		this.heap = new ArrayList<TreeNode>();
	}
	@Override
	public void meld(){
		if(heap.size() < 2) return;
		
		TreeNode tnode1, tnode2;
		Queue<TreeNode> queue = new LinkedList<TreeNode>();
		for(int i = 0; i < heap.size(); i++){
			queue.offer(heap.get(i));
		}
		
		while(queue.size() > 1){
			tnode1 = queue.poll();
			tnode2 = queue.poll();
			if(tnode1.node.freq > tnode2.node.freq){
				tnode2.child.add(0, tnode1);
				queue.offer(tnode2);
			}else{
				tnode1.child.add(0, tnode2);
				queue.offer(tnode1);
			}
		}
		heap.clear();
		heap.add(queue.poll());
	}
		
	public Node removeMin(){
		if(heap.size() == 0) return null;
		if(heap.size() > 1) meld();
		this.size--;
		
		Queue<TreeNode> queue = new LinkedList<TreeNode>();
		TreeNode head = heap.get(0);
		heap.clear();
		for(int i = 0; i < head.child.size(); i++){
			heap.add(head.child.get(i));
		}
		
		meld();
		return head.node;
	}
	
	public void insert(Node n){
		this.size++;
		if(heap.size() == 0){
			heap.add(new TreeNode(n));
			return;
		}
		heap.add(new TreeNode(n));
		meld();
	}
}
