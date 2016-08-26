package com.abhinav.qcards;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;

import android.os.Environment;

public class MappingReader {
	private static Hashtable<Integer,ArrayList<Integer>> mapTab;
	public static Hashtable<Integer,ArrayList<Integer>> loadMapping() throws IOException{
		mapTab = new Hashtable<Integer,ArrayList<Integer>>();
		//get the IDMap file from the QCards folder on external storage
		File mappingFile =  new File(
				Environment.getExternalStorageDirectory()
				+ "/QCards/IDMAP/");
		File file = mappingFile.listFiles()[0];
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String line=null;
		while((line=br.readLine())!=null){
			ArrayList<Integer> options = new ArrayList<Integer>();
			StringTokenizer stkn = new StringTokenizer(line,",");
			Integer key = Integer.parseInt(stkn.nextToken());
			while(stkn.hasMoreTokens()){
				options.add(Integer.parseInt(stkn.nextToken()));
			}
			mapTab.put(key, options);
		}
		return mapTab;
	}
}
