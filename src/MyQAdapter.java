package com.abhinav.qcards;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.abhinav.qcards.MyQAdapter.ViewHolder;

public class MyQAdapter extends RecyclerView.Adapter<ViewHolder> {
	static List<QSetCards> cards;
	Context context;
	int caller;
	int qClickId = -1;

	public MyQAdapter(Context context, List<QSetCards> cards, int parent) {
		this.cards = cards;
		this.context = context;
		this.caller = parent;
	}

	public MyQAdapter(Context context, List<QSetCards> cards) {
		this.cards = cards;
		this.context = context;
		this.caller = 0;
	}

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return (null != cards ? cards.size() : 0);
	}

	public void onBindViewHolder(ViewHolder myViewHolder, int position) {
		QSetCards card = cards.get(position);
		myViewHolder.viewQues.setText(card.getQuestion());
		/*
		 * myViewHolder.viewA.setText(card.getA());
		 * myViewHolder.viewB.setText(card.getB());
		 * myViewHolder.viewC.setText(card.getC());
		 * myViewHolder.viewD.setText(card.getD());
		 */
		Integer pos = position + 1;
		myViewHolder.labelQues.setText(pos.toString() + ". ");
		/*
		 * if (caller == 1) { if (card.isPresent() == true) {
		 * myViewHolder.cardView.setBackgroundColor(Color.GREEN); } else {
		 * myViewHolder.cardView.setBackgroundColor(Color.RED); } } else {
		 * myViewHolder.cardView.setBackgroundColor(Color.WHITE); }
		 */
		// myViewHolder.presentView.setText(card.isPresent().toString());

		// myViewHolder.presentView.setChecked(true);
		// TODO Auto-generated method stub

	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
		View v = LayoutInflater.from(viewGroup.getContext()).inflate(
				R.layout.list_qset_cards, null);
		ViewHolder mh = new ViewHolder(v);
		QSetCards card = cards.get(position);
		/*
		 * if (caller == 1) { if (card.isPresent() == true) {
		 * mh.cardView.setBackgroundColor(Color.GREEN); } else {
		 * mh.cardView.setBackgroundColor(Color.RED); } } else {
		 * mh.cardView.setBackgroundColor(Color.LTGRAY); }
		 */
		return mh;
	}

	public void delete(int position) {
		saveData(position);
		cards.remove(position);
		notifyItemRemoved(position);
	}

	public void saveData(int position) {
		QSetCards c = cards.get(position);

	}

	public class ViewHolder extends RecyclerView.ViewHolder implements
			View.OnClickListener, OnLongClickListener {
		protected TextView labelQues;
		protected TextView viewQues;
		protected TextView labelA;
		protected TextView viewA;
		protected TextView labelB;
		protected TextView viewB;
		protected TextView labelC;
		protected TextView viewC;
		protected TextView labelD;
		protected TextView viewD;
		protected CardView cardView;

		protected EditText dialogQues;
		protected EditText dialogA;
		protected EditText dialogB;
		protected EditText dialogC;
		protected EditText dialogD;
		
		protected ImageButton showQuesBut;
		protected ImageButton hideQuesBut;
		
		View contextView;
		Dialog dialog;

		public ViewHolder(View view) {
			super(view);
			contextView = view;
			this.labelA = (TextView) view.findViewById(R.id.labelA);
			this.labelB = (TextView) view.findViewById(R.id.labelB);
			this.labelC = (TextView) view.findViewById(R.id.labelC);
			this.labelD = (TextView) view.findViewById(R.id.labelD);
			this.labelQues = (TextView) view.findViewById(R.id.quesNum);
			this.viewA = (TextView) view.findViewById(R.id.opA);
			this.viewB = (TextView) view.findViewById(R.id.opB);
			this.viewC = (TextView) view.findViewById(R.id.opC);
			this.viewD = (TextView) view.findViewById(R.id.opD);
			this.viewQues = (TextView) view.findViewById(R.id.quest);
			this.cardView = (CardView) view.findViewById(R.id.card_view);
			this.showQuesBut = (ImageButton) view.findViewById(R.id.showQuesButton);
			/*this.showQuesBut.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
						viewQues.setVisibility(View.VISIBLE);
						viewA.setVisibility(View.VISIBLE);
						viewB.setVisibility(View.VISIBLE);
						viewC.setVisibility(View.VISIBLE);
						viewD.setVisibility(View.VISIBLE);
						
						viewQues.setText(cards.get(getPosition()).getQuestion());
						viewA.setText(cards.get(getPosition()).getA());
						viewB.setText(cards.get(getPosition()).getB());
						viewC.setText(cards.get(getPosition()).getC());
						viewD.setText(cards.get(getPosition()).getD());
						showQuesBut.setVisibility(View.GONE);
					
				}
			});*/
			this.cardView.setOnClickListener(this);
			this.cardView.setOnLongClickListener(this);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//viewQues.setVisibility(View.GONE);
			
			if(cards.get(getPosition()).isExpanded()){
			viewA.setVisibility(View.GONE);
			viewB.setVisibility(View.GONE);
			viewC.setVisibility(View.GONE);
			viewD.setVisibility(View.GONE);
			showQuesBut.setVisibility(View.VISIBLE);
			cards.get(getPosition()).setExpanded(false);
			}else{
				viewQues.setVisibility(View.VISIBLE);
				viewA.setVisibility(View.VISIBLE);
				viewB.setVisibility(View.VISIBLE);
				viewC.setVisibility(View.VISIBLE);
				viewD.setVisibility(View.VISIBLE);
				
				viewQues.setText(cards.get(getPosition()).getQuestion());
				viewA.setText(cards.get(getPosition()).getA());
				viewB.setText(cards.get(getPosition()).getB());
				viewC.setText(cards.get(getPosition()).getC());
				viewD.setText(cards.get(getPosition()).getD());
				showQuesBut.setVisibility(View.GONE);
				cards.get(getPosition()).setExpanded(true);
			}
		}

		@Override
		public boolean onLongClick(View v) {
			// TODO Auto-generated method stub
			openDialog(v);
			// AlertDialog.Builder alert = new
			// AlertDialog.Builder(v.getContext());
			return false;
		}
		
		
		private void openDialog(View v) {
			dialog = new Dialog(v.getContext());
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.edit_qset_entry);
			dialogQues = (EditText) dialog.findViewById(R.id.editQues);
			dialogA = (EditText) dialog.findViewById(R.id.editA);
			dialogB = (EditText) dialog.findViewById(R.id.editB);
			dialogC = (EditText) dialog.findViewById(R.id.editC);
			dialogD = (EditText) dialog.findViewById(R.id.editD);
			Button eDone = (Button) dialog.findViewById(R.id.editDone);

			final String oldQues = cards.get(getPosition()).getQuestion();
			final String oldA = cards.get(getPosition()).getA();
			final String oldB = cards.get(getPosition()).getB();
			final String oldC = cards.get(getPosition()).getC();
			final String oldD = cards.get(getPosition()).getD();

			dialogQues.setText(oldQues);
			dialogA.setText(oldA);
			dialogB.setText(oldB);
			dialogC.setText(oldC);
			dialogD.setText(oldD);

			eDone.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					// onDialogButton(v);
					String newQues = dialogQues.getText().toString();
					String newA = dialogA.getText().toString();
					String newB = dialogB.getText().toString();
					String newC = dialogC.getText().toString();
					String newD = dialogD.getText().toString();

					cards.get(getPosition()).setQuestion(newQues);
					cards.get(getPosition()).setA(newA);
					cards.get(getPosition()).setB(newB);
					cards.get(getPosition()).setC(newC);
					cards.get(getPosition()).setD(newD);

					viewQues.setText(cards.get(getPosition()).getQuestion());
					viewA.setText(cards.get(getPosition()).getA());
					viewB.setText(cards.get(getPosition()).getB());
					viewC.setText(cards.get(getPosition()).getC());
					viewD.setText(cards.get(getPosition()).getD());

					// Roster.modifyRoster() sets Roster.rosterModified to true
					// and if the roster
					// has been modified, the user must be prompted to save the
					// new roster or overwrite the existing one
					if (!oldQues.equals(newQues) || !oldA.equals(newA)
							|| !oldB.equals(newB) || !oldC.equals(newC)
							|| !oldD.equals(newD)) {
						cardView.setCardBackgroundColor(Color.parseColor("#EFF2BB"));
						QSet.modifyQset();
						try {
							if (saveChangesToQSet()) {
								Context context = v.getContext();
								CharSequence text = "Changes saved!";
								int duration = Toast.LENGTH_SHORT;
								Toast toast = Toast.makeText(context, text,
										duration);
								toast.show();
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					dialog.dismiss();
				}
			});

			// set Name and Id in the edit fields

			dialog.show();
		}

		private boolean saveChangesToQSet() throws IOException {
			String curQ = QSet.qSetNameGlobal + ".csv";
			saveNewQSetFile(cards, curQ);
			return true;
		}

		public void saveNewQSetFile(List<QSetCards> entries, String file_Name)
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
					QSetCards entry = entries.get(i);
					putInFileBuilder.append(entry.getQuestion());
					putInFileBuilder.append(",");
					putInFileBuilder.append(entry.getA());
					putInFileBuilder.append(",");
					putInFileBuilder.append(entry.getB());
					putInFileBuilder.append(",");
					putInFileBuilder.append(entry.getC());
					putInFileBuilder.append(",");
					putInFileBuilder.append(entry.getD());
					putInFileBuilder.append("\n");
				}

				// get path to QCards directory on External Storage
				appDirectory = new File(
						Environment.getExternalStorageDirectory()
								+ "/QCards/QuestionSet");
				if (!appDirectory.exists()) {
					if (appDirectory.mkdir()) {
						File saveIt = new File(appDirectory, file_Name);
						fos = new FileOutputStream(saveIt);
						fos.write(putInFileBuilder.toString().getBytes());
						fos.close();
						// String[] fileList = appDirectory.list();
						// Log.e(TAG, "Directory Created");
					}
				} else {
					File saveIt = new File(appDirectory, file_Name);
					fos = new FileOutputStream(saveIt);
					fos.write(putInFileBuilder.toString().getBytes());
					fos.close();
					String[] fileList = appDirectory.list();
					// Log.e(TAG, "Directory Created");
				}
			} else {
				// Log.e(TAG, "SD Card unavailable");
			}

		}
		/*
		 * public void onDialogButton(View v) { // final Dialog dial = new
		 * Dialog(v.getContext()); String newName = eName.getText().toString();
		 * String newId = eName.getText().toString();
		 * cards.get(getPosition()).setName(newName);
		 * cards.get(getPosition()).setId(newId); // dial.show(); }
		 */
	}

}
