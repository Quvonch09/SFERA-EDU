package com.example.sfera_education.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.sfera_education.entity.District;
import com.example.sfera_education.entity.Region;
import com.example.sfera_education.payload.ApiResponse;
import com.example.sfera_education.payload.DistrictDTO;
import com.example.sfera_education.payload.ResponseError;
import com.example.sfera_education.repository.DistrictRepository;
import com.example.sfera_education.repository.RegionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DistrictService {

    private final DistrictRepository districtRepository;
    private final RegionRepository regionRepository;

    public ApiResponse saveDistrict(DistrictDTO resDistrict) {
        boolean b = districtRepository.existsByName(resDistrict.getName());
        if (b) {
            return new ApiResponse(ResponseError.ALREADY_EXIST("District"));
        }

        Region region = regionRepository.findById(resDistrict.getRegionId()).orElse(null);
        if (region == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Region"));
        }

        District district = District.builder()
                .name(resDistrict.getName())
                .region(region)
                .build();
        districtRepository.save(district);
        return new ApiResponse("District saved!");
    }


    public ApiResponse getAllDistricts() {
        List<District> districts = districtRepository.findAll();
        if (districts.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("District"));
        }

        List<DistrictDTO> list = districts.stream().map(this::districtDTO).toList();
        return new ApiResponse(list);
    }


    public ApiResponse getDistrictById(Integer id) {
        District district = districtRepository.findById(id).orElse(null);
        if (district == null) {
            return new ApiResponse(ResponseError.NOTFOUND("District"));
        }

        DistrictDTO districtDTO = districtDTO(district);
        return new ApiResponse(districtDTO);
    }


    public ApiResponse updateDistrict(Integer districtId, DistrictDTO resDistrict) {
        District district = districtRepository.findById(districtId).orElse(null);
        if (district == null) {
            return new ApiResponse(ResponseError.NOTFOUND("District"));
        }

        Region region = regionRepository.findById(resDistrict.getRegionId()).orElse(null);
        if (region == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Region"));
        }

        district.setId(districtId);
        district.setName(resDistrict.getName());
        district.setRegion(region);
        districtRepository.save(district);
        return new ApiResponse("District updated!");
    }


    public ApiResponse deleteDistrict(Integer id) {
        District district = districtRepository.findById(id).orElse(null);
        if (district == null) {
            return new ApiResponse(ResponseError.NOTFOUND("District"));
        }

        districtRepository.delete(district);
        return new ApiResponse("District deleted!");
    }


    public DistrictDTO districtDTO(District district) {
        return DistrictDTO.builder()
                .id(district.getId())
                .name(district.getName())
                .regionId(district.getRegion().getId())
                .build();
    }
}
