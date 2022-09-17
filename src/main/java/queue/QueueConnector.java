package queue;

public interface QueueConnector {

    void connect() throws Exception;
    void consumeDownloadCompleteQueue(String queueName, String exchangeName) throws Exception;
}
