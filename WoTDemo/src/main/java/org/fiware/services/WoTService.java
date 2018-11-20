package org.fiware.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Timestamp;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
public class WoTService implements CommandLineRunner {

	@Value("#{'${some.server.url}'.split(',')}")
	private List<String> WoTurl;

	private JSONObject myJsonObject;

	Map<String, String> dataMap;
	Map<String, JSONObject> dataMapWithPropertyTypeInfo;
	
	Map<String, Map<String, String>> dataMapAll = new HashMap<>();
	Map<String, Map<String, JSONObject>> dataMapAllWithPropertyTypeInfo = new HashMap<>();

	JSONObject latestValueJO = new JSONObject();


	public List<String> getWoTurl() {
		System.out.println("this is the WoT Url Address from the properties file ");
		WoTurl.forEach(System.out::println);
		return WoTurl;
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println(getWoTurl());

		for (String w : WoTurl) {
			URL url = new URL(w);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			// optional default is GET
			con.setRequestMethod("GET");
			// add request header
			con.setRequestProperty("Accept", "application/ld+json"); // application/ld+json
			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			myJsonObject = new JSONObject(response.toString());
			dataMap = new HashMap<>();
			dataMapWithPropertyTypeInfo = new HashMap<>();
			String propertyType = "";

			JSONArray jsonArray_Interactions = myJsonObject.getJSONArray("interaction");

			for (int i = 0; i < jsonArray_Interactions.length(); i++) {
				JSONObject jsonObject_property = jsonArray_Interactions.getJSONObject(i);
				JSONArray jsonArrayForm = jsonObject_property.getJSONArray("form");
				propertyType = new String(jsonObject_property.getJSONObject("schema").getString("type"));
				String CapitalPropType = propertyType.substring(0, 1).toUpperCase() + propertyType.substring(1);
				
				for (int j = 0; j < jsonArrayForm.length(); j++) {

					String propertyName = new String(jsonObject_property.getString("name"));
					String urlP = new String(
							myJsonObject.getString("base") + jsonArrayForm.getJSONObject(j).getString("href"));
					if (jsonObject_property.getJSONArray("@type").getString(0).equals("Property")) {
						dataMap.put(propertyName, urlP);
						JSONObject typeJO = new JSONObject();
						typeJO.put("type",CapitalPropType);
						dataMapWithPropertyTypeInfo.put(propertyName, typeJO);
					}
				}
			}
			for (String name : dataMapWithPropertyTypeInfo.keySet()) {

				String key = name.toString();
				String value = dataMapWithPropertyTypeInfo.get(name).toString();
				System.out.println("Property is :::: " + key);
				System.out.println(value);

			}
			for (String name : dataMap.keySet()) {

				String key = name.toString();
				String value = dataMap.get(name).toString();
				System.out.println("Property is :::: " + key);
				System.out.println("url is :::: " + value);

			}
			dataMapAll.put(myJsonObject.getString("name"), dataMap);
			dataMapAllWithPropertyTypeInfo.put(myJsonObject.getString("name"), dataMapWithPropertyTypeInfo);
		}
		System.out.println(dataMapAll);
		System.out.println(dataMapAllWithPropertyTypeInfo);
	}

	public Map<String, Map<String, String>> getAllProperties() {
		return dataMapAll; // Arrays.asList(dataMap)
	}

	public Map<String, String> getThingProperties(String id) {
		// TODO Auto-generated method stub
		
	
		Map<String,String> properties = new HashMap<>();
		for (Entry<String, Map<String, String>> dma : dataMapAll.entrySet()) {
			String key = dma.getKey();

			if (key.equals(id)) {
				properties = dma.getValue();
			}
		}
		return properties;
	}

	public URL getpropertyURL(Map<String, String> thingPrperties, String property) throws IOException {
		// TODO Auto-generated method stub
		String propertyUrl= new String();
		for (Entry<String, String> tp : thingPrperties.entrySet()) {
			String key = tp.getKey();

			if (key.equals(property)) {
				propertyUrl = tp.getValue();
			}
		}
		
		URL url = new URL(propertyUrl);
		
		return url;
	}

	public String getPropertyLatestValue(URL url, String property) throws IOException {
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
      // optional default is GET
      con.setRequestMethod("GET");
      //add request header
      con.setRequestProperty("Accept", "application/json"); //application/ld+json
      int responseCode = con.getResponseCode();
      System.out.println("\nSending 'GET' request to URL : " + url);
      System.out.println("Response Code : " + responseCode);

      BufferedReader in = new BufferedReader(
              new InputStreamReader(con.getInputStream()));
      String inputLine;
      StringBuffer response = new StringBuffer();
      while ((inputLine = in.readLine()) != null) {
          response.append(inputLine);
      }
      in.close();

      //print in String
      System.out.println( "result of requesting this interaction" + response.toString());
      
      
      JSONArray jA = new JSONArray(response.toString());
      latestValueJO = jA.getJSONObject(0);
     
      
	Object ret = null;
	if (property.equals("temperature")) {
    	  System.out.println(latestValueJO.get("t"));
    	  ret  = latestValueJO.get("t");
	}
      if (property.equals("humedity")) {
    	  System.out.println(latestValueJO.get("h"));
    	  ret = latestValueJO.get("h");
	}

//		return response.toString();
      return ret.toString();
	}

	public String getPropertyType(String id, String property) {
		
		String propertyType = new String();
		
		for (Entry<String, Map<String, JSONObject>> dmawpti : dataMapAllWithPropertyTypeInfo.entrySet()) {
			String key = dmawpti.getKey();

			if (key.equals(id)) {
				Map<String, JSONObject> propertyTypeInfo = dmawpti.getValue();
				System.out.println(propertyTypeInfo);
				JSONObject propertyObj = propertyTypeInfo.get(property);
				System.out.println(propertyObj);
				propertyType = propertyObj.getString("type");
				System.out.println(propertyType);
			}
		}
		return propertyType;
	}

	public String getPropertyLatestTimeStamp(String property) {
        
		Object ts = new String();
		
		if (property.equals("temperature")) {
	    	  System.out.println(latestValueJO.get("t"));
	    	  ts  = latestValueJO.get("timestamp");
	    	  System.out.println("time stamp for :  " + property + "  " + ts);
		}
	      if (property.equals("humedity")) {
	    	  System.out.println(latestValueJO.get("h"));
	    	  ts = latestValueJO.get("timestamp");
	    	  System.out.println("time stamp for :  " + property + "  " + ts);
		}	    
		return ts.toString();
	}

}
