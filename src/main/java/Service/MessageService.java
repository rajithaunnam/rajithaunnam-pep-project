package Service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import DAO.MessageDAO;
import DAO.Exception;
import Model.Account;
import Model.Message;
import io.javalin.http.NotFoundResponse;

public class MessageService {
    private MessageDAO messageDAO;
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageService.class);
    private static final String DB_ACCESS_ERROR_MSG = "Error accessing the database";

    public MessageService() {
        messageDAO = new MessageDAO();
    }

    public MessageService(MessageDAO messageDao) {
        this.messageDAO = messageDao;
    }

    public Optional<Message> getMessageById(int id) {
        LOGGER.info("Fetching message with ID: {} ", id);
        try {
            Optional<Message> message = messageDAO.getById(id);
            if (!message.isPresent()) {
                throw new ExceptionService("Message not found");
            }
            LOGGER.info("Fetched message: {}", message.orElse(null));
            return message;
        } catch (Exception e) {
            throw new ExceptionService(DB_ACCESS_ERROR_MSG, e);
        }
    }

    public List<Message> getAllMessages() {
        LOGGER.info("Fetching all messages");
        try {
            List<Message> messages = messageDAO.getAll();
            LOGGER.info("Fetched {} messages", messages.size());
            return messages;
        } catch (Exception e) {
            throw new ExceptionService(DB_ACCESS_ERROR_MSG, e);
        }
    }

    public List<Message> getMessagesByAccountId(int accountId) {
        LOGGER.info("Fetching messages posted by ID account: {}", accountId);
        try {
            List<Message> messages = messageDAO.getMessagesByAccountId(accountId);
            LOGGER.info("Fetched {} messages", messages.size());
            return messages;
        } catch (Exception e) {
            throw new ExceptionService(DB_ACCESS_ERROR_MSG, e);
        }
    }

    public Message createMessage(Message message, Optional<Account> account) {
        LOGGER.info("Creating message: {}", message);

        if (!account.isPresent()) {
            throw new ExceptionService("Account must exist when posting a new message");
        }

        validateMessage(message);

        checkAccountPermission(account.get(), message.getPosted_by());
        try {

            Message createdMessage = messageDAO.insert(message);
            LOGGER.info("Created message: {}", createdMessage);
            return createdMessage;
        } catch (Exception e) {
            throw new ExceptionService(DB_ACCESS_ERROR_MSG, e);
        }
    }

    public Message updateMessage(Message message) {
        LOGGER.info("Updating message: {}", message.getMessage_id());

        Optional<Message> retrievedMessage = this.getMessageById(message.getMessage_id());

        if (!retrievedMessage.isPresent()) {
            throw new ExceptionService("Message not found");
        }

        retrievedMessage.get().setMessage_text(message.getMessage_text());

        validateMessage(retrievedMessage.get());

        try {

            messageDAO.update(retrievedMessage.get());
            LOGGER.info("Updated message: {}", message);
            return retrievedMessage.get();
        } catch (Exception e) {
            throw new ExceptionService(DB_ACCESS_ERROR_MSG, e);
        }
    }

    public void deleteMessage(Message message) {
        LOGGER.info("Deleting message: {}", message);
        try {
            boolean hasDeletedMessage = messageDAO.delete(message);
            if (hasDeletedMessage) {
                LOGGER.info("Deleted message {}", message);
            } else {
                throw new NotFoundResponse("Message to delete not found");
            }
        } catch (Exception e) {
            throw new ExceptionService(DB_ACCESS_ERROR_MSG, e);
        }
    }

    private void validateMessage(Message message) {
        LOGGER.info("Validating message: {}", message);
        if (message.getMessage_text() == null || message.getMessage_text().trim().isEmpty()) {
            throw new ExceptionService("Message text cannot be null or empty");
        }
        if (message.getMessage_text().length() > 254) {
            throw new ExceptionService("Message text cannot exceed 254 characters");
        }
    }

    private void checkAccountPermission(Account account, int postedBy) {
        LOGGER.info("Checking account permissions for messages");
        if (account.getAccount_id() != postedBy) {
            throw new ExceptionService("Account not authorized to modify this message");
        }
    }
}
