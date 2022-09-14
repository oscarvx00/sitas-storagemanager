package rabbit;

import dtos.DownloadCompleted;

public class RabbitConnector {

    public interface RabbitConnectorCallback{
        void downloadCompletedCallback(DownloadCompleted downloadCompleted);
    }

}
