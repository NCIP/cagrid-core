package gov.nih.nci.cagrid.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public abstract class BaseByteQueueTest extends TestCase {
    
    private ByteQueue byteQueue = null;
    
    public BaseByteQueueTest() {
        super();
    }
    
    
    protected abstract ByteBuffer getByteBuffer();
    
    
    public void setUp() {
        this.byteQueue = new ByteQueue(getByteBuffer());
    }
    
    
    public void tearDown() {
        this.byteQueue.cleanUp();
    }
    
    
    public void testSingleWord() {
        String word = "Hello";
        OutputStream output = byteQueue.getByteOutputStream();
        String processedWord = null;
        try {
            OutputStreamWriter writer = new OutputStreamWriter(output);
            writer.write(word);
            writer.close();
            output.close();
            BufferedReader reader = new BufferedReader(new InputStreamReader(byteQueue.getByteInputStream()));
            processedWord = reader.readLine();
        } catch (IOException ex) {
            ex.printStackTrace();
            fail("Error pushing bytes around: " + ex.getMessage());
        }
        assertEquals("Word was different after pushed through queue!", word, processedWord);
    }
    
    
    public void testMultipleWords() {
        String[] words = new String[5];
        for (int i = 0; i < words.length; i++) {
            words[i] = "Word " + i;
        }
        OutputStream output = byteQueue.getByteOutputStream();
        String[] processedWords = new String[words.length];
        try {
            OutputStreamWriter writer = new OutputStreamWriter(output);
            for (String w : words) {
                writer.write(w);
                writer.write("\n");
            }
            writer.close();
            BufferedReader reader = new BufferedReader(new InputStreamReader(byteQueue.getByteInputStream()));
            String line = null;
            int index = 0;
            while ((line = reader.readLine()) != null) {
                processedWords[index++] = line;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error pushing bytes around: " + ex.getMessage());
        }
        for (int i = 0; i < words.length; i++) {
            String original = words[i];
            String processed = processedWords[i];
            assertEquals("Word was different after pushed through queue!", original, processed);
        }
    }
    
    
    public void testHugeWord() {
        String word = generateRandomWord(8192);
        String processedWord = null;
        OutputStream output = byteQueue.getByteOutputStream();
        try {
            OutputStreamWriter writer = new OutputStreamWriter(output);
            writer.write(word);
            writer.close();
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(byteQueue.getByteInputStream()));
            processedWord = reader.readLine();
        } catch (IOException ex) {
            ex.printStackTrace();
            fail("Error pushing bytes around: " + ex.getMessage());
        }
        assertEquals("Word was different after pushed through queue!", word, processedWord);
    }
    
    
    public void testHugeWordRetrievedInPieces() {
        String word = generateRandomWord(8192);
        String processedWord = null;
        OutputStream output = byteQueue.getByteOutputStream();
        try {
            OutputStreamWriter writer = new OutputStreamWriter(output);
            writer.write(word);
            writer.close();
            InputStreamReader reader = new InputStreamReader(byteQueue.getByteInputStream());
            StringBuffer processed = new StringBuffer();
            Random rand = new Random(System.currentTimeMillis());
            char[] charBuff = new char[rand.nextInt(32) + 16];
            int charsRead = -1;
            while ((charsRead = reader.read(charBuff)) != -1) {
                processed.append(new String(charBuff, 0, charsRead));
                charBuff = new char[rand.nextInt(32) + 16];
            }
            processedWord = processed.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
            fail("Error pushing bytes around: " + ex.getMessage());
        }
        assertEquals("Word was different after pushed through queue!", word, processedWord);
    }
    
    
    public void testConcurrentReadWrite() {
        // generate a bunch of garbage to test
        final List<String> originalWords = new ArrayList<String>();
        for (int i = 0; i < 4; i++) {
            // originalWords.add(generateRandomWord(8192));
            originalWords.add("Word " + i);
        }
        // writer and re-reader
        final OutputStream output = byteQueue.getByteOutputStream();
        final InputStream input = byteQueue.getByteInputStream();
        // write words in to the buffer every 1000ms
        Runnable writeRunner = new Runnable() {
            public void run() {
                OutputStreamWriter writer = new OutputStreamWriter(output);
                Iterator<String> wordIter = originalWords.iterator();
                while (wordIter.hasNext()) {
                    try {
                        writer.write(wordIter.next());
                        writer.write('\n');
                        Thread.sleep(1000);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                try {
                    writer.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        final List<String> processedWords = new ArrayList<String>();
        // read bytes out as fast as possible
        Runnable readRunner = new Runnable() {
            public void run() {
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String line = null;
                try {
                    while ((line = reader.readLine()) != null) {
                        processedWords.add(line);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        // start up the processes
        Thread writeThread = new Thread(writeRunner);
        Thread readThread = new Thread(readRunner);
        writeThread.start();
        readThread.start();
        try {
            writeThread.join();
            readThread.join();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error waiting for worker threads: " + ex.getMessage());
        }
        assertEquals("Count of words after processing was not as expected", 
            originalWords.size(), processedWords.size());
        for (int i = 0; i < originalWords.size(); i++) {
            String original = originalWords.get(i);
            String processed = processedWords.get(i);
            assertEquals("Processed word was different from original", original, processed);
        }
    }
    
    
    private String generateRandomWord(int length) {
        char[] randChars = new char[length];
        Random rand = new Random(System.currentTimeMillis());
        for (int i = 0; i < randChars.length; i++) {
            char next = 'a';
            next += rand.nextInt(26);
            randChars[i] = next;
        }
        String word = new String(randChars);
        return word;
    }
    

    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(BaseByteQueueTest.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
