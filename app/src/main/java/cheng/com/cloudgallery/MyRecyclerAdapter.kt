package cheng.com.cloudgallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyRecyclerAdapter(pics: List<Picture>): RecyclerView.Adapter<MyRecyclerAdapter.PictureViewHolder>() {
    var pictures = pics
    var clickListener: MyClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureViewHolder {
        val view =  LayoutInflater.from(parent.context).inflate(R.layout.picture_item, parent, false)
        return PictureViewHolder(view)
    }

    override fun getItemCount(): Int {
        return pictures.size
    }

    override fun onBindViewHolder(holder: PictureViewHolder, position: Int) {
        if (pictures[position].drawableId != null) {
            holder.picture!!.setImageResource(pictures[position].drawableId!!)
        } else {
            holder.picture!!.setImageBitmap(pictures[position].bitmap)
        }
        holder.uploadTime!!.text = pictures[position].uploadTime
        holder.description!!.text = pictures[position].description
    }

    inner class PictureViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnLongClickListener, View.OnClickListener {
        var picture: ImageView? = null
        var uploadTime: TextView? =  null
        var description: TextView? = null

        init {
            picture = view.findViewById(R.id.picture)!!
            uploadTime = view.findViewById(R.id.uploadTime)!!
            description = view.findViewById(R.id.description)!!
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
        }

        override fun onLongClick(v: View?): Boolean {
            clickListener!!.onLongItemClicked(adapterPosition, v!!)
            return false
        }

        override fun onClick(v: View?) {
            clickListener!!.onItemClicked(adapterPosition, v!!)
        }
    }

    fun setOnItemClickListener(mcl: MyClickListener) {
        clickListener = mcl
    }

    interface MyClickListener {
        fun onItemClicked(position: Int, v: View)
        fun onLongItemClicked(position: Int, v: View)
    }
}