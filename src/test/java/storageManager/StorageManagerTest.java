package storageManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StorageManagerTest {

    @Test
    public void testDummy(){
        StorageManager underTest = new StorageManager();

        Assertions.assertEquals(0, underTest.dummy());
    }

}