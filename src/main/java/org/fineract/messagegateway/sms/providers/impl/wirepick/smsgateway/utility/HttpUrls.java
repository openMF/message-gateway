package sms.wirepick.smsgateway.Utility;

import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import sms.wirepick.smsgateway.model.MsgStatus;

public class HttpUrls {
	
	
	
	private static final String GET = "GET";
	
	public static MsgStatus sendByPostMethod(String sUrl, NameValuePair[] data,Map<String, String> headers) throws Exception {
		HttpClient httpClient = new HttpClient() ; 
		
		PostMethod postMethod = new PostMethod(sUrl);
		postMethod.setRequestHeader("Accept-Charset", "UTF-8");

		if (headers != null && !headers.isEmpty()) {
			for (Entry<String, String> entry : headers.entrySet()) {
				postMethod.addRequestHeader(entry.getKey(), entry.getValue());
			}
		}

		postMethod.addParameters(data);

		try {
			int statusCode = httpClient.executeMethod(postMethod);
			
			if (statusCode == HttpStatus.SC_OK) {
				String httpResponse = postMethod.getResponseBodyAsString();
			
				return Settings.parseWirepickResultXML(new StringReader(httpResponse)) ; 
				
			}
		}  catch (Exception e) {
			throw e;
		} finally {
			postMethod.releaseConnection();
			
		}
		return null;
	}


	public static MsgStatus sendByUrlHttpConnection(String url) throws Exception {

        HttpURLConnection con = null;
        try {
        	
            URL obj = new java.net.URL(url);
            con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod(GET);
            con.connect();

            int responseCode = con.getResponseCode();

            if (responseCode == HttpStatus.SC_OK) {

              return  Settings.parseWirepickResultXML(con.getInputStream()) ; 
               
            } else {
            	return null ; 
            }

        } catch (Exception ex) {
        	System.out.println(ex.getStackTrace());
           throw new Exception(ex) ; 
            
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }

    }


}
