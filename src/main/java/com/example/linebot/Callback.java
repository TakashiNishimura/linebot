package com.example.linebot;


import com.example.linebot.sensors.*;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.MessageContentResponse;
import com.linecorp.bot.model.action.*;
import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.ImageMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.quickreply.QuickReply;
import com.linecorp.bot.model.message.quickreply.QuickReplyItem;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@LineMessageHandler
public class Callback {

    private LineMessagingClient client;

    @Autowired
    public Callback(LineMessagingClient client) {
        this.client = client;
    }

    private static final Logger log = LoggerFactory.getLogger(Callback.class);

    // フォローイベントに対応する
    @EventMapping
    public TextMessage handleFollow(FollowEvent event) {
        String userId = event.getSource().getUserId();
        return reply("登録ありがとうございます！私は植物の生活をより良いものにするためのサポートをします！" +
                "\n" + "利用方法は「温度」「湿度」「光」「水分」というと植物の各状況がわかり、何をしたらいいのかがわかるようになっています！" +
                "\n"+"\n"+
                "----操作方法----"+"\n"+
                "・「温度」-> 植物のいる部屋の温度がわかる。" +"\n"+
                "・「湿度」-> 湿度がわかる。" +"\n"+
                "・「光」-> 光の量がわかる。"+"\n"+
                "・「水分」-> 土の湿り気がわかる。"+"\n"+
                "・「報告」-> 植物の状態を共有するためのツールバー。"+"\n"+
                "・「説明」-> 操作説明を表示する。"+"\n");
    }

    // callbackメッセージを作成する場所
    private TextMessage reply(String text) {
        return new TextMessage(text);
    }

    // 送られた文章に対する処理分岐
    @EventMapping
    public Message handleMessage(MessageEvent<TextMessageContent> event) {
        TextMessageContent tmc = event.getMessage();
        String text = tmc.getText();
        switch (text) {
            case "温度":
                return temperature();
            case "ヘルプ":
                return description();
            case "湿度":
                return humidity();
            case "光":
                return analog();
            case "水分":
                return moisture();
            case "メニュー":
                return quickReplayMenu();
            default:
                return reply(text);
        }

    }

    public TextMessage description() {
        return reply("----操作方法----"+"\n"+
                "・「温度」-> 植物のいる部屋の温度がわかる。" +"\n"+
                "・「湿度」-> 湿度がわかる。" +"\n"+
                "・「光」-> 光の量がわかる。"+"\n"+
                "・「水分」-> 土の湿り気がわかる。"+"\n"+
                "・「メニュー」-> 植物の状態を共有するためのツールバー。"+"\n"+
                "・「ヘルプ」-> 操作説明を表示する。"+"\n");
    }

    public TextMessage temperature() {
        try {
            Celsius_degree celsiusdegree = Sensor.createTemperature();
            return reply("温度は" +
                    celsiusdegree.getCelsius_degree() +
                    "です。");
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            return reply("センサーに接続できていません。確認してみてください。");
        }
    }

    public TextMessage humidity() {
        try {
            Humidity humidity = Sensor.createHumidity();
            return reply("湿度は" +
                    humidity.getHumidity() +
                    "です。");
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            return reply("センサーに接続できていません。確認してみてください。");
        }
    }

    public TextMessage analog() {
        try {
            Analog analog = Sensor.createAnalog();
            return reply("光の量は" +
                    analog.getAnalog() +
                    "です。");
        } catch (HttpClientErrorException e) {
            return reply("センサーに接続できていません。確認してみてください。");
        }
    }

    public TextMessage moisture() {
        try {
            Moisture moisture = Sensor.createMoisture();
            return reply("土の湿り気は" +
                    moisture.getMoisture() +
                    "です。");
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            return reply("センサーに接続できていません。確認してみてください。");
        }
    }

    @EventMapping
    public Message handleImg(MessageEvent<ImageMessageContent> event) {
        String msgId = event.getMessage().getId();
        Optional<String> opt = Optional.empty();
        try {
            MessageContentResponse resp = client.getMessageContent(msgId).get();
            log.info("quickReplayMenu content{}:", resp);
            opt = makeTmpFile(resp, ".jpg");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        String path = opt.orElseGet(() -> "ファイル書き込みNG");
        return reply(path);
    }

    private Optional<String> makeTmpFile(MessageContentResponse resp, String extension) {
        try (InputStream is = resp.getStream()) {
            Path tmpFilePath = Files.createTempFile("linebot", extension);
            Files.copy(is, tmpFilePath, StandardCopyOption.REPLACE_EXISTING);
            return Optional.ofNullable(tmpFilePath.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Message quickReplayMenu() {
        final List<QuickReplyItem> items = Arrays.<QuickReplyItem>asList(
                QuickReplyItem.builder()
                        .action(new MessageAction("光量を確認", "光"))
                        .build(),
                QuickReplyItem.builder()
                        .action(new MessageAction("水分量を確認", "水分"))
                        .build(),
                QuickReplyItem.builder()
                        .action(new MessageAction("温度を確認", "温度"))
                        .build(),
                QuickReplyItem.builder()
                        .action(new MessageAction("湿度を確認", "湿度"))
                        .build(),
                QuickReplyItem.builder()
                        .action(new MessageAction("ヘルプを表示", "ヘルプ"))
                        .build(),
                QuickReplyItem.builder()
                        .action(CameraAction.withLabel("植物の写真を撮ろう！"))
                        .build(),
                QuickReplyItem.builder()
                        .action(CameraRollAction.withLabel("撮った写真を確認！"))
                        .build(),
                QuickReplyItem.builder()
                        .action(PostbackAction.builder()
                                .label("PostbackAction")
                                .text("PostbackAction clicked")
                                .data("{PostbackAction: true}")
                                .build())
                        .build()
        );

        final QuickReply quickReply = QuickReply.items(items);

        return TextMessage
                .builder()
                .text("植物に優しい環境を！！")
                .quickReply(quickReply)
                .build();
    }

}