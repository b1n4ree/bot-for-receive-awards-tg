package gg.bot.bottg.service;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.EditMessageReplyMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import gg.bot.bottg.data.entity.Action;
import gg.bot.bottg.data.entity.User;
import gg.bot.bottg.data.repository.ActionRepository;
import gg.bot.bottg.data.repository.UserRepository;
import gg.bot.bottg.enums.Actions;
import gg.bot.bottg.jsonObjects.PrizeJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@Slf4j

public class CallBackService {

    private final TelegramBot telegramBot;
    private final KeyboardService keyboardService;
    private final ConnectionGizmoService connectionGizmoService;
    private final String TEXT_PACKAGE_TIME_GET = """
                                                    Награда доступна!
                                                    Подойди к админу и покажи это сообщение.
                                                    Пакет времени можно получить до:
                                                 """;

    private final UserRepository userRepository;

    @Value("${gizmo_users_spending_custom_url}")
    private String gizmoUserSpendingUrl;

    @Value("${gizmo_user_id}")
    private String userIdGizmo;

    @Value("${gizmo_user_award_url}")
    private String awardUser;

    @Autowired
    ActionRepository actionRepository;


    public CallBackService(TelegramBot telegramBot, KeyboardService keyboardService, ConnectionGizmoService connectionGizmoService, UserRepository userRepository) {

        this.telegramBot = telegramBot;
        this.keyboardService = keyboardService;
        this.connectionGizmoService = connectionGizmoService;
        this.userRepository = userRepository;
    }

    public void callbackHandler(Update update) {

        Action action = new Action();

        if (update.callbackQuery() != null) {

            Long userIdCallbackQuery = update.callbackQuery().from().id();
            Integer messageId = update.callbackQuery().message().messageId();
            PrizeJson statusPrize = keyboardService.getStatusPrize(userIdCallbackQuery);

            if (userRepository.getUserByTelegramId(userIdCallbackQuery).isEmpty()) {
                return;
            }

            User user = userRepository.getUserByTelegramId(userIdCallbackQuery).get();
            Long currentStreakDay = user.getCurrentStreakDay();
//            String userId = String.format(userIdGizmo, URLEncoder.encode(user.getGizmoName(), StandardCharsets.UTF_8));
//
//            JsonObject jsonObject = connectionGizmoService.connectionGet(connectionGizmoService.getToken(), userId);
//
//            if (jsonObject.get("result") != null) {
//
//                user.setGizmoId(jsonObject.get("result").getAsLong());
//                userRepository.save(user);
//            }
//            System.out.println(jsonObject.get("result"));

            if ("up_to_second_page".equalsIgnoreCase(update.callbackQuery().data())) {

                if (user.getCurrentStreakDay() > 6) {
                    EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(userIdCallbackQuery, messageId)
                            .replyMarkup(keyboardService.secondInlineKeyboardWithPrizes(userIdCallbackQuery));

                    telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()));
                    telegramBot.execute(editMessageReplyMarkup);

                    action.setAction(Actions.CALLBACK_PAGE_GO_TO.callbackPage(2) + "_SUCCESS_UP");

                } else {
                    telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Разблокируется при завершении первой недели"));

                    action.setAction(Actions.CALLBACK_PAGE_GO_TO.callbackPage(2) + "_UNSUCCESS_UP");
                }
                action.setUserTelegramId(user.getTelegramId());
                action.setMoneySpent(user.getMoneySpentInDay());
                action.setCurrentStreakDay(user.getCurrentStreakDay());
                action.setUserTelegramNickname(user.getTelegramNickname());
                actionRepository.save(action);

            } else if ("back_to_first_page".equalsIgnoreCase(update.callbackQuery().data())) {

                EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(userIdCallbackQuery, messageId)
                        .replyMarkup(keyboardService.firstInlineKeyboardWithPrizes(userIdCallbackQuery));

                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()));
                telegramBot.execute(editMessageReplyMarkup);

                action.setAction(Actions.CALLBACK_PAGE_GO_TO.callbackPage(1) + "_SUCCESS_UP");
                action.setUserTelegramId(user.getTelegramId());
                action.setMoneySpent(user.getMoneySpentInDay());
                action.setCurrentStreakDay(user.getCurrentStreakDay());
                action.setUserTelegramNickname(user.getTelegramNickname());
                actionRepository.save(action);

            } else if ("up_to_third_page".equalsIgnoreCase(update.callbackQuery().data())) {

                if (user.getCurrentStreakDay() > 13) {

                    EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(userIdCallbackQuery, messageId)
                            .replyMarkup(keyboardService.thirdInlineKeyboardWithPrizes(userIdCallbackQuery));

                    telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()));
                    telegramBot.execute(editMessageReplyMarkup);

                    action.setAction(Actions.CALLBACK_PAGE_GO_TO.callbackPage(3) + "_SUCCESS_UP");

                } else {
                    telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Разблокируется при завершении первой недели"));

                    action.setAction(Actions.CALLBACK_PAGE_GO_TO.callbackPage(3) + "_UNSUCCESS_UP");
                }
                action.setUserTelegramId(user.getTelegramId());
                action.setMoneySpent(user.getMoneySpentInDay());
                action.setCurrentStreakDay(user.getCurrentStreakDay());
                action.setUserTelegramNickname(user.getTelegramNickname());
                actionRepository.save(action);

            } else if ("back_to_second_page".equalsIgnoreCase(update.callbackQuery().data())) {

                EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(userIdCallbackQuery, messageId)
                        .replyMarkup(keyboardService.secondInlineKeyboardWithPrizes(userIdCallbackQuery));

                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()));
                telegramBot.execute(editMessageReplyMarkup);

                action.setAction(Actions.CALLBACK_PAGE_GO_TO.callbackPage(2) + "_SUCCESS_UP");
                action.setUserTelegramId(user.getTelegramId());
                action.setMoneySpent(user.getMoneySpentInDay());
                action.setCurrentStreakDay(user.getCurrentStreakDay());
                action.setUserTelegramNickname(user.getTelegramNickname());
                actionRepository.save(action);

            } else if ("up_to_fourth_page".equalsIgnoreCase(update.callbackQuery().data())) {

                if (user.getCurrentStreakDay() > 20) {
                    EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(userIdCallbackQuery, messageId)
                            .replyMarkup(keyboardService.fourthInlineKeyboardWithPrizes(userIdCallbackQuery));

                    telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()));
                    telegramBot.execute(editMessageReplyMarkup);

                    action.setAction(Actions.CALLBACK_PAGE_GO_TO.callbackPage(4) + "_SUCCESS_UP");

                } else {
                    telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Разблокируется при завершении первой недели"));

                    action.setAction(Actions.CALLBACK_PAGE_GO_TO.callbackPage(4) + "_UNSUCCESS_UP");
                }
                action.setUserTelegramId(user.getTelegramId());
                action.setMoneySpent(user.getMoneySpentInDay());
                action.setCurrentStreakDay(user.getCurrentStreakDay());
                action.setUserTelegramNickname(user.getTelegramNickname());
                actionRepository.save(action);

            } else if ("back_to_third_page".equalsIgnoreCase(update.callbackQuery().data())) {

                EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(userIdCallbackQuery, messageId)
                        .replyMarkup(keyboardService.thirdInlineKeyboardWithPrizes(userIdCallbackQuery));

                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()));
                telegramBot.execute(editMessageReplyMarkup);

                action.setAction(Actions.CALLBACK_PAGE_GO_TO.callbackPage(3) + "_SUCCESS_UP");
                action.setUserTelegramId(user.getTelegramId());
                action.setMoneySpent(user.getMoneySpentInDay());
                action.setCurrentStreakDay(user.getCurrentStreakDay());
                action.setUserTelegramNickname(user.getTelegramNickname());
                actionRepository.save(action);

            } else if ("update".equalsIgnoreCase(update.callbackQuery().data())) {

                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Обновил"));
                EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(userIdCallbackQuery, messageId)
                        .replyMarkup(keyboardService.firstInlineKeyboardWithPrizes(userIdCallbackQuery));

                telegramBot.execute(editMessageReplyMarkup);

                action.setAction(Actions.CALLBACK_UPDATE_PAGE.toString());
                action.setUserTelegramId(user.getTelegramId());
                action.setMoneySpent(user.getMoneySpentInDay());
                action.setCurrentStreakDay(user.getCurrentStreakDay());
                action.setUserTelegramNickname(user.getTelegramNickname());
                actionRepository.save(action);

            } else if ("1".equals(update.callbackQuery().data())) {
                if (!user.getIsZeroingStreak()) {
                    if (!statusPrize.getReceivedPrizeOfDay1()) {
                        if (currentStreakDay == 0) {

                            LocalDate currentDate = LocalDate.now(ZoneId.of("UTC+3"));
                            LocalDate dateGetPreviousPrize = currentDate.plusDays(1);
                            String urlSpending = String.format(gizmoUserSpendingUrl, user.getGizmoId(), currentDate, dateGetPreviousPrize);
                            JsonObject jsonObjectSpending = connectionGizmoService.connectionGet(connectionGizmoService.getToken(), urlSpending);
                            log.info(jsonObjectSpending.toString());

                            float spendingMoneyInDay = 0f;
                            try {
                                spendingMoneyInDay = jsonObjectSpending.getAsJsonArray("result")
                                        .get(0).getAsJsonObject().get("total").getAsFloat();
                            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                                log.info("Ни рубля не потратил :(");
                            }
                            user.setMoneySpentInDay((long) spendingMoneyInDay);

                            if (spendingMoneyInDay >= 100) {

                                System.out.println("Spend money: " + spendingMoneyInDay);

                                statusPrize.setReceivedPrizeOfDay1(true);
                                user.setCurrentStreakDay(currentStreakDay + 1);
                                user.setDateGetPreviousPrize(LocalDateTime.now(ZoneId.of("UTC+3")));
                                user.setPrizeJson(statusPrize);
                                userRepository.save(user);

                                EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(userIdCallbackQuery, messageId)
                                        .replyMarkup(keyboardService.firstInlineKeyboardWithPrizes(userIdCallbackQuery));

                                telegramBot.execute(editMessageReplyMarkup);
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("15 баллов получено"));
                                String urlAward = String.format(awardUser, user.getGizmoName(), 15);
                                connectionGizmoService.connectionPut(connectionGizmoService.getToken(), urlAward);

                            } else {
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Недостаточно потрачено"));
                            }
                        }
                    } else {
                        telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награда была уже получена"));
                    }
                } else {
                    user.setCurrentStreakDay(0L);
                    user.setIsZeroingStreak(false);
                    userRepository.save(user);
                    telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Нажми ещё раз на награду, чтобы получить"));
                }

                action.setAction(Actions.CALLBACK_DAY.callbackDay(1));
                action.setUserTelegramId(user.getTelegramId());
                action.setMoneySpent(user.getMoneySpentInDay());
                action.setCurrentStreakDay(user.getCurrentStreakDay());
                action.setUserTelegramNickname(user.getTelegramNickname());
                actionRepository.save(action);

            } else if ("2".equals(update.callbackQuery().data())) {
                if (!statusPrize.getReceivedPrizeOfDay2()) {
                    if (currentStreakDay == 1) {

                        LocalDate currentDate = LocalDate.now(ZoneId.of("UTC+3"));
                        LocalDate dateGetPreviousPrize = user.getDateGetPreviousPrize().toLocalDate();
                        long betweenDateGetPrevPrizeAndCurrentDate = currentDate.toEpochDay() - dateGetPreviousPrize.toEpochDay();

                        if (betweenDateGetPrevPrizeAndCurrentDate == 1) {

                            String urlSpending = String.format(gizmoUserSpendingUrl, user.getGizmoId(), currentDate, currentDate.plusDays(1));
                            JsonObject jsonObjectSpending = connectionGizmoService.connectionGet(connectionGizmoService.getToken(), urlSpending);
                            log.info(jsonObjectSpending.toString());

                            float spendingMoneyInDay = 0f;
                            try {
                                spendingMoneyInDay = jsonObjectSpending.getAsJsonArray("result")
                                        .get(0).getAsJsonObject().get("total").getAsFloat();
                            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                                log.info("Ни рубля не потратил :(");
                            }
                            user.setMoneySpentInDay((long) spendingMoneyInDay);
                            System.out.println("Spend money: " + spendingMoneyInDay);

                            if (spendingMoneyInDay >= 100) {

                                statusPrize.setReceivedPrizeOfDay2(true);
                                user.setCurrentStreakDay(currentStreakDay + 1);
                                user.setDateGetPreviousPrize(LocalDateTime.now(ZoneId.of("UTC+3")));
                                user.setPrizeJson(statusPrize);
                                userRepository.save(user);

                                EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(userIdCallbackQuery, messageId)
                                        .replyMarkup(keyboardService.firstInlineKeyboardWithPrizes(userIdCallbackQuery));

                                telegramBot.execute(editMessageReplyMarkup);
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("20 баллов получено"));
                                String urlAward = String.format(awardUser, user.getGizmoName(), 20);
                                connectionGizmoService.connectionPut(connectionGizmoService.getToken(), urlAward);

                            } else {
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Недостаточно потрачено денег"));
                            }
                        } else if (betweenDateGetPrevPrizeAndCurrentDate == 0) {
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награду можно получить завтра"));
                        } else {
                            user.setIsZeroingStreak(true);
                            user.setPrizeJson(new PrizeJson());
                            userRepository.save(user);
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ты пропустил 1 день. Нажми на \uD83D\uDD01"));
                        }
                    } else {
                        telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ещё рано"));
                    }
                } else {
                    telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награда была уже получена"));
                }

                action.setAction(Actions.CALLBACK_DAY.callbackDay(2));
                action.setUserTelegramId(user.getTelegramId());
                action.setMoneySpent(user.getMoneySpentInDay());
                action.setCurrentStreakDay(user.getCurrentStreakDay());
                action.setUserTelegramNickname(user.getTelegramNickname());
                actionRepository.save(action);

            } else if ("3".equals(update.callbackQuery().data())) {
                if (!statusPrize.getReceivedPrizeOfDay3()) {
                    if (currentStreakDay == 2) {

                        LocalDate currentDate = LocalDate.now(ZoneId.of("UTC+3"));
                        LocalDate dateGetPreviousPrize = user.getDateGetPreviousPrize().toLocalDate();
                        long betweenDateGetPrevPrizeAndCurrentDate = currentDate.toEpochDay() - dateGetPreviousPrize.toEpochDay();

                        if (betweenDateGetPrevPrizeAndCurrentDate == 1) {

                            String urlSpending = String.format(gizmoUserSpendingUrl, user.getGizmoId(), currentDate, currentDate.plusDays(1));
                            JsonObject jsonObjectSpending = connectionGizmoService.connectionGet(connectionGizmoService.getToken(), urlSpending);
                            log.info(jsonObjectSpending.toString());

                            float spendingMoneyInDay = 0f;
                            try {
                                spendingMoneyInDay = jsonObjectSpending.getAsJsonArray("result")
                                        .get(0).getAsJsonObject().get("total").getAsFloat();
                            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                                log.info("Ни рубля не потратил :(");
                            }
                            user.setMoneySpentInDay((long) spendingMoneyInDay);
                            System.out.println("Spend money: " + spendingMoneyInDay);

                            if (spendingMoneyInDay >= 100) {

                                statusPrize.setReceivedPrizeOfDay3(true);
                                user.setCurrentStreakDay(currentStreakDay + 1);
                                user.setDateGetPreviousPrize(LocalDateTime.now(ZoneId.of("UTC+3")));
                                user.setPrizeJson(statusPrize);
                                userRepository.save(user);

                                EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(userIdCallbackQuery, messageId)
                                        .replyMarkup(keyboardService.firstInlineKeyboardWithPrizes(userIdCallbackQuery));

                                telegramBot.execute(editMessageReplyMarkup);
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награда получена!"));
                                telegramBot.execute(new SendMessage(user.getTelegramId(), TEXT_PACKAGE_TIME_GET +
                                        LocalDate.now(ZoneId.of("UTC+3")) + " 23:59"));

                            } else {
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Недостаточно потрачено денег"));
                            }
                        } else if (betweenDateGetPrevPrizeAndCurrentDate == 0) {
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награду можно получить завтра"));
                        } else {
                            user.setIsZeroingStreak(true);
                            user.setPrizeJson(new PrizeJson());
                            userRepository.save(user);
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ты пропустил 1 день"));
                        }
                    } else {
                        telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ещё рано"));
                    }
                } else {
                    telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награда была уже получена"));
                }

                action.setAction(Actions.CALLBACK_DAY.callbackDay(3));
                action.setUserTelegramId(user.getTelegramId());
                action.setMoneySpent(user.getMoneySpentInDay());
                action.setCurrentStreakDay(user.getCurrentStreakDay());
                action.setUserTelegramNickname(user.getTelegramNickname());
                actionRepository.save(action);

            } else if ("4".equals(update.callbackQuery().data())) {
                if (!statusPrize.getReceivedPrizeOfDay4()) {
                    if (currentStreakDay == 3) {

                        LocalDate currentDate = LocalDate.now(ZoneId.of("UTC+3"));
                        LocalDate dateGetPreviousPrize = user.getDateGetPreviousPrize().toLocalDate();
                        long betweenDateGetPrevPrizeAndCurrentDate = currentDate.toEpochDay() - dateGetPreviousPrize.toEpochDay();

                        if (betweenDateGetPrevPrizeAndCurrentDate == 1) {

                            String urlSpending = String.format(gizmoUserSpendingUrl, user.getGizmoId(), currentDate, currentDate.plusDays(1));
                            JsonObject jsonObjectSpending = connectionGizmoService.connectionGet(connectionGizmoService.getToken(), urlSpending);
                            log.info(jsonObjectSpending.toString());

                            float spendingMoneyInDay = 0f;
                            try {
                                spendingMoneyInDay = jsonObjectSpending.getAsJsonArray("result")
                                        .get(0).getAsJsonObject().get("total").getAsFloat();
                            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                                log.info("Ни рубля не потратил :(");
                            }
                            user.setMoneySpentInDay((long) spendingMoneyInDay);
                            System.out.println("Spend money: " + spendingMoneyInDay);

                            if (spendingMoneyInDay >= 100) {

                                statusPrize.setReceivedPrizeOfDay4(true);
                                user.setCurrentStreakDay(currentStreakDay + 1);
                                user.setDateGetPreviousPrize(LocalDateTime.now(ZoneId.of("UTC+3")));
                                user.setPrizeJson(statusPrize);
                                userRepository.save(user);

                                EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(userIdCallbackQuery, messageId)
                                        .replyMarkup(keyboardService.firstInlineKeyboardWithPrizes(userIdCallbackQuery));

                                telegramBot.execute(editMessageReplyMarkup);
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("25 баллов получено"));
                                String urlAward = String.format(awardUser, user.getGizmoName(), 25);
                                connectionGizmoService.connectionPut(connectionGizmoService.getToken(), urlAward);

                            } else {
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Недостаточно потрачено денег"));
                            }
                        } else if (betweenDateGetPrevPrizeAndCurrentDate == 0) {
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награду можно получить завтра"));
                        } else {
                            user.setIsZeroingStreak(true);
                            user.setPrizeJson(new PrizeJson());
                            userRepository.save(user);
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ты пропустил 1 день"));
                        }
                    } else {
                        telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ещё рано"));
                    }
                } else {
                    telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награда была уже получена"));
                }

                action.setAction(Actions.CALLBACK_DAY.callbackDay(4));
                action.setUserTelegramId(user.getTelegramId());
                action.setMoneySpent(user.getMoneySpentInDay());
                action.setCurrentStreakDay(user.getCurrentStreakDay());
                action.setUserTelegramNickname(user.getTelegramNickname());
                actionRepository.save(action);

            } else if ("5".equals(update.callbackQuery().data())) {
                if (!statusPrize.getReceivedPrizeOfDay5()) {
                    if (currentStreakDay == 4) {

                        LocalDate currentDate = LocalDate.now(ZoneId.of("UTC+3"));
                        LocalDate dateGetPreviousPrize = user.getDateGetPreviousPrize().toLocalDate();
                        long betweenDateGetPrevPrizeAndCurrentDate = currentDate.toEpochDay() - dateGetPreviousPrize.toEpochDay();

                        if (betweenDateGetPrevPrizeAndCurrentDate == 1) {

                            String urlSpending = String.format(gizmoUserSpendingUrl, user.getGizmoId(), currentDate, currentDate.plusDays(1));
                            JsonObject jsonObjectSpending = connectionGizmoService.connectionGet(connectionGizmoService.getToken(), urlSpending);
                            log.info(jsonObjectSpending.toString());

                            float spendingMoneyInDay = 0f;
                            try {
                                spendingMoneyInDay = jsonObjectSpending.getAsJsonArray("result")
                                        .get(0).getAsJsonObject().get("total").getAsFloat();
                            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                                log.info("Ни рубля не потратил :(");
                            }
                            user.setMoneySpentInDay((long) spendingMoneyInDay);
                            System.out.println("Spend money: " + spendingMoneyInDay);

                            if (spendingMoneyInDay >= 100) {

                                statusPrize.setReceivedPrizeOfDay5(true);
                                user.setCurrentStreakDay(currentStreakDay + 1);
                                user.setDateGetPreviousPrize(LocalDateTime.now(ZoneId.of("UTC+3")));
                                user.setPrizeJson(statusPrize);
                                userRepository.save(user);

                                EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(userIdCallbackQuery, messageId)
                                        .replyMarkup(keyboardService.firstInlineKeyboardWithPrizes(userIdCallbackQuery));

                                telegramBot.execute(editMessageReplyMarkup);
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("30 баллов получено"));
                                String urlAward = String.format(awardUser, user.getGizmoName(), 30);
                                connectionGizmoService.connectionPut(connectionGizmoService.getToken(), urlAward);

                            } else {
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Недостаточно потрачено денег"));
                            }
                        } else if (betweenDateGetPrevPrizeAndCurrentDate == 0) {
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награду можно получить завтра"));
                        } else {
                            user.setIsZeroingStreak(true);
                            user.setPrizeJson(new PrizeJson());
                            userRepository.save(user);
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ты пропустил 1 день"));
                        }
                    } else {
                        telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ещё рано"));
                    }
                } else {
                    telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награда была уже получена"));
                }

                action.setAction(Actions.CALLBACK_DAY.callbackDay(5));
                action.setUserTelegramId(user.getTelegramId());
                action.setMoneySpent(user.getMoneySpentInDay());
                action.setCurrentStreakDay(user.getCurrentStreakDay());
                action.setUserTelegramNickname(user.getTelegramNickname());
                actionRepository.save(action);

            } else if ("6".equals(update.callbackQuery().data())) {
                if (!statusPrize.getReceivedPrizeOfDay6()) {
                    if (currentStreakDay == 5) {

                        LocalDate currentDate = LocalDate.now(ZoneId.of("UTC+3"));
                        LocalDate dateGetPreviousPrize = user.getDateGetPreviousPrize().toLocalDate();
                        long betweenDateGetPrevPrizeAndCurrentDate = currentDate.toEpochDay() - dateGetPreviousPrize.toEpochDay();

                        if (betweenDateGetPrevPrizeAndCurrentDate == 1) {

                            String urlSpending = String.format(gizmoUserSpendingUrl, user.getGizmoId(), currentDate, currentDate.plusDays(1));
                            JsonObject jsonObjectSpending = connectionGizmoService.connectionGet(connectionGizmoService.getToken(), urlSpending);
                            log.info(jsonObjectSpending.toString());

                            float spendingMoneyInDay = 0f;
                            try {
                                spendingMoneyInDay = jsonObjectSpending.getAsJsonArray("result")
                                        .get(0).getAsJsonObject().get("total").getAsFloat();
                            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                                log.info("Ни рубля не потратил :(");
                            }
                            user.setMoneySpentInDay((long) spendingMoneyInDay);
                            System.out.println("Spend money: " + spendingMoneyInDay);

                            if (spendingMoneyInDay >= 100) {

                                statusPrize.setReceivedPrizeOfDay6(true);
                                user.setCurrentStreakDay(currentStreakDay + 1);
                                user.setDateGetPreviousPrize(LocalDateTime.now(ZoneId.of("UTC+3")));
                                user.setPrizeJson(statusPrize);
                                userRepository.save(user);

                                EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(userIdCallbackQuery, messageId)
                                        .replyMarkup(keyboardService.firstInlineKeyboardWithPrizes(userIdCallbackQuery));

                                telegramBot.execute(editMessageReplyMarkup);
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("35 баллов получено"));
                                String urlAward = String.format(awardUser, user.getGizmoName(), 35);
                                connectionGizmoService.connectionPut(connectionGizmoService.getToken(), urlAward);

                            } else {
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Недостаточно потрачено денег"));
                            }
                        } else if (betweenDateGetPrevPrizeAndCurrentDate == 0) {
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награду можно получить завтра"));
                        } else {
                            user.setIsZeroingStreak(true);
                            user.setPrizeJson(new PrizeJson());
                            userRepository.save(user);
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ты пропустил 1 день"));
                        }
                    } else {
                        telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ещё рано"));
                    }
                } else {
                    telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награда была уже получена"));
                }

                action.setAction(Actions.CALLBACK_DAY.callbackDay(6));
                action.setUserTelegramId(user.getTelegramId());
                action.setMoneySpent(user.getMoneySpentInDay());
                action.setCurrentStreakDay(user.getCurrentStreakDay());
                action.setUserTelegramNickname(user.getTelegramNickname());
                actionRepository.save(action);

            } else if ("7".equals(update.callbackQuery().data())) {
                if (!statusPrize.getReceivedPrizeOfDay7()) {
                    if (currentStreakDay == 6) {

                        LocalDate currentDate = LocalDate.now(ZoneId.of("UTC+3"));
                        LocalDate dateGetPreviousPrize = user.getDateGetPreviousPrize().toLocalDate();
                        long betweenDateGetPrevPrizeAndCurrentDate = currentDate.toEpochDay() - dateGetPreviousPrize.toEpochDay();

                        if (betweenDateGetPrevPrizeAndCurrentDate == 1) {

                            String urlSpending = String.format(gizmoUserSpendingUrl, user.getGizmoId(), currentDate, currentDate.plusDays(1));
                            JsonObject jsonObjectSpending = connectionGizmoService.connectionGet(connectionGizmoService.getToken(), urlSpending);
                            log.info(jsonObjectSpending.toString());

                            float spendingMoneyInDay = 0f;
                            try {
                                spendingMoneyInDay = jsonObjectSpending.getAsJsonArray("result")
                                        .get(0).getAsJsonObject().get("total").getAsFloat();
                            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                                log.info("Ни рубля не потратил :(");
                            }
                            user.setMoneySpentInDay((long) spendingMoneyInDay);
                            System.out.println("Spend money: " + spendingMoneyInDay);

                            if (spendingMoneyInDay >= 100) {

                                statusPrize.setReceivedPrizeOfDay7(true);
                                user.setCurrentStreakDay(currentStreakDay + 1);
                                user.setDateGetPreviousPrize(LocalDateTime.now(ZoneId.of("UTC+3")));
                                user.setPrizeJson(statusPrize);
                                userRepository.save(user);

                                EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(userIdCallbackQuery, messageId)
                                        .replyMarkup(keyboardService.firstInlineKeyboardWithPrizes(userIdCallbackQuery));

                                telegramBot.execute(editMessageReplyMarkup);
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награда получена"));
                                telegramBot.execute(new SendMessage(user.getTelegramId(), TEXT_PACKAGE_TIME_GET +
                                        LocalDate.now(ZoneId.of("UTC+3")) + " 23:59"));

                            } else {
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Недостаточно потрачено денег"));
                            }
                        } else if (betweenDateGetPrevPrizeAndCurrentDate == 0) {
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награду можно получить завтра"));
                        } else {
                            user.setIsZeroingStreak(true);
                            user.setPrizeJson(new PrizeJson());
                            userRepository.save(user);
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ты пропустил 1 день"));
                        }
                    } else {
                        telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ещё рано"));
                    }
                } else {
                    telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награда была уже получена"));
                }

                action.setAction(Actions.CALLBACK_DAY.callbackDay(7));
                action.setUserTelegramId(user.getTelegramId());
                action.setMoneySpent(user.getMoneySpentInDay());
                action.setCurrentStreakDay(user.getCurrentStreakDay());
                action.setUserTelegramNickname(user.getTelegramNickname());
                actionRepository.save(action);

            } else if ("8".equals(update.callbackQuery().data())) {
                if (!statusPrize.getReceivedPrizeOfDay8()) {
                    if (currentStreakDay == 7) {

                        LocalDate currentDate = LocalDate.now(ZoneId.of("UTC+3"));
                        LocalDate dateGetPreviousPrize = user.getDateGetPreviousPrize().toLocalDate();
                        long betweenDateGetPrevPrizeAndCurrentDate = currentDate.toEpochDay() - dateGetPreviousPrize.toEpochDay();

                        if (betweenDateGetPrevPrizeAndCurrentDate == 1) {

                            String urlSpending = String.format(gizmoUserSpendingUrl, user.getGizmoId(), currentDate, currentDate.plusDays(1));
                            JsonObject jsonObjectSpending = connectionGizmoService.connectionGet(connectionGizmoService.getToken(), urlSpending);
                            log.info(jsonObjectSpending.toString());

                            float spendingMoneyInDay = 0f;
                            try {
                                spendingMoneyInDay = jsonObjectSpending.getAsJsonArray("result")
                                        .get(0).getAsJsonObject().get("total").getAsFloat();
                            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                                log.info("Ни рубля не потратил :(");
                            }
                            user.setMoneySpentInDay((long) spendingMoneyInDay);
                            System.out.println("Spend money: " + spendingMoneyInDay);

                            if (spendingMoneyInDay >= 100) {

                                statusPrize.setReceivedPrizeOfDay8(true);
                                user.setCurrentStreakDay(currentStreakDay + 1);
                                user.setDateGetPreviousPrize(LocalDateTime.now(ZoneId.of("UTC+3")));
                                user.setPrizeJson(statusPrize);
                                userRepository.save(user);

                                EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(userIdCallbackQuery, messageId)
                                        .replyMarkup(keyboardService.secondInlineKeyboardWithPrizes(userIdCallbackQuery));

                                telegramBot.execute(editMessageReplyMarkup);
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("20 баллов получено"));
                                String urlAward = String.format(awardUser, user.getGizmoName(), 20);
                                connectionGizmoService.connectionPut(connectionGizmoService.getToken(), urlAward);

                            } else {
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Недостаточно потрачено денег"));
                            }
                        } else if (betweenDateGetPrevPrizeAndCurrentDate == 0) {
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награду можно получить завтра"));
                        } else {
                            user.setIsZeroingStreak(true);
                            user.setPrizeJson(new PrizeJson());
                            userRepository.save(user);
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ты пропустил 1 день"));
                        }
                    } else {
                        telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ещё рано"));
                    }
                } else {
                    telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награда была уже получена"));
                }

                action.setAction(Actions.CALLBACK_DAY.callbackDay(8));
                action.setUserTelegramId(user.getTelegramId());
                action.setMoneySpent(user.getMoneySpentInDay());
                action.setCurrentStreakDay(user.getCurrentStreakDay());
                action.setUserTelegramNickname(user.getTelegramNickname());
                actionRepository.save(action);

            } else if ("9".equals(update.callbackQuery().data())) {
                if (!statusPrize.getReceivedPrizeOfDay9()) {
                    if (currentStreakDay == 8) {

                        LocalDate currentDate = LocalDate.now(ZoneId.of("UTC+3"));
                        LocalDate dateGetPreviousPrize = user.getDateGetPreviousPrize().toLocalDate();
                        long betweenDateGetPrevPrizeAndCurrentDate = currentDate.toEpochDay() - dateGetPreviousPrize.toEpochDay();

                        if (betweenDateGetPrevPrizeAndCurrentDate == 1) {

                            String urlSpending = String.format(gizmoUserSpendingUrl, user.getGizmoId(), currentDate, currentDate.plusDays(1));
                            JsonObject jsonObjectSpending = connectionGizmoService.connectionGet(connectionGizmoService.getToken(), urlSpending);
                            log.info(jsonObjectSpending.toString());

                            float spendingMoneyInDay = 0f;
                            try {
                                spendingMoneyInDay = jsonObjectSpending.getAsJsonArray("result")
                                        .get(0).getAsJsonObject().get("total").getAsFloat();
                            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                                log.info("Ни рубля не потратил :(");
                            }
                            user.setMoneySpentInDay((long) spendingMoneyInDay);
                            System.out.println("Spend money: " + spendingMoneyInDay);

                            if (spendingMoneyInDay >= 100) {

                                statusPrize.setReceivedPrizeOfDay9(true);
                                user.setCurrentStreakDay(currentStreakDay + 1);
                                user.setDateGetPreviousPrize(LocalDateTime.now(ZoneId.of("UTC+3")));
                                user.setPrizeJson(statusPrize);
                                userRepository.save(user);

                                EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(userIdCallbackQuery, messageId)
                                        .replyMarkup(keyboardService.secondInlineKeyboardWithPrizes(userIdCallbackQuery));

                                telegramBot.execute(editMessageReplyMarkup);
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("25 баллов получено"));
                                String urlAward = String.format(awardUser, user.getGizmoName(), 25);
                                connectionGizmoService.connectionPut(connectionGizmoService.getToken(), urlAward);

                            } else {
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Недостаточно потрачено денег"));
                            }
                        } else if (betweenDateGetPrevPrizeAndCurrentDate == 0) {
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награду можно получить завтра"));
                        } else {
                            user.setIsZeroingStreak(true);
                            user.setPrizeJson(new PrizeJson());
                            userRepository.save(user);
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ты пропустил 1 день"));
                        }
                    } else {
                        telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("||Ещё рано||"));
                    }
                } else {
                    telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награда была уже получена"));
                }

                action.setAction(Actions.CALLBACK_DAY.callbackDay(9));
                action.setUserTelegramId(user.getTelegramId());
                action.setMoneySpent(user.getMoneySpentInDay());
                action.setCurrentStreakDay(user.getCurrentStreakDay());
                action.setUserTelegramNickname(user.getTelegramNickname());
                actionRepository.save(action);

            } else if ("10".equals(update.callbackQuery().data())) {
                if (!statusPrize.getReceivedPrizeOfDay10()) {
                    if (currentStreakDay == 9) {

                        LocalDate currentDate = LocalDate.now(ZoneId.of("UTC+3"));
                        LocalDate dateGetPreviousPrize = user.getDateGetPreviousPrize().toLocalDate();
                        long betweenDateGetPrevPrizeAndCurrentDate = currentDate.toEpochDay() - dateGetPreviousPrize.toEpochDay();

                        if (betweenDateGetPrevPrizeAndCurrentDate == 1) {

                            String urlSpending = String.format(gizmoUserSpendingUrl, user.getGizmoId(), currentDate, currentDate.plusDays(1));
                            JsonObject jsonObjectSpending = connectionGizmoService.connectionGet(connectionGizmoService.getToken(), urlSpending);
                            log.info(jsonObjectSpending.toString());

                            float spendingMoneyInDay = 0f;
                            try {
                                spendingMoneyInDay = jsonObjectSpending.getAsJsonArray("result")
                                        .get(0).getAsJsonObject().get("total").getAsFloat();
                            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                                log.info("Ни рубля не потратил :(");
                            }
                            user.setMoneySpentInDay((long) spendingMoneyInDay);
                            System.out.println("Spend money: " + spendingMoneyInDay);

                            if (spendingMoneyInDay >= 100) {

                                statusPrize.setReceivedPrizeOfDay10(true);
                                user.setCurrentStreakDay(currentStreakDay + 1);
                                user.setDateGetPreviousPrize(LocalDateTime.now(ZoneId.of("UTC+3")));
                                user.setPrizeJson(statusPrize);
                                userRepository.save(user);

                                EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(userIdCallbackQuery, messageId)
                                        .replyMarkup(keyboardService.secondInlineKeyboardWithPrizes(userIdCallbackQuery));

                                telegramBot.execute(editMessageReplyMarkup);
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награда получена"));
                                telegramBot.execute(new SendMessage(user.getTelegramId(), TEXT_PACKAGE_TIME_GET +
                                        LocalDate.now(ZoneId.of("UTC+3")) + " 23:59"));

                            } else {
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Недостаточно потрачено денег"));
                            }
                        } else if (betweenDateGetPrevPrizeAndCurrentDate == 0) {
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награду можно получить завтра"));
                        } else {
                            user.setIsZeroingStreak(true);
                            user.setPrizeJson(new PrizeJson());
                            userRepository.save(user);
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ты пропустил 1 день"));
                        }
                    } else {
                        telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ещё рано"));
                    }
                } else {
                    telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награда была уже получена"));
                }

                action.setAction(Actions.CALLBACK_DAY.callbackDay(10));
                action.setUserTelegramId(user.getTelegramId());
                action.setMoneySpent(user.getMoneySpentInDay());
                action.setCurrentStreakDay(user.getCurrentStreakDay());
                action.setUserTelegramNickname(user.getTelegramNickname());
                actionRepository.save(action);

            } else if ("11".equals(update.callbackQuery().data())) {
                if (!statusPrize.getReceivedPrizeOfDay11()) {
                    if (currentStreakDay == 10) {

                        LocalDate currentDate = LocalDate.now(ZoneId.of("UTC+3"));
                        LocalDate dateGetPreviousPrize = user.getDateGetPreviousPrize().toLocalDate();
                        long betweenDateGetPrevPrizeAndCurrentDate = currentDate.toEpochDay() - dateGetPreviousPrize.toEpochDay();

                        if (betweenDateGetPrevPrizeAndCurrentDate == 1) {

                            String urlSpending = String.format(gizmoUserSpendingUrl, user.getGizmoId(), currentDate, currentDate.plusDays(1));
                            JsonObject jsonObjectSpending = connectionGizmoService.connectionGet(connectionGizmoService.getToken(), urlSpending);
                            log.info(jsonObjectSpending.toString());

                            float spendingMoneyInDay = 0f;
                            try {
                                spendingMoneyInDay = jsonObjectSpending.getAsJsonArray("result")
                                        .get(0).getAsJsonObject().get("total").getAsFloat();
                            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                                log.info("Ни рубля не потратил :(");
                            }
                            user.setMoneySpentInDay((long) spendingMoneyInDay);
                            System.out.println("Spend money: " + spendingMoneyInDay);

                            if (spendingMoneyInDay >= 100) {

                                statusPrize.setReceivedPrizeOfDay11(true);
                                user.setCurrentStreakDay(currentStreakDay + 1);
                                user.setDateGetPreviousPrize(LocalDateTime.now(ZoneId.of("UTC+3")));
                                user.setPrizeJson(statusPrize);
                                userRepository.save(user);

                                EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(userIdCallbackQuery, messageId)
                                        .replyMarkup(keyboardService.secondInlineKeyboardWithPrizes(userIdCallbackQuery));

                                telegramBot.execute(editMessageReplyMarkup);
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("30 баллов получено"));
                                telegramBot.execute(new SendMessage(user.getTelegramId(), TEXT_PACKAGE_TIME_GET +
                                        LocalDate.now(ZoneId.of("UTC+3")) + " 23:59"));

                            } else {
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Недостаточно потрачено денег"));
                            }
                        } else if (betweenDateGetPrevPrizeAndCurrentDate == 0) {
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награду можно получить завтра"));
                        } else {
                            user.setIsZeroingStreak(true);
                            user.setPrizeJson(new PrizeJson());
                            userRepository.save(user);
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ты пропустил 1 день"));
                        }
                    } else {
                        telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ещё рано"));
                    }
                } else {
                    telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награда была уже получена"));
                }

                action.setAction(Actions.CALLBACK_DAY.callbackDay(11));
                action.setUserTelegramId(user.getTelegramId());
                action.setMoneySpent(user.getMoneySpentInDay());
                action.setCurrentStreakDay(user.getCurrentStreakDay());
                action.setUserTelegramNickname(user.getTelegramNickname());
                actionRepository.save(action);

            } else if ("12".equals(update.callbackQuery().data())) {
                if (!statusPrize.getReceivedPrizeOfDay12()) {
                    if (currentStreakDay == 11) {

                        LocalDate currentDate = LocalDate.now(ZoneId.of("UTC+3"));
                        LocalDate dateGetPreviousPrize = user.getDateGetPreviousPrize().toLocalDate();
                        long betweenDateGetPrevPrizeAndCurrentDate = currentDate.toEpochDay() - dateGetPreviousPrize.toEpochDay();

                        if (betweenDateGetPrevPrizeAndCurrentDate == 1) {

                            String urlSpending = String.format(gizmoUserSpendingUrl, user.getGizmoId(), currentDate, currentDate.plusDays(1));
                            JsonObject jsonObjectSpending = connectionGizmoService.connectionGet(connectionGizmoService.getToken(), urlSpending);
                            log.info(jsonObjectSpending.toString());

                            float spendingMoneyInDay = 0f;
                            try {
                                spendingMoneyInDay = jsonObjectSpending.getAsJsonArray("result")
                                        .get(0).getAsJsonObject().get("total").getAsFloat();
                            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                                log.info("Ни рубля не потратил :(");
                            }
                            user.setMoneySpentInDay((long) spendingMoneyInDay);
                            System.out.println("Spend money: " + spendingMoneyInDay);

                            if (spendingMoneyInDay >= 100) {

                                statusPrize.setReceivedPrizeOfDay12(true);
                                user.setCurrentStreakDay(currentStreakDay + 1);
                                user.setDateGetPreviousPrize(LocalDateTime.now(ZoneId.of("UTC+3")));
                                user.setPrizeJson(statusPrize);
                                userRepository.save(user);

                                EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(userIdCallbackQuery, messageId)
                                        .replyMarkup(keyboardService.secondInlineKeyboardWithPrizes(userIdCallbackQuery));

                                telegramBot.execute(editMessageReplyMarkup);
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("35 баллов получено"));
                                String urlAward = String.format(awardUser, user.getGizmoName(), 35);
                                connectionGizmoService.connectionPut(connectionGizmoService.getToken(), urlAward);

                            } else {
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Недостаточно потрачено денег"));
                            }
                        } else if (betweenDateGetPrevPrizeAndCurrentDate == 0) {
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награду можно получить завтра"));
                        } else {
                            user.setIsZeroingStreak(true);
                            user.setPrizeJson(new PrizeJson());
                            userRepository.save(user);
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ты пропустил 1 день"));
                        }
                    } else {
                        telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ещё рано"));
                    }
                } else {
                    telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награда была уже получена"));
                }

                action.setAction(Actions.CALLBACK_DAY.callbackDay(12));
                action.setUserTelegramId(user.getTelegramId());
                action.setMoneySpent(user.getMoneySpentInDay());
                action.setCurrentStreakDay(user.getCurrentStreakDay());
                action.setUserTelegramNickname(user.getTelegramNickname());
                actionRepository.save(action);

            } else if ("13".equals(update.callbackQuery().data())) {
                if (!statusPrize.getReceivedPrizeOfDay13()) {
                    if (currentStreakDay == 12) {

                        LocalDate currentDate = LocalDate.now(ZoneId.of("UTC+3"));
                        LocalDate dateGetPreviousPrize = user.getDateGetPreviousPrize().toLocalDate();
                        long betweenDateGetPrevPrizeAndCurrentDate = currentDate.toEpochDay() - dateGetPreviousPrize.toEpochDay();

                        if (betweenDateGetPrevPrizeAndCurrentDate == 1) {

                            String urlSpending = String.format(gizmoUserSpendingUrl, user.getGizmoId(), currentDate, currentDate.plusDays(1));
                            JsonObject jsonObjectSpending = connectionGizmoService.connectionGet(connectionGizmoService.getToken(), urlSpending);
                            log.info(jsonObjectSpending.toString());

                            float spendingMoneyInDay = 0f;
                            try {
                                spendingMoneyInDay = jsonObjectSpending.getAsJsonArray("result")
                                        .get(0).getAsJsonObject().get("total").getAsFloat();
                            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                                log.info("Ни рубля не потратил :(");
                            }
                            user.setMoneySpentInDay((long) spendingMoneyInDay);
                            System.out.println("Spend money: " + spendingMoneyInDay);

                            if (spendingMoneyInDay >= 100) {

                                statusPrize.setReceivedPrizeOfDay13(true);
                                user.setCurrentStreakDay(currentStreakDay + 1);
                                user.setDateGetPreviousPrize(LocalDateTime.now(ZoneId.of("UTC+3")));
                                user.setPrizeJson(statusPrize);
                                userRepository.save(user);

                                EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(userIdCallbackQuery, messageId)
                                        .replyMarkup(keyboardService.secondInlineKeyboardWithPrizes(userIdCallbackQuery));

                                telegramBot.execute(editMessageReplyMarkup);
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("40 баллов получено"));
                                String urlAward = String.format(awardUser, user.getGizmoName(), 40);
                                connectionGizmoService.connectionPut(connectionGizmoService.getToken(), urlAward);

                            } else {
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Недостаточно потрачено денег"));
                            }
                        } else if (betweenDateGetPrevPrizeAndCurrentDate == 0) {
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награду можно получить завтра"));
                        } else {
                            user.setIsZeroingStreak(true);
                            user.setPrizeJson(new PrizeJson());
                            userRepository.save(user);
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ты пропустил 1 день"));
                        }
                    } else {
                        telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ещё рано"));
                    }
                } else {
                    telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награда была уже получена"));
                }

                action.setAction(Actions.CALLBACK_DAY.callbackDay(13));
                action.setUserTelegramId(user.getTelegramId());
                action.setMoneySpent(user.getMoneySpentInDay());
                action.setCurrentStreakDay(user.getCurrentStreakDay());
                action.setUserTelegramNickname(user.getTelegramNickname());
                actionRepository.save(action);

            } else if ("14".equals(update.callbackQuery().data())) {
                if (!statusPrize.getReceivedPrizeOfDay14()) {
                    if (currentStreakDay == 13) {

                        LocalDate currentDate = LocalDate.now(ZoneId.of("UTC+3"));
                        LocalDate dateGetPreviousPrize = user.getDateGetPreviousPrize().toLocalDate();
                        long betweenDateGetPrevPrizeAndCurrentDate = currentDate.toEpochDay() - dateGetPreviousPrize.toEpochDay();

                        if (betweenDateGetPrevPrizeAndCurrentDate == 1) {

                            String urlSpending = String.format(gizmoUserSpendingUrl, user.getGizmoId(), currentDate, currentDate.plusDays(1));
                            JsonObject jsonObjectSpending = connectionGizmoService.connectionGet(connectionGizmoService.getToken(), urlSpending);
                            log.info(jsonObjectSpending.toString());

                            float spendingMoneyInDay = 0f;
                            try {
                                spendingMoneyInDay = jsonObjectSpending.getAsJsonArray("result")
                                        .get(0).getAsJsonObject().get("total").getAsFloat();
                            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                                log.info("Ни рубля не потратил :(");
                            }
                            user.setMoneySpentInDay((long) spendingMoneyInDay);
                            System.out.println("Spend money: " + spendingMoneyInDay);

                            if (spendingMoneyInDay >= 100) {

                                statusPrize.setReceivedPrizeOfDay14(true);
                                user.setCurrentStreakDay(currentStreakDay + 1);
                                user.setDateGetPreviousPrize(LocalDateTime.now(ZoneId.of("UTC+3")));
                                user.setPrizeJson(statusPrize);
                                userRepository.save(user);

                                EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(userIdCallbackQuery, messageId)
                                        .replyMarkup(keyboardService.secondInlineKeyboardWithPrizes(userIdCallbackQuery));

                                telegramBot.execute(editMessageReplyMarkup);
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награда получена"));
                                telegramBot.execute(new SendMessage(user.getTelegramId(), TEXT_PACKAGE_TIME_GET +
                                        LocalDate.now(ZoneId.of("UTC+3")) + " 23:59"));

                            } else {
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Недостаточно потрачено денег"));
                            }
                        } else if (betweenDateGetPrevPrizeAndCurrentDate == 0) {
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награду можно получить завтра"));
                        } else {
                            user.setIsZeroingStreak(true);
                            user.setPrizeJson(new PrizeJson());
                            userRepository.save(user);
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ты пропустил 1 день"));
                        }
                    } else {
                        telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ещё рано"));
                    }
                } else {
                    telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награда была уже получена"));
                }

                action.setAction(Actions.CALLBACK_DAY.callbackDay(14));
                action.setUserTelegramId(user.getTelegramId());
                action.setMoneySpent(user.getMoneySpentInDay());
                action.setCurrentStreakDay(user.getCurrentStreakDay());
                action.setUserTelegramNickname(user.getTelegramNickname());
                actionRepository.save(action);

            } else if ("15".equals(update.callbackQuery().data())) {
                if (!statusPrize.getReceivedPrizeOfDay15()) {
                    if (currentStreakDay == 14) {

                        LocalDate currentDate = LocalDate.now(ZoneId.of("UTC+3"));
                        LocalDate dateGetPreviousPrize = user.getDateGetPreviousPrize().toLocalDate();
                        long betweenDateGetPrevPrizeAndCurrentDate = currentDate.toEpochDay() - dateGetPreviousPrize.toEpochDay();

                        if (betweenDateGetPrevPrizeAndCurrentDate == 1) {

                            String urlSpending = String.format(gizmoUserSpendingUrl, user.getGizmoId(), currentDate, currentDate.plusDays(1));
                            JsonObject jsonObjectSpending = connectionGizmoService.connectionGet(connectionGizmoService.getToken(), urlSpending);
                            log.info(jsonObjectSpending.toString());

                            float spendingMoneyInDay = 0f;
                            try {
                                spendingMoneyInDay = jsonObjectSpending.getAsJsonArray("result")
                                        .get(0).getAsJsonObject().get("total").getAsFloat();
                            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                                log.info("Ни рубля не потратил :(");
                            }
                            user.setMoneySpentInDay((long) spendingMoneyInDay);
                            System.out.println("Spend money: " + spendingMoneyInDay);

                            if (spendingMoneyInDay >= 100) {

                                statusPrize.setReceivedPrizeOfDay15(true);
                                user.setCurrentStreakDay(currentStreakDay + 1);
                                user.setDateGetPreviousPrize(LocalDateTime.now(ZoneId.of("UTC+3")));
                                user.setPrizeJson(statusPrize);
                                userRepository.save(user);

                                EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(userIdCallbackQuery, messageId)
                                        .replyMarkup(keyboardService.thirdInlineKeyboardWithPrizes(userIdCallbackQuery));

                                telegramBot.execute(editMessageReplyMarkup);
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("25 баллов получено"));
                                String urlAward = String.format(awardUser, user.getGizmoName(), 25);
                                connectionGizmoService.connectionPut(connectionGizmoService.getToken(), urlAward);

                            } else {
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Недостаточно потрачено денег"));
                            }
                        } else if (betweenDateGetPrevPrizeAndCurrentDate == 0) {
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награду можно получить завтра"));
                        } else {
                            user.setIsZeroingStreak(true);
                            user.setPrizeJson(new PrizeJson());
                            userRepository.save(user);
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ты пропустил 1 день"));
                        }
                    } else {
                        telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ещё рано"));
                    }
                } else {
                    telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награда была уже получена"));
                }

                action.setAction(Actions.CALLBACK_DAY.callbackDay(15));
                action.setUserTelegramId(user.getTelegramId());
                action.setMoneySpent(user.getMoneySpentInDay());
                action.setCurrentStreakDay(user.getCurrentStreakDay());
                action.setUserTelegramNickname(user.getTelegramNickname());
                actionRepository.save(action);

            } else if ("16".equals(update.callbackQuery().data())) {
                if (!statusPrize.getReceivedPrizeOfDay2()) {
                    if (currentStreakDay == 15) {

                        LocalDate currentDate = LocalDate.now(ZoneId.of("UTC+3"));
                        LocalDate dateGetPreviousPrize = user.getDateGetPreviousPrize().toLocalDate();
                        long betweenDateGetPrevPrizeAndCurrentDate = currentDate.toEpochDay() - dateGetPreviousPrize.toEpochDay();

                        if (betweenDateGetPrevPrizeAndCurrentDate == 1) {

                            String urlSpending = String.format(gizmoUserSpendingUrl, user.getGizmoId(), currentDate, currentDate.plusDays(1));
                            JsonObject jsonObjectSpending = connectionGizmoService.connectionGet(connectionGizmoService.getToken(), urlSpending);
                            log.info(jsonObjectSpending.toString());

                            float spendingMoneyInDay = 0f;
                            try {
                                spendingMoneyInDay = jsonObjectSpending.getAsJsonArray("result")
                                        .get(0).getAsJsonObject().get("total").getAsFloat();
                            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                                log.info("Ни рубля не потратил :(");
                            }
                            user.setMoneySpentInDay((long) spendingMoneyInDay);
                            System.out.println("Spend money: " + spendingMoneyInDay);

                            if (spendingMoneyInDay >= 100) {

                                statusPrize.setReceivedPrizeOfDay16(true);
                                user.setCurrentStreakDay(currentStreakDay + 1);
                                user.setDateGetPreviousPrize(LocalDateTime.now(ZoneId.of("UTC+3")));
                                user.setPrizeJson(statusPrize);
                                userRepository.save(user);

                                EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(userIdCallbackQuery, messageId)
                                        .replyMarkup(keyboardService.thirdInlineKeyboardWithPrizes(userIdCallbackQuery));

                                telegramBot.execute(editMessageReplyMarkup);
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("30 баллов получено"));
                                String urlAward = String.format(awardUser, user.getGizmoName(), 30);
                                connectionGizmoService.connectionPut(connectionGizmoService.getToken(), urlAward);

                            } else {
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Недостаточно потрачено денег"));
                            }
                        } else if (betweenDateGetPrevPrizeAndCurrentDate == 0) {
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награду можно получить завтра"));
                        } else {
                            user.setIsZeroingStreak(true);
                            user.setPrizeJson(new PrizeJson());
                            userRepository.save(user);
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ты пропустил 1 день"));
                        }
                    } else {
                        telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ещё рано"));
                    }
                } else {
                    telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награда была уже получена"));
                }

                action.setAction(Actions.CALLBACK_DAY.callbackDay(16));
                action.setUserTelegramId(user.getTelegramId());
                action.setMoneySpent(user.getMoneySpentInDay());
                action.setCurrentStreakDay(user.getCurrentStreakDay());
                action.setUserTelegramNickname(user.getTelegramNickname());
                actionRepository.save(action);

            } else if ("17".equals(update.callbackQuery().data())) {
                if (!statusPrize.getReceivedPrizeOfDay17()) {
                    if (currentStreakDay == 16) {

                        LocalDate currentDate = LocalDate.now(ZoneId.of("UTC+3"));
                        LocalDate dateGetPreviousPrize = user.getDateGetPreviousPrize().toLocalDate();
                        long betweenDateGetPrevPrizeAndCurrentDate = currentDate.toEpochDay() - dateGetPreviousPrize.toEpochDay();

                        if (betweenDateGetPrevPrizeAndCurrentDate == 1) {

                            String urlSpending = String.format(gizmoUserSpendingUrl, user.getGizmoId(), currentDate, currentDate.plusDays(1));
                            JsonObject jsonObjectSpending = connectionGizmoService.connectionGet(connectionGizmoService.getToken(), urlSpending);
                            log.info(jsonObjectSpending.toString());

                            float spendingMoneyInDay = 0f;
                            try {
                                spendingMoneyInDay = jsonObjectSpending.getAsJsonArray("result")
                                        .get(0).getAsJsonObject().get("total").getAsFloat();
                            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                                log.info("Ни рубля не потратил :(");
                            }
                            user.setMoneySpentInDay((long) spendingMoneyInDay);
                            System.out.println("Spend money: " + spendingMoneyInDay);

                            if (spendingMoneyInDay >= 100) {

                                statusPrize.setReceivedPrizeOfDay17(true);
                                user.setCurrentStreakDay(currentStreakDay + 1);
                                user.setDateGetPreviousPrize(LocalDateTime.now(ZoneId.of("UTC+3")));
                                user.setPrizeJson(statusPrize);
                                userRepository.save(user);

                                EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(userIdCallbackQuery, messageId)
                                        .replyMarkup(keyboardService.thirdInlineKeyboardWithPrizes(userIdCallbackQuery));

                                telegramBot.execute(editMessageReplyMarkup);
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награда получена"));
                                telegramBot.execute(new SendMessage(user.getTelegramId(), TEXT_PACKAGE_TIME_GET +
                                        LocalDate.now(ZoneId.of("UTC+3")) + " 23:59"));

                            } else {
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Недостаточно потрачено денег"));
                            }
                        } else if (betweenDateGetPrevPrizeAndCurrentDate == 0) {
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награду можно получить завтра"));
                        } else {
                            user.setIsZeroingStreak(true);
                            user.setPrizeJson(new PrizeJson());
                            userRepository.save(user);
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ты пропустил 1 день"));
                        }
                    } else {
                        telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ещё рано"));
                    }
                } else {
                    telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награда была уже получена"));
                }

                action.setAction(Actions.CALLBACK_DAY.callbackDay(17));
                action.setUserTelegramId(user.getTelegramId());
                action.setMoneySpent(user.getMoneySpentInDay());
                action.setCurrentStreakDay(user.getCurrentStreakDay());
                action.setUserTelegramNickname(user.getTelegramNickname());
                actionRepository.save(action);

            } else if ("18".equals(update.callbackQuery().data())) {
                if (!statusPrize.getReceivedPrizeOfDay18()) {
                    if (currentStreakDay == 17) {

                        LocalDate currentDate = LocalDate.now(ZoneId.of("UTC+3"));
                        LocalDate dateGetPreviousPrize = user.getDateGetPreviousPrize().toLocalDate();
                        long betweenDateGetPrevPrizeAndCurrentDate = currentDate.toEpochDay() - dateGetPreviousPrize.toEpochDay();

                        if (betweenDateGetPrevPrizeAndCurrentDate == 1) {

                            String urlSpending = String.format(gizmoUserSpendingUrl, user.getGizmoId(), currentDate, currentDate.plusDays(1));
                            JsonObject jsonObjectSpending = connectionGizmoService.connectionGet(connectionGizmoService.getToken(), urlSpending);
                            log.info(jsonObjectSpending.toString());

                            float spendingMoneyInDay = 0f;
                            try {
                                spendingMoneyInDay = jsonObjectSpending.getAsJsonArray("result")
                                        .get(0).getAsJsonObject().get("total").getAsFloat();
                            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                                log.info("Ни рубля не потратил :(");
                            }
                            user.setMoneySpentInDay((long) spendingMoneyInDay);
                            System.out.println("Spend money: " + spendingMoneyInDay);

                            if (spendingMoneyInDay >= 100) {

                                statusPrize.setReceivedPrizeOfDay18(true);
                                user.setCurrentStreakDay(currentStreakDay + 1);
                                user.setDateGetPreviousPrize(LocalDateTime.now(ZoneId.of("UTC+3")));
                                user.setPrizeJson(statusPrize);
                                userRepository.save(user);

                                EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(userIdCallbackQuery, messageId)
                                        .replyMarkup(keyboardService.thirdInlineKeyboardWithPrizes(userIdCallbackQuery));

                                telegramBot.execute(editMessageReplyMarkup);
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("35 баллов получено"));
                                String urlAward = String.format(awardUser, user.getGizmoName(), 35);
                                connectionGizmoService.connectionPut(connectionGizmoService.getToken(), urlAward);

                            } else {
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Недостаточно потрачено денег"));
                            }
                        } else if (betweenDateGetPrevPrizeAndCurrentDate == 0) {
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награду можно получить завтра"));
                        } else {
                            user.setIsZeroingStreak(true);
                            user.setPrizeJson(new PrizeJson());
                            userRepository.save(user);
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ты пропустил 1 день"));
                        }
                    } else {
                        telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ещё рано"));
                    }
                } else {
                    telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награда была уже получена"));
                }

                action.setAction(Actions.CALLBACK_DAY.callbackDay(18));
                action.setUserTelegramId(user.getTelegramId());
                action.setMoneySpent(user.getMoneySpentInDay());
                action.setCurrentStreakDay(user.getCurrentStreakDay());
                action.setUserTelegramNickname(user.getTelegramNickname());
                actionRepository.save(action);

            } else if ("19".equals(update.callbackQuery().data())) {
                if (!statusPrize.getReceivedPrizeOfDay19()) {
                    if (currentStreakDay == 18) {

                        LocalDate currentDate = LocalDate.now(ZoneId.of("UTC+3"));
                        LocalDate dateGetPreviousPrize = user.getDateGetPreviousPrize().toLocalDate();
                        long betweenDateGetPrevPrizeAndCurrentDate = currentDate.toEpochDay() - dateGetPreviousPrize.toEpochDay();

                        if (betweenDateGetPrevPrizeAndCurrentDate == 1) {

                            String urlSpending = String.format(gizmoUserSpendingUrl, user.getGizmoId(), currentDate, currentDate.plusDays(1));
                            JsonObject jsonObjectSpending = connectionGizmoService.connectionGet(connectionGizmoService.getToken(), urlSpending);
                            log.info(jsonObjectSpending.toString());

                            float spendingMoneyInDay = 0f;
                            try {
                                spendingMoneyInDay = jsonObjectSpending.getAsJsonArray("result")
                                        .get(0).getAsJsonObject().get("total").getAsFloat();
                            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                                log.info("Ни рубля не потратил :(");
                            }
                            user.setMoneySpentInDay((long) spendingMoneyInDay);
                            System.out.println("Spend money: " + spendingMoneyInDay);

                            if (spendingMoneyInDay >= 100) {

                                statusPrize.setReceivedPrizeOfDay19(true);
                                user.setCurrentStreakDay(currentStreakDay + 1);
                                user.setDateGetPreviousPrize(LocalDateTime.now(ZoneId.of("UTC+3")));
                                user.setPrizeJson(statusPrize);
                                userRepository.save(user);

                                EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(userIdCallbackQuery, messageId)
                                        .replyMarkup(keyboardService.thirdInlineKeyboardWithPrizes(userIdCallbackQuery));

                                telegramBot.execute(editMessageReplyMarkup);
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("40 баллов получено"));
                                String urlAward = String.format(awardUser, user.getGizmoName(), 40);
                                connectionGizmoService.connectionPut(connectionGizmoService.getToken(), urlAward);

                            } else {
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Недостаточно потрачено денег"));
                            }
                        } else if (betweenDateGetPrevPrizeAndCurrentDate == 0) {
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награду можно получить завтра"));
                        } else {
                            user.setIsZeroingStreak(true);
                            user.setPrizeJson(new PrizeJson());
                            userRepository.save(user);
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ты пропустил 1 день"));
                        }
                    } else {
                        telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ещё рано"));
                    }
                } else {
                    telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награда была уже получена"));
                }

                action.setAction(Actions.CALLBACK_DAY.callbackDay(19));
                action.setUserTelegramId(user.getTelegramId());
                action.setMoneySpent(user.getMoneySpentInDay());
                action.setCurrentStreakDay(user.getCurrentStreakDay());
                action.setUserTelegramNickname(user.getTelegramNickname());
                actionRepository.save(action);

            } else if ("20".equals(update.callbackQuery().data())) {
                if (!statusPrize.getReceivedPrizeOfDay20()) {
                    if (currentStreakDay == 19) {

                        LocalDate currentDate = LocalDate.now(ZoneId.of("UTC+3"));
                        LocalDate dateGetPreviousPrize = user.getDateGetPreviousPrize().toLocalDate();
                        long betweenDateGetPrevPrizeAndCurrentDate = currentDate.toEpochDay() - dateGetPreviousPrize.toEpochDay();

                        if (betweenDateGetPrevPrizeAndCurrentDate == 1) {

                            String urlSpending = String.format(gizmoUserSpendingUrl, user.getGizmoId(), currentDate, currentDate.plusDays(1));
                            JsonObject jsonObjectSpending = connectionGizmoService.connectionGet(connectionGizmoService.getToken(), urlSpending);
                            log.info(jsonObjectSpending.toString());

                            float spendingMoneyInDay = 0f;
                            try {
                                spendingMoneyInDay = jsonObjectSpending.getAsJsonArray("result")
                                        .get(0).getAsJsonObject().get("total").getAsFloat();
                            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                                log.info("Ни рубля не потратил :(");
                            }
                            user.setMoneySpentInDay((long) spendingMoneyInDay);
                            System.out.println("Spend money: " + spendingMoneyInDay);

                            if (spendingMoneyInDay >= 100) {

                                statusPrize.setReceivedPrizeOfDay20(true);
                                user.setCurrentStreakDay(currentStreakDay + 1);
                                user.setDateGetPreviousPrize(LocalDateTime.now(ZoneId.of("UTC+3")));
                                user.setPrizeJson(statusPrize);
                                userRepository.save(user);

                                EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(userIdCallbackQuery, messageId)
                                        .replyMarkup(keyboardService.thirdInlineKeyboardWithPrizes(userIdCallbackQuery));

                                telegramBot.execute(editMessageReplyMarkup);
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("45 баллов получено"));
                                String urlAward = String.format(awardUser, user.getGizmoName(), 45);
                                connectionGizmoService.connectionPut(connectionGizmoService.getToken(), urlAward);

                            } else {
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Недостаточно потрачено денег"));
                            }
                        } else if (betweenDateGetPrevPrizeAndCurrentDate == 0) {
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награду можно получить завтра"));
                        } else {
                            user.setIsZeroingStreak(true);
                            user.setPrizeJson(new PrizeJson());
                            userRepository.save(user);
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ты пропустил 1 день"));
                        }
                    } else {
                        telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ещё рано"));
                    }
                } else {
                    telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награда была уже получена"));
                }

                action.setAction(Actions.CALLBACK_DAY.callbackDay(20));
                action.setUserTelegramId(user.getTelegramId());
                action.setMoneySpent(user.getMoneySpentInDay());
                action.setCurrentStreakDay(user.getCurrentStreakDay());
                action.setUserTelegramNickname(user.getTelegramNickname());
                actionRepository.save(action);

            } else if ("21".equals(update.callbackQuery().data())) {
                if (!statusPrize.getReceivedPrizeOfDay21()) {
                    if (currentStreakDay == 20) {

                        LocalDate currentDate = LocalDate.now(ZoneId.of("UTC+3"));
                        LocalDate dateGetPreviousPrize = user.getDateGetPreviousPrize().toLocalDate();
                        long betweenDateGetPrevPrizeAndCurrentDate = currentDate.toEpochDay() - dateGetPreviousPrize.toEpochDay();

                        if (betweenDateGetPrevPrizeAndCurrentDate == 1) {

                            String urlSpending = String.format(gizmoUserSpendingUrl, user.getGizmoId(), currentDate, currentDate.plusDays(1));
                            JsonObject jsonObjectSpending = connectionGizmoService.connectionGet(connectionGizmoService.getToken(), urlSpending);
                            log.info(jsonObjectSpending.toString());

                            float spendingMoneyInDay = 0f;
                            try {
                                spendingMoneyInDay = jsonObjectSpending.getAsJsonArray("result")
                                        .get(0).getAsJsonObject().get("total").getAsFloat();
                            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                                log.info("Ни рубля не потратил :(");
                            }
                            user.setMoneySpentInDay((long) spendingMoneyInDay);
                            System.out.println("Spend money: " + spendingMoneyInDay);

                            if (spendingMoneyInDay >= 100) {

                                statusPrize.setReceivedPrizeOfDay21(true);
                                user.setCurrentStreakDay(currentStreakDay + 1);
                                user.setDateGetPreviousPrize(LocalDateTime.now(ZoneId.of("UTC+3")));
                                user.setPrizeJson(statusPrize);
                                userRepository.save(user);

                                EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(userIdCallbackQuery, messageId)
                                        .replyMarkup(keyboardService.thirdInlineKeyboardWithPrizes(userIdCallbackQuery));

                                telegramBot.execute(editMessageReplyMarkup);
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награда получена"));
                                telegramBot.execute(new SendMessage(user.getTelegramId(), TEXT_PACKAGE_TIME_GET +
                                        LocalDate.now(ZoneId.of("UTC+3")) + " 23:59"));

                            } else {
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Недостаточно потрачено денег"));
                            }
                        } else if (betweenDateGetPrevPrizeAndCurrentDate == 0) {
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награду можно получить завтра"));
                        } else {
                            user.setIsZeroingStreak(true);
                            user.setPrizeJson(new PrizeJson());
                            userRepository.save(user);
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ты пропустил 1 день"));
                        }
                    } else {
                        telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ещё рано"));
                    }
                } else {
                    telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награда была уже получена"));
                }

                action.setAction(Actions.CALLBACK_DAY.callbackDay(21));
                action.setUserTelegramId(user.getTelegramId());
                action.setMoneySpent(user.getMoneySpentInDay());
                action.setCurrentStreakDay(user.getCurrentStreakDay());
                action.setUserTelegramNickname(user.getTelegramNickname());
                actionRepository.save(action);

            } else if ("22".equals(update.callbackQuery().data())) {
                if (!statusPrize.getReceivedPrizeOfDay22()) {
                    if (currentStreakDay == 21) {

                        LocalDate currentDate = LocalDate.now(ZoneId.of("UTC+3"));
                        LocalDate dateGetPreviousPrize = user.getDateGetPreviousPrize().toLocalDate();
                        long betweenDateGetPrevPrizeAndCurrentDate = currentDate.toEpochDay() - dateGetPreviousPrize.toEpochDay();

                        if (betweenDateGetPrevPrizeAndCurrentDate == 1) {

                            String urlSpending = String.format(gizmoUserSpendingUrl, user.getGizmoId(), currentDate, currentDate.plusDays(1));
                            JsonObject jsonObjectSpending = connectionGizmoService.connectionGet(connectionGizmoService.getToken(), urlSpending);
                            log.info(jsonObjectSpending.toString());

                            float spendingMoneyInDay = 0f;
                            try {
                                spendingMoneyInDay = jsonObjectSpending.getAsJsonArray("result")
                                        .get(0).getAsJsonObject().get("total").getAsFloat();
                            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                                log.info("Ни рубля не потратил :(");
                            }
                            user.setMoneySpentInDay((long) spendingMoneyInDay);
                            System.out.println("Spend money: " + spendingMoneyInDay);

                            if (spendingMoneyInDay >= 100) {

                                statusPrize.setReceivedPrizeOfDay22(true);
                                user.setCurrentStreakDay(currentStreakDay + 1);
                                user.setDateGetPreviousPrize(LocalDateTime.now(ZoneId.of("UTC+3")));
                                user.setPrizeJson(statusPrize);
                                userRepository.save(user);

                                EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(userIdCallbackQuery, messageId)
                                        .replyMarkup(keyboardService.fourthInlineKeyboardWithPrizes(userIdCallbackQuery));

                                telegramBot.execute(editMessageReplyMarkup);
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("30 баллов получено"));
                                String urlAward = String.format(awardUser, user.getGizmoName(), 30);
                                connectionGizmoService.connectionPut(connectionGizmoService.getToken(), urlAward);

                            } else {
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Недостаточно потрачено денег"));
                            }
                        } else if (betweenDateGetPrevPrizeAndCurrentDate == 0) {
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награду можно получить завтра"));
                        } else {
                            user.setIsZeroingStreak(true);
                            user.setPrizeJson(new PrizeJson());
                            userRepository.save(user);
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ты пропустил 1 день"));
                        }
                    } else {
                        telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ещё рано"));
                    }
                } else {
                    telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награда была уже получена"));
                }

                action.setAction(Actions.CALLBACK_DAY.callbackDay(22));
                action.setUserTelegramId(user.getTelegramId());
                action.setMoneySpent(user.getMoneySpentInDay());
                action.setCurrentStreakDay(user.getCurrentStreakDay());
                action.setUserTelegramNickname(user.getTelegramNickname());
                actionRepository.save(action);

            } else if ("23".equals(update.callbackQuery().data())) {
                if (!statusPrize.getReceivedPrizeOfDay23()) {
                    if (currentStreakDay == 22) {

                        LocalDate currentDate = LocalDate.now(ZoneId.of("UTC+3"));
                        LocalDate dateGetPreviousPrize = user.getDateGetPreviousPrize().toLocalDate();
                        long betweenDateGetPrevPrizeAndCurrentDate = currentDate.toEpochDay() - dateGetPreviousPrize.toEpochDay();

                        if (betweenDateGetPrevPrizeAndCurrentDate == 1) {

                            String urlSpending = String.format(gizmoUserSpendingUrl, user.getGizmoId(), currentDate, currentDate.plusDays(1));
                            JsonObject jsonObjectSpending = connectionGizmoService.connectionGet(connectionGizmoService.getToken(), urlSpending);
                            log.info(jsonObjectSpending.toString());

                            float spendingMoneyInDay = 0f;
                            try {
                                spendingMoneyInDay = jsonObjectSpending.getAsJsonArray("result")
                                        .get(0).getAsJsonObject().get("total").getAsFloat();
                            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                                log.info("Ни рубля не потратил :(");
                            }
                            user.setMoneySpentInDay((long) spendingMoneyInDay);
                            System.out.println("Spend money: " + spendingMoneyInDay);

                            if (spendingMoneyInDay >= 100) {

                                statusPrize.setReceivedPrizeOfDay23(true);
                                user.setCurrentStreakDay(currentStreakDay + 1);
                                user.setDateGetPreviousPrize(LocalDateTime.now(ZoneId.of("UTC+3")));
                                user.setPrizeJson(statusPrize);
                                userRepository.save(user);

                                EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(userIdCallbackQuery, messageId)
                                        .replyMarkup(keyboardService.fourthInlineKeyboardWithPrizes(userIdCallbackQuery));

                                telegramBot.execute(editMessageReplyMarkup);
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("35 баллов получено"));
                                String urlAward = String.format(awardUser, user.getGizmoName(), 35);
                                connectionGizmoService.connectionPut(connectionGizmoService.getToken(), urlAward);

                            } else {
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Недостаточно потрачено денег"));
                            }
                        } else if (betweenDateGetPrevPrizeAndCurrentDate == 0) {
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награду можно получить завтра"));
                        } else {
                            user.setIsZeroingStreak(true);
                            user.setPrizeJson(new PrizeJson());
                            userRepository.save(user);
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ты пропустил 1 день"));
                        }
                    } else {
                        telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ещё рано"));
                    }
                } else {
                    telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награда была уже получена"));
                }

                action.setAction(Actions.CALLBACK_DAY.callbackDay(23));
                action.setUserTelegramId(user.getTelegramId());
                action.setMoneySpent(user.getMoneySpentInDay());
                action.setCurrentStreakDay(user.getCurrentStreakDay());
                action.setUserTelegramNickname(user.getTelegramNickname());
                actionRepository.save(action);

            } else if ("24".equals(update.callbackQuery().data())) {
                if (!statusPrize.getReceivedPrizeOfDay24()) {
                    if (currentStreakDay == 23) {

                        LocalDate currentDate = LocalDate.now(ZoneId.of("UTC+3"));
                        LocalDate dateGetPreviousPrize = user.getDateGetPreviousPrize().toLocalDate();
                        long betweenDateGetPrevPrizeAndCurrentDate = currentDate.toEpochDay() - dateGetPreviousPrize.toEpochDay();

                        if (betweenDateGetPrevPrizeAndCurrentDate == 1) {

                            String urlSpending = String.format(gizmoUserSpendingUrl, user.getGizmoId(), currentDate, currentDate.plusDays(1));
                            JsonObject jsonObjectSpending = connectionGizmoService.connectionGet(connectionGizmoService.getToken(), urlSpending);
                            log.info(jsonObjectSpending.toString());

                            float spendingMoneyInDay = 0f;
                            try {
                                spendingMoneyInDay = jsonObjectSpending.getAsJsonArray("result")
                                        .get(0).getAsJsonObject().get("total").getAsFloat();
                            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                                log.info("Ни рубля не потратил :(");
                            }
                            user.setMoneySpentInDay((long) spendingMoneyInDay);
                            System.out.println("Spend money: " + spendingMoneyInDay);

                            if (spendingMoneyInDay >= 100) {

                                statusPrize.setReceivedPrizeOfDay24(true);
                                user.setCurrentStreakDay(currentStreakDay + 1);
                                user.setDateGetPreviousPrize(LocalDateTime.now(ZoneId.of("UTC+3")));
                                user.setPrizeJson(statusPrize);
                                userRepository.save(user);

                                EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(userIdCallbackQuery, messageId)
                                        .replyMarkup(keyboardService.fourthInlineKeyboardWithPrizes(userIdCallbackQuery));

                                telegramBot.execute(editMessageReplyMarkup);
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награда получена"));
                                telegramBot.execute(new SendMessage(user.getTelegramId(), TEXT_PACKAGE_TIME_GET +
                                        LocalDate.now(ZoneId.of("UTC+3")) + " 23:59"));

                            } else {
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Недостаточно потрачено денег"));
                            }
                        } else if (betweenDateGetPrevPrizeAndCurrentDate == 0) {
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награду можно получить завтра"));
                        } else {
                            user.setIsZeroingStreak(true);
                            user.setPrizeJson(new PrizeJson());
                            userRepository.save(user);
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ты пропустил 1 день"));
                        }
                    } else {
                        telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ещё рано"));
                    }
                } else {
                    telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награда была уже получена"));
                }

                action.setAction(Actions.CALLBACK_DAY.callbackDay(24));
                action.setUserTelegramId(user.getTelegramId());
                action.setMoneySpent(user.getMoneySpentInDay());
                action.setCurrentStreakDay(user.getCurrentStreakDay());
                action.setUserTelegramNickname(user.getTelegramNickname());
                actionRepository.save(action);

            } else if ("25".equals(update.callbackQuery().data())) {
                if (!statusPrize.getReceivedPrizeOfDay25()) {
                    if (currentStreakDay == 24) {

                        LocalDate currentDate = LocalDate.now(ZoneId.of("UTC+3"));
                        LocalDate dateGetPreviousPrize = user.getDateGetPreviousPrize().toLocalDate();
                        long betweenDateGetPrevPrizeAndCurrentDate = currentDate.toEpochDay() - dateGetPreviousPrize.toEpochDay();

                        if (betweenDateGetPrevPrizeAndCurrentDate == 1) {

                            String urlSpending = String.format(gizmoUserSpendingUrl, user.getGizmoId(), currentDate, currentDate.plusDays(1));
                            JsonObject jsonObjectSpending = connectionGizmoService.connectionGet(connectionGizmoService.getToken(), urlSpending);
                            log.info(jsonObjectSpending.toString());

                            float spendingMoneyInDay = 0f;
                            try {
                                spendingMoneyInDay = jsonObjectSpending.getAsJsonArray("result")
                                        .get(0).getAsJsonObject().get("total").getAsFloat();
                            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                                log.info("Ни рубля не потратил :(");
                            }
                            user.setMoneySpentInDay((long) spendingMoneyInDay);
                            System.out.println("Spend money: " + spendingMoneyInDay);

                            if (spendingMoneyInDay >= 100) {

                                statusPrize.setReceivedPrizeOfDay25(true);
                                user.setCurrentStreakDay(currentStreakDay + 1);
                                user.setDateGetPreviousPrize(LocalDateTime.now(ZoneId.of("UTC+3")));
                                user.setPrizeJson(statusPrize);
                                userRepository.save(user);

                                EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(userIdCallbackQuery, messageId)
                                        .replyMarkup(keyboardService.fourthInlineKeyboardWithPrizes(userIdCallbackQuery));

                                telegramBot.execute(editMessageReplyMarkup);
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("40 баллов получего"));
                                String urlAward = String.format(awardUser, user.getGizmoName(), 40);
                                connectionGizmoService.connectionPut(connectionGizmoService.getToken(), urlAward);

                            } else {
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Недостаточно потрачено денег"));
                            }
                        } else if (betweenDateGetPrevPrizeAndCurrentDate == 0) {
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награду можно получить завтра"));
                        } else {
                            user.setIsZeroingStreak(true);
                            user.setPrizeJson(new PrizeJson());
                            userRepository.save(user);
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ты пропустил 1 день"));
                        }
                    } else {
                        telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ещё рано"));
                    }
                } else {
                    telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награда была уже получена"));
                }

                action.setAction(Actions.CALLBACK_DAY.callbackDay(25));
                action.setUserTelegramId(user.getTelegramId());
                action.setMoneySpent(user.getMoneySpentInDay());
                action.setCurrentStreakDay(user.getCurrentStreakDay());
                action.setUserTelegramNickname(user.getTelegramNickname());
                actionRepository.save(action);

            } else if ("26".equals(update.callbackQuery().data())) {
                if (!statusPrize.getReceivedPrizeOfDay26()) {
                    if (currentStreakDay == 25) {

                        LocalDate currentDate = LocalDate.now(ZoneId.of("UTC+3"));
                        LocalDate dateGetPreviousPrize = user.getDateGetPreviousPrize().toLocalDate();
                        long betweenDateGetPrevPrizeAndCurrentDate = currentDate.toEpochDay() - dateGetPreviousPrize.toEpochDay();

                        if (betweenDateGetPrevPrizeAndCurrentDate == 1) {

                            String urlSpending = String.format(gizmoUserSpendingUrl, user.getGizmoId(), currentDate, currentDate.plusDays(1));
                            JsonObject jsonObjectSpending = connectionGizmoService.connectionGet(connectionGizmoService.getToken(), urlSpending);
                            log.info(jsonObjectSpending.toString());

                            float spendingMoneyInDay = 0f;
                            try {
                                spendingMoneyInDay = jsonObjectSpending.getAsJsonArray("result")
                                        .get(0).getAsJsonObject().get("total").getAsFloat();
                            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                                log.info("Ни рубля не потратил :(");
                            }
                            user.setMoneySpentInDay((long) spendingMoneyInDay);
                            System.out.println("Spend money: " + spendingMoneyInDay);

                            if (spendingMoneyInDay >= 100) {

                                statusPrize.setReceivedPrizeOfDay26(true);
                                user.setCurrentStreakDay(currentStreakDay + 1);
                                user.setDateGetPreviousPrize(LocalDateTime.now(ZoneId.of("UTC+3")));
                                user.setPrizeJson(statusPrize);
                                userRepository.save(user);

                                EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(userIdCallbackQuery, messageId)
                                        .replyMarkup(keyboardService.fourthInlineKeyboardWithPrizes(userIdCallbackQuery));

                                telegramBot.execute(editMessageReplyMarkup);
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("45 баллов получено"));
                                String urlAward = String.format(awardUser, user.getGizmoName(), 45);
                                connectionGizmoService.connectionPut(connectionGizmoService.getToken(), urlAward);

                            } else {
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Недостаточно потрачено денег"));
                            }
                        } else if (betweenDateGetPrevPrizeAndCurrentDate == 0) {
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награду можно получить завтра"));
                        } else {
                            user.setIsZeroingStreak(true);
                            user.setPrizeJson(new PrizeJson());
                            userRepository.save(user);
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ты пропустил 1 день"));
                        }
                    } else {
                        telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ещё рано"));
                    }
                } else {
                    telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награда была уже получена"));
                }

                action.setAction(Actions.CALLBACK_DAY.callbackDay(26));
                action.setUserTelegramId(user.getTelegramId());
                action.setMoneySpent(user.getMoneySpentInDay());
                action.setCurrentStreakDay(user.getCurrentStreakDay());
                action.setUserTelegramNickname(user.getTelegramNickname());
                actionRepository.save(action);

            } else if ("27".equals(update.callbackQuery().data())) {
                if (!statusPrize.getReceivedPrizeOfDay27()) {
                    if (currentStreakDay == 26) {

                        LocalDate currentDate = LocalDate.now(ZoneId.of("UTC+3"));
                        LocalDate dateGetPreviousPrize = user.getDateGetPreviousPrize().toLocalDate();
                        long betweenDateGetPrevPrizeAndCurrentDate = currentDate.toEpochDay() - dateGetPreviousPrize.toEpochDay();

                        if (betweenDateGetPrevPrizeAndCurrentDate == 1) {

                            String urlSpending = String.format(gizmoUserSpendingUrl, user.getGizmoId(), currentDate, currentDate.plusDays(1));
                            JsonObject jsonObjectSpending = connectionGizmoService.connectionGet(connectionGizmoService.getToken(), urlSpending);
                            log.info(jsonObjectSpending.toString());

                            float spendingMoneyInDay = 0f;
                            try {
                                spendingMoneyInDay = jsonObjectSpending.getAsJsonArray("result")
                                        .get(0).getAsJsonObject().get("total").getAsFloat();
                            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                                log.info("Ни рубля не потратил :(");
                            }
                            user.setMoneySpentInDay((long) spendingMoneyInDay);
                            System.out.println("Spend money: " + spendingMoneyInDay);

                            if (spendingMoneyInDay >= 100) {

                                statusPrize.setReceivedPrizeOfDay27(true);
                                user.setCurrentStreakDay(currentStreakDay + 1);
                                user.setDateGetPreviousPrize(LocalDateTime.now(ZoneId.of("UTC+3")));
                                user.setPrizeJson(statusPrize);
                                userRepository.save(user);

                                EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(userIdCallbackQuery, messageId)
                                        .replyMarkup(keyboardService.fourthInlineKeyboardWithPrizes(userIdCallbackQuery));

                                telegramBot.execute(editMessageReplyMarkup);
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("50 баллов получено"));
                                String urlAward = String.format(awardUser, user.getGizmoName(), 50);
                                connectionGizmoService.connectionPut(connectionGizmoService.getToken(), urlAward);

                            } else {
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Недостаточно потрачено денег"));
                            }
                        } else if (betweenDateGetPrevPrizeAndCurrentDate == 0) {
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награду можно получить завтра"));
                        } else {
                            user.setIsZeroingStreak(true);
                            user.setPrizeJson(new PrizeJson());
                            userRepository.save(user);
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ты пропустил 1 день"));
                        }
                    } else {
                        telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ещё рано"));
                    }
                } else {
                    telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награда была уже получена"));
                }

                action.setAction(Actions.CALLBACK_DAY.callbackDay(27));
                action.setUserTelegramId(user.getTelegramId());
                action.setMoneySpent(user.getMoneySpentInDay());
                action.setCurrentStreakDay(user.getCurrentStreakDay());
                action.setUserTelegramNickname(user.getTelegramNickname());
                actionRepository.save(action);

            } else if ("28".equals(update.callbackQuery().data())) {
                if (!statusPrize.getReceivedPrizeOfDay28()) {
                    if (currentStreakDay == 27) {

                        LocalDate currentDate = LocalDate.now(ZoneId.of("UTC+3"));
                        LocalDate dateGetPreviousPrize = user.getDateGetPreviousPrize().toLocalDate();
                        long betweenDateGetPrevPrizeAndCurrentDate = currentDate.toEpochDay() - dateGetPreviousPrize.toEpochDay();

                        if (betweenDateGetPrevPrizeAndCurrentDate == 1) {

                            String urlSpending = String.format(gizmoUserSpendingUrl, user.getGizmoId(), currentDate, currentDate.plusDays(1));
                            JsonObject jsonObjectSpending = connectionGizmoService.connectionGet(connectionGizmoService.getToken(), urlSpending);
                            log.info(jsonObjectSpending.toString());

                            float spendingMoneyInDay = 0f;
                            try {
                                spendingMoneyInDay = jsonObjectSpending.getAsJsonArray("result")
                                        .get(0).getAsJsonObject().get("total").getAsFloat();
                            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                                log.info("Ни рубля не потратил :(");
                            }
                            user.setMoneySpentInDay((long) spendingMoneyInDay);
                            System.out.println("Spend money: " + spendingMoneyInDay);

                            if (spendingMoneyInDay >= 100) {

                                statusPrize.setReceivedPrizeOfDay28(true);
                                user.setCurrentStreakDay(currentStreakDay + 1);
                                user.setDateGetPreviousPrize(LocalDateTime.now(ZoneId.of("UTC+3")));
                                user.setIsZeroingStreak(true);
                                user.setPrizeJson(statusPrize);
                                userRepository.save(user);

                                EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(userIdCallbackQuery, messageId)
                                        .replyMarkup(keyboardService.fourthInlineKeyboardWithPrizes(userIdCallbackQuery));

                                telegramBot.execute(editMessageReplyMarkup);
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награда получена"));
                                telegramBot.execute(new SendMessage(user.getTelegramId(), TEXT_PACKAGE_TIME_GET +
                                        LocalDate.now(ZoneId.of("UTC+3")) + " 23:59"));

                            } else {
                                telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Недостаточно потрачено денег"));
                            }
                        } else if (betweenDateGetPrevPrizeAndCurrentDate == 0) {
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награду можно получить завтра"));
                        } else {
                            user.setIsZeroingStreak(true);
                            user.setPrizeJson(new PrizeJson());
                            userRepository.save(user);
                            telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ты пропустил 1 день"));
                        }
                    } else {
                        telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ещё рано"));
                    }
                } else {
                    telegramBot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Награда была уже получена"));
                }

                action.setAction(Actions.CALLBACK_DAY.callbackDay(28));
                action.setUserTelegramId(user.getTelegramId());
                action.setMoneySpent(user.getMoneySpentInDay());
                action.setCurrentStreakDay(user.getCurrentStreakDay());
                action.setUserTelegramNickname(user.getTelegramNickname());
                actionRepository.save(action);
            }
        }
    }
}
