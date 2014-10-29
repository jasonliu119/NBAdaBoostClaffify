package other_packages;

public class Attribute {
	int index;
	int value;
	
	public Attribute(int i,int v){
		index=i;
		value=v;
	}
	
	public Attribute(String record){
		String[] split=record.split(":");
		if(split.length!=2)
			System.out.println("Attribute Constructor Error: Invalid Record!");
		else
		{
			this.index=Integer.valueOf(split[0]);
			this.value=Integer.valueOf(split[1]);
			//System.out.println(this);
		}
	}
	
	public boolean equals(Object o){
		if(o==null)
			return false;
		Attribute other=(Attribute) o;
		
		if((this.index==other.index)&&(this.value==other.value))
			return true;
		
		return false;
		
	}
	
	public int hashCode(){
		return this.index*11+this.value;
	}
	
	
	
	public String toString(){
		return String.valueOf(this.index)+":"+String.valueOf(this.value);
	}
}
