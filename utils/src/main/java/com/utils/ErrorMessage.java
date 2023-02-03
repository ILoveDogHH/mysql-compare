package com.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author cc
 *
 */
public class ErrorMessage {
	/**
	 * 获取error的message
	 * 
	 * @param e
	 * @return
	 */
	public static String getErrorMessage(Throwable e) {
		Throwable error = e.getCause();
		if (error == null) {
			error = e;
		}
		String msg = error.getMessage();
		if (msg == null) {
			msg = error.toString();
		}
		return msg;
	}

	/**
     * 完整的堆栈信息
     *
     * @param e Exception
     * @return Full StackTrace
     */
    public static String getErrorStackTrace(Throwable e) {
        try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();
            return sw.toString();
        } catch (IOException e1) {
        }
        return e.getMessage();
    }

	/**
	 * 获取error的详细信息(message+stacestack)
	 * 
	 * @param e
	 * @return
	 */
	public static String getErrorDetail(Throwable e) {
		String msg = getErrorMessage(e) + "\n" + getErrorStackTrace(e);
		return msg;
	}
}
