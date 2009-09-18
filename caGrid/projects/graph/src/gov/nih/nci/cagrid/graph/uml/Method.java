package gov.nih.nci.cagrid.graph.uml;

import java.util.Vector;

public class Method
{
     protected String returnType;
     protected String name;
     protected Vector parameterNames = new Vector();
     protected Vector parameterTypes = new Vector();

     public Method(String name, String returnType)
     {
          this.name = name;
          this.returnType = returnType;
     }

     public void addParamter(String type, String name)
     {
          this.parameterNames.addElement(name);
          this.parameterTypes.addElement(type);
     }

     public void setReturnType(String type)
     {
          this.returnType = type;
     }



     public String toString()
     {
          return name + "(): " + returnType;
     }


}
