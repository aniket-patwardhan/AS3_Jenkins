package io.jenkins.plugins;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Base64;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.tasks.Publisher;
import jenkins.tasks.SimpleBuildStep;


public abstract class ApplicationExtension extends Builder implements SimpleBuildStep {
	
	
    public void ApplicationExtension() {
       
    }
	
    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Builder> {
    	
    	public DescriptorImpl() throws Exception{
    	TrustManager[] trustAllCerts = new TrustManager[] {
			       new X509TrustManager() {
			          public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			            return null;
			          }

			          public void checkClientTrusted(X509Certificate[] certs, String authType) {  }

			          public void checkServerTrusted(X509Certificate[] certs, String authType) {  }

			       }
			    };

			    SSLContext sc = SSLContext.getInstance("SSL");
			    sc.init(null, trustAllCerts, new java.security.SecureRandom());
			    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

			    // Create all-trusting host name verifier
			    HostnameVerifier allHostsValid = new HostnameVerifier() {
			        public boolean verify(String hostname, SSLSession session) {
			          return true;
			        }
			    };
			    // Install the all-trusting host verifier
			    HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
			    

		URL url = new URL ("https://10.1.1.245/mgmt/shared/appsvcs/declare");
		
		HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
		con.setRequestMethod("POST");

		String encoding = Base64.getEncoder().encodeToString(("admin:admin").getBytes());
		
		con.setRequestProperty ("Authorization", "Basic " + encoding);
		con.setRequestProperty("Content-Type", "application/json; utf-8");
		con.setRequestProperty("Accept", "application/json");
		
		con.setDoOutput(true);
		
		
		
		String jsonInputString = "{\n" + 
				"    \"class\": \"AS3\",\n" + 
				"    \"action\": \"deploy\",\n" + 
				"    \"declaration\": {\n" + 
				"        \"class\": \"ADC\",\n" + 
				"        \"schemaVersion\": \"3.10.0\",\n" + 
				"        \"id\": \"SuperNetOps_Class_1_Lab2_3_Step2\",\n" + 
				"        \"label\": \"SuperNetOps_Class_1_Lab2_3_Step2\",\n" + 
				"        \"remark\": \"Super NetOps Class 1 Lab 2.3 Step2\",\n" + 
				"        \"Tenant1\": {\n" + 
				"            \"class\": \"Tenant\",\n" + 
				"            \"HTTP_Service\": {\n" + 
				"                \"class\": \"Application\",\n" + 
				"                \"template\": \"http\",\n" + 
				"                \"serviceMain\": {\n" + 
				"                    \"class\": \"Service_HTTP\",\n" + 
				"                    \"virtualAddresses\": [\n" + 
				"                        \"10.1.20.121\"\n" + 
				"                    ],\n" + 
				"                    \"snat\": \"auto\",\n" + 
				"                    \"pool\": \"Pool1\"\n" + 
				"                },\n" + 
				"                \"Pool1\": {\n" + 
				"                    \"class\": \"Pool\",\n" + 
				"                    \"monitors\": [\n" + 
				"                        \"http\"\n" + 
				"                    ],\n" + 
				"                    \"members\": [\n" + 
				"                      {\n" + 
				"                            \"servicePort\": 8001,\n" + 
				"                            \"serverAddresses\": [\n" + 
				"                              \"10.1.10.100\"\n" + 
				"                            ]\n" + 
				"                        },\n" + 
				"                        {\n" + 
				"                            \"servicePort\": 8002,\n" + 
				"                            \"serverAddresses\": [\n" + 
				"                                \"10.1.10.101\"\n" + 
				"                            ]\n" + 
				"                        }\n" + 
				"\n" + 
				"                    ]\n" + 
				"                }\n" + 
				"            }            \n" + 
				"        }\n" + 
				"    }\n" + 
				" }";
		
		try(OutputStream os = con.getOutputStream()){
			byte[] input = jsonInputString.getBytes("utf-8");
			os.write(input, 0, input.length);			
		}

		int code = con.getResponseCode();
		System.out.println(code);
		
		try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))){
			StringBuilder response = new StringBuilder();
			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {
				response.append(responseLine.trim());
			}
			System.out.println(response.toString());
		}
	}

    
	
    @Override
    public boolean isApplicable(Class<? extends AbstractProject> aClass) {
        return true;
}
    }
}
