package mysql_comparer;

public class DDL{
	
	private StringBuilder createClause = null;
	private StringBuilder modifyClause = null;
	private StringBuilder dropClause = null;
	
	private CLAUSE_TYPE defaultType = null;
	
	public static enum CLAUSE_TYPE{
		CREATE,
		MODIFY,
		DROP
	};
	
	DDL(){
		createClause = new StringBuilder();
		modifyClause = new StringBuilder();
		dropClause = new StringBuilder();
	}
	
	DDL(StringBuilder stringBuilder, CLAUSE_TYPE type){
		this();
		append(stringBuilder.toString(), type);
	}
	
	DDL(String string, CLAUSE_TYPE type){
		this();
		append(string, type);
	}

	DDL(CLAUSE_TYPE type){
		this();
		setDefaultClauseType(type);
	}
	
	public void setDefaultClauseType(CLAUSE_TYPE type){
		defaultType = type;
	}
	
	public void restoreDefaultClauseType(){
		defaultType = null;
	}
	
	private void checkDefaultClauseType() throws Exception{
		if(defaultType == null){
			throw new Exception("undefine default type");
		}
	}
	
	public DDL clean(){
		createClause.delete(0, createClause.length());
		modifyClause.delete(0, modifyClause.length());
		dropClause.delete(0, dropClause.length());
		return this;
	}
	
	public DDL append(DDL ddl){
		if(ddl == null){
			throw new NullPointerException();
		}
		this.createClause.append(ddl.createClause);
		this.modifyClause.append(ddl.modifyClause);
		this.dropClause.append(ddl.dropClause);
		return this;
	}
	
	public DDL append(String clause, CLAUSE_TYPE type){
		switch(type){
		case CREATE:
			createClause.append(clause);
			break;
		case MODIFY:
			modifyClause.append(clause);
			break;
		case DROP:
			dropClause.append(clause);
			break;
		}
		return this;
	}
	
	public DDL append(String clause) throws Exception{
		checkDefaultClauseType();
		append(clause, defaultType);
		return this;
	}
	
	@Override
	public String toString(){
		return createClause.toString() + modifyClause.toString() + dropClause.toString();
	}
}
