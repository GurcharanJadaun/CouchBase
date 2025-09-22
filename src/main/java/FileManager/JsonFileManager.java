package FileManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonFileManager {
	String dir, pathSep;
	 
	public JsonFileManager() {
		pathSep = File.separator;
		this.dir = System.getProperty("user.dir");
	}
	
	
	public Iterator<JsonNode> getItemsFromJson(String fileName) throws IOException {
		 ObjectMapper objectMapper = new ObjectMapper();
	        JsonNode jsonNode = objectMapper.readTree(new File(fileName+".json"));
	        Iterator<JsonNode> jsonArray = jsonNode.elements();
	    return jsonArray;
	}
	
	public JsonNode getItemFromJson(String fileName)throws IOException {
		 ObjectMapper objectMapper = new ObjectMapper();
	        JsonNode jsonNode = objectMapper.readTree(new File(fileName+".json"));
	    return jsonNode;
	}
	
	public static void main(String[] args) {
		JsonFileManager object = new JsonFileManager();
		
		try {
			JsonNode jsonItems = object.getItemFromJson("DeviceConfig");
			// jsonItems.toString();
			System.out.print(jsonItems.toString());
			
		}catch(Exception ex) {
			System.out.print(ex.toString());
		}
	}
	
}
