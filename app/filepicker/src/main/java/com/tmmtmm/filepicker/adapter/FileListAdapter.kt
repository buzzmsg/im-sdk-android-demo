package com.tmmtmm.filepicker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.android.filepicker.FilePickerActivity

import com.android.filepicker.bean.FileItemBeanImpl
import com.android.filepicker.config.FilePickerManager.config
import com.tmmtmm.filepicker.R
import java.io.File

class FileListAdapter(
    private val activity: FilePickerActivity,
    var dataList: ArrayList<FileItemBeanImpl>?,
    private var isSingleChoice: Boolean = config.singleChoice
) : BaseAdapter() {
    private var latestChoicePos = -1
    private lateinit var recyclerView: RecyclerView

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (parent is RecyclerView) {
            recyclerView = parent
        }
        return when (isSingleChoice) {
            true -> {
                FileListItemSingleChoiceHolder(
                    LayoutInflater.from(activity).inflate(
                        R.layout.item_single_choise_list_file_picker,
                        parent,
                        false
                    )
                )
            }
            else -> {
                FileListItemHolder(
                    LayoutInflater.from(activity).inflate(
                        R.layout.item_list_file_picker,
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun getItemView(position: Int): View? {
        return recyclerView.findViewHolderForAdapterPosition(position)?.itemView
    }

    override fun getItemCount(): Int {
        return dataList?.size ?: 10
    }

    override fun getItemViewType(position: Int): Int {
        return DEFAULT_FILE_TYPE
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as BaseViewHolder).bind(dataList!![position], position)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        // Using payload to refresh partly
        if (payloads.isNullOrEmpty()) {
            onBindViewHolder(holder, position)
            return
        }
        when (holder) {
            is FileListItemHolder -> {
                holder.itemView.findViewById<CheckBox>(R.id.cb_list_file_picker)?.let {
                    it.isChecked = payloads[0] as Boolean
                }
            }
            is FileListItemSingleChoiceHolder -> {
                holder.itemView.findViewById<RadioButton>(R.id.rb_list_file_picker)?.let {
                    it.isChecked = payloads[0] as Boolean
                }
            }
        }
    }


    override fun getItem(position: Int): FileItemBeanImpl? {
        if (position >= 0 &&
            position < dataList!!.size &&
            getItemViewType(position) == DEFAULT_FILE_TYPE
        ) return dataList!![position]
        return null
    }

    /*--------------------------OutSide call method begin------------------------------*/
    inline fun multipleCheckOrNo(
        item: FileItemBeanImpl,
        position: Int,
        isCanSelect: () -> Boolean,
        checkFailedFunc: () -> Unit
    ) {
        when {
            item.isChecked() -> {
                // had selected, will dis-select
                multipleDisCheck(position)
            }
            isCanSelect() -> {
                // current item is not selected, and can be selected, will select
                multipleCheck(position)
            }
            else -> {
                // add new selected item failed
                checkFailedFunc()
            }
        }
    }

    fun multipleCheck(position: Int) {
        getItem(position)?.let {
            it.setCheck(true)
            notifyItemChanged(position, true)
        }
    }


    fun multipleDisCheck(position: Int) {
        getItem(position)?.let {
            it.setCheck(false)
            notifyItemChanged(position, false)
        }
    }


    fun singleCheck(position: Int) {
        when (latestChoicePos) {
            -1 -> {
                getItem(position)?.let {
                    it.setCheck(true)
                    notifyItemChanged(position, true)
                }
                latestChoicePos = position
            }
            position -> {
                getItem(latestChoicePos)?.let {
                    it.setCheck(false)
                    notifyItemChanged(latestChoicePos, false)
                }
                latestChoicePos = -1
            }
            else -> {
                // disCheck the old one
                getItem(latestChoicePos)?.let {
                    it.setCheck(false)
                    notifyItemChanged(latestChoicePos, false)
                }
                // check the new one
                latestChoicePos = position
                getItem(latestChoicePos)?.let {
                    it.setCheck(true)
                    notifyItemChanged(latestChoicePos, true)
                }
            }
        }
    }

    fun disCheckAll() {
        dataList
            ?.forEachIndexed { index, item ->
                if (!(config.isSkipDir && item.isDir) && item.isChecked()) {
                    item.setCheck(false)
                    notifyItemChanged(index, false)
                }
            }
    }

    fun checkAll(hadSelectedCount: Int) {
        var checkCount = hadSelectedCount
        dataList
            ?.forEachIndexed { index, item ->
                if (checkCount >= config.maxSelectable) {
                    return
                }
                if (!(config.isSkipDir && item.isDir) && !item.isChecked()) {
                    item.setCheck(true)
                    notifyItemChanged(index, true)
                    checkCount++
                }
            }
    }


    /*--------------------------OutSide call method end------------------------------*/

    /*--------------------------ViewHolder Begin------------------------------*/

    abstract inner class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(itemImpl: FileItemBeanImpl, position: Int)
    }

    inner class FileListItemSingleChoiceHolder(itemView: View) :
        BaseViewHolder(itemView) {

        private val isSkipDir: Boolean = config.isSkipDir
        private val mTvFileName = itemView.findViewById<TextView>(R.id.tv_list_file_picker)!!
        private val mRbItem = itemView.findViewById<RadioButton>(R.id.rb_list_file_picker)!!
        private val mIcon = itemView.findViewById<ImageView>(R.id.iv_icon_list_file_picker)!!
        private var mItemBeanImpl: FileItemBeanImpl? = null
        private var mPosition: Int? = null


        override fun bind(itemImpl: FileItemBeanImpl, position: Int) {
            mItemBeanImpl = itemImpl
            mPosition = position

            mTvFileName.text = itemImpl.fileName
            mRbItem.isChecked = itemImpl.isChecked()
            mRbItem.visibility = View.VISIBLE

            val isDir = File(itemImpl.filePath).isDirectory

            if (isDir) {
                mIcon.setImageResource(R.drawable.ic_folder_file_picker)
                mRbItem.visibility = if (isSkipDir) View.GONE else View.VISIBLE
                return
            }

            val resId: Int = itemImpl.fileType?.fileIconResId ?: R.drawable.ic_unknown_file_picker
            mIcon.setImageResource(resId)
        }

    }

    inner class FileListItemHolder(itemView: View) :
        BaseViewHolder(itemView) {

        private val isSkipDir: Boolean = config.isSkipDir
        private val mTvFileName = itemView.findViewById<TextView>(R.id.tv_list_file_picker)!!
        private val mCbItem = itemView.findViewById<CheckBox>(R.id.cb_list_file_picker)!!
        private val mIcon = itemView.findViewById<ImageView>(R.id.iv_icon_list_file_picker)!!
        private var mItemBeanImpl: FileItemBeanImpl? = null
        private var mPosition: Int? = null


        override fun bind(itemImpl: FileItemBeanImpl, position: Int) {
            mItemBeanImpl = itemImpl
            mPosition = position

            mTvFileName.text = itemImpl.fileName
            mCbItem.isChecked = itemImpl.isChecked()
            mCbItem.visibility = View.VISIBLE

            val isDir = File(itemImpl.filePath).isDirectory

            if (isDir) {
                mIcon.setImageResource(R.drawable.ic_folder_file_picker)
                mCbItem.visibility = if (isSkipDir) View.GONE else View.VISIBLE
                return
            }

            val resId: Int = itemImpl.fileType?.fileIconResId ?: R.drawable.ic_unknown_file_picker
            mIcon.setImageResource(resId)
        }

    }

    /*--------------------------ViewHolder End------------------------------*/

    companion object {
        const val DEFAULT_FILE_TYPE = 10001
    }
}