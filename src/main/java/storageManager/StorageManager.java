package storageManager;

import database.DatabaseManager;
import database.MongoManager;
import dtos.DownloadCompleted;
import dtos.SongDownload;
import dtos.StorageNode;
import internalStorage.InternalStorageManager;
import internalStorage.MinioInternalStorage;
import nodeStorage.NodeMinio;
import nodeStorage.NodeStorage;
import queue.QueueConnector;
import queue.QueueConnectorCallback;
import queue.RabbitConnector;


import java.io.InputStream;
import java.util.*;

public class StorageManager implements QueueConnectorCallback {

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
                System.getenv("RABBITMQ_ENDPOINT"),
                System.getenv("RABBITMQ_USER"), System.getenv("RABBITMQ_PASS"),
                System.getenv("RABBITMQ_VHOST")
        );

        initNodeStorages();

        try{
            rabbitConnector.connect();
            rabbitConnector.consumeDownloadCompleteQueue(
                    this,
                    System.getenv("RABBITMQ_QUEUE_DOWNLOADCOMPLETED"),
                    System.getenv("RABBITMQ_EXCHANGE_DOWNLOADCOMPLETED")
            );
        } catch (Exception e){
            System.err.println("Rabbit consume error: " + e.getMessage());
        }
    }

    private void initNodeStorages(){
        List<StorageNode> storageNodesData = databaseManager.getAllStorageNodes();
        storageNodesData.sort(new Comparator<StorageNode>() {
            @Override
            public int compare(StorageNode o1, StorageNode o2) {
                return Boolean.compare(o1.isStable(), o2.isStable());
            }
        });
        for(StorageNode storageNode : storageNodesData){
            try{
                switch (storageNode.getType()){
                    case MINIO:
                        nodeStorages.add(new NodeMinio(storageNode));
                        break;
                    default:
                        System.err.println("Storage node type " + storageNode.getType().toString() + " not found");
                }
            } catch (Exception ex){
                System.err.println("Error creating nodeStorage " + storageNode.getName() + ": " + ex.getMessage());
            }
        }

    }

    @Override
    public void downloadCompletedCallback(DownloadCompleted downloadCompleted) {
        SongDownload songDownload = databaseManager.getSongDownload(downloadCompleted.getDownloadId());
        if(songDownload == null){
            System.err.println("SongDownload " + downloadCompleted.getDownloadId() + " not found");
            return;
        }

        Long fileSize = internalStorage.getFileSize(songDownload.getDownloadId());
        if(fileSize == null){
            songDownload.setStatus("ERROR");
            databaseManager.updateSongDownload(songDownload);
            System.err.println("Song file " + songDownload.getDownloadId() + " not found in internal storage");
            return;
        }
        InputStream file = internalStorage.getFile(songDownload.getDownloadId());

        //try to upload in every node, if all fails something is wrong

        for(NodeStorage nodeStorage : nodeStorages){
            try{
                nodeStorage.storeFile(file, downloadCompleted.getDownloadName(), fileSize);
                songDownload.setStatus("COMPLETED");
                songDownload.setStored(true);
                songDownload.setStorageNodeName(nodeStorage.getStorageNodeName());
                songDownload.setDownloadName(downloadCompleted.getDownloadName());
                databaseManager.updateSongDownload(songDownload);
                return;
            } catch (Exception ex){
                System.err.println("Error uploading to nodeStorage: " + ex.getMessage());
            }
        }
    }
}
