package com.fiware.services;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PropertyController {
	
	@Autowired
	private WoTService woTService;
	
	@RequestMapping(method = RequestMethod.GET, value = "/properties")
	public  Map<String, Map<String, String>> getAllProperties()  {
		return woTService.getAllProperties();
	}
	
}
