package util;


import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import dao.AccountDao;
import dao.AppUserDao;
import dao.BankDao;
import dao.TransactionDao;
import entity.Account;
import entity.AppUser;
import entity.Transaction;
import exception.NoSuchAccountFoundException;
import exception.NoSuchBankFoundException;
import exception.NoSuchUserFoundException;
import lombok.experimental.UtilityClass;


import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Objects;
import java.util.Optional;

@UtilityClass
public class CheckPrinter {
    private final AccountDao accountDao = AccountDao.getInstance();
    private final BankDao bankDao = BankDao.getInstance();
    private final TransactionDao transactionDao = TransactionDao.getInstance();
    private final AppUserDao appUserDao = AppUserDao.getInstance();
    private static final String RECEIPT = """
            ---------------------------------------------
            |                  Receipt                  |
            | receipt:                       %10d |
            | %s             %20s |
            | transaction type:                %8s |
            | Senders bank:        %20s |
            | Receivers bank:      %20s |
            | Senders Account:     %20s |
            | Receivers Account:   %20s |
            | Total:                  %17.2f |
            ---------------------------------------------
            """;


    private static final String ACCOUNT_STATEMENT = """
                                        Account statement
                                         Clever-Bank
             Client               | %-35s
             Account              | %-20d
             Currency             | %-5s
             Opening date         | %-10s
             Period               | %-23s
             Current date         | %-17s
             Balance              | %-17.2f
                 Date   | Type                             | Amount
            ---------------------------------------------------------------
             """;
    private static final String MONEY_STATEMENT = """
                                        Money statement
                                             Clever-Bank
             Client                          %s
             Account                      %d
             Currency                    %s
             Opening date             %s
             Period                        %s
             Current date              %s
             Balance                     %.2f
            -----------------------------------------------------------------------------
             Income                       %.2f
             Outgo                        %.2f
            """;

    private static final String TRANSACTION = """
             %10s | %-10s                       | %-17.2f
            """;

    /**
     * Prints money statement (income and outgo) depending on from-date to to-date in statement-money folder
     *
     * @param accId account id for which the money statement will be printed
     * @param from  start date of calculating income and outgo
     * @param to    finish date of calculating income and outgo
     * @throws NoSuchAccountFoundException if account with such accId not found
     * @throws NoSuchUserFoundException    if user that has this account not found
     */
    public static void printMoneyStatement(Integer accId, LocalDateTime from, LocalDateTime to) throws NoSuchAccountFoundException, NoSuchUserFoundException {
        Account account = accountDao.getById(accId);
        AppUser appUser = appUserDao.getById(account.getAppUserId());
        DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("dd.MM.yyy").toFormatter();

        Double income = accountDao.getIncomeByIdAndPeriod(accId, from, to);
        Double outgo = accountDao.getOutgoByIdAndPeriod(accId, from, to);

        String statement =
                MONEY_STATEMENT.formatted(
                        appUser.getFullName(),
                        accId,
                        account.getCurrency().name(),
                        account.getOpeningDate().format(formatter),
                        from.format(formatter) + " - " + to.format(formatter),
                        LocalDateTime.now().format(new DateTimeFormatterBuilder().appendPattern("dd.MM.yyy HH:mm:ss").toFormatter()),
                        account.getBalance(),
                        income,
                        outgo);

        synchronized (CheckPrinter.class) {
            try {
                Document doc = new Document();
                File directory = new File(PropertiesUtil.getYaml("absoluteProjectPath") + "\\statement-money");
                if (!directory.exists()) directory.mkdirs();

                PdfWriter writer = PdfWriter.getInstance(doc,
                        new FileOutputStream(directory.getAbsolutePath() + "\\receipt" + directory.listFiles().length + ".pdf"));

                Font font = new Font(Font.COURIER, 10, Font.getFontStyleFromName(BaseFont.CP1250), Color.BLACK);
                Chunk chunk = new Chunk(statement, font);
                doc.open();
                Paragraph paragraph = new Paragraph("");
                doc.add(paragraph);
                doc.add(chunk);
                doc.close();
                writer.close();
            } catch (DocumentException | FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Prints account statement (all transactions) depending on one of 3 periods in account-statement folder
     *
     * @param period one of 3 available periods (CURRENT_YEAR, CURRENT_MONTH, WHOLE_PERIOD)
     * @param accId  account id for which the account statement will be printed
     * @throws NoSuchAccountFoundException if account with such accId not found
     * @throws NoSuchUserFoundException    if user that has this account not found
     * @see AccountStatementPeriod
     */
    public static void printAccountStatement(AccountStatementPeriod period, Integer accId) throws NoSuchAccountFoundException, NoSuchUserFoundException {
        Account account = accountDao.getById(accId);
        AppUser appUser = appUserDao.getById(account.getAppUserId());


        DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
                .appendPattern("dd.MM.yyy").toFormatter();

        LocalDateTime date = switch (period) {
            case CURRENT_YEAR -> LocalDateTime.now()
                    .minusMonths(LocalDateTime.now().getMonth().getValue() - 1)
                    .minusDays(LocalDateTime.now().getDayOfMonth() - 1);
            case CURRENT_MONTH -> LocalDateTime.now().minusDays(LocalDateTime.now().getDayOfMonth() - 1);
            case WHOLE_PERIOD -> account.getOpeningDate();
        };

        String statement =
                ACCOUNT_STATEMENT.formatted(
                        appUser.getFullName(),
                        accId,
                        account.getCurrency().name(),
                        account.getOpeningDate().format(dateTimeFormatter),
                        date.format(dateTimeFormatter) + " - " + LocalDateTime.now().format(dateTimeFormatter),
                        LocalDateTime.now().format(new DateTimeFormatterBuilder().appendPattern("dd.MM.yyy HH:mm:ss").toFormatter()),
                        account.getBalance());

        synchronized (CheckPrinter.class) {
            File directory = new File(PropertiesUtil.getYaml("absoluteProjectPath") + "\\account-statement");
            if (!directory.exists()) directory.mkdirs();

            try (PrintWriter writer = new PrintWriter(directory.getAbsolutePath() + "\\statement" + directory.listFiles().length + ".txt")) {

                writer.write(statement);

                //writing all matching transactions to a file
                transactionDao.findAll().stream()
                        .filter(transaction -> Objects.equals(transaction.getReceiverAccId().orElse(0), accId)
                                               || Objects.equals(transaction.getSenderAccId().orElse(0), accId))
                        .filter(transaction -> transaction.getDate()
                                .isAfter(LocalDateTime.from(date)))
                        .forEach(transaction -> writer.write(TRANSACTION.formatted(
                                transaction.getDate().format(dateTimeFormatter),
                                transaction.getType().name(),
                                Objects.equals(transaction.getSenderAccId().orElse(0), accId) ? -transaction.getTotal() : transaction.getTotal()
                        )));

            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Prints receipt depending on transaction in check folder
     *
     * @param transaction transaction to be printed
     * @throws NoSuchBankFoundException    if bank not found
     * @throws NoSuchAccountFoundException if one of accounts(senders or receivers) not found
     * @see Transaction
     */
    public static void printReceipt(Transaction transaction) throws NoSuchBankFoundException, NoSuchAccountFoundException {
        File directory = new File(PropertiesUtil.getYaml("absoluteProjectPath") + "\\check");

        synchronized (CheckPrinter.class) {
            if (!directory.exists()) directory.mkdirs();
            int fileCount = directory.listFiles().length;

            try (PrintWriter writer = new PrintWriter("check/check" + fileCount + ".txt")) {
                writer.write(prepareReceipt(transaction, fileCount));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Prepares receipt depending on transaction
     *
     * @param transaction transaction to be printed
     * @param receiptNo   receipt number for printing
     * @return String - check prepared for printing
     * @throws NoSuchAccountFoundException if one of accounts(senders or receivers) not found
     * @throws NoSuchBankFoundException    if bank not found
     * @see Transaction
     */
    private String prepareReceipt(Transaction transaction, int receiptNo) throws NoSuchAccountFoundException, NoSuchBankFoundException {
        Account senderAccount;
        Account receiverAccount;

        String senderBankName = "";
        String receiverBankName = "";

        Optional<Integer> receiverAccId = transaction.getReceiverAccId();
        Optional<Integer> senderAccId = transaction.getSenderAccId();
        if (receiverAccId.isPresent()) {
            receiverAccount = accountDao.getById(receiverAccId.get());
            receiverBankName = bankDao.getById(receiverAccount.getBankId()).getName();
        }
        if (senderAccId.isPresent()) {
            senderAccount = accountDao.getById(senderAccId.get());
            senderBankName = bankDao.getById(senderAccount.getBankId()).getName();
        }

        return RECEIPT.formatted(
                receiptNo,
                transaction.getDate().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                transaction.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyy")),
                transaction.getType().name(),
                senderBankName,
                receiverBankName,
                senderAccId.map(Object::toString).orElse(""),
                receiverAccId.map(Object::toString).orElse(""),
                transaction.getTotal()
        );
    }
}
