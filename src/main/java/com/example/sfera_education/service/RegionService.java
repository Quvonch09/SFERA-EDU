package com.example.sfera_education.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.sfera_education.entity.Region;
import com.example.sfera_education.payload.ApiResponse;
import com.example.sfera_education.payload.RegionDTO;
import com.example.sfera_education.payload.ResponseError;
import com.example.sfera_education.repository.RegionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegionService {

    private final RegionRepository regionRepository;

    public ApiResponse saveRegion(RegionDTO resRegion) {
        boolean exist = regionRepository.existsByName(resRegion.getName());
        if (!exist) {
            Region region = Region.builder()
                    .name(resRegion.getName())
                    .build();
            regionRepository.save(region);
            return new ApiResponse("Successfully saved region");
        }
        return new ApiResponse(ResponseError.ALREADY_EXIST("Region"));
    }


    public ApiResponse getAllRegion() {
        List<Region> all = regionRepository.findAll();
        if (all.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Region"));
        }

        List<RegionDTO> list = all.stream().map(this::regionDTO).toList();
        return new ApiResponse(list);
    }


    public ApiResponse getOneRegion(Integer regionId) {
        Region region = regionRepository.findById(regionId).orElse(null);
        if (region == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Region"));
        }
        return new ApiResponse(regionDTO(region));
    }


    public ApiResponse updateRegion(Integer regionId, RegionDTO resRegion) {
        Region region = regionRepository.findById(regionId).orElse(null);
        if (region == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Region"));
        }

        region.setName(resRegion.getName());
        regionRepository.save(region);
        return new ApiResponse("Successfully updated region");
    }


    public ApiResponse deleteRegion(Integer regionId) {
        Region region = regionRepository.findById(regionId).orElse(null);
        if (region == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Region"));
        }

        regionRepository.delete(region);
        return new ApiResponse("Successfully deleted region");
    }

    private RegionDTO regionDTO(Region region) {
        return RegionDTO.builder()
                .id(region.getId())
                .name(region.getName())
                .build();
    }
}
