package org.cagrid.gaards.tasks;

import gov.nih.nci.cagrid.common.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;

public class DownloadGridsTask {
	private String targetGridUrl = null; 
	private String gaardsConfigDirectory = null;
	
	String tempDir = System.getProperty("java.io.tmpdir") + File.separator + "gaards";
	
	final static String CERT_PATTERN = "[\\w\\.\\-]+";
	final static String CONFIG_PATTERN = "[\\w\\.\\-]+\\.xml";
	final static String GRID_PATTERN = "[\\w\\.\\-]+/";
	
	public DownloadGridsTask(String targetGridUrl, String gaardsConfigDirectory) {
		this.gaardsConfigDirectory = gaardsConfigDirectory;
		this.targetGridUrl = targetGridUrl;
	}
	
	public void execute() throws Exception {
		DefaultHttpClient httpclient = new DefaultHttpClient();

		HttpGet httpget = new HttpGet(targetGridUrl);

		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		String responseBody = httpclient.execute(httpget, responseHandler);
		Parser parser = new Parser(responseBody);
		NodeFilter filter = new TagNameFilter("A");
		NodeList list = parser.parse(filter);
		SimpleNodeIterator iter = list.elements();
		while (iter.hasMoreNodes()) {
			Node node = iter.nextNode();
			String targetGrid = ((LinkTag) node).extractLink();

			Pattern p = Pattern.compile(GRID_PATTERN);
			Matcher m = p.matcher(targetGrid);
			if (m.matches()) {
				parseServerDirectory(targetGridUrl, targetGrid, CONFIG_PATTERN);
				parseServerDirectory(targetGridUrl, targetGrid + File.separator + "certificates", CERT_PATTERN);

				File tempTargetGridDir = new File(tempDir + File.separator + targetGrid);
				File gaardsTargetGridDir = new File(gaardsConfigDirectory + File.separator + targetGrid);
				File tempTargetGridCertDir = new File(tempTargetGridDir, "certificates");
				File gaardsTargetGridCertDir = new File(gaardsTargetGridDir, "certificates");
				Utils.copyDirectory(tempTargetGridDir, gaardsTargetGridDir);
				Utils.copyDirectory(tempTargetGridCertDir, gaardsTargetGridCertDir);
			}

		}
		httpclient.getConnectionManager().shutdown();
	}

	private void parseServerDirectory(String serverURL, String targetGrid, String pattern) throws IOException, ClientProtocolException,
			ParserException, Exception {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(serverURL + "/" + targetGrid);
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		String configFiles = httpclient.execute(httpget, responseHandler);
		Parser parser = new Parser(configFiles);
		NodeFilter filter = new TagNameFilter("A");
		NodeList configFileList = parser.parse(filter);
		SimpleNodeIterator configFileIter = configFileList.elements();				
		while (configFileIter.hasMoreNodes()) {
			Node configFileIterNode = configFileIter.nextNode();
			String configFileNodeName = ((LinkTag) configFileIterNode).extractLink();

			Pattern configFilePattern = Pattern.compile(pattern);
			Matcher configFileMatcher = configFilePattern.matcher(configFileNodeName);
			if (configFileMatcher.matches()) {
				downloadFile(httpget.getURI().toASCIIString(), configFileNodeName, tempDir + File.separator + targetGrid);
			}
		}
		httpclient.getConnectionManager().shutdown();
	}
	
	private void downloadFile(String location, String fileName, String toDir) throws IOException {
		File toDirectory = new File(toDir);
		if (!toDirectory.exists()) {
			toDirectory.mkdirs();
		}
        File toFile = new File(toDirectory, fileName);
        if (toFile.exists()) {
            toFile.delete();
        }
        
		URL url = new URL(location + "/" + fileName);
		ReadableByteChannel rbc = Channels.newChannel(url.openStream());
		FileOutputStream fos = new FileOutputStream(toFile);
		fos.getChannel().transferFrom(rbc, 0, 1 << 24);
		
	}

}
