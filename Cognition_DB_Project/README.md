# Cognition Learner Intelligence Database System (BACSE202)

This starter project is based on the attached Database Systems mini-project guideline:
Oracle SQL implementation, integrity constraints, sample data, DDL/DML/TCL, SQL queries, and front-end connectivity with Insert/Update/Delete/Search/Display.

## Contents
- `sql/01_schema.sql` — tables, PK/FK, CHECK/UNIQUE constraints, indexes
- `sql/02_sample_data.sql` — synthetic demo records
- `sql/03_queries.sql` — SELECT, JOIN, nested query, functions, DML, TCL examples
- `java/src/CognitionApp.java` — Java console front end using JDBC

## 1. Create/run the database
1. Start your Oracle database and open SQL Developer or SQL*Plus.
2. Connect using your project schema/user.
3. Run `sql/01_schema.sql`.
4. Run `sql/02_sample_data.sql`.
5. Run each query in `sql/03_queries.sql` individually and capture actual output screenshots.
6. If you run the sample-data script more than once, unique participant/topic codes will conflict. Either clear the demo tables in FK-safe order or use a fresh project schema.

The default Java JDBC URL is `jdbc:oracle:thin:@//localhost:1521/FREEPDB1`. Your service may be different (for example, XE commonly uses `XEPDB1`). Use the service name configured on your machine.

## 2. Compile/run Java app
Requirements: JDK 17+ and Oracle JDBC driver (`ojdbc` jar) compatible with your Oracle/JDK setup.

Windows PowerShell example (replace the jar path and credentials):
```powershell
$env:ORACLE_URL="jdbc:oracle:thin:@//localhost:1521/FREEPDB1"
$env:ORACLE_USER="your_schema_user"
$env:ORACLE_PASSWORD="your_password"
javac -cp ".;C:\path\to\ojdbc11.jar" .\java\src\CognitionApp.java
java -cp ".;C:\path\to\ojdbc11.jar;.\java\src" CognitionApp
```

Linux/macOS classpath separator is `:` instead of `;`.

Do not commit passwords or database credentials to GitHub. For a lab submission, use only synthetic sample records unless you have the required consent and approval for real learner data.

## 3. Suggested report/handwritten sections
1. Requirement analysis: purpose, scope, information stored, entities, relationships, and business rules.
2. ER diagram: draw by hand using your course's ER notation; include cardinality and participation constraints.
3. ER-to-relational mapping: document each mapping step and mark PK/FK.
4. Implementation: include schema SQL, DDL/DML/TCL examples, at least five SELECT queries, actual output screenshots, and front-end CRUD demonstration.
5. Limitations: learner signals are stored as records; this starter does not claim to infer true psychological states or implement a validated AI model.

## 4. Business rules used
- Each learner has a unique participant code.
- Each session belongs to exactly one learner and one topic.
- A session can contain zero or many interactions and state transitions.
- An interaction can have zero or many detected/reviewed signals.
- Concept progress is tracked per learner, topic, and concept.
- Mastery score is a numeric estimate from 0 to 100; it is not a validated measure until a suitable evaluation is conducted.

## 5. Important
The ER diagram in the accompanying presentation is a conceptual draft. Check the final handwritten diagram against the implemented schema before submission. Verify all scripts on your own Oracle installation and capture genuine outputs; database service names, permissions, and JDBC driver versions vary by machine.
