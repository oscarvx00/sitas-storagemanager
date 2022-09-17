package internalStorage;

import io.minio.StatObjectResponse;

import java.io.InputStream;

public interface InternalStorageManager {
    InputStream getFile(String fileId);
    Long getFileSize(String fileId);
}
