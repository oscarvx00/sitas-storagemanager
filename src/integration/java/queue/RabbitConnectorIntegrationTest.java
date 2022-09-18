package queue;

import dtos.DownloadCompleted;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;
import storageManager.StorageManager;

import static org.mockito.Mockito.*;

public class RabbitConnectorIntegrationTest{

    private static QueueConnector rabbitConnector;

    @Mock
    private StorageManager storageManager;


    @BeforeAll
    public static void setUp() throws Exception{
        rabbitConnector = new RabbitConnector(
                System.getenv("RABBITMQ_ENDPOINT"),
                System.getenv("RABBITMQ_USER"), System.getenv("RABBITMQ_PASS"),
                System.getenv("RABBITMQ_VHOST")
        );
        rabbitConnector.connect();
    }

    @AfterAll
    public static void clean(){
        ((RabbitConnector) rabbitConnector).finishConnection();
    }

    /*
    @Test
    public void consumeDownloadCompleteQueueTest() throws Exception{



    }*/


}
