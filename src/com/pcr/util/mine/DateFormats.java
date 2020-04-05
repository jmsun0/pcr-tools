package com.pcr.util.mine;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public enum DateFormats {
	/** HH:mm */
	HHmm("HH:mm"), //
	/** HH:mm:ss */
	HHmmss("HH:mm:ss"), //
	/** yyyy/MM/dd HH:mm:ss */
	yyyyMMddHHmmss("yyyy/MM/dd HH:mm:ss"), //
	/** yyyy-MM-dd HH:mm:ss */
	yyyy_MM_ddHHmmss("yyyy-MM-dd HH:mm:ss"), // 常用
	/** yyyy_MM_dd_HH_mm_ss */
	fyyyy_MM_dd_HH_mm_ss("yyyy_MM_dd_HH_mm_ss"), //
	/** yyyy-MM-dd */
	yyyy_MM_dd("yyyy-MM-dd"), //
    yyyyMMdd("yyyyMMdd"), //
	/** yyyy年MM月dd日 */
	yyyyMMddC("yyyy年MM月dd日"), //
	/** yyyy-MM-dd HH:mm:ss.SSS */
	yyyy_MM_ddHHmmssSSS("yyyy-MM-dd HH:mm:ss.SSS"), //
	/** yyyy-MM-dd'T'HH:mm:ss.SSSXXX */
	yyyy_MM_ddTHHmmssSSSXXX("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"), //
	yyyy_MM_ddTHHmmssSSSSSSSSXXX("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX"), //
	/** yyyy-MM-dd'T'HH:mm:ssXXX 截掉秒后面的字符 */
	yyyy_MM_ddTHHmmssXXX("yyyy-MM-dd'T'HH:mm:" + "ssXXX"), //
	;

	private ThreadLocal<SimpleDateFormat> sdf = new ThreadLocal<>();
	private String pattern;

	private DateFormats(String pattern) {
		this.pattern = pattern;
	}

	public String getPattern() {
		return pattern;
	}

	public SimpleDateFormat get() {
		SimpleDateFormat fmt = sdf.get();
		if (fmt == null)
			sdf.set(fmt = new SimpleDateFormat(pattern));
		return fmt;
	}

	public String format(Date time, TimeZone zone) {
		if (time == null)
			return "";
		try {
			SimpleDateFormat format = get();
			if (zone != null) {
				TimeZone oldZone = format.getTimeZone();
				format.setTimeZone(zone);
				String result = format.format(time);
				format.setTimeZone(oldZone);
				return result;
			} else
				return format.format(time);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public String format(Date time) {
		return format(time, null);
	}

	public Date parse(String time, TimeZone zone) {
		if (time == null || time.isEmpty())
			return null;
		try {
			SimpleDateFormat format = get();
			if (zone != null) {
				TimeZone oldZone = format.getTimeZone();
				format.setTimeZone(zone);
				Date result = format.parse(time);
				format.setTimeZone(oldZone);
				return result;
			} else
				return format.parse(time);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Date parse(String time) {
		return parse(time, null);
	}
}
