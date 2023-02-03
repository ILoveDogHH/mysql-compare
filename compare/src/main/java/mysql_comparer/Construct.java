package mysql_comparer;

import com.dao.base.DaoException;

import java.util.ArrayList;
import java.util.List;

public abstract class Construct extends Information {

	protected List<Information> construct;
	
	Construct(String name, MysqlDatabase2 mysql, int keyType) {
		super(name, mysql, keyType);
		this.construct = new ArrayList<>();
	}
	
	protected void loadConstruct(ArrayList<Information> construct){
		this.construct = construct;
		switch(keyType){
			case TABLE_KEY:
				Logger.log("load table {} construct success, size: {}", name, getConstructSize());
				break;
			case SCHEMA_KEY:
				Logger.log("load schema {} construct success, size: {}", name, getConstructSize());
				break;
			case TABLE_INDEX_KEY:
				Logger.log("load table index {} construct success, size: {}", name, getConstructSize());
				break;
		}
	}
	
	public boolean equals(Construct target){
		//information construct	
		if(target == null){
			return false;
		}
		if(!super.equals(target)){
			return false;
		}
		boolean result = false;
		if(target != null && construct.size() == target.getConstructSize()){
			result = true;
			for(Information e : construct){
				if(!e.equals(target.getElement(e.name))){
					result = false;
					break;
				}				
			}
		}
		return result;
	}
	
	@Override
	public String toString(){
		return super.toString() + '\n' + construct.toString();
	}
	
	protected int getConstructSize(){
		return construct.size();
	}
	
	protected boolean contains(String name){
		for(Information e : construct){
			if(e.name.equals(name)){
				return true;
			}
		}
		return false;
	}
	
	public abstract Information getElement(int index);
	
	public abstract Information getElement(String name);
	
	public abstract String dump(String[] match) throws DaoException;
}
