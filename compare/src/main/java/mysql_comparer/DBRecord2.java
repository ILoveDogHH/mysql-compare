package mysql_comparer;

import com.dao.database.base.DBRecord;

public class DBRecord2 extends DBRecord{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public String getString(String column){
		if(column == null){
			return null;
		}
		return super.getString(column);
	}

}
