package mysql_comparer;

import com.dao.base.DaoException;
import com.dao.database.base.DBInterfaceException;
import com.dao.database.mysql.MysqlDatabase;
import com.dao.database.mysql.MysqlDatabaseConfig;
import com.dao.database.mysql.MysqlDatabaseParameter;
import com.utils.JediCast;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MysqlDatabase2 extends MysqlDatabase{

	public MysqlDatabase2(MysqlDatabaseConfig mysqlConfig) throws ClassNotFoundException {
		super(mysqlConfig);
	}
	
    public DBRecord2 sql_fetch_one2(String sql, Object... msgs) throws DaoException {
		DBRecord2 data = new DBRecord2();
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet res = null;
        try {
            MysqlDatabaseParameter params = formatSqlParams(sql, msgs);
            conn = connPool.borrowObject();
            st = conn.prepareStatement(params.getSql());
            st = formatPrepareStatement(st, params.getMsgs());
            res = st.executeQuery();
            ResultSetMetaData md = res.getMetaData();
            String cols_name;
            if (res.next()) {
            	// 遍历此行的所有列
                for (int i = 0; i < md.getColumnCount(); i++)  {
                	// 取出列名
                    cols_name = md.getColumnLabel(i + 1); 
                    Object val = res.getObject(cols_name);
                    data.put(cols_name, val);
                }
                return data;
            }
		} catch (Exception e) {
            throw new DBInterfaceException("Failed to borrow connection from the pool", e);
        } finally {
			safeClose(conn, st, res);
        }

        return data;
	}
	
    public List<DBRecord2> sql_fetch_rows2(String sql, Object... msgs) throws DaoException{
        List<DBRecord2> ret = new ArrayList<>();
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet res = null;
        try {
            MysqlDatabaseParameter params = formatSqlParams(sql, msgs);
            conn = connPool.borrowObject();
            st = conn.prepareStatement(params.getSql());
            st = formatPrepareStatement(st, params.getMsgs());
            res = st.executeQuery();
            ResultSetMetaData md = res.getMetaData();
            String cols_name;
            while (res.next()) {
            	DBRecord2 data = new DBRecord2();
                // 遍历此行的所有列
                for (int i = 0; i < md.getColumnCount(); i++) {
                	// 取出列名
                    cols_name = md.getColumnLabel(i + 1);
                    Object val = res.getObject(cols_name);
                    data.put(cols_name, val);
                }
                ret.add(data);
            }
        } catch (Exception e) {
            throw new DBInterfaceException("Failed to borrow connection from the pool", e);
        } finally {
			safeClose(conn, st, res);
        }

        return ret;
    }
    
    public String sql_fetch_one_cell_string2(String sql, Object... msgs) throws DaoException {
    	Connection conn = null;
        PreparedStatement st = null;
        ResultSet res = null;
        try {
            MysqlDatabaseParameter params = formatSqlParams(sql, msgs);
            conn = connPool.borrowObject();
            st = conn.prepareStatement(params.getSql());
            st = formatPrepareStatement(st, params.getMsgs());
            res = st.executeQuery();
            if (res.next()) {
                Object val = res.getObject(1);
                if(val == null){
                	return null;
                }
                return JediCast.toString(val);
            }
		} catch (Exception e) {
            throw new DBInterfaceException("Failed to borrow connection from the pool", e);
        } finally {
			safeClose(conn, st, res);
        }
        return null;
    }

	/**
	 * 读取sql文件一行行执行
	 * 
	 * @param sqlFilePath
	 * @return
	 * @throws Exception
	 */
	public void runSqlFile(String sqlFilePath) throws IOException, DBInterfaceException {
		FileInputStream inputStream = null;
		Scanner scanner = null;
		try {
			inputStream = new FileInputStream(sqlFilePath);
			scanner = new Scanner(inputStream, "UTF-8");
			StringBuilder sqlLine = new StringBuilder();
			String line;
			while (scanner.hasNextLine()) {
				line = scanner.nextLine();
				if (line.isEmpty()) {
					continue;
				}
				if (line.startsWith("#")) {
					continue;
				}
				if (line.startsWith("--")) {
					continue;
				}
				sqlLine.append(line);
				if (sqlLine.charAt(sqlLine.length() - 1) == ';') {
					if (sqlLine.length() > 1) {
						try {
							sql_update(sqlLine.toString());
						} catch (DaoException e) {
							Logger.log("run sql error {}", sqlLine.toString());
							return;
						}
					}
					sqlLine = new StringBuilder();
				} else {
					sqlLine.append("\n");
				}
			}
			if (scanner.ioException() != null) {
				throw scanner.ioException();
			}
			if (sqlLine.length() > 0) {
				throw new DBInterfaceException("unhandle sql:" + sqlLine.toString());
			}
		} catch (IOException | DBInterfaceException e) {
			Logger.log("error when read sql file");
			throw e;
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
			if (scanner != null) {
				scanner.close();
			}
		}
	}
}
