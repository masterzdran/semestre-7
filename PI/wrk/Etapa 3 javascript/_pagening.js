/**
	Nuno Cancelo
*/
/*	
	Definition of infinitive scroll
	-------------------------------
*/
$(changeToScroll = function()
	{
		//clear the Navigation DIV, so we could perform our custom display
		$("#PageNavigationDiv").empty(); 
		
		//create new Page View
		$("#PageNavigationDiv").append("<div id ='WatchMoreBotton'><label >Watch More</label></div>");
		
		//'Button' Definition CSS
		$("#WatchMoreBotton")
		.css('height','50px').css('width','400px')
		.css("border", "2px solid red").css("text-align","center")
		.css("display"," table-cell").css("vertical-align","middle")							
		;
		
		//Button Function
		$("#WatchMoreBotton")
		.click(function(e)
			{
				alert ($('#ItemsOrderDiv :selected').val()+"--"+$('#ItemsNbrDiv :selected').val());
				e.preventDefault();
				for (var t =0;t<$('#ItemsNbrDiv :selected').val();t++){
					$('#CufList').append("<li><a href='/CUF/Official/Detail/PE'>Probabilidade e Estatistica</a></li>");
				}
				//Button scroll to bottom.
				$('#CufListDiv').animate({scrollTop: $('.span4').innerHeight()},1000);
			});
		//Cuf List CSS
		$("#CufListDiv")
		.css('height','300px').css('width','400px')
		.css('overflow','auto').css('border','1px solid gray');
		return this;
	}
	
);
/*	
	Definition of Pages
	-------------------------------
*/

function call(param){
	return function(){
			var url = "/CUF/Official/Page?pagenr=" + param + "&itemsnr=" + $('#ItemsNbrDiv :selected').val() + "&order=" + $('#ItemsOrderDiv :selected').val();
		alert( url	);
	}
}
$(changeToPages = function ()
	{
		var pages = 10 ;
		//clear the Navigation DIV, so we could perform our custom display
		$("#PageNavigationDiv").empty(); 
		
		//create new Page View
		//$("#PageNavigationDiv").append("<div id ='WatchMoreBotton'><label >Watch Page</label></div>");
		
		var pag = document.createElement("DIV");
		pag.id = "WatchMoreBotton";
		for (var i = 0; i< pages;++i){
			var p = document.createElement("LABEL");
			var t = "p"+i;
			var c = i;
			p.id = t;
			p.title = "Page n. "+i  ;
			p.onclick =  call(i);
			p.appendChild(document.createTextNode("  "+c+"  "));

			pag.appendChild(p);
			
		}
		$("#PageNavigationDiv").append(pag);
		//'Button' Definition CSS
		$("#WatchMoreBotton")
		.css('height','50px').css('width','400px')
		.css("border", "2px solid red").css("text-align","center")
		.css("display"," table-cell").css("vertical-align","middle");
		
		/*for (var i = 0; i< pages;++i){
			$("#p"+i).click(call(i));
		}*/
		//Cuf List CSS
		$("#CufListDiv")
		.css('height','300px').css('width','400px')
		.css('overflow','auto').css('border','1px solid gray');
		return this;
	}
);