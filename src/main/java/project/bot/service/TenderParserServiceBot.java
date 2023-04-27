package project.bot.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import project.bot.config.BotConfig;

/**
 * @author Vladyslav Pustovalov
 * class TelegramBot which gets messages from useers and could answer to them
 */
@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TenderParserServiceBot extends TelegramLongPollingBot {
    final BotConfig config = new BotConfig();
    final MessageSender messageSender = new MessageSender();

    public TenderParserServiceBot(DefaultBotOptions options, String botToken) {
        super(options, botToken);
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    /**
     * Method which gets updates from users and sends to them an answer
     */
    @Override
    public void onUpdateReceived(Update update) {
        String userName = update.getMessage().getChat().getUserName();
        String messageText = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        String errorOccurred = "Error occurred: ";

        if (!update.hasMessage() || !update.getMessage().hasText()) {
            log.warn("Unexpected update from user " + userName);
            try {
                execute(messageSender.sendUnsupportedDataWarning(chatId));
                log.info("Warn message was sent to user " + userName);
            } catch (TelegramApiException e) {
                log.error(errorOccurred + e);
            }
        } else {
            switch (messageText) {
                case "/start" -> {
                    try {
                        execute(messageSender.sendWelcomeMessage(chatId));
                        log.info("Welcome message was sent to user " + userName);
                    } catch (TelegramApiException e) {
                        log.error(errorOccurred + e);
                    }
                }
                case "My subscription list" -> {
                    try {
                        execute(messageSender.sendUserSubscriptionsList(chatId));
                        log.info("Subscription list was sent to user " + userName);
                    } catch (TelegramApiException e) {
                        log.error(errorOccurred + e);
                    }
                }
                case "Available tender sites for subscription" -> {
                    try {
                        execute(messageSender.SendAvailableSitesList(chatId));
                        log.info("Available sites list was sent to user " + userName);
                    } catch (TelegramApiException e) {
                        log.error(errorOccurred + e);
                    }
                }
                case "Help instructions" -> {
                    try {
                        execute(messageSender.sendHelpMessage(chatId));
                        log.info("Help instructions was sent to user " + userName);
                    } catch (TelegramApiException e) {
                        log.error(errorOccurred + e);
                    }
                }
                default -> {
                    try {
                        execute(messageSender.sendUnsupportedCommand(chatId));
                        log.info("Default message was sent to user " + userName);
                    } catch (TelegramApiException e) {
                        log.error(errorOccurred + e);
                    }
                }
            }
        }
    }
}