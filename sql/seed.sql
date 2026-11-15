-- Demo data. Run AFTER the application has started once (Hibernate creates the tables).
USE bank_db;
INSERT INTO account_type (id,name,min_balance,interest_rate,daily_withdraw_limit) VALUES
(1,'SAVINGS',1000.00,3.50,50000.00),(2,'CURRENT',5000.00,0.00,200000.00);
INSERT INTO branch (id,name,ifsc_code,city,address,phone) VALUES
(1,'Bhopal Main','NOVA0000101','Bhopal','MP Nagar Zone 1','0755000101'),
(2,'Indore Central','NOVA0000102','Indore','Vijay Nagar','0731000102'),
(3,'Jabalpur Civil Lines','NOVA0000103','Jabalpur','Civil Lines','0761000103');
INSERT INTO customer (id,full_name,email,phone,pan,dob,address,monthly_income,created_at) VALUES
(1,'Aarav Sharma','aarav@example.com','9876500001','ABCDE1234F','1990-04-12','Bhopal',60000.00,NOW()),
(2,'Diya Verma','diya@example.com','9876500002','BCDEF2345G','1993-09-30','Indore',45000.00,NOW()),
(3,'Rohan Gupta','rohan@example.com','9876500003','CDEFG3456H','1985-01-20','Jabalpur',90000.00,NOW()),
(4,'Meera Iyer','meera@example.com','9876500004','DEFGH4567J','1998-12-05','Bhopal',30000.00,NOW()),
(5,'Kabir Khan','kabir@example.com','9876500005','EFGHI5678K','1988-07-18','Indore',75000.00,NOW());
INSERT INTO employee (id,name,designation,salary,branch_id) VALUES
(1,'Sunita Rao','Branch Manager',95000.00,1),(2,'Vikram Joshi','Loan Officer',60000.00,1),
(3,'Neha Patil','Cashier',35000.00,2),(4,'Imran Ali','Branch Manager',92000.00,3);
INSERT INTO loan_type (id,name,interest_rate,max_tenure_months,income_multiple) VALUES
(1,'Personal Loan',11.00,60,10.00),(2,'Home Loan',8.50,240,60.00),(3,'Vehicle Loan',9.25,84,24.00);
INSERT INTO account (id,account_number,branch_id,account_type_id,balance,status,opened_on) VALUES
(1,'AC1000000001',1,1,99676.73,'ACTIVE',DATE_SUB(CURDATE(), INTERVAL 130 DAY)),
(2,'AC1000000002',1,2,235000.00,'ACTIVE',DATE_SUB(CURDATE(), INTERVAL 100 DAY)),
(3,'AC1000000003',2,1,10000.00,'ACTIVE',DATE_SUB(CURDATE(), INTERVAL 100 DAY)),
(4,'AC1000000004',3,1,95000.00,'ACTIVE',DATE_SUB(CURDATE(), INTERVAL 100 DAY)),
(5,'AC1000000005',2,1,5500.00,'ACTIVE',DATE_SUB(CURDATE(), INTERVAL 100 DAY)),
(6,'AC1000000006',1,1,2500.00,'ACTIVE',DATE_SUB(CURDATE(), INTERVAL 100 DAY));
-- account 3 is a joint account (Diya Verma + Meera Iyer): many-to-many
INSERT INTO account_holder (account_id,customer_id) VALUES (1,1),(2,3),(3,2),(3,4),(4,5),(5,4),(6,5);
INSERT INTO bank_transaction (account_id,type,amount,balance_after,txn_time,description,reference_no) VALUES
(1,'DEPOSIT',50000.00,50000.00,DATE_SUB(NOW(), INTERVAL 130 DAY),'Opening deposit',NULL),
(2,'DEPOSIT',250000.00,250000.00,DATE_SUB(NOW(), INTERVAL 125 DAY),'Opening deposit',NULL),
(3,'DEPOSIT',12000.00,12000.00,DATE_SUB(NOW(), INTERVAL 120 DAY),'Opening deposit',NULL),
(4,'DEPOSIT',80000.00,80000.00,DATE_SUB(NOW(), INTERVAL 118 DAY),'Opening deposit',NULL),
(5,'DEPOSIT',4000.00,4000.00,DATE_SUB(NOW(), INTERVAL 100 DAY),'Opening deposit',NULL),
(6,'DEPOSIT',2500.00,2500.00,DATE_SUB(NOW(), INTERVAL 90 DAY),'Opening deposit',NULL),
(1,'LOAN_DISBURSAL',60000.00,110000.00,DATE_SUB(NOW(), INTERVAL 120 DAY),'Loan disbursal, loan #1',NULL),
(1,'EMI_PAYMENT',10323.27,99676.73,DATE_SUB(NOW(), INTERVAL 90 DAY),'EMI 1/6 for loan #1',NULL),
(3,'WITHDRAWAL',2000.00,10000.00,DATE_SUB(NOW(), INTERVAL 30 DAY),'Cash withdrawal',NULL),
(2,'TRANSFER_OUT',15000.00,235000.00,DATE_SUB(NOW(), INTERVAL 10 DAY),'Fund transfer to AC1000000004','TRFDEMO00001'),
(4,'TRANSFER_IN',15000.00,95000.00,DATE_SUB(NOW(), INTERVAL 10 DAY),'Fund transfer from AC1000000002','TRFDEMO00001'),
(5,'DEPOSIT',1500.00,5500.00,DATE_SUB(NOW(), INTERVAL 3 DAY),'Cash deposit',NULL);
INSERT INTO loan_application (id,customer_id,loan_type_id,account_id,amount,tenure_months,status,applied_on,decided_on,remarks) VALUES
(1,1,1,1,60000.00,6,'APPROVED',DATE_SUB(CURDATE(), INTERVAL 125 DAY),DATE_SUB(CURDATE(), INTERVAL 120 DAY),'Approved'),
(2,3,2,2,2500000.00,120,'PENDING',CURDATE(),NULL,NULL),
(3,4,3,5,400000.00,48,'PENDING',CURDATE(),NULL,NULL);
INSERT INTO loan (id,application_id,customer_id,loan_type_id,account_id,principal,annual_rate,tenure_months,emi_amount,disbursed_on,status) VALUES
(1,1,1,1,1,60000.00,11.00,6,10323.27,DATE_SUB(CURDATE(), INTERVAL 4 MONTH),'ACTIVE');
INSERT INTO emi_installment (loan_id,installment_no,due_date,emi_amount,principal_component,interest_component,balance_outstanding,status,paid_on) VALUES
(1,1,DATE_ADD(DATE_SUB(CURDATE(), INTERVAL 4 MONTH), INTERVAL 1 MONTH),10323.27,9773.27,550.00,50226.73,'PAID',DATE_ADD(DATE_SUB(CURDATE(), INTERVAL 4 MONTH), INTERVAL 1 MONTH)),
(1,2,DATE_ADD(DATE_SUB(CURDATE(), INTERVAL 4 MONTH), INTERVAL 2 MONTH),10323.27,9862.86,460.41,40363.87,'PENDING',NULL),
(1,3,DATE_ADD(DATE_SUB(CURDATE(), INTERVAL 4 MONTH), INTERVAL 3 MONTH),10323.27,9953.27,370.00,30410.60,'PENDING',NULL),
(1,4,DATE_ADD(DATE_SUB(CURDATE(), INTERVAL 4 MONTH), INTERVAL 4 MONTH),10323.27,10044.51,278.76,20366.09,'PENDING',NULL),
(1,5,DATE_ADD(DATE_SUB(CURDATE(), INTERVAL 4 MONTH), INTERVAL 5 MONTH),10323.27,10136.58,186.69,10229.51,'PENDING',NULL),
(1,6,DATE_ADD(DATE_SUB(CURDATE(), INTERVAL 4 MONTH), INTERVAL 6 MONTH),10323.28,10229.51,93.77,0.00,'PENDING',NULL);
