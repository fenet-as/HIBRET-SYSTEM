package dao.impl;

import dao.TransactionDAO;
import model.Transaction;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAOImpl implements TransactionDAO {
    // Simulated Database
    private static List<Transaction> database = new ArrayList<>();

    @Override
    public void saveTransaction(Transaction transaction) {
        // SQL: INSERT INTO transactions ...
        database.add(transaction);
    }

    @Override
    public List<Transaction> getAllTransactions() {
        // SQL: SELECT * FROM transactions
        return new ArrayList<>(database);
    }
}
