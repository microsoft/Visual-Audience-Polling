package com.abhinav.qcards;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.abhinav.qcards.RosterListAdapter.ViewHolder;

public class RosterListAdapter extends RecyclerView.Adapter<ViewHolder> {
	static List<RosterListCard> cards;
	Context context;
	KeyValues keyVal;
	public static boolean fromRosterList = false;
	public static String fileName;

	public RosterListAdapter(Context context, List<RosterListCard> cards) {
		this.cards = cards;
		this.context = context;
		//keyVal = new KeyValues(context,"globals");
	}

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return (null != cards ? cards.size() : 0);
	}

	public void onBindViewHolder(ViewHolder myViewHolder, int position) {
		RosterListCard card = cards.get(position);
		myViewHolder.rosterNameView.setText(card.getName());
		myViewHolder.numView.setText(card.getNum() + " students");

		// myViewHolder.presentView.setChecked(true);
		// TODO Auto-generated method stub

	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int arg1) {
		View v = LayoutInflater.from(viewGroup.getContext()).inflate(
				R.layout.list_roster_cards, null);
		ViewHolder mh = new ViewHolder(v);
		return mh;
	}

	public void delete(int position) {
		saveData(position);
		cards.remove(position);
		notifyItemRemoved(position);
	}

	public void saveData(int position) {
		RosterListCard c = cards.get(position);

	}

	public class ViewHolder extends RecyclerView.ViewHolder implements
			View.OnClickListener, OnLongClickListener {
		protected TextView rosterNameView;
		protected TextView numView;
		protected CardView cardView;
		View contextView;

		public ViewHolder(View view) {
			super(view);
			contextView = view;
			this.rosterNameView = (TextView) view.findViewById(R.id.rName);
			this.numView = (TextView) view.findViewById(R.id.rNum);
			this.cardView = (CardView) view
					.findViewById(R.id.card_view_roster_list);
			this.cardView.setOnClickListener(this);
			this.cardView.setOnLongClickListener(this);
		}

		@Override
		public void onClick(View v) {
			Intent i = new Intent(contextView.getContext(), Roster.class);
			fileName = cards.get(getLayoutPosition()).getName();
			i.putExtra("fileName", fileName);
			fromRosterList = true;
			i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			contextView.getContext().startActivity(i);
			//cardView.setCardBackgroundColor(Color.BLUE);

			// TODO Auto-generated method stub
			// delete(+getLayoutPosition());
			// cards.remove(+getLayoutPosition());

		}

		@Override
		public boolean onLongClick(View v) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					v.getContext());
			builder.setItems(
					R.array.edit_delete_rename,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case 0:
								
								try {
									doCopy();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								break;
							case 1:
								doRename();
								break;
							case 2:
								doDelete();
								break;
							}
						}
					});
			AlertDialog opDialog = builder.create();
			opDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			opDialog.show();
			return false;
		}

		/*
		 * public boolean onLongClick(View v) { final Dialog delDialog = new
		 * Dialog(v.getContext());
		 * delDialog.setContentView(R.layout.delete_dialog); Button pBut =
		 * (Button) delDialog.findViewById(R.id.delete_button_pos); Button nBut
		 * = (Button) delDialog.findViewById(R.id.delete_button_neg); TextView
		 * tv = (TextView) delDialog.findViewById(R.id.delete_warning);
		 * tv.setText("Do you want to permanently delete this question set?");
		 * delDialog.setTitle("Delete!"); pBut.setOnClickListener(new
		 * View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub File filePath = new File(Environment
		 * .getExternalStorageDirectory() + "/QCards/Rosters/" +
		 * cards.get(getLayoutPosition()).getFileName()); if(filePath.delete()){
		 * delete(getLayoutPosition()); } delDialog.dismiss(); } });
		 * 
		 * nBut.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub delDialog.dismiss(); } }); delDialog.show(); // TODO
		 * Auto-generated method stub return false;
		 * 
		 * }
		 */

		public void doCopy() throws IOException {
			File fileDirectory = new File(Environment.getExternalStorageDirectory()
					+ "/QCards/Rosters/");
			File filePath = new File(Environment.getExternalStorageDirectory()
					+ "/QCards/Rosters/" + cards.get(getLayoutPosition()).getName()
					+ ".csv");
			String fName = cards.get(getLayoutPosition()).getName();
			//String copyName = getCopyName(fileDirectory, fName);
			String copyName = getCopyName(fileDirectory,fName);
			/*checkOtherCopies(cards.get(getLayoutPosition()).getName()
					+ ".csv");
			*/
			
			//fName+=".csv";
			File copyPath = new File(Environment.getExternalStorageDirectory()
					+ "/QCards/Rosters/" + copyName+".csv");
			
			FileReader fr = new FileReader(filePath);
			FileWriter fw = new FileWriter(copyPath);
			
			BufferedReader br = new BufferedReader(fr);
			BufferedWriter bw = new BufferedWriter(fw);
			String data = null;
			while((data = br.readLine())!= null){
				bw.append(data);
				bw.newLine();
			}
			bw.close();
			br.close();
			fw.close();
			fr.close();
			
			
			
			Intent i = new Intent(context, RosterList.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			context.startActivity(i);
			// notifyItemChanged(cards.size());
			
		}
		
		/*public String getCopyName(File fileDirectory, String fName){
			String[] fileList = 
			
		}*/

		/*public String checkOtherCopies(String fname){
			
		}*/
		
		public String getCopyName(File fileDirectory, String fName) {
			
			String fileList[] = fileDirectory.list();
			ArrayList<String> files = new ArrayList<String>();
			for(int i=0; i<fileList.length; i++){
				if(fileList[i].endsWith(".csv")){
				String extStripped = RosterList.removeExt(fileList[i]);
				files.add(extStripped);
				}
			}
			int j = 1;
			for(int k=1;k<files.size();k++){
				if(files.contains(fName+"_"+k)){
					j++;
				}else{
					break;
				}
			}
			
			String copyName = fName+"_"+j;
			
			return copyName;

		}

		public boolean doDelete() {
			File filePath = new File(Environment.getExternalStorageDirectory()
					+ "/QCards/Rosters/" + cards.get(getLayoutPosition()).getName()
					+ ".csv");
			if (filePath.delete()) {
				delete(getLayoutPosition());
				Toast delNotify = Toast.makeText(contextView.getContext(),
						"Successfully deleted.", Toast.LENGTH_SHORT);
				delNotify.show();
				return true;
			} else {
				return false;
			}
		}

		public boolean doRename() {
			File fileDirectory = new File(
					Environment.getExternalStorageDirectory()
							+ "/QCards/Rosters/");
			String oldFile = cards.get(getLayoutPosition()).getName() + ".csv";
			File oldPath = new File(fileDirectory + "/" + oldFile);
			File newPath = new File(fileDirectory + "/");

			renameDialog(oldPath, newPath);

			return false;
		}

		public void renameDialog(File oldPath, File newPath) {
			final File oldf = oldPath;
			final File newf = newPath;

			final Dialog dial = new Dialog(contextView.getContext());
			dial.setContentView(R.layout.new_roster_name);
			dial.setTitle("Rename File");
			Button sButton = (Button) dial.findViewById(R.id.createButton);
			final EditText fName = (EditText) dial
					.findViewById(R.id.newRosterName);
			sButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					// onDialogButton(v);
					File newFile = new File(newf + "/"
							+ fName.getText().toString() + ".csv");
					if (!newFile.exists()) {
						if (oldf.renameTo(newFile)) {
							Toast renameNotify = Toast.makeText(
									contextView.getContext(),
									"File successfully renamed",
									Toast.LENGTH_SHORT);
							renameNotify.show();
							dial.dismiss();
						}
					} else {
						showDuplicateAlert();
						Toast duplicateNotify = Toast.makeText(
								contextView.getContext(),
								"A file with the same name already exists, please use some other name.",
								Toast.LENGTH_SHORT);

					}
				}
			});
			dial.show();

		}

		public void showDuplicateAlert() {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					contextView.getContext());

			builder.setMessage(R.string.file_exists_message).setTitle(
					R.string.file_exists_title);
			AlertDialog alert = builder.create();

			alert.show();

		}

	}

}
