package storageManager;

import dtos.DownloadCompleted;
import rabbit.RabbitConnector;

public class StorageManager implements RabbitConnector.RabbitConnectorCallback {

    private RabbitConnector rabbitConnector;

    public StorageManager() {
    }

    public void init(){
        //rabbitConnector =
    }

    @Override
    public void downloadCompletedCallback(DownloadCompleted downloadCompleted) {

    }
}
