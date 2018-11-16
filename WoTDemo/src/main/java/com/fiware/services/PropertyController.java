package com.fiware.services;

import java.awt.List;
import java.net.URL;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.text.html.parser.Entity;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PropertyController {

	@Autowired
	private WoTService woTService;

	@RequestMapping(method = RequestMethod.GET, value = "/properties")
	public Map<String, Map<String, String>> getAllProperties() {
		return woTService.getAllProperties();
	}

	@RequestMapping(method = RequestMethod.POST, value = "/queryContext")
	public String addProperty(@RequestBody Map<String, ArrayList<Object>> payload) throws Exception {

		String id = new String();
		String type = new String();
		String property = new String();
		
		
		System.out.println("here is the payload");
		System.out.println(payload);

		for (Entry<String, ArrayList<Object>> en : payload.entrySet()) {
			String key = en.getKey();

			if (key.equals("entities")) {
				ArrayList<Object> entityInfo = en.getValue();
				System.out.println(" entity info ::: " + entityInfo);
				for (int i = 0; i < entityInfo.size(); i++) {
					Map<String, String> entityinfoMap = (Map<String, String>) entityInfo.get(i);
					id = entityinfoMap.get("id");
					type = entityinfoMap.get("type");
					System.out.println(id);
					System.out.println(type);
				}

			}

			if (key.equals("attributes")) {
				ArrayList<Object> propertiesToRetrieve = en.getValue();
				System.out.println("Properties to retrieve ::: " + propertiesToRetrieve);
				for (int i = 0; i < propertiesToRetrieve.size(); i++) {
					property = propertiesToRetrieve.get(i).toString();
					System.out.println(property);
				}
			}

		}

		Map<String, String> thingPrperties = woTService.getThingProperties(id);
		System.out.println(thingPrperties);
		URL url = woTService.getpropertyURL(thingPrperties, property);
		System.out.print(url);
		
//		adjust the output to be NGSI v1 compatible  - needs to be in a seperate method

		JSONObject jo1 = new JSONObject();
		JSONObject jo2 = new JSONObject();
		JSONObject jo22 = new JSONObject();
		JSONObject jo = new JSONObject();
		JSONObject jsonResponse = new JSONObject();

		JSONArray ja1 = new JSONArray();
		JSONArray ja = new JSONArray();
		
		
		JSONObject jomd = new JSONObject();
//		JSONObject jomd1 = new JSONObject();
		
		JSONArray jamd = new JSONArray();
		
		

		jo1.put("name", property);
		jo1.put("type", woTService.getPropertyType(id, property));
		jo1.put("value", woTService.getPropertyLatestValue(url, property));
		
		
		jomd.put("name", "timestamp");
		jomd.put("type", "DateTime");
		jomd.put("value", woTService.getPropertyLatestTimeStamp(property));
		
		jamd.put(jomd);
		
		jo1.put("metadatas", jamd);

		ja1.put(jo1);

		jo2.put("attributes", ja1);
		jo2.put("id", id);
		jo2.put("isPattern", "false");
		jo2.put("type", type);

		jo22.put("code", HttpStatus.OK);
		jo22.put("reasonPhrase", "OK");

		jo.put("contextElement", jo2);
		jo.put("statusCode", jo22);

		ja.put(jo);

		jsonResponse.put("contextResponses", ja);

		return jsonResponse.toString();
		
//		return woTService.getPropertyLatestValue(url, property);

	}

}

