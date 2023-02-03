package com.utils.config;

import com.utils.JediCast;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigCenter {
    public static ConcurrentHashMap<String, String> config=new ConcurrentHashMap<>();
    
    /**
     * 判断是否包含某个配置项
     * @param key
     * @return
     */
    public static boolean containsKey(String key) {
        return config.containsKey(key);
    }
    
    /**
     * 获取某个配置,返回String值, 如果没有这个key则返回""
     * @param key 
     * @return
     */
    public static String getString(String key){
        if(config.containsKey(key)){
            return config.get(key);
        }
        return "";
    }
    
    /**
     * 获取某个配置,并转为boolean值, 如果没有这个key则返回false
     * @param key 
     * @return
     */
    public static boolean getBoolean(String key){
        if(config.containsKey(key)){
            return JediCast.toBoolean(config.get(key));
        }
        return false;
    }
    
    /**
     * 获取某个配置,并转为int值, 如果没有这个key则返回0
     * @param key 
     * @return
     */
    public static int getInt(String key){
        if(config.containsKey(key)){
            return JediCast.toInt(config.get(key));
        }
        return 0;
    }
    
    /**
     * 获取某个配置,并转为long值, 如果没有这个key则返回0
     * @param key 
     * @return
     */
    public static long getLong(String key){
        if(config.containsKey(key)){
            return JediCast.toLong(config.get(key));
        }
        return 0L;
    }  
    
    /**
     * 获取某个配置,并转为double值, 如果没有这个key则返回0
     * @param key 
     * @return
     */
    public static double getDouble(String key){
        if(config.containsKey(key)){
            return JediCast.toDouble(config.get(key));
        }
        return 0D;
    }
        
    /**
     * 重新读取配置, 有重复的使用以前的
     * @param filePath, 文件路径
     * @param filelist, {{文件名,类型(str)}}
     */
    public static void initConfig(String filePath, String[][] filelist){
        for(String[] fileinfo:filelist) {
            String fullFilePath=filePath+File.separator+fileinfo[0];
            ConfigReader reader;
            for(ConfigReader.ConfigFileType type: ConfigReader.ConfigFileType.values()) {
				if (type.getValue().equals(fileinfo[1])) {
                    reader = new ConfigReader(fullFilePath, type);
                    config.putAll(reader.getValues());
                    break ;
                }
            }
        }
    }
    
	/**
	 * 重新读取配置, 有重复的使用以前的
	 * 
	 * @param filelist
	 */
	public static void initConfig(ConfigFileInfo... filelist) {
		for (ConfigFileInfo fileinfo : filelist) {
			String fullFilePath = fileinfo.fullFilePath;
			ConfigReader reader = new ConfigReader(fullFilePath, fileinfo.type);
			config.putAll(reader.getValues());
		}
	}

	/**
	 * 重新读取配置, 有重复的使用以前的
	 * 
	 * @param filelist
	 */
	public static void initConfig(ConfigReader... readers) {
		for (ConfigReader reader : readers) {
			config.putAll(reader.getValues());
		}
	}

	public static class ConfigFileInfo {
        ConfigReader.ConfigFileType type;
        String fullFilePath;
        
        /**
         * 文件类型
         * @param fullFilePath, 文件路径+文件名
         * @param type, 文件类型
         */
        public ConfigFileInfo(String fullFilePath, ConfigReader.ConfigFileType type) {
            this.fullFilePath=fullFilePath;
            this.type=type;
        }
    }
}
