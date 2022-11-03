package dtos;

import com.google.gson.Gson;

public class DownloadCompleted {

    private String downloadId;
    private String downloadName;

    public DownloadCompleted(String downloadId, String downloadName) {

        this.downloadId = downloadId;
        this.downloadName = downloadName;
    }

    public static DownloadCompleted fromJson(String rawJson){
        Gson gson = new Gson();

        try{
            DownloadCompleted downloadCompleted = gson.fromJson(rawJson, DownloadCompleted.class);
            return downloadCompleted;
        } catch(Exception e){
            System.err.println(e.getMessage());
            return null;
        }
    }

    public String getDownloadId() {
        return downloadId;
    }

    public String getDownloadName() {
        return downloadName;
    }

    public void setDownloadId(String downloadId) {
        this.downloadId = downloadId;
    }
}
