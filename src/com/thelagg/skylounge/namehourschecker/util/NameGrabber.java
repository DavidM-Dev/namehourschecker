package com.thelagg.skylounge.namehourschecker.util;

import java.io.IOException;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class NameGrabber {
	
	List<Long> times;
	Map<String,List<Object[]>> searchCache;
	
	public NameGrabber() {
		times = new ArrayList<Long>();
		searchCache = new HashMap<String,List<Object[]>>();
	}
	
	public void waitForApi() throws InterruptedException {
		while(times.size()>=500) {
			Thread.sleep(500);
		}
		Long time = System.currentTimeMillis();
		times.add(time);
		new Thread(() -> {
			try {
				Thread.sleep(10*60*1000);
				times.remove(time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();
	}
	
	public List<Object[]> getNames(String currentName) throws InterruptedException, IOException, ParseException {
		if(searchCache.containsKey(currentName)) {
			List<Object[]> l = searchCache.get(currentName);
			if(System.currentTimeMillis()-(long)(l.get(l.size()-1)[0])<1000*60*30) {
				return l;
			}
		}
		
		List<Object[]> list = getNames(getUUID(currentName));
		list.add(new Object[] {System.currentTimeMillis()});
		searchCache.put(currentName, list);
		return list;
	}
	
	public List<Object[]> getNames(UUID uuid) throws InterruptedException, IOException, ParseException {
		waitForApi();
		String content = URLConnectionReader.getContent("https://api.mojang.com/user/profiles/" + uuid.toString().replaceAll("-", "") + "/names");
		JSONArray obj = (JSONArray)(new JSONParser().parse(content));
		List<Object[]> list = new ArrayList<Object[]>();
		for(int i = obj.size()-1; i>=0; i--) {
			Object o = obj.get(i);
			JSONObject json = (JSONObject)o;
			String name = (String)json.get("name");
			long changedTime = json.containsKey("changedToAt")?(Long)json.get("changedToAt"):0L;
			list.add(new Object[] {name,changedTime});
		}
		return list;
	}
	
	public UUID getUUID(String name) throws IOException, InterruptedException, ParseException {
		waitForApi();
		String content = URLConnectionReader.getContent("https://api.mojang.com/users/profiles/minecraft/" + name + "?at=" + Long.toString(System.currentTimeMillis()/1000));
		JSONObject obj = (JSONObject) new JSONParser().parse(content);
		return parseUUID((String)obj.get("id"));
	}
	
	public UUID parseUUID(String s) {
		if(s.length()==32) {
			s = s.substring(0, 8) + "-" + s.substring(8,12) + "-" + s.substring(12,16) + "-" +
					s.substring(16,20) + "-" + s.substring(20,32);
		}
		return UUID.fromString(s);
	}
	
}
