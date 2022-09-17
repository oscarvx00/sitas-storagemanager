package nodeStorage;

import dtos.StorageNode;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;

import java.io.InputStream;

public class NodeMinio extends NodeStorage{

    private MinioClient client;
    private String bucketName;

    public NodeMinio(StorageNode storageNode){
        super(storageNode);
        this.client = MinioClient.builder()
                .endpoint(storageNode.getEndpoint())
                .credentials(storageNode.getCredUser(), storageNode.getCredPass())
                .build();
        this.bucketName = storageNode.getBucket();
    }

    @Override
    public void storeFile(InputStream file, String fileId, Long fileSize) throws Exception{
        client.putObject(
                PutObjectArgs.builder().bucket(bucketName).object(fileId+".mp3").stream(
                        file, fileSize, -1)
                        .contentType("audio/mpeg3")
                        .build()
        );
    }
}
