package RecicleViewHelpers

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.expogbss.R

class ViewHolderMSolicitud (view: View): RecyclerView.ViewHolder(view) {

    var statusTextView1 = itemView.findViewById<TextView>(R.id.txtStatus)
    var jobTitleTextView2 = itemView.findViewById<TextView>(R.id.tvTitle)
    var jobCategoryTextView2 = itemView.findViewById<TextView>(R.id.tvCategory)
}