package dataLayer.performers;

import dataLayer.DAO;
import dataLayer.entitites.Account;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AccountPerformer {

    public void transferMoney (int sender, int receiver, long amount) throws Exception{
        //get amount on sender account, if < amount

        PreparedStatement ps = DAO.connection.prepareStatement("SELECT balance FROM accounts WHERE id = ?");
        ps.setInt(1,sender);
        ResultSet rs = ps.executeQuery();

        if(rs.next()){
            if(rs.getInt(1) >= amount) {
                ps = DAO.connection.prepareStatement(
                        "UPDATE accounts SET balance = balance + ? WHERE id = ?");
                ps.setLong(1, amount);
                ps.setInt(2, receiver);
                ps.executeUpdate();

                ps = DAO.connection.prepareStatement(
                        "UPDATE accounts SET balance = balance - ? WHERE id = ?");
                ps.setLong(1, amount);
                ps.setInt(2, sender);
                ps.executeUpdate();

            }
            else {
                throw new Exception("your too poor to do that, you wanna sell your soul?");
            }
        }
        else {
            throw new Exception("Sender does not exists");
        }
    }

    public Account get(int id) {
        Account acc = null;
        try {
            PreparedStatement ps = DAO.connection.prepareStatement("SELECT * FROM accounts WHERE id=?;");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                acc = new Account(null, null, rs.getInt(1));
                acc.setBalance(rs.getInt(2)) ;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return acc;
    }

    public void delete(int id) {
        try {
            PreparedStatement ps = DAO.connection.prepareStatement("DELETE FROM accounts WHERE id=?;");
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void persist(int balance, int cpr, int cvr) {
        try {
            PreparedStatement ps = DAO.connection.prepareStatement("INSERT INTO accounts(balance, customerCpr, bankCvr) VALUES(?,?,?) ;");
            ps.setInt(1, balance);
            ps.setInt(2, cpr);
            ps.setInt(3, cvr);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void update(int id, int balance, int cpr, int cvr) {
        try {
            PreparedStatement ps = DAO.connection.prepareStatement("UPDATE accounts SET balance=?, cpr=?, cvr=? WHERE id=?;");
            ps.setInt(1, balance);
            ps.setInt(2, cpr);
            ps.setInt(3, cvr);
            ps.setInt(4, id);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public List<SimpleAccountTransferable> getAllAcountsForCustomer (String customer) throws Exception{
        List<SimpleAccountTransferable> accounts = new ArrayList<SimpleAccountTransferable>();
        PreparedStatement ps = DAO.connection.prepareStatement("SELECT * FROM accounts where customerCpr = ? ;");
        ps.setString(1, customer);
        ResultSet rs = ps.executeQuery();
        int x = 0;
        while(rs.next()){
            accounts.add(new SimpleAccountTransferable(rs.getInt(2),rs.getInt(1)));
            }
        rs.close();
        ps.close();
        return accounts;
    }
}
