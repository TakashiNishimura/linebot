package com.example.linebot;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.action.DatetimePickerAction;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.model.richmenu.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

//課金は草
@RestController
public class RichMenuController {

    private static final Logger log = LoggerFactory.getLogger(Push.class);

    private String userId = "U21fa6381fbfe945c7f2b88e9f366605d";

    private final LineMessagingClient client;

    @Autowired
    public RichMenuController(LineMessagingClient lineMessagingClient) {
        this.client = lineMessagingClient;
    }

    @GetMapping("addRich")
    public String addRichMenu() {
        String text = "create menu-bar";

        RichMenu richMenu = RichMenu.builder()
                .name("menu01")
                .chatBarText("image")
                .areas(makeRichMenuAreas())
                .selected(true)
                .size(RichMenuSize.FULL)
                .build();

        try {

            RichMenuIdResponse resp1 = client.createRichMenu(richMenu).get();
            log.info("create richmenu:{}", resp1);

            ClassPathResource cpr = new ClassPathResource("/img/RichMenuSample.jpg");
            byte[] fileContent = Files.readAllBytes(cpr.getFile().toPath());
            BotApiResponse resp2 = client.setRichMenuImage(resp1.getRichMenuId(), "image/jpeg", fileContent).get();
            log.info("set richmenu image:{}", resp2);

            BotApiResponse resp3 = client.linkRichMenuIdToUser(userId, resp1.getRichMenuId()).get();
            log.info("link richmenu:{}", resp3);

        } catch (InterruptedException | ExecutionException | IOException e) {
            throw new RuntimeException(e);
        }
        return text;
    }

    @GetMapping("delRich")
    public String delRichMenu() {
        String text = "削除";
        try {

            client.unlinkRichMenuIdFromUser(userId);

            RichMenuListResponse resp4 = client.getRichMenuList().get();
            log.info("quickReplayMenu richmenus:{}", resp4);

            resp4.getRichMenus().stream()
                    .forEach(r -> client.deleteRichMenu(r.getRichMenuId()));

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return text;
    }

    private List<RichMenuArea> makeRichMenuAreas() {
        final List<RichMenuArea> richMenuAreas = new ArrayList<>();
        richMenuAreas.add(makeMessageAction(551, 325, 321, 321, "Up"));
        richMenuAreas.add(makeMessageAction(876, 651, 321, 321, "Right"));
        richMenuAreas.add(makeMessageAction(551, 972, 321, 321, "Down"));
        richMenuAreas.add(makeMessageAction(225, 651, 321, 321, "Left"));
        richMenuAreas.add(makeURIAction(1433, 657, 367, 367, "a", "https://google.com/"));
        richMenuAreas.add(makeDateTimeAction(1907, 657, 367, 367, "b"));
        return richMenuAreas;
    }

    private RichMenuArea makeMessageAction(int x, int y, int w, int h, String label) {
        return new RichMenuArea(new RichMenuBounds(x, y, w, h),
                //未修正
                new MessageAction(label, label ));
    }

    private RichMenuArea makeURIAction(int x, int y, int w, int h, String label, String uri) {
        return new RichMenuArea(new RichMenuBounds(x, y, w, h),
                new URIAction(label, uri, new URIAction.AltUri(URI.create(uri))));
    }

    private RichMenuArea makeDateTimeAction(int x, int y, int w, int h, String label) {
        return new RichMenuArea(new RichMenuBounds(x, y, w, h),
                new DatetimePickerAction(label, "DT", "datetime"));
    }

}