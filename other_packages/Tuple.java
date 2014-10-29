package other_packages;

import java.util.HashMap;

public class Tuple {
	public int classlabel;//+1,-1
	public int classresult;
	public double weight;
	public HashMap<Integer,Integer> indexvaluemap;
	
	public Tuple(){
		
	}
	public Tuple(String line){
		indexvaluemap=new HashMap<Integer,Integer>();
		String[] split=line.split(" ");
		if(split.length<2){
			//System.out.println(" Tuple Constructor Error: Invalid Line");
			return;
		}
		
		if(split[0].equals("+1"))
			this.classlabel=1;
		else
			this.classlabel=-1;
		
		for(int i=1;i<split.length;i++){
			String[] attri=split[i].split(":");
			this.indexvaluemap.put(Integer.valueOf(attri[0]), Integer.valueOf(attri[1]));
		}
	}
}
