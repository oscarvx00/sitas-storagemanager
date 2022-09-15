package storageManager;

import dtos.DownloadCompleted;
import queue.RabbitConnector;

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

    public int dummy(){
        int i = 0;
        return i;
    }
}
