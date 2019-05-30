package jp.co.hiropro.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jp.co.hiropro.dao.annotation.Ignore;
import jp.co.hiropro.dao.annotation.PrimaryKey;
import jp.co.hiropro.utils.HSSLog;

public class DAOHandler {
    private static DAOHandler instance;
    private SQLiteHelper helper;
    private SQLiteDatabase db;

    private DAOHandler(Context context) {
        helper = new SQLiteHelper(context);
        db = helper.getWritableDatabase();
    }

    public static DAOHandler getInstance() {
        if (instance == null)
            throw new NullPointerException("DAOHandler not initialized. Please, invoke initialize() method first");
        return instance;
    }

    public static void initialize(Context context) {
        instance = new DAOHandler(context);
    }

    public void close() {
        if (db != null) db.close();
        helper.close();
    }

    public void add(Object object) {
        List<Field> fields = getTableField(object.getClass());
        ContentValues values = new ContentValues();
        for (Field column : fields) {
            if (column.isAnnotationPresent(PrimaryKey.class)) {
                if (column.getAnnotation(PrimaryKey.class).autoIncrease())
                    continue;
            }
            String columnName = column.getName();
            Object obj = getValue(object, columnName);
            String value = (obj == null) ? (String) obj : obj.toString();
            values.put(columnName, value);
        }
        try {
            db.insertOrThrow(object.getClass().getSimpleName(), null, values);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertOrUpdate(Object object) {
        List<Field> fields = getTableField(object.getClass());
        ContentValues values = new ContentValues();
        for (Field column : fields) {
            if (column.isAnnotationPresent(PrimaryKey.class)) {
                if (column.getAnnotation(PrimaryKey.class).autoIncrease())
                    continue;
            }
            String columnName = column.getName();
            Object obj = getValue(object, columnName);
            String value = (obj == null) ? (String) obj : obj.toString();
            values.put(columnName, value);
        }
        try {
            db.insertWithOnConflict(object.getClass().getSimpleName(), null, values,SQLiteDatabase.CONFLICT_REPLACE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Object object) {
        List<Field> fields = getTableField(object.getClass());
        ContentValues values = new ContentValues();
        String whereClause = null;
        String[] whereArgs = null;
        for (Field column : fields) {
            if (column.isAnnotationPresent(PrimaryKey.class)) {
                whereClause = column.getName() + "=?";
                whereArgs = new String[]{getValue(object, column.getName()).toString()};
                continue;
            }
            String columnName = column.getName();
            values.put(columnName, getValue(object, columnName).toString());
        }
        try {
            db.update(object.getClass().getSimpleName(), values, whereClause, whereArgs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(Object object) {
        List<Field> fields = getTableField(object.getClass());
        String whereClause = null;
        String[] whereArgs = null;
        for (Field column : fields) {
            if (column.isAnnotationPresent(PrimaryKey.class)) {
                whereClause = column.getName() + "=?";
                whereArgs = new String[]{getValue(object, column.getName()).toString()};
                db.delete(object.getClass().getSimpleName(), whereClause, whereArgs);
                return;
            }
        }
    }

    public void delete(Class<?> table, int id) {
        List<Field> fields = getTableField(table);
        String whereClause = null;
        String[] whereArgs = null;
        for (Field column : fields) {
            if (column.isAnnotationPresent(PrimaryKey.class)) {
                whereClause = column.getName() + "=?";
                whereArgs = new String[]{String.valueOf(id)};
                db.delete(table.getSimpleName(), whereClause, whereArgs);
                return;
            }
        }
    }

    public DAOHandler delete(Class table) {
        db.delete(table.getSimpleName(), null, null);
        return this;
    }

    /**
     * Find an object in database by id
     *
     * @param type Class type of Object
     * @param id   id of Object
     * @return Object in database if finded and <b>null</b> if not findable
     */
    public <T> T findById(Class<T> type, int id) {
        List<Field> fields = getTableField(type);
        String whereClause = null;
        String[] whereArgs = null;
        for (Field column : fields) {
            if (column.isAnnotationPresent(PrimaryKey.class)) {
                whereClause = column.getName() + "=?";
                whereArgs = new String[]{String.valueOf(id)};
                break;
            }
        }
        List<T> array = search(type, whereClause, whereArgs);
        if (array.size() > 0) return array.get(0);
        return null;
    }

    public <T> List<T> search(Class<T> type, String whereClause, String... whereArgs) {
        Cursor cursor = db.query(
                type.getSimpleName(), null, whereClause, whereArgs, null, null, null, null);
        List<T> result = new ArrayList<T>();
        T entity;
        try {
            while (cursor.moveToNext()) {
                entity = type.newInstance();
                inflate(cursor, entity);
                result.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return result;
    }

    public <T> List<T> listAll(Class<T> type) {
        return search(type, null);
    }

    private void inflate(Cursor cursor, Object object) {
        List<Field> fields = getTableField(object.getClass());
        for (Field field : fields) {
            setFieldValueFromCursor(cursor, field, object);
        }
    }

    private List<Field> getTableField(Class<?> table) {
        List<Field> fields = new ArrayList<Field>();
        Collections.addAll(fields, table.getDeclaredFields());
        while (table.getSuperclass() != null) {
            table = table.getSuperclass();
            Collections.addAll(fields, table.getDeclaredFields());
        }
        for (int i = fields.size() - 1; i >= 0; i--) {
            if (fields.get(i).isAnnotationPresent(Ignore.class)) fields.remove(i);
        }
        return fields;
    }

    private Object getValue(Object obj, String columnName) {
        String first = columnName.substring(0, 1).toUpperCase(Locale.US);
        columnName = first + columnName.substring(1);
        Class table = obj.getClass();
        List<Method> methods = new ArrayList<Method>();
        Collections.addAll(methods, table.getDeclaredMethods());
        while (table.getSuperclass() != null) {
            table = table.getSuperclass();
            Collections.addAll(methods, table.getDeclaredMethods());
        }
        for (Method method : methods) {
            String name = method.getName();
            if (name.equals("get" + columnName) || name.equals("is" + columnName)) {
                try {
                    return method.invoke(obj, new Object[]{});
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private void setFieldValueFromCursor(Cursor cursor, Field field, Object object) {
        field.setAccessible(true);
        try {
            Class<?> fieldType = field.getType();
            String colName = field.getName();

            int columnIndex = cursor.getColumnIndex(colName);
            if (cursor.isNull(columnIndex)) {
                return;
            }

            if (fieldType.equals(int.class) || fieldType.equals(Integer.class)) {
                field.set(object, cursor.getInt(columnIndex));
            } else if (fieldType.equals(long.class) || fieldType.equals(Long.class)) {
                field.set(object, cursor.getLong(columnIndex));
            } else if (fieldType.equals(String.class)) {
                String val = cursor.getString(columnIndex);
                field.set(object, val != null && val.equals("null") ? null : val);
            } else if (fieldType.equals(double.class) || fieldType.equals(Double.class)) {
                field.set(object, cursor.getDouble(columnIndex));
            } else if (fieldType.equals(boolean.class) || fieldType.equals(Boolean.class)) {
                field.set(object, cursor.getString(columnIndex).equals("1"));
            } else if (field.getType().getName().equals("[B")) {
                field.set(object, cursor.getBlob(columnIndex));
            } else if (fieldType.equals(float.class) || fieldType.equals(Float.class)) {
                field.set(object, cursor.getFloat(columnIndex));
            } else if (fieldType.equals(short.class) || fieldType.equals(Short.class)) {
                field.set(object, cursor.getShort(columnIndex));
            } else if (fieldType.equals(Timestamp.class)) {
                long l = cursor.getLong(columnIndex);
                field.set(object, new Timestamp(l));
            } else if (fieldType.equals(Date.class)) {
                long l = cursor.getLong(columnIndex);
                field.set(object, new Date(l));
            } else if (fieldType.equals(Calendar.class)) {
                long l = cursor.getLong(columnIndex);
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(l);
                field.set(object, c);
            } else if (Enum.class.isAssignableFrom(fieldType)) {
                try {
                    Method valueOf = field.getType().getMethod("valueOf", String.class);
                    String strVal = cursor.getString(columnIndex);
                    Object enumVal = valueOf.invoke(field.getType(), strVal);
                    field.set(object, enumVal);
                } catch (Exception e) {
                    HSSLog.e("Enum cannot be read from Sqlite3 database. Please check the type of field " + field.getName());
                }
            } else
                HSSLog.e("Class cannot be read from Sqlite3 database. Please check the type of field " + field.getName() + "(" + field.getType().getName() + ")");
        } catch (IllegalArgumentException e) {
            HSSLog.e("field set error", e.getMessage());
        } catch (IllegalAccessException e) {
            HSSLog.e("field set error", e.getMessage());
        } finally {
            field.setAccessible(false);
        }
    }
}
