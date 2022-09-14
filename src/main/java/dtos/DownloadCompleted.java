package dtos;

public class DownloadCompleted {

    private String downloadId;

    public DownloadCompleted(String downloadId) {
        this.downloadId = downloadId;
    }

    public String getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(String downloadId) {
        this.downloadId = downloadId;
    }
}
