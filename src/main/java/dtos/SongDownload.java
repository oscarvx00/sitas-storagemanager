package dtos;

public class SongDownload {

    private String userId;
    private String downloadId;
    private String songName;
    private boolean stored;
    private String status;
    private String storageNodeName;

    public SongDownload(String userId, String downloadId, String songName, boolean stored, String status, String storageNodeName) {
        this.userId = userId;
        this.downloadId = downloadId;
        this.songName = songName;
        this.stored = stored;
        this.status = status;
        this.storageNodeName = storageNodeName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(String downloadId) {
        this.downloadId = downloadId;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public boolean isStored() {
        return stored;
    }

    public void setStored(boolean stored) {
        this.stored = stored;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStorageNodeName() {
        return storageNodeName;
    }

    public void setStorageNodeName(String storageNodeName) {
        this.storageNodeName = storageNodeName;
    }
}
