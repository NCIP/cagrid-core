package gov.nih.nci.cagrid.syncgts.test;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.syncgts.bean.DateFilter;
import gov.nih.nci.cagrid.syncgts.core.HistoryManager;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class HistoryManagerTestCase extends TestCase {
    
    private File tempHistoryDir = null;
    private HistoryManager historyManager = null;
    
    public HistoryManagerTestCase(String name) {
        super(name);
    }
    
    
    public void setUp() {
        try {
            tempHistoryDir = File.createTempFile("HistoryManagerTest", "temp");
            tempHistoryDir.delete();
            tempHistoryDir.mkdirs();
        } catch (IOException e) {
            e.printStackTrace();
            fail("Error creating temp dir: " + e.getMessage());
        }
        historyManager = new HistoryManager() {
            protected File getHistoryDirectory() {
                return tempHistoryDir;
            }
        };
    }
    
    
    public void tearDown() {
        Utils.deleteDir(tempHistoryDir);
    }
    
    
    public void testPruneNothing() {
        // simple 1 month filter
        DateFilter oneMonth = new DateFilter(0, 1, 0);
        try {
            historyManager.prune(oneMonth);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error pruning no files at all: " + ex.getMessage());
        }
        assertTrue("Temp history directory is missing!", tempHistoryDir.exists());
    }
    
    
    public void testPruneOneMonthRemoveNothing() {
        int historyDocs = 20;
        // create a month's worth of fake reports
        createFakeReports(historyDocs, getYear(), getMonth(), getDay());
        
        // simple 1 month filter
        DateFilter oneMonth = new DateFilter(0, 1, 0);
        try {
            historyManager.prune(oneMonth);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error pruning files: " + ex.getMessage());
        }
        assertTrue("Temp history directory is missing!", tempHistoryDir.exists());
        
        int remainingHistoryDocs = countXmlDocsInHistory();
        assertEquals("Prune did something it shouldn't have", historyDocs, remainingHistoryDocs);
    }
    
    
    public void testPruneOneMonthRemoveOneMonthKeepOneMonth() {
        int historyDocs = 20;
        // create a month's worth of fake reports
        createFakeReports(historyDocs, getYear(), getMonth(), getDay());
        // create a month's worth for last month
        createFakeReports(historyDocs, getYear(), getMonth() - 1, getDay() - 1);
        
        // simple 1 month filter
        DateFilter oneMonth = new DateFilter(0, 1, 0);
        try {
            historyManager.prune(oneMonth);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error pruning files: " + ex.getMessage());
        }
        assertTrue("Temp history directory is missing!", tempHistoryDir.exists());
        
        int remainingHistoryDocs = countXmlDocsInHistory();
        assertEquals("Prune did something it shouldn't have", historyDocs, remainingHistoryDocs);
    }
    
    
    private int getYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }
    
    
    private int getMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }
    
    
    private int getDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }
    
    
    private void createFakeReports(int number, int year, int month, int day) {
        // ensure we've got reasonable dates
        if (day <= 0) {
            month--;
            day = 25;
        }
        if (month <= 0) {
            month = 12;
            year--;
        }
        
        File yearDir = new File(tempHistoryDir, String.valueOf(year));
        File monthDir = null;
        if (month < 10) {
            monthDir = new File(yearDir, "0" + String.valueOf(month));
        } else {
            new File(yearDir, String.valueOf(month));
        }
        File dayDir = null;
        if (day < 10) {
            dayDir = new File(monthDir, "0" + String.valueOf(day));
        } else {
            dayDir = new File(monthDir, String.valueOf(day));
        }
        
        dayDir.mkdirs();
        String text = "this is a test file " + year + month + day;
        for (int i = 0; i < number; i++) {
            StringBuffer data = new StringBuffer();
            data.append(text).append(i);
            File fake = new File(dayDir, "fake_report_ " + i + ".xml");
            try {
                Utils.stringBufferToFile(data, fake);
            } catch (IOException e) {
                e.printStackTrace();
                fail("Error writing fake report file " + fake.getAbsolutePath() + ": " + e.getMessage());
            }
        }
    }
    
    
    private int countXmlDocsInHistory() {
        List<File> history = Utils.recursiveListFiles(tempHistoryDir, new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".xml");
            }
        });
        int count = 0;
        for (File f : history) {
            if (f.isFile() && f.getName().endsWith(".xml")) {
                count++;
            }
        }
        return count;
    }
    

    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(CycleTestCase.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
