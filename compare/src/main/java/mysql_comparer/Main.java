package mysql_comparer;

import com.dao.base.DaoException;
import com.logger.JLogger;
import com.utils.ErrorMessage;
import com.utils.config.ConfigReader;
import com.utils.config.ConfigReader.ConfigFileType;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;

public class Main {
	public static ConfigReader config;

	public static void main(String[] args) throws ClassNotFoundException, DaoException, IOException {
		JLogger.create(null, "ALL");
		Logger.log(">>>>>>>>>>>>>>>mysql-comparer start<<<<<<<<<<<<<<<<");
		config = new ConfigReader(System.getProperty("user.dir") + File.separator + "config.properties",
				ConfigFileType.properties);
		Comparer comparer = new Comparer();
		comparer.dataMatches = config.getString("data_export").split("[,]");
		comparer.tableMatches = config.getString("table_export").split("[,]");
		comparer.exportFilePath = config.getString("export_path");
		comparer.parseArgs(args);
		try {
			comparer.handle();
		} catch (Exception e) {
			JLogger.error("handle error, args=[" + StringUtils.join(args, ",") + "]", e);
			Logger.log("[ERROR]" + ErrorMessage.getErrorDetail(e));
			System.exit(-1);
		} finally {
			Logger.log("<<<<<<<<<<<<<<<<mysql-comparer finish>>>>>>>>>>>>>>>");
		}
    }
}
