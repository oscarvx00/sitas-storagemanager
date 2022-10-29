package database.POJOs;

import dtos.StorageNode;
import dtos.StorageNodeType;
import org.bson.types.ObjectId;

public class StorageNodePOJO {

    private ObjectId id;
    private String name;
    private boolean stable;
    private String bucket;
    private String type;
    private String endpoint;
    private String credUser;
    private String credPass;

    public StorageNodePOJO() {}

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isStable() {
        return stable;
    }

    public void setStable(boolean stable) {
        this.stable = stable;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getCredUser() {
        return credUser;
    }

    public void setCredUser(String credUser) {
        this.credUser = credUser;
    }

    public String getCredPass() {
        return credPass;
    }

    public void setCredPass(String credPass) {
        this.credPass = credPass;
    }

    public StorageNode toStorageNode(){
        try{
            return new StorageNode(
                    name,
                    stable,
                    bucket,
                    StorageNodeType.valueOf(type),
                    endpoint,
                    credUser,
                    credPass
            );
        } catch (Exception ex){
            System.err.println("Error getting storage node " + name + ": " + ex.getMessage());
            return null;
        }
    }
}
