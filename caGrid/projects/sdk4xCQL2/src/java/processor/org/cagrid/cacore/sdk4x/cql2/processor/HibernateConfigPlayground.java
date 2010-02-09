package org.cagrid.cacore.sdk4x.cql2.processor;

import java.io.InputStream;
import java.util.Iterator;

import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.ToOne;
import org.hibernate.mapping.Value;

public class HibernateConfigPlayground {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            Configuration config = new Configuration();
            InputStream stream = HibernateConfigPlayground.class.getResourceAsStream("/hibernate.cfg.xml");
            System.out.println("Stream is " + (stream == null ? "null!" : "ok"));
            config.addInputStream(stream);
            config.configure();
            
            PersistentClass clazz = config.getClassMapping("gov.nih.nci.cacoresdk.domain.manytoone.unidirectional.Chef");
            Iterator<?> propertyIter = clazz.getPropertyIterator();
            while (propertyIter.hasNext()) {
                Property property = (Property) propertyIter.next();
                Value value = property.getValue();
                if (value instanceof ToOne || value instanceof OneToMany) {
                    System.out.println("Association type name: " + property.getType().getName());
                    System.out.println("Association end name: " + property.getName());
                    System.out.println();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
