package com.androids.photoalbum.view;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Vector;

import android.content.Context;
import android.util.Log;

public class ImageFileManager {
	Context mContext = null;
	Vector<String> listFiles = null;
	Vector<String> listPathes = null;
	
	public void setContext(Context context){
		this.mContext = context;
	}
	
	public ImageFileManager(){
		listFiles = new Vector<String>();
		listPathes = new Vector<String>(3);
		listPathes.add("/sdcard/album");
		listPathes.add("/sdcard-ext/album");
		listPathes.add("/data/album");
	}

	public int listFiles(){
		int numb = 0;
		
		for(int i = 0; i< listPathes.size(); i++){
			File file = new File(listPathes.get(i));
			if(file.exists()){
				numb += updateFileList(listPathes.get(i));
			}
		}
		return numb;
	}
	
	private int updateFileList(String path) {
		int numb = 0;
		// It have to be matched with the directory in SDCard
		File f = new File(path);
		if (f != null && f.exists()) {
			File[] files = f.listFiles(new FilenameFilter() {
				// @override
				public boolean accept(File dir, String name) {
					return ((name.endsWith(".JPG")) || (name.endsWith(".jpg")) || (name
							.endsWith(".png")) || (name.endsWith(".PNG")) 
							|| (name.endsWith(".jpeg")) || (name.endsWith(".JPEG")));
				}
			});
			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					File file = files[i];
					this.listFiles.add(file.getPath());
					numb++;
				}
			}
		}
		return numb;

	}
	
	public Vector<String> getFileList(){
		this.listFiles.clear();
		this.listFiles();
		return this.listFiles;
	}
	
	public boolean isExist(String fileName){
		File file = new File(fileName);
		return file.exists();
	}
	
	public boolean isPathesExist() {
		for(int i = 0; i<this.listPathes.size(); i++){
			if(isExist(listPathes.get(i))){
				return true;
			}
		}
		return false;
    }
	
	public String getPathes(){
		String pathes="";
		for(int i = 0; i<this.listPathes.size(); i++){
			pathes = pathes + listPathes.get(i) + "\n";
		}
		return pathes;
	}
}
