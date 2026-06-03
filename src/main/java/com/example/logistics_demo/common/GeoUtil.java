package com.example.logistics_demo.common;

import com.example.logistics_demo.user.repository.StationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.HashMap;

@Component
public class GeoUtil {
    @Autowired
    private StationRepository stationRepository;

    private static final Logger log = LoggerFactory.getLogger(GeoUtil.class);

    // 省会坐标静态Map
    private static final Map<String, Double[]> PROVINCE_CAPITAL_COORDS = new HashMap<>();
    static {
        PROVINCE_CAPITAL_COORDS.put("北京市", new Double[]{116.405285, 39.904989});
        PROVINCE_CAPITAL_COORDS.put("天津市", new Double[]{117.190182, 39.125596});
        PROVINCE_CAPITAL_COORDS.put("河北省", new Double[]{114.502461, 38.045474});
        PROVINCE_CAPITAL_COORDS.put("山西省", new Double[]{112.549248, 37.857014});
        PROVINCE_CAPITAL_COORDS.put("内蒙古自治区", new Double[]{111.670801, 40.818311});
        PROVINCE_CAPITAL_COORDS.put("辽宁省", new Double[]{123.429096, 41.796767});
        PROVINCE_CAPITAL_COORDS.put("吉林省", new Double[]{125.3245, 43.886841});
        PROVINCE_CAPITAL_COORDS.put("黑龙江省", new Double[]{126.642464, 45.756967});
        PROVINCE_CAPITAL_COORDS.put("上海市", new Double[]{121.472644, 31.231706});
        PROVINCE_CAPITAL_COORDS.put("江苏省", new Double[]{118.767413, 32.041544});
        PROVINCE_CAPITAL_COORDS.put("浙江省", new Double[]{120.153576, 30.287459});
        PROVINCE_CAPITAL_COORDS.put("安徽省", new Double[]{117.283042, 31.86119});
        PROVINCE_CAPITAL_COORDS.put("福建省", new Double[]{119.306239, 26.075302});
        PROVINCE_CAPITAL_COORDS.put("江西省", new Double[]{115.892151, 28.676493});
        PROVINCE_CAPITAL_COORDS.put("山东省", new Double[]{117.000923, 36.675807});
        PROVINCE_CAPITAL_COORDS.put("河南省", new Double[]{113.665412, 34.757975});
        PROVINCE_CAPITAL_COORDS.put("湖北省", new Double[]{114.298572, 30.584355});
        PROVINCE_CAPITAL_COORDS.put("湖南省", new Double[]{112.982279, 28.19409});
        PROVINCE_CAPITAL_COORDS.put("广东省", new Double[]{113.280637, 23.125178});
        PROVINCE_CAPITAL_COORDS.put("广西壮族自治区", new Double[]{108.320004, 22.82402});
        PROVINCE_CAPITAL_COORDS.put("海南省", new Double[]{110.33119, 20.031971});
        PROVINCE_CAPITAL_COORDS.put("重庆市", new Double[]{106.504962, 29.533155});
        PROVINCE_CAPITAL_COORDS.put("四川省", new Double[]{104.065735, 30.659462});
        PROVINCE_CAPITAL_COORDS.put("贵州省", new Double[]{106.713478, 26.578343});
        PROVINCE_CAPITAL_COORDS.put("云南省", new Double[]{102.712251, 25.040609});
        PROVINCE_CAPITAL_COORDS.put("西藏自治区", new Double[]{91.132212, 29.660361});
        PROVINCE_CAPITAL_COORDS.put("陕西省", new Double[]{108.948024, 34.263161});
        PROVINCE_CAPITAL_COORDS.put("甘肃省", new Double[]{103.823557, 36.058039});
        PROVINCE_CAPITAL_COORDS.put("青海省", new Double[]{101.778916, 36.623178});
        PROVINCE_CAPITAL_COORDS.put("宁夏回族自治区", new Double[]{106.278179, 38.46637});
        PROVINCE_CAPITAL_COORDS.put("新疆维吾尔自治区", new Double[]{87.617733, 43.792818});
        PROVINCE_CAPITAL_COORDS.put("台湾省", new Double[]{121.509062, 25.044332});
        PROVINCE_CAPITAL_COORDS.put("香港特别行政区", new Double[]{114.173355, 22.320048});
        PROVINCE_CAPITAL_COORDS.put("澳门特别行政区", new Double[]{113.54909, 22.198951});
    }

    /**
     * 根据地名查找经纬度，优先查库，查不到则调用高德API
     * @param province 省
     * @param city 城市
     * @param district 省市区的第三级，优先使用
     * @param fullAddress 完整的地址
     */
    public Double[] getLngLatByLocation(String province, String city, String district, String fullAddress) {
        // 优先用区/县
        if (district != null && !district.isEmpty()) {
            Double[] result = callAmapAPI(city, district);
            if (result != null) return result;
        }
        // 其次用市
        if (city != null && !city.isEmpty()) {
            Double[] result = callAmapAPI(null, city);
            if (result != null) return result;
        }
        // 再次用省
        if (province != null && !province.isEmpty()) {
            Double[] result = callAmapAPI(null, province);
            if (result != null) return result;
        }
        // 最后用省会兜底
        if (province != null && PROVINCE_CAPITAL_COORDS.containsKey(province)) {
            return PROVINCE_CAPITAL_COORDS.get(province);
        }
        // 全国兜底
        return new Double[]{116.397499, 39.908722}; // 北京天安门
    }

    /**
     * 调用高德地图API进行地理编码
     */
    private Double[] callAmapAPI(String city, String address) {
        try {
            String key = "014a4531071697d5ef926d69b070be26";
            String url = "https://restapi.amap.com/v3/geocode/geo?address=" + java.net.URLEncoder.encode(address, "UTF-8");
            if (city != null && !city.isEmpty()) {
                url += "&city=" + java.net.URLEncoder.encode(city, "UTF-8");
            }
            url += "&key=" + key;
            log.info("Querying Amap API with URL: {}", url);

            RestTemplate restTemplate = new RestTemplate();
            String body = restTemplate.getForObject(url, String.class);
            log.info("Amap API response for location '{}': {}", address, body);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(body);

            // 检查API返回状态
            if (!json.get("status").asText().equals("1")) {
                log.error("Amap API returned an error for location '{}'. Response: {}", address, body);
                return null;
            }

            JsonNode geocodes = json.get("geocodes");
            if (geocodes != null && geocodes.isArray() && geocodes.size() > 0) {
                String locationStr = geocodes.get(0).get("location").asText();
                if (locationStr != null && !locationStr.isEmpty()){
                    String[] arr = locationStr.split(",");
                    if (arr.length == 2) {
                        Double lng = Double.valueOf(arr[0]);
                        Double lat = Double.valueOf(arr[1]);
                        return new Double[]{lng, lat};
                    }
                }
            }
            log.warn("No geocodes found for location '{}' in Amap response.", address);
        } catch (Exception e) {
            log.error("Exception occurred while geocoding location: {}", address, e);
        }
        return null;
    }

    /**
     * 清理地址中的非标准行政区划名称
     * @param address 原始地址
     * @return 清理后的地址
     */
    private String cleanAddress(String address) {
        if (address == null) return null;
        
        // 移除"市辖区"等非标准行政区划名称
        address = address.replace("市辖区", "");
        address = address.replace("县辖区", "");
        address = address.replace("区辖区", "");
        
        // 移除多余的空格
        address = address.replaceAll("\\s+", "");
        
        // 如果地址以"市"结尾且前面是"市"，则移除一个"市"
        address = address.replaceAll("市市", "市");
        
        return address.trim();
    }
} 