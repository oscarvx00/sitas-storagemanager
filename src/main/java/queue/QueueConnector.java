package queue;

import dtos.DownloadCompleted;

public interface QueueConnector {

    void connect() throws Exception;

    void consumeDownloadCompleteQueue(QueueConnectorCallback callback, String queueName, String exchangeName) throws Exception;
}
