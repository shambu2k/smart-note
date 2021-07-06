package com.example.smartnote.adapters

import android.app.Dialog
import android.graphics.BitmapFactory
import android.view.*
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartnote.MainActivity
import com.example.smartnote.R
import com.example.smartnote.databinding.PageItemBinding
import com.jsibbold.zoomage.ZoomageView
import com.squareup.picasso.Picasso
import java.io.File

class PagesAdapter(var listImages: MutableList<String>, private var activity: MainActivity, private val deleteListener: DeleteListener) :
  RecyclerView.Adapter<PagesAdapter.PagesViewHolder>(),
  androidx.appcompat.view.ActionMode.Callback {

  private var multiselect = false
  private var isSelectAll = false
  private val selectedItems = mutableListOf<String>()
  class PagesViewHolder(b: PageItemBinding) : RecyclerView.ViewHolder(b.root) {
    val binding = b
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagesViewHolder {
    return PagesViewHolder(
      PageItemBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
      )
    )
  }

  override fun onBindViewHolder(holder: PagesViewHolder, position: Int) {
    Picasso.get().load(File(listImages[position])).into(holder.binding.imageViewPage)
    if (isSelectAll)
      holder.binding.pageBg.alpha = 0.3f
    else
      holder.binding.pageBg.alpha = 1.0f
    holder.binding.pageBg.setOnLongClickListener {
      if (!multiselect) {
        multiselect = true
        activity.startSupportActionMode(this@PagesAdapter)
        selectItem(holder, listImages[position])
      }
      true
    }
    holder.binding.pageBg.setOnClickListener {
      if (multiselect)
        selectItem(holder, listImages[position])
      else{
        val pageDialog = Dialog(activity,android.R.style.Theme_Material_Light_NoActionBar_Fullscreen)
        pageDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        val view = activity.layoutInflater.inflate(R.layout.dialog_image_view,null)
        val bitmap = BitmapFactory.decodeFile(listImages[position])
        view.findViewById<ZoomageView>(R.id.page_image).setImageBitmap(bitmap)
        pageDialog.setContentView(view)
        pageDialog.show()
      }
    }
  }

  private fun selectItem(holder: PagesAdapter.PagesViewHolder, s: String) {
    if (selectedItems.contains(s)) {
      selectedItems.remove(s)
      holder.binding.pageBg.alpha = 1.0f
    } else {
      selectedItems.add(s)
      holder.binding.pageBg.alpha = 0.3f
    }
  }

  override fun getItemCount(): Int {
    return listImages.size
  }

  override fun onActionItemClicked(
    mode: androidx.appcompat.view.ActionMode?,
    item: MenuItem?
  ): Boolean {
    if (item?.itemId == R.id.page_delete) {
      deleteListener.deletePages(selectedItems)
      notifyDataSetChanged()
      mode?.finish()
    } else if (item?.itemId == R.id.select_all) {
      if (selectedItems.size == listImages.size) {
        isSelectAll = false
        selectedItems.clear()
      } else {
        isSelectAll = true
        selectedItems.clear()
        selectedItems.addAll(listImages)
      }
      notifyDataSetChanged()
    }
    return true
  }

  override fun onCreateActionMode(mode: androidx.appcompat.view.ActionMode?, menu: Menu?): Boolean {
    val inflater: MenuInflater? = mode?.menuInflater
    inflater?.inflate(R.menu.delete_menu, menu)
    return true
  }

  override fun onPrepareActionMode(
    mode: androidx.appcompat.view.ActionMode?,
    menu: Menu?
  ): Boolean {
    return true
  }

  override fun onDestroyActionMode(mode: androidx.appcompat.view.ActionMode?) {
    multiselect = false
    selectedItems.clear()
    notifyDataSetChanged()
  }

  interface DeleteListener {
    fun deletePages(selectedItems: MutableList<String>)
  }
}
