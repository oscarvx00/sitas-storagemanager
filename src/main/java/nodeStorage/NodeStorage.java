package nodeStorage;

import java.io.InputStream;

public interface NodeStorage {
    void storeFile(InputStream file, String fileId) throws Exception;
}
