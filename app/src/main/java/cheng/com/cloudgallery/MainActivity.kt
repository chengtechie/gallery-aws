package cheng.com.cloudgallery

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private var pictures = ArrayList<Picture>()
    private var bundle = Bundle()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        uploadBtn.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()

        }

        setDummyPics()

        recyclerView = findViewById(R.id.picturesRecyclerView)
    }

    override fun onStart() {
        super.onStart()

        val viewAdapter = MyRecyclerAdapter(pictures)

        viewAdapter.setOnItemClickListener(object: MyRecyclerAdapter.MyClickListener {
            override fun onItemClicked(position: Int, v: View) {
//                Toast.makeText(this@MainActivity, "Position is $position", Toast.LENGTH_SHORT).show() // testing
            }

            override fun onLongItemClicked(position: Int, v: View) {
//                Toast.makeText(this@MainActivity, "Position (long) is $position", Toast.LENGTH_SHORT).show() // testing
                bundle.putInt("position", position + 1) // pass data to fragment using intent
                val fileActionFragment = FileActionFragment()
                fileActionFragment.arguments = bundle
                supportFragmentManager.beginTransaction() // show the most recent fragment with the correct data
                    .replace(R.id.fragmentContainer, fileActionFragment).commit()
                fragmentContainer.visibility = View.VISIBLE
            }
        })

        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = viewAdapter
        }
    }

    fun onCancelClicked(view: View) {
        fragmentContainer.visibility = View.GONE
    }

    fun onDeleteClicked(view: View) {
        val deleteActionFragment = DeleteActionFragment()
        deleteActionFragment.arguments = bundle
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, deleteActionFragment).commit()
    }

    private fun setDummyPics() { // test recycler view
        pictures.add(Picture(R.drawable.upload, "1:00pm", "This is picture 1"))
        pictures.add(Picture(R.drawable.upload, "2:30pm", "This is picture 2"))
        pictures.add(Picture(R.drawable.upload, "3:45pm", "This is picture 3"))
        pictures.add(Picture(R.drawable.upload, "1:00pm", "This is picture 1"))
        pictures.add(Picture(R.drawable.upload, "2:30pm", "This is picture 2"))
        pictures.add(Picture(R.drawable.upload, "3:45pm", "This is picture 3"))
        pictures.add(Picture(R.drawable.upload, "1:00pm", "This is picture 1"))
        pictures.add(Picture(R.drawable.upload, "2:30pm", "This is picture 2"))
        pictures.add(Picture(R.drawable.upload, "3:45pm", "This is picture 3"))
    }

}
