package dao;

import model.Transaction;
import java.util.List;

public interface TransactionDAO {
    void saveTransaction(Transaction transaction);
    List<Transaction> getAllTransactions();
}