/*
  awt-project
  Copyright (C) 2019  Susanna Pozzoli

  This file is part of awt-project.

  awt-project is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  awt-project is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with awt-project.  If not, see <https://www.gnu.org/licenses/>.
*/

var campaignId = $( "#id" ).val();

var yellowIcon = L.icon({
  iconUrl: '/js/leaflet/images/yellow.png',
  shadowUrl: '/js/leaflet/images/shadow.png',

  iconSize: [32, 32],
  shadowSize: [64, 64],
  iconAnchor: [16, 32],
  shadowAnchor: [16, 32],
  popupAnchor: [0, -32]
});

var orangeIcon = L.icon({
  iconUrl: '/js/leaflet/images/orange.png',
  shadowUrl: '/js/leaflet/images/shadow.png',

  iconSize: [32, 32],
  shadowSize: [64, 64],
  iconAnchor: [16, 32],
  shadowAnchor: [16, 32],
  popupAnchor: [0, -32]
});

var redIcon = L.icon({
  iconUrl: '/js/leaflet/images/red.png',
  shadowUrl: '/js/leaflet/images/shadow.png',

  iconSize: [32, 32],
  shadowSize: [64, 64],
  iconAnchor: [16, 32],
  shadowAnchor: [16, 32],
  popupAnchor: [0, -32]
});

var greenIcon = L.icon({
  iconUrl: '/js/leaflet/images/green.png',
  shadowUrl: '/js/leaflet/images/shadow.png',

  iconSize: [32, 32],
  shadowSize: [64, 64],
  iconAnchor: [16, 32],
  shadowAnchor: [16, 32],
  popupAnchor: [0, -32]
});

$( "#pills-map-tab" ).on( "shown.bs.tab", function( e ) {
  $( "#map-container_manager" ).height( (1080 / 1920) * $( "#map-container_manager" ).width() + "px" );

  $( "#map-2d_manager" ).height( $( "#map-container_manager" ).height() + "px" );
  $( "#map-3d_manager" ).height( $( "#map-container_manager" ).height() + "px" );

  setTimeout(function() {
    map.invalidateSize();
  }, 300);

  if (greenPeaks.length + orangePeaks.length + redPeaks.length + yellowPeaks.length > 0) {
    var peak;
    if (greenPeaks.length == 0) {
      var peaks = yellowPeaks.concat(orangePeaks.concat(redPeaks));
      peak = peaks[Math.floor(Math.random() * peaks.length)];
    }
    if (greenPeaks.length > 0) {
      peak = greenPeaks[Math.floor(Math.random() * greenPeaks.length)];
    }
    var latlng = L.latLng(peak.latitude, peak.longitude);
    map.setView(latlng, 13);
    viewer.camera.setView({
      destination : Cesium.Cartesian3.fromDegrees(peak.longitude, peak.latitude, 8848.0),
      orientation : {
        heading : 0.0,
        pitch : -Cesium.Math.PI_OVER_TWO,
        roll : 0.0
      }
    });
  }
} );

$( "#file" ).on( "change", function() {
  var name = $( "#file" ).val();
  var start = name.lastIndexOf("\\") + 1;
  name = name.substring(start);
  $( "#file" ).next().html(name);
} );

$( "#2d" ).on( "change", function() {
  if ($( "#2d" ).is( ":checked" )) {
    $( "#map-2d_manager" ).show();
    map.invalidateSize();
    var latlng = L.latLng();

    $( "#map-3d_manager" ).hide();
  }
} );

$( "#3d" ).on( "change", function() {
  if ($( "#3d" ).is( ":checked" )) {
    $( "#map-2d_manager" ).hide();
    map.invalidateSize();
    var latlng = map.getCenter();

    $( "#map-3d_manager" ).show();
  }
} );

var map = L.map('map-2d_manager').setView([51.505, -0.09], 13);

L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
  attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, <a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
  maxZoom: 18,
  id: 'mapbox.streets-satellite',
  accessToken: '' // TODO
}).addTo(map);

Cesium.Ion.defaultAccessToken = ''; // TODO

var viewer = new Cesium.Viewer('map-3d_manager', {
  baseLayerPicker : false,
  fullscreenButton : false,
  mapProjection : new Cesium.WebMercatorProjection(),
  sceneMode : Cesium.SceneMode.COLUMBUS_VIEW,
  terrainProvider : Cesium.createWorldTerrain({
    requestWaterMask : true,
    requestVertexNormals: true
  })
});

viewer.screenSpaceEventHandler.removeInputAction(Cesium.ScreenSpaceEventType.LEFT_CLICK);
viewer.screenSpaceEventHandler.removeInputAction(Cesium.ScreenSpaceEventType.LEFT_DOUBLE_CLICK);

$.each( yellowPeaks, function( index, element ) {
  var height;
  if (element.elevation == null) {
    height = 0.0;
  } else {
      height = element.elevation;
  }
  var latitude = element.latitude;
  var longitude = element.longitude;

  L.marker([latitude, longitude], {icon: yellowIcon}).addTo(map).bindPopup("<b>Peak " + element.name + "</b><br><a href=\"/manager/campaigns/" + campaignId + "/peaks/" + element.id + "\">Details</a>");

  viewer.entities.add({
    id : 'cylinder-' + element.id,
    position : Cesium.Cartesian3.fromDegrees(longitude, latitude, height),
    cylinder : {
      length: 1000,
      topRadius : 0.0,
      bottomRadius : 300,
      material : Cesium.Color.YELLOW
    }
  });
  var scene = viewer.scene;
  var handler = new Cesium.ScreenSpaceEventHandler(scene.canvas);
  handler.setInputAction(function(movement) {
    var pick = scene.pick(movement.position);
    if ((Cesium.defined(pick)) && (pick.id.id === 'cylinder-' + element.id)) {
      window.location.href = "/manager/campaigns/" + campignId + "/peaks/" + element.id;
    }
  }, Cesium.ScreenSpaceEventType.LEFT_CLICK);
} );

$.each( orangePeaks, function( index, element ) {
  var height;
  if (element.elevation == null) {
    height = 0.0;
  } else {
      height = element.elevation;
  }
  var latitude = element.latitude;
  var longitude = element.longitude;

  L.marker([latitude, longitude], {icon: orangeIcon}).addTo(map).bindPopup("<b>Peak " + element.name + "</b><br><a href=\"/manager/campaigns/" + campaignId + "/peaks/" + element.id + "\">Details</a>");

  viewer.entities.add({
    id : 'cylinder-' + element.id,
    position : Cesium.Cartesian3.fromDegrees(longitude, latitude, height),
    cylinder : {
      length: 1000,
      topRadius : 0.0,
      bottomRadius : 300,
      material : Cesium.Color.ORANGE
    }
  });
  var scene = viewer.scene;
  var handler = new Cesium.ScreenSpaceEventHandler(scene.canvas);
  handler.setInputAction(function(movement) {
    var pick = scene.pick(movement.position);
    if ((Cesium.defined(pick)) && (pick.id.id === 'cylinder-' + element.id)) {
      window.location.href = "/manager/campaigns/" + campignId + "/peaks/" + element.id;
    }
  }, Cesium.ScreenSpaceEventType.LEFT_CLICK);
} );

$.each( redPeaks, function( index, element ) {
  var height;
  if (element.elevation == null) {
    height = 0.0;
  } else {
      height = element.elevation;
  }
  var latitude = element.latitude;
  var longitude = element.longitude;

  L.marker([latitude, longitude], {icon: redIcon}).addTo(map).bindPopup("<b>Peak " + element.name + "</b><br><a href=\"/manager/campaigns/" + campignId + "/peaks/" + element.id + "\">Details</a>");

  viewer.entities.add({
    id : 'cylinder-' + element.id,
    position : Cesium.Cartesian3.fromDegrees(longitude, latitude, height),
    cylinder : {
      length: 1000,
      topRadius : 0.0,
      bottomRadius : 300,
      material : Cesium.Color.RED
    }
  });
  var scene = viewer.scene;
  var handler = new Cesium.ScreenSpaceEventHandler(scene.canvas);
  handler.setInputAction(function(movement) {
    var pick = scene.pick(movement.position);
    if ((Cesium.defined(pick)) && (pick.id.id === 'cylinder-' + element.id)) {
      window.location.href = "/manager/campaigns/" + campignId + "/peaks/" + element.id;
    }
  }, Cesium.ScreenSpaceEventType.LEFT_CLICK);
} );

$.each( greenPeaks, function( index, element ) {
  var height;
  if (element.elevation == null) {
    height = 0.0;
  } else {
      height = element.elevation;
  }
  var latitude = element.latitude;
  var longitude = element.longitude;

  L.marker([latitude, longitude], {icon: greenIcon}).addTo(map).bindPopup("<b>Peak " + element.name + "</b><br><a href=\"/manager/campaigns/" + campaignId + "/peaks/" + element.id + "\">Details</a>");

  viewer.entities.add({
    id : 'cylinder-' + element.id,
    position : Cesium.Cartesian3.fromDegrees(longitude, latitude, height),
    cylinder : {
      length: 1000,
      topRadius : 0.0,
      bottomRadius : 300,
      material : Cesium.Color.GREEN
    }
  });
  var scene = viewer.scene;
  var handler = new Cesium.ScreenSpaceEventHandler(scene.canvas);
  handler.setInputAction(function(movement) {
    var pick = scene.pick(movement.position);
    if ((Cesium.defined(pick)) && (pick.id.id === 'cylinder-' + element.id)) {
      window.location.href = "/manager/campaigns/" + campignId + "/peaks/" + element.id;
    }
  }, Cesium.ScreenSpaceEventType.LEFT_CLICK);
} );

function selectPeak(peakId) {
  var peak = undefined;
  $.each( yellowPeaks, function( index, element ) {
    if (element.id == peakId) {
      peak = element;
      return false;
    }
  } );
  $.each( orangePeaks, function( index, element ) {
    if (element.id == peakId) {
      peak = element;
      return false;
    }
  } );
  $.each( redPeaks, function( index, element ) {
    if (element.id == peakId) {
      peak = element;
      return false;
    }
  } );
  $.each( greenPeaks, function( index, element ) {
    if (element.id == peakId) {
      peak = element;
      return false;
    }
  } );
  return peak;
}
