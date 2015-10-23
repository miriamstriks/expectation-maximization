import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;


public class EM {
   
   //current estimated parameters
   private Parameters parameters;
   
   //counts of each possible configuration (000 through 111)
   //this is provided as complete input and never changed
   private int[] hardCounts;
   
   //expected counts for missing data
   //we use "double" to accommodate fractions of a count
   private double[] expectedCounts;
   
   //calculate gender probabilities for each possible configuration of weight & height
   //we only store the probabilities that gender = male because given those probabilities,
   //we can easily calculate the probability that gender = female.
   private double[] theta;
   
   private int totalObservations;

   
   /*
    * constructor
    */
   public EM()
   {
       hardCounts = new int[8];
       expectedCounts = new double[8];
       theta  = new double[4];
   }
   
   public void setParameters(Parameters params){
      parameters = params;
   }
   
   
   
   public void readInput(File file){
           
      String line;
      int intForm; //integer form of input (001 --> 1)
      double probM;
      
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
               probM = theta[intForm]; 
               //add probability to expected count
               expectedCounts[intForm]  += probM;  //expected count male given weight and height
               expectedCounts[4 + intForm] += (1- probM); //expected count female
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
      
       while (true)
       {
           numIterations++;
             
           expectation();
   
           Parameters estimatedParameters = maximization();
   
           System.out.printf("Iteration " + numIterations + "\n%s\n", estimatedParameters);
   
           if (parameters.converged(estimatedParameters)) {
               break;
           }
   
           parameters = estimatedParameters;
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
      populateTheta(parameters);
      
      //reset all expected counts to 0
      Arrays.fill(expectedCounts, 0.0);
    
      for (int i = 0; i < 4; i++){
         
            //look up probability (expected count) for this case
            probM = theta[i];
            expectedCounts[i] += probM;
            expectedCounts[4 + i] += (1 - probM);
            
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
             sum_g1w0 / ((double)totalObservations - sum_g0), sum_g0h0 / sum_g0, sum_g1h0 / ((double)totalObservations - sum_g0));

       //System.out.printf("parameters: %s\n", _parameters);

   }
   
 
   private void populateTheta(Parameters params){
      
      theta[0] = params.getProbabilityMale("0", "0");
      theta[1] = params.getProbabilityMale("0", "1");
      theta[2] = params.getProbabilityMale("1", "0");
      theta[3] = params.getProbabilityMale("1", "1");
      
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
      
      System.out.printf("Initial parameters:\n%s\n", initialParameters);
            
      EM em = new EM();
      
      em.setParameters(initialParameters);
      
      em.populateTheta(initialParameters);
      
      em.readInput(file);

      Parameters finalParameters = em.execute();

      System.out.printf("Final result:\n%s\n", finalParameters);
      
   }
   

}
