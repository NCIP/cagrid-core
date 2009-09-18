package gov.nci.nih.cagrid.tests.core.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Hashtable;


public class SimpleXmlReplacer {
    private Hashtable<String, String> replacementTable = new Hashtable<String, String>();


    public SimpleXmlReplacer() {
        super();
    }


    public void addReplacement(String elementName, String value) {
        replacementTable.put(elementName, value);
    }


    public void performReplacement(File inFile) throws IOException {
        performReplacement(inFile, inFile);
    }


    public void performReplacement(File inFile, File outFile) throws IOException {
        HashSet<String> replacedNames = new HashSet<String>(replacementTable.size());

        File tempFile = File.createTempFile("SimpleXmlReplacer", ".xml");
        tempFile.deleteOnExit();
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(tempFile)));
            BufferedReader br = new BufferedReader(new FileReader(inFile));
            String line = null;
            while ((line = br.readLine()) != null) {
                int index = line.indexOf("<");
                if (index != -1) {
                    int index2 = line.indexOf(' ', index);
                    if (index2 == -1)
                        index2 = line.indexOf("/>", index);
                    if (index2 == -1)
                        index2 = line.indexOf('>', index);
                    if (index2 != -1) {
                        String name = line.substring(index + 1, index2);
                        String value = replacementTable.get(name);
                        if (value != null) {
                            index = line.indexOf("/>", index);
                            if (index != -1) {
                                line = line.substring(0, index) + ">" + value + "</" + name + ">"
                                    + line.substring(index + 2);
                                replacedNames.add(name);
                            } else {
                                index = line.indexOf('>', index);
                                if (index != -1) {
                                    index2 = line.indexOf('<', index);
                                    if (index2 != -1) {
                                        line = line.substring(0, index + 1) + value + line.substring(index2);
                                        replacedNames.add(name);
                                    }
                                }
                            }
                        }
                    }
                }
                out.println(line);
            }
            br.close();
            out.flush();
            out.close();

            if (replacedNames.size() != replacementTable.size()) {
                StringBuffer msg = new StringBuffer();
                for (String name : replacementTable.keySet()) {
                    if (!replacedNames.contains(name)) {
                        if (msg.length() > 0)
                            msg.append(", ");
                        msg.append(name);
                    }
                }
                throw new IOException("did not replace " + msg);
            }

            FileUtils.copy(tempFile, outFile);
        } finally {
            tempFile.delete();
        }
    }
}
