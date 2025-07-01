$('document').ready(function(){

	/* Stops a collapse event from propagating if the clicked element or any parent has .avoid-collapse */
	$(".collapse").on("show.bs.collapse hide.bs.collapse", function(e) {
		var source = event.target || event.srcElement;
		if($(source).hasClass('avoid-collapse') || $(source).parents('.avoid-collapse').length){
			e.preventDefault();
			e.stopPropagation();
		}
	});
	
});