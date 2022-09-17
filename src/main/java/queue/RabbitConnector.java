package queue;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import dtos.DownloadCompleted;

public class RabbitConnector  implements QueueConnector{

    private String endpoint;
    private String credUser;
    private String credPass;
    private String virtualHost;

    private RabbitConnectorCallback callback;

    private Channel channel;


    public interface RabbitConnectorCallback{
        void downloadCompletedCallback(DownloadCompleted downloadCompleted);
    }


    public RabbitConnector(RabbitConnectorCallback callback, String endpoint, String credUser, String credPass, String virtualHost) {
        this.endpoint = endpoint;
        this.credUser = credUser;
        this.credPass = credPass;
        this.virtualHost = virtualHost;
        this.callback = callback;
    }
    @Override
    public void connect() throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(endpoint);
        factory.setVirtualHost(virtualHost);
        factory.setUsername(credUser);
        factory.setPassword(credPass);
        Connection connection = factory.newConnection();

        this.channel = connection.createChannel();
    }

    @Override
    public void consumeDownloadCompleteQueue(String queueName, String exchangeName) throws Exception{
        channel.exchangeDeclare(exchangeName, "fanout");
        channel.queueDeclare(queueName, false, false, true, null);
        channel.queueBind(queueName, exchangeName, "");

        DeliverCallback deliverCallback = ((consumerTag, message) -> {
            String content = new String(message.getBody(), "UTF-8");
            callback.downloadCompletedCallback(DownloadCompleted.fromJson(content));
        });
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});
    }



}
