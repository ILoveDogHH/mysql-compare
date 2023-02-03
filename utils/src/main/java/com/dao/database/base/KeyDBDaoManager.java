package com.dao.database.base;

import com.dao.base.KeyDaoManager;
import com.logger.JLogger;
import com.utils.ClassSearcher;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class KeyDBDaoManager<K> extends KeyDaoManager<K, DBDaoBase> {
    /**
     * 获取key-dao实例, 如果不存在则使用database初始化
     * @param key
     * @param claz
     * @param database
     * @return
     * @throws DBInterfaceException
     */
    public <T extends DBDaoBase> T get(K key, Class<T> claz, DBInterface database) throws DBInterfaceException {
        try{
            T dao = get(key, claz);
            return dao;
        }catch(DBInterfaceException e) {
            try {
                Constructor<T> constructor = claz.getConstructor(DBInterface.class);
                T newDao = constructor.newInstance(database);
                add(key, newDao);
                return newDao;
            }catch(NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e2) {
                throw new DBInterfaceException(e2.getMessage(), e2);
            }
        }
    }
    /**
     * 使用当前的classLoader注册package下的key-dao
     * @param key
     * @param database
     * @param packageNames
     */
    public void register(K key, DBInterface database, String[] packageNames) {
        registerByClassSearcher(new ClassSearcher(), key, database, packageNames);
    }
    
    /**
     * 使用指定的classLoader注册package下的key-dao
     * @param classLoader
     * @param key
     * @param database
     * @param packageNames
     */
    public void register(ClassLoader classLoader, K key, DBInterface database, String[] packageNames) {
        registerByClassSearcher(new ClassSearcher(classLoader), key, database, packageNames); 
        
    }
    
    /**
     * 注册key-dao
     * @param classSearcher
     * @param key
     * @param database
     * @param packageNames
     */
    private void registerByClassSearcher(ClassSearcher classSearcher, K key, DBInterface database, String[] packageNames) {
        List<String> classNames = new ArrayList<>(); 
        // 获取包里所有的类
        for(String packagaName:packageNames) {
            classNames.addAll(classSearcher.getClassNames(packagaName, true));
        }
        Class<DBDaoBase> dbDaoBase=DBDaoBase.class;
        for(String className:classNames) {
            try {
                Class<?> cls = Class.forName(className, true, classSearcher.getClassLoader());
                // 检测是否继承CommondBase的
                if(!dbDaoBase.isAssignableFrom(cls) || Modifier.isAbstract(cls.getModifiers())){
                    continue;
                }
                Constructor<?> constructor = cls.getConstructor(DBInterface.class);
                DBDaoBase newDao = (DBDaoBase) constructor.newInstance(database);
                add(key, newDao);
            } catch (ClassNotFoundException | SecurityException | IllegalArgumentException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
				JLogger.error("init dao error: ", e);
				System.out.println("init dao error: " + e.getMessage());
            }
        }
    }
}
