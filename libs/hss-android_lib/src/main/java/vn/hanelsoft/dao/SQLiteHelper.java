package vn.hanelsoft.dao;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import dalvik.system.DexFile;
import vn.hanelsoft.dao.annotation.Column;
import vn.hanelsoft.dao.annotation.Ignore;
import vn.hanelsoft.dao.annotation.NotNull;
import vn.hanelsoft.dao.annotation.PrimaryKey;
import vn.hanelsoft.dao.annotation.Table;
import vn.hanelsoft.dao.annotation.Unique;

class SQLiteHelper extends SQLiteOpenHelper{
	private Context context;
	public SQLiteHelper(Context context) {
		super(context, "hss_database.db", null, getMetaDataVerson(context));
		this.context = context;
	}

	private static Integer getMetaDataVerson(Context context) {
		Integer value = 1;
		try {
			ApplicationInfo ai = context.getPackageManager().getApplicationInfo(
					context.getPackageName(), PackageManager.GET_META_DATA);
			value = ai.metaData.getInt("DB_VERSION");
		} catch (Exception e) {
//            HSSLog.d("Couldn't find config value: DB_VERSION");
		}
		if(value < 1) value = 1;
		return value;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		for(Class<?> c:getListTable()){
			db.execSQL(createTable(c));
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		for(Class<?> c:getListTable()){
			db.execSQL("DROP TABLE IF EXISTS " + c.getSimpleName());
		}
		onCreate(db);
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		for(Class<?> c:getListTable()){
			db.execSQL("DROP TABLE IF EXISTS " + c.getSimpleName());
		}
		onCreate(db);
	}

	private String createTable(Class<?> table){
		StringBuilder sb = new StringBuilder("CREATE TABLE ");
		sb.append(table.getSimpleName()).append("(");
		List<Field> fields = new ArrayList<Field>();
		Collections.addAll(fields, table.getDeclaredFields());
		while(table.getSuperclass()!=null){
			table = table.getSuperclass();
			Collections.addAll(fields, table.getDeclaredFields());
		}
		for (Field column : fields) {
			String columnName = column.getName();
			String columnType =	getColumnType(column.getType());

			if(column.isAnnotationPresent(Ignore.class)
					|| Modifier.isTransient(column.getModifiers())
					|| Modifier.isStatic(column.getModifiers())
					|| Modifier.isVolatile(column.getModifiers())
					|| Modifier.isFinal(column.getModifiers()))continue;
			if(column.isAnnotationPresent(PrimaryKey.class)){
				StringBuilder builder = new StringBuilder();
				PrimaryKey primaryKey = column.getAnnotation(PrimaryKey.class);
				builder.append(columnName).append(" ").append(columnType).append(" PRIMARY KEY");
				if(primaryKey.autoIncrease()){
					builder.append(" AUTOINCREMENT");
				}
				sb.insert(sb.indexOf("(")+1, builder);
				continue;
			}

			if (columnType != null) {
				if (column.isAnnotationPresent(Column.class)) {
					Column columnAnnotation = column.getAnnotation(Column.class);
					sb.append(", ").append(columnName).append(" ").append(columnType);

					if (columnAnnotation.notNull()) {
						if (columnType.endsWith(" NULL")) {
							sb.delete(sb.length() - 5, sb.length());
						}
						sb.append(" NOT NULL");
					}

					if (columnAnnotation.unique()) {
						sb.append(" UNIQUE");
					}

				} else {
					sb.append(", ").append(columnName).append(" ").append(columnType);

					if (column.isAnnotationPresent(NotNull.class)) {
						if (columnType.endsWith(" NULL")) {
							sb.delete(sb.length() - 5, sb.length());
						}
						sb.append(" NOT NULL");
					}

					if (column.isAnnotationPresent(Unique.class)) {
						sb.append(" UNIQUE");
					}
				}
			}
		}
		sb.append(")");
		return sb.toString();
	}

	private List<Class<?>> getListTable(){
		List<Class<?>> classes = new ArrayList<Class<?>>();
		try {
			DexFile df = new DexFile(context.getPackageCodePath());
			for (Enumeration<String> iter = df.entries(); iter.hasMoreElements();) {
				String className = iter.nextElement();
				Class<?> discoveredClass;
				try {
					discoveredClass = Class.forName(className, false, context.getClass().getClassLoader());
					if(discoveredClass.isAnnotationPresent(Table.class)){
						classes.add(discoveredClass);
					}
				} catch (ClassNotFoundException|IncompatibleClassChangeError e) {}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return classes;
	}

	private String getColumnType(Class<?> type) {
		if ((type.equals(Boolean.class)) || (type.equals(Boolean.TYPE))
				|| (type.equals(Integer.class)) || (type.equals(Integer.TYPE))
				|| (type.equals(Long.class)) || (type.equals(Long.TYPE)))  {
			return "INTEGER";
		}

		if ((type.equals(java.util.Date.class)) ||
				(type.equals(java.sql.Date.class)) ||
				(type.equals(java.util.Calendar.class))) {
			return "INTEGER NULL";
		}

		if (type.getName().equals("[B")) {
			return "BLOB";
		}

		if ((type.equals(Double.class)) || (type.equals(Double.TYPE))
				|| (type.equals(Float.class)) || (type.equals(Float.TYPE))) {
			return "FLOAT";
		}

		if ((type.equals(String.class)) || (type.equals(Character.TYPE))) {
			return "TEXT";
		}

		return "";
	}

}
