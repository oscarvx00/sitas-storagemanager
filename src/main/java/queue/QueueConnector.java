package queue;

import dtos.DownloadCompleted;

public interface QueueConnector {

    void consumeDownloadCompleteQueue();
}
