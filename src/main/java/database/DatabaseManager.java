package database;

import dtos.SongDownload;
import dtos.StorageNode;

import java.util.List;

public interface DatabaseManager {

    SongDownload getSongDownload(String songDownloadId);
    void updateSongDownload(SongDownload songDownload);

    List<StorageNode> getAllStorageNodes();
}
