package com.edesign.paymentsdk.version2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.edesign.paymentsdk.R

class SupportedCardTypeAdapter(
    var arrayList:ArrayList<String>
) : RecyclerView.Adapter<SupportedCardTypeAdapter.MyViewHolder?>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.getContext()).inflate(R.layout.row_supported_card_type, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (arrayList[position].equals("VISA",true)){
            holder.imgCard.setImageResource(R.drawable.visa)
        } else if (arrayList[position].equals("MASTERCARD",true)){
            holder.imgCard.setImageResource(R.drawable.master_card)
        }else if (arrayList[position].equals("AMEX",true)){
            holder.imgCard.setImageResource(R.drawable.amex)
        }else if (arrayList[position].equals("DINERS",true)){
            holder.imgCard.setImageResource(R.drawable.diners)
        }else if (arrayList[position].equals("UNION",true)){
            holder.imgCard.setImageResource(R.drawable.union)
        }else if (arrayList[position].equals("JCB",true)){
            holder.imgCard.setImageResource(R.drawable.jcb)
        }else if (arrayList[position].equals("DISCOVER",true)){
            holder.imgCard.setImageResource(R.drawable.discover)
        }else if (arrayList[position].equals("MADA",true)){
            holder.imgCard.setImageResource(R.drawable.mada)
        }

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imgCard:ImageView

        init {
            imgCard=view.findViewById(R.id.imgCard)
        }

    }


}