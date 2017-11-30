package cz.eman.android.devfest.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.data.kml.KmlLayer
import cz.eman.android.devfest.*
import lv.rigadevday.android.ui.base.BaseFragment
import lv.rigadevday.android.utils.BaseApp
import lv.rigadevday.android.utils.bindSchedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File
import java.net.URL


/**
 * @author vsouhrada (vaclav.souhrada@eman.cz)
 * @see BaseFragment
 */
class MapFragment : BaseFragment(), OnMapReadyCallback {

    override val layoutId = R.layout.fragment_map

    override val ignoreUiUpdates = true;

    private lateinit var map: GoogleMap
    private final val KML_LAYER = "kmllayer.kml"

    companion object {
        const val PERMISSION_REQUEST_CODE = 301
    }

    override fun inject() {
        BaseApp.graph.inject(this)
    }

    override fun viewReady(view: View) {
        setupActionBar(R.string.tab_map)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        if (context != null && activity != null && !activity.isFinishing) {
            map = googleMap

            checkUserPermissions()

            val bounds = LatLngBounds.Builder().include(LatLng(MAP_NW_LATITUDE, MAP_NW_LONGITUDE)).include(LatLng(MAP_SE_LATITUDE, MAP_SE_LONGITUDE)).build()
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0))

            if (isFileExist(KML_LAYER)) {
                loadLocalKmlLayer()
            } else {
                repo.devFestAreaKML()
                        .bindSchedulers()
                        .subscribe { kml ->

                            doAsync {
                                val inputStream = URL(kml).readText().byteInputStream()
                                val fos = context.openFileOutput(KML_LAYER, Context.MODE_PRIVATE)
                                inputStream.copyTo(out = fos)
                                fos.close()
                                uiThread {
                                    if (context != null && activity != null && !activity.isFinishing) {
                                        loadLocalKmlLayer()
                                    }
                                }
                            }
                        }
            }


        }
    }

    private fun loadLocalKmlLayer() {
        try {
            val file = File(context.filesDir, KML_LAYER)
            val layer = KmlLayer(map, file.inputStream(), context.applicationContext)
            layer.addLayerToMap()
        } catch (e: Exception) {

        }

    }

    @SuppressLint("MissingPermission")
    private fun checkUserPermissions() {
        val permissions = mutableListOf<String>()
        if (!hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        if (!hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (permissions.isNotEmpty()) {
            requestPermissions(permissions.toTypedArray(), PERMISSION_REQUEST_CODE);
        } else {
            map.isMyLocationEnabled = true
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (PERMISSION_REQUEST_CODE == requestCode) {
            if (hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    && hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {

                map.isMyLocationEnabled = true
            }
        }
    }

    private fun hasPermission(permission: String) = PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, permission)

    fun isFileExist(fname: String): Boolean {
        val file = File(context.filesDir, fname)
        return file.exists()
    }
}