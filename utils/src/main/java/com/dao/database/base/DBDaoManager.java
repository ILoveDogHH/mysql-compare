package com.dao.database.base;

import com.dao.database.mysql.MysqlDatabase;
import com.dao.database.mysql.MysqlDatabaseConfig;
import com.logger.JLogger;
import com.utils.ClassSearcher;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DBDaoManager {
    /**
     * 所有的daos
     */
    private ConcurrentHashMap<Class<?>, DBDaoBase> daoMap = new ConcurrentHashMap<>();
	public KeyDBDaoManager<String> manager = new KeyDBDaoManager<>();
    
    /**
     * 利用enum做单例模式
     */
	public DBDaoManager() {
    }
    
    /**
     * 增加DBDao实例
     * @param dao
     */
    public void add(DBDaoBase dao) {
        Optional.ofNullable(dao).ifPresent(d->daoMap.putIfAbsent(d.getClass(), d));
    }
    
    /**
     * 增加分服dao
     * @param serverId
     * @param serverDbIp
     * @return
     * @throws ClassNotFoundException
     */
	public  DBInterface addSubServerDb(int serverId, String serverDbIp, String server_db_name, int port, String userName, String passWorld) throws ClassNotFoundException {
		MysqlDatabaseConfig config = new MysqlDatabaseConfig(serverDbIp, port, server_db_name, userName, passWorld);
		DBInterface subServerDb = new MysqlDatabase(config);
		manager.register(String.valueOf(serverId), subServerDb, new String[] {"service.dao.subServer"});
		return subServerDb;
	}
	
    

    
    /**
     * 获取DBDao实例
     * @param name
     * @return
     * @throws DBInterfaceException 
     */
    @SuppressWarnings("unchecked")
    public <T extends DBDaoBase> T get(Class<T> claz) throws DBInterfaceException {
        return Optional.ofNullable((T) daoMap.get(claz))
                .orElseThrow(()->{
                    String msg=String.format("dao[%s] has not register", claz.getName());
                    DBInterfaceException execption = new DBInterfaceException(msg);
                    JLogger.error("", execption);
                    return execption;
                });
    }
    

    
   
    
    /**
     * 获取DBDao实例, 如果这个dao不存在 则用database去初始化他
     * @param claz
     * @param database
     * @return
     * @throws DBInterfaceException
     */
    @SuppressWarnings("unchecked")
    public <T extends DBDaoBase> T get(Class<T> claz, DBInterface database) throws DBInterfaceException {
        try{
            return Optional.ofNullable((T) daoMap.get(claz))
                .orElseGet(()->{
                    try {
                        Constructor<T> constructor = claz.getConstructor(DBInterface.class);
                        T newDao = constructor.newInstance(database);
                        add(newDao);
                        return newDao;
                    }catch(NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        throw new NoSuchElementException(e.getMessage());
                    }
                });
        }catch(NoSuchElementException e) {
            String msg=String.format("dao[%s] register failed", claz.getName());
            JLogger.error(msg, e);
            throw new DBInterfaceException(msg, e);
        }
    }
    
    /**
     * 关闭一个db连接
     * @param name
     */
    public void destory(Class<? extends DBDaoBase> claz) {
        daoMap.remove(claz);
    }
    
    /**
     * 使用当前的classLoader注册package下的dao
     * @param database
     * @param packageNames
     */
    public void register(DBInterface database, String[] packageNames) {
        registerByClassSearcher(new ClassSearcher(), database, packageNames);
    }
    

    
    
    /**
     * 使用指定的classLoader注册package下的dao
     * @param classLoader
     * @param database
     * @param packageNames
     */
    public void register(ClassLoader classLoader, DBInterface database, String[] packageNames) {
        registerByClassSearcher(new ClassSearcher(classLoader), database, packageNames); 
        
    }
    
    /**
     * 注册dao方法
     * @param classSearcher
     * @param database
     * @param packageNames
     */
    private void registerByClassSearcher(ClassSearcher classSearcher, DBInterface database, String[] packageNames) {
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
                if(!dbDaoBase.isAssignableFrom(cls)){
                    continue;
                }
                Constructor<?> constructor = cls.getConstructor(DBInterface.class);
                DBDaoBase newDao = (DBDaoBase) constructor.newInstance(database);
                add(newDao);
            } catch (ClassNotFoundException | SecurityException | IllegalArgumentException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
				JLogger.error("init dao error: ", e);
				System.out.println("init dao error: " + e.getMessage());
            }
        }
    }
    

    
}
