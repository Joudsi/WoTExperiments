package org.fiware.services;

import org.json.JSONObject;

public class Property {

	    private JSONObject[] entities;
	    private String[] attributes;
	    
		public Property(JSONObject[] entities, String[] attributes) {
			super();
			this.entities = entities;
			this.attributes = attributes;
		}
		
		public JSONObject[] getEntities() {
			return entities;
		}
		public void setEntities(JSONObject[] entities) {
			this.entities = entities;
		}
		public String[] getAttributes() {
			return attributes;
		}
		public void setAttributes(String[] attributes) {
			this.attributes = attributes;
		}
	    
	    
	    
	    
}