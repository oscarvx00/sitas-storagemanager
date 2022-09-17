package internalStorage;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;

import java.io.InputStream;

public class MinioInternalStorage implements InternalStorageManager{

    private MinioClient client;
    private String bucketName;

    public MinioInternalStorage(String endpoint, String credUser, String credPass, String bucketName){
        this.client = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(credUser, credPass)
                .build();
        this.bucketName = bucketName;
    }

    @Override
    public InputStream getFile(String fileId){
        try {
            InputStream inputStream = client.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileId+".mp3")
                            .build()
            );
            return inputStream;
        } catch (Exception ex){
            System.err.println("Error getting file from internal storage " + ex.getMessage());
            return null;
        }
    }

    @Override
    public Long getFileSize(String fileId){
        try {
            StatObjectResponse stat = client.statObject(
                    StatObjectArgs.builder().bucket(bucketName).object(fileId+".mp3").build()
            );
            return stat.size();
        } catch (Exception ex) {
            System.err.println("Error getting object stat " + fileId);
            return null;
        }
    }
}
