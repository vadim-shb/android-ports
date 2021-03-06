package liquibase.statement.core;

import liquibase.statement.AbstractSqlStatement;

public class DropForeignKeyConstraintStatement extends AbstractSqlStatement {

    private String baseTableSchemaName;
    private String baseTableName;
    private String constraintName;

    public DropForeignKeyConstraintStatement(String baseTableSchemaName, String baseTableName, String constraintName) {
        this.baseTableSchemaName = baseTableSchemaName;
        this.baseTableName = baseTableName;
        this.constraintName = constraintName;
    }

    public String getBaseTableSchemaName() {
        return baseTableSchemaName;
    }

    public String getBaseTableName() {
        return baseTableName;
    }

    public String getConstraintName() {
        return constraintName;
    }
}
