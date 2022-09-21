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

    private Connection connection;
    private Channel channel;

    public RabbitConnector(String endpoint, String credUser, String credPass, String virtualHost) {
        this.endpoint = endpoint;
        this.credUser = credUser;
        this.credPass = credPass;
        this.virtualHost = virtualHost;
    }
    @Override
    public void connect() throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(endpoint);
        factory.setVirtualHost(virtualHost);
        factory.setUsername(credUser);
        factory.setPassword(credPass);
        try{
            this.connection = factory.newConnection();
            this.channel = connection.createChannel();
        } catch (Exception ex){
            System.err.println("Error creating rabbit connection");
            throw new Exception(ex.getMessage());
        }

    }

    @Override
    public void consumeDownloadCompleteQueue(QueueConnectorCallback callback, String queueName, String exchangeName) throws Exception{
        channel.exchangeDeclare(exchangeName, "fanout");
        channel.queueDeclare(queueName, false, false, true, null);
        channel.queueBind(queueName, exchangeName, "");

        DeliverCallback deliverCallback = ((consumerTag, message) -> {
            String content = new String(message.getBody(), "UTF-8");
            callback.downloadCompletedCallback(DownloadCompleted.fromJson(content));
        });
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});
    }

    public void finishConnection(){
        try{
            this.channel.close();
        } catch (Exception ex){
            System.err.println("Error closing rabbit channel");
        }
    }


}
