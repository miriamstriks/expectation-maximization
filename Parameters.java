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
   
       double pM; // Probability gender is male
       double pWeightM; // Probability weight > 130 given gender is male
       double pWeightF; // Probability weight > 130 given gender is female
       double pHeightM; // Probability height > 55 given gender is male
       double pHeightF; // Probability height > 55 given gender is female

       double delta = 0.001;

       public Parameters(double m, double wm, double wf, 
             double hm, double hf)
       {
          pM = m;
          pWeightM = wm;
          pWeightF = wf;
          pHeightM = hm;
          pHeightF = hf;
          
       }
       

       /*
        * calculates the probability that gender = male,
        * given values for weight and height
        */
       public double getProbabilityMale(String w, String h){
          
          // case 00
          if(w.equals("0") && h.equals("0")){
             return (pWeightM * pHeightM * pM) / 
                   ((pWeightM * pHeightM * pM) + (pWeightF * pHeightF * (1-pM)));
          }
          // case 01
          else if(w.equals("0") && h.equals("1")){
             return (pWeightM * (1-pHeightM) * pM) / 
                   ((pWeightM * (1-pHeightM) * pM) + (pWeightF * (1-pHeightF) * (1-pM)));
          }
          //case 10
          else if(w.equals("1") && h.equals("0")){
             return ((1-pWeightM) * pHeightM * pM) / 
                   (((1-pWeightM) * pHeightM * pM) + ((1-pWeightF) * pHeightF * (1-pM)));
          }
          //case 11
          else return ((1-pWeightM) * (1-pHeightM) * pM) / 
                (((1-pWeightM) * (1-pHeightM) * pM) + ((1-pWeightF) * (1-pHeightF) * (1-pM)));
          
       }
       
       /*
        * Returns true if this parameter is close enough to another parameter
        * based on the threshold represented by delta; 
        */      
       public boolean converged(Parameters other)
       {
           return (Math.abs(pM - other.getPM()) < delta &&
               Math.abs(pWeightM - other.getPWeightM()) < delta &&
               Math.abs(pWeightF - other.getPWeightF()) < delta &&
               Math.abs(pHeightM - other.getPHeightM()) < delta &&
               Math.abs(pHeightF - other.getPHeightF()) < delta);

       }

       public double getPM()
       {
           return pM;
       }

       public double getPWeightM()
       {
           return pWeightM;
       }
       
       public double getPWeightF()
       {
           return pWeightF;
       }
       
       public double getPHeightM()
       {
           return pHeightM;
       }
       
       public double getPHeightF()
       {
           return pHeightF;
       }

       public String toString()
       {
           return String.format("Male: %.3f\n"
                 + "weight > 130 given male: %.3f\n"
                 + "weight > 130 given female: %.3f\n"
                 + "height > 55 given male: %.3f\n"
                 + "height > 55 given female: %.3f\n", pM, pWeightM, pWeightF, pHeightM, pHeightF);
       }

}
