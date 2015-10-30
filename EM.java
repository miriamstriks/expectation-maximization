import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;


public class EM {
   
   //current estimated parameters
   private Parameters parameters;
   
   //counts of each configuration (000 through 111)
   //that is provided as complete data
   private int[] hardCounts;
    
   //total number of missing data instances in the data set per configuration
   private int[] numOfConfig;
   
   //expected counts for missing data
   //we use "double" to accommodate fractions of a count
   private double[] expectedCounts;
   
   //calculate gender probabilities for each possible configuration of weight & height
   //we only store the probabilities that gender = male because given those probabilities,
   //we can easily calculate the probability that gender = female.
   private double[] theta;
   
   //total number of instances in the data set
   private int totalObservations;
   
   //the likelihood that the current model generated the data
   private double likelihood;
   
   //the threshold by which we measure convergence
   public final double DELTA = .001;

   
   /*
    * constructor
    */
   public EM()
   {
       hardCounts = new int[8];
       expectedCounts = new double[8];
       theta  = new double[4];
       numOfConfig = new int[8];
   }
   
   public void setParameters(Parameters params){
      parameters = params;

   }

   
   public void readInput(File file){
           
      String line;
      int intForm; //integer form of input (001 --> 1)
      
      try {        
         Scanner inFile = new Scanner(file);
         
         //skip header
         inFile.nextLine();
         
         //read rest of input
         while(inFile.hasNextLine()){
            
            //increment count of observations
            totalObservations++;
            
            //convert input to binary string by removing whitespace
            line = inFile.nextLine().replaceAll("\\s", "");
             
            //if gender is missing, add the probability to of each possibility
            //to the appropriate sum of expected counts
            if(line.charAt(0) == '-'){
               //look up probability male given observed weight and height
               intForm = Integer.parseInt(line.substring(1), 2);
               
               //increment the count of each completion of the missing data
               numOfConfig[intForm]++;  //count of male given weight and height
               numOfConfig[4 + intForm]++; //count of female
            }
            else{
               intForm = Integer.parseInt(line, 2);
               hardCounts[intForm]++;
               
            }       
         }
         
         inFile.close();
         
      } catch (FileNotFoundException e) {
         System.out.println("File not found");
         System.exit(1);
      } 
         
   }
   
   
   /*
    * performs expectation maximization until convergence
    */
   public Parameters execute()
   {
      int numIterations = 0;
      double newLikelihood;
      
       while (true)
       {
           numIterations++;
             
           expectation();
   
           parameters = maximization();
   
           newLikelihood = computeLikelihood(parameters);        
           
           System.out.printf("Iteration " + numIterations + "\nLikelihood: " + "%.3f\n\n", newLikelihood);
           
           //test for convergence
           if (Math.abs(likelihood - newLikelihood) < DELTA) {
               break;
           }

           likelihood = newLikelihood;
       }

       return parameters;

   }
   
   
   
   /*
    *  Given the observations and current estimated parameters, 
    *  compute the expected counts of each observation
    */
   private void expectation()
   {   
      //probability male
      double probM = 0.0; 
      
      //compute probability for each possible case given the new estimated parameters
      populateTheta();
      
      //reset all expected counts to 0
      //Arrays.fill(expectedCounts, 0.0);
    
      for (int i = 0; i < 4; i++){
         
            //look up probability (expected count) for this case. 
            //0 corresponds to case 000, 1 to case 001, etc.
            probM = theta[i];
            expectedCounts[i] = probM * numOfConfig[i];
            expectedCounts[4 + i] = (1 - probM) * numOfConfig[4 + i];
            
       }
   }
   
   
   /*
    * Calculates new estimated parameters based on new estimated observations
    */
   private Parameters maximization()
   {
      //counts
      double sum_g0 = 0.0, sum_g0w0 = 0.0, sum_g1w0 = 0.0,
            sum_g0h0 = 0.0, sum_g1h0 = 0.0;
      
      //calculate counts of gender = 0 (000 thru 011)
      for(int i = 0; i < 4; i++){
         sum_g0 += expectedCounts[i] + hardCounts[i];
      }
      
      //calculate counts of gender = 0 and weight = 0 (000 and 001)
      sum_g0w0 = expectedCounts[0] + expectedCounts[1] 
            + hardCounts[0] + hardCounts[1];
      
      //calculate counts of gender = 1 and weight = 0 (100 and 101)
      sum_g1w0 = expectedCounts[4] + expectedCounts[5] 
            + hardCounts[4] + hardCounts[5];

      //calculate counts of gender = 0 and height = 0 (000 and 010)
      sum_g0h0 = expectedCounts[0] + expectedCounts[2] 
            + hardCounts[0] + hardCounts[2];

      //calculate counts of gender = 1 and height = 0 (100 and 110)
      sum_g1h0 = expectedCounts[4] + expectedCounts[6] 
            + hardCounts[4] + hardCounts[6];
      
      
       return new Parameters(sum_g0 / (double)totalObservations, sum_g0w0 / sum_g0,
             sum_g1w0 / (((double)totalObservations) - sum_g0), sum_g0h0 / sum_g0, 
             sum_g1h0 / (((double)totalObservations) - sum_g0));

   }
   
 
   private void populateTheta(){
      
      for(int i=0; i<4; i++){
         
         theta[i] = parameters.getProbabilityMale(i);     
      }  
   }
   
   /*
    * Returns the likelihood that the given data was generated by the given parameters
    * We take the log of the likelihood to avoid underflow 
    */      
   private double computeLikelihood(Parameters params)
   {  
      double sum = 0.0;
      
      for(int i = 0; i < 4; i++){
         sum += hardCounts[i] * Math.log(params.getLikelihoodComplete(i)) +
               hardCounts[i + 4] * Math.log(params.getLikelihoodComplete(i + 4)) +
               numOfConfig[i] * Math.log(params.getLikelihoodIncomplete(i));
               
      }
       
      return sum;
      
   }
   
   
   public static void main(String args[]) throws Exception{
      
      File file; 
   
      //check for filename provided as argument
      if(args.length > 0){
         file = new File(args[0]);
      }
      else{
         throw new Exception("No input file specified.");
      }
      
      Parameters initialParameters = new Parameters(0.7, 0.8, 0.4, 0.7, 0.3);
      
      System.out.printf("Initial parameters:\n%s", initialParameters);
            
      EM em = new EM();
      
      em.setParameters(initialParameters);
      
      em.populateTheta();
      
      em.readInput(file);
      
      System.out.printf("Likelihood: " + "%.3f\n\n", em.computeLikelihood(initialParameters));

      Parameters finalParameters = em.execute();

      System.out.printf("Final result:\n%s\n", finalParameters);
      
   }
   

}
