package com.demo;

import java.sql.Date;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.sql.*;
import java.time.*;

public class lifeCycleService {
    static Scanner sc = new Scanner(System.in);

    public  static  void initLifeCycle() throws  SQLException{
        Connection conn = DBConnection.getConnection();
        System.out.println("Enter the Case ID : ");
        int caseId = sc.nextInt();
        sc.nextLine();

        List<StageDefinition> stDef = stageTemplate.getDefaultStage();

        for(StageDefinition stg : stDef){
            PreparedStatement ps_wf = conn.prepareStatement(
                    "insert into workflow_stage(case_id, sequence_number, role_responsible, sla_days) values (?,?,?,?)"
            );
            ps_wf.setInt(1, caseId);
            ps_wf.setInt(2, stg.getSeqNum());
            ps_wf.setString(3, stg.getRole());
            ps_wf.setInt(4, stg.getSlaDays());

            ps_wf.executeUpdate();
        }

        PreparedStatement firstStage = conn.prepareStatement("select stage_id from workflow_stage where case_id = ? and sequence_number = 1");
        firstStage.setInt(1, caseId);

        ResultSet rs = firstStage.executeQuery();
        int stageId = -1;
        if(rs.next()){
            stageId = rs.getInt("stage_id");
        }
        PreparedStatement ps_sla = conn.prepareStatement("insert into sla_records " +
                "(case_id, stage_id, start_id, status) values (?, ?, ?, ?)");

        ps_sla.setInt(1, caseId);
        ps_sla.setInt(2, stageId);
        ps_sla.setDate(3, Date.valueOf(LocalDate.now()));
        ps_sla.setString(4, "OnTime");

        ps_sla.executeUpdate();

        System.out.println("Lifecycke Initialised Succesfully");
        conn.close();
    }

    public static void advanceStage() throws SQLException{
        Connection conn = DBConnection.getConnection();

        System.out.println("Enter Case ID : ");
        int caseID = sc.nextInt();
        sc.nextLine();

        PreparedStatement all_sla = conn.prepareStatement("select * from sla_records where case_id = ? and end_date is null");
        all_sla.setInt(1, caseID);

        ResultSet rs_all = all_sla.executeQuery();

        int stageID;
        Date startDate;
        int slaID;
        if(rs_all.next()){
            
                System.out.println("Case ID : "+ rs_all.getInt("case_id"));
                System.out.println("Stage ID : "+ rs_all.getInt("stage_id"));
                System.out.println("Start Date : "+ rs_all.getDate("start_id"));
                System.out.println("Status : "+ rs_all.getString("status"));
                System.out.println("------------------------------------------");
                stageID = rs_all.getInt("stage_id");
                startDate = rs_all.getDate("start_id");
                slaID = rs_all.getInt("sla_record_id");
        }
        
        else{
            System.out.println("No active stage of given Case ID found.");
            return;
        }

        

        PreparedStatement sla_days = conn.prepareStatement("select SLA_Days, Sequence_number from workflow_stage where stage_id = ?");
        sla_days.setInt(1, stageID);

        ResultSet rs_sla = sla_days.executeQuery();
        rs_sla.next();

        int days = rs_sla.getInt("sla_days");
        int sqno = rs_sla.getInt("Sequence_number");

        long days_taken = ChronoUnit.DAYS.between(startDate.toLocalDate(), LocalDate.now());

        String status = (days >= days_taken) ? "OnTime" : "Breached";
        PreparedStatement up_sla = conn.prepareStatement("update sla_records set end_date = ?, status = ? where sla_record_id=?");
        up_sla.setDate(1, Date.valueOf(LocalDate.now()));
        up_sla.setString(2, "Completed");
        up_sla.setInt(3, slaID);

        up_sla.executeUpdate();

        PreparedStatement nextStage = conn.prepareStatement("select stage_id from workflow_stage where case_id = ? and sequence_number = ?");
        nextStage.setInt(1, caseID);
        nextStage.setInt(2, sqno+1);

        ResultSet nextStg = nextStage.executeQuery();

        int nxtStgId = -1;

        if(nextStg.next()){
            nxtStgId = nextStg.getInt("stage_id");
        }

        PreparedStatement newVal = conn.prepareStatement("insert into sla_records(case_id, stage_id, start_id, status)" +
                "values (?, ?, ?, ?)");
        newVal.setInt(1, caseID);
        newVal.setInt(2, nxtStgId);
        newVal.setDate(3, Date.valueOf(LocalDate.now()));
        newVal.setString(4, "OnTime");

        newVal.executeUpdate();

        System.out.println("Advanced to next Step");
        System.out.println("---------------------------");

        conn.close();
    }

    public static void checkBreach() throws  SQLException{
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement("select sl.sla_record_id, sl.start_id, wk.sla_days, sl.status" +
                "from sla_records sl" +
                "join workflow_stage wk on" +
                "sl.stage_id = wk.stage_id where sl.end_date is null");

        ResultSet rs = ps.executeQuery();

        System.out.println("Pending Case Files are :- ");
        while(rs.next()){
            int slaID = rs.getInt("sla_record_id");
            Date startDate = rs.getDate("start_id");
            long slaDays = rs.getInt("sla_days");
            String status = rs.getString("status");

            System.out.println("SLA Record ID : " + slaID);
            System.out.println("Start Date : "+startDate);
            System.out.println("Number of Days Alloted : "+slaDays);

            long time_taken = ChronoUnit.DAYS.between(startDate.toLocalDate(), LocalDate.now());

            String newStatus = (time_taken > slaDays) ? "Breached" : "Completed";

            System.out.println("Status : "+newStatus);
            if(newStatus.equals("Breached"))
            {
                System.out.println("Update Status ?");
                System.out.println("1. Yes");
                System.out.println("2. No");
                int ch = sc.nextInt();
                sc.nextLine();
                if(ch == 1){
                    PreparedStatement ps1 = conn.prepareStatement("update sla_records set status = ? where sla_record_id = ?");
                    ps1.setString(1, "Breached");
                    ps1.setInt(2, slaID);

                    ps1.executeUpdate();
                }
                else{
                    return;
                }
            }
            else {
                System.out.println("No Breaches Found.");
                return;
            }
        }
        System.out.println("SLA Breach Check Completed.");
        conn.close();
    }
}
