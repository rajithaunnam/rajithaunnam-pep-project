package Service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import DAO.AccountDAO;
import DAO.Exception;
import Model.Account;

public class AccountService {
    private AccountDAO accountDao;
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountService.class);

    // Default constructor initializing the AccountDao object
    public AccountService() {
        accountDao = new AccountDAO();
    }

    public AccountService(AccountDAO accountDao) {
        this.accountDao = accountDao;
    }

    public Optional<Account> getAccountById(int id) {
        LOGGER.info("Fetching account with ID: {}", id);
        try {
            Optional<Account> account = accountDao.getById(id);
            LOGGER.info("Fetched account: {}", account.orElse(null));
            return account;
        } catch (Exception e) {
            throw new ExceptionService("Exception occurred while fetching account", e);
        }
    }

    public List<Account> getAllAccounts() {
        LOGGER.info("Fetching all accounts");
        try {
            List<Account> accounts = accountDao.getAll();
            LOGGER.info("Fetched {} accounts", accounts.size());
            return accounts;
        } catch (Exception e) {
            throw new ExceptionService("Exception occurred while fetching accounts", e);
        }
    }

    public Optional<Account> findAccountByUsername(String username) {
        LOGGER.info("Finding account by username: {}", username);
        try {
            Optional<Account> account = accountDao.findAccountByUsername(username);
            LOGGER.info("Found account: {}", account.orElse(null));
            return account;
        } catch (Exception e) {
            throw new ExceptionService("Exception occurred while finding account by username " + username, e);
        }
    }

    public Optional<Account> validateLogin(Account account) {
        LOGGER.info("Validating login");
        try {
            Optional<Account> validatedAccount = accountDao.validateLogin(account.getUsername(),
                    account.getPassword());
            LOGGER.info("Login validation result: {}", validatedAccount.isPresent());
            return validatedAccount;
        } catch (Exception e) {
            throw new ExceptionService("Exception occurred while validating login", e);
        }
    }

    public Account createAccount(Account account) {
        LOGGER.info("Creating account: {}", account);
        try {
            validateAccount(account);
            Optional<Account> searchedAccount = findAccountByUsername(account.getUsername());
            if (searchedAccount.isPresent()) {
                throw new ExceptionService("Account already exist");
            }
            Account createdAccount = accountDao.insert(account);
            LOGGER.info("Created account: {}", createdAccount);
            return createdAccount;
        } catch (Exception e) {
            throw new ExceptionService("Exception occurred while creating account", e);
        }
    }

    public boolean updateAccount(Account account) {
        LOGGER.info("Updating account: {}", account);
        try {
            account.setPassword(account.password);
            boolean updated = accountDao.update(account);
            LOGGER.info("Updated account: {}. Update successful {}", account, updated);
            return updated;
        } catch (Exception e) {
            throw new ExceptionService("Exception occurred while while updating account", e);
        }
    }

    public boolean deleteAccount(Account account) {
        LOGGER.info("Deleting account: {}", account);
        if (account.getAccount_id() == 0) {
            throw new IllegalArgumentException("Account ID cannot be null");
        }
        try {
            boolean deleted = accountDao.delete(account);
            LOGGER.info("Deleted account: {} . Deletion successful {}", account, deleted);
            return deleted;
        } catch (Exception e) {
            throw new ExceptionService("Exception occurred while while deleting account", e);
        }
    }

    private void validateAccount(Account account) {
        LOGGER.info("Validating account: {}", account);
        try {

            String username = account.getUsername().trim();
            String password = account.getPassword().trim();

            if (username.isEmpty()) {
                throw new ExceptionService("Username cannot be blank");
            }
            if (password.isEmpty()) {
                throw new ExceptionService("Password cannot be empty");
            }

            if (password.length() < 4) {
                throw new ExceptionService("Password must be at least 4 characters long");
            }
            if (accountDao.doesUsernameExist(account.getUsername())) {
                throw new ExceptionService("The username must be unique");
            }
        } catch (Exception e) {
            throw new ExceptionService("Exception occurred while validating account", e);
        }
    }

    public boolean accountExists(int accountId) {
        LOGGER.info("Checking account existence with ID: {}", accountId);
        try {
            Optional<Account> account = accountDao.getById(accountId);
            boolean exists = account.isPresent();
            LOGGER.info("Account existence: {}", exists);
            return exists;
        } catch (Exception e) {
            throw new ExceptionService("Exception occurred while checking account existence", e);
        }
    }
}
