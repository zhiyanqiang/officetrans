package com.netpower.library.util;

import java.util.ArrayList;

public class JArray {
	private ArrayList<String> index = null;
	private ArrayList<String> data = null;

	public JArray(){
		index = new ArrayList<String>();
		data = new ArrayList<String>();
	}

	public JArray(String arrstr){
		set(makeArray(arrstr));
	}

	@SuppressWarnings("unchecked")
	public JArray(ArrayList<String> index, ArrayList<String> data){
		this.index = (ArrayList<String>) index.clone();
		this.data = (ArrayList<String>) data.clone();
	}

	public JArray clone(){
		return new JArray(index, data);
	}

	public void set(JArray array){
		if(array == null)
			array = new JArray();
		this.index = array.index;
		this.data = array.data;
	}

	public int len(){
		return index.size();
	}

	public String getIndex(int ind){
		return index.get(ind);
	}

	public String get(int ind){
		return data.get(ind);
	}

	public void add(String name, String dat){
		index.add(name);
		data.add(dat);
	}

	public void set(String name, String dat){
		int pos = index.indexOf(name);
		if(pos < 0){
			add(name, dat);
			return;
		}
		data.set(pos, dat);
	}

	public String get(String name){
		int pos = index.indexOf(name);
		if(pos < 0)
			return null;
		return data.get(pos);
	}

	public String get(String name, String base){
		int pos = index.indexOf(name);
		if(pos < 0 || data.get(pos) == null){
			return base;
		}
		return data.get(pos);
	}

	public void del(String name){
		int pos = index.indexOf(name);
		if(pos > -1){
			index.remove(pos);
			data.remove(pos);
		}
	}

	//json_encode
	public String makeString() {
		String ret = "{";
		for(int i = 0; i < index.size(); i++){
			String ind = index.get(i);
			ret += '"' + ind + "\":\"" + get(ind) + "\",";
		}
		ret = ret.substring(0, (ret.lastIndexOf(",") < 0 ? ret.length() : ret.lastIndexOf(","))) + "}";
		return ret;
	}

	//json_decode
	public JArray makeArray(String arr) {
		if(arr == null)
			return null;
		arr = arr.trim();
		int pos = (arr.indexOf("{") >= 0 ? arr.indexOf("{") : (arr.indexOf("[") >= 0 ? arr.indexOf("[") : -1));
		if(pos < 0)
			return null;
		arr = arr.substring(pos);
		if(arr.length() < 3)
			return null;
		return getJString(arr);
	}

	public JArray getJString(String ok){
		ok = ok.trim();
		if(ok.length() == 0)
			return null;
		boolean before = false;
		boolean two = false;
		boolean one = false;
		boolean isValue = false;
		char[] json = ok.toCharArray();
		String index = "";
		String data = "";
		JArray ret = new JArray();
		for (int i = 0; i < json.length; i++) {
			if (!one && !two) {
				if ((json[i] == '{') || (json[i] == '[')) {
					before = true;
					data = "";
					index = "";
					isValue = false;
					continue;
				} else if ((json[i] == '}') || (json[i] == ']')) {
					index = index.trim();
					if(index.length() != 0){
						ret.add(index, data);
					}
					return ret;
				} else if (json[i] == ':') {
					isValue = true;
					before = true;
					continue;
				} else if (json[i] == ',') {
					index = index.trim();
					if(index.length() != 0){
						ret.add(index, data);
					}
					data = "";
					index = "";
					isValue = false;
					before = true;
					continue;
				}
			}
			else{
				if((one && json[i] == '\'') || (two && json[i] == '"')){
					one = false;
					two = false;
					continue;
				}
			}
			if(before){
				if(json[i] == '"'){
					two = true;
					before = false;
					continue;
				}
				if(json[i] == '\''){
					one = true;
					before = false;
					continue;
				}
			}
			if(!isValue)
				index += json[i];
			else
				data += json[i];
		}
		return ret;
	}
}
