package mysql_comparer;

import com.dao.base.DaoException;

import java.util.ArrayList;
import java.util.List;

public class TableIndex extends Construct{

	public final String schemaName;
	
	TableIndex(String schemaName, String name, MysqlDatabase2 mysql, List<DBRecord2> tableIndex)
			throws DaoException {
		super(name, mysql, TABLE_INDEX_KEY);
		this.schemaName = schemaName;
		this.mysql = mysql;
		ArrayList<Information> construct = new ArrayList<>();
		for(int i = 0; i < tableIndex.size(); i++){
			construct.add(new Index(this.schemaName, name, tableIndex.get(i).getString("INDEX_NAME"), this.mysql, tableIndex.get(i)));
		}
		loadConstruct(construct);
	}

	@Override
	public Index getElement(int index) {
		return (Index) construct.get(index);
	}

	@Override
	public Index getElement(String name) {
		Index e;
		for(int i = 0; i < getConstructSize(); i++){
			e = getElement(i);
			if(e.name.equals(name)){
				return e;
			}
		}
		return null;
	}

	@Override
	public String dump(String[] match) throws DaoException {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public boolean equals(Object target){
//		//information construct
//		boolean result = false;
//		if(target != null && target instanceof TableIndex
//				&& getConstructSize() == ((TableIndex) target).getConstructSize()){
//			Index e;
//			result = true;
//			for(int i = 0; i < construct.size() && result; i++){
//				e = (Index) construct.get(i);
//				if(!e.equals(((Table) target).getElement(e.name))){
//					result = false;
//				}
//			}
//		}
//		return result;
//	}
	
	@Override
	public DDL compare(Information target) throws Exception {
		if(target == null || target instanceof TableIndex){
			return createDDL(target);
		}
		else{
			throw new Exception("target object is not instanceof TableIndex");
		}
	}

	public String getPrimaryKeyClause(){
		Index index = getElement("PRIMARY");
		if(index == null){
			return null;
		}
		return '(' + index.getInformation().getString("COLUMNS") + ')';

	}
	
	@Override
	protected DDL createDDL(Information target) throws Exception {
		DDL ddl = new DDL();
		int size = ((TableIndex) target).getConstructSize();
		Index e;
		//create drop clause
		for(int i = 0; i < size; i++){
			e = ((TableIndex) target).getElement(i);
			if(!contains(e.name)){
				ddl.append(e.createDropDDL());
			}
		}		
		for(int i = 0; i < getConstructSize(); i++){
			if(!((TableIndex) target).contains(getElement(i).name)){
				ddl.append(getElement(i).compare(null));
			}
		}
		//检查修改和删除
		String primary = getPrimaryKeyClause();
		if(primary != null){
			if(target == null || !primary.equals(((TableIndex) target).getPrimaryKeyClause())){
				String targetPrimary = ((TableIndex) target).getPrimaryKeyClause();
				if(targetPrimary != null && !targetPrimary.equals("")){
					ddl.append(String.format("ALTER TABLE `%s` DROP PRIMARY KEY;\n", name),
							DDL.CLAUSE_TYPE.MODIFY);
					ddl.append("-- WARING DUPLICATE KEY!!!!\n", DDL.CLAUSE_TYPE.MODIFY);
				}
				ddl.append(String.format("ALTER TABLE `%s` ADD PRIMARY KEY %s;\n", name, primary), DDL.CLAUSE_TYPE.MODIFY);
			}
		}
		else{
			ddl.append(String.format("-- WARING %s EMPTY PRIMARY KEY!!!!\n", name), DDL.CLAUSE_TYPE.MODIFY);
		}
		ddl.append("\n", DDL.CLAUSE_TYPE.MODIFY);
		return ddl;
	}
	
	@Override
	protected DDL createDDL() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DDL createDropDDL() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
