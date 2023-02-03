package mysql_comparer;

import com.dao.base.DaoException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Schema extends Construct{
	Schema(String name, MysqlDatabase2 mysql) throws DaoException {
		super(name, mysql, SCHEMA_KEY);
		// schema
		String sql = String.format("SELECT SCHEMA_NAME,DEFAULT_CHARACTER_SET_NAME,DEFAULT_COLLATION_NAME "
				+ "FROM information_schema.SCHEMATA WHERE SCHEMA_NAME = '%s'", this.name);
		loadInformation(this.mysql.sql_fetch_one2(sql));

		// table
		sql = String.format(
				"SELECT ENGINE,TABLE_NAME,TABLE_TYPE,ROW_FORMAT,AUTO_INCREMENT,TABLE_COLLATION"
						+ " FROM information_schema.TABLES WHERE TABLE_SCHEMA = '%s'",
				this.name);
		List<DBRecord2> tables = this.mysql.sql_fetch_rows2(sql);

		// column
		sql = String.format(
				"SELECT TABLE_NAME,COLUMN_NAME,COLUMN_DEFAULT,IS_NULLABLE,"
						+ "COLUMN_TYPE,DATA_TYPE,EXTRA,COLUMN_COMMENT,CHARACTER_SET_NAME,COLLATION_NAME"
						+ " FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = '%s' ORDER BY TABLE_NAME,ORDINAL_POSITION",
				this.name);

		List<DBRecord2> columns = this.mysql.sql_fetch_rows2(sql);
		Map<String, List<DBRecord2>> columnsMap = new HashMap<>();
		for (DBRecord2 column : columns) {
			String tableName = column.getString("TABLE_NAME");
			column.remove("TABLE_NAME");
			columnsMap.putIfAbsent(tableName, new ArrayList<>());
			columnsMap.get(tableName).add(column);
		}

		// index
		List<DBRecord2> tableIndexes = mysql.sql_fetch_rows2(String.format(
				"SELECT TABLE_NAME,GROUP_CONCAT(CONCAT('`',COLUMN_NAME,'`') ORDER BY SEQ_IN_INDEX) AS `COLUMNS`,INDEX_TYPE,INDEX_NAME"
						+ " FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = '%s' GROUP BY TABLE_NAME,INDEX_NAME",
				this.name));
		Map<String, List<DBRecord2>> tableIndexesMap = new HashMap<>();
		for (DBRecord2 tableIndex : tableIndexes) {
			String tableName = tableIndex.getString("TABLE_NAME");
			tableIndex.remove("TABLE_NAME");
			tableIndexesMap.putIfAbsent(tableName, new ArrayList<>());
			tableIndexesMap.get(tableName).add(tableIndex);
		}

		// 设置每个表的属性
		String tableName;
		ArrayList<Information> construct = new ArrayList<>();
		for(int i = 0; i < tables.size(); i++){
			tableName = tables.get(i).getString("TABLE_NAME");
			DBRecord2 tableInfomation = tables.get(i);
			tableInfomation.remove("TABLE_NAME");
			construct.add(new Table(tableName, this.name, this.mysql, tableInfomation,
					columnsMap.getOrDefault(tableName, new ArrayList<>()),
					tableIndexesMap.getOrDefault(tableName, new ArrayList<>())));
		}
		loadConstruct(construct);
	}

	private boolean tableMatched(Table table, String[] tableMatches) {
		for (String tableMatch : tableMatches) {
			if (table.name.matches(tableMatch)) {
				return true;
			}
		}
		return false;
	}


	@Deprecated
	@Override
	protected DDL createDDL() throws Exception {
		throw new Exception("Deprecated");
	}

	@Deprecated
	@Override
	protected DDL createDDL(Information target) throws Exception {
		throw new Exception("Deprecated");
	}

	protected DDL createDDL(String[] tableMatches) throws Exception {
		DDL ddl = new DDL();
		for (int i = 0; i < getConstructSize(); i++) {
			Table e = getElement(i);
			if (!tableMatched(e, tableMatches)) {
				continue;
			}
			ddl.append(getElement(i).compare(null));
		}
		return ddl;
	}

	protected DDL createDDL(Information target, boolean ignoreDrop, String[] tableMatches) throws Exception {
		if (target == null) {
			return createDDL(tableMatches);
		}
		DDL ddl = new DDL();
		Table e, targetE;
		int targetTableSize = ((Schema) target).getConstructSize();
		for (int i = 0; i < targetTableSize; i++) {
			e = ((Schema) target).getElement(i);
			if (!tableMatched(e, tableMatches)) {
				continue;
			}
			if (!ignoreDrop && !contains(e.name)) {
				ddl.append(e.createDropDDL());
			}
		}
		for (int i = 0; i < getConstructSize(); i++) {
			e = getElement(i);
			if (!tableMatched(e, tableMatches)) {
				continue;
			}
			targetE = ((Schema) target).getElement(e.name);
			if (!e.equals(targetE)) {
				ddl.append(e.compare(targetE));
			}
		}
		return ddl;
	}

	@Override
	public DDL createDropDDL() {
		return null;
	}


	@Override
	public Table getElement(int index) {
		return (Table) construct.get(index);
	}

	@Override
	public Table getElement(String name) {
		Table e;
		for(int i = 0; i < getConstructSize(); i++){
			e = getElement(i);
			if(e.name.equals(name)){
				return e;
			}
		}
		return null;
	}

	@Deprecated
	@Override
	public String dump(String[] match) throws DaoException {
		throw new DaoException("Deprecated");
	}

	/**
	 * 导出数据
	 * 
	 * @param tableMatches
	 * @param dataMatches
	 * @return
	 * @throws DaoException
	 */
	public String dump(String[] tableMatches, String[] dataMatches) throws DaoException {
		StringBuilder sql = new StringBuilder();
		for (int i = 0; i < getConstructSize(); i++) {
			Table table = getElement(i);
			boolean needDump = false;
			for (String tableMatch : tableMatches) {
				if (table.name.matches(tableMatch)) {
					needDump = true;
					break;
				}
			}
			if (!needDump) {
				continue;
			}
			sql.append(table.dump(dataMatches));
		}
		return sql.toString();
	}

	@Deprecated
	@Override
	public DDL compare(Information target) throws Exception {
		throw new Exception("Deprecated");
	}

	/**
	 * 结构对比
	 * 
	 * @param target
	 * @param tableMatches
	 * @return
	 * @throws Exception
	 */
	public DDL compare(Information target, String[] tableMatches) throws Exception {
		return compare(target, false, tableMatches);
	}

	/**
	 * 结构对比
	 * 
	 * @param target
	 * @param ignoreDrop
	 * @param tableMatches
	 * @return
	 * @throws Exception
	 */
	public DDL compare(Information target, boolean ignoreDrop, String[] tableMatches) throws Exception {
		if (target == null || target instanceof Schema) {
			return createDDL(target, ignoreDrop, tableMatches);
		} else {
			throw new Exception("target object is not instanceof Table");
		}
	}

	/**
	 * 数据对比
	 * 
	 * @param target
	 * @param tableMatches
	 * @param dataMatches
	 * @return
	 * @throws Exception
	 */
	public String compareData(Information target, String[] tableMatches, String[] dataMatches) throws Exception {
		if (target == null) {
			return dump(tableMatches, dataMatches);
		}
		if (!(target instanceof Schema)) {
			throw new Exception("target object is not instanceof Schema");
		}
		StringBuilder sql = new StringBuilder();
		Schema targetSchema = (Schema) target;
		for (int i = 0; i < getConstructSize(); i++) {
			Table table = getElement(i);
			boolean needDump = false;
			for (String tableMatch : tableMatches) {
				if (table.name.matches(tableMatch)) {
					needDump = true;
					break;
				}
			}
			if (!needDump) {
				continue;
			}
			Table targetTable = targetSchema.getElement(table.name);
			String data = table.dump(dataMatches);
			if (targetTable == null || !table.equals(targetTable)) {
				sql.append(data);
				continue;
			}
			String targetData = targetTable.dump(dataMatches);
			if (!data.equals(targetData)) {
				sql.append(data);
			} else {
			}
		}
		return sql.toString();
	}
}
