package liquibase.database;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Base test class for database-specific tests
 */
public abstract class AbstractDatabaseTest {

    protected AbstractDatabase database;

    protected AbstractDatabaseTest(AbstractDatabase database) throws Exception {
        this.database = database;
    }

    public AbstractDatabase getDatabase() {
        return database;
    }

    protected abstract String getProductNameString();

    public abstract void supportsInitiallyDeferrableColumns();

    public abstract void getCurrentDateTimeFunction();

    @Test
    public void onlyAdjustAutoCommitOnMismatch() throws Exception {
        // Train expectations for setConnection(). If getAutoCommit() returns the same value the Database wants, based
        // on getAutoCommitMode(), it should _not_ call setAutoCommit(boolean) on the connection
        DatabaseConnection connection = createStrictMock(DatabaseConnection.class);
        expect(connection.getConnectionUserName()).andReturn("user");
        expect(connection.getURL()).andReturn("URL");
        expect(connection.getAutoCommit()).andReturn(getDatabase().getAutoCommitMode());
        replay(connection);

        getDatabase().setConnection(connection);
        verify(connection);

        // Reset the mock and train expectations for close(). Since the auto-commit mode was not adjusted while setting
        // the connection, it should not be adjusted on close() either
        reset(connection);
        connection.close();
        replay(connection);

        getDatabase().close();
        verify(connection);
    }

    @Test
    public void restoreAutoCommitOnClose() throws Exception {
        DatabaseConnection connection = createStrictMock(DatabaseConnection.class);
        expect(connection.getConnectionUserName()).andReturn("user");
        expect(connection.getURL()).andReturn("URL");
        expect(connection.getAutoCommit()).andReturn(true);
        connection.setAutoCommit(eq(false));
        replay(connection);

        getDatabase().setConnection(connection);
        verify(connection);

        reset(connection);
        connection.setAutoCommit(eq(true));
        connection.close();
        replay(connection);

        getDatabase().close();
        verify(connection);
    }

    @Test
    public void isCorrectDatabaseImplementation() throws Exception {
        assertTrue(getDatabase().isCorrectDatabaseImplementation(getMockConnection()));
    }

    protected DatabaseConnection getMockConnection() throws Exception {
        DatabaseConnection conn = createMock(DatabaseConnection.class);
//        DatabaseMetaData metaData = createMock(DatabaseMetaData.class);
        conn.setAutoCommit(false);

        expectLastCall().anyTimes();
//        expect(((JdbcConnection) conn).getUnderlyingConnection().getMetaData()).andReturn(metaData).anyTimes();
        expect(conn.getDatabaseProductName()).andReturn(getProductNameString()).anyTimes();
        replay(conn);
//        replay(metaData);
        return conn;
    }

    @Test
    public void escapeTableName_noSchema() {
        Database database = getDatabase();
        assertEquals("tableName", database.escapeTableName(null, "tableName"));
    }

    @Test
    public void escapeTableName_withSchema() {
        Database database = getDatabase();
        assertEquals("schemaName.tableName", database.escapeTableName("schemaName", "tableName"));
    }

//    @Test
//    public void getColumnType_javaTypes() throws SQLException {
//        Database database = getDatabase();
//        DatabaseConnection connection = database.getConnection();
//        if (connection != null) {
//            ((JdbcConnection) connection).getUnderlyingConnection().rollback();
//            assertEquals(database.getDateType().getDataTypeName().toUpperCase(), database.getDataType("java.sql.Types.DATE", false).toUpperCase());
//            assertEquals(database.getBooleanType().getDataTypeName().toUpperCase(), database.getDataType("java.sql.Types.BOOLEAN", false).toUpperCase());
//            assertEquals("VARCHAR(255)", database.getDataType("java.sql.Types.VARCHAR(255)", false).toUpperCase().replaceAll("VARCHAR2", "VARCHAR"));
//        }
//    }
}
