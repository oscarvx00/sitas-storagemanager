package queue;

import dtos.DownloadCompleted;

public class RabbitConnector  implements QueueConnector{

    private String endpoint;
    private String credUser;
    private String credPass;
    private String virtualHost;

    public RabbitConnector(String endpoint, String credUser, String credPass, String virtualHost) {
        this.endpoint = endpoint;
        this.credUser = credUser;
        this.credPass = credPass;
        this.virtualHost = virtualHost;
    }

    @Override
    public void consumeQueue(String queueName) {

    }

    public interface RabbitConnectorCallback{
        void downloadCompletedCallback(DownloadCompleted downloadCompleted);
    }

}
