package com.abhinav.qcards;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class RosterEntryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
	protected TextView nameView;
	protected TextView idView;
	protected CardView cardView;
	
	public RosterEntryViewHolder(View view) {
		super(view);
		
		this.nameView = (TextView)view.findViewById(R.id.entry_name);
		this.idView = (TextView)view.findViewById(R.id.entry_id);
		this.cardView = (CardView)view.findViewById(R.id.show_entry);
		this.cardView.setOnClickListener(this);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		nameView.setText("Works!"+getPosition());
		cardView.setBackgroundColor(Color.GREEN);
	}

}
