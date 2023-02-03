package com.utils.ip;

public class IpAddressInfo {
	public String country="";		//国家
	public String country_id="";	//国家id
	
	public String area="";			//地区_area
	public String area_id="";		//地区_area_id
	
	public String region="";		//区域id
	public String region_id="";		//区域id
	
	public String city="";			//区域id
	public String city_id="";		//区域id
	
	public String county="";		//县
	public String county_id="";		//县id
	
	public String isp="";			//服务商
	public String isp_id="";		//服务商id
	
	/**
	 * @param country
	 * @param country_id
	 * @param area
	 * @param area_id
	 * @param region
	 * @param region_id
	 * @param city
	 * @param city_id
	 * @param county
	 * @param county_id
	 * @param isp
	 * @param isp_id
	 */
	public void setInfo(String country, String country_id, String area, String area_id, String region, 
			String region_id, String city, String city_id, String county, String county_id, String isp, String isp_id){
		this.country=country;
		this.country_id=country_id;
		this.area=area;
		this.area_id=area_id;
		this.region=region;
		this.region_id=region_id;
		this.city=city;
		this.city_id=city_id;
		this.county=county;
		this.county_id=county_id;
		this.isp=isp;
		this.isp_id=isp_id;
	}
}
