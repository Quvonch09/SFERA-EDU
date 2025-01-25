package com.example.sfera_education.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.sfera_education.entity.File;
import com.example.sfera_education.exception.NotFoundException;
import com.example.sfera_education.payload.ApiResponse;
import com.example.sfera_education.payload.ResponseError;
import com.example.sfera_education.payload.res.ResFile;
import com.example.sfera_education.repository.FileRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository videoFileRepository;

    //local uchun
//    private static final Path root= Paths.get("src/main/resources");
    //server uchun
    private static final Path root = Paths.get("/root");


    public ApiResponse saveFile(MultipartFile file) {
        String director = checkingAttachmentType(file);
        if (director == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Fayl yuklash uchun papka"));
        }

        long millis = System.currentTimeMillis();
        Path resolve = root.resolve(director + "/" + millis + "-" + file.getOriginalFilename());
        File files;
        try {
            Files.copy(file.getInputStream(), resolve, StandardCopyOption.REPLACE_EXISTING);
            File videoFile = new File();
            videoFile.setFileName(file.getOriginalFilename());
            videoFile.setFilepath(root.resolve(director + "/" + millis + "-" + file.getOriginalFilename()).toString());
            videoFile.setContentType(file.getContentType());
            videoFile.setSize(file.getSize());
            files = videoFileRepository.save(videoFile);
        } catch (IOException e) {
            throw new NotFoundException(new ApiResponse(ResponseError.NOTFOUND(e.getMessage())));
        }
        return new ApiResponse(files.getId());
    }


    //  GetFile uchun
    public ResFile loadFileAsResource(Long id) {
        try {
            Optional<File> videoFileOptional = videoFileRepository.findById(id);
            if (videoFileOptional.isPresent()) {
                File videoFile = videoFileOptional.get();
                java.io.File file = new java.io.File(videoFile.getFilepath());
                Resource resource = new UrlResource(file.toURI());
                ResFile resFile = new ResFile();
                resFile.setFillName(videoFile.getFileName());
                resFile.setResource(resource);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType(videoFile.getContentType()));
                headers.setContentLength(videoFile.getSize());

                resFile.setHeaders(headers);

                return resFile;
            }
            return null;
        } catch (IOException e) {
            e.fillInStackTrace();
            throw new NotFoundException(new ApiResponse(ResponseError.DEFAULT_ERROR(e.getMessage())));
        }
    }


    //    update
    public ApiResponse updateFile(Long id, MultipartFile file) {
        try {
            Optional<File> existingVideoFile = videoFileRepository.findById(id);
            if (existingVideoFile.isPresent()) {
                File videoFile = existingVideoFile.get();
                Path oldFilePath = Paths.get(videoFile.getFilepath());
                Files.deleteIfExists(oldFilePath);

                String filename = file.getOriginalFilename();
                String director = checkingAttachmentType(file);
                if (director == null) {
                    return new ApiResponse(ResponseError.NOTFOUND("Fayl yuklash uchun papka"));
                }

                long millis = System.currentTimeMillis();
                Path uploadPath = root.resolve(director + "/" + millis + "-" + file.getOriginalFilename());
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                if (filename != null) {
                    Path filePath = uploadPath.resolve(filename);
                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                    videoFile.setFileName(filename);
                    videoFile.setFilepath(filePath.toString());
                    videoFile.setContentType(file.getContentType());

                    File file1;
                    file1 = videoFileRepository.save(videoFile);
                    return new ApiResponse(file1.getId());
                } else {
                    return new ApiResponse(ResponseError.NOTFOUND("File name"));
                }
            } else {
                throw new NotFoundException(new ApiResponse(ResponseError.NOTFOUND("File")));
            }
        } catch (IOException e) {
            throw new NotFoundException(new ApiResponse(ResponseError.DEFAULT_ERROR("Fileni o'qishda xatolik")));
        }
    }


    //delete file
    public ApiResponse deleteFile(Long id) {
        try {
            Optional<File> existingVideoFile = videoFileRepository.findById(id);
            if (existingVideoFile.isPresent()) {
                File videoFile = existingVideoFile.get();
                Path filePath = Paths.get(videoFile.getFilepath());
                Files.deleteIfExists(filePath);
                videoFileRepository.delete(videoFile);
                return new ApiResponse("Successfully deleted");
            } else {
                throw new IOException("File not found");
            }
        } catch (IOException e) {
            throw new NotFoundException(new ApiResponse(ResponseError.DEFAULT_ERROR("Fileni o'qishda xatolik")));
        }
    }


    public String checkingAttachmentType(MultipartFile file) {
        String filename = file.getOriginalFilename();

        assert filename != null;
        if (filename.endsWith(".png") || filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(".webp")
                || filename.endsWith(".PNG") || filename.endsWith(".JPG") || filename.endsWith(".JPEG") || filename.endsWith(".WEBP")) {
            return "img";
        } else if (checkFile(filename)) {
            return "files";
        }
        return null;
    }


    public boolean checkFile(String filename) {
        return filename.endsWith(".pdf") || filename.endsWith(".docx") ||
                filename.endsWith(".pptx") || filename.endsWith(".zip") ||
                filename.endsWith(".PDF") || filename.endsWith(".DOCX") ||
                filename.endsWith(".PPTX") || filename.endsWith(".ZIP");
    }
}