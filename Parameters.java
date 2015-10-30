/*
 *  Represents the probabilities needed to estimate gender given height and weight.
 *  Specifically, the parameters include the following probabilities:
 *      P(gender= 0)
 *      P(weight= 0|gender= 0)
 *      P(weight= 0|gender= 1)
 *      P(height= 0|gender= 0)
 *      P(height= 0|gender= 1)
 */
public class Parameters {
   
       double pG0; // Probability gender is male
       double pG0W0; // Probability weight > 130 given gender is male
       double pG1W0; // Probability weight > 130 given gender is female
       double pG0H0; // Probability height > 55 given gender is male
       double pG1H0; // Probability height > 55 given gender is female

       public Parameters(double m, double wm, double wf, 
             double hm, double hf)
       {
          pG0 = m;
          pG0W0 = wm;
          pG1W0 = wf;
          pG0H0 = hm;
          pG1H0 = hf;
          
       }
       

       /*
        * calculates the probability that gender = male,
        * given values for weight and height
        */
       public double getProbabilityMale(int wh){
          
          if(wh > 3 || wh < 0)
             throw new IllegalArgumentException("Argument to getProbabilityMale() must be between 0 and 3");
          
          // case 00
          if(wh == 0){
             return (pG0W0 * pG0H0 * pG0) / 
                   ((pG0W0 * pG0H0 * pG0) + (pG1W0 * pG1H0 * (1-pG0)));
          }
          // case 01
          else if(wh == 1){
             return (pG0W0 * (1-pG0H0) * pG0) / 
                   ((pG0W0 * (1-pG0H0) * pG0) + (pG1W0 * (1-pG1H0) * (1-pG0)));
          }
          //case 10
          else if(wh == 2){
             return ((1-pG0W0) * pG0H0 * pG0) / 
                   (((1-pG0W0) * pG0H0 * pG0) + ((1-pG1W0) * pG1H0 * (1-pG0)));
          }
          //case 11
          else return ((1-pG0W0) * (1-pG0H0) * pG0) / 
                (((1-pG0W0) * (1-pG0H0) * pG0) + ((1-pG1W0) * (1-pG1H0) * (1-pG0)));
          
       }
       
      /*
       * calculates the likelihood of the given complete configuration
       */
      public double getLikelihoodComplete(int config){
         
         if(config > 7 || config < 0)
            throw new IllegalArgumentException("Argument to getLikelihoodComplete() must be between 0 and 7");
         
         switch(config){
         //case 000
         case 0: return pG0W0 * pG0H0 * pG0;
         //case 001
         case 1: return pG0W0 * (1-pG0H0) * pG0;
         //case 010
         case 2: return (1-pG0W0) * pG0H0 * pG0;
         //case 011
         case 3: return (1-pG0W0) * (1-pG0H0) * pG0;
         //case 100
         case 4: return pG1W0 * pG1H0 * (1-pG0);
         //case 101
         case 5: return pG1W0 * (1-pG1H0) * (1-pG0);
         //case 110
         case 6: return (1-pG1W0) * pG1H0 * (1-pG0);
         //case 111
         default: return (1-pG1W0) * (1-pG1H0) * (1-pG0);
               
         }       
      }
      
      /*
       * calculates the likelihood of the given incomplete configuration
       */
      public double getLikelihoodIncomplete(int config){
         
         if(config > 3 || config < 0)
            throw new IllegalArgumentException("Argument to getLikelihoodComplete() must be between 0 and 3");
         
         switch(config){
         //case 00
         case 0: return (pG0W0 * pG0H0 * pG0) + (pG1W0 * pG1H0 * (1-pG0));
         //case 01
         case 1: return (pG0W0 * (1-pG0H0) * pG0) + (pG1W0 * (1-pG1H0) * (1-pG0));
         //case 10
         case 2: return ((1-pG0W0) * pG0H0 * pG0) + ((1-pG1W0) * pG1H0 * (1-pG0));
         //case 11
         default: return ((1-pG0W0) * (1-pG0H0) * pG0) + ((1-pG1W0) * (1-pG1H0) * (1-pG0));
               
         }       
      }
       

       public double getpG0()
       {
           return pG0;
       }

       public double getpG0W0()
       {
           return pG0W0;
       }
       
       public double getpG1W0()
       {
           return pG1W0;
       }
       
       public double getpG0H0()
       {
           return pG0H0;
       }
       
       public double getpG1H0()
       {
           return pG1H0;
       }

       public String toString()
       {
           return String.format("X = 0: %.3f\n"
                 + "X = 0, W = 0: %.3f\n"
                 + "X = 1, W = 0: %.3f\n"
                 + "X = 0, H = 0: %.3f\n"
                 + "X = 1, H = 0: %.3f\n", pG0, pG0W0, pG1W0, pG0H0, pG1H0);
       }

}
