package com.abhinav.qcards;

import java.io.File;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.abhinav.qcards.QSetAdapter.ViewHolder;

public class QSetAdapter extends RecyclerView.Adapter<ViewHolder> {
	static List<RosterListCard> cards;
	Context context;

	public QSetAdapter(Context context, List<RosterListCard> cards) {
		this.cards = cards;
		this.context = context;
	}

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return (null != cards ? cards.size() : 0);
	}

	public void onBindViewHolder(ViewHolder myViewHolder, int position) {
		RosterListCard card = cards.get(position);
		myViewHolder.rosterNameView.setText(card.getName());
		myViewHolder.numView.setText(card.getNum() + " questions");

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
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onClick(View v) {
			Intent i = new Intent(contextView.getContext(), QSet.class);
			//QSet.qSetNameGlobal = cards.get(getPosition()).getName();
			
			 i.putExtra("setName", cards.get(getPosition()).getFileName());
			contextView.getContext().startActivity(i);
			cardView.setCardBackgroundColor(Color.BLUE);

			// TODO Auto-generated method stub
			// delete(+getPosition());
			// cards.remove(+getPosition());

		}

		@Override
		public boolean onLongClick(View v) {
			final Dialog delDialog = new Dialog(v.getContext());
			delDialog.setContentView(R.layout.delete_dialog);
			Button pBut = (Button) delDialog.findViewById(R.id.delete_button_pos);
			Button nBut = (Button) delDialog.findViewById(R.id.delete_button_neg);
			TextView tv = (TextView) delDialog.findViewById(R.id.delete_warning);
			tv.setText("Do you want to permanently delete this question set?");
			delDialog.setTitle("Delete!");
			pBut.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					File filePath = new File(Environment
							.getExternalStorageDirectory()
							+ "/QCards/QuestionSet/"
							+ cards.get(getPosition()).getFileName());
					if(filePath.delete()){
						delete(getPosition());
					}
					delDialog.dismiss();
				}
			});

			nBut.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					delDialog.dismiss();
				}
			});
			delDialog.show();
			// TODO Auto-generated method stub
			return false;
			
		}

	}

}
