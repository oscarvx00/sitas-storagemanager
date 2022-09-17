package storageManager;

import database.DatabaseManager;
import database.MongoManager;
import dtos.DownloadCompleted;
import queue.QueueConnector;
import queue.RabbitConnector;

public class StorageManager implements RabbitConnector.RabbitConnectorCallback {

    private QueueConnector rabbitConnector;
    private DatabaseManager databaseManager;

    public StorageManager() {
    }

    public void init(){

        try{
            databaseManager = new MongoManager(
                    "mongodb+srv://sitas-db-user:mSudF19AlNNR510G@sitas-cluster0.3byxaum.mongodb.net/?retryWrites=true&w=majority",
                    "sitas-dev"
            );
        } catch (Exception ex){
            System.err.println("Error connecting to database " + ex.getMessage());
        }

        databaseManager.getSongDownload("downloadId");

        rabbitConnector = new RabbitConnector(
                this,
                "goose-01.rmq2.cloudamqp.com",
                "oaoesvtq", "S3ssgHzl0YgY8dMJR6ZsjclHiOiP8FkN",
                "oaoesvtq"
        );

        try{
            rabbitConnector.connect();
            rabbitConnector.consumeDownloadCompleteQueue(
                    "hello",
                    "hello"
            );
        } catch (Exception e){
            System.err.println(e.getMessage());
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
