package com.abhinav.qcards;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.abhinav.qcards.MyAdapter.ViewHolder;

public class MyAdapter extends RecyclerView.Adapter<ViewHolder> {
	private static final String TAG = null;
	static List<MyCards> cards;
	Context context;
	Context appContext;
	int caller;
	

	public MyAdapter(Context context, List<MyCards> cards, int parent, Context appContext) {
		this.cards = cards;
		this.context = context;
		this.caller = parent;
		this.appContext = appContext;
	}

	public MyAdapter(Context context, List<MyCards> cards, Context appContext) {
		this.cards = cards;
		this.context = context;
		this.caller = 0;
		this.appContext = appContext;
	}

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return (null != cards ? cards.size() : 0);
	}

	public void onBindViewHolder(ViewHolder myViewHolder, int position) {
		MyCards card = cards.get(position);
		myViewHolder.nameView.setText("Name: " + card.getName());
		myViewHolder.idView.setText("Card ID: " + card.getId());
		// myViewHolder.presentView.setText(card.isPresent().toString());
		if (caller == 1) {
			if (card.isPresent() == true) {
				myViewHolder.cardView.setCardBackgroundColor(Color.GREEN);
			} else {
				myViewHolder.cardView.setCardBackgroundColor(Color.RED);
			}
		} else {
			/*if (Roster.colorIndices != null) {
				if (Roster.colorIndices.contains(card.getId())) {
					myViewHolder.cardView.setCardBackgroundColor(Color
							.parseColor("#EFF2BB"));
				}
			}else{*/
			myViewHolder.cardView.setCardBackgroundColor(Color.WHITE);
			/*}*/
		}
		// myViewHolder.presentView.setText(card.isPresent().toString());

		// myViewHolder.presentView.setChecked(true);
		// TODO Auto-generated method stub

	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
		View v = LayoutInflater.from(viewGroup.getContext()).inflate(
				R.layout.item_layout, null);
		ViewHolder mh = new ViewHolder(v);
		MyCards card = cards.get(position);
		if (caller == 1) {
			if (card.isPresent() == true) {
				mh.cardView.setCardBackgroundColor(Color.GREEN);
			} else {
				mh.cardView.setCardBackgroundColor(Color.RED);
			}
		} else {
			/*if(Roster.colorIndices.contains(card.getId())){
				mh.cardView.setCardBackgroundColor(Color
				.parseColor("#EFF2BB"));
			}else{*/
			mh.cardView.setCardBackgroundColor(Color.LTGRAY);
			/*}*/
		}
		return mh;
	}

	public void delete(int position) {
		saveData(position);

		cards.remove(position);
		notifyItemRemoved(position);
	}

	public void saveData(int position) {
		MyCards c = cards.get(position);

	}

	public class ViewHolder extends RecyclerView.ViewHolder implements
			View.OnClickListener, OnLongClickListener {
		protected TextView nameView;
		protected TextView idView;
		// protected TextView presentView;
		protected CardView cardView;
		EditText eName;
		EditText eId;
		View contextView;
		Dialog dialog;
		ArrayList<String> entriesId;

		public ViewHolder(View view) {
			super(view);
			contextView = view;
			this.nameView = (TextView) view.findViewById(R.id.sName);
			this.idView = (TextView) view.findViewById(R.id.sId);
			// this.presentView = (TextView) view.findViewById(R.id.sPresent);
			this.cardView = (CardView) view.findViewById(R.id.card_view);
			this.cardView.setOnClickListener(this);
			this.cardView.setOnLongClickListener(this);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// delete(+getLayoutPosition());
			// cards.remove(+getLayoutPosition());
			// if (AttendanceResults.attendance_results == true) {
			// toggle present status
			if (caller == 1) {
				if (cards.get(getLayoutPosition()).isPresent() == false) {
					cards.get(getLayoutPosition()).setPresent(true);
					cardView.setCardBackgroundColor(Color.GREEN);
					// presentView.setText(cards.get(getLayoutPosition()).isPresent().toString());
				} else {
					cards.get(getLayoutPosition()).setPresent(false);
					cardView.setCardBackgroundColor(Color.RED);
					// presentView.setText(cards.get(getLayoutPosition()).isPresent().toString());
				}
			} else {
				entriesId = new ArrayList<String>();
				Iterator<MyCards> it = cards.iterator();
				while (it.hasNext()) {
					MyCards card = it.next();
					entriesId.add(card.getId());
				}
				openDialog(v);
				// Roster.cardList = cards;
				/*
				 * if(Roster.isRosterModified()){ try { saveModified();
				 * Roster.unsetModifyRoster(); } catch (IOException e) { // TODO
				 * Auto-generated catch block e.printStackTrace(); } }
				 */
			}
		}/*
		 * else { Intent i = new Intent(contextView.getContext(),
		 * QuickPoll.class);
		 * 
		 * contextView.getContext().startActivity(i); if
		 * (cards.get(getLayoutPosition()).isPresent() == false) {
		 * cardView.setCardBackgroundColor(Color.GREEN); } }
		 */

		@Override
		public boolean onLongClick(View v) {
			// all ids are read into entriesId to check that new ID is not
			// already present
			entriesId = new ArrayList<String>();
			Iterator<MyCards> it = cards.iterator();
			while (it.hasNext()) {
				MyCards card = it.next();
				entriesId.add(card.getId());
			}
			// TODO Auto-generated method stub
			editDeleteDialog(v);

			// Roster.cardList = cards;
			/*
			 * if(Roster.isRosterModified()){ try { saveModified();
			 * Roster.unsetModifyRoster(); } catch (IOException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); } }
			 */
			// openDialog(v);
			// AlertDialog.Builder alert = new
			// AlertDialog.Builder(v.getContext());
			return false;
		}

		private void editDeleteDialog(View v) {
			dialog = new Dialog(v.getContext());
			dialog.setContentView(R.layout.edit_delete_dialog);
			Button editButton = (Button) dialog.findViewById(R.id.edit_button);
			Button deleteButton = (Button) dialog
					.findViewById(R.id.delete_button);
			editButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					openDialog(v);
					dialog.dismiss();

				}
			});

			deleteButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					// recalculate the color indices
					/*if (Roster.colorIndices.size()>0) {
						if (getLayoutPosition() > Roster.colorIndices.get(0)) {
							int index = Roster.colorIndices.indexOf(cards
									.get(getLayoutPosition()));
							for (int i = index; i < Roster.colorIndices.size(); i++) {
								Roster.colorIndices.add(i,
										Roster.colorIndices.get(i) - 1);

							}
						} else {
							for (int i = 0; i < Roster.colorIndices.size(); i++) {
								Roster.colorIndices.add(i,
										Roster.colorIndices.get(i) - 1);

							}
						}
					}*/
					delete(getLayoutPosition());

					try {
						saveModified();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					dialog.dismiss();
				}
			});

			dialog.show();

		}

		private void openDialog(View v) {
			final Dialog odialog = new Dialog(v.getContext());
			odialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			odialog.setContentView(R.layout.edit_roster_entry);
			eName = (EditText) odialog.findViewById(R.id.editName);
			eId = (EditText) odialog.findViewById(R.id.editId);
			Button eDone = (Button) odialog.findViewById(R.id.editDone);

			// eDone.setEnabled(false);

			final String oldName;
			final String oldId;

			eName.setText(cards.get(getLayoutPosition()).getName().toString());
			eId.setText(cards.get(getLayoutPosition()).getId().toString());
			oldName = eName.getText().toString();
			oldId = eId.getText().toString();

			eDone.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					// onDialogButton(v);
					String newName = eName.getText().toString();
					String newId = eId.getText().toString();
					if (!oldName.equals(newName) || !oldId.equals(newId)){
					if (entriesId.contains(newId)) {
						
						AlertDialog.Builder builder = new AlertDialog.Builder(v
								.getContext());
						
						builder.setMessage(R.string.id_exists_message)
								.setTitle(R.string.id_exists_title);
						AlertDialog alert = builder.create();
						alert.show();

					} else {
						cards.get(getLayoutPosition()).setName(newName);
						cards.get(getLayoutPosition()).setId(newId);
						idView.setText("Card ID: "
								+ cards.get(getLayoutPosition()).getId());
						nameView.setText("Name: "
								+ cards.get(getLayoutPosition()).getName());

						// Roster.modifyRoster() sets Roster.rosterModified to
						// true
						// and if the roster
						// has been modified, the user must be prompted to save
						// the
						// new roster or overwrite the existing one
						/*if (!oldName.equals(newName) || !oldId.equals(newId)) {*/
							cardView.setCardBackgroundColor(Color
									.parseColor("#EFF2BB"));
							Roster.setModifyRoster();
							try {
								saveModified();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
						odialog.dismiss();
						// dialog.dismiss();
					}
				
			});

			// set Name and Id in the edit fields

			odialog.show();
		}

		public void saveModified() throws IOException {
			KeyValues keyVal = new KeyValues(appContext,"globals");
			String thisRosterFile = keyVal.getRoster();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < cards.size(); i++) {
				sb.append(cards.get(i).getName());
				sb.append(",");
				sb.append(cards.get(i).getId());
				sb.append("\n");
			}
			String writeToFile = sb.toString();
			File filePath = new File(Environment.getExternalStorageDirectory()
					+ "/QCards/Rosters/" + thisRosterFile);
			if (filePath.delete()) {
				saveNewRosterFile(cards, thisRosterFile);

			}
			Log.e(TAG, "it will write");
		}

		public void saveNewRosterFile(List<MyCards> entries, String fileName)
				throws IOException {
			// checking if External Storage is available
			Boolean isExternalAvailable = android.os.Environment
					.getExternalStorageState().equals(
							android.os.Environment.MEDIA_MOUNTED);
			File appDirectory;
			FileOutputStream fos;
			StringBuilder putInFileBuilder = new StringBuilder();
			if (isExternalAvailable) {
				for (int i = 0; i < entries.size(); i++) {
					MyCards entry = entries.get(i);
					putInFileBuilder.append(entry.getName());
					putInFileBuilder.append(",");
					putInFileBuilder.append(entry.getId());
					putInFileBuilder.append("\n");
				}

				// get path to QCards directory on External Storage
				appDirectory = new File(
						Environment.getExternalStorageDirectory()
								+ "/QCards/Rosters");
				if (!appDirectory.exists()) {
					if (appDirectory.mkdir()) {
						Log.e(TAG, "Directory Created");
					}
				} else {
					File saveIt = new File(appDirectory, fileName);
					fos = new FileOutputStream(saveIt);
					fos.write(putInFileBuilder.toString().getBytes());
					fos.close();
					String[] fileList = appDirectory.list();
					Log.e(TAG, "Directory Created");
					Toast saveNotify = Toast.makeText(context,
							"Changes saved!", Toast.LENGTH_SHORT);
					saveNotify.show();
					Roster.unsetModifyRoster();
				}
			} else {
				Log.e(TAG, "SD Card unavailable");
			}
		}

		public void onDialogButton(View v) {
			// final Dialog dial = new Dialog(v.getContext());
			String newName = eName.getText().toString();
			String newId = eName.getText().toString();
			cards.get(getLayoutPosition()).setName(newName);
			cards.get(getLayoutPosition()).setId(newId);
			// dial.show();
		}
	}
}
