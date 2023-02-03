package mysql_comparer;

public class Column extends Information{
	
	public final String tableName;
	public final String schemaName;
	
	Column(String name, String tableName, String schemaName, String lastColumn, MysqlDatabase2 mysql,
			DBRecord2 infomation) {
		super(name, mysql, COLUMN_KEY);
		this.tableName = tableName;
		this.schemaName = schemaName;
		infomation.put("LAST_COLUMN", lastColumn);
		loadInformation(infomation);
	}

	@Override
	protected DDL createDDL(Information target) {
		if(target == null){
			return createColumnDDL("ADD");
		}
		return createColumnDDL("MODIFY");
	}
	
	private DDL createColumnDDL(String operation){
		// ALTER TABLE `u_mail` ADD COLUMN `writer`  int(11) NOT NULL DEFAULT %s AFTER `sort_w`;
		return new DDL(String.format("ALTER TABLE `%s` %s COLUMN `%s`%s%s%s%s%s%s%s;\n",
					tableName, operation, name, getTypeClause(), getCharSetClause(), getNullAbleClause(),
					getDefaultClause(), getCommentClause(), getExtraClause(), getPositionClause()),
					DDL.CLAUSE_TYPE.MODIFY);
	}
	
	private String getTypeClause(){
		return ' ' + information.getString("COLUMN_TYPE");
	}
	
	private String getNullAbleClause(){
		return information.getString("IS_NULLABLE").equals("NO") ? " NOT NULL" : "";
	}
	
	private String getCharSetClause(){
		if(information.getString("DATA_TYPE").contains("char")){
			return String.format(" CHARACTER SET %s COLLATE %s",
					information.getString("CHARACTER_SET_NAME"), information.getString("COLLATION_NAME"));
		}
		return EMPTY_STRING;
	}
	
	private String getDefaultClause(){
		String value = information.getString("COLUMN_DEFAULT");
		if(value.equals("null") || value.equals("NULL")){
			return EMPTY_STRING;
		}
		if(information.getString("DATA_TYPE").contains("char")){
			return String.format(" DEFAULT '%s'", value);
		}
		else if(!value.equals(EMPTY_VALUE)){
			return String.format(" DEFAULT %s", value);
		}
		return "";
	}
	
	private String getCommentClause(){
		String value = information.getString("COLUMN_COMMENT");
		return value.equals(EMPTY_VALUE) ? "" : String.format(" COMMENT '%s'", value);
	}
	
	private String getExtraClause(){
		String value = information.getString("EXTRA");
		return value.equals(EMPTY_VALUE) ? "" : String.format(" %s", value);
	}
	
	private String getPositionClause(){
		String value = information.getString("LAST_COLUMN");
		return value.equals(EMPTY_VALUE) ? " FIRST" : String.format(" AFTER `%s`", value);
	}
	
	@Override
	public DDL compare(Information target) throws Exception {
		if(target == null || target instanceof Column){
			return createDDL(target);
		}
		else{
			throw new Exception("target object is not instanceof Column");
		}
	}

	@Override
	protected DDL createDDL() throws Exception{
		//`finger_type` int(11) NOT NULL DEFAULT '0' COMMENT '0表示不出现手指1表示立刻出现手指',
		return new DDL(String.format("`%s`%s%s%s%s%s", name, getTypeClause(), getNullAbleClause(),
				getDefaultClause(), getCommentClause(), getExtraClause()), DDL.CLAUSE_TYPE.CREATE);
	}

	@Override
	public DDL createDropDDL() throws Exception {
		// ALTER TABLE p16_new_inner.cfg_tech DROP COLUMN tech_category ;
		return new DDL(String.format("ALTER TABLE `%s` DROP COLUMN `%s`;\n", tableName, name),
					DDL.CLAUSE_TYPE.MODIFY);
	}
	
}
