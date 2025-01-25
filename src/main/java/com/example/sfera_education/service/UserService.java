package com.example.sfera_education.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.sfera_education.entity.File;
import com.example.sfera_education.entity.Group;
import com.example.sfera_education.entity.User;
import com.example.sfera_education.entity.enums.CategoryEnum;
import com.example.sfera_education.entity.enums.ERole;
import com.example.sfera_education.payload.ApiResponse;
import com.example.sfera_education.payload.ResponseError;
import com.example.sfera_education.payload.UserDTO;
import com.example.sfera_education.payload.auth.AuthRegister;
import com.example.sfera_education.payload.auth.ResponseLogin;
import com.example.sfera_education.payload.res.ResPageable;
import com.example.sfera_education.repository.FileRepository;
import com.example.sfera_education.repository.GroupRepository;
import com.example.sfera_education.repository.UserRepository;
import com.example.sfera_education.security.JwtProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final FileRepository fileRepository;
    private final JwtProvider jwtProvider;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;


    public ApiResponse updateRole(User admin, Long userId) {
        if (admin.getRole() != ERole.ROLE_ADMIN) {
            return new ApiResponse(ResponseError.YOR_ARE_NOT_AN_ADMIN());
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ApiResponse(ResponseError.NOTFOUND("User"));
        }

        user.setRole(ERole.ROLE_TEACHER);
        user.setGroupId(null);
        userRepository.save(user);

        return new ApiResponse("Success");
    }


    public ApiResponse addUserGroup(Long userId, Integer groupId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ApiResponse(ResponseError.NOTFOUND("User"));
        }

        if (user.getGroupId() != null && user.getGroupId().equals(groupId)) {
            return new ApiResponse(ResponseError.ALREADY_EXIST("Group"));
        }

        Group group = groupRepository.findById(groupId).orElse(null);
        if (group == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Group"));
        }

        for (User groupStudent : group.getStudents()) {
            if (user.getId().equals(groupStudent.getId())) {
                return new ApiResponse(ResponseError.ALREADY_EXIST("User"));
            }
        }


        List<User> students = group.getStudents();
        students.add(user);
        group.setStudents(students);
        groupRepository.save(group);
        user.setGroupId(group.getId());
        user.setRole(ERole.ROLE_STUDENT);
        userRepository.save(user);
        return new ApiResponse("Success");
    }

    public ApiResponse updateUserGroup(Long userId, Integer groupId) {
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return new ApiResponse(ResponseError.NOTFOUND("User"));
        }

        Group group = groupRepository.findById(groupId).orElse(null);

        if (group == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Group"));
        }

        if (user.getGroupId() != null && user.getGroupId().equals(groupId)) {
            return new ApiResponse(ResponseError.ALREADY_EXIST("Group"));
        }
        if (user.getGroupId() != null && user.getRole().equals(ERole.ROLE_STUDENT)) {
            Group userGroup = groupRepository.findById(user.getGroupId()).orElse(null);
            assert userGroup != null;
            List<User> students = userGroup.getStudents();
            students.remove(user);
            group.getDeleteStudents().add(user);
            userGroup.setStudents(students);
            groupRepository.save(group);
            user.setGroupId(groupId);
            userRepository.save(user);
            List<User> students1 = group.getStudents();
            students1.add(user);
            group.setStudents(students1);
            groupRepository.save(group);
            return new ApiResponse("Success");
        }
        return new ApiResponse(ResponseError.DEFAULT_ERROR("Userning groupi mavjud emas"));

    }


    public ApiResponse updateUser(Long id, AuthRegister auth, Long fileId) {
        User newUser = userRepository.findById(id).orElse(null);
        if (newUser == null) {
            return new ApiResponse(ResponseError.NOTFOUND("User"));
        }

        boolean exists = userRepository.existsByPhoneNumberAndIdNot(auth.getPhoneNumber(), newUser.getId());
        if (exists) {
            return new ApiResponse(ResponseError.ALREADY_EXIST("User"));
        }

        File file = null;
        if (fileId != 0) {
            file = fileRepository.findById(fileId).orElse(null);
            if (file == null) {
                return new ApiResponse(ResponseError.NOTFOUND("File"));
            }
        }


        newUser.setFirstName(auth.getFirstName() != null && !auth.getFirstName().isEmpty() ? auth.getFirstName() : newUser.getFirstName());
        newUser.setLastName(auth.getLastName() != null && !auth.getLastName().isEmpty() ? auth.getLastName() : newUser.getLastName());
        newUser.setPhoneNumber(auth.getPhoneNumber() != null && !auth.getPhoneNumber().isEmpty() ? auth.getPhoneNumber() : newUser.getPhoneNumber());


        if (auth.getPassword() != null && !auth.getPassword().isEmpty()) {
            newUser.setPassword(passwordEncoder.encode(auth.getPassword()));
        }

        newUser.setFile(file);
        User save = userRepository.save(newUser);

        String token = jwtProvider.generateToken(save.getPhoneNumber());
        ResponseLogin responseLogin = new ResponseLogin(token, save.getRole().name(), save.getId());


        notificationService.saveNotification(
                newUser,
                "Eslatma!",
                "Ma'lumotlaringiz yangilandi... " +
                        "F.I.Sh: " + newUser.getFirstName() + " " + newUser.getLastName() +
                        "Telefon: " + newUser.getPhoneNumber(),
                0L,
                false
        );

        return new ApiResponse(responseLogin);
    }


    public ApiResponse getMe(Long id) {

        User newUser = userRepository.findById(id).orElse(null);
        if (newUser == null) {
            return new ApiResponse(ResponseError.NOTFOUND("User"));
        }

        UserDTO userDTO = createUserDTO(newUser);

        return new ApiResponse(userDTO);
    }


    public ApiResponse searchUser(User currentUser, String name,
                                  Long teacherId, Integer groupId,
                                  String phoneNumber, CategoryEnum type, int page, int size) {
        User users = userRepository.findById(currentUser.getId()).orElse(null);
        if (users == null) {
            return new ApiResponse(ResponseError.NOTFOUND("User"));
        }

        if (users.getRole() == ERole.ROLE_TEACHER) {
            teacherId = users.getId();
        }

        Page<User> userPage;
        switch (type) {
            case QUIZ:
                userPage = userRepository.searchQuizTypeNameOrPhoneNumber(name, phoneNumber, PageRequest.of(page, size));
                break;
            case EDUCATION:
                userPage = userRepository.searchNameOrTeacherIdOrGroupIdOrPhoneNumber(name, teacherId,
                        groupId, phoneNumber, PageRequest.of(page, size));
                break;
            case ONLINE:
                userPage = userRepository.searchOnlineTypeNameOrPhoneNumber(name, phoneNumber, PageRequest.of(page, size));
                break;
            default:
                return new ApiResponse(ResponseError.DEFAULT_ERROR("Yo'nalish noto'g'ri tanlandi!"));
        }

        if (userPage.getContent().isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Users"));
        }

        List<UserDTO> userDTOList = userPage.getContent().stream()
                .map(this::createUserDTO)
                .collect(Collectors.toList());

        ResPageable resPageable = new ResPageable();
        resPageable.setPage(page);
        resPageable.setSize(size);
        resPageable.setBody(userDTOList);
        resPageable.setTotalPage(userPage.getTotalPages());
        resPageable.setTotalElements(userPage.getTotalElements());

        return new ApiResponse(resPageable);
    }


    public ApiResponse searchUserAdmin(String name, String phoneNumber, int page, int size) {
        Page<User> allByRoleAndEnabledTrue =
                userRepository.searchNameOrPhoneNumber(name, phoneNumber, PageRequest.of(page, size));
        List<UserDTO> userDTOList = new ArrayList<>();
        for (User user : allByRoleAndEnabledTrue.getContent()) {

            userDTOList.add(createUserDTO(user));
        }

        ResPageable resPageable = new ResPageable();
        resPageable.setPage(page);
        resPageable.setSize(size);
        resPageable.setBody(userDTOList);
        resPageable.setTotalPage(allByRoleAndEnabledTrue.getTotalPages());
        resPageable.setTotalElements(allByRoleAndEnabledTrue.getTotalElements());
        return new ApiResponse(resPageable);
    }


    public ApiResponse findAllTeacher() {
        List<User> byRole = userRepository.findByRole(ERole.ROLE_TEACHER);
        if (byRole.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Teacher"));
        }

        List<UserDTO> userDTOList = new ArrayList<>();
        for (User user : byRole) {
            userDTOList.add(createUserDTO(user));
        }
        return new ApiResponse(userDTOList);
    }


    public ApiResponse deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ApiResponse(ResponseError.NOTFOUND("User"));
        }

        if (user.getRole() == ERole.ROLE_ADMIN) {
            return new ApiResponse(ResponseError.DEFAULT_ERROR("Admin o'zini o'chira olmaydi"));
        }
        Group group = groupRepository.findById(user.getGroupId()).orElse(null);
        if (group == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Group"));
        }

        group.getStudents().remove(user);
        group.getDeleteStudents().add(user);
        groupRepository.save(group);
        user.setGroupId(null);
        user.setEnabled(false);
        userRepository.save(user);
        return new ApiResponse("Success");
    }


    private UserDTO createUserDTO(User user) {
        Group group = null;
        if (user.getGroupId() != null) {
            group = groupRepository.findById(user.getGroupId()).orElse(null);
        }
        int countGroup = groupRepository.countAllByTeacherIdAndActiveTrue(user.getId());

        return UserDTO.builder()
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .fileId(user.getFile() != null ? user.getFile().getId() : null)
                .role(user.getRole().name())
                .groupName(group != null ? group.getName() : null)
                .countGroup(countGroup)
                .build();
    }

}
