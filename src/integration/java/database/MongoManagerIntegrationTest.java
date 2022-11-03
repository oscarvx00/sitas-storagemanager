package database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import database.POJOs.SongDownloadPOJO;
import database.POJOs.StorageNodePOJO;
import dtos.SongDownload;
import dtos.StorageNode;
import org.bson.Document;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoManagerIntegrationTest {

    static DatabaseManager databaseManager;
    static MongoDatabase configDatabase;

    @BeforeAll
    public static void setUp() throws Exception{
        databaseManager = new MongoManager(
                System.getenv("MONGODB_ENDPOINT"),
                System.getenv("MONGODB_DATABASE")
        );
        initTestDatabase();
    }

    @AfterAll
    public static void resetTestDatabase(){
        configDatabase.getCollection("SongDownload", SongDownloadPOJO.class).deleteMany(new Document());
        configDatabase.getCollection("StorageNode", SongDownloadPOJO.class).deleteMany(new Document());
    }

    private static void initTestDatabase(){
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry codecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
        MongoClient client = MongoClients.create(System.getenv("MONGODB_ENDPOINT"));
        configDatabase = client.getDatabase(System.getenv("MONGODB_DATABASE")).withCodecRegistry(codecRegistry);

        configDatabase.getCollection("SongDownload", SongDownloadPOJO.class).deleteMany(new Document());
        configDatabase.getCollection("StorageNode", SongDownloadPOJO.class).deleteMany(new Document());

        initSongDownloadData(configDatabase);
        initStorageNodeData(configDatabase);
    }

    private static void initSongDownloadData(MongoDatabase database){

        MongoCollection<SongDownloadPOJO> collection = database.getCollection("SongDownload", SongDownloadPOJO.class);

        List<SongDownloadPOJO> songDownloadPOJOS = new ArrayList<>();
        SongDownloadPOJO pojo1 = new SongDownloadPOJO();
        pojo1.setUserId("u01");
        pojo1.setDownloadId("d01");
        pojo1.setSongName("s01");
        pojo1.setStored(false);
        pojo1.setStatus("DOWNLOADING");
        pojo1.setDownloadName("downloadName01");
        songDownloadPOJOS.add(pojo1);

        SongDownloadPOJO pojo2 = new SongDownloadPOJO();
        pojo2.setUserId("u02");
        pojo2.setDownloadId("d02");
        pojo2.setSongName("s02");
        pojo2.setStored(false);
        pojo2.setStatus("DOWNLOADING");
        pojo2.setDownloadName("downloadName02");
        songDownloadPOJOS.add(pojo2);

        collection.insertMany(songDownloadPOJOS);
    }

    private static void initStorageNodeData(MongoDatabase database){

        MongoCollection<StorageNodePOJO> collection = database.getCollection("StorageNode", StorageNodePOJO.class);

        List<StorageNodePOJO> storageNodePOJOS = new ArrayList<>();
        StorageNodePOJO pojo1 = new StorageNodePOJO();
        pojo1.setName("sn01");
        pojo1.setStable(false);
        pojo1.setBucket("b01");
        pojo1.setType("MINIO");
        pojo1.setEndpoint("e01");
        pojo1.setCredUser("cu01");
        pojo1.setCredPass("cp01");
        storageNodePOJOS.add(pojo1);

        StorageNodePOJO pojo2 = new StorageNodePOJO();
        pojo2.setName("sn02");
        pojo2.setStable(true);
        pojo2.setBucket("b02");
        pojo2.setType("INVALID");
        pojo2.setEndpoint("e02");
        pojo2.setCredUser("cu02");
        pojo2.setCredPass("cp02");
        storageNodePOJOS.add(pojo2);

        collection.insertMany(storageNodePOJOS);
    }

    @Test
    public void getSongDownloadTest (){
        SongDownload songDownload = databaseManager.getSongDownload("d01");

        Assertions.assertEquals(songDownload.getUserId(), "u01");
        Assertions.assertEquals(songDownload.getDownloadId(), "d01");
        Assertions.assertEquals(songDownload.getSongName(), "s01");
        Assertions.assertFalse(songDownload.isStored());
        Assertions.assertEquals(songDownload.getStatus(), "DOWNLOADING");
    }

    @Test
    public void getSongDownloadNotFoundTest(){
        SongDownload songDownload = databaseManager.getSongDownload("invalid");

        Assertions.assertNull(songDownload);
    }

    @Test
    public void updateSongDownloadTest(){
        SongDownload songDownload = new SongDownload(
                "uU02",
                "d02",
                "sU02",
                true,
                "COMPLETED",
                "sn02",
                "downloadName02"
        );

        databaseManager.updateSongDownload(songDownload);

        SongDownload updated = databaseManager.getSongDownload("d02");

        Assertions.assertEquals(updated.getUserId(), "uU02");
        Assertions.assertEquals(updated.getDownloadId(), "d02");
        Assertions.assertEquals(updated.getSongName(), "sU02");
        Assertions.assertTrue(updated.isStored());
        Assertions.assertEquals(updated.getStatus(), "COMPLETED");
        Assertions.assertEquals(updated.getStorageNodeName(), "sn02");
        Assertions.assertEquals("downloadName02", updated.getDownloadName());
    }

    @Test
    public void updateSongDownloadNotFoundTest(){
        SongDownload songDownload = new SongDownload(
                "uU02",
                "invalid",
                "sU02",
                true,
                "COMPLETED",
                "sn02",
                "downloadName02"
        );

        databaseManager.updateSongDownload(songDownload);
        SongDownload updated = databaseManager.getSongDownload("invalid");

        Assertions.assertNull(updated);
    }

    @Test
    public void getAllStorageNodes(){
        List<StorageNode> storageNodes = databaseManager.getAllStorageNodes();

        Assertions.assertEquals(storageNodes.size(), 2);
    }
}
