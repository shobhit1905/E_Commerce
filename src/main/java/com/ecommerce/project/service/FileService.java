package com.ecommerce.project.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    public String uploadFile(String path, MultipartFile file) throws IOException {
        // get the file names of the current/original file
        String originalFileName = file.getOriginalFilename();

        // generate a unique filename
        String randomId = UUID.randomUUID().toString() ;
        String newFileName = randomId.concat(originalFileName.substring(originalFileName.lastIndexOf('.'))) ;
        String filePath = path + File.separator + newFileName;

        // check if path exists else create
        File folder = new File(path);
        if(!folder.exists()){
            folder.mkdir();
        }

        // upload to the server
        Files.copy(file.getInputStream() , Paths.get(filePath)) ;

        // returning filename
        return newFileName;
    }
}
