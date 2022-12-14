package dtos;

public class StorageNode {

    private String name;
    private boolean stable;
    private String bucket;
    private StorageNodeType type;
    private String endpoint;
    private String credUser;
    private String credPass;

    public StorageNode(String name, boolean stable, String bucket, StorageNodeType type, String endpoint, String credUser, String credPass) {
        this.name = name;
        this.stable = stable;
        this.bucket = bucket;
        this.type = type;
        this.endpoint = endpoint;
        this.credUser = credUser;
        this.credPass = credPass;
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

    public StorageNodeType getType() {
        return type;
    }

    public void setType(StorageNodeType type) {
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
}
