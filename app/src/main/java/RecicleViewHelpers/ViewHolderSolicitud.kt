package RecicleViewHelpers

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.expogbss.R
import com.google.android.material.button.MaterialButton

class ViewHolderSolicitud (view: View): RecyclerView.ViewHolder(view) {

    var statusTextView = itemView.findViewById<TextView>(R.id.txtRequestStatus)
    var jobTitleTextView = itemView.findViewById<TextView>(R.id.tvJobTitle)
    var jobCategoryTextView = itemView.findViewById<TextView>(R.id.tvJobCategory)
    var acceptButton = itemView.findViewById<MaterialButton>(R.id.btnAceptar)
    var rejectButton = itemView.findViewById<MaterialButton>(R.id.btnRechazar)
}