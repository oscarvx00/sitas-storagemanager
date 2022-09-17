package database;

import static com.mongodb.client.model.Filters.eq;

import com.mongodb.client.*;
import database.POJOs.SongDownloadPOJO;
import database.POJOs.StorageNodePOJO;
import dtos.SongDownload;
import dtos.StorageNode;
import org.bson.Document;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.ArrayList;
import java.util.List;

import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;

public class MongoManager implements DatabaseManager {
    private MongoClient client;
    private MongoDatabase database;


    public MongoManager(String endpoint, String database) throws Exception {
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry codecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
        this.client = MongoClients.create(endpoint);
        this.database = client.getDatabase(database).withCodecRegistry(codecRegistry);
    }

    @Override
    public SongDownload getSongDownload(String songDownloadId) {
        MongoCollection<SongDownloadPOJO> collection = database.getCollection("SongDownload", SongDownloadPOJO.class);
        SongDownloadPOJO doc = collection.find(eq("downloadId", songDownloadId)).first();
        if(doc == null){
            return null;
        }
        return doc.toSongDownload();
    }

    @Override
    public void updateSongDownload(SongDownload songDownload) {
        MongoCollection<SongDownloadPOJO> collection = database.getCollection("SongDownload", SongDownloadPOJO.class);
        SongDownloadPOJO doc = collection.find(eq("downloadId", songDownload.getDownloadId())).first();
        doc.updateFromSongDownload(songDownload);
        collection.deleteOne(eq("_id", doc.getId()));
        collection.insertOne(doc);
    }

    @Override
    public List<StorageNode> getAllStorageNodes() {
        MongoCollection<StorageNodePOJO> collection = database.getCollection("StorageNode", StorageNodePOJO.class);
        MongoCursor<StorageNodePOJO> cursor = collection.find().iterator();
        List<StorageNode> storageNodes = new ArrayList<>();
        while(cursor.hasNext()){
            storageNodes.add(cursor.next().toStorageNode());
        }
        return storageNodes;
    }
}
