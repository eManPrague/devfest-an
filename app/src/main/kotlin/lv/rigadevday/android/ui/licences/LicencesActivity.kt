package lv.rigadevday.android.ui.licences

import cz.eman.android.devfest.R
import kotlinx.android.synthetic.main.activity_licences.*
import lv.rigadevday.android.ui.base.BaseActivity
import lv.rigadevday.android.utils.BaseApp

/**
 */
class LicencesActivity : BaseActivity() {

    override val layoutId = R.layout.activity_licences

    override fun inject() = BaseApp.graph.inject(this)

    override fun viewReady() {
        BaseApp.graph.analytics().aboutOpened()
        setupActionBar(R.string.licenses_title)
        homeAsUp()
        licences_webview.loadUrl("file:///android_asset/licenses.html")
    }

}
