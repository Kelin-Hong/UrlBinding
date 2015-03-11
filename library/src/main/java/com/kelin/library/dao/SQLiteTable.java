package com.kelin.library.dao;


import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;

public class SQLiteTable {
    String mTableName;

    ArrayList<ColumnsDefinition> mColumnsDefinitions = new ArrayList<ColumnsDefinition>();

    /**
     * 会自动添加主键 BaseColumns._ID
     *
     * @param tableName
     */
    public SQLiteTable(String tableName) {
        mTableName = tableName;
        mColumnsDefinitions.add(new ColumnsDefinition(BaseColumns._ID, ColumnsDefinition.Constraint.PRIMARY_KEY,
                ColumnsDefinition.DataType.INTEGER));
    }

    public SQLiteTable addColumn(ColumnsDefinition columnsDefinition) {
        mColumnsDefinitions.add(columnsDefinition);
        return this;
    }

    public void create(SQLiteDatabase db) {
        String formatter = " %s";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("CREATE TABLE ");
        stringBuilder.append(mTableName);
        stringBuilder.append("(");
        int columnCount = mColumnsDefinitions.size();
        int index = 0;
        for (ColumnsDefinition columnsDefinition : mColumnsDefinitions) {
            stringBuilder.append(columnsDefinition.getColumnName()).append(
                    String.format(formatter, columnsDefinition.getDataType().name()));
            ColumnsDefinition.Constraint constraint = columnsDefinition.getConstraint();

            if (constraint != null) {
                String s=constraint.toString();
                String ss=ColumnsDefinition.Constraint.FOREIGN_KEY_REFERENCE.toString();
                if (constraint.toString().equals(ss)) {
                    stringBuilder.append(String.format(formatter, columnsDefinition.getForeignKey()));
                } else {
                    stringBuilder.append(String.format(formatter, constraint.toString()));
                }
            }
            if (index < columnCount - 1) {
                stringBuilder.append(",");
            }
            index++;
        }
        stringBuilder.append(");");
        db.execSQL(stringBuilder.toString());
    }

    public void delete(final SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + mTableName);
    }
}
