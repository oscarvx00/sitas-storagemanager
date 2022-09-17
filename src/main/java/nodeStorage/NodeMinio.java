package nodeStorage;

import dtos.StorageNode;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;

import java.io.InputStream;

public class NodeMinio implements NodeStorage{

    private MinioClient client;
    private String bucketName;

    public NodeMinio(StorageNode storageNode){
        this.client = MinioClient.builder()
                .endpoint(storageNode.getEndpoint())
                .credentials(storageNode.getCredUser(), storageNode.getCredPass())
                .build();
        this.bucketName = storageNode.getBucket();
    }

    @Override
    public void storeFile(InputStream file, String fileId) throws Exception{
        client.putObject(
                PutObjectArgs.builder().bucket(bucketName).object(fileId).stream(
                        file, file.available(), -1)
                        .contentType("audio/mpeg3")
                        .build()
        );
    }
}
