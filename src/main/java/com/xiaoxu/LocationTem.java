package com.xiaoxu;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author xx
 * @create 2021/4/20 17:29
 */
public class LocationTem{

    private Map<String, String> province = new HashMap<>(64);

    private Map<String, String> city = new HashMap<>(512);

    private Map<String, String> area = new HashMap<>(1024);

    private static final String CHINA_PROVINCE = "http://www.weather.com.cn/data/city3jdata/china.html";

    private static final String CITY = "http://www.weather.com.cn/data/city3jdata/provshi/";

    private static final String LOCATION_TEM = "http://www.weather.com.cn/data/sk/";

    private static final String SUFFIX = ".html";

    public LocationTem(){
        String str = HttpUtil.get(CHINA_PROVINCE);
        JSONObject obj = JSONUtil.parseObj(str);
        for(Map.Entry<String, Object> entry : obj.entrySet()){
            this.province.put((String) entry.getValue(), entry.getKey());
        }
    }


    public Optional<Integer> getTemperature(String province, String city, String county){
        String proCode = this.province.get(province);
        String cityCode, areaCode;
        if((cityCode = this.city.get(province + city)) == null){
            // 初始化city
        }
        if((areaCode = this.area.get(province + city + county)) == null){
            // 初始化area
        }
        String result = HttpUtil.get(LOCATION_TEM + proCode + cityCode + areaCode + SUFFIX);
        JSONObject obj = JSONUtil.parseObj(result);
        String weatherInfo = obj.getStr("weatherinfo");
        if(weatherInfo == null){
            return Optional.empty();
        }
        JSONObject jsonObject = JSONUtil.parseObj(weatherInfo);
        String temp = jsonObject.getStr("temp");
        return Optional.of(Integer.valueOf(temp));
    }

}
