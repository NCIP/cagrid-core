package org.cagrid.data.test.system;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import gov.nih.nci.cagrid.data.mapping.ClassToQname;
import gov.nih.nci.cagrid.data.mapping.Mappings;

import javax.xml.namespace.QName;

import org.projectmobius.bookstore.Book;
import org.projectmobius.bookstore.BookStore;

/**
 * TestQueryResultsGenerator
 * Simple util to generate some predictable test query results
 * 
 * @author David
 */
public class TestQueryResultsGenerator {

    public static final String BOOKSTORE_NAMESPACE = "gme://projectmobius.org/1/BookStore";

    public static int BOOK_COUNT = 4;
    
    public static String[] BOOK_TITLES = {
        "Eclipse", "XML Schema", "Hibernate In Action", "Hibernate Quickly"
    };
    public static String[] BOOK_AUTHORS = {
        "Jim D'Anjou, et al", "Eric van der Vlist", 
        "Christian Bauer, Gavin King", "Patrick Peak, Nick, Heudecker"
    };
    
    
    public static Mappings getClassToQnameMappings() {
        Mappings mapping = new Mappings();
        ClassToQname bookMap = new ClassToQname();
        bookMap.setClassName(Book.class.getName());
        bookMap.setQname(new QName(BOOKSTORE_NAMESPACE, "Book").toString());
        ClassToQname storeMap = new ClassToQname();
        storeMap.setClassName(BookStore.class.getName());
        storeMap.setQname(new QName(BOOKSTORE_NAMESPACE, "BookStore").toString());
        mapping.setMapping(new ClassToQname[] {bookMap, storeMap});
        return mapping;
    }
    
    
    public static List getResultBooks() {
        List<Book> books = new LinkedList<Book>();
        for (int i = 0; i < BOOK_COUNT; i++) {          
            Book book = new Book();
            book.setTitle(BOOK_TITLES[i]);
            book.setAuthor(BOOK_AUTHORS[i]);
            books.add(book);
        }
        return books;
    }
    
    
    public static List getResultBookStore() {
        return Collections.singletonList(new BookStore());
    }
}
