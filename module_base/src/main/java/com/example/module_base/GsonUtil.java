package com.example.module_base;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 一些JSON和实体类(T)之间的转换
 *
 */
public class GsonUtil {
	private static final String GSON_LOG = "GSON_TOOLS";

	/**
	 * 使用json转换成实体Bean
	 * 
	 * @param json json数据字符串
	 * @param className 类名，为了和JSON对接
	 * @return 返回这个类的实体类
	 */
	public static <T> T json2Bean(String json, Class<T> className) {
		T t = null;
		try {
			Gson gson = new Gson();
			t = gson.fromJson(json, className);
		} catch (Exception e) {
			Log.e(GSON_LOG, e.toString());
		}
		return t;
	}


	/**
	 * 使用json转换成List
	 * 
	 * @param json json格式字符串
	 * @param className  类名
	 * @return 返回一个list
	 */
	public static <T> List<T> json2List(String json, Class<T> className) {
		List<T> list = new ArrayList<T>();
		try {
			Gson gson = new Gson();
			list = gson.fromJson(json, new TypeToken<List<T>>() {
			}.getType());
		} catch (Exception e) {
			Log.e(GSON_LOG, e.toString());
		}
		return list;
		
	}

    /**
     * 根据具体类型转换对象
	 * json转列表用
     **/
    public static <T> T jsonToObject(String jsonStr, Type listType) {
        Gson gson = new Gson();
        return gson.fromJson(jsonStr,listType);
    }
//	Type listType = new TypeToken<List<OrderCenterModel>>() {}.getType();
//	List<OrderCenterModel> orderModelList = GsonUtil.jsonToObject(jsonStr,listType);

	/**
	 * 返回Map<String,T>
	 * 
	 * @param json json数据字符串
	 * @param className 类名
	 * @return 返回一个Map类型
	 */
	public static <T> Map<String, T> json2Map(String json, Class<T> className) {
		Map<String, T> map = new HashMap<String, T>();
		try {
			Gson gson = new Gson();
			map = gson.fromJson(json, new TypeToken<Map<String, T>>() {
			}.getType());
		} catch (Exception e) {
			Log.e(GSON_LOG, e.toString());
		}
		return map;
	}
	
	/**
	 * 返回Set<T>
	 * 
	 * @param json 一个json格式数据
	 * @param className 匹配的数据类型
	 * @return 返回这个类型的集合Set
	 */
	public static <T> Set<T> json2Set(String json, Class<T> className) {
		Set<T> set = new HashSet<T>();
		try {
			Gson gson = new Gson();
			set = gson.fromJson(json, new TypeToken<Set<T>>() {
			}.getType());
		} catch (Exception e) {
			Log.e(GSON_LOG, e.toString());
		}
		return set;
	}
	/**
	 * 返回List<Mao<String,T>>
	 * @param json 一个json格式数据
	 * @param className 匹配的数据类型
	 * @return 返回这个类型的集合Set
	 */
	
	public static <T> List<Map<String, T>> listKeyMaps(String json,Class<T> className) {
        List<Map<String, T>> list = new ArrayList<Map<String, T>>();
        try {
            Gson gson = new Gson();
            list = gson.fromJson(json,
                    new TypeToken<List<Map<String, T>>>() {
                    }.getType());
        } catch (Exception e) {
          Log.e(GSON_LOG,e.toString());
        }
        return list;
    } 
	
	/**
	 * 将实体类转换成JSON
	 * @return 返回JSON字符串
	 */
	public static <T> String bean2json(T t) {
		Gson gson = new Gson();
		return gson.toJson(t);
	}

	/**
	 * 泛型List集合对象转换成JSON字符串
	 * 
	 * @param list  List集合对象
	 *            
	 * @return json  返回JSON对象
	 */
	public static <T> String list2json(List<T> list) {
		Gson gson = new Gson();
		return gson.toJson(list);
	}

	/**
	 * Map集合转换成JSONString
	 * 
	 * @param map key为String,value为实体类<T>
	 *           
	 * @return JSON实体类对象
	 */
	public static <T> String map2json(Map<String, T> map) {
		Gson gson = new Gson();
		return gson.toJson(map);
	}

	/**
	 * Set集合转换为JSONString
	 * 
	 * @param
	 * @return 返回一个标准JSON字符串
	 */
	public static <T> String set2json(Set<T> set) {
		Gson gson = new Gson();
		return gson.toJson(gson);
	}

	/**
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static <T> ArrayList<T> jsonToArrayList(String json, Class<T> clazz)
	{
		Type type = new TypeToken<ArrayList<JsonObject>>()
		{}.getType();
		ArrayList<JsonObject> jsonObjects = new Gson().fromJson(json, type);

		ArrayList<T> arrayList = new ArrayList<>();
		for (JsonObject jsonObject : jsonObjects)
		{
			arrayList.add(new Gson().fromJson(jsonObject, clazz));
		}
		return arrayList;
	}
}