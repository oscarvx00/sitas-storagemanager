package database.POJOs;

import dtos.SongDownload;
import org.bson.types.ObjectId;

public class SongDownloadPOJO {

    private ObjectId id;
    private String userId;
    private String downloadId;
    private String songName;
    private boolean stored;
    private String status;
    private String storageNodeName;

    private String downloadName;


    public SongDownloadPOJO(){}

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
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

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getDownloadName() {
        return downloadName;
    }

    public void setDownloadName(String downloadName) {
        this.downloadName = downloadName;
    }

    public SongDownload toSongDownload(){
        return new SongDownload(
                userId,
                downloadId,
                songName,
                stored,
                status,
                storageNodeName,
                downloadName
        );
    }

    public void updateFromSongDownload(SongDownload songDownload){
        this.songName = songDownload.getSongName();
        this.downloadId = songDownload.getDownloadId();
        this.userId = songDownload.getUserId();
        this.status = songDownload.getStatus();
        this.storageNodeName = songDownload.getStorageNodeName();
        this.stored = songDownload.isStored();
    }
}
