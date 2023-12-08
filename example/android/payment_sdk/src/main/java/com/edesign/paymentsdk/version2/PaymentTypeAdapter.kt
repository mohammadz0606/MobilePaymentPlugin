package com.edesign.paymentsdk.version2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edesign.paymentsdk.R

class PaymentTypeAdapter(
    private val context: Context,
    var arrayList: ArrayList<PaymentTypeModel>,
    var onClickPaymentMethod: OnClickPaymentMethod
) : RecyclerView.Adapter<PaymentTypeAdapter.MyViewHolder?>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.getContext()).inflate(R.layout.row_payment_type, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.textLbl.setText(arrayList[position].label)
      /*  if (arrayList[position].selected){
            holder.rlType.background=context.getDrawable(R.drawable.payment_type_selected_border)
            holder.imgCard.setImageResource(arrayList[position].selected_icon);
        }else{
            holder.imgCard.setImageResource(arrayList[position].deselected_icon);
            holder.rlType.background=null

        }*/
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var rlType:LinearLayout
        var imgCard:ImageView
        var textLbl:TextView

        init {
            rlType=view.findViewById(R.id.rlType)
            imgCard=view.findViewById(R.id.imgCard)
            textLbl=view.findViewById(R.id.textLbl)

            rlType.setOnClickListener {
                onClickPaymentMethod.onClickPaymentMethod(arrayList[adapterPosition].type)
            }
        }

    }

    public interface OnClickPaymentMethod{
        fun onClickPaymentMethod(type:String)
    }
}