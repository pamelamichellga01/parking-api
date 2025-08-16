package com.nelumbo.parking.services;

import com.nelumbo.parking.dto.ParkingRequest;
import com.nelumbo.parking.entities.Parking;
import com.nelumbo.parking.entities.User;
import com.nelumbo.parking.enums.Role;
import com.nelumbo.parking.exceptions.ValidationException;
import com.nelumbo.parking.repositories.ParkingRepository;
import com.nelumbo.parking.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParkingService {
    
    private final ParkingRepository parkingRepository;
    private final UserRepository userRepository;
    
    public Parking createParking(ParkingRequest request) {
        
        if (parkingRepository.existsByName(request.getName())) {
            throw new ValidationException("Ya existe un parqueadero con ese nombre");
        }
        
        
        User partner = null;
        if (request.getPartnerId() != null) {
            partner = userRepository.findById(request.getPartnerId())
                    .orElseThrow(() -> new ValidationException("El socio especificado no existe"));
            
            if (partner.getRole() != Role.SOCIO) {
                throw new ValidationException("El usuario especificado debe tener rol SOCIO");
            }
        }
        
        Parking parking = Parking.builder()
                .name(request.getName())
                .capacity(request.getCapacity())
                .hourlyRate(request.getHourlyRate())
                .partner(partner)  
                .build();
        
        return parkingRepository.save(parking);
    }
    
    public Parking getParkingById(Long id) {
        return parkingRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Parqueadero no encontrado"));
    }
    
    public List<Parking> getAllParkings() {
        return parkingRepository.findAll();
    }
    
    public Parking updateParking(Long id, ParkingRequest request) {
        Parking existingParking = getParkingById(id);
        
        
        if (!existingParking.getName().equals(request.getName()) && 
            parkingRepository.existsByName(request.getName())) {
            throw new ValidationException("Ya existe un parqueadero con ese nombre");
        }
        
        
        User partner = userRepository.findById(request.getPartnerId())
                .orElseThrow(() -> new ValidationException("El socio especificado no existe"));
        
        if (partner.getRole() != Role.SOCIO) {
            throw new ValidationException("El usuario especificado debe tener rol SOCIO");
        }
        
        existingParking.setName(request.getName());
        existingParking.setCapacity(request.getCapacity());
        existingParking.setHourlyRate(request.getHourlyRate());
        existingParking.setPartner(partner);
        
        return parkingRepository.save(existingParking);
    }
    
    public void deleteParking(Long id) {
        Parking parking = getParkingById(id);
        parkingRepository.delete(parking);
    }
    
    public List<Parking> getParkingsByPartner(Long partnerId) {
        return parkingRepository.findByPartnerId(partnerId);
    }
    
    public List<Parking> getParkingsByPartnerEmail(String partnerEmail) {
        return parkingRepository.findByPartnerEmail(partnerEmail);
    }
    

    public Parking associatePartnerToParking(Long parkingId, Long partnerId) {
       
        Parking parking = getParkingById(parkingId);
        
        
        User partner = userRepository.findById(partnerId)
                .orElseThrow(() -> new ValidationException("El socio especificado no existe"));
        
        if (partner.getRole() != Role.SOCIO) {
            throw new ValidationException("El usuario especificado debe tener rol SOCIO");
        }
        
        
        parking.setPartner(partner);
        
        return parkingRepository.save(parking);
    }
    
    
    public Parking removePartnerFromParking(Long parkingId) {
        Parking parking = getParkingById(parkingId);
        
        // Desasociar el socio del parqueadero
        parking.setPartner(null);
        
        return parkingRepository.save(parking);
    }
    
    
    public boolean hasPartner(Long parkingId) {
        Parking parking = getParkingById(parkingId);
        return parking.getPartner() != null;
    }
    
    
    public List<Parking> getParkingsWithoutPartner() {
        return parkingRepository.findAll().stream()
                .filter(parking -> parking.getPartner() == null)
                .toList();
    }
}
