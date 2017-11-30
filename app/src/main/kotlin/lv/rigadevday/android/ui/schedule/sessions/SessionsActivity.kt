package lv.rigadevday.android.ui.schedule.sessions

import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import cz.eman.android.devfest.R
import kotlinx.android.synthetic.main.fragment_list.*
import lv.rigadevday.android.repository.model.schedule.Session
import lv.rigadevday.android.ui.base.BaseActivity
import lv.rigadevday.android.ui.openSessionDetailsActivity
import lv.rigadevday.android.ui.openSpeakerActivity
import lv.rigadevday.android.utils.BaseApp
import lv.rigadevday.android.utils.bindSchedulers
import lv.rigadevday.android.utils.showMessage

class SessionsActivity : BaseActivity() {

    override val layoutId = R.layout.fragment_list

    private val listAdapter: SessionsAdapter = SessionsAdapter(object : SessionContract {
        override fun openSession(id: Int) {
            openSessionDetailsActivity(id)
        }

        override fun openSpeaker(id: Int) {
            openSpeakerActivity(id)
        }
    })

    private lateinit var bookmarkedTag: String
    private var tags: Array<String>? = null
    private var cachedSessions: List<Session>? = null

    private val filterDialog: AlertDialog by lazy {
        AlertDialog.Builder(this@SessionsActivity)
                .setTitle(R.string.sessions_filter_title)
                .setItems(tags) { _, index ->
                    val tag = tags?.get(index) ?: bookmarkedTag

                    if (tag == bookmarkedTag) {
                        filterBookmarked()
                    } else {
                        filerByTag(tag)
                    }
                    supportActionBar?.title = tag

                }
                .setPositiveButton(R.string.sessions_filter_clear) { _, _ ->
                    cachedSessions?.let { listAdapter.data = it }
                    supportActionBar?.setTitle(R.string.sessions_title)
                }
                .create()
    }

    private fun filerByTag(tag: String) {
        cachedSessions?.let { listAdapter.data = it.filter { it.tags.contains(tag) } }
    }

    private fun filterBookmarked() {
        repo.bookmarkedIds()
                .bindSchedulers()
                .subscribe(
                        { bookmarkedIds ->
                            cachedSessions?.let { sessions ->
                                listAdapter.data = sessions.filter { bookmarkedIds.contains(it.id.toString()) }
                            }
                        },
                        { cachedSessions?.let { listAdapter.data = emptyList() } }
                )
    }

    override fun inject() {
        BaseApp.graph.inject(this)
    }

    override fun viewReady() {
        bookmarkedTag = getString(R.string.schedule_filter_bookmarked)

        setupActionBar(getString(R.string.sessions_title))
        homeAsUp()

        list_fragment_recycler.run {
            layoutManager = LinearLayoutManager(context)
            adapter = listAdapter
        }

        dataFetchSubscription = repo.sessions()
                .filter { it.speakers.isNotEmpty() }
                .toList()
                .subscribe(
                        { sessions ->
                            tags = prependBokmarked(sessions)
                            cachedSessions = sessions
                            listAdapter.data = sessions
                        },
                        { _ -> list_fragment_recycler.showMessage(R.string.error_message) }
                )
    }

    private fun prependBokmarked(sessions: List<Session>) =
            arrayOf(bookmarkedTag) + sessions.flatMap { it.tags }.distinct().toTypedArray()


    override fun onResume() {
        super.onResume()
        listAdapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_sessions, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_filter -> filterDialog.show()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

}

interface SessionContract {
    fun openSession(id: Int)
    fun openSpeaker(id: Int)
}
