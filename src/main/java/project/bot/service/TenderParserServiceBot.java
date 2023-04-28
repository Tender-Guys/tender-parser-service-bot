package project.bot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import project.bot.config.BotConfig;

/**
 * @author Vladyslav Pustovalov
 * class TelegramBot which gets messages from useers and could answer to them
 */
@Component
@Slf4j
public class TenderParserServiceBot extends TelegramLongPollingBot {
    private final BotConfig config = new BotConfig();
    private final MessageSender messageSender = new MessageSender();

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

        if (!update.hasMessage() || !update.getMessage().hasText()) {
            log.warn("Unexpected update from user " + userName);
            sendMessageToUser(messageSender.sendUnsupportedDataWarning(chatId));
            log.info("Warn message was sent to user " + userName);
        } else {
            switch (messageText) {
                case "/start" -> {
                    sendMessageToUser(messageSender.sendWelcomeMessage(chatId));
                    log.info("Welcome message was sent to user " + userName);
                }
                case "My subscription list" -> {
                    sendMessageToUser(messageSender.sendUserSubscriptionsList(chatId));
                    log.info("Subscription list was sent to user " + userName);

                }
                case "Available tender sites for subscription" -> {
                    sendMessageToUser(messageSender.sendAvailableSitesList(chatId));
                    log.info("Available sites list was sent to user " + userName);
                }
                case "Help instructions" -> {
                    sendMessageToUser(messageSender.sendHelpMessage(chatId));
                    log.info("Help instructions was sent to user " + userName);
                }
                default -> {
                    sendMessageToUser(messageSender.sendUnsupportedCommandWarning(chatId));
                    log.info("Unsupported command warning was sent to user " + userName);
                }
            }
        }
    }

    private void sendMessageToUser(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: "+e);
        }
    }
}