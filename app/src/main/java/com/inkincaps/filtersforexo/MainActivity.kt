package com.inkincaps.filtersforexo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daasuu.epf.EPlayerView
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.inkincaps.filtersforexo.databinding.RvFilterLayoutBinding


const val REQUEST_CODE_GALLERY_FILES = 10

class RVFilterActivity : AppCompatActivity() {
    val binding: RvFilterLayoutBinding by lazy {
        RvFilterLayoutBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
//        setContentView(R.layout.rv_filter_layout)
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
                Intent.createChooser(intent, "Select Video"),
                REQUEST_CODE_GALLERY_FILES
        )
    }

    lateinit var uri: Uri
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_GALLERY_FILES) {
                uri = data!!.data!!
                proceed()
            }
        }
    }

    private fun proceed(){
//        binding.mySimpleExoPlayer.playWhenReady = true
//        exoPlayer.prepare(source)
        val x = SimpleExoPlayer.Builder(this).build().apply {
            val uri = setMediaItem(MediaItem.fromUri(uri))
            prepare()
            playWhenReady = true
        }
        binding.mySimpleExoPlayer.player = x
        val ePlayerView =  EPlayerView(this);
        ePlayerView.setSimpleExoPlayer(binding.mySimpleExoPlayer.player as SimpleExoPlayer);
        ePlayerView.layoutParams =
                ConstraintLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        binding.root.addView(ePlayerView)
        ePlayerView.onResume()
        binding.rv.layoutManager = GridLayoutManager(this, 3)
        binding.rv.adapter = FilterRVAdapter(FilterType.createFilterList(), ePlayerView)
    }
}

class FilterRVAdapter(val filter: List<FilterType>, val ePlayerView: EPlayerView):RecyclerView.Adapter<FilterRVAdapter.ViewHolder>(){
    inner class ViewHolder(v: View):RecyclerView.ViewHolder(v){
        val imageView = v.findViewById<ImageView>(R.id.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.filter_view_holder, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageView.setOnClickListener {
            ePlayerView.setGlFilter(FilterType.createGlFilter(filter[position], ePlayerView.context))
        }
    }

    override fun getItemCount(): Int {
        return filter.size
    }
}