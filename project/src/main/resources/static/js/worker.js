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

var id = $( "#id" ).val();

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

$( "#2d" ).on( "change", function() {
  if ($( "#2d" ).is( ":checked" )) {
    $( "#map-2d_worker" ).show();
    map.invalidateSize();
    var latlng = L.latLng();

    $( "#map-3d_worker" ).hide();
  }
} );

$( "#3d" ).on( "change", function() {
  if ($( "#3d" ).is( ":checked" )) {
    $( "#map-2d_worker" ).hide();
    map.invalidateSize();
    var latlng = map.getCenter();

    $( "#map-3d_worker" ).show();
  }
} );

var map = L.map('map-2d_worker').setView([51.505, -0.09], 13);

L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
  attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, <a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
  maxZoom: 18,
  id: 'mapbox.streets-satellite',
  accessToken: 'pk.eyJ1Ijoic3BvenpvbGkiLCJhIjoiY2pqYTY3d2lzMDJmbDNwcGVtOGZjc3p1MSJ9.FfRspyXWiQvFKGEmMhCIpg'
}).addTo(map);

Cesium.Ion.defaultAccessToken = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJqdGkiOiI4ZGFiYzMyNi0xZTU5LTQ4OWItOGMwYi1mMTEyYjMxMGI5ZGIiLCJpZCI6MjExNywiaWF0IjoxNTMxNTkwMTM0fQ.9kR241eLyACoxZtEBzOl5BvYbg1g6yd95bjwnq7qzbU';

var viewer = new Cesium.Viewer('map-3d_worker', {
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

$( "#map-container_worker" ).height( (1080 / 1920) * $( "#map-container_worker" ).width() + "px" );

$( "#map-2d_worker" ).height( $( "#map-container_worker" ).height() + "px" );
$( "#map-3d_worker" ).height( $( "#map-container_worker" ).height() + "px" );

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

$.each( yellowPeaks, function( index, element ) {
  var height;
  if (element.elevation == null) {
    height = 0.0;
  } else {
      height = element.elevation;
  }
  var latitude = element.latitude;
  var longitude = element.longitude;

  L.marker([latitude, longitude], {icon: yellowIcon}).addTo(map).bindPopup("<b>Peak " + element.name + "</b><br><a onclick=\"showModal(" + element.id + ", false)\">Details</a>");

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
      showModal(element.id, false);
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

  L.marker([latitude, longitude], {icon: orangeIcon}).addTo(map).bindPopup("<b>Peak " + element.name + "</b><br><a onclick=\"showModal(" + element.id + ", false)\">Details</a>");

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
      showModal(element.id, false);
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

  L.marker([latitude, longitude], {icon: redIcon}).addTo(map).bindPopup("<b>Peak " + element.name + "</b><br><a onclick=\"showModal(" + element.id + ", false)\">Details</a>");

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
      showModal(element.id, false);
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

  L.marker([latitude, longitude], {icon: greenIcon}).addTo(map).bindPopup("<b>Peak " + element.name + "</b><br><a onclick=\"showModal(" + element.id + ", true)\">Details</a>");

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
      showModal(element.id, true);
    }
  }, Cesium.ScreenSpaceEventType.LEFT_CLICK);
} );

function addLocalizedName( length, index ) {
  var language = $( "#localized-name-" + index + "-language" ).val();
  var name = $( "#localized-name-" + index + "-name" ).val();

  var localizedNames;
  var value = $( "#localized-names" ).val();
  if (value != null) {
    localizedNames = JSON.parse(value);
  }
  if (localizedNames != undefined) {
    var localizedName = { language: language, name: name };
    localizedNames.push(localizedName);
  }
  if (localizedNames == undefined) {
    localizedNames = [{ language: language, name: name }];
  }
  $( "#localized-names" ).val( JSON.stringify(localizedNames) );

  $( "#localized-name-" + index + "-name" ).parent().next().children().first().html( "Remove" );
  $( "#localized-name-" + index + "-name" ).parent().next().children().first().attr( "onclick", "removeLocalizedName(" + localizedNames.length + ", " + index + ")" );

  var htmlString = $( "#localized-name-" + index + "-name" ).parent().parent().parent().html();
  htmlString += "" +
  "<div class=\"row mb-3\">" +
    "<div class=\"col-4\">" +
      "<input type=\"text\" class=\"form-control\" id=\"localized-name-";
  if (length == 0) {
    htmlString += 1;
  } else {
    htmlString += length + 1;
  }
  htmlString += "-language\" />" +
    "</div>" +
    "<div class=\"col-4\">" +
      "<input type=\"text\" class=\"form-control\" id=\"localized-name-";
  if (length == 0) {
    htmlString += 1;
  } else {
    htmlString += length + 1;
  }
  htmlString += "-name\" />" +
    "</div>" +
    "<div class=\"col-4\">" +
      "<button type=\"button\" class=\"btn btn-primary w-100\" onclick=\"addLocalizedName(" + (length + 1) + ", ";
  if (length == 0) {
    htmlString += 1;
  } else {
    htmlString += length + 1;
  }
  htmlString += ")\">Add</button>" +
    "</div>" +
  "</div>";
  $( "#localized-name-" + index + "-name" ).parent().parent().parent().html( htmlString );

  for (var i = 0; i < localizedNames.length; i++) {
    $( "#localized-name-" + i + "-language" ).val( localizedNames[i].language );
    $( "#localized-name-" + i + "-name" ).val( localizedNames[i].name );
  }
}

function removeLocalizedName( length, index ) {
  var language = $( "#localized-name-" + index + "-language" ).val();
  var name = $( "#localized-name-" + index + "-name" ).val();

  var localizedNames;
  var value = $( "#localized-names" ).val();
  if (value != null) {
    localizedNames = JSON.parse($( "#localized-names" ).html());
  }
  if (localizedNames != undefined) {
    var localizedName = { language: language, name: name };
    for (var i = 0; i < localizedNames.length; i++) {
      if ((localizedNames[i].language == language) && (localizedNames[i].name == name)) {
        localizedNames.splice(i, 1);
      }
    }
  }
  if (localizedNames == undefined) {
    localizedNames = [];
  }
  $( "#localized-names" ).val( JSON.stringify(localizedNames) );

  var htmlString;
  if (localizedNames.length == 0) {
    htmlString = "" +
    "<div class=\"row mb-3\">" +
      "<div class=\"col-4\">" +
        "<input type=\"text\" class=\"form-control\" id=\"localized-name-0-language\" />" +
      "</div>" +
      "<div class=\"col-4\">" +
        "<input type=\"text\" class=\"form-control\" id=\"localized-name-0-name\" />" +
      "</div>" +
      "<div class=\"col-4\">" +
        "<button type=\"button\" class=\"btn btn-primary w-100\" onclick=\"addLocalizedName(0, 0)\">Add</button>" +
      "</div>" +
    "</div>";
  }
  if (localizedNames.length > 0) {
    htmlString = "";
    var j = 0;
    for (; j < localizedNames.length; j++) {
      htmlString += "" +
      "<div class=\"row mb-3\">" +
        "<div class=\"col-4\">" +
          "<input type=\"text\" class=\"form-control\" id=\"localized-name-" + j + "-language\" />" +
        "</div>" +
        "<div class=\"col-4\">" +
          "<input type=\"text\" class=\"form-control\" id=\"localized-name-" + j + "-name\" />" +
        "</div>" +
        "<div class=\"col-4\">" +
          "<button type=\"button\" class=\"btn btn-primary w-100\" onclick=\"removeLocalizedName(" + localizedNames.length + ", " + j + ")\">Remove</button>" +
        "</div>" +
      "</div>";
    }
    htmlString += "" +
    "<div class=\"row mb-3\">" +
      "<div class=\"col-4\">" +
        "<input type=\"text\" class=\"form-control\" id=\"localized-name-" + j + "-language\" />" +
      "</div>" +
      "<div class=\"col-4\">" +
        "<input type=\"text\" class=\"form-control\" id=\"localized-name-" + j + "-name\" />" +
      "</div>" +
      "<div class=\"col-4\">" +
        "<button type=\"button\" class=\"btn btn-primary w-100\" onclick=\"addLocalizedName(" + localizedNames.length + ", " + j + ")\">Add</button>" +
      "</div>" +
    "</div>";
  }
  $( "#localized-name-" + index + "-name" ).parent().parent().parent().html( htmlString );

  for (var i = 0; i < localizedNames.length; i++) {
    $( "#localized-name-" + i + "-language" ).val( localizedNames[i].language );
    $( "#localized-name-" + i + "-name" ).val( localizedNames[i].name );
  }
}

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

function showModal(peakId, green) {
  // TODO green peaks
  var peak = selectPeak(peakId);
  var html = "" +
    "<div class=\"modal\" id=\"modal\" tabindex=\"-1\" role=\"dialog\">" +
      "<div class=\"modal-dialog\" role=\"document\">" +
        "<div class=\"modal-content\">" +
          "<div class=\"modal-header\">" +
            "<h5 class=\"modal-title\">Peak " + peak.name + "</h5>" +
            "<button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-label=\"Close\">" +
              "<span aria-hidden=\"true\">&times;</span>" +
            "</button>" +
          "</div>";
  if (!(green)) {
    html += "" +
          "<form action=\"/worker/campaigns/" + id + "/peaks/" + peakId + "/annotate\" method=\"post\">" +
            "<div class=\"modal-body\">" +
              "<p>" +
                "<b>Latitude:</b> " + peak.latitude +
                "<br>" +
                "<b>Longitude:</b> " + peak.longitude +
              "</p>" +
              "<p>" +
                "<b>Provenance:</b> " + peak.provenance +
              "</p>" +
              "<div class=\"mb-3\" id=\"radios\">" +
                "<div class=\"custom-control custom-radio custom-control-inline\">" +
                  "<input type=\"radio\" id=\"valid\" name=\"custom-radio-inline\" class=\"custom-control-input\" value=\"valid\">" +
                  "<label class=\"custom-control-label\" for=\"valid\">Valid</label>" +
                "</div>" +
                "<div class=\"custom-control custom-radio custom-control-inline\">" +
                  "<input type=\"radio\" id=\"invalid\" name=\"custom-radio-inline\" class=\"custom-control-input\" value=\"invalid\">" +
                  "<label class=\"custom-control-label\" for=\"invalid\">Invalid</label>" +
                "</div>" +
              "</div>" +
              "<fieldset disabled>" +
                // elevation
                "<div class=\"form-group\">" +
                  "<label for=\"elevation\">Elevation</label>" +
                  "<input type=\"number\" class=\"form-control\" id=\"elevation\" min=\"0.000\" name=\"elevation\" step=\"0.001\" value=\"" + peak.elevation + "\" />" +
                "</div>" +
                // name
                "<div class=\"form-group\">" +
                  "<label for=\"name\">Name</label>" +
                  "<input type=\"text\" class=\"form-control\" id=\"name\" name=\"name\" value=\"" + peak.name + "\" />" +
                "</div>" +
                // localized names
                "<div class=\"form-group mb-0\">" +
                  "<label for=\"localized-names\">Localized Names</label>" +
                  "<textarea class=\"form-control\" id=\"localized-names\" name=\"localized-names\" style=\"display:none;\">";
    if ((peak.localizedNames != null) && (peak.localizedNames.length > 0)) {
      html += JSON.stringify(peak.localizedNames).replace(/"id":[0-9]+,/g, "");
    } else {
      html += "[]";
    }
    html += "" +
                  "</textarea>" +
                "</div>" +
                "<div class=\"form-group\">";
    var i = 0;
    if (peak.localizedNames != null) {
      for (; i < peak.localizedNames.length; i++) {
        html += "" +
                    "<div class=\"row mb-3\">" +
                      "<div class=\"col-4\">" +
                        "<input type=\"text\" class=\"form-control\" id=\"localized-name-" + i + "-language\" value=\"" + peak.localizedNames[i].language + "\" />" +
                      "</div>" +
                      "<div class=\"col-4\">" +
                        "<input type=\"text\" class=\"form-control\" id=\"localized-name-" + i + "-name\" value=\"" + peak.localizedNames[i].name + "\" />" +
                      "</div>" +
                      "<div class=\"col-4\">" +
                        "<button type=\"button\" class=\"btn btn-primary w-100\" onclick=\"removeLocalizedName(" + peak.localizedNames.length + ", " + i + ")\">Remove</button>" +
                      "</div>" +
                    "</div>";
      }
    }
    html += "" +
                  "<div class=\"row mb-3\">" +
                    "<div class=\"col-4\">" +
                      "<input type=\"text\" class=\"form-control\" id=\"localized-name-" + i + "-language\" />" +
                    "</div>" +
                    "<div class=\"col-4\">" +
                      "<input type=\"text\" class=\"form-control\" id=\"localized-name-" + i + "-name\" />" +
                    "</div>" +
                    "<div class=\"col-4\">" +
                      "<button type=\"button\" class=\"btn btn-primary w-100\" onclick=\"addLocalizedName(" + peak.localizedNames.length + ", " + i + ")\">Add</button>" +
                    "</div>" +
                  "</div>" +
                "</div>" +
              "</fieldset>" +
            "</div>" +
            "<div class=\"modal-footer\">" +
              "<button type=\"submit\" class=\"btn btn-primary\">Submit</button>" +
              "<button type=\"button\" class=\"btn btn-secondary\" data-dismiss=\"modal\">Close</button>" +
            "</div>" +
          "</form>";
  }
  if (green) {
    html += "" +
            "<div class=\"modal-body\">" +
              // name
              "<p>" +
                "<b>Name:</b> " + peak.name +
              "</p>" +
              "<p>" +
                "<b>Latitude:</b> " + peak.latitude +
                "<br>" +
                "<b>Longitude:</b> " + peak.longitude +
                "<br>" +
                "<b>Elevation:</b> " + peak.elevation +
              "</p>";
              // localized names
    if ((peak.localizedNames != null) && (peak.localizedNames.length > 0)) {
      html += "" +
              "<p class=\"mb-0\">" +
                "<b>Localized Names:</b>" +
              "</p>" +
              "<ul>";
      for (var i = 0; i < peak.localizedNames.length; i++) {
        html += "" +
                "<li>" +
                  "<b>" + peak.localizedNames[i].language + "</b> " + peak.localizedNames[i].name +
                "</li>";
      }
      html += "" +
              "</ul>";
    }
    html += "" +
              // provenance
              "<p>" +
                "<b>Provenance:</b> " + peak.provenance +
              "</p>" +
            "</div>" +
            "<div class=\"modal-footer\">" +
              "<button type=\"submit\" class=\"btn btn-primary\">Submit</button>" +
              "<button type=\"button\" class=\"btn btn-secondary\" data-dismiss=\"modal\">Close</button>" +
            "</div>";
  }
  html += "" +
        "</div>" +
      "</div>" +
    "</div>" +
  "";
  $( "#modal-container" ).html( html );

  $( "#valid" ).on( "change", function( e ) {
    if ($( "#valid" ).is( ":checked" )) {
      $( "#radios" ).next().prop( "disabled", true );
    }
  });
  $( "#invalid" ).on( "change", function( e ) {
    if ($( "#invalid" ).is( ":checked" )) {
      $( "#radios" ).next().prop( "disabled", false );
    }
  });

  $( "#modal" ).modal( 'show' );
}
