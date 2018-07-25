package com.thelagg.skylounge.namehourschecker.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class URLConnectionReader {
	public static String getContent(String url) throws IOException {
		URL oracle = new URL(url);
        URLConnection yc = oracle.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                                    yc.getInputStream()));
        String inputLine;
        String s = "";
        while ((inputLine = in.readLine()) != null) 
            s += inputLine;
        in.close();
        return s;
	}
}
