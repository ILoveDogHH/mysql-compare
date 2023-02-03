package mysql_comparer;

import com.dao.base.DaoException;
import com.dao.database.base.DBRecord;
import com.dao.database.mysql.MysqlDatabaseConfig;
import com.utils.TimeManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.List;

public class Comparer {
	public String newDbUrl = null;
	public String newDbFilePath = null;
	public String newRunFilePath = null;
	public String newVersion = null;

	public String oldDbUrl = null;
	public String oldDbFilePath = null;
	public String oldVersion = null;
	public String mode = "diff";
	public String dbname = null;

	public String[] dataMatches = null;
	public String exportFilePath = ".";

	public String[] tableMatches = null;

	public boolean ignoreDrop = true;

	public final String time = TimeManager.currentTime2DateString("yyyyMMdd_HHmmss");

	/**
	 * 复制参数
	 * 
	 * @param comparer
	 * @param args
	 */
	public void parseArgs(String[] args) {
		// --new.db=f:xxx
		// --new.version=1.9.0
		// --old.db=jdbc:mysql://localhost:3306/p16?user=p16&password=yeswecan2015
		// --old.version=1.9.10
		// --mode=a/d/e(default=diff)
		for (String arg : args) {
			String tmp = "--new.db=";
			if (arg.startsWith(tmp)) {
				newDbUrl = arg.substring(tmp.length());
			}
			tmp = "--new.file=";
			if (arg.startsWith(tmp)) {
				newDbFilePath = arg.substring(tmp.length());
			}
			tmp = "--new.runfile=";
			if (arg.startsWith(tmp)) {
				newRunFilePath = arg.substring(tmp.length());
			}
			tmp = "--new.version=";
			if (arg.startsWith(tmp)) {
				newVersion = arg.substring(tmp.length());
			}

			tmp = "--old.db=";
			if (arg.startsWith(tmp)) {
				oldDbUrl = arg.substring(tmp.length());
			}
			tmp = "--old.file=";
			if (arg.startsWith(tmp)) {
				oldDbFilePath = arg.substring(tmp.length());
			}
			tmp = "--old.version=";
			if (arg.startsWith(tmp)) {
				oldVersion = arg.substring(tmp.length());
			}

			tmp = "--mode=";
			if (arg.startsWith(tmp)) {
				mode = arg.substring(tmp.length());
			}

			tmp = "--dbname=";
			if (arg.startsWith(tmp)) {
				dbname = arg.substring(tmp.length());
			}
			
			tmp = "--export.path=";
			if (arg.startsWith(tmp)) {
				exportFilePath = arg.substring(tmp.length());
			}


			tmp = "--match.data=";
			if (arg.startsWith(tmp)) {
				String option = arg.substring(tmp.length());
				if (option == null || option.equals("")) {
					dataMatches = new String[] { ".*" };
				} else {
					dataMatches = arg.substring(tmp.length()).split("[,]");
				}
			}

			tmp = "--match.table=";
			if (arg.startsWith(tmp)) {
				String option = arg.substring(tmp.length());
				if (option == null || option.equals("")) {
					tableMatches = new String[] { ".*" };
				} else {
					tableMatches = arg.substring(tmp.length()).split("[,]");
				}
			}

			tmp = "--ignore-drop=";
			if (arg.startsWith(tmp)) {
				ignoreDrop = Boolean.parseBoolean(arg.substring(tmp.length()));
			}
		}
	}

	public void handle() throws Exception {
		switch (mode) {
			case "u":
			case "update":
				// update();
				// 关闭update的功能
				throw new Exception("error: illegal mode-" + mode);
			case "d":
			case "diff":
				diff();
				break;
			case "e":
			case "export":
				export();
				break;
			default:
				throw new Exception("error: illegal mode-" + mode);
		}
		Logger.log("finish, dbname={}, newVersion={}, oldVersion={}, time={}, mode={}", dbname, newVersion,
				oldVersion, time, mode);
	}

	/**
	 * 将文件里面的内容完整的导入到临时数据库里去
	 * 
	 * @param db
	 * @param file
	 * @throws DaoException
	 * @throws IOException
	 */
	private void _cleanAndImportToTempDb(MysqlDatabase2 database, String file) throws DaoException, IOException {
		// 清空数据库
		List<DBRecord> allTables = database.sql_fetch_rows("show tables");
		String databaseName = database.getDBName();
		for (DBRecord table : allTables) {
			String tableName = table.getString("Tables_in_" + databaseName);
			database.sql_update(String.format("drop table %s", tableName));
		}
		// 导入数据
		database.runSqlFile(file);
	}

	/**
	 * 对比+创建差异文件
	 * 
	 * @param newSchema
	 * @param oldSchema
	 * @return
	 * @throws Exception
	 */
	private String createDiff(Schema newSchema, Schema oldSchema) throws Exception {
		Logger.log("start create diff");
		DDL ddl = newSchema.compare(oldSchema, ignoreDrop, tableMatches);
		String data = newSchema.compareData(oldSchema, tableMatches, dataMatches);
		File exportPath = new File(exportFilePath);
		if (!exportPath.exists()) {
			exportPath.mkdirs();
		}
		String fileName = String.format("%s%s%s_db_diff_%s_%s_%s.sql", exportFilePath, File.separator, dbname,
				oldVersion, newVersion, time);
		File file = new File(fileName);
		write(file, ddl.toString() + data);
		return fileName;
	}

	/**
	 * 创建完整文件
	 * 
	 * @param newSchema
	 * @return
	 * @throws Exception
	 */
	private String createComplete(Schema newSchema) throws Exception {
		Logger.log("start create complete");
		DDL ddl = newSchema.compare(null, tableMatches);
		String data = newSchema.compareData(null, tableMatches, dataMatches);
		File exportPath = new File(exportFilePath);
		if (!exportPath.exists()) {
			exportPath.mkdirs();
		}
		String fileName = String.format("%s%s%s_db_%s_%s.sql", exportFilePath, File.separator, dbname, newVersion,
				time);
		File file = new File(fileName);
		write(file, ddl.toString() + data + getExtraSql());
		return fileName;
	}

	/**
	 * 1. 执行一个runfile到old数据库中</br>
	 * 2. 将完整的newDbFile同步到old数据库中</br>
	 * 3. newDb同步到old数据库中</br>
	 * 
	 * 这个更新会直接更新到oldDb中, 并生成对应的diff和db文件</br>
	 * 要求oldDbUrl不是空的</br>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private void update() throws Exception {
		Logger.log("start-Comparer.update");
		if (dbname == null) {
			throw new Exception("on Comparer.update, (dbname) is null");
		}
		if (oldVersion == null) {
			throw new Exception("on Comparer.update, (old.version) is null");
		}
		if (newVersion == null) {
			throw new Exception("on Comparer.update, (new.version) is null");
		}
		if (oldDbUrl == null) {
			throw new Exception("on Comparer.update, (old.db) is null");
		}
		int total = newDbFilePath == null ? 1 : 0 + newRunFilePath == null ? 1 : 0 + newDbUrl == null ? 1 : 0;
		if (total == 0) {
			throw new Exception("on Comparer.update, (new.file, new.runfile, new.db) are null");
		}
		if (total != 1) {
			throw new Exception("on Comparer.update, (new.file, new.runfile, new.db) only one could be not null");
		}
		if (newDbFilePath != null) {
			// 读取newDb, 读取oldDb
			MysqlDatabase2 newDb = new MysqlDatabase2(new MysqlDatabaseConfig(Main.config, "temp.new"));
			_cleanAndImportToTempDb(newDb, newDbFilePath);
			MysqlDatabase2 oldDb = new MysqlDatabase2(new MysqlDatabaseConfig(oldDbUrl));
			Schema newSchema = new Schema(newDb.getDBName(), newDb);
			Schema oldSchema = new Schema(oldDb.getDBName(), oldDb);
			// 导出差异
			String diffFile = createDiff(newSchema, oldSchema);
			// 导出完整的newDb
			createComplete(newSchema);
			// 更新到oldDb中
			oldDb.runSqlFile(diffFile);
			return;
		}
		if (newRunFilePath != null) {
			// 将newFile导入到oldDb中
			MysqlDatabase2 oldDb = new MysqlDatabase2(new MysqlDatabaseConfig(oldDbUrl));
			oldDb.runSqlFile(newRunFilePath);
			Schema oldSchema = new Schema(oldDb.getDBName(), oldDb);
			// 然后导出oldDb
			createComplete(oldSchema);
			// 复制diff文件
			File outFile = new File(String.format("%s%s%s_db_diff_%s_%s_%s", exportFilePath, File.separator, dbname,
					oldVersion, newVersion, time));
			if (!outFile.exists()) {
				File newFile2 = new File(newRunFilePath);
				Files.copy(newFile2.toPath(), outFile.toPath());
			}
			return;
		}
		if (newDbUrl != null) {
			// 读取newDb, 读取oldDb
			MysqlDatabase2 newDb = new MysqlDatabase2(new MysqlDatabaseConfig(newDbUrl));
			MysqlDatabase2 oldDb = new MysqlDatabase2(new MysqlDatabaseConfig(oldDbUrl));
			Schema newSchema = new Schema(newDb.getDBName(), newDb);
			Schema oldSchema = new Schema(oldDb.getDBName(), oldDb);
			// 导出差异
			String diffFile = createDiff(newSchema, oldSchema);
			// 导出完整的newDb
			createComplete(newSchema);
			// 更新到oldDb中
			oldDb.runSqlFile(diffFile);
			return;
		}
	}

	/**
	 * 1. 将完整的newDbFile和old数据库对比</br>
	 * 2. newDb和old数据库对比</br>
	 * 3. 将完整的sqlDbFile和完整的oldDbFile对比</br>
	 * 4. newDb和完整的oldDbFile对比</br>
	 * 
	 * 生成对应的diff和db文件</br>
	 * 
	 * @throws Exception
	 */
	private void diff() throws Exception {
		Logger.log("start-Comparer.diff");
		if (dbname == null) {
			throw new Exception("on Comparer.diff, (dbname) is null");
		}
		if (oldVersion == null) {
			throw new Exception("on Comparer.diff, (old.version) is null");
		}
		if (newVersion == null) {
			throw new Exception("on Comparer.diff, (new.version) is null");
		}
		if (newDbFilePath == null && newDbUrl == null) {
			throw new Exception("on Comparer.diff, (new.file, new.db) are null");
		}
		if (newDbFilePath != null && newDbUrl != null) {
			throw new Exception("on Comparer.diff, (new.file, new.db) only one could be not null");
		}
		if (oldDbFilePath == null && oldDbUrl == null) {
			throw new Exception("on Comparer.diff, (old.file, old.db) are null");
		}
		if (oldDbFilePath != null && oldDbUrl != null) {
			throw new Exception("on Comparer.diff, (old.file, old.db) only one could be not null");
		}
		MysqlDatabase2 newDb;
		if (newDbFilePath != null) {
			newDb = new MysqlDatabase2(new MysqlDatabaseConfig(Main.config, "temp.new"));
			_cleanAndImportToTempDb(newDb, newDbFilePath);
		} else {
			newDb = new MysqlDatabase2(new MysqlDatabaseConfig(newDbUrl));
		}
		MysqlDatabase2 oldDb;
		if (oldDbFilePath != null) {
			Logger.log("compare after clean and import to temp db");
			oldDb = new MysqlDatabase2(new MysqlDatabaseConfig(Main.config, "temp.old"));
			_cleanAndImportToTempDb(oldDb, oldDbFilePath);
		} else {
			Logger.log("compare directly");
			oldDb = new MysqlDatabase2(new MysqlDatabaseConfig(oldDbUrl));
		}
		Schema newSchema = new Schema(newDb.getDBName(), newDb);
		Schema oldSchema = new Schema(oldDb.getDBName(), oldDb);
		// 导出差异
		createDiff(newSchema, oldSchema);
		// 导出完整的newDb
		createComplete(newSchema);
	}

	/**
	 * 将new从指定数据库连接中dump出来
	 * 
	 * @throws Exception
	 */
	private void export() throws Exception {
		Logger.log("start-Comparer.export");
		if (dbname == null) {
			throw new Exception("on Comparer.export, (dbname) is null");
		}
		if (newDbUrl == null) {
			throw new Exception("on Comparer.export, (new.db) is null");
		}
		if (newVersion == null) {
			throw new Exception("on Comparer.export, (new.version) is null");
		}
		// 导出newDb
		MysqlDatabase2 newDb = new MysqlDatabase2(new MysqlDatabaseConfig(newDbUrl));
		Schema newSchema = new Schema(newDb.getDBName(), newDb);
		createComplete(newSchema);
	}

	/**
	 * 写文件
	 * 
	 * @param file
	 * @param content
	 * @throws IOException
	 */
	private void write(File file, String content) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");
		out.write(content);
		out.flush();
		out.close();
		Logger.log("{}\t\t...completed", file);
	}

	/**
	 * 完整文件额外数据
	 * 
	 * @return
	 */
	private String getExtraSql() {
		return "";
		// StringBuilder stringBuilder = new StringBuilder();
//		stringBuilder.append(
//				"INSERT IGNORE INTO server_act(aid,startts,endts,avalue,note,noend,auto) VALUES (114,UNIX_TIMESTAMP(),UNIX_TIMESTAMP()+10*365*86400,0,'攻城略地活动',1,1),(115,UNIX_TIMESTAMP(),UNIX_TIMESTAMP()+10*365*86400,0,'成长计划',1,1);\n");
		// return stringBuilder.toString();
	}
}
