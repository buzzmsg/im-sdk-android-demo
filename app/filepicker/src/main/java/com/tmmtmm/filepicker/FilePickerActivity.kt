package com.android.filepicker

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.os.Environment.MEDIA_MOUNTED
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.filepicker.adapter.FileNavAdapter
import com.android.filepicker.adapter.RecyclerViewListener
import com.tmmtmm.filepicker.bean.BeanSubscriber
import com.android.filepicker.bean.FileBean
import com.android.filepicker.bean.FileItemBeanImpl
import com.android.filepicker.bean.FileNavBeanImpl
import com.android.filepicker.config.FilePickerManager
import com.tmmtmm.filepicker.utils.FileUtils
import com.android.filepicker.widget.PosLinearLayoutManager
import com.tmmtmm.filepicker.R
import com.tmmtmm.filepicker.adapter.BaseAdapter
import com.tmmtmm.filepicker.adapter.FileListAdapter
import com.tmmtmm.filepicker.databinding.MainActivityForFilePickerBinding
import com.tmmtmm.filepicker.utils.bindView
import java.io.File


@SuppressLint("ShowToast")
class FilePickerActivity : AppCompatActivity(),
    View.OnClickListener,
    RecyclerViewListener.OnItemClickListener,
    BeanSubscriber {

    private var mainHandler = Handler(Looper.getMainLooper())

    private var loadFileThread: Thread? = null

    private val loadFileRunnable: Runnable by lazy {
        Runnable {
            val rootFile = if (navDataSource.isEmpty()) {
                FileUtils.getRootFile()
            } else {
                File(navDataSource.last().dirPath)
            }
            val listData = FileUtils.produceListDataSource(rootFile, this@FilePickerActivity)
            navDataSource = FileUtils.produceNavDataSource(
                navDataSource,
                if (navDataSource.isEmpty()) {
                    rootFile.path
                } else {
                    navDataSource.last().dirPath
                },
                this@FilePickerActivity
            )
            mainHandler.post {
                initRv(listData, navDataSource)
            }
        }
    }


    private var listAdapter: FileListAdapter? = null

    private var navAdapter: FileNavAdapter? = null

    private var navDataSource = ArrayList<FileNavBeanImpl>()

    private var selectedCount: Int = 0
    private val maxSelectable = FilePickerManager.config.maxSelectable
    private val pickerConfig by lazy { FilePickerManager.config }
    private val fileListListener: RecyclerViewListener by lazy { getListener(mBinding.rvListFilePicker) }
    private val navListener: RecyclerViewListener by lazy { getListener(mBinding.rvNavFilePicker) }


    private lateinit var mBinding: MainActivityForFilePickerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(pickerConfig.themeId)
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.main_activity_for_file_picker)
        mBinding = MainActivityForFilePickerBinding.inflate(layoutInflater).bindView(this)
        // checking permission
        if (isPermissionGrated()) {
            prepareLauncher()
        } else {
            requestPermission()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (loadFileThread?.isAlive == true) {
            loadFileThread?.interrupt()
        }
    }

    private fun isPermissionGrated() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED


    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this@FilePickerActivity,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            FILE_PICKER_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            FILE_PICKER_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(
//                        this@FilePickerActivity.applicationContext,
//                        getString(R.string.file_picker_request_permission_failed),
//                        Toast.LENGTH_SHORT
//                    ).show()
                } else {
                    prepareLauncher()
                }
            }
        }
    }


    private fun prepareLauncher() {
        if (Environment.getExternalStorageState() != MEDIA_MOUNTED) {
            throw Throwable(cause = IllegalStateException("External storage is not available ====>>> Environment.getExternalStorageState() != MEDIA_MOUNTED"))
        }
        initView()
        loadList()
    }

    private fun initView() {
        mBinding.btnGoBackFilePicker.setOnClickListener(this@FilePickerActivity)

        mBinding.btnSelectedAllFilePicker.apply {
            if (pickerConfig.singleChoice) {
                visibility = View.GONE
                return@apply
            }
            setOnClickListener(this@FilePickerActivity)
            FilePickerManager.config.selectAllText.let {
                text = it
            }
        }
        mBinding.btnConfirmFilePicker.apply {
            setOnClickListener(this@FilePickerActivity)
            FilePickerManager.config.confirmText.let {
                text = it
            }
        }

        mBinding.tvToolbarTitleFilePicker.visibility = if (pickerConfig.singleChoice) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun loadList() {
        if (loadFileThread?.isAlive == true) {
            loadFileThread?.interrupt()
        }
        loadFileThread = Thread(loadFileRunnable)
        loadFileThread?.start()
    }

    private fun initRv(
        listData: ArrayList<FileItemBeanImpl>?,
        navDataList: ArrayList<FileNavBeanImpl>
    ) {
        listData?.let { switchButton(true) }
        mBinding.rvNavFilePicker?.apply {
            navAdapter = produceNavAdapter(navDataList)
            adapter = navAdapter
            layoutManager =
                LinearLayoutManager(this@FilePickerActivity, LinearLayoutManager.HORIZONTAL, false)
            removeOnItemTouchListener(navListener)
            addOnItemTouchListener(navListener)
        }

        listAdapter = produceListAdapter(listData)
        mBinding.rvListFilePicker?.apply {
            emptyView = LayoutInflater.from(context)
                .inflate(R.layout.empty_file_list_file_picker, null).apply {
                    this.findViewById<TextView>(R.id.tv_empty_list).text = pickerConfig.emptyListTips
                }
            setHasFixedSize(true)
            adapter = listAdapter
            layoutAnimation =
                AnimationUtils.loadLayoutAnimation(
                    context,
                    R.anim.layout_item_anim_file_picker
                )
            layoutManager = PosLinearLayoutManager(this@FilePickerActivity)
            removeOnItemTouchListener(fileListListener)
            addOnItemTouchListener(fileListListener)
        }
    }


    private fun getListener(recyclerView: RecyclerView): RecyclerViewListener {
        return RecyclerViewListener(this@FilePickerActivity, recyclerView, this@FilePickerActivity)
    }


    private fun produceListAdapter(dataSource: ArrayList<FileItemBeanImpl>?): FileListAdapter {
        return FileListAdapter(
            this@FilePickerActivity,
            dataSource,
            FilePickerManager.config.singleChoice
        )
    }


    private fun produceNavAdapter(dataSource: ArrayList<FileNavBeanImpl>): FileNavAdapter {
        return FileNavAdapter(this@FilePickerActivity, dataSource)
    }

    private val currPosMap: HashMap<String, Int> by lazy {
        HashMap<String, Int>(4)
    }
    private val currOffsetMap: HashMap<String, Int> by lazy {
        HashMap<String, Int>(4)
    }


    private fun saveCurrPos(item: FileNavBeanImpl?, position: Int) {
        item?.run {
            currPosMap[filePath] = position
            (mBinding.rvListFilePicker?.layoutManager as? LinearLayoutManager)?.let {
                currOffsetMap.put(filePath, it.findViewByPosition(position)?.top ?: 0)
            }
        }
    }

    /*--------------------------Item click listener begin------------------------------*/


    override fun onItemClick(
        adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
        view: View,
        position: Int
    ) {
        val item = (adapter as BaseAdapter).getItem(position)
        item ?: return
        val file = File(item.filePath)
        if (!file.exists()) {
            return
        }
        when (view.id) {
            R.id.item_list_file_picker -> {
                if (file.isDirectory) {
                    (mBinding.rvNavFilePicker?.adapter as? FileNavAdapter)?.let {
                        saveCurrPos(it.data.last(), position)
                    }
                    enterDirAndUpdateUI(item)
                } else {
                    FilePickerManager.config.fileItemOnClickListener?.onItemClick(
                        adapter as FileListAdapter,
                        view,
                        position
                    )
                }
            }
            R.id.item_nav_file_picker -> {
                if (file.isDirectory) {
                    (mBinding.rvNavFilePicker?.adapter as? FileNavAdapter)?.let {
                        saveCurrPos(it.data.last(), position)
                    }
                    enterDirAndUpdateUI(item)
                }
            }
        }
    }


    override fun onItemChildClick(
        adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
        view: View,
        position: Int
    ) {
        when (view.id) {
            R.id.tv_btn_nav_file_picker -> {
                val item = (adapter as FileNavAdapter).getItem(position)
                item ?: return
                enterDirAndUpdateUI(item)
            }
            else -> {
                val item = (adapter as FileListAdapter).getItem(position) ?: return
                // if it's Dir, enter directly
                if (item.isDir && pickerConfig.isSkipDir) {
                    enterDirAndUpdateUI(item)
                    return
                }
                if (pickerConfig.singleChoice) {
                    listAdapter?.singleCheck(position)
                } else {
                    listAdapter?.multipleCheckOrNo(item, position, ::isCanSelect) {
//                        Toast.makeText(
//                            this@FilePickerActivity.applicationContext,
//                            getString(R.string.max_select_count_tips, maxSelectable),
//                            Toast.LENGTH_SHORT
//                        ).show()
                    }
                }
            }
        }
    }


    override fun onItemLongClick(
        adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
        view: View,
        position: Int
    ) {
        if (view.id != R.id.item_list_file_picker) return
        val item = (adapter as FileListAdapter).getItem(position)
        item ?: return
        val file = File(item.filePath)
        val isSkipDir = FilePickerManager.config.isSkipDir
        if (file.exists() && file.isDirectory && isSkipDir) return
        // same action like child click
        onItemChildClick(adapter, view, position)
        // notify listener
        FilePickerManager.config.fileItemOnClickListener?.onItemLongClick(adapter, view, position)
    }

    /*--------------------------Item click listener end------------------------------*/

    private fun enterDirAndUpdateUI(fileBean: FileBean) {
        cleanStatus()

        val nextFiles = File(fileBean.filePath)

        listAdapter?.dataList =
            FileUtils.produceListDataSource(nextFiles, this@FilePickerActivity)

        navDataSource = FileUtils.produceNavDataSource(
            ArrayList(navAdapter!!.data),
            fileBean.filePath,
            this@FilePickerActivity
        )
        navAdapter?.data = navDataSource

        navAdapter!!.notifyDataSetChanged()
        notifyDataChangedForList(fileBean)

        mBinding.rvNavFilePicker?.adapter?.itemCount?.let {
            mBinding.rvNavFilePicker?.smoothScrollToPosition(
                if (it == 0) {
                    0
                } else {
                    it - 1
                }
            )
        }
    }

    private fun notifyDataChangedForList(fileBean: FileBean) {
        mBinding.rvListFilePicker?.apply {
            (layoutManager as? PosLinearLayoutManager)?.setTargetPos(
                currPosMap[fileBean.filePath] ?: 0,
                currOffsetMap[fileBean.filePath] ?: 0
            )
            layoutAnimation =
                AnimationUtils.loadLayoutAnimation(
                    context,
                    R.anim.layout_item_anim_file_picker
                )
            adapter?.notifyDataSetChanged()
            scheduleLayoutAnimation()
        }
    }


    private fun switchButton(isEnable: Boolean) {
        mBinding.btnConfirmFilePicker?.isEnabled = isEnable
        mBinding.btnSelectedAllFilePicker?.isEnabled = isEnable
    }

    private fun cleanStatus() {
        selectedCount = 1
        updateItemUI(false)
    }

    override fun updateItemUI(isCheck: Boolean) {
        if (isCheck) {
            selectedCount++
        } else {
            selectedCount--
        }
        if (pickerConfig.singleChoice) {
            return
        }
        if (selectedCount == 0) {
            mBinding.btnSelectedAllFilePicker.text = pickerConfig.selectAllText
            mBinding.tvToolbarTitleFilePicker.text = ""
            return
        }
        mBinding.btnSelectedAllFilePicker.text = pickerConfig.deSelectAllText
        mBinding.tvToolbarTitleFilePicker.text =
            resources.getString(pickerConfig.hadSelectedText, selectedCount)
    }

    override fun onBackPressed() {
        if ((mBinding.rvNavFilePicker?.adapter as? FileNavAdapter)?.itemCount ?: 0 <= 1) {
            super.onBackPressed()
        } else {
            (mBinding.rvNavFilePicker?.adapter as? FileNavAdapter)?.run {
                enterDirAndUpdateUI(getItem(this.itemCount - 2)!!)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_selected_all_file_picker -> {
                if (selectedCount > 0) {
                    listAdapter?.disCheckAll()
                } else if (isCanSelect()) {
                    listAdapter?.checkAll(selectedCount)
                }
            }

            R.id.btn_confirm_file_picker -> {
                val list = ArrayList<String>()
                val intent = Intent()

                for (data in listAdapter!!.dataList!!) {
                    if (data.isChecked()) {
                        list.add(data.filePath)
                    }
                }

                if (list.isEmpty()) {
                    this@FilePickerActivity.setResult(Activity.RESULT_CANCELED, intent)
                    finish()
                    return
                }

                FilePickerManager.saveData(list)
                this@FilePickerActivity.setResult(Activity.RESULT_OK, intent)
                finish()
            }
            R.id.btn_go_back_file_picker -> {
                finish()
            }
        }
    }

    private fun getAvailableCount(): Long {
        var count: Long = 0
        for (item in listAdapter!!.dataList!!) {
            val file = File(item.filePath)
            if (pickerConfig.isSkipDir && file.exists() && file.isDirectory) {
                continue
            }
            count++
        }
        return count
    }

    private fun isCanSelect() = selectedCount < maxSelectable

    companion object {
        private const val FILE_PICKER_PERMISSION_REQUEST_CODE = 10201
    }
}
