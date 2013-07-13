/*
* 绑定改变彩期类型参数
* changeElementId  改变的元素id
* phaseSelectElementId   级联的彩期下拉列表元素id
* url  获得彩期的url地址
* allPhaseFlag 是否显示所有彩期的下拉选项
*/
function bindChangeLotteryType(changeElementId,phaseSelectElementId,url,allPhaseFlag,phaseSelectValue){
	$("#"+changeElementId).change(function(){
		var lotteryTypeValue = $("#"+changeElementId).val();
		getLastPhaseList(lotteryTypeValue,phaseSelectElementId,url,allPhaseFlag,phaseSelectValue);
	});
}
/*
* 获得最新彩期列表
* lotteryTypeValue  彩期类型值
* phaseSelectElementId    级联彩期下拉列表元素id
* url  获得彩期的url地址
* allPhaseFlag 是否显示所有彩期的下拉选项
* phaseSelectValue  已选择的彩期下拉列表的值
*/
function getLastPhaseList(lotteryTypeValue,phaseSelectElementId,url,allPhaseFlag,phaseSelectValue){
	if(parseInt(lotteryTypeValue,10)>0){
		$.ajax({
			url:url,
			data:{action:"getLastPhaseList","lotteryTypeValue":lotteryTypeValue},
			type:"post",
			dataType:"json",
			success:function(data){
				var phases = data.phases;
				bindPhaseList(phases,phaseSelectElementId,allPhaseFlag,phaseSelectValue);
			}
		});
	}else{
		if(allPhaseFlag==1){
			$("#"+phaseSelectElementId).empty();
			var option = $("<option>全部</option>");
			$(option).val(-1);
			$("#"+phaseSelectElementId).append(option);
		}else{
			$("#"+phaseSelectElementId).empty();
			var option = $("<option>无相应彩期</option>");
			$(option).val(-1);
			$("#"+phaseSelectElementId).append(option);
		}
	}
}
//绑定彩期列表数据到指定下拉下拉列表元素
function bindPhaseList(phases,phaseSelectElementId,allPhaseFlag,phaseSelectValue){
	$("#"+phaseSelectElementId).empty();
	if(phases.length>0){
		if(allPhaseFlag==1){
			var allOption = $("<option>全部</option>");
			$(allOption).val(-1);
			$("#"+phaseSelectElementId).append(allOption);
		}
		$.each(phases,function(i,phase){
			var option = $("<option>"+phase.phase+"</option>");
			$(option).val(phase.phase);
			if(phase.phase==phaseSelectValue){
				$(option).attr("selected","selected");
			}
			$("#"+phaseSelectElementId).append(option);
		});
	}else{
		if(allPhaseFlag==1){
			var allOption = $("<option>全部</option>");
			$(allOption).val(-1);
			$("#"+phaseSelectElementId).append(allOption);
		}else{
			var option = $("<option>无相应彩期</option>");
			$(option).val(-1);
			$("#"+phaseSelectElementId).append(option);
		}
		
	}
}


/*******************↑小雨天出品***********************
 * 
 *******************↓CS出品**************************/

AppointPhaseParam = function(changeElementId,phaseSelectElementId,url,allPhaseFlag,phaseSelectValue,count,project)
{
    this.changeElementId=changeElementId;
    this.phaseSelectElementId=phaseSelectElementId;
    this.url=url;
    this.allPhaseFlag = allPhaseFlag;
    this.phaseSelectValue = phaseSelectValue;
    this.count = count;
    this.project = project;
}

/*
* 绑定改变彩期类型参数
* changeElementId  改变的元素id
* phaseSelectElementId   级联的彩期下拉列表元素id
* url  获得彩期的url地址
* allPhaseFlag 是否显示所有彩期的下拉选项
*/
function changeLotteryType(param){
	if(param == null)return;
	//彩种列表绑定change事件
	$("#"+param.changeElementId).change(function(){
		var lotteryTypeValue = $("#"+param.changeElementId).val();
		var project = param.project;
		if(typeof(project) == "undefined"){
			project = "";
		}

		param.phaseSelectValue = "";// 切换彩种要清除当前期,保证读取当前期
		getAppointPhaseList(lotteryTypeValue,param.phaseSelectElementId,param.url,param.allPhaseFlag,param.phaseSelectValue,param.count);
		/* 废弃原因,由后台查询当前期,不再前台显示调用 note by lm 2011-01-26
		if(param.phaseSelectValue==null || param.phaseSelectValue==""){
			var url = project+"/lottery/phase.do?action=getCurrentPhase";
			jQuery.post(url, {"lotteryTypeValue":lotteryTypeValue}, function(data){
				param.phaseSelectValue = data.phase;
				getAppointPhaseList(lotteryTypeValue,param.phaseSelectElementId,param.url,param.allPhaseFlag,param.phaseSelectValue,param.count);
			}, "json");
		} else {
			getAppointPhaseList(lotteryTypeValue,param.phaseSelectElementId,param.url,param.allPhaseFlag,param.phaseSelectValue,param.count);
		}*/
	});
}
/*
* 获得指定彩期前后N期列表
* lotteryTypeValue  彩期类型值
* phaseSelectElementId    级联彩期下拉列表元素id
* url  获得彩期的url地址
* allPhaseFlag 是否显示所有彩期的下拉选项
* phaseSelectValue  已选择的彩期下拉列表的值
*/
function getAppointPhaseList(lotteryTypeValue,phaseSelectElementId,url,allPhaseFlag,phaseSelectValue,count){
	$("#"+phaseSelectElementId).unbind("change");
	if(parseInt(lotteryTypeValue,10)>0){
		$.ajax({
			url:url,
			data:{"lotteryTypeValue":lotteryTypeValue,"assignPhaseNo":phaseSelectValue,"count":count},
			type:"post",
			dataType:"json",
			success:function(data){
				var phases = data.phases;
				var assignPhaseNo = data.assignPhaseNo;
				bindPhaseList(phases,phaseSelectElementId,allPhaseFlag,assignPhaseNo);
				//彩期下拉绑定change事件
				$("#"+phaseSelectElementId).bind('change',function(){
					//var lotteryTypeValue = $("#"+param.changeElementId).val();
					var phaseSelectValue = $("#"+phaseSelectElementId).val();
					if(phaseSelectValue != null && parseInt(phaseSelectValue,10)>0){
						getAppointPhaseList(lotteryTypeValue,phaseSelectElementId,url,allPhaseFlag,phaseSelectValue,count);
					}
				});
			}
		});
	}else{
		if(allPhaseFlag==1){
			$("#"+phaseSelectElementId).empty();
			var option = $("<option>全部</option>");
			$(option).val(-1);
			$("#"+phaseSelectElementId).append(option);
		}else{
			$("#"+phaseSelectElementId).empty();
			var option = $("<option>无相应彩期</option>");
			$(option).val(-1);
			$("#"+phaseSelectElementId).append(option);
		}
	}
}
