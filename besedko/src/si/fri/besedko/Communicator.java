package si.fri.besedko;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

public class Communicator {
	protected static final String encoding = "UTF-8";
	//protected static final String userAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.19 (KHTML, like Gecko) Ubuntu/11.10 Chromium/18.0.1025.168 Chrome/18.0.1025.168 Safari/535.19";
	protected static final String userAgent = "Mozilla/5.0; (Macintosh; U; Intel Mac OS X 10_6_3; en-us) AppleWebKit/533.16 (KHTML, like Gecko) Version/5.0 Safari/533.16";
	
	public static InputStream GETis(String url, Map<String,String> data) throws IOException {
		URL urlurl = new URL(url);	// Sestavi objekt URL iz niza
		URLConnection connection = urlurl.openConnection();
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); // Za zahteve POST
		connection.setRequestProperty("User-Agent", userAgent);
		connection.setUseCaches(true);	// Vklopi keširanje
		connection.setDoOutput(true);	// Vklopi izhod (želimo prejeti odgovor)

		// Ustvari pisalni tok, ki bo povezavi posredoval podatke
		OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());

		osw.write(buildURLParameters(data)); // Zapiši podatke v pisalni tok
		osw.flush();	// Poplavi pisalni tok
		osw.close();	// Zapri pisalni tok

		return connection.getInputStream(); // Vrni povezavo do toka vhodnih podatkov
	}

	public static String GET(String url, Map<String,String> data) throws IOException {
		InputStreamReader isr = new InputStreamReader(GETis(url,data)); // Tok podatkov odpri z bralcem toka podatkov
		String rawText = readReader(isr);	// Preberi celoten tok podatkov in ga shrani v niz
		isr.close();	// Zapri bralec toka podatkov
		return rawText;	// Vrni surov niz
	}
	
	// Function source: http://stackoverflow.com/a/1607997
	protected void enableSelfSignedSSL() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
                public boolean verify(String hostname, SSLSession session) { return true; }
            });
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[] {
            	new X509TrustManager() {
	                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
	                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
	                public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
        		}
            }, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch (Exception e) { // should never happen
            e.printStackTrace();
        }
	}

	public static String getEncoding() {
		return encoding;
	}
	
	public static String getUserAgent() {
		return userAgent;
	}
	
	public static String getXmlHeader(String encoding) {
		return "<?xml version=\"1.0\" encoding=\""+encoding+"\" ?>";
	}
	public static String getXmlHeader() {
		return getXmlHeader(encoding);
	}
	
	public static String getHTMLHeader(String encoding) {
		return "<head>"+"<meta http-equiv=\"Content-type\" content=\"text/html; charset="+encoding+"\" />"+"</head>";
	}
	public static String getHTMLHeader() {
		return getHTMLHeader(encoding);
	}
	
	// Build URL parameters String from KV data
	protected static String buildURLParameters(Map<String,String> data) {
		if (data == null) return "";
		StringBuilder dataString = new StringBuilder();
		Iterator<String> it = data.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			String val = data.get(key);
			
			if (key == null) continue; // If key is null: skip
			if (val == null) val = ""; // If val is null: assume blank
			
			try {
				dataString.append('&' + URLEncoder.encode(key, encoding) + '=' + URLEncoder.encode(val, encoding));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}	
		}

		/*
		// We don't actually need this
		if (dataString.charAt(0) != '?') {
			dataString.setCharAt(0, '?');
			//dataString.replace(1, 1, "?"); // Replace first delimiter "&" into "?"
		}
		*/

		return dataString.toString();
	}
	
	// Prebere celotno vsebino Readerja in jo shrani ter vrne kot niz
	private static String readReader(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int c;
		while ((c = rd.read()) != -1)
			sb.append((char)c);
		return sb.toString();
  	}
}
