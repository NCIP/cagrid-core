package org.cagrid.transfer.context.client.helper;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.cagrid.transfer.descriptor.DataTransferDescriptor;
import org.globus.axis.gsi.GSIConstants;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.gssapi.GlobusGSSCredentialImpl;
import org.globus.net.GSIHttpURLConnection;
import org.ietf.jgss.GSSCredential;


public class TransferClientHelper {

    /**
     * Returns a handle to the input stream of the socket which is returning the
     * data referred to by the descriptor. This method can make an https
     * connection if desired using the credentials passed in.  If you wish to use this
     * method to connect to http it will not use the Crediential whether you pass 
     * them in or they are null,
     * 
     * @param desc              data transfer descriptor received from TransferServiceContextClient
     * @param creds             creator of the transfer resource credentials
     * @return
     * @throws Exception
     */
    public static InputStream getData(DataTransferDescriptor desc, GlobusCredential creds) throws Exception {
        URL url = new URL(desc.getUrl());
        if (url.getProtocol().equals("http")) {
            URLConnection conn = url.openConnection();
            conn.connect();
            return conn.getInputStream();
        } else if (url.getProtocol().equals("https")) {
            if(creds!=null){
            GlobusGSSCredentialImpl cred = new GlobusGSSCredentialImpl(creds, GSSCredential.INITIATE_AND_ACCEPT);
            GSIHttpURLConnection connection = new GSIHttpURLConnection(url);
            connection.setGSSMode(GSIConstants.MODE_SSL);
            connection.setCredentials(cred);
            return connection.getInputStream();
            } else {
                throw new Exception(
                "To use the https protocol to retrieve data from the Transfer Service you must have credentials");
            }
        }
        throw new Exception("Protocol " + url.getProtocol() + " not supported.");
    }


    /**
     * Returns a handle to the input stream of the socket which is returning the
     * data referred to by the descriptor. This method cannot make secure https
     * connects and only works with http.
     * 
     * @param desc              data transfer descriptor received from TransferServiceContextClient
     * @return
     * @throws Exception
     */
    public static InputStream getData(DataTransferDescriptor desc) throws Exception {
        return getData(desc, null);
    }


    /**
     * Reads from the input stream to put the data to the server. Be sure to close the stream when
     * done writing the data. This method can use http and https if the
     * credentials are provided.  This is a blocking call. Will return with the entire data has been transmitted.
     * 
     * @param is                input stream providing the data
     * @param contentLength     number of bytes in the input stream to be read
     * @param desc              data transfer descriptor received from TransferServiceContextClient
     * @param creds             creator of the transfer resource credentials
     * @return
     * @throws Exception
     */
    public static void putData(InputStream is, long contentLength, DataTransferDescriptor desc, GlobusCredential creds) throws Exception {
        URL url = new URL(desc.getUrl());
        if (url.getProtocol().equals("http")) {
            PostMethod post = new PostMethod(desc.getUrl());
            InputStreamRequestEntity re = new InputStreamRequestEntity(is, contentLength);
            post.setRequestEntity(re);
            HttpClient client = new HttpClient();
            int status = client.executeMethod(post);
            return;
        } else if (url.getProtocol().equals("https")) {
            if(creds!=null){
            GlobusGSSCredentialImpl cred = new GlobusGSSCredentialImpl(creds, GSSCredential.INITIATE_AND_ACCEPT);
            GSIHttpURLConnection connection = new GSIHttpURLConnection(url);
            connection.setGSSMode(GSIConstants.MODE_SSL);
            connection.setCredentials(cred);
            try {
                int l;
                byte[] buffer = new byte[1024];
                while ((l = is.read(buffer)) != -1) {
                    connection.getOutputStream().write(buffer, 0, l);
                }
            } finally {
                is.close();
            }
            connection.getOutputStream().close();
            connection.getInputStream().close();
            return;
            } else {
                throw new Exception(
                "To use the https protocol to stage data to the Transfer Service you must have credentials");
            }
        }
        throw new Exception("Protocol " + url.getProtocol() + " not supported.");
    }


    /**
     * Reads from the input stream to put the data to the server. This method can only put to an
     * http connection and not an https one. Be sure to close the stream when
     * done writing to it. Will return with the entire data has been transmitted.
     * 
     * @param is                input stream providing the data
     * @param contentLength     number of bytes in the input stream to be read
     * @param desc              data transfer descriptor received from TransferServiceContextClient              
     * @param creds             creator of the transfer resource credentials
     * @return
     * @throws Exception
     */
    public static void putData(InputStream is, long contentLength, DataTransferDescriptor desc) throws Exception {
        putData(is, contentLength, desc, null);
    }

}
