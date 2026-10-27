# Cognition Mobile Preview

This is a mobile-friendly, self-contained UI preview. Start it on Windows by double-clicking `Run-Preview-Windows.bat`, then open `http://localhost:8000`.

Demo login: `demo` / `cognition123`. This is only a client-side demonstration gate, not secure authentication. Learner insert/update/delete actions modify browser-local sample data only. They do not write to Oracle.

For the database-connectivity demo, run the SQL scripts in `../sql` in Oracle and configure the Java JDBC app with your actual Oracle connection details. Live Oracle connectivity was not verified in the generation environment.
