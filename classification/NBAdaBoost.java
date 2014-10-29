package classification;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import other_packages.Tuple;
import other_packages.Constants;


public class NBAdaBoost {
	double ERROR_SHRESHOLD;
	int ROUNDS;
	int count_of_sample;
	double samplerate; 
	ArrayList<Bayes> classifiers;
	ArrayList<Tuple> traintuples;
	ArrayList<Tuple> testtuples;
	
	int train_true_positive=0,train_false_positive=0,train_true_negative=0,train_false_negative=0;
	int test_true_positive=0,test_false_positive=0,test_true_negative=0,test_false_negative=0;
	
	String trainfile;
	String testfile;
	
	NBAdaBoost(String train,String test){
		this.trainfile=train;
		this.testfile=test;
		this.classifiers=new ArrayList<Bayes>();
		this.traintuples=new ArrayList<Tuple>();
		this.testtuples=new ArrayList<Tuple>();
		samplerate=0.5;
		ROUNDS=10;
		ERROR_SHRESHOLD=0.5;
	}
	
	//**********************MAIN**************************************//
	//**********************MAIN**************************************//
	//**********************MAIN**************************************//
	
	public static void main(String[] args){
		//long begin=System.currentTimeMillis();
		String train_file=Constants.adult_train;
		String test_file=Constants.adult_test;
		
		if(args.length!=0){
			if(args.length==1){
				//System.out.println("Main Error: lack enough parameters ");
				return;
			}
			else{
				train_file=args[0];
				test_file=args[1];
			}
		}
		
		
		//Step 3: Implement Ensemble Classification Method
		NBAdaBoost boost=new NBAdaBoost(train_file,test_file);
		boost.adaBoost();
		boost.displayAll();
		
		//long end=System.currentTimeMillis();
		//System.out.println("The running time is "+(end-begin)/1000+" sec");
	}
	
	//**********************MAIN**************************************//
	//**********************MAIN**************************************//
	//**********************MAIN**************************************//
	
	public void initializeWeight(){
		for(Tuple tuple:this.traintuples){
			tuple.weight=(double)1.0/this.traintuples.size();
		}
	}
	
	//Sample
	public ArrayList<Tuple> sampleTuples(){
		int number=(int) (this.traintuples.size()*this.samplerate);
		ArrayList<Tuple> sample=new ArrayList<Tuple>();
		
		for(int count=1;count<=number;count++){
			int i;
			double rnd=Math.random();
			for(i=0;i<this.traintuples.size()-1;i++){
				rnd-=this.traintuples.get(i).weight;
				if(rnd<0)
					break;
			}
			
			sample.add(this.traintuples.get(i));
			//System.out.println(i);
		}
		return sample;
	}
	
	
	//read all the tuples from train and test
	public void readTuple(){
		try {
			BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(new File(this.trainfile))));
			String line;
			while((line=br.readLine())!=null){
				if(line.length()<3)
					continue;
				this.traintuples.add(new Tuple(line));
			}
			br.close();
			
			br=new BufferedReader(new InputStreamReader(new FileInputStream(new File(this.testfile))));
			while((line=br.readLine())!=null){
				if(line.length()<3)
					continue;
				this.testtuples.add(new Tuple(line));
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//use adaboost algorithm to get the classifiers
	public void adaBoost(){
		Bayes bayes;
		
		this.readTuple();
		this.initializeWeight();
		
		for(int i=1;i<=this.ROUNDS;i++){
			bayes=new Bayes();
			
			bayes.trainWithSample(this.sampleTuples());
			
			bayes.testAllTrain(this.traintuples);
			if(bayes.errorrate>ERROR_SHRESHOLD){
				//System.out.println("bayes.errorrate>0.5");
				--i;
				continue;
			}
			
			
			bayes.ModifyPros(traintuples);
			
			this.classifiers.add(bayes);
			
		}
		
		this.testBoost(traintuples);
		this.testBoost(testtuples);
		
		//this.displayPara();
		
	}
	
	public void displayPara(){
		int i=1;
		for(Bayes bayes:this.classifiers){
			//System.out.println("No."+(i++)+" classifier:"+
		//" Error:"+bayes.errorrate+","
					//+" Weight:"+bayes.classifier_weight);
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
	
	public void testBoost(ArrayList<Tuple> tuples){
		
		int true_positive=0,false_positive=0,true_negative=0,false_negative=0;

		double[] result=new double[tuples.size()];
		ArrayList<Double> tmp;
		for(int i=0;i<result.length;i++){
			result[i]=0.0;
		}
		
		for(Bayes bayes:this.classifiers){
			tmp=bayes.classifyTestData(tuples);
			for(int i=0;i<result.length;i++){
				result[i]+=tmp.get(i); //merge the classifier together
			}
		}
		
		int classresult;
		for(int i=0;i<result.length;i++){
			if(result[i]>0)
				classresult=1;
			else
				classresult=-1;
			
			if(classresult==1&&tuples.get(i).classlabel==1)
				++true_positive;
			else if(classresult==1&&tuples.get(i).classlabel==-1)
				++false_positive;
			else if(classresult==-1&&tuples.get(i).classlabel==1)
				++false_negative;
			else if(classresult==-1&&tuples.get(i).classlabel==-1)
				++true_negative;
		}
		
		if(tuples==this.traintuples){
			train_true_positive=true_positive;
			train_false_positive=false_positive;
			train_true_negative=true_negative;
			train_false_negative=false_negative;
		}
		else if(tuples==this.testtuples){
			test_true_positive=true_positive;
			test_false_positive=false_positive;
			test_true_negative=true_negative;
			test_false_negative=false_negative;
		}
		
	}
}

class Bayes{
	final double delta=0.07; 
	double classifier_weight;
	double errorrate;
	HashMap<Integer,HashMap<Integer,Integer>> pAttributeCount;
	HashMap<Integer,HashMap<Integer,Integer>> nAttributeCount;
	int[] classsupport;
	int totalsupport;
	double[] Pros;      //P(x1|H)*P(x2|H)*P(x3|H)...P(xn|H)*P(H)
	
	String trainfile;
	String testfile;
	
	Bayes(){
		this.nAttributeCount=new HashMap<Integer,HashMap<Integer,Integer>>();
		this.pAttributeCount=new HashMap<Integer,HashMap<Integer,Integer>>();
		this.classsupport=new int[2];
		this.Pros=new double[2];
	}
	
	//train the Bayes classifier with sample data
	void trainWithSample(ArrayList<Tuple> sample){
		int negativecount=0;
		int positivecount=0;
		HashMap<Integer,HashMap<Integer,Integer>> attriCount;
		HashMap<Integer,Integer> indexMap;
		
		for(Tuple tuple:sample){
			
			if(tuple.classlabel==1){
				attriCount=this.pAttributeCount;
				++positivecount;
			}
			else{
				attriCount=this.nAttributeCount;
				++negativecount;
			}
			
			for(Integer index:tuple.indexvaluemap.keySet()){
				int value=tuple.indexvaluemap.get(index);
				if(attriCount.containsKey(index)){
					indexMap=attriCount.get(index);
					if(indexMap.containsKey(value)){
						indexMap.put(value, indexMap.get(value)+1);
					}
					else{
						indexMap.put(value, 1);
					}
				}
				else{
					indexMap=new HashMap<Integer,Integer>();
					attriCount.put(index, indexMap);
					indexMap.put(value, 1);
				}
			}
		}
		
		this.classsupport[0]=negativecount;
		this.classsupport[1]=positivecount;
		this.totalsupport=negativecount+positivecount;
		this.Pros[0]=(double)negativecount/this.totalsupport;
		this.Pros[1]=(double)positivecount/this.totalsupport;
		//System.out.println(this.classsupport[0]+","+this.classsupport[1]+","+this.totalsupport);
	}
	
	//classify a single tuple
	//*****return the label and modify the classified label of the tuple
	int classify(Tuple tuple){
		double pro0=this.Pros[0];
		double pro1=this.Pros[1];
		HashMap<Integer,HashMap<Integer,Integer>> attriCount;
		
		for(Integer index:tuple.indexvaluemap.keySet()){
			int value=tuple.indexvaluemap.get(index);
			
			//calculate P(x|-1)
			attriCount=this.nAttributeCount;
			if(attriCount.containsKey(index)){
				if(attriCount.get(index).containsKey(value)){
					pro0*=(double)attriCount.get(index).get(value)/this.classsupport[0];
				}
				else{
					pro0*=(double)1.0/(this.classsupport[0]+attriCount.get(index).keySet().size());
				}
			}
			
			//calculate P(x|+1)
			attriCount=this.pAttributeCount;
			if(attriCount.containsKey(index)){
				if(attriCount.get(index).containsKey(value)){
					pro1*=(double)attriCount.get(index).get(value)/this.classsupport[1];
				}
				else{
					pro1*=(double)1.0/(this.classsupport[1]+attriCount.get(index).keySet().size());
				}
			}
		}
		
		//*************************************************
		if(pro0-pro1>0){
			tuple.classresult=-1;
			return -1;
		}
		else {
			tuple.classresult=+1;
			//System.out.println("positive");
			return 1;
		}
		/*
		else{
			tuple.classresult=0;
			return 0;
		}*/
		//*************************************************
		
	}
	
	//calculate the error rate of all the tuples in the training set
	void testAllTrain(ArrayList<Tuple> train){
		
		double classifier_error_rate=0.0;
		
		for(Tuple tuple:train){
			int classresult=this.classify(tuple);
			if(classresult!=tuple.classlabel){
				classifier_error_rate+=tuple.weight;
			}
		}
		this.errorrate=classifier_error_rate;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////
	double modifyPara(boolean isSame){ //how to calculate the parameter of modification of the distribution
		if(isSame)
			return this.errorrate/(1-this.errorrate);
		else
			return 1.0;
	}
	
	double modifyPara2(boolean isSame){ //how to calculate the parameter of modification of the distribution
		if(isSame)
			return Math.pow(2.718281, (-this.classifier_weight));
		else
			return Math.pow(2.718281, (+this.classifier_weight));
	}
	/////////////////////////////////////////////////////////////////////////////////////////////
	//modify the weight of each of the tuple
	void ModifyPros(ArrayList<Tuple> train){
		
		this.classifier_weight=this.calculateClassWeight();
		
		//double beta=modifyPara();
		double pros_sum=0.0;
		for(Tuple tuple:train){
			/*
			if(tuple.classlabel==tuple.classresult){//only modify the correct ones
				tuple.weight*=beta; 
			}
			*/
			tuple.weight*=this.modifyPara(tuple.classlabel==tuple.classresult);
			pros_sum+=tuple.weight;
		}
		
		for(Tuple tuple:train){
			tuple.weight/=pros_sum;//normalization
		}
		
	}
	
	/////////////////////////////////////////////////////////////
	double calculateClassWeight(){
		double ori=Math.log((1-this.errorrate)/(this.errorrate));
		return ori;
	}
	
	double calculateClassWeight2(){
		return this.errorrate/(1-this.errorrate);
	}
	////////////////////////////////////////////////////////////
	
	//classify the test data and return the result
	ArrayList<Double> classifyTestData(ArrayList<Tuple> test){
		ArrayList<Double> result=new ArrayList<Double>();
		for(Tuple tuple:test){
			this.classify(tuple);
			result.add(tuple.classresult*this.classifier_weight);
		}
		return result;
	}
	
}
