package gg.bot.bottg.service;

import com.pengrad.telegrambot.model.request.*;
import gg.bot.bottg.data.entity.Prize;
import gg.bot.bottg.data.entity.User;
import gg.bot.bottg.data.repository.PrizeRepository;
import gg.bot.bottg.data.repository.UserRepository;
import gg.bot.bottg.jsonObjects.PrizeJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public class KeyboardService {

    @Autowired
    PrizeRepository prizeRepository;

    @Autowired
    UserRepository userRepository;

    public HashMap<Long, String> getPrizes() {

        HashMap<Long, String> hashMapWithPrizeDayAndName = new HashMap<>();
        List<Prize> prizes = prizeRepository.findAll();

        for (Prize prize : prizes) {
            hashMapWithPrizeDayAndName.put(prize.getPrizeDay(), prize.getPrizeName());
        }
        return hashMapWithPrizeDayAndName;
    }

    public PrizeJson getStatusPrize(Long telegramId) {

        if (userRepository.getUserByTelegramId(telegramId).isEmpty()) {
            return new PrizeJson();
        } else {
            return userRepository.getUserByTelegramId(telegramId).get().getPrizeJson();
        }
    }

    public Keyboard chooseYeaOrNo() {

        return new ReplyKeyboardMarkup(
                new KeyboardButton("Да ☺\uFE0F"),
                new KeyboardButton("Нет \uD83E\uDD72"))
                .resizeKeyboard(true)
                .oneTimeKeyboard(true);
    }

    public InlineKeyboardMarkup firstInlineKeyboardWithPrizes(Long id) {

        HashMap<Long, String> firstInlineHashMap = getPrizes();
        PrizeJson prizeJson = getStatusPrize(id);

        String strPrize1;
        if (prizeJson.getReceivedPrizeOfDay1()) {
            strPrize1 = firstInlineHashMap.get(1L) + " ✅";
        } else {
            strPrize1 = firstInlineHashMap.get(1L) + " ❌";
        }

        String strPrize2;
        if (prizeJson.getReceivedPrizeOfDay2()) {
            strPrize2 = firstInlineHashMap.get(2L) + " ✅";
        } else {
            strPrize2 = firstInlineHashMap.get(2L) + " ❌";
        }

        String strPrize3;
        if (prizeJson.getReceivedPrizeOfDay3()) {
            strPrize3 = firstInlineHashMap.get(3L) + " ✅";
        } else {
            strPrize3 = firstInlineHashMap.get(3L) + " ❌";
        }

        String strPrize4;
        if (prizeJson.getReceivedPrizeOfDay4()) {
            strPrize4 = firstInlineHashMap.get(4L) + " ✅";
        } else {
            strPrize4 = firstInlineHashMap.get(4L) + " ❌";
        }

        String strPrize5;
        if (prizeJson.getReceivedPrizeOfDay5()) {
            strPrize5 = firstInlineHashMap.get(5L) + " ✅";
        } else {
            strPrize5 = firstInlineHashMap.get(5L) + " ❌";
        }

        String strPrize6;
        if (prizeJson.getReceivedPrizeOfDay6()) {
            strPrize6 = firstInlineHashMap.get(6L) + " ✅";
        } else {
            strPrize6 = firstInlineHashMap.get(6L) + " ❌";
        }

        String strPrize7;
        if (prizeJson.getReceivedPrizeOfDay7()) {
            strPrize7 = firstInlineHashMap.get(7L) + " ✅";
        } else {
            strPrize7 = firstInlineHashMap.get(7L) + " ❌";
        }

        return new InlineKeyboardMarkup(
                new InlineKeyboardButton(strPrize1).callbackData("1"))
                .addRow(new InlineKeyboardButton(strPrize2).callbackData("2"))
                .addRow(new InlineKeyboardButton(strPrize3).callbackData("3"))
                .addRow(new InlineKeyboardButton(strPrize4).callbackData("4"))
                .addRow(new InlineKeyboardButton(strPrize5).callbackData("5"))
                .addRow(new InlineKeyboardButton(strPrize6).callbackData("6"))
                .addRow(new InlineKeyboardButton("\uD83D\uDD01").callbackData("update"),
                        new InlineKeyboardButton(strPrize7).callbackData("7"),
                        new InlineKeyboardButton("➡\uFE0F").callbackData("up_to_second_page"));
    }

    public InlineKeyboardMarkup secondInlineKeyboardWithPrizes(Long id) {

        HashMap<Long, String> secondInlineHashMap = getPrizes();
        PrizeJson prizeJson = getStatusPrize(id);

        String strPrize8;
        if (prizeJson.getReceivedPrizeOfDay8()) {
            strPrize8 = secondInlineHashMap.get(8L) + " ✅";
        } else {
            strPrize8 = secondInlineHashMap.get(8L) + " ❌";
        }

        String strPrize9;
        if (prizeJson.getReceivedPrizeOfDay9()) {
            strPrize9 = secondInlineHashMap.get(9L) + " ✅";
        } else {
            strPrize9 = secondInlineHashMap.get(9L) + " ❌";
        }

        String strPrize10;
        if (prizeJson.getReceivedPrizeOfDay10()) {
            strPrize10 = secondInlineHashMap.get(10L) + " ✅";
        } else {
            strPrize10 = secondInlineHashMap.get(10L) + " ❌";
        }

        String strPrize11;
        if (prizeJson.getReceivedPrizeOfDay11()) {
            strPrize11 = secondInlineHashMap.get(11L) + " ✅";
        } else {
            strPrize11 = secondInlineHashMap.get(11L) + " ❌";
        }

        String strPrize12;
        if (prizeJson.getReceivedPrizeOfDay12()) {
            strPrize12 = secondInlineHashMap.get(12L) + " ✅";
        } else {
            strPrize12 = secondInlineHashMap.get(12L) + " ❌";
        }

        String strPrize13;
        if (prizeJson.getReceivedPrizeOfDay13()) {
            strPrize13 = secondInlineHashMap.get(13L) + " ✅";
        } else {
            strPrize13 = secondInlineHashMap.get(13L) + " ❌";
        }

        String strPrize14;
        if (prizeJson.getReceivedPrizeOfDay14()) {
            strPrize14 = secondInlineHashMap.get(14L) + " ✅";
        } else {
            strPrize14 = secondInlineHashMap.get(14L) + " ❌";
        }

        return new InlineKeyboardMarkup(
                new InlineKeyboardButton(strPrize8).callbackData("8"))
                .addRow(new InlineKeyboardButton(strPrize9).callbackData("9"))
                .addRow(new InlineKeyboardButton(strPrize10).callbackData("10"))
                .addRow(new InlineKeyboardButton(strPrize11).callbackData("11"))
                .addRow(new InlineKeyboardButton(strPrize12).callbackData("12"))
                .addRow(new InlineKeyboardButton(strPrize13).callbackData("13"))
                .addRow(new InlineKeyboardButton("⬅\uFE0F").callbackData("back_to_first_page"),
                        new InlineKeyboardButton(strPrize14).callbackData("14"),
                        new InlineKeyboardButton("➡\uFE0F").callbackData("up_to_third_page"));
    }

    public InlineKeyboardMarkup thirdInlineKeyboardWithPrizes(Long id) {

        HashMap<Long, String> thirdInlineHashMap = getPrizes();
        PrizeJson prizeJson = getStatusPrize(id);

        String strPrize15;
        if (prizeJson.getReceivedPrizeOfDay15()) {
            strPrize15 = thirdInlineHashMap.get(15L) + " ✅";
        } else {
            strPrize15 = thirdInlineHashMap.get(15L) + " ❌";
        }

        String strPrize16;
        if (prizeJson.getReceivedPrizeOfDay16()) {
            strPrize16 = thirdInlineHashMap.get(16L) + " ✅";
        } else {
            strPrize16 = thirdInlineHashMap.get(16L) + " ❌";
        }

        String strPrize17;
        if (prizeJson.getReceivedPrizeOfDay17()) {
            strPrize17 = thirdInlineHashMap.get(17L) + " ✅";
        } else {
            strPrize17 = thirdInlineHashMap.get(17L) + " ❌";
        }

        String strPrize18;
        if (prizeJson.getReceivedPrizeOfDay18()) {
            strPrize18 = thirdInlineHashMap.get(18L) + " ✅";
        } else {
            strPrize18 = thirdInlineHashMap.get(18L) + " ❌";
        }

        String strPrize19;
        if (prizeJson.getReceivedPrizeOfDay19()) {
            strPrize19 = thirdInlineHashMap.get(19L) + " ✅";
        } else {
            strPrize19 = thirdInlineHashMap.get(19L) + " ❌";
        }

        String strPrize20;
        if (prizeJson.getReceivedPrizeOfDay20()) {
            strPrize20 = thirdInlineHashMap.get(20L) + " ✅";
        } else {
            strPrize20 = thirdInlineHashMap.get(20L) + " ❌";
        }

        String strPrize21;
        if (prizeJson.getReceivedPrizeOfDay21()) {
            strPrize21 = thirdInlineHashMap.get(21L) + " ✅";
        } else {
            strPrize21 = thirdInlineHashMap.get(21L) + " ❌";
        }

        return new InlineKeyboardMarkup(
                new InlineKeyboardButton(strPrize15).callbackData("15"))
                .addRow(new InlineKeyboardButton(strPrize16).callbackData("16"))
                .addRow(new InlineKeyboardButton(strPrize17).callbackData("17"))
                .addRow(new InlineKeyboardButton(strPrize18).callbackData("18"))
                .addRow(new InlineKeyboardButton(strPrize19).callbackData("19"))
                .addRow(new InlineKeyboardButton(strPrize20).callbackData("20"))
                .addRow(new InlineKeyboardButton("⬅\uFE0F").callbackData("back_to_second_page"),
                        new InlineKeyboardButton(strPrize21).callbackData("21"),
                        new InlineKeyboardButton("➡\uFE0F").callbackData("up_to_fourth_page"));
    }

    public InlineKeyboardMarkup fourthInlineKeyboardWithPrizes(Long id) {

        HashMap<Long, String> fourthInlineHashMap = getPrizes();
        PrizeJson prizeJson = getStatusPrize(id);

        String strPrize22;
        if (prizeJson.getReceivedPrizeOfDay22()) {
            strPrize22 = fourthInlineHashMap.get(22L) + " ✅";
        } else {
            strPrize22 = fourthInlineHashMap.get(22L) + " ❌";
        }

        String strPrize23;
        if (prizeJson.getReceivedPrizeOfDay23()) {
            strPrize23 = fourthInlineHashMap.get(23L) + " ✅";
        } else {
            strPrize23 = fourthInlineHashMap.get(23L) + " ❌";
        }

        String strPrize24;
        if (prizeJson.getReceivedPrizeOfDay24()) {
            strPrize24 = fourthInlineHashMap.get(24L) + " ✅";
        } else {
            strPrize24 = fourthInlineHashMap.get(24L) + " ❌";
        }

        String strPrize25;
        if (prizeJson.getReceivedPrizeOfDay25()) {
            strPrize25 = fourthInlineHashMap.get(25L) + " ✅";
        } else {
            strPrize25 = fourthInlineHashMap.get(25L) + " ❌";
        }

        String strPrize26;
        if (prizeJson.getReceivedPrizeOfDay26()) {
            strPrize26 = fourthInlineHashMap.get(26L) + " ✅";
        } else {
            strPrize26 = fourthInlineHashMap.get(26L) + " ❌";
        }

        String strPrize27;
        if (prizeJson.getReceivedPrizeOfDay27()) {
            strPrize27 = fourthInlineHashMap.get(27L) + " ✅";
        } else {
            strPrize27 = fourthInlineHashMap.get(27L) + " ❌";
        }

        String strPrize28;
        if (prizeJson.getReceivedPrizeOfDay28()) {
            strPrize28 = fourthInlineHashMap.get(28L) + " ✅";
        } else {
            strPrize28 = fourthInlineHashMap.get(28L) + " ❌";
        }

        return new InlineKeyboardMarkup(
                new InlineKeyboardButton(strPrize22).callbackData("22"))
                .addRow(new InlineKeyboardButton(strPrize23).callbackData("23"))
                .addRow(new InlineKeyboardButton(strPrize24).callbackData("24"))
                .addRow(new InlineKeyboardButton(strPrize25).callbackData("25"))
                .addRow(new InlineKeyboardButton(strPrize26).callbackData("26"))
                .addRow(new InlineKeyboardButton(strPrize27).callbackData("27"))
                .addRow(new InlineKeyboardButton("⬅\uFE0F").callbackData("back_to_third_page"),
                        new InlineKeyboardButton(strPrize28).callbackData("28"),
                        new InlineKeyboardButton(" ").callbackData("none"));
    }
}
