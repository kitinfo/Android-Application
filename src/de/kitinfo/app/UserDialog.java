package de.kitinfo.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.view.View;

/**
 * This Class provides Methods to open a dialog window
 * 
 * @author indidev
 * 
 */
public class UserDialog {
	/**
	 * context for the dialog
	 */
	private Context ctx;

	/**
	 * creates a new UserDialog object
	 * 
	 * @param ctx
	 *            the context the dialog belongs to
	 */
	public UserDialog(Context ctx) {
		this.ctx = ctx;
	}

	/**
	 * creates a new message dialog
	 * 
	 * @param title
	 *            title of the dialog
	 * @param message
	 *            message of the dialog
	 * @param approveListener
	 *            what to do, when the user approves to the dialog
	 */
	public void openMessageDialog(String title, String message,
			OnClickListener approveListener) {
		openMessageDialog(title, message, getStringOf(R.string.cancel_button),
				getStringOf(R.string.ok), approveListener, null);
	}

	/**
	 * creates a new message dialog
	 * 
	 * @param title
	 *            title of the dialog
	 * @param message
	 *            message of the dialog
	 * @param cancelText
	 *            cancel text
	 * @param approveText
	 *            approve text
	 * @param approveListener
	 *            what to do, when the user approves to the dialog
	 * @param cancelListener
	 *            what to do, when the user cancels the dialog
	 */
	public void openMessageDialog(String title, String message,
			String cancelText, String approveText,
			OnClickListener approveListener, OnClickListener cancelListener) {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ctx);
		dialogBuilder.setMessage(message);

		execDialog(dialogBuilder, title, cancelText, approveText,
				cancelListener, approveListener);
	}

	/**
	 * opens an option dialog
	 * 
	 * @param title
	 *            title of the dialog
	 * @param options
	 *            string array of options this dialog should contain
	 * @param checkedItem
	 *            which item is selected at the beginning
	 * @param changeListener
	 *            onClickListener what to do, when an option is clicked
	 */
	public void openOptionDialog(String title, String[] options,
			int checkedItem, OnClickListener changeListener) {

		openOptionDialog(title, options, checkedItem, getStringOf(R.string.ok),
				changeListener);

	}

	/**
	 * opens an option dialog with
	 * 
	 * @param title
	 *            title of the dialog
	 * @param options
	 *            string array of options this dialog should contain
	 * @param checkedItem
	 *            which item is selected at the beginning
	 * @param approveText
	 *            text of approve button
	 * @param changeListener
	 *            onClickListener what to do, when an option is clicked
	 */
	public void openOptionDialog(String title, String[] options,
			int checkedItem, String approveText, OnClickListener changeListener) {

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ctx);

		dialogBuilder
				.setSingleChoiceItems(options, checkedItem, changeListener);

		execDialog(dialogBuilder, title, null, approveText, null, null);
	}

	/**
	 * opens an option dialog with approve and cancel listener
	 * 
	 * @param title
	 *            title of the dialog
	 * @param options
	 *            string array of options this dialog should contain
	 * @param checkedItem
	 *            which item is selected at the beginning
	 * @param cancelText
	 *            text of the cancel button
	 * @param approveText
	 *            text of approve button
	 * @param changeListener
	 *            onClickListener what to do, when an option is clicked
	 * @param cancelListener
	 *            onClickListener what to do, when the user aborts
	 * @param approveListener
	 *            what the dialog should do if the user clicks the approve
	 *            button
	 */
	public void openOptionDialog(String title, String[] options,
			int checkedItem, String cancelText, String approveText,
			OnClickListener changeListener, OnClickListener cancelListener,
			OnClickListener approveListener) {

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ctx);

		dialogBuilder
				.setSingleChoiceItems(options, checkedItem, changeListener);

		execDialog(dialogBuilder, title, cancelText, approveText,
				cancelListener, approveListener);
	}

	/**
	 * opens an dialog which contains a view
	 * 
	 * @param title
	 *            title of the dialog
	 * @param view
	 *            view this dialog should contain
	 * @param applyButtonListener
	 *            onClickListener what to do, when the user pushes the apply
	 *            button
	 */
	public void openViewDialog(String title, View view,
			OnClickListener applyButtonListener) {
		String approveText = getStringOf(R.string.save_button);

		String cancelText = getStringOf(R.string.cancel_button);

		openViewDialog(title, view, cancelText, approveText,
				applyButtonListener);

	}

	/**
	 * opens an dialog which contains a view
	 * 
	 * @param title
	 *            title of the dialog
	 * @param view
	 *            view this dialog should contain
	 * @param cancelText
	 *            text the cancel button should contain
	 * @param approveText
	 *            text the approve button should contain
	 * @param applyButtonListener
	 *            onClickListener what to do, when the user pushes the apply
	 *            button
	 */
	public void openViewDialog(String title, View view, String cancelText,
			String approveText, OnClickListener applyButtonListener) {

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ctx);

		dialogBuilder.setView(view);

		execDialog(dialogBuilder, title, cancelText, approveText, null,
				applyButtonListener);
	}

	/**
	 * method to execute a dialog
	 * 
	 * @param dialogBuilder
	 *            dialog builder which builds the dialog
	 * @param title
	 *            title of the dialog
	 * @param cancelText
	 *            text the cancel button should display (null if no cancel
	 *            button should be displayed)
	 * @param approveText
	 *            text the approve button should display (null if no approve
	 *            button should be displayed)
	 * @param cancelListener
	 *            onClickListener which will be executed if the user clicks on
	 *            the cancel button
	 * @param approveListener
	 *            onClickListener which will be executed if the user clicks on
	 *            the approve button
	 */
	private void execDialog(AlertDialog.Builder dialogBuilder, String title,
			String cancelText, String approveText,
			OnClickListener cancelListener, OnClickListener approveListener) {

		dialogBuilder.setTitle(title);

		// add cancel button on the left
		if (cancelText != null) {
			dialogBuilder.setPositiveButton(cancelText, cancelListener);
		}

		// add approve button on the right
		if (approveText != null) {
			dialogBuilder.setNegativeButton(approveText, approveListener);
		}

		dialogBuilder.show();
	}

	/**
	 * gets the string with the given resource id out of the resources
	 * 
	 * @param resourceId
	 *            resource id of the string
	 * @return string with the given id
	 */
	private String getStringOf(int resourceId) {
		return ctx.getResources().getString(resourceId);
	}
}
