# expectation-maximization
**An implementation of expectation maximization to estimate missing values in a dataset**

Below is a sample input file where:
  - '-' represents a missing value
  - Gender = 0 represents male, 1 represents female
  - Weight = 0 represents weight > 150 lbs, 1 represents < 150 lbs
  - Height = 0 represents height > 5'5", 1 represents < 5'5"

Gender Weight Height

0 0 0

0	0	0

0	1	0

1	0	1

0	0	0

0	0	1

\-	0	0

1	1	1

0	0	1

0	0	1

0	0	0

0	0	0

\-	0	1

0	1	0

1	1	1

0	0	1

0	0	0

1	0	1

1	0	1
