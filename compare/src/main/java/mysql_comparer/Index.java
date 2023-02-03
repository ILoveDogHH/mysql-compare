package mysql_comparer;

import com.dao.base.DaoException;

public class Index extends Information{

	public final String schemaName;
	public final String tableName;
	
	Index(String schemaName, String tableName, String name, MysqlDatabase2 mysql, DBRecord2 index)
			throws DaoException {
		super(name, mysql, INDEX_KEY);
		this.schemaName = schemaName;
		this.tableName = tableName;
		this.mysql = mysql;
		loadInformation(index);
	}
	
	@Override
	public boolean equals(Object target){
		if(target != null && target instanceof Index){
			DBRecord2 targetInformation = ((Index) target).getInformation();
			return information.getString("COLUMNS")
						.equals(targetInformation.getString("COLUMNS"))
					&& information.getString("INDEX_TYPE")
						.equals(targetInformation.getString("INDEX_TYPE"));
		}
		return false;
	}

	@Override
	public DDL compare(Information target) throws Exception {
		if(target == null || target instanceof Index){
			return createDDL(target);
		}
		else{
			throw new Exception("target object is not instanceof Index");
		}
	}

	@Override
	protected DDL createDDL(Information target) throws Exception {
		if(target == null){
			return createDDL();
		}
		return createDropDDL().append(createDDL());
	}

	@Override
	protected DDL createDDL() throws Exception {
		//CREATE INDEX `mtype2mlv` ON `cfg_military`(`mtype`, `mlv`) USING BTREE ;
		if(name.equals("PRIMARY")){
			return new DDL();
		}
		return new DDL(String.format("CREATE INDEX `%s` ON `%s`(%s) USING %s;\n",
					name, tableName, information.getString("COLUMNS"), information.getString("INDEX_TYPE")),
					DDL.CLAUSE_TYPE.MODIFY);
	}

	@Override
	public DDL createDropDDL() throws Exception {
		return new DDL(String.format("DROP INDEX `%s` ON `%s`;\n", name, tableName),
				DDL.CLAUSE_TYPE.MODIFY);
	}

}
