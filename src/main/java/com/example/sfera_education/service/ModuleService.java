package com.example.sfera_education.service;

import com.example.sfera_education.entity.Module;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.sfera_education.entity.*;
import com.example.sfera_education.entity.Category;
import com.example.sfera_education.entity.enums.CategoryEnum;
import com.example.sfera_education.payload.ApiResponse;
import com.example.sfera_education.payload.ModuleDTO;
import com.example.sfera_education.payload.ResponseError;
import com.example.sfera_education.payload.res.ResModule;
import com.example.sfera_education.repository.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final CategoryRepository categoryRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final UserProgressRepository userProgressRepository;

    public ApiResponse saveModule(ResModule moduleDTO) {
        Category category = categoryRepository.findById(moduleDTO.getCategoryId()).orElse(null);
        if (category == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Module"));
        }

        if (category.getCategoryEnum().equals(CategoryEnum.QUIZ)) {
            return new ApiResponse(ResponseError.DEFAULT_ERROR("Category type EDUCATION yoki ONLINE emas"));
        }
        boolean exists = moduleRepository.existsByNameIgnoreCaseAndCategory_Id(moduleDTO.getName(), category.getId());

        if (exists) {
            return new ApiResponse(ResponseError.ALREADY_EXIST("Module"));
        }
        Module module = Module.builder()
                .name(moduleDTO.getName())
                .category(category)
                .build();
        moduleRepository.save(module);
        return new ApiResponse("Module saved");
    }


//    public ApiResponse getAllModules()
//    {
//        List<Module> modules = moduleRepository.findAll();
//        List<ModuleDTO> moduleDTOs = new ArrayList<>();
//        for (Module module : modules) {
//            moduleDTOs.add(moduleDTO(module));
//        }
//        return new ApiResponse(moduleDTOs);
//    }


    public ApiResponse getOneModule(Integer id) {
        Module module = moduleRepository.findById(id).orElse(null);
        if (module == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Module"));
        }
        return new ApiResponse(moduleDTO(module));
    }


    public ApiResponse updateModule(Integer id, ResModule moduleDTO) {
        Module module = moduleRepository.findById(id).orElse(null);
        if (module == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Module"));
        }

        Category category = categoryRepository.findById(moduleDTO.getCategoryId()).orElse(null);
        if (category == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Category"));
        }

        boolean exists = moduleRepository.existsByNameIgnoreCaseAndCategory_Id(moduleDTO.getName(), category.getId());
        if (!exists) {
            module.setId(id);
            module.setName(moduleDTO.getName());
            module.setCategory(category);
            moduleRepository.save(module);
            return new ApiResponse("Module successfully updated");
        }
        return new ApiResponse(ResponseError.ALREADY_EXIST("Module"));
    }


    public ApiResponse deleteModule(Integer id) {
        Module module = moduleRepository.findById(id).orElse(null);
        if (module == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Module"));
        }

        module.setDeleted(true);
        moduleRepository.save(module);
        return new ApiResponse("Module successfully deleted");
    }


    @Transactional
    public ApiResponse getModuleByCategoryId(Integer id, User user) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Category"));
        }
        List<Module> allByCategoryId = moduleRepository.findAllByCategoryIdAndDeletedFalse(category.getId());
        if (allByCategoryId.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Module"));
        }

        List<ModuleDTO> list = allByCategoryId.stream().map(this::moduleDTO).toList();
        userRepository.addCategoryToUser(user.getId(), category.getId());
        return new ApiResponse(list);
    }


    public ApiResponse searchModule(String moduleName, Integer categoryId, String categoryEnum) {
        List<Module> modules = moduleRepository.searchAllByModule(moduleName, categoryId, categoryEnum);
        List<ModuleDTO> list = modules.stream().map(this::moduleDTO).toList();
        return new ApiResponse(list);
    }


    private ModuleDTO moduleDTO(Module module) {
        Integer lessonCount = lessonRepository.countAllByModuleIdAndDeletedFalse(module.getId());
        return ModuleDTO.builder()
                .name(module.getName())
                .moduleId(module.getId())
                .categoryId(module.getCategory().getId())
                .lessonCount(lessonCount)
                .build();
    }
}