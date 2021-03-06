import java.util.Arrays;

public class CuckooHash<K> {

	class Node<S>{
		
		S data;
		
		Node(S data){
			this.data = data;
		}
		
	}
	
	
	int capacity = 100;
	double threshold = 0.75;
	int maxRoundLoops = 16;
	int size = 0;
	Node<K> arr1[];
	Node<K> arr2[];
	int numbers[] = {11,23,37,61,123};
	int append = 0;
	long memory = 0;
	long cumelativeMemory = 0;
	K incomingElement = null;
	
	public int getCapacity() {
		return capacity;
	}

	CuckooHash(int capacity, int maxRoundLoops){
		this.capacity = capacity;
		this.maxRoundLoops = maxRoundLoops;
		arr1 = new Node[capacity];
		arr2 = new Node[capacity];
		Arrays.fill(arr2, null);
		Arrays.fill(arr1, null);
	}
	
	public void put(K data){
		incomingElement = data;
		Node<K> newNode = new Node<K>(data);
		int key1 = hash1(data);
		int key2 = hash2(data);
		if((arr1[key1] != null && arr1[key1].data.equals(data)) || (arr2[key2] != null && arr2[key2].data.equals(data))){
			
			//System.out.println("element "+data+" already present");
			return;
			
		}else{
			memory = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
			if(insertToTableOne(key1, newNode, 0, newNode.data)){
				size++;
				//System.out.println("out:"+memory);
				cumelativeMemory += memory;
			}
		}
	}
	
	private boolean insertToTableOne(int key, Node<K> n, int count, K data){
		if(count > maxRoundLoops){// && data.equals(n.data)){
			maxLoopsStuck(data);
			return insertToTableOne(hash1(n.data), n, 0, data);
		}else if(arr1[key] == null){
			arr1[key] = n;
			//System.out.println("in:"+(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory() - memory));
			//memory = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory() - memory; 
			//cumelativeMemory += memory;
			memory = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory() - memory; 
			return true;
		}else{
			Node<K> temp = arr1[key];
			arr1[key] = n;
			return insertToTableTwo(hash2(temp.data), temp, ++count, data);
		}
	}
	
	
	
	private boolean insertToTableTwo(int key, Node<K> n, int count, K data){
		if(count > maxRoundLoops){// && data.equals(n.data)){
			maxLoopsStuck(data);
			return insertToTableOne(hash1(n.data), n, 0, data);
		}else if(arr2[key] == null){
			arr2[key] = n;
			//System.out.println("in:"+(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory() - memory));
			//memory = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory() - memory; 
			//cumelativeMemory += memory; 
			memory = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory() - memory; 
			return true;
		}else{
			Node<K> temp = arr2[key];
			arr2[key] = n;
			return insertToTableOne(hash1(temp.data), temp, ++count, data);
		}
	}
	
	private void maxLoopsStuck(K data){
		Node<K> temp1[] = arr1;
		Node<K> temp2[] = arr2;
		capacity = capacity + numbers[append];
		if(append < 4){
			append++;
		}
		size = 0;
		arr1 = new Node[capacity];
		arr2 = new Node[capacity];
		Arrays.fill(arr1, null);
		Arrays.fill(arr2, null);
		for(int i = 0 ; i < temp1.length; i++){
			if(temp1[i] != null){
				if(temp1[i].data == incomingElement)
					put(data);
				else
					put(temp1[i].data);
				
			}
			if(temp2[i] != null){
				if(temp2[i].data == incomingElement)
					put(data);
				else
					put(temp2[i].data);
			}
		}
	}
	
	private int  hash1(K data){
		int key = 0;
		key = ((data.hashCode() & 0xf7777777)*7 + 29) % (capacity/2);
		return key;
	}
	
	private int hash2(K data){
		int key = 0;
		int hash = data.hashCode();
		int modified = 0;
		int count = 0;
		while (hash != 0){
			count ++;
			int remainder = hash%7;
			hash = hash/7;
			remainder *= count;
			//remainder += (7* (count/2)); 
			modified += (remainder * 11);
		}
		key = ((modified & 0xf7777777)) % (capacity);
		return key;
	}
	
	public boolean delete(K data){
		int key1 = hash1(data);
		int key2 = hash2(data);
		if(arr1[key1] != null && arr1[key1].data.equals(data)){
			arr1[key1] = null;
			size--;
			return true;
		}else if(arr2[key2] != null && arr2[key2].data.equals(data)){
			arr2[key2] = null;
			size--;
			return true;
		}else{
			System.out.println(data+" is not on the table");
			return false;
		}
	}
	
	public int size(){
		return size;
	}
	
	public double loadFactor(){
		return (1.0*size)/capacity;
	}
	
	public long avgMemoryUsagePerInsertion(){
		return cumelativeMemory/size;
	}
	
}
