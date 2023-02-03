package com.utils.ip;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.utils.HttpRequest;

import java.io.IOException;

public class Ip implements IpAddressQuery {
	private static final String QUERY_URL="http://ip.taobao.com/service/getIpInfo.php?ip=%s";
	public String ip;
	public IpAddressInfo ipAddrInfo;
	private String _queryUrl;
	private boolean isQuerySuccess =false;
	
	public Ip(String ip){
		this._queryUrl=String.format(QUERY_URL, ip);
		this.ip=ip;
		this.ipAddrInfo=new IpAddressInfo();
	}
	
	@Override
	public IpAddressInfo queryIpAddress() throws IpQueryFailed, IpQueryError {
		if(this.isQuerySuccess){
			return this.ipAddrInfo;
		}
		String jsonResult=null;
		try{
			jsonResult = HttpRequest.httpRequest(1000, "GET", this._queryUrl, "");
		}catch(IOException e){
			throw new IpQueryFailed();
		}
		try {
			JSONObject result = JSON.parseObject(jsonResult);
			Integer code = result.getInteger("code");
			if (code == null || code != 0) {
				throw new IpQueryFailed();
			}
			JSONObject data = result.getJSONObject("data");
			this.ipAddrInfo.setInfo(data.getString("country"),
					data.getString("country_id"),
					data.getString("area"),
					data.getString("area_id"),
					data.getString("region"),
					data.getString("region_id"),
					data.getString("city"),
					data.getString("city_id"),
					data.getString("county"),
					data.getString("county_id"),
					data.getString("isp"),
					data.getString("isp_id"));
			this.isQuerySuccess=true;
			return this.ipAddrInfo;
		} catch (JSONException e) {
			this.isQuerySuccess=false;
			throw new IpQueryError(e.getMessage(),e);
		}
	}
}
