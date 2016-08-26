package com.abhinav.qcards;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
	protected TextView nameView;
	protected TextView idView;
	protected TextView presentView;
	protected CardView cardView;
	
	public MyViewHolder(View view) {
		super(view);
		
		this.nameView = (TextView)view.findViewById(R.id.sName);
		this.idView = (TextView)view.findViewById(R.id.sId);
		//this.presentView = (TextView)view.findViewById(R.id.sPresent);
		this.cardView = (CardView)view.findViewById(R.id.card_view);
		this.cardView.setOnClickListener(this);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		presentView.setText("Works!"+getPosition());
		cardView.setBackgroundColor(Color.GREEN);
	}

}
