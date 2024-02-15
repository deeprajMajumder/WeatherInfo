import android.graphics.drawable.Drawable
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.deepraj.weatherinfo.utils.BindableAdapter
import com.deepraj.weatherinfo.utils.DividerItemDecorator

/**
 * Holds all custom binding adapters that bind to [RecyclerView].
 **/
class RecyclerViewBindingAdapter {

  companion object {
    @JvmStatic
    @BindingAdapter("data")
    fun <T : Any> bindDataList(recyclerView: RecyclerView, liveData: LiveData<List<T>>) {
      val adapter = recyclerView.adapter as BindableAdapter<T>
      liveData.value?.let { list ->
        adapter.setData(list)
      }
    }

    @JvmStatic
    @BindingAdapter("itemDecorator")
    fun addItemDecorator(
      recyclerView: RecyclerView,
      drawable: Drawable
    ) {
      recyclerView.addItemDecoration(DividerItemDecorator(drawable))
    }
  }
}
