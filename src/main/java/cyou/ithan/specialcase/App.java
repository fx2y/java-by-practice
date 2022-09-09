package cyou.ithan.specialcase;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        // DB seeding
        LOGGER.info("Db seeding: " + "1 user: {\"ignite1771\", amount = 1000.0}, "
                + "2 products: {\"computer\": price = 800.0, \"car\": price = 20000.0}");
        Db.getInstance().seedUser("ignite1771", 1000.0);
        Db.getInstance().seedItem("computer", 800.0);
        Db.getInstance().seedItem("car", 20000.0);

        final var applicationServices = new ApplicationServices();
        ReceiptViewModel receipt;

        LOGGER.info("[REQUEST] User: " + "abc123" + " buy product: " + "tv");
        receipt = applicationServices.loggedInUserPurchase("abc123", "tv");
        ReceiptViewModel.show(receipt);
        MaintenanceLock.getInstance().setLock(false);
        LOGGER.info("[REQUEST] User: " + "abc123" + " buy product: " + "tv");
        receipt = applicationServices.loggedInUserPurchase("abc123", "tv");
        ReceiptViewModel.show(receipt);
        LOGGER.info("[REQUEST] User: " + "ignite1771" + " buy product: " + "tv");
        receipt = applicationServices.loggedInUserPurchase("ignite1771", "tv");
        ReceiptViewModel.show(receipt);
        LOGGER.info("[REQUEST] User: " + "ignite1771" + " buy product: " + "car");
        receipt = applicationServices.loggedInUserPurchase("ignite1771", "car");
        ReceiptViewModel.show(receipt);
        LOGGER.info("[REQUEST] User: " + "ignite1771" + " buy product: " + "computer");
        receipt = applicationServices.loggedInUserPurchase("ignite1771", "computer");
        ReceiptViewModel.show(receipt);
    }

    sealed interface ReceiptViewModel {
        static void show(ReceiptViewModel receipt) {
            switch (receipt) {
                case ReceiptDto e -> ReceiptDto.LOGGER.info("Receipt: " + e.price + " paid");
                case InvalidUser e -> InvalidUser.LOGGER.info("Invalid user: " + e.userName);
                case OutOfStock e ->
                        OutOfStock.LOGGER.info("Out of stock: " + e.itemName + " for user = " + e.userName + " to buy");
                case InsufficientFunds e ->
                        InsufficientFunds.LOGGER.info("Insufficient funds: " + e.amount + " of user: " + e.userName + " for buying item: " + e.itemName);
                case DownForMaintenance ignored -> DownForMaintenance.LOGGER.info("Down for maintenance");
            }
        }
    }

    record ReceiptDto(Double price) implements ReceiptViewModel {
        static final Logger LOGGER = LoggerFactory.getLogger(ReceiptDto.class);
    }

    record OutOfStock(String userName, String itemName) implements ReceiptViewModel {
        static final Logger LOGGER = LoggerFactory.getLogger(OutOfStock.class);
    }

    record InvalidUser(String userName) implements ReceiptViewModel {
        static final Logger LOGGER = LoggerFactory.getLogger(InvalidUser.class);
    }

    record InsufficientFunds(String userName, Double amount, String itemName) implements ReceiptViewModel {
        static final Logger LOGGER = LoggerFactory.getLogger(InsufficientFunds.class);
    }

    record DownForMaintenance() implements ReceiptViewModel {
        static final Logger LOGGER = LoggerFactory.getLogger(DownForMaintenance.class);
    }

    public static class DomainServices {
        public ReceiptViewModel purchase(String userName, String itemName) {
            var user = Db.getInstance().findUserByUserName(userName);
            if (user == null) {
                return new InvalidUser(userName);
            }
            var account = Db.getInstance().findAccountByUser(user);
            return purchase(user, account, itemName);
        }

        private ReceiptViewModel purchase(Db.User user, Db.Account account, String itemName) {
            var item = Db.getInstance().findProductByItemName(itemName);
            if (item == null) {
                return new OutOfStock(user.userName, itemName);
            }
            var receipt = Db.userPurchase(item);
            var transaction = Db.accountWithdraw(account, receipt.price);
            if (transaction == null) {
                return new InsufficientFunds(user.userName, account.amount, itemName);
            }
            return receipt;
        }
    }

    record Db(Map<String, User> userName2User, Map<User, Account> user2Account, Map<String, Product> itemName2Product) {
        private static Db instance;

        public static synchronized Db getInstance() {
            if (instance == null) {
                instance = new Db(new HashMap<>(), new HashMap<>(), new HashMap<>());
            }
            return instance;
        }

        public static ReceiptDto userPurchase(Db.Product item) {
            return new ReceiptDto(item.price);
        }

        public static MoneyTransaction accountWithdraw(Db.Account account, Double price) {
            if (price > account.amount) {
                return null;
            }
            return new MoneyTransaction(account.amount, price);
        }

        public void seedUser(String userName, Double amount) {
            var user = new Db.User(userName);
            userName2User.put(userName, user);
            var account = new Db.Account(amount);
            user2Account.put(user, account);
        }

        public void seedItem(String itemName, Double price) {
            var item = new Db.Product(price);
            itemName2Product.put(itemName, item);
        }

        public Db.User findUserByUserName(String userName) {
            if (!userName2User.containsKey(userName)) {
                return null;
            }
            return userName2User.get(userName);
        }

        public Db.Account findAccountByUser(Db.User user) {
            if (!user2Account.containsKey(user)) {
                return null;
            }
            return user2Account.get(user);
        }

        public Db.Product findProductByItemName(String itemName) {
            if (!itemName2Product.containsKey(itemName)) {
                return null;
            }
            return itemName2Product.get(itemName);
        }

        record User(String userName) {
        }

        record Account(Double amount) {
        }

        record Product(Double price) {
        }
    }

    public static class ApplicationServices {
        private final DomainServices domain = new DomainServices();

        public ReceiptViewModel loggedInUserPurchase(String userName, String itemName) {
            if (isDownForMaintenance())
                return new DownForMaintenance();
            return this.domain.purchase(userName, itemName);
        }

        private boolean isDownForMaintenance() {
            return MaintenanceLock.getInstance().isLock();
        }
    }

    record MoneyTransaction(Double amount, Double price) {
    }

    @Getter
    public static class MaintenanceLock {
        private static final Logger LOGGER = LoggerFactory.getLogger(MaintenanceLock.class);
        private static MaintenanceLock instance;
        private boolean lock = true;

        public static synchronized MaintenanceLock getInstance() {
            if (instance == null) {
                instance = new MaintenanceLock();
            }
            return instance;
        }

        public void setLock(boolean lock) {
            this.lock = lock;
            LOGGER.info("Maintenance lock is set to: {}", lock);
        }
    }
}
