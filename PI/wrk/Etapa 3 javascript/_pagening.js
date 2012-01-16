//Watch More Button
//Infinitive Scroll
$(
	function()
	{
		//clear the Navigation DIV, so we could perform our custom display
		$("#PageNavigationDiv").empty(); 
		
		//create new Page View
		$("#PageNavigationDiv").append("<div id ='WatchMoreBotton'><label >Watch More</label></div>");
		//'Button' Definition
		$("#WatchMoreBotton")
		.css('height','50px').css('width','400px')
		.css("border", "2px solid red").css("text-align","center")
		.css("display"," table-cell").css("vertical-align","middle")							
		;
		//Button Function
		$("#WatchMoreBotton")
		.click(function(e)
			{
				e.preventDefault();
				//In this stage an ajax request for more results
				$('#CufList').append("<li><a href='/CUF/Official/Detail/PE'>Probabilidade e Estatistica</a></li>");
				//Button scroll to bottom.
				$('#CufListDiv').animate({scrollTop: $('.span4').innerHeight()},1000);
			});
		//Cuf List CSS
		$("#CufListDiv")
		.css('height','100px').css('width','400px')
		.css('overflow','auto').css('border','1px solid gray');
	}
);
			
