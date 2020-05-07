package com.ujian.backEnd.controller;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.ujian.backEnd.repository.UserMapper;
import com.ujian.backEnd.service.BackendService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/")
public class BEController {


    @Autowired
    UserMapper userMapper;

    @Autowired
    BackendService be;



    @GetMapping("/login")
    public void login() throws IOException, TimeoutException {
        String TASK_QUEUE_NAME = "login";
        ConnectionFactory factory = new ConnectionFactory();
        JSONParser jsonParser = new JSONParser();
        factory.setHost("localhost");
        final Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();
        channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        channel.basicQos(1);
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
            try {
                JSONObject jsonObject =(JSONObject)jsonParser.parse(message);
                String username = (String) jsonObject.get("username");
                String password = (String) jsonObject.get("password");
                if(userMapper.cekUser(username,password)!= 0){
                    System.out.println("berhasil login");
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                doWork(message);
            } finally {
                System.out.println(" [x] Done");
            }
        };
        channel.basicConsume(TASK_QUEUE_NAME, true, deliverCallback, consumerTag -> { });

    }


    @GetMapping("/cart")
    public void listCart() throws IOException, TimeoutException {
        Jedis jedis = new Jedis("localhost");
        System.out.println("Connection to server sucessfully");
        String TASK_QUEUE_NAME = "cart";
        ConnectionFactory factory = new ConnectionFactory();
        JSONParser jsonParser = new JSONParser();
        factory.setHost("localhost");
        final Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();
        channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        channel.basicQos(1);
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
            try {
                doWork(message);
                JSONObject jsonObject =(JSONObject)jsonParser.parse(message);
                String id = (String) jsonObject.get("id");
                String item = (String) jsonObject.get("item");
                String nama = userMapper.cariNama(Integer.parseInt(id));
                jedis.lpush(nama,item);
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                System.out.println(" [x] Done");
            }
        };
        channel.basicConsume(TASK_QUEUE_NAME, true, deliverCallback, consumerTag -> { });
    }

//    input ke tabel transaction dari redis

    @PostMapping("/{id}/payment")
    public ResponseEntity bayar(@PathVariable int id){
        String nama = userMapper.cariNama(id);
        be.saveitem(nama);
        return  ResponseEntity.status(HttpStatus.CREATED).build();
    }

//    show all transaction
    @GetMapping("/payment")
    public CompletableFuture<ResponseEntity> findAll(){
        return be.show().thenApply(ResponseEntity::ok);
    }

//    -----------------------------------------------------------------------------------
    private static void doWork(String task) {
        for (char ch : task.toCharArray()) {
            if (ch == '}') {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException _ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
//    ------------------------------------------------------------------------------------


}
