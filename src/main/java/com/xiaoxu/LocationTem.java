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

    private final Map<String, String> province = new HashMap<>(64);

    private final Map<String, String> city = new HashMap<>(512);

    private final Map<String, String> area = new HashMap<>(1024);

    private static final String CHINA_PROVINCE = "http://www.weather.com.cn/data/city3jdata/china.html";

    private static final String CITY = "http://www.weather.com.cn/data/city3jdata/provshi/";

    private static final String AREA =  "http://www.weather.com.cn/data/city3jdata/station/";

    private static final String LOCATION_TEM = "http://www.weather.com.cn/data/sk/";

    private static final String SUFFIX = ".html";

    private static final Double NOT_EXISTS = -10000.0;

    private static final Double API_TIME_OUT = -11111.0;

    private static final LocationTem instance = new LocationTem();


    public static LocationTem getInstance(){
        return instance;
    }


    private LocationTem(){
        String str = HttpUtil.get(CHINA_PROVINCE);
        JSONObject obj = JSONUtil.parseObj(str);
        for(Map.Entry<String, Object> entry : obj.entrySet()){
            this.province.put((String) entry.getValue(), entry.getKey());
        }
    }


    /**
     *
     * @param province
     * @param city
     * @param county
     * @return -10000.0 省 市 区 名字错误或不存在   -11111.0 调用查询温度api超时
     */
    public Optional<Double> getTemperature(String province, String city, String county){
        String cityCode, areaCode,proCode;
        if((proCode = this.province.get(province)) == null){
            return Optional.of(NOT_EXISTS);
        }
        if((cityCode = this.city.get(province + city)) == null){
            // 如果没有，添加对应code
            initData(this.city, proCode, province, 1);
            if((cityCode = this.city.get(province + city)) == null){
                return Optional.of(NOT_EXISTS);
            }
        }
        if((areaCode = this.area.get(province + city + county)) == null){
            initData(this.area, proCode + cityCode, province + city, 2);
            if((areaCode = this.area.get(province + city + county)) == null){
                return Optional.of(NOT_EXISTS);
            }
        }
        String result = HttpUtil.get(LOCATION_TEM + proCode + cityCode + areaCode + SUFFIX);
        JSONObject obj = JSONUtil.parseObj(result);
        String weatherInfo = obj.getStr("weatherinfo");
        if(weatherInfo == null){
            return Optional.of(API_TIME_OUT);
        }
        JSONObject jsonObject = JSONUtil.parseObj(weatherInfo);
        String temp = jsonObject.getStr("temp");
        return Optional.of(Double.valueOf(temp));
    }

    private void initData(Map<String, String> map, String s, String ar, int i){
        String str = HttpUtil.get(i == 1 ? CITY + s + SUFFIX : AREA + s + SUFFIX);
        JSONObject obj = JSONUtil.parseObj(str);
        for(Map.Entry<String, Object> entry : obj.entrySet()){
            map.put(ar + entry.getValue(), entry.getKey());
        }
    }

}
