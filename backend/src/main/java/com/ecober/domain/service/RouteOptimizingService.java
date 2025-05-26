package com.ecober.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.ecober.adapter.Dto.DistanceDurationDTO;
import org.json.JSONObject;
@Service
public class RouteOptimizingService {

    @Value("${gmap.api.key}")
    String gmapApiKey;

    @Autowired
    RestTemplate restTemplate;

    public DistanceDurationDTO getDistanceAndETA(double pickupLat,double pickupLong,double dropoffLat,double dropoffLong)
    {
        String url=String.format("https://maps.googleapis.com/maps/api/directions/json?origin=%f,%f&destination=%f,%f&departure_time=now&key=%s",
            pickupLat, pickupLong, dropoffLat, dropoffLong, gmapApiKey);
        ResponseEntity<String>response=restTemplate.getForEntity(url,String.class);
        JSONObject json = new JSONObject(response.getBody());
        int durationSeconds = json
            .getJSONArray("routes").getJSONObject(0)
            .getJSONArray("legs").getJSONObject(0)
            .getJSONObject("duration_in_traffic").getInt("value");

        double distanceInMeters=json
            .getJSONArray("routes").getJSONObject(0)
            .getJSONArray("legs").getJSONObject(0)
            .getJSONObject("distance").getInt("value");

        return DistanceDurationDTO.builder().durationInMins(durationSeconds/60).durationInTrafficSecs(durationSeconds).distanceKm(distanceInMeters/1000).build();
    }
}
