package com.fiware.services;

import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PropertyController {

	@Autowired
	private WoTService woTService;

	@RequestMapping(method = RequestMethod.GET, value = "/properties")
	public Map<String, Map<String, String>> getAllProperties() {
		return woTService.getAllProperties();
	}

	@RequestMapping(method = RequestMethod.POST, value = "/query")
	public Object addProperty(@RequestBody Map<String, ArrayList<Object>> payload) throws Exception {

		String id = new String();
		String property = new String();
		
		System.out.println(payload);
		
		for (Entry<String, ArrayList<Object>> en : payload.entrySet()) {
			String key = en.getKey();

			if (key.equals("entities")) {
				ArrayList<Object> entityInfo = en.getValue();
				System.out.println(" entity info ::: " + entityInfo);
				for (int i = 0; i < entityInfo.size(); i++) {
					Map<String, String> entityinfoMap = (Map<String, String>) entityInfo.get(i);
					id = entityinfoMap.get("id");
					System.out.println(id);
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
		return woTService.getPropertyValues(url);
		
	}

}
