package lv.rigadevday.android.ui.base

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.part_toolbar.*
import lv.rigadevday.android.repository.Repository
import lv.rigadevday.android.ui.tabs.TabActivity
import javax.inject.Inject

abstract class BaseFragment : Fragment() {

    @Inject lateinit var repo: Repository

    abstract val layoutId: Int

    abstract fun inject()

    abstract fun viewReady(view: View)

    open val ignoreUiUpdates: Boolean = false

    protected var dataFetchSubscription: Disposable? = null
    protected var uiUpdateSubscription: Disposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
        = inflater.inflate(layoutId, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inject()
        viewReady(view)
    }

    override fun onResume() {
        super.onResume()

        if (!ignoreUiUpdates) {
            uiUpdateSubscription = repo.cacheUpdated
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.let { viewReady(it) } }
        }
    }

    override fun onPause() {
        uiUpdateSubscription?.takeUnless { it.isDisposed }?.dispose()
        super.onPause()
    }

    override fun onStop() {
        dataFetchSubscription?.dispose()
        super.onStop()
    }

    fun setupActionBar(@StringRes title: Int) {
        setupActionBar(getString(title))
    }

    fun setupActionBar(title: String) {
        (activity as TabActivity).let {
            it.setSupportActionBar(toolbar)
            it.supportActionBar?.title = title
        }
    }
}
