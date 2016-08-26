package com.abhinav.qcards;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.abhinav.qcards.RosterEntryAdapter.ViewHolder;

public class RosterEntryAdapter extends RecyclerView.Adapter<ViewHolder> {
	static List<RosterEntryCards> cards;
	Context context;
	int caller;

	public RosterEntryAdapter(Context context, List<RosterEntryCards> cards, int parent) {
		this.cards = cards;
		this.context = context;
		this.caller = parent;
	}

	public RosterEntryAdapter(Context context, List<RosterEntryCards> rosterEntries) {
		this.cards = rosterEntries;
		this.context = context;
		this.caller = 0;
	}

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return (null != cards ? cards.size() : 0);
	}

	public void onBindViewHolder(ViewHolder myViewHolder, int position) {
		RosterEntryCards card = cards.get(position);
		myViewHolder.nameView.setText(card.getName());
		myViewHolder.idView.setText(card.getId());

		// myViewHolder.presentView.setText(card.isPresent().toString());

		// myViewHolder.presentView.setChecked(true);
		// TODO Auto-generated method stub

	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
		View v = LayoutInflater.from(viewGroup.getContext()).inflate(
				R.layout.jump_in_attendance_new_card, null);
		ViewHolder mh = new ViewHolder(v);
		return mh;
	}

	public void delete(int position) throws IOException {
		//saveData(position);
		cards.remove(position);
		notifyItemRemoved(position);
		RosterEntry.saveData(cards);
	}

	public void saveData(int position) {
		RosterEntryCards c = cards.get(position);

	}

	public class ViewHolder extends RecyclerView.ViewHolder implements
			View.OnClickListener {
		protected TextView nameView;
		protected TextView idView;
		protected CardView cardView;
		View contextView;

		public ViewHolder(View view) {
			super(view);
			contextView = view;
			this.nameView = (TextView) view.findViewById(R.id.entry_name);
			this.idView = (TextView) view.findViewById(R.id.entry_id);
			this.cardView = (CardView) view.findViewById(R.id.show_entry);

			this.cardView.setOnClickListener(this);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onClick(View v) {
			try {
				delete(getPosition());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		
	}

}
