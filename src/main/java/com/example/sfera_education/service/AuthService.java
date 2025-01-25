package com.example.sfera_education.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.sfera_education.entity.Group;
import com.example.sfera_education.entity.User;
import com.example.sfera_education.entity.enums.ERole;
import com.example.sfera_education.payload.ApiResponse;
import com.example.sfera_education.payload.ResponseError;
import com.example.sfera_education.payload.auth.AuthLogin;
import com.example.sfera_education.payload.auth.AuthRegister;
import com.example.sfera_education.payload.auth.ResponseLogin;
import com.example.sfera_education.repository.GroupRepository;
import com.example.sfera_education.repository.UserRepository;
import com.example.sfera_education.security.JwtProvider;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final GroupRepository groupRepository;
    private final NotificationService notificationService;


    public ApiResponse login(AuthLogin authLogin) {
        User user = userRepository.findByPhoneNumber(authLogin.getPhoneNumber());
        if (user == null) {
            return new ApiResponse(ResponseError.NOTFOUND("User"));
        }

        if (passwordEncoder.matches(authLogin.getPassword(), user.getPassword())) {
            String token = jwtProvider.generateToken(authLogin.getPhoneNumber());
            ResponseLogin responseLogin = new ResponseLogin(token, user.getRole().name(), user.getId());
            return new ApiResponse(responseLogin);
        }

        return new ApiResponse(ResponseError.PASSWORD_DID_NOT_MATCH());
    }


    public ApiResponse register(AuthRegister auth) {

        User byPhoneNumber = userRepository.findByPhoneNumber(auth.getPhoneNumber());
        if (byPhoneNumber != null) {
            return new ApiResponse(ResponseError.ALREADY_EXIST("Phone number"));
        }

        saveUser(auth, ERole.ROLE_USER);

        return new ApiResponse("Success");
    }


    public ApiResponse adminSaveUser(AuthRegister auth, Integer groupId) {

        User byPhoneNumber = userRepository.findByPhoneNumber(auth.getPhoneNumber());
        if (byPhoneNumber != null) {
            return new ApiResponse(ResponseError.ALREADY_EXIST("Phone number"));
        }

        Group group = groupRepository.findById(groupId).orElse(null);
        if (group == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Group"));
        }

        User user = saveUser(auth, ERole.ROLE_USER);

        List<User> students = group.getStudents();
        students.add(user);
        group.setStudents(students);
        groupRepository.save(group);
        user.setGroupId(group.getId());
        user.setRole(ERole.ROLE_STUDENT);
        userRepository.save(user);

        notificationService.saveNotification(
                user,
                "Hurmatli " + user.getFirstName() + " " + user.getLastName() + "!",
                "Siz " + group.getName() + " guruhiga qo'shildingiz.\n" +
                        "O'qituvchingiz: " + group.getTeacher().getFirstName() + " " +
                        group.getTeacher().getLastName() + "\n" +
                        "Darslar " + group.getStartDate() + " da boshlanadi.",
                0L,
                false
        );

        return new ApiResponse("Success");
    }


    public ApiResponse adminSaveTeacher(AuthRegister auth) {

        User byPhoneNumber = userRepository.findByPhoneNumber(auth.getPhoneNumber());
        if (byPhoneNumber != null) {
            return new ApiResponse(ResponseError.ALREADY_EXIST("Phone number"));
        }

        saveUser(auth, ERole.ROLE_TEACHER);
        return new ApiResponse("Success");
    }


    private User saveUser(AuthRegister auth, ERole role) {
        User user = User.builder()
                .firstName(auth.getFirstName())
                .lastName(auth.getLastName())
                .phoneNumber(auth.getPhoneNumber())
                .password(passwordEncoder.encode(auth.getPassword()))
                .role(role)
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();

        User save = userRepository.save(user);

        notificationService.saveNotification(
                save,
                "Salom " + save.getFirstName() + " " + save.getLastName() + "!",
                "Siz bizning saytimizdan muvoffaqiyatli ro'yxatdan o'tganingiz bilan tabriklaymiz." +
                        "Ma'lumotlaringiz yangilandi... " +
                        "Telefon: " + save.getPhoneNumber(),
                0L,
                false
        );

        return save;
    }
}
