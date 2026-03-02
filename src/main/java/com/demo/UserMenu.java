package com.demo;
import java.util.*;
import java.sql.*;

public class UserMenu {

    Scanner sc = new Scanner(System.in);
    public void mainMenu() throws SQLException {
        while(true){
            System.out.println("1. File Case");
            System.out.println("2. Check Updates");
            System.out.println("3. Upload Documents");
            System.out.println("4. Exit");
            System.out.println("/n");
            System.out.println("Enter your choice : ");
            int c = sc.nextInt();
            switch(c){
                case 1 -> caseFile();
                case 2 -> chkUpdts();
                case 3 -> upDocs();
                case 4 -> { return; }
            }
        }
    }

    public void chkUpdts() throws SQLException {
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement("select * from documents where id = ?");
        System.out.println("Enter your user ID : ");
        int id = sc.nextInt();
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            System.out.println("ID : "+rs.getInt("id"));
            System.out.println("Title : "+rs.getString("title"));
            System.out.println("User ID : "+rs.getInt("user"));
            System.out.println("Case ID : "+rs.getInt("case_id"));
            System.out.println("Status : "+rs.getString("status"));
            System.out.println("Reason : "+rs.getString("reason"));
            System.out.println("----------------------------------------------");
        }
    }
    public void upDocs() throws SQLException {

    }
    public void caseFile(){

    }

}
