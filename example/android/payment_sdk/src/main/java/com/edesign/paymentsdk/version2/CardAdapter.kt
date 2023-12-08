package com.edesign.paymentsdk.version2

import android.graphics.BitmapFactory
import android.graphics.drawable.GradientDrawable
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.edesign.paymentsdk.version2.savedCardAPI.SavedCardModel
import com.edesign.paymentsdk.R

class CardAdapter(
    private val mFragment: CardBottomSheetFragment,
    var arrayList: ArrayList<SavedCardModel>
) : RecyclerView.Adapter<CardAdapter.MyViewHolder?>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.getContext()).inflate(R.layout.row_card, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val rainbow: IntArray = mFragment.requireActivity().getResources().getIntArray(R.array.saved_card_color_gradient_top)
        val rainbow2: IntArray = mFragment.requireActivity().getResources().getIntArray(R.array.saved_card_color_gradient_bottom)

       var gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(
                rainbow[position%rainbow.size],
                rainbow2[position%rainbow2.size]
            )
        )
        gradientDrawable.cornerRadius = 0f
        holder.textCardHolderName.setText(arrayList[position].cardHolderName)
        holder.textCardNumber.setText(arrayList[position].cardNumber)
        holder.textExpiry.setText(
            arrayList[position].cardExpDate.substring(
                2,
                arrayList[position].cardExpDate.length
            ) + "/" + arrayList[position].cardExpDate.substring(0, 2)
        )
        holder.clCard.setBackground(gradientDrawable);
        try {
            val bytes: ByteArray =
                Base64.decode(arrayList[position].cardLogo.replace(" ", "+"), Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            holder.imgType.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var clCard: ConstraintLayout
        var textCardHolderName: TextView
        var textCardNumber: TextView
        var textExpiry: TextView
        var imgType: ImageView
        var imgDelete: ImageView

        init {
            clCard = view.findViewById(R.id.clCard)
            textCardHolderName = view.findViewById(R.id.textCardHolderName)
            textCardNumber = view.findViewById(R.id.textCardNumber)
            textExpiry = view.findViewById(R.id.textExpiry)
            imgType = view.findViewById(R.id.imgType)
            imgDelete = view.findViewById(R.id.imgDelete)

            clCard.setOnClickListener {
                CVVBottomSheetFragment.openFragment(object : AnotherCardInterface {
                    override fun onCardSelected(model: SavedCardModel) {
                        mFragment.param.onCardSelected(model)
                        mFragment.dismiss()

                    }

                    override fun onDeleteCard(token: String, deleted: Boolean) {

                    }

                    override fun onClickAnotherCard() {
                        mFragment.dismiss()
                    }
                }, arrayList[adapterPosition])
                    .show(mFragment.requireActivity().supportFragmentManager, "")


            }

            imgDelete.setOnClickListener {
                mFragment.param.onDeleteCard(arrayList.get(adapterPosition).token, true)
                arrayList.removeAt(adapterPosition)
                mFragment.dismiss()
            }
        }
    }
}