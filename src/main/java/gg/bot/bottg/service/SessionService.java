package gg.bot.bottg.service;


import com.google.gson.JsonArray;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import gg.bot.bottg.data.entity.Hosts;
import gg.bot.bottg.data.repository.HostsRepository;
import gg.bot.bottg.data.repository.PrizeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class SessionService {

    private final HostsRepository hostsRepository;
    private final ConnectionGizmoService connectionGizmoService;
    private final TelegramBot telegramBot;

    private final String ACTIVE_SESSION = "/api/usersessions/activeinfo";
    private final String USER_BALANCE = "/api/users/%s/balance";

    public SessionService(PrizeRepository prizeRepository, HostsRepository hostsRepository, ConnectionGizmoService connectionGizmoService, TelegramBot telegramBot) {
        this.hostsRepository = hostsRepository;
        this.connectionGizmoService = connectionGizmoService;
        this.telegramBot = telegramBot;
    }

    public HashMap<Long, String> getIds() {

        HashMap<Long, String> hashMap = new HashMap<>();
        JsonArray activeSeshJA = connectionGizmoService.connectionGet(connectionGizmoService.getToken(), ACTIVE_SESSION).getAsJsonArray("result");

        activeSeshJA.iterator().forEachRemaining(el -> {

            Long userId = el.getAsJsonObject().get("userId").getAsLong();
            Long hostId = el.getAsJsonObject().get("hostId").getAsLong();

            String url = String.format(USER_BALANCE, userId);
            long js = connectionGizmoService.connectionGet(connectionGizmoService.getToken(), url).getAsJsonObject("result")
                    .get("availableTime").getAsLong() / 60;


            String time = (js / 60) + "h " + (js % 60) + "min";

            hashMap.put(hostId, time);
        });

        return hashMap;
    }

    public HashMap<Long, String> getUserName() {

        HashMap<Long, String> userName = new HashMap<>();
        JsonArray activeSeshJA = connectionGizmoService.connectionGet(connectionGizmoService.getToken(), ACTIVE_SESSION).getAsJsonArray("result");

        activeSeshJA.iterator().forEachRemaining(name -> {

            Long hostId = name.getAsJsonObject().get("hostId").getAsLong();
            String user = name.getAsJsonObject().get("username").getAsString();

            userName.put(hostId, user);
        });

        return userName;
    }

    public void userActiveTime(Update update, boolean withName) {

        StringBuilder sb = new StringBuilder();
        List<Hosts> hostsList = hostsRepository.findAll();
        HashMap<Long, String> hashMap = getIds();
        HashMap<Long, String> userHashMap = getUserName();

        String pc = "\uD83D\uDCBB ";
        String available = " ✅";
        String unavailable = " \uD83D\uDE80";
        var ref = new Object() {
            int nonFreeCount = 0;
        };
        if (!withName) {
            hostsList.forEach(host -> {

                sb.append(pc).append(host.getHostNumber()).append(" ").append(host.getHostGroup()).append(" | ");

                if (hashMap.containsKey(host.getHostId())) {

                    sb.append(hashMap.get(host.getHostId())).append(unavailable);
                    ref.nonFreeCount++;
                } else {
                    sb.append(host.getTime()).append(available);
                }

                sb.append("\n");
            });
        } else {
            hostsList.forEach(host -> {

                String userName = userHashMap.get(host.getHostId());

                if (userName == null) {
                    userName = "";
                }

                sb.append(pc).append(host.getHostNumber()).append(" ").append(host.getHostGroup())
                        .append(" ").append("*")
                        .append(userName).append("*").append(" | ");

                if (hashMap.containsKey(host.getHostId())) {

                    sb.append(hashMap.get(host.getHostId())).append(unavailable);
                    ref.nonFreeCount++;
                } else {
                    sb.append(host.getTime()).append(available);
                }

                sb.append("\n");
            });
        }

        sb.append("\n").append("Свободных компов: ").append(39 - ref.nonFreeCount).append("\n");
        sb.append("Занятых компов: ").append(ref.nonFreeCount);

        telegramBot.execute(new SendMessage(update.message().chat().id(), sb.toString()).parseMode(ParseMode.Markdown));
    }
}
