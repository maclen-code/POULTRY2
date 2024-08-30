package com.example.poultry2.ui.downloadMap


import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.example.poultry2.R
import com.example.poultry2.databinding.FragmentDownloadMapBinding
import com.example.poultry2.ui.LocationPermissionHelper
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.bindgen.Value
import com.mapbox.common.*
import com.mapbox.geojson.Point
import com.mapbox.maps.*
import com.mapbox.maps.extension.style.expressions.generated.Expression.Companion.interpolate
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import java.lang.ref.WeakReference


class DownloadMapFragment : Fragment() {


    private var _binding: FragmentDownloadMapBinding? = null
    private lateinit var locationPermissionHelper: com.example.poultry2.ui.LocationPermissionHelper
    private val binding get() = _binding!!

    private val stylePackLoadOptions: StylePackLoadOptions = StylePackLoadOptions.Builder()
        .glyphsRasterizationMode(GlyphsRasterizationMode.IDEOGRAPHS_RASTERIZED_LOCALLY)
        .metadata(Value("STYLE_PACK_METADATA"))
        .build()



    private val offlineManager: OfflineManager = OfflineManager()
    private val tileStore = TileStore.create()

    private val tilesetDescriptor = offlineManager.createTilesetDescriptor(
        TilesetDescriptorOptions.Builder()
            .styleURI(Style.STANDARD)
            .minZoom(0)
            .maxZoom(22)
            .build()
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDownloadMapBinding.inflate(inflater, container, false)

        locationPermissionHelper =
            com.example.poultry2.ui.LocationPermissionHelper(WeakReference(requireActivity()))
        locationPermissionHelper.checkPermissions {
            onMapReady()
        }


        binding.mapView.gestures.addOnMoveListener(onMoveListener)
        setupMenu()

        binding.btDownload.setOnClickListener {
            downloadRegionDialog()
        }

        binding.btList.setOnClickListener {
            downloadedRegionList()
        }

        binding.clProgress.visibility=View.INVISIBLE

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onPrepareMenu(menu: Menu) {
                // Handle for example visibility of menu item
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_user_location, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Validate and handle the selected menu item

                return when (menuItem.itemId) {

                    R.id.menu_user_location -> {
                        initLocationComponent()
                        true
                    }


                    else -> false}
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener {
        binding.mapView.mapboxMap.setCamera(CameraOptions.Builder().bearing(it).build())
    }

    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        binding.mapView.mapboxMap.setCamera(CameraOptions.Builder().center(it).build())
        binding.mapView.gestures.focalPoint = binding.mapView.mapboxMap.pixelForCoordinate(it)
    }

    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            onCameraTrackingDismissed()
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {

        }
    }

    private fun onMapReady() {

        binding.mapView.mapboxMap.setCamera(
            CameraOptions.Builder()
                .zoom(12.0)
                .build()
        )
        binding.mapView.mapboxMap.loadStyle(
            Style.STANDARD
        ) {
            initLocationComponent()
        }

    }

    @SuppressLint("IncorrectNumberOfArgumentsInExpression")
    private fun initLocationComponent() {
//        val locationComponentPlugin = binding.mapView.location
//        locationComponentPlugin.updateSettings {
//            this.enabled = true
//        }
//        locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
//        locationComponentPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        val locationComponentPlugin = binding.mapView.location
        locationComponentPlugin.updateSettings {
            puckBearing = PuckBearing.COURSE
            puckBearingEnabled = true
            enabled = true
            locationPuck = LocationPuck2D(
                bearingImage = ImageHolder.from(R.drawable.mapbox_user_puck_icon),
                shadowImage = ImageHolder.from(R.drawable.mapbox_user_icon_shadow),
                scaleExpression = interpolate {
                    linear()
                    zoom()
                    stop {
                        literal(0.0)
                        literal(0.6)
                    }
                    stop {
                        literal(20.0)
                        literal(1.0)
                    }
                }.toJson()
            )
        }
        locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        locationComponentPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
}

    private fun onCameraTrackingDismissed() {
        binding.mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        binding.mapView.location
            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


///Download region

//    private val offlineManager: OfflineManager = OfflineManager()
//    private val tileStore = TileStore.create()
//
//    private var stylePackCancelable: Cancelable? = null
//    private var tilePackCancelable: Cancelable? = null

    private fun downloadRegionDialog() {
        // Set up download interaction. Display a dialog
        // when the user clicks download button and require
        // a user-provided region name
        val builder =
            AlertDialog.Builder(requireContext())
        val regionNameEdit = EditText(requireContext())
        regionNameEdit.hint = "Set Region"

        // Build the dialog box
        builder.setTitle("Region")
            .setView(regionNameEdit)
            .setMessage("Download Region")
            .setPositiveButton(
                "Ok"
            ) { _, _ ->
                val regionName = regionNameEdit.text.toString()
                // Require a region name to begin the download.
                // If the user-provided string is empty, display
                // a toast message and do not begin download.
                if (regionName.isEmpty()) {
                    Toast.makeText(
                        activity,
                        "set region name",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Begin download process
                    downloadOfflineRegion(regionName)
                }
            }
            .setNegativeButton(
                "Cancel"
            ) { dialog, _ -> dialog.cancel() }

        // Display the dialog
        builder.show()
    }

    private fun getBounds(): Point {
        val cameraState = binding.mapView.mapboxMap.cameraState
        return binding.mapView.mapboxMap
            .coordinateBoundsForCamera(cameraState.toCameraOptions()).center()
    }


    private fun downloadOfflineRegion(regionId:String) {



        val tileRegionLoadOptions = TileRegionLoadOptions.Builder()
            .geometry(getBounds())
            .descriptors(listOf(tilesetDescriptor))
            .metadata(Value("TILE_REGION_METADATA"))
            .acceptExpired(false)
            .networkRestriction(NetworkRestriction.NONE)
            .build()


        binding.clProgress.visibility=View.VISIBLE
        val stylePackCancelable = offlineManager.loadStylePack(
            Style.STANDARD,
            // Build Style pack load options
            stylePackLoadOptions,
            { progress ->
                // Handle the download progress
                val x=(progress.completedResourceCount.toDouble()/ progress.requiredResourceCount) * 100
                Handler(Looper.getMainLooper()).post {
                    binding.pbStyle.progress=x.toInt()
                }

            },
            { expected ->
                if (expected.isValue) {
                    expected.value?.let { stylePack ->
                        // Style pack download finished successfully
                    }
                }
                expected.error?.let {
                    // Handle errors that occurred during the style pack download.
                }
            }
        )





        val tileRegionCancelable = tileStore.loadTileRegion(
            regionId,
             tileRegionLoadOptions,
            { progress ->
                // Handle the download progress
                val x=(progress.completedResourceCount.toDouble()/ progress.requiredResourceCount) * 100
                Handler(Looper.getMainLooper()).post {
                    binding.pbRegion.progress=x.toInt()
                }
            }
        ) { expected ->
            if (expected.isValue) {
                // Tile region download finishes successfully
                expected.value?.let {
                    binding.clProgress.visibility=View.INVISIBLE
                }
            }
            expected.error?.let {
                // Handle errors that occurred during the tile region download.
            }
        }


    }


    private val offlineRegionsNames = ArrayList<String>()
    private fun downloadedRegionList() {
        offlineRegionsNames.clear()
        tileStore?.getAllTileRegions { expected ->
            if (expected.isValue) {
                expected.value?.let { tileRegionList ->
                    Handler(Looper.getMainLooper()).post {
                        tileRegionList.forEach { i ->
                            offlineRegionsNames.add(i.id)

                        }

                        if (offlineRegionsNames.isEmpty()) {
                            Toast.makeText(activity,"No region yet", Toast.LENGTH_SHORT).show()
                        }else {

                            val items =
                                offlineRegionsNames.toTypedArray<CharSequence>()
                            var regionSelected=0
                            val dialog =
                                AlertDialog.Builder(requireContext())
                                    .setTitle("Offline Region List")
                                    .setSingleChoiceItems(
                                        items, 0
                                    ) { dialog, which -> // Track which region the user selects
                                        regionSelected=which
                                        showOnMap(offlineRegionsNames[regionSelected])
                                    }

                                    .setPositiveButton("Ok") { dialog, _ ->
                                        dialog.dismiss()
                                    }
                                    .setNeutralButton("Delete") { dialog, _ ->
                                        removeMap(regionSelected)
                                    }
                                    .create()
                            dialog.show()
                        }

                    }
                }
            }
            expected.error?.let {

            }
        }

    }

    private fun removeMap(id:Int){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete  ${offlineRegionsNames[id]}")
        builder.setMessage("Are you sure?")
            .setCancelable(false)
            .setPositiveButton("Yes", null)
            .setNegativeButton("No", null)
        val alert = builder.create()
        alert.setOnShowListener {

            alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                tileStore.removeTileRegion(offlineRegionsNames[id])
                alert.dismiss()
            }

            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
                alert.dismiss()
            }
        }
        alert.show()
    }

    private fun showOnMap(id:String){
        tileStore.getTileRegionGeometry(id,
            (TileRegionGeometryCallback { result ->
                if (result.isValue) {
                    result.value?.let { i ->
                        val point: Point = i as Point
                        Handler(Looper.getMainLooper()).post {
                            binding.mapView.mapboxMap.setCamera(
                                CameraOptions.Builder()
                                    .zoom(12.0).center(point)
                                    .build()
                            )
                        }
                    }
                }
            })
        )
    }

}





