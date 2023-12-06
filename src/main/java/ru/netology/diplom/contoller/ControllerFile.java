package ru.netology.diplom.contoller;

import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.diplom.dto.FileResponse;
import ru.netology.diplom.model.RenameFile;
import ru.netology.diplom.service.ServiceCloud;

import java.util.List;

@RestController
@AllArgsConstructor
public class ControllerFile {

    private ServiceCloud cloud;

    @PostMapping("/file")
    public ResponseEntity<?> inputFile(@RequestHeader("auth-token") String token, @RequestParam("filename") String name,
                                       MultipartFile file) {
        cloud.fileUpload(token, name, file);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/list")
    public List<FileResponse> list(@RequestHeader("auth-token") String token, @RequestParam("limit") Integer limit) {
        return cloud.allFiles(token, limit).stream()
                .map(a -> new FileResponse(a.getFileName(), a.getSize())).toList();
    }

    @GetMapping("/file")
    public ResponseEntity<Resource> fileDownload(@RequestHeader("auth-token") String token,
                                                 @RequestParam("filename") String fileName) {
        byte[] file = cloud.fileDownload(fileName,token);
        return ResponseEntity.ok(new ByteArrayResource(file));
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(@RequestHeader("auth-token") String token,
                                        @RequestParam("filename") String fileName) {
        cloud.fileDelete(fileName, token);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping("/file")
    @ResponseBody
    public ResponseEntity<?> renameFile(@RequestHeader("auth-token") String token,
                                        @RequestParam("filename") String fileName,
                                        @RequestBody RenameFile file) {
        cloud.renameFile(fileName, file, token);
        return ResponseEntity.ok(HttpStatus.OK);
    }

}
