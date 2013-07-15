/*
 * Example: <input class="calendarInit" type="text" id="dogdate" readonly="readonly" />
 * Required_Attr: class="calendarInit" id="XXX"
 */
jQuery(function(){
	if(jQuery(".calendarInit").length > 0){
		jQuery(".calendarInit").each(function(){
			var inputId = jQuery(this).attr("id");
			var btId = inputId+"Trigger";
			var buttonHtml = '<input type="button" id="'+btId+'" value="选择"/>';
			jQuery(this).after(buttonHtml);
			Calendar.setup( {
				inputField : inputId, // ID of the input field
				ifFormat : "%Y-%m-%d %H:%M:%S", // the date format
				button : btId, // ID of the button
				showsTime : true,
				date : Calendar.initNewDate("0:0:0"),
				timeFormat : "24"
				});
		});
	}
});
