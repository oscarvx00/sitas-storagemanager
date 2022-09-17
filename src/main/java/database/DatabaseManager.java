package database;

import dtos.SongDownload;

public interface DatabaseManager {

    SongDownload getSongDownload(String songDownloadId);
    void updateSongDownload(SongDownload songDownload);
}
