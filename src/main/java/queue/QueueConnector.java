package queue;

import dtos.DownloadCompleted;

public interface QueueConnector {

    void connect() throws Exception;

    void consumeDownloadCompleteQueue();
}
