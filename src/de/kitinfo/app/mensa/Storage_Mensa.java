/**
 * 
 */
package de.kitinfo.app.mensa;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import de.kitinfo.app.data.Database;
import de.kitinfo.app.data.StorageContract;
import de.kitinfo.app.data.StorageInterface;
import de.kitinfo.app.data.Database.ColumnValues;

/**
 * @author mpease
 *
 */
public class Storage_Mensa implements StorageInterface<MensaDay> {

	private static final String SPLIT_SYMBOL = ":";
	private static final Mensa m = Mensa.ADENAUER;
	private Context ctx;
	private ContentResolver resolver;
	
	public Storage_Mensa(Context ctx) {
		this.ctx = ctx;
		resolver = ctx.getContentResolver();
	}
	
	
	
	@Override
	public void add(MensaDay day) {
		
		
		// get uris from contract
		Uri mensaLineUri = Uri.parse(StorageContract.MENSA_LINE_URI);
		Uri mensaMealUri = Uri.parse(StorageContract.MEAL_URI);
		
		// define
		ContentValues values;
		String lineWhere = ColumnValues.LINE_NAME.getName() + " = ? AND " + ColumnValues.LINE_MENSA.getName() + " = ?";
		String mealWhere = ColumnValues.MEAL_DATE.getName() + " = ? AND " + ColumnValues.MEAL_LINE.getName() + " = ?";
		
		
		// insert mensa lines
		List<MensaLine> lines = day.getLines();
		
		for (MensaLine ml : lines) {
			values = convertLineToContentValues(ml, m);
			
			String[] lineArgs = {String.valueOf(values.get(ColumnValues.LINE_NAME.getName())), String.valueOf(m.ordinal())};
			
			updateOrInsert(mensaLineUri, values, lineWhere, lineArgs);
			
			List<MensaMeal> meals = ml.getMeals();
			
			// insert meals
			for (MensaMeal mm : meals) {
				values = convertMealToContentValues(mm, day.getDateTime());
				
				// put rest values in list
				values.put(ColumnValues.MEAL_LINE.getName(), getMensaLineID(ml));
				
				String[] mealSelectionArgs = {"" + day.getDateTime(), "" + ml.getID()};
				
				// database action
				updateOrInsert(mensaMealUri, values, mealWhere, mealSelectionArgs);
			}
		}
		new Database(ctx).close();
	}
	
	/**
	 * converts a line object with the mensa object to his content values
	 * @param ml the mensa line object
	 * @param m the mensa object (@see Mensa)
	 * @return his content values
	 */
	public ContentValues convertLineToContentValues(MensaLine ml, Mensa m) {
		ContentValues values = new ContentValues();
		
		values.put(ColumnValues.LINE_MENSA.getName(), m.ordinal());
		values.put(ColumnValues.LINE_NAME.getName(), ml.getName());
		int id = getMensaLineID(ml);
		values.put(ColumnValues.LINE_ID.getName(), id);
		
		return values;
	}
	
	/**
	 * converts a meal object to his content values.
	 * @param mm the meal object
	 * @param date the date of the meal
	 * @return his content values
	 */
	public ContentValues convertMealToContentValues(MensaMeal mm, float date) {
		ContentValues values = new ContentValues();
		
		StringBuilder adds = new StringBuilder();
		
		// we split our adds with ;
		for (String s : mm.getAdds()) {
			adds.append(s);
			adds.append(SPLIT_SYMBOL);
		}
		
		values.put(ColumnValues.MEAL_HINT.getName(), mm.getHint());
		values.put(ColumnValues.MEAL_INFO.getName(), mm.getInfo());
		values.put(ColumnValues.MEAL_NAME.getName(), mm.getName());
		values.put(ColumnValues.MEAL_PRICE.getName(), mm.getPrice());
		values.put(ColumnValues.MEAL_ADDS.getName(), adds.toString());
		values.put(ColumnValues.MEAL_DATE.getName(), date);
		values.put(ColumnValues.MEAL_TAGS.getName(), mm.getTags(SPLIT_SYMBOL));
		
		return values;
	}
	
	

	/**
	 * Returns a mensa day object from database with same properties as the given mensa day object (general the date)
	 * @param day the object with date property inside
	 * @return a mensa day object
	 */
	@Override
	public MensaDay get(MensaDay day) {
		
		SparseArray<List<MensaMeal>> meals = getMensaMeals(day.getDateTime());
		
		List<MensaLine> mensaLines = getMensaLines(meals);
		
		
		MensaDay newDay = new MensaDay(day.getDateTime());
		
		// add lines
		for (MensaLine ml : mensaLines) {
			newDay.addLine(ml);
		}
		new Database(ctx).close();
		return newDay;
	}
	
	/**
	 * Returns a list of mensa lines with its meals
	 * @param meals list of meals listed by id of the line
	 * @return list of mensa lines
	 */
	public List<MensaLine> getMensaLines(SparseArray<List<MensaMeal>> meals) {
		
		// get lines
		List<MensaLine> mensaLines = new LinkedList<MensaLine>();
		
		
		for (int i = 0; i < meals.size(); i++) {
			int key = meals.keyAt(i);
				
			MensaLine ml = getMensaLine(key);
			
			// check if key not in list
			if (ml == null) {
				break;
			}
				
			// add meals to list
			for (MensaMeal mm : meals.get(key)) {
				if (mm != null) {
					ml.addMeal(mm);
				}
			}
		}
		return mensaLines;
	}
	
	/**
	 * Returns a list of mensa meals by a date
	 * @param date date in seconds
	 * @return list of meals with key to his mensa line
	 */
	public SparseArray<List<MensaMeal>> getMensaMeals(long date) {
		// get meals
		SparseArray<List<MensaMeal>> meals = new SparseArray<List<MensaMeal>>();
		
		Uri mealUri = Uri.parse(StorageContract.MEAL_URI);
		String selection = ColumnValues.MEAL_DATE.getName() + " = ?";
		String[] selectionArgs = {String.valueOf(date)};
		Cursor c = query(mealUri, null, selection, selectionArgs, ColumnValues.MEAL_DATE.getName() + " ASC");
		
		while (c.moveToNext()) {
			
			int id = c.getInt(c.getColumnIndex(ColumnValues.MEAL_LINE.getName()));
			
			List<MensaMeal> list = meals.get(id);
			
			if (list == null) {
				list = new LinkedList<MensaMeal>();
			}
			
			list.add(convertCursorToMensaMeal(c));
			meals.put(id, list);
		}
		
		c.close();
		new Database(ctx).close();
		return meals;
	}
	
	/**
	 * Returns a mensa line object from its id
	 * @param id id of the object
	 * @return the mensa line object with the given id. Null if id is not in database.
	 */
	public MensaLine getMensaLine(int id) {
		
		String selection = ColumnValues.LINE_ID.getName() + " = ?";
		String[] selectionArgs = {String.valueOf(id)};
		Uri uri = Uri.parse(StorageContract.MENSA_LINE_URI);
		
		Cursor c = query(uri, null, selection, selectionArgs, null);
		MensaLine line = null;
		
		// if there is a line;
		if (c.moveToNext()) {
			line = convertCursorToMensaLine(c);
		}
		c.close();
		new Database(ctx).close();
		return line;
	}
	
	/**
	 * converts a cursor to a line
	 * @param c cursor from database
	 * @return a new mensa line object (without meals)
	 */
	public MensaLine convertCursorToMensaLine(Cursor c) {
		
		int id = c.getInt(c.getColumnIndex(ColumnValues.LINE_ID.getName()));
		String name = c.getString(c.getColumnIndex(ColumnValues.LINE_NAME.getName()));
		int mensaID = c.getInt(c.getColumnIndex(ColumnValues.LINE_MENSA.getName()));
		
		return new MensaLine(name, id, mensaID);
	}
	
	
	/**
	 * sends a query action to content provider
	 * @param uri uri of the content provider and table
	 * @param projection columns we want to have
	 * @param selection the where clause
	 * @param selectionArgs all arguments for the clause
	 * @param sortOrder a sort order
	 * @return cursor with data
	 */
	private Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		
		
		
		return resolver.query(uri, projection, selection, selectionArgs, sortOrder);
	}
	
	
	
	/**
	 * converts a cursor to a meal object
	 * @param c cursor from database
	 * @return mensa meal object
	 */
	public MensaMeal convertCursorToMensaMeal(Cursor c) {
		
		float price = c.getFloat(c.getColumnIndex(ColumnValues.MEAL_PRICE.getName()));
		String adds = c.getString(c.getColumnIndex(ColumnValues.MEAL_ADDS.getName()));
		String info = c.getString(c.getColumnIndex(ColumnValues.MEAL_INFO.getName()));
		String hint = c.getString(c.getColumnIndex(ColumnValues.MEAL_HINT.getName()));
		String name = c.getString(c.getColumnIndex(ColumnValues.MEAL_NAME.getName()));
		String tag = c.getString(c.getColumnIndex(ColumnValues.MEAL_TAGS.getName()));
		
		List<String> addList = new LinkedList<String>();
		for (String a : adds.split(SPLIT_SYMBOL)) {
			addList.add(a);
		}
		
		
		String[] tags = tag.split("\n");
		
		HashMap<String, Boolean> tagMap = new HashMap<String, Boolean>();
		for (String t :tags) {
			String[] tc = t.split(SPLIT_SYMBOL);
			tagMap.put(tc[0], Boolean.parseBoolean(tc[1]));
		}
		
		return new MensaMeal(tagMap, name, hint, info, price, addList);
	}
	
	
	
	

	@Override
	public void reset() {
		Uri uri = Uri.parse(StorageContract.MENSA_LINE_RESET_URI);
		
		
		
		resolver.delete(uri, null, null);
		
		uri = Uri.parse(StorageContract.MENSA_MEAL_RESET_URI);
		
		resolver.delete(uri, null, null);
	}
	
	
	/*
	 * returns the next id for an line
	 * @return next line id
	 */
	private int getNextMensaLineID() {
		
		
		
		// get highest id
		Uri uri = Uri.parse(StorageContract.MENSA_LINE_URI);
		String[] projection = {ColumnValues.LINE_ID.getName()};
		String sortOrder = ColumnValues.LINE_ID.getName() + " DESC LIMIT 1";
		
		
		
		Cursor c = resolver.query(uri, projection, null, null, sortOrder);
		int id = 0;
		if (c.moveToNext()) {
			// then +1 for new id
			id = c.getInt(c.getColumnIndex(ColumnValues.LINE_ID.getName())) + 1;
		}
		c.close();
		new Database(ctx).close();
		return id;
	}
	
	/**
	 * Returns the id of a mensa line
	 * @param line object of line
	 * @return id in database
	 */
	public int getMensaLineID(MensaLine line) {
		
		// if line has an id :)
		if (line.getID() >= 0) {
			return line.getID();
		}
		
		// is there an object that fits to this data
		
		
		Uri uri = Uri.parse(StorageContract.MENSA_LINE_URI);
		String[] projection =  {ColumnValues.LINE_ID.getName()};
		String selection = ColumnValues.LINE_MENSA.getName() + " = ? AND " + ColumnValues.LINE_NAME.getName() + " = ?"; 
		String[] selectionArgs = {line.getMensaID() + "", line.getName()};
		
		Cursor c = resolver.query(uri, projection, selection, selectionArgs, null);
		
		int id;
		if (c.moveToNext()) {
			id = c.getInt(c.getColumnIndex(ColumnValues.LINE_ID.getName()));
			
		}
		// no object in database that fits, get next free id
		id = getNextMensaLineID();
		c.close();
		new Database(ctx).close();
		return id;
	}
	
	/**
	 * updates a row or inserts a new one
	 * @param uri the uri for the content provider
	 * @param values the used column values
	 * @param where statement for finding the right update row
	 * @param selectionArgs arguments for the selection
	 * @return true if we update
	 */
	public boolean updateOrInsert(Uri uri, ContentValues values, String where, String[] selectionArgs) {
		
		
		
		if (resolver.update(uri, values, where, selectionArgs) == 0) {
			resolver.insert(uri, values);
			return false;
		}
		
		return true;
	}



	@Override
	public void add(List<MensaDay> days) {
		long seconds = (long) System.currentTimeMillis() / 1000;
		
		for (MensaDay day : days) {
			if (!(day.getDateTime() < seconds)) {
			
				add(day);
			}
		}
	}



	@Override
	public int delete(MensaDay t) {

		// first delete meals
		int rows = deleteMeals(t.getDateTime());
		
		// then clean the line table
		rows += cleanMensaLine();
		
		return rows;
	}
	
	/**
	 * clean the mensa line table
	 * @return rows affected
	 */
	public int cleanMensaLine() {
		
		Uri uri = Uri.parse(StorageContract.MEAL_URI);
	
		// get all used line ids
		Cursor c = query(uri, new String[]{ColumnValues.MEAL_LINE.getName()}, null, null, null);
		
		SparseBooleanArray arr = new SparseBooleanArray();
		
		// add ids in list to sparse array
		while (c.moveToNext()) {
			int id = c.getInt(c.getColumnIndex(ColumnValues.MEAL_LINE.getName()));
			
			arr.put(id, true);
		}
		
		c.close();
		new Database(ctx).close();
		
		uri = Uri.parse(StorageContract.MENSA_LINE_URI);
		
		// get all line ids in database
		c = query(uri, new String[]{ColumnValues.LINE_ID.getName()}, null, null, null);
		
		int count = 0;
		
		// if id not in sparse array we can delete the row
		while (c.moveToNext()) {
			
			int id = c.getInt(c.getColumnIndex(ColumnValues.LINE_ID.getName()));
			
			if (!arr.get(id)) {
				count += deleteLine(id);
			}
		}
		c.close();
		new Database(ctx).close();
		return count;
	}
	
	public void closeDatabase() {
		new Database(ctx).close();
	}
	
	public int deleteLine(int id) {
		
		Uri uri = Uri.parse(StorageContract.MENSA_LINE_URI);
		
		String where = ColumnValues.LINE_ID.getName() + " = ?";
		String[] selectionArgs = {String.valueOf(id)};
		
		return delete(uri, where, selectionArgs);
	}
	
	/**
	 * delete meals by date
	 * @param date date in seconds
	 * @return affected rows
	 */
	public int deleteMeals(long date) {
		
		Uri uri = Uri.parse(StorageContract.MEAL_URI);
		
		String where = ColumnValues.MEAL_DATE.getName() + " = ?";
		String[] selectionArgs = {String.valueOf(date)};
		
		return delete(uri, where, selectionArgs);
	}
	
	/**
	 * Deletes from database
	 * @param uri the uri where we want to delete
	 * @param where selection string
	 * @param selectionArgs arguments for selection
	 * @return affected rows
	 */
	public int delete(Uri uri, String where, String[] selectionArgs) {
		
		return resolver.delete(uri, where, selectionArgs);
	}



	@Override
	public List<MensaDay> getAll() {
		Log.d("Storage_Mensa|getAll()", "do it");
		List<MensaDay> days = new LinkedList<MensaDay>();
		
		Uri uri = Uri.parse(StorageContract.MENSA_MEAL_DISTINCT_URI);
		String[] projection = {ColumnValues.MEAL_DATE.getName()};
		String sortOrder = ColumnValues.MEAL_DATE.getName() + " ASC";
		
		Cursor c = query(uri, projection, null, null, sortOrder);
		
		while (c.moveToNext()) {
			MensaDay day = new MensaDay(c.getLong(c.getColumnIndex(ColumnValues.MEAL_DATE.getName())));
			Log.d("Storage_Mensa|getAll()", day.getDateTime() + "");
			days.add(get(day));
		}
		c.close();
		new Database(ctx).close();
		return days;
	}



	@Override
	public int delete(List<MensaDay> t) {
		// TODO Auto-generated method stub
		return 0;
	}

}
