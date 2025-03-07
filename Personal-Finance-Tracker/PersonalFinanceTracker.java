import java.sql.*;
import java.util.Scanner;

public class PersonalFinanceTracker {

    static final String DB_URL = "jdbc:mysql://localhost:3306/finance_tracker";
    static final String USER = "root";
    static final String PASS = "PassWord@123";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            System.out.println("Got Connected to my database");
            while (true) {
                System.out.println("\n1.To Add Transaction\n2.To View Transactions\n3.To View Summary\n4. Exit");
                int choice = sc.nextInt();
                sc.nextLine();
                switch (choice) {
                    case 1:
                        addTransaction(conn, sc);
                        break;
                    case 2:
                        viewTransactions(conn);
                        break;
                    case 3:
                        viewSummary(conn);
                        break;
                    case 4:
                        System.out.println("Exiting");
                        return;
                    default:
                        System.out.println("Invalid choice");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void addTransaction(Connection conn, Scanner sc) throws SQLException {
        System.out.println("Enter transaction type (Income/Expense):");
        String type = sc.nextLine();
        System.out.println("Enter amount:");
        double amount = sc.nextDouble();
        sc.nextLine();
        System.out.println("Enter category:");
        String category = sc.nextLine();
        System.out.println("Enter description:");
        String description = sc.nextLine();
        System.out.println("Enter transaction date (YYYY-MM-DD):");
        String date = sc.nextLine();

        String sql = "INSERT INTO transactions (transaction_type, amount, category, description, transaction_date) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, type);
            pstmt.setDouble(2, amount);
            pstmt.setString(3, category);
            pstmt.setString(4, description);
            pstmt.setString(5, date);
            pstmt.executeUpdate();
            System.out.println("Transaction added successfully!");
        }
    }

    private static void viewTransactions(Connection conn) throws SQLException {
        String sql = "SELECT * FROM transactions ORDER BY transaction_date DESC";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.printf("%-15s %-10s %-10s %-20s %-30s %-15s%n", "Transaction ID", "Type", "Amount", "Category", "Description", "Date");
            while (rs.next()) {
                System.out.printf("%-15d %-10s %-10f %-20s %-30s %-15s%n",
                        rs.getInt("transaction_id"),
                        rs.getString("transaction_type"),
                        rs.getDouble("amount"),
                        rs.getString("category"),
                        rs.getString("description"),
                        rs.getDate("transaction_date"));
            }
        }
    }

    private static void viewSummary(Connection conn) throws SQLException {
        String incomeSql = "SELECT SUM(amount) AS total_income FROM transactions WHERE transaction_type = 'Income'";
        String expenseSql = "SELECT SUM(amount) AS total_expense FROM transactions WHERE transaction_type = 'Expense'";

        try (
                Statement incomeStmt = conn.createStatement();
                ResultSet incomeRs = incomeStmt.executeQuery(incomeSql);
                Statement expenseStmt = conn.createStatement();
                ResultSet expenseRs = expenseStmt.executeQuery(expenseSql)
        ) {
            double totalIncome = 0, totalExpense = 0;

            if (incomeRs.next()) {
                totalIncome = incomeRs.getDouble("total_income");
            }
            if (expenseRs.next()) {
                totalExpense = expenseRs.getDouble("total_expense");
            }

            System.out.println("\nMY Financial Summary:");
            System.out.println("Total Income: " + totalIncome);
            System.out.println("Total Expense: " + totalExpense);
            System.out.println("Balance: " + (totalIncome - totalExpense));
        }
    }

}

