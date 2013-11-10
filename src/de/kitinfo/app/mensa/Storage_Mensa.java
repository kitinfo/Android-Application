/**
 * 
 */
package de.kitinfo.app.mensa;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.SparseArray;
import de.kitinfo.app.data.StorageContract;
import de.kitinfo.app.data.StorageInterface;
import de.kitinfo.app.data.Database.ColumnValues;

/**
 * @author mpease
 *
 */
public class Storage_Mensa implements StorageInterface<MensaDay> {

	private static final String SPLIT_SYMBOL = ";";
	private static final Mensa m = Mensa.ADENAUER;
	private Context ctx;
	
	public Storage_Mensa(Context ctx) {
		this.ctx = ctx;
	}
	
	
	
	@Override
	public void add(MensaDay day) {
		
		
		// get uris from contract
		Uri mensaLineUri = Uri.parse(StorageContract.MENSA_LINE_URI);
		Uri mensaMealUri = Uri.parse(StorageContract.MEAL_URI);
		
		// define
		ContentValues values;
		String lineWhere = ColumnValues.LINE_ID.getName() + " = ? AND " + ColumnValues.LINE_MENSA.getName() + " = ?";
		String mealWhere = ColumnValues.MEAL_DATE.getName() + " = ? AND " + ColumnValues.MEAL_LINE.getName() + " = ?";
		
		
		// insert mensa lines
		List<MensaLine> lines = day.getLines();
		
		for (MensaLine ml : lines) {
			values = convertLineToContentValues(ml, m);
			
			String[] lineArgs = {String.valueOf(values.get(ColumnValues.LINE_ID.getName())), String.valueOf(m.ordinal())};
			
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
		
		return values;
	}

	/**
	 * Returns a mensa day object from database with same properties as the given mensa day object (general the date)
	 * @param day the object with date property inside
	 * @return a mensa day object
	 */
	@Override
	public MensaDay get(MensaDay day) {
		
		
		
		Uri mealUri = Uri.parse(StorageContract.MEAL_URI);
		String selection = ColumnValues.MEAL_DATE.getName() + " = ?";
		String[] selectionArgs = {String.valueOf(day.getDateTime())};
		Cursor c = query(mealUri, null, selection, selectionArgs, "ASC");
		
		// get meals
		SparseArray<List<MensaMeal>> meals = new SparseArray<List<MensaMeal>>();
		
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
		
		// get lines
		List<MensaLine> mensaLines = new LinkedList<MensaLine>();
		
		for (int i = 0; i < meals.size(); i++) {
			int key = meals.keyAt(i);
			
			MensaLine ml = getMensaLine(key);
			
			if (ml == null) {
				break;
			}
			
			// add meals to list
			for (MensaMeal mm : meals.get(key)) {
				ml.addMeal(mm);
			}
		}
		
		
		MensaDay newDay = new MensaDay(day.getDateTime());
		
		// add lines
		for (MensaLine ml : mensaLines) {
			newDay.addLine(ml);
		}
		
		
		return newDay;
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
		ContentResolver resolver = ctx.getContentResolver();
		
		
		return resolver.query(uri, projection, selection, selectionArgs, sortOrder);
	}
	
	
	
	/**
	 * converts a cursor to a meal object
	 * @param c cursor from database
	 * @return mensa meal object
	 */
	public MensaMeal convertCursorToMensaMeal(Cursor c) {
		
		int id = c.getInt(c.getColumnIndex(ColumnValues.MEAL_ID.getName()));
		float price = c.getFloat(c.getColumnIndex(ColumnValues.MEAL_PRICE.getName()));
		String adds = c.getString(c.getColumnIndex(ColumnValues.MEAL_ADDS.getName()));
		String info = c.getString(c.getColumnIndex(ColumnValues.MEAL_INFO.getName()));
		String hint = c.getString(c.getColumnIndex(ColumnValues.MEAL_HINT.getName()));
		String name = c.getString(c.getColumnIndex(ColumnValues.MEAL_NAME.getName()));
		String tag = c.getString(c.getColumnIndex(ColumnValues.MEAL_TAGS.getName()));
		
		String[] tags = tag.split(SPLIT_SYMBOL);
		
		List<String> addList = new LinkedList<String>();
		for (String a : adds.split(SPLIT_SYMBOL)) {
			addList.add(a);
		}
		
		
		return new MensaMeal(id, tags[0].isEmpty(), tags[1].isEmpty(), tags[2].isEmpty(), tags[3].isEmpty(), tags[4].isEmpty(), tags[5].isEmpty(), tags[6].isEmpty(), name, hint, info, price, addList);
	}
	
	
	
	

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
	
	
	/*
	 * returns the next id for an line
	 * @return next line id
	 */
	private int getNextMensaLineID() {
		
		ContentResolver resolver = ctx.getContentResolver();
		
		// get highest id
		Uri uri = Uri.parse(StorageContract.MENSA_LINE_URI);
		String[] projection = {ColumnValues.LINE_ID.getName()};
		String sortOrder = "DESC LIMIT 1";
		
		
		
		Cursor c = resolver.query(uri, projection, null, null, sortOrder);
		int id = 0;
		if (c.moveToNext()) {
			// then +1 for new id
			id = c.getInt(c.getColumnIndex(ColumnValues.LINE_ID.getName())) + 1;
		}
		c.close();
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
		ContentResolver resolver = ctx.getContentResolver();
		
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
		
		ContentResolver resolver = ctx.getContentResolver();
		
		if (resolver.update(uri, values, where, selectionArgs) == 0) {
			resolver.insert(uri, values);
			return false;
		}
		
		return true;
	}



	@Override
	public void add(List<MensaDay> days) {
		for (MensaDay day : days) {
			add(day);
		}
	}



	@Override
	public int delete(MensaDay t) {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public List<MensaDay> getAll() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public int delete(List<MensaDay> t) {
		// TODO Auto-generated method stub
		return 0;
	}

}
