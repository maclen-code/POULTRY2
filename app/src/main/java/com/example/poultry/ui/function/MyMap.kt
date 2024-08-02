package com.example.poultry.ui.function

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.poultry.R
import com.example.poultry.ui.global.filter.Filter
import com.google.gson.JsonElement
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.plugin.animation.MapAnimationOptions.Companion.mapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions


object MyMap {

    var lastVisit:Point?=null
    enum class MarkerStatus {
        LOCATION,
        NEW_LOCATION,
        INACTIVE,
        MISS,
        VISITED,
        NON_SCHED,
        PROBLEM
    }

    private fun iconColor(markerStatus: MarkerStatus):Int {
        return when (markerStatus) {
            MarkerStatus.LOCATION -> R.color.account_location
            MarkerStatus.NEW_LOCATION -> R.color.account_new_location
            MarkerStatus.INACTIVE -> R.color.marker_inactive
            MarkerStatus.MISS-> R.color.marker_miss
            MarkerStatus.VISITED -> R.color.marker_visited
            MarkerStatus.NON_SCHED -> R.color.marker_nonSched
            MarkerStatus.PROBLEM -> R.color.marker_issue
        }
    }

    fun getOfficePoint(context: Context):Point?{
        var point:Point?=null
        val settingName = "${Filter.cid} settings"
        val offLoc=MySharedPrep.get(context, settingName, "OFF_LOC")
        if (offLoc != "") {
            val points: List<String> = offLoc.split(",")
            val lon = points[1].toDouble()
            val lat = points[0].toDouble()
            point = Point.fromLngLat(lon, lat)
        }

        return point
    }

    fun getLastVisit(context: Context):Point?{
        var point:Point?=null
        if (lastVisit!=null)
            point=lastVisit
        else{
            val settingName = "${Filter.cid} settings"
            val lastVisit = MySharedPrep.get(context, settingName, "last visit")
            if (lastVisit != "") {
                val points: List<String> = lastVisit.split(",")
                val lon = points[1].toDouble()
                val lat = points[0].toDouble()
                point = Point.fromLngLat(lon, lat)
            }
        }
        return point
    }

    fun setInitialPoint(mapboxMap: MapboxMap,point: Point?){
        // set initial camera position
        if (point!=null) {
            val initialCameraOptions = CameraOptions.Builder()
                .center(point)
                .pitch(0.0)
                .zoom(18.0)
                .bearing(0.0)
                .build()
            mapboxMap.setCamera(initialCameraOptions)
        }

        mapboxMap.setCamera(
            CameraOptions.Builder()
                .zoom(16.0)
                .build()
        )
    }

    fun cameraToPoints(mapView:MapView,point:Point,animate:Boolean){
        val mapboxMap = mapView.mapboxMap
        val camera = cameraOptions {
            center(point)
            zoom(18.0)
            pitch(45.0)
            bearing(0.0)
        }

        if (animate)
            mapboxMap.flyTo(
                camera,
                mapAnimationOptions {
                    duration(12_000)
                }
            )
        else
            mapboxMap.setCamera(camera)

    }
    fun cameraToPoints(mapView:MapView,list:List<Point>,animate:Boolean){
        val mapboxMap = mapView.mapboxMap
        val camera = mapboxMap
            .cameraForCoordinates(
                list,
                CameraOptions.Builder()
                    .zoom(18.0)
                    .pitch(45.0)
                    .bearing(0.0)
                    .build(),
                EdgeInsets(50.0, 50.0, 50.0, 50.0),
                18.0,null

            )
        if (animate)
            mapboxMap.flyTo(
                camera,
                mapAnimationOptions {
                    duration(12_000)
                }
            )
        else
            mapboxMap.setCamera(camera)
    }

    class IconOffset(var x:Double,var y:Double)
    fun addMarker(context: Context,pointAnnotationManager:PointAnnotationManager,point: Point,
                  markerStatus:MarkerStatus=MarkerStatus.MISS,iconOffset:IconOffset=IconOffset(0.0,0.0),
                  data:JsonElement?=null
    ) {

        val color=iconColor(markerStatus)

        val unwrappedDrawable: Drawable? =
            AppCompatResources.getDrawable(context, R.drawable.ic_location_solid)
        val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)


        DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(context,color))

        val bitmap=(wrappedDrawable).toBitmap()
        val scaledBitmap= Bitmap.createScaledBitmap(bitmap,48,48, false)

        val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
            .withPoint(point)
            .withIconOffset(listOf(iconOffset.x,iconOffset.y))
            .withIconImage(
                scaledBitmap
            )
            .withIconAnchor(IconAnchor.TOP)
        if (data!=null) pointAnnotationOptions.withData(data)
        pointAnnotationManager.create(pointAnnotationOptions)
    }
    fun removeMarker(pointAnnotationManager: PointAnnotationManager, point: Point){
        pointAnnotationManager.annotations.forEach {
            if(it.point==point) {
                pointAnnotationManager.delete(it)
                return
            }
        }
    }

    fun addWarehouseMarker(context: Context,pointAnnotationManager:PointAnnotationManager,
                           point: Point
    ) {


        val unwrappedDrawable: Drawable? =
            AppCompatResources.getDrawable(context, R.drawable.ic_warehouse)
        val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)


        val bitmap=(wrappedDrawable).toBitmap()
        val scaledBitmap= Bitmap.createScaledBitmap(bitmap,48,48, false)

        val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
            .withPoint(point)
            .withIconImage(
                scaledBitmap
            )
            .withIconAnchor(IconAnchor.TOP)
        pointAnnotationManager.create(pointAnnotationOptions)
    }



}