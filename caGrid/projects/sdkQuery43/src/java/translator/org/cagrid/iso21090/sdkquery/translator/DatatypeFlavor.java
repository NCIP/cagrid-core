package org.cagrid.iso21090.sdkquery.translator;

import gov.nih.nci.iso21090.AddressPartType;
import gov.nih.nci.iso21090.Bl;
import gov.nih.nci.iso21090.Cd;
import gov.nih.nci.iso21090.Compression;
import gov.nih.nci.iso21090.DSet;
import gov.nih.nci.iso21090.Ed;
import gov.nih.nci.iso21090.En;
import gov.nih.nci.iso21090.EntityNamePartQualifier;
import gov.nih.nci.iso21090.EntityNamePartType;
import gov.nih.nci.iso21090.EntityNameUse;
import gov.nih.nci.iso21090.IdentifierReliability;
import gov.nih.nci.iso21090.IdentifierScope;
import gov.nih.nci.iso21090.Int;
import gov.nih.nci.iso21090.IntegrityCheckAlgorithm;
import gov.nih.nci.iso21090.Ivl;
import gov.nih.nci.iso21090.NullFlavor;
import gov.nih.nci.iso21090.PostalAddressUse;
import gov.nih.nci.iso21090.Pq;
import gov.nih.nci.iso21090.Pqv;
import gov.nih.nci.iso21090.Sc;
import gov.nih.nci.iso21090.St;
import gov.nih.nci.iso21090.Tel;
import gov.nih.nci.iso21090.TelPerson;
import gov.nih.nci.iso21090.TelecommunicationAddressUse;
import gov.nih.nci.iso21090.Ts;
import gov.nih.nci.iso21090.UncertaintyType;

import java.util.HashMap;
import java.util.Map;

import org.iso._21090.Ad;
import org.iso._21090.BL;
import org.iso._21090.BlNonNull;
import org.iso._21090.CD;
import org.iso._21090.DSetAd;
import org.iso._21090.DSetCd;
import org.iso._21090.DSetII;
import org.iso._21090.DSetTel;
import org.iso._21090.ED;
import org.iso._21090.EN;
import org.iso._21090.ENXP;
import org.iso._21090.EdText;
import org.iso._21090.EnOn;
import org.iso._21090.EnPn;
import org.iso._21090.INT;
import org.iso._21090.IVLINT;
import org.iso._21090.IVLPQ;
import org.iso._21090.IVLREAL;
import org.iso._21090.IVLTS;
import org.iso._21090.Ii;
import org.iso._21090.PQ;
import org.iso._21090.Real;
import org.iso._21090.SC;
import org.iso._21090.ST;
import org.iso._21090.StNt;
import org.iso._21090.TEL;
import org.iso._21090.TELPerson;
import org.iso._21090.TS;
import org.iso._21090.TelEmail;
import org.iso._21090.TelPhone;
import org.iso._21090.TelUrl;

/**
 * DatatypeFlavor
 * Describes the queried data type to the CQL to HQL translator
 * so it can appropriately generate queries
 * 
 * @author David
 */
public enum DatatypeFlavor {

    STANDARD,                                                               // your.domain.Person
    ENUMERATION,                                                            // gov.nih.nci.iso21090.NullFlavor
    COMPLEX_WITH_SIMPLE_CONTENT,                                            // org.iso._21090.II
    COMPLEX_WITH_MIXED_CONTENT,                                             // org.iso._21090.CD
    COMPLEX_WITH_COLLECTION_OF_COMPLEX,                                     // org.iso._21090.EN
    COLLECTION_OF_COMPLEX_WITH_SIMPLE_CONTENT,                              // org.iso._21090.DSET<CD>
    COLLECTION_OF_COMPLEX_WITH_COLLECTION_OF_COMPLEX_WITH_SIMPLE_CONTENT    // org.iso._21090.DSET<AD>
    ;
    
    private static final Map<Class<?>, DatatypeFlavor> CLASS_FLAVORS = new HashMap<Class<?>, DatatypeFlavor>();
    static {
        CLASS_FLAVORS.put(NullFlavor.class, ENUMERATION);
        CLASS_FLAVORS.put(org.iso._21090.NullFlavor.class, ENUMERATION);
        CLASS_FLAVORS.put(AddressPartType.class, ENUMERATION);
        CLASS_FLAVORS.put(org.iso._21090.AddressPartType.class, ENUMERATION);
        CLASS_FLAVORS.put(EntityNamePartQualifier.class, ENUMERATION);
        CLASS_FLAVORS.put(org.iso._21090.EntityNamePartQualifier.class, ENUMERATION);
        CLASS_FLAVORS.put(EntityNamePartType.class, ENUMERATION);
        CLASS_FLAVORS.put(org.iso._21090.EntityNamePartType.class, ENUMERATION);
        CLASS_FLAVORS.put(EntityNameUse.class, ENUMERATION);
        CLASS_FLAVORS.put(Compression.class, ENUMERATION);
        CLASS_FLAVORS.put(org.iso._21090.Compression.class, ENUMERATION);
        CLASS_FLAVORS.put(IdentifierReliability.class, ENUMERATION);
        CLASS_FLAVORS.put(org.iso._21090.IdentifierReliability.class, ENUMERATION);
        CLASS_FLAVORS.put(IdentifierScope.class, ENUMERATION);
        CLASS_FLAVORS.put(org.iso._21090.IdentifierScope.class, ENUMERATION);
        CLASS_FLAVORS.put(IntegrityCheckAlgorithm.class, ENUMERATION);
        CLASS_FLAVORS.put(org.iso._21090.IntegrityCheckAlgorithm.class, ENUMERATION);
        CLASS_FLAVORS.put(PostalAddressUse.class, ENUMERATION);
        CLASS_FLAVORS.put(org.iso._21090.PostalAddressUse.class, ENUMERATION);
        CLASS_FLAVORS.put(TelecommunicationAddressUse.class, ENUMERATION);
        CLASS_FLAVORS.put(org.iso._21090.TelecommunicationAddressUse.class, ENUMERATION);
        // CLASS_FLAVORS.put(TimingEvent.class, ENUMERATION);
        CLASS_FLAVORS.put(org.iso._21090.TimingEvent.class, ENUMERATION);
        CLASS_FLAVORS.put(UncertaintyType.class, ENUMERATION);
        CLASS_FLAVORS.put(org.iso._21090.UncertaintyType.class, ENUMERATION);
        // CLASS_FLAVORS.put(UpdateMode.class, ENUMERATION);
        CLASS_FLAVORS.put(org.iso._21090.UpdateMode.class, ENUMERATION);
        
        CLASS_FLAVORS.put(BL.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(Bl.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(BlNonNull.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(gov.nih.nci.iso21090.BlNonNull.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(ST.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(St.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(StNt.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(gov.nih.nci.iso21090.StNt.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(Ii.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(gov.nih.nci.iso21090.Ii.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(TEL.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(Tel.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(TELPerson.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(TelPerson.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(TelUrl.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(gov.nih.nci.iso21090.TelUrl.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(TelPhone.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(gov.nih.nci.iso21090.TelPhone.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(TelEmail.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(gov.nih.nci.iso21090.TelEmail.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(ED.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(Ed.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(EdText.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(gov.nih.nci.iso21090.EdText.class, COMPLEX_WITH_SIMPLE_CONTENT);
        
        CLASS_FLAVORS.put(CD.class, COMPLEX_WITH_MIXED_CONTENT);
        CLASS_FLAVORS.put(Cd.class, COMPLEX_WITH_MIXED_CONTENT);
        CLASS_FLAVORS.put(SC.class, COMPLEX_WITH_MIXED_CONTENT);
        CLASS_FLAVORS.put(Sc.class, COMPLEX_WITH_MIXED_CONTENT);
        CLASS_FLAVORS.put(INT.class, COMPLEX_WITH_MIXED_CONTENT);
        CLASS_FLAVORS.put(Int.class, COMPLEX_WITH_MIXED_CONTENT);
        CLASS_FLAVORS.put(Real.class, COMPLEX_WITH_MIXED_CONTENT);
        CLASS_FLAVORS.put(gov.nih.nci.iso21090.Real.class, COMPLEX_WITH_MIXED_CONTENT);
        CLASS_FLAVORS.put(TS.class, COMPLEX_WITH_MIXED_CONTENT);
        CLASS_FLAVORS.put(Ts.class, COMPLEX_WITH_MIXED_CONTENT);
        // CLASS_FLAVORS.put(PQV.class, COMPLEX_WITH_NESTED_COMPLEX);
        CLASS_FLAVORS.put(Pqv.class, COMPLEX_WITH_MIXED_CONTENT);
        CLASS_FLAVORS.put(PQ.class, COMPLEX_WITH_MIXED_CONTENT);
        CLASS_FLAVORS.put(Pq.class, COMPLEX_WITH_MIXED_CONTENT);
        CLASS_FLAVORS.put(Ivl.class, COMPLEX_WITH_MIXED_CONTENT);
        CLASS_FLAVORS.put(IVLPQ.class, COMPLEX_WITH_MIXED_CONTENT);
        // CLASS_FLAVORS.put(IVLPQV.class, COMPLEX_WITH_NESTED_COMPLEX);
        CLASS_FLAVORS.put(IVLREAL.class, COMPLEX_WITH_MIXED_CONTENT);
        CLASS_FLAVORS.put(IVLINT.class, COMPLEX_WITH_MIXED_CONTENT);
        CLASS_FLAVORS.put(IVLTS.class, COMPLEX_WITH_MIXED_CONTENT);
        
        CLASS_FLAVORS.put(Ad.class, COMPLEX_WITH_COLLECTION_OF_COMPLEX);
        CLASS_FLAVORS.put(gov.nih.nci.iso21090.Ad.class, COMPLEX_WITH_COLLECTION_OF_COMPLEX);
        CLASS_FLAVORS.put(org.iso._21090.ADXP.class, COMPLEX_WITH_COLLECTION_OF_COMPLEX);
        CLASS_FLAVORS.put(gov.nih.nci.iso21090.Adxp.class, COMPLEX_WITH_COLLECTION_OF_COMPLEX);
        CLASS_FLAVORS.put(EN.class, COMPLEX_WITH_COLLECTION_OF_COMPLEX);
        CLASS_FLAVORS.put(En.class, COMPLEX_WITH_COLLECTION_OF_COMPLEX);
        CLASS_FLAVORS.put(EnOn.class, COMPLEX_WITH_COLLECTION_OF_COMPLEX);
        CLASS_FLAVORS.put(gov.nih.nci.iso21090.EnOn.class, COMPLEX_WITH_COLLECTION_OF_COMPLEX);
        CLASS_FLAVORS.put(EnPn.class, COMPLEX_WITH_COLLECTION_OF_COMPLEX);
        CLASS_FLAVORS.put(gov.nih.nci.iso21090.EnPn.class, COMPLEX_WITH_COLLECTION_OF_COMPLEX);
        CLASS_FLAVORS.put(ENXP.class, COMPLEX_WITH_COLLECTION_OF_COMPLEX);
        CLASS_FLAVORS.put(gov.nih.nci.iso21090.Enxp.class, COMPLEX_WITH_COLLECTION_OF_COMPLEX);
        
        CLASS_FLAVORS.put(DSetII.class, COLLECTION_OF_COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(DSet.class, COLLECTION_OF_COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(DSetTel.class, COLLECTION_OF_COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(DSetCd.class, COLLECTION_OF_COMPLEX_WITH_SIMPLE_CONTENT);
        
        CLASS_FLAVORS.put(DSetAd.class, COLLECTION_OF_COMPLEX_WITH_COLLECTION_OF_COMPLEX_WITH_SIMPLE_CONTENT);
    }
    
    public static DatatypeFlavor getFlavorOfClass(Class<?> clazz) {
        DatatypeFlavor flavor = CLASS_FLAVORS.get(clazz);
        if (flavor == null) {
            flavor = STANDARD;
        }
        return flavor;
    }
}
