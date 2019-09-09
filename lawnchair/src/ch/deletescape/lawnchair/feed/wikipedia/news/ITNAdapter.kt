package ch.deletescape.lawnchair.feed.wikipedia.news

import android.text.Html
import android.view.ViewGroup
import ch.deletescape.lawnchair.inflate
import ch.deletescape.lawnchair.util.extensions.d
import com.android.launcher3.R
import com.android.launcher3.Utilities
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.itn_item.view.*

class ITNAdapter(val list: List<NewsItem>) : androidx.recyclerview.widget.RecyclerView.Adapter<ITNViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ITNViewHolder = ITNViewHolder(parent)
    override fun getItemCount() = list.size.also { d("getItemCount: there are $it items") }
    override fun onBindViewHolder(holder: ITNViewHolder, position: Int) = holder.initFromItem(list[position])
}

class ITNViewHolder(parent: ViewGroup) : androidx.recyclerview.widget.RecyclerView.ViewHolder(parent.inflate(R.layout.itn_item)) {
    fun initFromItem(newsItem: NewsItem) {
        itemView.itn_text.text = Html.fromHtml(newsItem.story, 0)
        Picasso.Builder(itemView.context).build()
                .load(newsItem.thumbnail)
                .into(itemView.itn_thumbnail)
        itemView.setOnTouchListener { v, ev ->
            itemView.parent.requestDisallowInterceptTouchEvent(true)
            false
        }
        itemView.setOnClickListener {
            Utilities.openURLinBrowser(itemView.context, newsItem.contentUrl)
        }
    }
}