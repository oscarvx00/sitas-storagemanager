package storageManager;

import database.DatabaseManager;
import database.MongoManager;
import dtos.DownloadCompleted;
import dtos.StorageNode;
import internalStorage.InternalStorageManager;
import internalStorage.MinioInternalStorage;
import nodeStorage.NodeMinio;
import nodeStorage.NodeStorage;
import queue.QueueConnector;
import queue.RabbitConnector;


import java.util.ArrayList;
import java.util.List;

public class StorageManager implements RabbitConnector.RabbitConnectorCallback {

    private QueueConnector rabbitConnector;
    private DatabaseManager databaseManager;
    private InternalStorageManager internalStorage;
    private List<NodeStorage> nodeStorages = new ArrayList<>();

    public StorageManager() {
    }

    public void init(){
        try{
            databaseManager = new MongoManager(
                    System.getenv("MONGODB_ENDPOINT"),
                    System.getenv("MONGODB_DATABASE")
            );
        } catch (Exception ex){
            System.err.println("Error connecting to database " + ex.getMessage());
        }

        internalStorage = new MinioInternalStorage(
                System.getenv("MINIO_INTERNAL_ENDPOINT"),
                System.getenv("MINIO_INTERNAL_USER"),
                System.getenv("MINIO_INTERNAL_PASS"),
                System.getenv("MINIO_INTERNAL_BUCKET")
        );

        rabbitConnector = new RabbitConnector(
                this,
                System.getenv("RABBITMQ_ENDPOINT"),
                System.getenv("RABBITMQ_USER"), System.getenv("RABBITMQ_PASS"),
                System.getenv("RABBITMQ_VHOST")
        );

        initNodeStorages();

        try{
            rabbitConnector.connect();
            rabbitConnector.consumeDownloadCompleteQueue(
                    System.getenv("RABBITMQ_QUEUE_DOWNLOADCOMPLETED"),
                    System.getenv("RABBITMQ_QUEUE_DOWNLOADCOMPLETED")
            );
        } catch (Exception e){
            System.err.println(e.getMessage());
        }
    }

    private void initNodeStorages(){
        List<StorageNode> storageNodesData = databaseManager.getAllStorageNodes();
        for(StorageNode storageNode : storageNodesData){
            try{
                switch (storageNode.getType()){
                    case MINIO -> nodeStorages.add(new NodeMinio(storageNode));
                    default -> System.err.println("Storage node type " + storageNode.getType().toString() + " not found");
                }
            } catch (Exception ex){
                System.err.println("Error creating nodeStorage " + storageNode.getName());
            }
        }
    }

    @Override
    public void downloadCompletedCallback(DownloadCompleted downloadCompleted) {

    }

    public int dummy(){
        int i = 0;
        return i;
    }
}
