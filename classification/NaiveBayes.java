package classification;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import other_packages.Attribute;
import other_packages.Constants;

public class NaiveBayes {
	
	//Bayes Theorem:
	//posteriori = likelihood x prior/evidence
	//P(H|X)=P(X|H)*P(H) /P(X) -->  
	//P(x1|H)*P(x2|H)*P(x3|H)...P(xn|H)*P(H)
	
	final int classcount=2;//Only 2 classes, mapping index 0 -- -1, index 1 -- +1
	int[] classsupport;
	int totalsupport;
	double[] Pros;
	ArrayList<HashMap<Attribute,Integer>> attributeCount;

	BufferedReader trainbr;
	String tranfile;
	String testfile;
	double train_tp,train_fp,train_tn,train_fn;
	double test_tp,test_fp,test_tn,test_fn;
	int train_true_positive=0,train_false_positive=0,train_true_negative=0,train_false_negative=0;
	int test_true_positive=0,test_false_positive=0,test_true_negative=0,test_false_negative=0;
	
	NaiveBayes(String ttranfile,String ttestfile){
		this.tranfile=ttranfile;
		this.testfile=ttestfile;
		
		this.classsupport=new int[this.classcount];
		this.Pros=new double[this.classcount];
		this.attributeCount=new ArrayList<HashMap<Attribute,Integer>>();
		
		for(int i=0;i<this.classcount;i++){
			classsupport[i]=0;
			this.attributeCount.add(new HashMap<Attribute,Integer>());
		}
		
		
		
	}
	

	public void displayAll(){
		//display:
		//true positive in training, false negative in training, false positive in training, true negative in training 
		//true positive in test, false negative in test, false positive in test, true negative in test
		System.out.print(this.train_true_positive+" ");
		System.out.print(this.train_false_negative+" ");
		System.out.print(this.train_false_positive+" ");
		System.out.print(this.train_true_negative+"\n");
		
		System.out.print(this.test_true_positive+" ");
		System.out.print(this.test_false_negative+" ");
		System.out.print(this.test_false_positive+" ");
		System.out.print(this.test_true_negative+"\n");
	}
	
	public int classify(String record){
		String[] split=record.split(" ");
		Attribute attri;
		double[] tmpPro=new double[this.classcount];
		double tmp=1.0;
		HashMap<Attribute,Integer> tmpmap;
		
		//P(x1|H)*P(x2|H)*P(x3|H)...P(xn|H)*P(H)
		
		for(int i=0;i<this.classcount;i++){
			tmp=1.0; //reset!!!
			tmp*=this.Pros[i];
			tmpmap=this.attributeCount.get(i);
			
			for(int a=1;a<split.length;a++){//for each attribute, we calculate the P(xi|H)
				attri=new Attribute(split[a]);
				if(tmpmap.containsKey(attri)){
					tmp*=(double)tmpmap.get(attri)/this.classsupport[i];
				}
				else{
					//System.out.println(	"   classify: No such attribute "+split[a]);
					//tmp*=1.0/(this.classsupport[i]+1); //Avoiding the Zero-Probability Problem 
					//just skip it?
				}
			}
				
			tmpPro[i]=tmp;
		}
		
		//System.out.println(tmpPro[0]+","+tmpPro[1]);
		
		if(tmpPro[1]>tmpPro[0]) //return the class with the maximal pros
			return 1;
		else
			return -1;
	}
	
	public void classifyDataFile(int train_flag){
		String file;
		if(train_flag==0){ //switch 0--train_file, 1--test_file
			file=this.tranfile;
		}
		else{
			file=this.testfile;
		}
		
		
		String tmpline;
		String[] split;
		int correctlabel;
		Attribute tmpattri;
		HashMap<Attribute,Integer> tmpmap;
		int true_positive=0,false_positive=0,true_negative=0,false_negative=0;
		
		try{
			
		BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(new File(file))));
		
		try {
			
			
			while((tmpline=br.readLine())!=null){
				split=tmpline.split(" ");
				if(split[0].equals("+1")){
					correctlabel=1;//mapping index 0 -- -1, index 1 -- +1
				}
				else if(split[0].equals("-1")){
					correctlabel=-1;//mapping index 0 -- -1, index 1 -- +1
				}
				else{
					//System.out.println("  preprocess Error: Neither +1 nor -1");
					continue;
				}
				
				int classfy_result=this.classify(tmpline);
				
				if(correctlabel==1&&classfy_result==1)
					++true_positive;
				else if(correctlabel==-1&&classfy_result==1)
					++false_positive;
				else if((correctlabel==-1&&classfy_result==-1))
					++true_negative;
				else if((correctlabel==1&&classfy_result==-1))
					++false_negative;
				
			}
			
			if(train_flag==0){
				this.train_true_positive=true_positive;
				this.train_false_positive=false_positive;
				this.train_true_negative=true_negative;
				this.train_false_negative=false_negative;		
			}
			else{
				this.test_true_positive=true_positive;
				this.test_false_positive=false_positive;
				this.test_true_negative=true_negative;
				this.test_false_negative=false_negative;	
			}
			
			br.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	
	public void preprocess(){
		try {
			trainbr=new BufferedReader(new InputStreamReader(new FileInputStream(new File(this.tranfile))));
			String tmpline;
			String[] split;
			int currentlabel;
			Attribute tmpattri;
			HashMap<Attribute,Integer> tmpmap;
			//Format: <label> <index1>:<value1> <index2>:<value2> ...
			try {
				while((tmpline=this.trainbr.readLine())!=null){
					split=tmpline.split(" ");

					if(split[0].equals("+1")){
						currentlabel=1;//mapping index 0 -- -1, index 1 -- +1
					}
					else if(split[0].equals("-1")){
						currentlabel=0;//mapping index 0 -- -1, index 1 -- +1
					}
					else{
						//System.out.println("  preprocess Error: Neither +1 nor -1");
						continue;
					}
					
					++this.classsupport[currentlabel];
					tmpmap=this.attributeCount.get(currentlabel);
					
					for(int i=1;i<split.length;i++){
						tmpattri=new Attribute(split[i]);
						
						if(tmpmap.containsKey(tmpattri)){
							//System.out.println(" Contains "+tmpattri);
							tmpmap.put(tmpattri,tmpmap.get(tmpattri)+1);
						}
						else{
							//System.out.println(" not Contains "+tmpattri);
							tmpmap.put(tmpattri,1);
						}
					}
	
				}
				
				//count the total number of the records
				this.totalsupport=0;
				for(int i=0;i<this.classcount;i++){
					this.totalsupport+=this.classsupport[i];
				}
				
				//count the likelihood
				//posteriori = likelihood x prior/evidence
				for(int i=0;i<this.classcount;i++){
					this.Pros[i]=(double)this.classsupport[i]/this.totalsupport;
				}
				
				//System.out.println(" Prepocess: total support is "+this.totalsupport);
				
				trainbr.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
	
		String train_file=Constants.adult_train;
		String test_file=Constants.adult_test;
		
		if(args.length!=0){
			if(args.length==1){
				System.out.println("Main Error: lack enough parameters ");
				return;
			}
			else{
				train_file=args[0];
				test_file=args[1];
			}
		}
		
		//Step 1: Data I/O and Data Format
		NaiveBayes bayes=new NaiveBayes(train_file,test_file);
		bayes.preprocess();
		
		//Step 2: Implement Basic Classification Method
		bayes.classifyDataFile(0); //classify the train data set
		bayes.classifyDataFile(1); //classify the test data set
		bayes.displayAll();
		
		
	}
}


