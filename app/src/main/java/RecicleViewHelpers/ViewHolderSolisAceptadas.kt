package RecicleViewHelpers

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.expogbss.R

class ViewHolderSolisAceptadas  (view: View): RecyclerView.ViewHolder(view) {

    var statusTextView3 = itemView.findViewById<TextView>(R.id.txtRequesStatusss)
    var jobTitleTextView3 = itemView.findViewById<TextView>(R.id.txJobTitleeee)
    var jobCategoryTextView3 = itemView.findViewById<TextView>(R.id.txJobCategoriaa)
}