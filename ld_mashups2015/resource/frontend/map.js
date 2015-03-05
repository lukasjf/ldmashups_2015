var map;
var lastSearchResults;
var SEARCH_AJAX_FORM = {
    longitude : null,
    latitude : null,

    beforeSubmitHandler:function(arr, form, options) {
        $.each(arr, function(index, aField) {
            if ('longitude' === aField.name) {
                longitude = aField.value;
            } else if ('latitude' === aField.name) {
                latitude = aField.value;
            }
        })
        return (longitude && latitude);
    },

    successHandler:function(responseText, status, xhr, form) {
        $.ajax({
            'url' : 'http://localhost:9998/api/occurrences',
            'type' : 'GET',
            'data' : {
                'longitude' : longitude,
                'latitude' : latitude
            },

            'success' : function(data) {
                lastSearchresults = data;
                addFeaturesFor(data);
            }

        });
    },

    initSearchForm:function() {
        $("#searchform").ajaxForm({
            beforeSubmit:this.beforeSubmitHandler,
            success:this.successHandler,
            clearForm:false
        })
    }
}

function hideDiv() {
    document.getElementById('searchdiv').style.display = 'none';
    document.getElementById('images').style.display = 'none';
    document.getElementById('images').innerHTML= '';
}
        
function showSearch() {
    for (var el = 0;  el < document.getElementById('searchform').getElementsByTagName('label').length; el++) {
        document.getElementById('searchform').getElementsByTagName('label')[el].style.display="";
    }
    for (var el = 0; el < document.getElementById('searchform').getElementsByTagName('input').length; el++) {
        document.getElementById('searchform').getElementsByTagName('input')[el].style.display="";
    }
}

function getRendererFromQueryString() {
    var obj = {}, queryString = location.search.slice(1),
            re = /([^&=]+)=([^&]*)/g, m;

    while (m = re.exec(queryString)) {
        obj[decodeURIComponent(m[1])] = decodeURIComponent(m[2]);
    }
    if ('renderers' in obj) {
        return obj['renderers'].split(',');
    } else if ('renderer' in obj) {
        return [obj['renderer']];
    } else {
        return undefined;
    }
}

function loadMap() {   	   
   var rasterLayer =
		   new ol.layer.Tile({
			   source: new ol.source.OSM()
		   });

   var view = new ol.View({
	   center: [0, 0],
	   zoom: 10
   });

   var geolocation = new ol.Geolocation({
	   projection: view.getProjection(),
	   tracking: true
   });

   map = new ol.Map({
	   controls: ol.control.defaults({
		   attributionOptions: /** @type {olx.control.AttributionOptions} */ ({
			   collapsible: false
		   }),
		   zoomOptions: /** */ ({
			   zoomInTipLabel: '',
			   zoomOutTipLabel: ''
		   })
	   }),
	   layers: [rasterLayer],
	   target: document.getElementById('mapdiv'),
	   view: view
   });
   map.addControl(new ol.control.ZoomSlider());
   
   geolocation.once("change:position", function() {
		map.getView().setCenter(geolocation.getPosition());
		var coords = ol.proj.transform(geolocation.getPosition(), view.getProjection().getCode(), 'EPSG:4326');
		$('#longitude').val(coords[0]);
		$('#latitude').val(coords[1]);
   });

   var accuracyFeature = new ol.Feature({name: 'You are here!'});
   accuracyFeature.bindTo('geometry', geolocation, 'accuracyGeometry');

   var positionFeature = new ol.Feature({name: 'You are here!'});
   positionFeature.bindTo('geometry', geolocation, 'position')
		   .transform(function() {}, function(coordinates) {
			   return coordinates ? new ol.geom.Point(coordinates) : null;
		   });

   var featuresOverlay = new ol.FeatureOverlay({
	   map: map,
	   features: [accuracyFeature, positionFeature]
   });

   var element = document.getElementById('popup');

   var popup = new ol.Overlay({
	   element: element,
	   positioning: 'bottom-center',
	   stopEvent: false
   });

   // display popup on click
   map.on('click', function(evt) {
	   var feature = map.forEachFeatureAtPixel(evt.pixel,
			   function(feature, layer) {
				   return feature;
			   });
	   if (feature) {
		   var images = "";
		   for (var i = 0; i < feature.get('data')['occurrenceInfo'].length; i++) {
			   var occ = feature.get('data')['occurrenceInfo'][i];
			   var species = occ['species'];
			   images = images + "<table style='float:left;width:300px'><tr><td></td>";
			   images = images + '<td rowspan=2><a href="#" onClick="getSpeciesInfo(\''+species+'\')">' +
			   '<img src="' + occ['thumbnailURL']  + '"/></a></td></tr><tr><td colspan=2 valign="bottom">' +
					occ['binomial'] + '</td></tr></table>'
		   }
		   images = images;
		   var imagesElement = document.getElementById('images');
		   imagesElement.style.display = "";
		   imagesElement.innerHTML = images;
	   }
   });
   map.addOverlay(popup);

	// change mouse cursor when over marker
   $(map.getViewport()).on('mousemove', function(e) {
	   var pixel = map.getEventPixel(e.originalEvent);
	   var hit = map.forEachFeatureAtPixel(pixel, function(feature, layer) {
		   return true;
	   });
	   if (hit) {
		   map.getTarget().style.cursor = 'pointer';
	   } else {
		   map.getTarget().style.cursor = '';
	   }
   });
}
       
       

function addFeaturesFor(data) {
    var features = [];
    var locationData = [];
    for (var i = 0; i < data.length; i++) {
        if (locationData[[data[i]['longitude'],data[i]['latitude']]]) {
            locationData[[data[i]['longitude'],data[i]['latitude']]]['occurrenceInfo'].push(data[i]);
        } else {
            locationData[[data[i]['longitude'],data[i]['latitude']]] = {
                geolocation: {
                    longitude: data[i]["longitude"],
                    latitude: data[i]["latitude"]
                },
                occurrenceInfo: [data[i]]
            };
        }
    }

    var iconStyle = new ol.style.Style({
        image: new ol.style.Icon(/** @type {olx.style.IconOptions} */ ({
            anchor: [0.5, 0.5],
            anchorXUnits: 'fraction',
            anchorYUnits: 'fraction',
            opacity: 0.75,
            src: "pics/icon.png"
        }))
    });

    for (var i = 0; i < Object.keys(locationData).length; i++) {
        var occ = locationData[Object.keys(locationData)[i]];
        var feature = new ol.Feature({
            data:  occ,
            name: [occ['geolocation']['longitude'],occ['geolocation']['latitude']],
            geometry: new ol.geom.Point(ol.proj.transform(
                    [occ['geolocation']['longitude'],
                     occ['geolocation']['latitude']],
                    'EPSG:4326', 'EPSG:3857'))
        });

        feature.setStyle(iconStyle);
        features.push(feature);
    }
    var vectorSource = new ol.source.Vector({features: features});
    var vectorLayer = new ol.layer.Vector({source: vectorSource});
    map.addLayer(vectorLayer);
}


$(document).ready(function() {
    SEARCH_AJAX_FORM.initSearchForm();
});