/*
 * Copyright (C) 2012 CyborgDev <cyborg@alta189.com>
 *
 * This file is part of CyborgCommit
 *
 * CyborgCommit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CyborgCommit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.alta189.cyborg.commit;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

public class ShortUrlService {
	private static Service service = Service.BIT_LY;
	private static String user = "";
	private static String apiKey = "";

	public String shorten(String url) {
		return shorten(url, null);
	}

	public static String shorten(String url, String keyword) {
		switch (service) {
			case BIT_LY:
				HttpClient httpclient = new HttpClient();
				HttpMethod method = new GetMethod("http://api.bit.ly/shorten");
				method.setQueryString(
						new NameValuePair[]{
								new NameValuePair("longUrl", url),
								new NameValuePair("version", "2.0.1"),
								new NameValuePair("login", user),
								new NameValuePair("apiKey", apiKey),
								new NameValuePair("format", "xml"),
								new NameValuePair("history", "1")
						}
				);
				try {
					httpclient.executeMethod(method);
					String responseXml = method.getResponseBodyAsString();
					String retVal = null;
					if (responseXml != null) {
						DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
						DocumentBuilder db = dbf.newDocumentBuilder();
						StringReader st = new StringReader(responseXml);
						Document d = db.parse(new InputSource(st));
						NodeList nl = d.getElementsByTagName("shortUrl");
						if (nl != null) {
							Node n = nl.item(0);
							retVal = n.getTextContent();
						}
					}

					return retVal;
				} catch (HttpException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				} catch (SAXException e) {
					e.printStackTrace();
				}
				return null;
			case SPOUT_IN:
				HttpClient client = new HttpClient();
				String result = null;
				client.getParams().setParameter("http.useragent", "Test Client");

				BufferedReader br = null;
				PostMethod pMethod = new PostMethod("http://spout.in/yourls-api.php");
				pMethod.addParameter("signature", apiKey);
				pMethod.addParameter("action", "shorturl");
				pMethod.addParameter("format", "simple");
				pMethod.addParameter("url", url);
				if (keyword != null) {
					pMethod.addParameter("keyword", keyword);
				}

				try {
					int returnCode = client.executeMethod(pMethod);

					if (returnCode == HttpStatus.SC_NOT_IMPLEMENTED) {
						System.err.println("The Post method is not implemented by this URI");
						pMethod.getResponseBodyAsString();
					} else {
						br = new BufferedReader(new InputStreamReader(pMethod.getResponseBodyAsStream()));
						String readLine;
						if (((readLine = br.readLine()) != null)) {
							result = readLine;
						}
					}
				} catch (Exception e) {
					System.err.println(e);
				} finally {
					pMethod.releaseConnection();
					if (br != null) {
						try {
							br.close();
						} catch (Exception fe) {
							fe.printStackTrace();
						}
					}
				}
				return result;
		}
		return null;
	}

	public static Service getService() {
		return service;
	}

	public static void setService(Service service) {
		ShortUrlService.service = service;
	}

	public static String getUser() {
		return user;
	}

	public static void setUser(String user) {
		ShortUrlService.user = user;
	}

	public static String getApiKey() {
		return apiKey;
	}

	public static void setApiKey(String apiKey) {
		ShortUrlService.apiKey = apiKey;
	}

	public enum Service {
		BIT_LY,
		SPOUT_IN;
	}
}
