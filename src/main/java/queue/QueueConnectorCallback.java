package queue;

import dtos.DownloadCompleted;

public interface QueueConnectorCallback {
    void downloadCompletedCallback(DownloadCompleted downloadCompleted);
}
