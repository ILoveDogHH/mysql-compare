package mysql_comparer;

public abstract class Information{
	/**
	 * .equals 比较用
	 */
	public static final String[][] KEY_SET2 = {
			{
			},
			{
				//"ENGINE",
				//"ROW_FORMAT",
				//"AUTO_INCREMENT",
				//"TABLE_COLLATION"
			},
			{
				"COLUMN_DEFAULT",
				"IS_NULLABLE",
				"COLUMN_TYPE",
				"DATA_TYPE",
				"EXTRA",
				"COLUMN_COMMENT",
				"CHARACTER_SET_NAME",
				"COLLATION_NAME",
				"LAST_COLUMN"
			},
			{
				"COLUMNS",
				"INDEX_TYPE",
			},
			{
			}
	};
	
	public static final String EMPTY_STRING = "";
	
	public static final String EMPTY_VALUE = "";
	
	public static final int SCHEMA_KEY = 0;
	public static final int TABLE_KEY = 1;
	public static final int COLUMN_KEY = 2;
	public static final int INDEX_KEY = 3;
	public static final int TABLE_INDEX_KEY = 4;
	
	
	protected MysqlDatabase2 mysql;
	protected String name;
	protected DBRecord2 information;
	public final int keyType;
	
	Information(String name, MysqlDatabase2 mysql, int keyType) {
		this.name = name;
		this.information = new DBRecord2();
		this.mysql = mysql;
		this.keyType = keyType;
	}
	
	protected void loadInformation(DBRecord2 information){
		this.information = information;
		switch(keyType){
			case COLUMN_KEY:
				Logger.log("load column {} information success", name);
				break;
			case TABLE_KEY:
				Logger.log("load table {} information success", name);
				break;
			case SCHEMA_KEY:
				Logger.log("load schema {} information success", name);
				break;
			case INDEX_KEY:
				Logger.log("load index {} information success", name);
				break;
			case TABLE_INDEX_KEY:
				Logger.log("load table index {} information success", name);
				break;
		}
	}
	
	public boolean equals(Information target){
		if(target == null){
			return false;
		}
		for(String e : KEY_SET2[keyType]){
			if(!information.getString(e).equals(target.getInformation().getString(e))){
				return false;
			}			
		}
		return true;
	}
	
	@Override
	public String toString(){
		return  name + ':' + information.toString();
	}
	
	public int getInformationSize(){
		return information.size();
	}
	
	public DBRecord2 getInformation(){
		return information;
	}
	
	public abstract DDL compare(Information target) throws Exception;
	
	protected abstract DDL createDDL(Information target) throws Exception;
	
	protected abstract DDL createDDL() throws Exception;
	
	public abstract DDL createDropDDL() throws Exception;
}
