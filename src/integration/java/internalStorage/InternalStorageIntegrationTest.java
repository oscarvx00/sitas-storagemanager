package internalStorage;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class InternalStorageIntegrationTest {

    private static MinioClient minioClient;
    private static InternalStorageManager internalStorage;
    private static String testBucketName;

    private static long testFileSize;

    @BeforeAll
    public static void setUp() throws Exception{
        internalStorage = new MinioInternalStorage(
                System.getenv("MINIO_INTERNAL_ENDPOINT"),
                System.getenv("MINIO_INTERNAL_USER"),
                System.getenv("MINIO_INTERNAL_PASS"),
                System.getenv("MINIO_INTERNAL_BUCKET")
        );

        minioClient = MinioClient.builder()
                .endpoint(System.getenv("MINIO_INTERNAL_ENDPOINT"))
                .credentials(System.getenv("MINIO_INTERNAL_USER"), System.getenv("MINIO_INTERNAL_PASS"))
                .build();

        testBucketName = System.getenv("MINIO_INTERNAL_BUCKET");

        //Insert file into bucket
        Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("testFile.mp3"), "utf-8"
        ));
        writer.write("TEST_STRING");
        //file size
        testFileSize = Files.size(Path.of("testFile.mp3"));

        InputStream fileReaded = new FileInputStream("testFile.mp3");

        //upload to minio
        minioClient.putObject(
                PutObjectArgs.builder().bucket(testBucketName).object("testFile.mp3").stream(
                                fileReaded, testFileSize, -1)
                        .contentType("audio/mpeg3")
                        .build()
        );
    }

    @AfterAll
    public static void resetMinio() throws Exception{
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(testBucketName)
                        .object("testFile.mp3")
                        .build()
        );
    }

    @Test
    public void getFileTest() {
        InputStream file = internalStorage.getFile("testFile");

        Assertions.assertNotNull(file);
    }

    @Test
    public void getFileNotFoundTest() {
        InputStream file = null;

        try{
            file = internalStorage.getFile("invalid");
        } catch (Exception ex){}

        Assertions.assertNull(file);
    }

    @Test
    public void getFileSizeTest(){
        Long fileSize = internalStorage.getFileSize("testFile");
        Assertions.assertEquals(fileSize, testFileSize);
    }

    @Test
    public void getFileSizeNotFound(){
        Long fileSize = internalStorage.getFileSize("invalid");
        Assertions.assertNull(fileSize);
    }

}
