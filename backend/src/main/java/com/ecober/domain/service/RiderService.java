package com.ecober.domain.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecober.adapter.Dto.TripDTO;
import com.ecober.adapter.mapper.TripMapper;
import com.ecober.domain.model.Trip;
import com.ecober.infrastructure.repository.TripRepository;

@Service
public class RiderService {

    @Autowired
    TripRepository tripRepository;

    @Autowired
    TripMapper tripMapper;

    public List<TripDTO> fetchAllTrips(String riderID)
    {
        List<Trip>results=tripRepository.findByUserId(UUID.fromString(riderID));
        List<TripDTO>trips=tripMapper.toDtoList(results);
        return trips;
        
    }
}    
