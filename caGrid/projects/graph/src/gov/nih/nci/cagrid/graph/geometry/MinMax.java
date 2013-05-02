/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package gov.nih.nci.cagrid.graph.geometry;


public class MinMax
{
     public static Index rval = new Index();

     public static Index min(float[] values)
     {
          float min = (float) values[0];
          int   index = 0;

          for(int k = 0; k < values.length; k++)
          {
               if(values[k] < min)
               {
                    min = values[k];
                    index = k;
               }
          }

          rval.index = index;
          rval.value = min;

          return rval;
     }

     public static Index max(float[] values)
     {
          float min = (float) values[0];
          int   index = 0;

          for(int k = 0; k < values.length; k++)
          {
               if(values[k] > min)
               {
                    min = values[k];
                    index = k;
               }
          }

          rval.index = index;
          rval.value = min;

          return rval;
     }

     public static void normalize(float[] values, float low, float high)
     {
          float max = max(values).value;
          float min = min(values).value;

          float dis = high - low;

          for(int k = 0; k < values.length; k++)
          {
               values[k] = low + (values[k]-min)/((max-min)/(high - low));
          }
     }
}
