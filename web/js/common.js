//验证小数
function  isNumeric(p)  { //   /^\d+$/.test(str) 
	if(/^(\+|-)?(0|[1-9]\d*)(\.\d*[0-9])?$/.test(p))
		return true ;
	else
		return false ;
}
//判断输入的字符是否为Integer类型，是返回true，否则返回false
function isInteger(p){
	if(/^(\+|-)?(0|[1-9]\d*)(\d*[1-9])?$/.test(p))
		return true ;
	else
		return false ;
}
function avoidRepeatSubmit(obj){
	obj.disabled=true;
	//obj.onclick= function () {} 
	return false;
}
function avoidRepeatSubmitByElementId(id){
	document.getElementById(id).disabled=true;
	//document.getElementById(id).onclick= function () {} 
	return false;
}
function recoverOp(id){
	document.getElementById(id).disabled=false;
	//document.getElementById(id).onclick= function () {avoidRepeatSubmit(this);javascript:onSubmit();return false;} 
}
/*
 * pageid 页面的html元素id
 * orderIndex 页面上排序的位置
 * content 所在tr里的所有html代码
 */
ElemEntityOnPage = function(pageid , orderIndex , content){
	this.pageid = pageid;
	this.orderIndex = orderIndex;
	this.content = content;		
}
//重绘表格
function redrawElemTable(array,tableid){
	for(var i = 0 ;i<array.length; i ++){		
		$("#"+tableid).append(array[i].content);
	}
}
//表格行上移
function up(obj,elemheader,tableid){
	var elems = $("#"+tableid).find("[id^="+elemheader+"]");
	var array = new Array();
	$.each(elems , function(i,elem){
		array.push(new ElemEntityOnPage(elem.id , i , elem));
	});
	var tmpEntity = new ElemEntityOnPage();
	var entityIndex = 0;
	for(var i = 0 ; i < array.length ;i++){
		if($(obj).parent().parent().attr("id") == array[i].pageid){
			if(i == 0){
				alert("已到达第一个");
				return false;
			}else{
				entityIndex = i;		
				break;
			}
		}
	}
	if(entityIndex>0){
		tmpEntity = array[entityIndex-1];
		array[entityIndex-1] = array[entityIndex];
		array[entityIndex] = tmpEntity;
	}
	redrawElemTable(array,tableid);
}
//表格行下移
function down(obj,elemheader,tableid){
	var elems = $("#"+tableid).find("[id^="+elemheader+"]");
	array = new Array();
	$.each(elems , function(i,elem){
		array.push(new ElemEntityOnPage(elem.id , i , elem));
	});
	var tmpEntity = new ElemEntityOnPage();
	var entityIndex = -1;
	for(var i = 0 ; i < array.length  ;i++){
		if($(obj).parent().parent().attr("id") == array[i].pageid){
			if(i == array.length-1){
				alert("已到达最后一个");
				return false;
			}else{
				entityIndex = i;		
				break;
			}
		}
	}
	if(entityIndex >= 0){
		tmpEntity = array[entityIndex+1];
		array[entityIndex+1] = array[entityIndex];
		array[entityIndex] = tmpEntity;
	}
	redrawElemTable(array,tableid);
}
//删除行 有特殊删除需要可重写
function removeRow(obj){
	var row = $(obj.parentNode.parentNode);
	if(confirm("确认删除这行?")){
		row.remove();
	}
}
/**
 * 全选指定元素下的复选框
 * @param elemId
 * @param ckbname
 * @return
 */
function selectAllCkb(elemId,ckbname){
	var ckbs = $("#"+elemId).find("[name="+ckbname+"]");
	$.each(ckbs,function(i,ckb){
		$(ckb).attr("checked",true);
	});
}
/**
 * 全不选指定元素下的复选框
 * @param elemId
 * @param ckbname
 * @return
 */
function selectNotAllCkb(elemId,ckbname){
	var ckbs = $("#"+elemId).find("[name="+ckbname+"]");
	$.each(ckbs,function(i,ckb){
		$(ckb).removeAttr("checked");
	});
}
/**
 * parse String to Date
 * parseDate('2006-1-1') return new Date(2006,0,1)
 * parseDate(' 2006-1-1 ') return new Date(2006,0,1)
 * parseDate('2006-1-1 15:14:16') return new Date(2006,0,1,15,14,16)
 * parseDate(' 2006-1-1 15:14:16 ') return new Date(2006,0,1,15,14,16);
 * parseDate('2006-1-1 15:14:16.254') return new Date(2006,0,1,15,14,16,254)
 * parseDate(' 2006-1-1 15:14:16.254 ') return new Date(2006,0,1,15,14,16,254)
 * parseDate('invalid format string') retrun null
*/
function parseDate(str) {
    if (typeof str == 'string') {
        var results = str.match(/^ *(\d{4})-(\d{1,2})-(\d{1,2}) *$/);
        if (results && results.length > 3) {
            return new Date(parseInt(results[1], 10),parseInt(results[2], 10) -1,parseInt(results[3], 10));
        }
        results = str.match(/^ *(\d{4})-(\d{1,2})-(\d{1,2}) +(\d{1,2}):(\d{1,2}):(\d{1,2}) *$/);
        if (results && results.length > 6) {
            return new Date(parseInt(results[1], 10),parseInt(results[2], 10) -1,parseInt(results[3], 10),parseInt(results[4], 10),parseInt(results[5], 10),parseInt(results[6], 10));
        }
        results = str.match(/^ *(\d{4})-(\d{1,2})-(\d{1,2}) +(\d{1,2}):(\d{1,2}):(\d{1,2})\.(\d{1,9}) *$/);
        if (results && results.length > 7) {
            return new Date(parseInt(results[1], 10),parseInt(results[2], 10) -1,parseInt(results[3], 10),parseInt(results[4], 10),parseInt(results[5], 10),parseInt(results[6], 10),parseInt(results[7], 10));
        }
    }
    return null;
}

function StringBuffer(str) {                                                                                                                                                                   
    this._strings = new Array();
    this.append(str);
};
StringBuffer.prototype.append = function(str) {
    this._strings.push(str);
    return this;
};
StringBuffer.prototype.toString = function() {
    var str = (arguments.length == 0) ? '' : arguments[0];
    return this._strings.join(str);
};