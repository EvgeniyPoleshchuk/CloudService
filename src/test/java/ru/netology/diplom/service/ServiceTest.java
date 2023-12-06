package ru.netology.diplom.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.netology.diplom.dto.FileResponse;
import ru.netology.diplom.model.FileData;
import ru.netology.diplom.model.RenameFile;
import ru.netology.diplom.model.UserData;
import ru.netology.diplom.repository.FileRepository;
import ru.netology.diplom.repository.UserRepository;
import ru.netology.diplom.repository.UserTokenRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ServiceTest {
    @InjectMocks
    private ServiceCloud cloud;
    @Mock
    private UserRepository userRepository;
    @Mock
    private FileRepository fileRepository;
    @Mock
    private UserTokenRepository userTokenRepository;

    private final String BEARER_TOKEN = "Bearer token";

    FileData FILEDATA = FileData.builder()
            .fileName("test1.jpg")
            .size(1000)
            .fileData("test".getBytes())
            .build();
    FileData FILEDATA1 = FileData.builder()
            .fileName("test2.jpg")
            .size(1000)
            .fileData("test".getBytes())
            .build();
    UserData userData = new UserData(1, "2x2", "100");


    @BeforeEach
    void setUp() {
        Mockito.when(fileRepository.findByFileNameAndUserData("test1.jpg", userData)).thenReturn(FILEDATA);
        Mockito.when(userTokenRepository.getUserNameByToken(BEARER_TOKEN.split(" ")[1])).thenReturn("2x2");
        Mockito.when(userRepository.findByEmail("2x2")).thenReturn(userData);
    }

    @Test
    void upload() {
        byte[] test = "accept".getBytes();
        MockMultipartFile file = new MockMultipartFile("testFile", test);
        Mockito.when(fileRepository.findByFileNameAndUserData("testFile", userData)).thenReturn(FILEDATA);
        Mockito.verify(cloud,Mockito.times(1)).fileUpload(BEARER_TOKEN, "testFile", file);
    }

    @Test
    void list() {
        List<FileResponse> list = List.of(new FileResponse("test1.jpg", 1000)
                , new FileResponse("test2.jpg", 1000));

        List<FileData> list1 = List.of(FILEDATA, FILEDATA1);
        Mockito.when(fileRepository.findFileDataByUserData(userData)).thenReturn(list1);

        assertEquals(list, cloud.allFiles(BEARER_TOKEN, 3));
    }

    @Test
    void fileDownload() {
        ByteArrayResource byteArrayResource = new ByteArrayResource(FILEDATA.getFileData());
        Mockito.when(fileRepository.findByFileNameAndUserData("testFile", userData)).thenReturn(FILEDATA);
        assertEquals(byteArrayResource, cloud.fileDownload("testFile", BEARER_TOKEN));
    }

    @Test
    void delete() {
        cloud.fileDelete("test1.jpg", BEARER_TOKEN);
        Mockito.verify(fileRepository, Mockito.times(1)).delete(FILEDATA);
    }
    @Test
    void renameFile(){
        RenameFile renameFile = new RenameFile();
        renameFile.setFilename("testRename");
        cloud.renameFile("test1.jpg",renameFile,BEARER_TOKEN);
        Mockito.verify(fileRepository,Mockito.times(1)).save(FILEDATA);
    }


}