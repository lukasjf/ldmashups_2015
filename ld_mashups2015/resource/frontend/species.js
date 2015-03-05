function getSpeciesInfo(speciesID){
	$.ajax({
	       url: 'http://localhost:9998/api/species?species='+speciesID,
	       type: 'GET',
	       dataType: 'application/json',
	       complete: function(data){
	    	   processSpeciesData($.parseJSON(data.responseText));
	    	  
	       }
	})
}

function processSpeciesData(speciesJSON){
	  var species = speciesJSON['results']['bindings'][0];
	  $('#scientificName').empty().append('<h1>'+species['scientificName']['value']+'</h1>');
	  $('#abstract').empty().append(species['abstract']['value']);
	  
	  addLinksToSpecies(species);
	  addImagesToSpecies(species);
	  addMediaToSpecies(species);
	  addTaxonomyToSpecies(species);
}

function addLinksToSpecies(species){
	$('div#speciesLinks').empty();
	  $.each(species['equivalentWebpages'][0], function(i, item) {
	   	$("<a href=\""+item['equivalentWebpages']['value']+ "\">"+item['equivalentWebpages']['value']+"</a>")
	   		.appendTo("div#speciesLinks");
	   	$("<br></br>").appendTo("div#speciesLinks");
	  });
	  $("")
}

function addImagesToSpecies(species){
	$('#slider1_container').remove();
	  $('<div id="slider1_container" style="position: relative; top: 0px; left: 0px; width: 600px; height: 600px;"><div id="speciesImages" u="slides" style="cursor: move; position: absolute; overflow: hidden; left: 0px; top: 0px; width: 600px; height: 300px;"></div></div>').appendTo('#speciesdiv');
	  $.each(species['imageUrls'][0], function(i, item) {
	   	$('<div><img class="speciesImage" src="'+item['imageUrl']['value']+ '"></div>').appendTo("div#speciesImages")
	  });
	  
	   jQuery(document).ready(function ($) {
	        var options = { $AutoPlay: true };
	        var jssor_slider1 = new $JssorSlider$('slider1_container', options);
	   });
}

function addMediaToSpecies(species){
	  $('div#speciesVideos').empty();
	  $.each(species['videoUrls'][0], function(i, item) {
	   	$('<video width="320" height="240" controls>' +
	   		'<source src="'+item['videoURL']['value']+'" type=\"video/ogg\"></video>')
	   		.appendTo("div#speciesVideos");
	  });
}
    	
function addTaxonomyToSpecies(species){
	$('#taxondiv')
		.empty()
		.append('<button type="button" class="btn btn-info" data-toggle="collapse" data-target=".taxon">Taxonomy</button>')
		.append('<a id="kingdom" class = "collapse taxon" target="_blank" href="'+species['kingdom']['value']+'">Kingdom</a>')
		.append('<a id="phylum" class = "collapse taxon" target="_blank" href="'+species['phylum']['value']+'">Phylum</a>')
		.append('<a id="class" class = "collapse taxon" target="_blank" href="'+species['class']['value']+'">Class</a>')
		.append('<a id="order" class = "collapse taxon" target="_blank" href="'+species['order']['value']+'">Order</a>')
		.append('<a id="family" class = "collapse taxon" target="_blank" href="'+species['family']['value']+'">Family</a>')
		.append('<a id="genus" class = "collapse taxon" target="_blank" href="'+species['genus']['value']+'">Genus</a>')
		.append('<a id="species" class = "collapse taxon" target="_blank" href="'+species['scientificName']['value']+'">Species</a>');
}