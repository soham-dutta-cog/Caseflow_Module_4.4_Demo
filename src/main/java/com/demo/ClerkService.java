package com.demo;
import java.awt.desktop.PreferencesEvent;
import java.util.*;
import java.sql.*;
public class ClerkService {
    Scanner sc = new Scanner(System.in);

    public void mainMenu() throws SQLException {
        while(true) {
            System.out.println("1. Check Updates (Notifications)");
            System.out.println("2. Manage Documents");
            System.out.println("3. Schedule Hearings (not dnoe till now)");
            System.out.println("\n---------Module 4.4---------");
            System.out.println("4. Initialize Case Lifecycle");
            System.out.println("5. Advance Case Lifecycle Stage");
            System.out.println("6. Check Breach");
            System.out.println("7. Go Back");
            int c = sc.nextInt();
            sc.nextLine();
            switch (c){
                case 1 -> chkUpdts();
                case 2 -> mngDocs();
                case 3 -> { return; }
                case 4 -> { lifeCycleService.initLifeCycle(); }
                case 5 -> { lifeCycleService.advanceStage(); }
                case 6 -> { return; }
                case 7 -> { return; }
                default -> System.out.println("Invalid Input, choose from the list");
            }
        }
    }



    public void chkUpdts() throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("select id, title from documents where status = 'PENDING'");
             ResultSet rs = ps.executeQuery()) {
            System.out.println("The Pending Documents are :-");
            while (rs.next()) {
                System.out.println("ID : " + rs.getInt("id"));
                System.out.println("Title : " + rs.getString("title"));
                System.out.println("----------------------------------------------");
            }
            conn.close();
        }
    }

    public  void mngDocs() throws SQLException {
        Connection conn = DBConnection.getConnection();
//        PreparedStatement ps_pend = conn.prepareStatement("select * from documents where STATUS = 'PENDING'");
//        ResultSet rs_pend = ps_pend.executeQuery();

        while(true){
            System.out.println("1. View all documents");
            System.out.println("2. View pending documents");
            System.out.println("3. Update Document");
            System.out.println("4. Go Back");
            int c = sc.nextInt();
            switch (c){
                case 1 :
                    PreparedStatement ps_all = conn.prepareStatement("select * from documents");
                    ResultSet rs_all = ps_all.executeQuery();
                    System.out.println("All the documents are :- ");
                    while(rs_all.next()){
                        System.out.println("ID : "+rs_all.getInt("id"));
                        System.out.println("TITLE : "+rs_all.getString("title"));
                        System.out.println("STATUS : "+rs_all.getString("status"));
                        System.out.println("REASON : "+rs_all.getString("reason"));
                        System.out.println("---------------------------------------------------");
                    }
                    break;
                case 2 :
                    chkUpdts();
                    break;
                case 3:
                    while(true){
                        System.out.println("Enter the document ID : ");
                        int id = sc.nextInt();
                        sc.nextLine();
                        System.out.println("1. Verify Document");
                        System.out.println("2. Reject Document");
                        System.out.println("3. Go Back");
                        int ch = sc.nextInt();
                        if(ch == 1) {
                            PreparedStatement ps_noti = conn.prepareStatement("update documents set status = 'VERIFIED' where id = ?");
                            ps_noti.setInt(1, id);
                            ps_noti.executeUpdate();
                        }
                        else if(ch == 2) {
                            PreparedStatement ps_noti = conn.prepareStatement("update documents set status = 'NOT VERIFIED', reason = ? where id = ?");
                            System.out.println("Enter rejection reason : ");
                            String s = sc.nextLine();
                            ps_noti.setString(1, s);
                            ps_noti.setInt(2, id);
                            ps_noti.executeUpdate();
                        }
                        else if(ch == 3)
                            break;
                        else{
                            System.out.println("Invalid input, choose from the list");
                        }
                    }
                case 4:
                    return;
                default:
                    System.out.println("Invalid Input, choose from teh list");
            }
        }

    }
}
