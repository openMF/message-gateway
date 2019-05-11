/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
/* package sms.wirepick.smsgateway.Utility; */
package org.fineract.messagegateway.sms.providers.impl.wirepick.smsgateway.Utility;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.httpclient.NameValuePair;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/*import sms.wirepick.smsgateway.model.MsgStatus;*/
import org.fineract.messagegateway.sms.providers.impl.wirepick.smsgateway.model.MsgStatus;
/*import sms.wirepick.smsgateway.model.WpkClientConfig;*/
import org.fineract.messagegateway.sms.providers.impl.wirepick.smsgateway.model.WpkClientConfig;

public class Settings {
	
	
	private static final String API = "API";
	private static final String SMS = "sms";
	private static final String STATUS2 = "status";
	private static final String PHONE = "phone";
	private static final String MSGID = "msgid";
	private static final String UTF_8 = "UTF-8";
	public static final String HOST = "https://sms.wirepick.com/httpsms/send" ; 
	private static final String DEFAULT_AFFILIATE = "999" ; 
	
	public static MsgStatus parseWirepickResultXML(InputStream stream) throws Exception, IOException  {
        DocumentBuilder objDocumentBuilder = DocBuilder();
        
        Document doc = objDocumentBuilder.parse(stream);
        
        return DocumentProcessor(doc);
    }

	private static DocumentBuilder DocBuilder() throws ParserConfigurationException {
		DocumentBuilderFactory objDocumentBuilderFactory = null;
        DocumentBuilder objDocumentBuilder = null;
        
        objDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
        objDocumentBuilder = objDocumentBuilderFactory.newDocumentBuilder();
		return objDocumentBuilder;
	}
	
	public static MsgStatus parseWirepickResultXML(StringReader stream) throws Exception, IOException  {
        DocumentBuilder objDocumentBuilder = DocBuilder();
        
        Document doc = objDocumentBuilder.parse(new InputSource(stream));
        return DocumentProcessor(doc);
    }

	private static MsgStatus DocumentProcessor(Document doc) {
		NodeList nList = doc.getElementsByTagName(SMS);

        MsgStatus status = new MsgStatus();
        
        for (int temp = 0; temp < nList.getLength(); temp++) {

            Node nNode = nList.item(temp);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) nNode;

                if (eElement.getElementsByTagName(MSGID) != null) {
                    status.setMsgid(eElement.getElementsByTagName(MSGID).item(0).getTextContent());
                    status.setPhone( eElement.getElementsByTagName(PHONE).item(0).getTextContent());
                    status.setStatus(eElement.getElementsByTagName(STATUS2).item(0).getTextContent());
                } else {
                    
                	return null ;
                }
                
                break ; 
            }
        }
		return status;
	}
	
	public static String HTTPparameters(WpkClientConfig config) throws UnsupportedEncodingException
	{
		StringBuffer buffer = new  StringBuffer(HOST) ; 
		
		ValidateParams(config);
		
		buffer.append("?client=").append(URLEncoder.encode(config.getClientid(), UTF_8)) ;
		
		if(config.getAffiliate() != null)
		{
			buffer.append("&affiliate=").append(URLEncoder.encode(config.getAffiliate(), UTF_8));
		}
		else
		{
			buffer.append("&affiliate=").append(DEFAULT_AFFILIATE);
		}
		
		buffer.append("&password=").append(URLEncoder.encode(config.getPws(), UTF_8));
		buffer.append("&phone=").append(URLEncoder.encode(config.getPhone(), UTF_8));
		buffer.append("&text=").append(URLEncoder.encode(config.getSms(), UTF_8));
		buffer.append("&from=").append(URLEncoder.encode(config.getSenderid(), UTF_8));
		
		if(config.getTag() != null)
		{
			buffer.append("&tag=").append(URLEncoder.encode(config.getTag(), UTF_8));
		}
		else
		{
			buffer.append("&tag=").append(URLEncoder.encode(API, UTF_8));
		}
		
		
		return buffer.toString() ; 
		
	}

	private static void ValidateParams(WpkClientConfig config) {
		if(config.getClientid() == null || config.getClientid().isEmpty())
		{
			throw new NullPointerException("Client id/Password is required") ; 
		}
		else if(config.getPws() == null || config.getPws().isEmpty())
		{
			throw new NullPointerException("Client id/Password is required") ; 
		}
		else if(config.getPhone() == null || config.getPhone().isEmpty())
		{
			throw new NullPointerException("Phone is required") ; 
		}
		else if(config.getSms() == null || config.getSms().isEmpty())
		{
			throw new NullPointerException("SMS content is required") ; 
		}
		else if(config.getSenderid() == null || config.getSenderid().isEmpty())
		{
			throw new NullPointerException("Sender id is required") ; 
		}
	}
	
	
	public static  NameValuePair[] GetParameters(WpkClientConfig config) throws UnsupportedEncodingException
	{
		
		ValidateParams(config);
		NameValuePair[] nameValuePairs = new NameValuePair[7] ; 
		
		nameValuePairs[0] = new NameValuePair("client", config.getClientid()) ;
		if(config.getAffiliate() != null)
		{
			nameValuePairs[1] = new NameValuePair("affiliate", config.getAffiliate()) ; 
		}
		else
		{
			nameValuePairs[1] = new NameValuePair("affiliate",DEFAULT_AFFILIATE) ; 
		}
		
		nameValuePairs[2] = new NameValuePair("password", config.getPws()) ; 
		nameValuePairs[3] = new NameValuePair("phone", config.getPhone()) ; 
		nameValuePairs[4] = new NameValuePair("text",config.getSms()) ;
		nameValuePairs[5] = new NameValuePair("from", config.getSenderid()) ;
		if(config.getAffiliate() != null)
		{
			nameValuePairs[6] = new NameValuePair("tag",config.getTag()) ;
		}
		else
		{
			nameValuePairs[6] = new NameValuePair("tag",API) ;
		}
		
		
		return nameValuePairs;
		
		
	}
	
	

}
