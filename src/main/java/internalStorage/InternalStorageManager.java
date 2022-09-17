package internalStorage;

import java.io.InputStream;

public interface InternalStorageManager {
    InputStream getFile(String fileId);
}
