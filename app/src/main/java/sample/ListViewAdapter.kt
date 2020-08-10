package sample

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService


class ListViewAdapter(applicationContext: Context, list: Int, scenes: ArrayList<String>) : BaseAdapter(){
    internal class ViewHolder {
        var textView: TextView? = null
    }

    private var inflater: LayoutInflater? = applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
    private var itemLayoutId = list
    private var titles: ArrayList<String> = scenes

//    fun ListViewAdapter(
//        context: Context, itemLayoutId: Int,
//        scenes: ArrayList<String>
//    ) {
//        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
//        this.itemLayoutId = itemLayoutId
//        titles = scenes
//    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var convertView: View? = convertView
        val holder: ViewHolder
        // 最初だけ View を inflate して、それを再利用する
        if (convertView == null) {
            // activity_main.xml に list.xml を inflate して convertView とする
            convertView = inflater!!.inflate(itemLayoutId, parent, false)
            // ViewHolder を生成
            holder = ViewHolder()
            holder.textView = convertView.findViewById(R.id.custList)
            convertView.setTag(holder)
        } else {
            holder = convertView.getTag() as ViewHolder
        }

        // 現在の position にあるファイル名リストを holder の textView にセット
        holder.textView!!.text = titles[position]
        return convertView
    }

    override fun getCount(): Int {
        // texts 配列の要素数
        return titles.size
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }
}