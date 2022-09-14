package dtos;

public class StorageNode {

    private String name;
    private int priority;
    private String bucket;
    private int bucketLimit;
    private StorageNodeType type;
    private String endpoint;
    private String credUser;
    private String credPass;

    public StorageNode(String name, int priority, String bucket, int bucketLimit, StorageNodeType type, String endpoint, String credUser, String credPass) {
        this.name = name;
        this.priority = priority;
        this.bucket = bucket;
        this.bucketLimit = bucketLimit;
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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public int getBucketLimit() {
        return bucketLimit;
    }

    public void setBucketLimit(int bucketLimit) {
        this.bucketLimit = bucketLimit;
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
