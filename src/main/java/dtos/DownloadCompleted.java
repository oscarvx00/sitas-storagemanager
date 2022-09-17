package dtos;

import com.google.gson.Gson;

public class DownloadCompleted {

    private String downloadId;

    public DownloadCompleted(String downloadId) {
        this.downloadId = downloadId;
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

    public void setDownloadId(String downloadId) {
        this.downloadId = downloadId;
    }
}
