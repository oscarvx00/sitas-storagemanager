package nodeStorage;

import dtos.StorageNode;
import dtos.StorageNodeType;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class NodeMinioIntegrationTest {

    private static MinioClient minioClient;
    private static NodeStorage minioStorage;
    private static String testBucketName;


    @BeforeAll
    public static void setUp(){
        StorageNode minioTestStorageNode = new StorageNode(
                "MINIO_NODE_TEST",
                true,
                System.getenv("MINIO_NODE_BUCKET"),
                StorageNodeType.MINIO,
                System.getenv("MINIO_NODE_ENDPOINT"),
                System.getenv("MINIO_NODE_USER"),
                System.getenv("MINIO_NODE_PASS")
        );
        minioStorage = new NodeMinio(minioTestStorageNode);

        minioClient = MinioClient.builder()
                .endpoint(System.getenv("MINIO_NODE_ENDPOINT"))
                .credentials(System.getenv("MINIO_NODE_USER"), System.getenv("MINIO_NODE_PASS"))
                .build();
        testBucketName = System.getenv("MINIO_NODE_BUCKET");
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
    public void storeFileTest()  throws Exception {
        //Create file to store
        Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("testFile.mp3"), "utf-8"
        ));
        writer.write("TEST_STRING");
        //file size
        long fileBytes = Files.size(Path.of("testFile.mp3"));
        //Read file as InputStream
        InputStream fileReaded = new FileInputStream("testFile.mp3");

        //Call file upload
        minioStorage.storeFile(fileReaded, "testFile", fileBytes);

        //Assert file uploaded, obtain object stats
        StatObjectResponse stat = minioClient.statObject(
                StatObjectArgs.builder().bucket(testBucketName).object("testFile.mp3").build());

        Assertions.assertNotNull(stat);
    }

    @Test
    public void getNodeName(){
        Assertions.assertEquals(minioStorage.getStorageNodeName(), "MINIO_NODE_TEST");
    }

}
