package com.example.sfera_education.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.sfera_education.entity.File;
import com.example.sfera_education.entity.Notification;
import com.example.sfera_education.entity.User;
import com.example.sfera_education.payload.ApiResponse;
import com.example.sfera_education.payload.IdList;
import com.example.sfera_education.payload.NotificationDTO;
import com.example.sfera_education.payload.ResponseError;
import com.example.sfera_education.payload.res.ResContactNotification;
import com.example.sfera_education.payload.res.ResNotification;
import com.example.sfera_education.repository.FileRepository;
import com.example.sfera_education.repository.NotificationRepository;
import com.example.sfera_education.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final FileRepository fileRepository;

    public ApiResponse getCountNotification(User user) {
        Integer count = notificationRepository.countAllByUserIdAndReadFalse(user.getId());
        return new ApiResponse(count != null ? count : 0);
    }

    public ApiResponse getNotifications(User user) {

        User user1 = userRepository.findById(user.getId()).orElse(null);
        if (user1 == null) {
            return new ApiResponse(ResponseError.NOTFOUND("User"));
        }

        List<Notification> notifications = notificationRepository.findAllByUserId(user.getId());
        if (notifications.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Notifications list"));
        }

        List<NotificationDTO> notificationDTOS = new ArrayList<>();
        for (Notification notification : notifications) {
            notificationDTOS.add(notificationDTO(notification));
        }

        return new ApiResponse(notificationDTOS);

    }

    public ApiResponse adminSendNotification(ResNotification resNotification, Long fileId) {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("User list"));
        }

        File file = null;

        if (fileId != 0) {
            file = fileRepository.findById(fileId).orElse(null);
            if (file == null) {
                return new ApiResponse(ResponseError.NOTFOUND("File"));
            }
        }
        for (User user : users) {
            saveNotification(
                    user,
                    resNotification.getTitle(),
                    resNotification.getContent(),
                    file != null ? file.getId() : 0,
                    false
            );
        }
        return new ApiResponse("Success");
    }


    public ApiResponse contactNotification(ResContactNotification contact) {
        User admin = userRepository.findById(1L).orElse(null);
        saveNotification(
                admin,
                "Foydalanuvchi: " + contact.getName() + "Raqami: " + contact.getPhone(),
                "Xabar: " + contact.getMessage(),
                0L,
                true
        );

        return new ApiResponse("Success");
    }


    public ApiResponse isReadAllNotification(IdList idList) {
        if (idList.getIds().isEmpty()) {
            return new ApiResponse(ResponseError.DEFAULT_ERROR("List bo'sh bo'lmasligi kerak."));
        }

        List<Notification> notifications = notificationRepository.findAllById(idList.getIds());

        for (Notification notification : notifications) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }

        return new ApiResponse("Success");
    }


    public ApiResponse notification(User user) {

        User admin = userRepository.findById(1L).orElse(null);

        String title = "Bildirishnoma!";
        String content = "( " + user.getFirstname() + " " + user.getLastname() + " ) foydalanuvchi sizdan edu tizimga " +
                "kirish uchun ruxsat so'ramozda . . .";

        Notification notification = saveNotification(admin, title, content, 0L, false);
        notification.setRegistrant(user);
        notificationRepository.save(notification);

        return new ApiResponse("Success");
    }


    public ApiResponse delRegistrant(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return new ApiResponse(ResponseError.NOTFOUND("User"));
        }
        Notification notification = notificationRepository.findByRegistrantId(id);
        notificationRepository.deleteById(notification.getId());

        String title = "Bildirishnoma!";
        String content = "Hurmatli " + user.getFirstname() + " " + user.getLastname() + ". Education platformasiga " +
                "kirish uchun yuborgan o'rovingiz qabul qilinmadi!";


        saveNotification(user, title, content, 0L, false);

        return new ApiResponse("Success");
    }


    public ApiResponse deleteNotification(Long id) {
        Notification notification = notificationRepository.findById(id).orElse(null);
        if (notification == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Notification"));
        }

        notificationRepository.delete(notification);
        return new ApiResponse("Success");
    }


    public ApiResponse getOnlineNotification(User user) {
        user = userRepository.findById(user.getId()).orElse(null);
        if (user == null) {
            return new ApiResponse(ResponseError.NOTFOUND("User"));
        }
        List<Notification> notifications = notificationRepository.findAllByUserId(user.getId());
        if (notifications.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Notifications list"));
        }

        List<NotificationDTO> notificationDTOS = new ArrayList<>();
        int size = notifications.size();
        if (size <= 10) {
            Collections.reverse(notifications);
            for (Notification notification : notifications) {
                notificationDTOS.add(notificationDTO(notification));
            }
        } else {
            List<Notification> sublist = notifications.subList(size - 10, size);
            Collections.reverse(sublist);
            for (Notification notification : sublist) {
                notificationDTOS.add(notificationDTO(notification));
            }
        }
        return new ApiResponse(notificationDTOS);
    }


    public Notification saveNotification(User user, String title, String content, Long fileId, boolean contact) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Tashkent")));
        notification.setUser(user);
        notification.setFile(fileRepository.findById(fileId).orElse(null));
        notification.setContact(contact);

        return notificationRepository.save(notification);
    }


    public NotificationDTO notificationDTO(Notification notification) {
        return new NotificationDTO(
                notification.getId(),
                notification.getTitle(),
                notification.getContent(),
                notification.isRead(),
                notification.getCreatedAt(),
                notification.getUser().getId(),
                notification.getFile() != null ? notification.getFile().getId() : 0
        );
    }
}
