
package com.ecober.domain.service;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeocodingService
{
    @Value("${gmap.api.key}")
    private String gmapKey;

    @Autowired
    RestTemplate restTemplate;
    public double[] getLatAndLong(String location)
    {
        String encodedAddress=URLEncoder.encode(location,StandardCharsets.UTF_8);
        String url=String.format("\"https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=%s\"",encodedAddress,gmapKey);
        ResponseEntity<String>response=restTemplate.getForEntity(url, String.class);
        JSONObject json=new JSONObject(response.getBody());
        JSONObject jsonLocation = json.getJSONArray("results")
                                .getJSONObject(0)
                                .getJSONObject("geometry")
                                .getJSONObject("location");
        double lat = jsonLocation.getDouble("lat");
        double lng = jsonLocation.getDouble("lng");

        return new double[]{lat, lng};


    }
}
