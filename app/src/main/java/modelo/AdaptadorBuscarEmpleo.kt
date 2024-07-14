package modelo

class AdaptadorBuscarEmpleo (private var itemList: List<String>)// : RecyclerView.Adapter<AdaptadorBuscarEmpleo.ViewHolder>() {

//    private var filteredItemList: List<String> = itemList
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//
//    }
//
//    override fun getItemCount(): Int {
//        return filteredItemList.size
//    }
//
//    fun filter(query: String) {
//        filteredItemList = if (query.isEmpty()) {
//            itemList
//        } else {
//            itemList.filter { it.contains(query, true) }
//        }
//        notifyDataSetChanged()
//    }
//
//    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//
//    }
//}
