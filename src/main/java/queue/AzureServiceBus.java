package queue;

import com.azure.messaging.servicebus.*;
import com.google.gson.Gson;
import dtos.DownloadCompleted;
import storageManager.StorageManager;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

public class AzureServiceBus implements QueueConnector {


    @Override
    public void connect() throws Exception {

    }

    @Override
    public void consumeDownloadCompleteQueue() {
        ServiceBusProcessorClient processorClient = new ServiceBusClientBuilder()
                .connectionString(System.getenv("AZURE_SERVICE_BUS_CONNECTION_STRING"))
                .processor()
                .queueName(System.getenv("QUEUE_DOWNLOAD_COMPLETED"))
                .processMessage(context -> processMessage(context))
                .processError(AzureServiceBus::processError)
                .buildProcessorClient();

        processorClient.start();
    }

    private static void processMessage(ServiceBusReceivedMessageContext context) {
        ServiceBusReceivedMessage message = context.getMessage();
        Gson gson = new Gson();
        try {
            DownloadCompleted downloadCompleted = DownloadCompleted.fromJson(new String(message.getBody().toBytes(), "UTF-8"));
            StorageManager.downloadCompletedCallback(downloadCompleted);
        } catch (UnsupportedEncodingException ex) {
            System.err.println("Encoding error azure queue message: " + ex.getMessage());
        }
    }

    private static void processError(ServiceBusErrorContext context){
        System.out.printf("Error when receiving messages from namespace: '%s'. Entity: '%s'%n",
                context.getFullyQualifiedNamespace(), context.getEntityPath());

        if (!(context.getException() instanceof ServiceBusException)) {
            System.out.printf("Non-ServiceBusException occurred: %s%n", context.getException());
            return;
        }

        ServiceBusException exception = (ServiceBusException) context.getException();
        ServiceBusFailureReason reason = exception.getReason();

        if (reason == ServiceBusFailureReason.MESSAGING_ENTITY_DISABLED
                || reason == ServiceBusFailureReason.MESSAGING_ENTITY_NOT_FOUND
                || reason == ServiceBusFailureReason.UNAUTHORIZED) {
            System.out.printf("An unrecoverable error occurred. Stopping processing with reason %s: %s%n",
                    reason, exception.getMessage());

        } else if (reason == ServiceBusFailureReason.MESSAGE_LOCK_LOST) {
            System.out.printf("Message lock lost for message: %s%n", context.getException());
        } else if (reason == ServiceBusFailureReason.SERVICE_BUSY) {
            try {
                // Choosing an arbitrary amount of time to wait until trying again.
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                System.err.println("Unable to sleep for period of time");
            }
        } else {
            System.out.printf("Error source %s, reason %s, message: %s%n", context.getErrorSource(),
                    reason, context.getException());
        }
    }
}
