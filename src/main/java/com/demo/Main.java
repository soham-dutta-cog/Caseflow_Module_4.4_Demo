package com.demo;

import java.sql.SQLException;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws SQLException {


        Scanner sc = new Scanner(System.in);

        System.out.println("Login Page");
        System.out.println("-----------------------");
        System.out.println("1. Litigant");
        System.out.println("2. Clerk");
        System.out.println("3. Exit");
        int c = sc.nextInt();

        UserMenu u = new UserMenu();
        ClerkService cl = new ClerkService();

        switch (c){
            case 1 -> u.mainMenu();
            case 2 -> cl.mainMenu();
            case 3 -> { return; }
        }
    }
}