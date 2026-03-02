**Module 4.4 — Case Lifecycle and SLA Engine**.

### Overview

**Module purpose** This module implements a lightweight case lifecycle engine that models ordered workflow stages for a case and tracks SLA windows per stage. It supports:

*   **Initialize Lifecycle** for a case (create ordered stages and SLA records).
    
*   **Advance Stage** (complete current stage and start the next stage atomically).
    
*   **Check Breach** (detect and mark SLA breaches for active stages).
    

**Scope** This repo contains only a small backend CLI implementation for Module 4.4. It is minimal and focused on correctness, transactions, and clear DB state transitions.

### Database Schema

**WorkflowStage table**

| Column | Type | Description |
| --- | --- | --- |
| StageID | INT AUTO_INCREMENT PRIMARY KEY | Unique stage identifier |
| CaseID | INT | FK to cases table |
| SequenceNumber | INT | Order of the stage (1 = first) |
| RoleResponsible | VARCHAR(100) | Role assigned to the stage |
| SLA_Days | INT | SLA length in days for the stage |

**SLARecord table**

| Column | Type | Description |
| --- | --- | --- |
| SLARecordID | INT AUTO_INCREMENT PRIMARY KEY | Unique SLA record id |
| CaseID | INT | FK to cases table |
| StageID | INT | FK to WorkflowStage.StageID |
| StartDate | TIMESTAMP NULL | When the stage actually started |
| EndDate | TIMESTAMP NULL | Deadline or completion timestamp |
| Status | VARCHAR(50) | One of PENDING, IN_PROGRESS, COMPLETED, BREACHED |

**Notes**

*   The first stage is initialized with `StartDate = now`, `EndDate = now + SLA_Days`, and `Status = OnTime`.
    
*   Subsequent stages are created with `StartDate = NULL`, `EndDate = NULL`, and `Status = PENDING`.
    
*   All lifecycle operations use transactions to avoid partial updates.
    

### Installation and Run

**Prerequisites**

*   Java JDK 17 or later
    
*   Maven
    
*   MySQL server with a database for testing
    

**Quick DB setup** Run these SQL snippets to create the two tables used by this module:

sql

    CREATE TABLE workflow_stage (
      stage_id INT AUTO_INCREMENT PRIMARY KEY,
      case_id INT NOT NULL,
      sequence_number INT NOT NULL,
      role_responsible VARCHAR(100),
      sla_days INT
    );
    
    CREATE TABLE sla_records (
      sla_record_id INT AUTO_INCREMENT PRIMARY KEY,
      case_id INT NOT NULL,
      stage_id INT NOT NULL,
      start_date TIMESTAMP NULL,
      end_date TIMESTAMP NULL,
      status VARCHAR(50) DEFAULT 'PENDING'
    );
     

**DB connection** Edit `DBConnection.java` to point to your MySQL instance. For developmental purposes I have used a local DB and a test user.

### Usage Examples

**Initialize lifecycle for a case**

*   Menu: Clerk → Module 4.4 → Initialize Case Lifecycle
    
*   Input: `caseId` (existing case id)
    
*   Result: `workflow_stage` rows inserted; `sla_records` rows inserted; first stage set to `IN_PROGRESS`.
    

**Advance stage for a case**

*   Menu: Clerk → Module 4.4 → Advance Case Lifecycle Stage
    
*   Input: `caseId`
    
*   Result: current `IN_PROGRESS` SLA record marked `COMPLETED` with `EndDate = now`; next stage SLA record set `StartDate = now`, `EndDate = now + SLA_Days`, `Status = OnTime`. If no next stage exists, lifecycle is complete.
    

**Check breach for a case**

*   Menu: Clerk → Module 4.4 → Check Breach
    
*   Input: `caseId`
    
*   Result: any `IN_PROGRESS` SLA record with `EndDate < today` is updated to `BREACHED`.
        

### Implementation Details

**Key classes and methods**

*   `lifeCycleService`
    
    *   `initLifeCycle()` — reads `caseId`, inserts `workflow_stage` rows from a stage template, inserts initial `sla_records`, sets first stage `OnTime`.
        
    *   `advanceStage()` — finds current `OnTime` SLA record, completes it changing status to `Completed`, computes next stage, inserts or updates next SLA record and sets it `OnTime`. Uses `rs.next()` checks before reading result sets.
        
    *   `checkBreach()` — scans `OnTime` records and marks breaches.
        
*   `stageTemplate` **and** `StageDefinition`
    
    *   Provide the default ordered stage definitions and SLA days used by `initLifeCycle()`.
              

**Contributing**

*   This module is scoped to Module 4.4. If you want to extend it, open a branch, add tests for lifecycle transitions, and submit a PR with a clear description.
    
