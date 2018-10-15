package com.fiware.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

			JSONArray jsonArray_Interactions = myJsonObject.getJSONArray("interaction");

			for (int i = 0; i < jsonArray_Interactions.length(); i++) {
				JSONObject jsonObject_property = jsonArray_Interactions.getJSONObject(i);
				JSONArray jsonArrayForm = jsonObject_property.getJSONArray("form");

				for (int j = 0; j < jsonArrayForm.length(); j++) {

					String propertyName = new String(jsonObject_property.getString("name"));
					String urlP = new String(
							myJsonObject.getString("base") + jsonArrayForm.getJSONObject(j).getString("href"));
					if (jsonObject_property.getJSONArray("@type").getString(0).equals("Property")) {
						dataMap.put(propertyName, urlP);
					}
				}
			}
			for (String name : dataMap.keySet()) {

				String key = name.toString();
				String value = dataMap.get(name).toString();
				System.out.println("Property is :::: " + key);
				System.out.println("url is :::: " + value);

			}
		}
	}

	public Map<String, String> getAllProperties() {
		return dataMap; // Arrays.asList(dataMap)
	}

}
