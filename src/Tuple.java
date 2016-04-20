


public class Tuple implements Comparable<Tuple>{
	
	
	private String word;
	private Integer value;
	
	public Tuple(String w, Integer v){
		word = w;
		value = v;
	}
	
	public String getStr(){ return word; }
	
	public int getValue(){ return (int)value ; }

	@Override
	public String toString(){
		return (word + ": " + value);
	}
	
	@Override
	public int compareTo(Tuple o) {
		if (getValue() > o.getValue()) return -1;
		if (getValue() < o.getValue()) return 1;
		return 0;
	}
	
}
