package mysql_comparer;

import com.dao.base.DaoException;

import java.util.ArrayList;
import java.util.List;

public class Table extends Construct{

	public final String schemaName;
	TableIndex tableIndex;
	
	Table(String name, String schemaName, MysqlDatabase2 mysql, DBRecord2 information, List<DBRecord2> columns,
			List<DBRecord2> tableIndexes)
			throws DaoException {
		super(name, mysql, TABLE_KEY);
		this.schemaName = schemaName;
		loadInformation(information);
		String columnName;
		String lastColumn = "";
		ArrayList<Information> construct = new ArrayList<>();
		for (int i = 0; i < columns.size(); i++) {
			columnName = columns.get(i).getString("COLUMN_NAME");
			construct.add(new Column(columnName, this.name, this.schemaName, lastColumn, this.mysql, columns.get(i)));
			lastColumn = columnName;
		}
		loadConstruct(construct);
		tableIndex = new TableIndex(this.schemaName, name, mysql, tableIndexes);
	}

	@Override
	public Column getElement(int index){
		return (Column) construct.get(index);
	}
	
	@Override
	public Column getElement(String name){
		Column e;
		for(int i = 0; i < getConstructSize(); i++){
			e = getElement(i);
			if(e.name.equals(name)){
				return e;
			}
		}
		return null;
	}

	@Override
	public boolean equals(Construct target){
		if(!super.equals(target)){
			return false;
		}
		return tableIndex.equals(((Table) target).getTableIndex());
	}
	
	@Override
	protected DDL createDDL(Information target) throws Exception {
		if(target == null){
			return createDDL();
		}
		DDL ddl = new DDL();
		int targetColumnSize = ((Table) target).getConstructSize();
		Column e, targetE;
		//这里还要比较information
		for(int i = 0; i < targetColumnSize; i++){
			e = ((Table) target).getElement(i);
			if(!contains(e.name)){
				ddl.append(e.createDropDDL());
			}
		}
		for(int i = 0; i < getConstructSize(); i++){
			e = getElement(i);
			targetE = ((Table) target).getElement(e.name);
			if(!e.equals(targetE)){
				ddl.append(e.compare(targetE));
			}
		}
		if(!tableIndex.equals(((Table) target).getTableIndex())){
			ddl.append(tableIndex.compare(((Table) target).getTableIndex()));
		}
		ddl.append("\n", DDL.CLAUSE_TYPE.MODIFY);
		return ddl;
	}

	public TableIndex getTableIndex(){
		return tableIndex;
	}
	
	@Override
	public DDL compare(Information target) throws Exception {
		if(target == null || target instanceof Table){
			return createDDL(target);
		}
		else{
			throw new Exception("target object is not instanceof Table");
		}
	}

	@Override
	protected DDL createDDL() throws Exception {
		String createTableDDL = mysql.sql_fetch_one(String.format("SHOW CREATE TABLE %s", name)).getString("Create Table");
		createTableDDL = createTableDDL.replace("CREATE TABLE", "CREATE TABLE IF NOT EXISTS");
		createTableDDL = createTableDDL.replaceAll("AUTO_INCREMENT=[0-9]{1,}", "AUTO_INCREMENT=1");
		return new DDL(createTableDDL + ";\n\n", DDL.CLAUSE_TYPE.CREATE);
	}
	
//	private String getEngineClause(){
//		return String.format("ENGINE=%s", information.get("ENGINE"));
//	}
//	
//	private String getDefaultCharsetClause(){
//		return "DEFAULT CHARSET=utf8mb4";
//	}
//
//	private String getCollateClause(){
//		return String.format("COLLATE=%s", information.get("TABLE_COLLATION"));
//	}
//
//	private String getAutoIncrementClause(){
//		String autoIncrement = information.getString("AUTO_INCREMENT");
//		return autoIncrement.equals("null") ? "" : String.format("auto_increment = %s", autoIncrement);
//	}

	/*
	private String getRestrict(int index){
		return null;
	}
	
	private String getRestrict(String name){
		return null;
	}
	*/
	@Override
	public DDL createDropDDL() throws Exception{
		// DROP TABLE act_statistics_base;
		return new DDL(String.format("DROP TABLE `%s`;\n\n", name), DDL.CLAUSE_TYPE.CREATE);
	}

	@Override
	public String dump(String[] match) throws DaoException{
		boolean flag = false;
		for (String element : match) {
			if(name.matches(element)){
				flag = true;
				break;
			}
		}
		if(!flag){
			return new String();
		}
		StringBuilder stringBuilder = new StringBuilder();
		String[] columnList = new String[getConstructSize()];
		for(int i = 0; i < getConstructSize(); i++){
			if(stringBuilder.length() > 0){
				stringBuilder.append(',');
			}
			columnList[i] = getElement(i).name;
			stringBuilder.append('`' + columnList[i] + '`');
		}
		String columns = stringBuilder.toString();
		StringBuilder sql = new StringBuilder();
		sql.append(String.format("DELETE FROM %s;\n", name));
		if(mysql.sql_fetch_one_cell_int(String.format("SELECT COUNT(1) FROM %s LIMIT 0,1", name)) == 0){
			sql.append("\n\n");
			return sql.toString();
		}		
		sql.append(String.format("INSERT IGNORE %s (%s) VALUES\n", name, columns));
		List<DBRecord2> date = mysql.sql_fetch_rows2(String.format("SELECT %s FROM %s", columns, name));
		StringBuilder part = new StringBuilder();
		DBRecord2 e;
		for(int i = 0; i < date.size(); i++){
			e = date.get(i);
			part.delete(0, part.length());
			if(i > 0){
				part.append(",\n");
			}
			part.append('(');
			for(int k = 0; k < columnList.length; k++){
				if(k > 0){
					part.append(',');
				}
				part.append('\'' + e.getString(columnList[k]).replace("\'", "\\\'") + '\'');
			}
			part.append(")");
			sql.append(part);
		}
		sql.append(";\n\n");
		return sql.toString();
	}

}
