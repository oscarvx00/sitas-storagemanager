package nodeStorage;

import dtos.StorageNode;

import java.io.InputStream;

public abstract class NodeStorage {

    StorageNode info;
    public NodeStorage(StorageNode info){
        this.info = info;
    }
    public abstract void storeFile(InputStream file, String fileId, Long fileSize) throws Exception;

    public String getStorageNodeName(){
        return info.getName();
    }

}
