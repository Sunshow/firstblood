/*
Copyright (c) 2003-2010, CKSource - Frederico Knabben. All rights reserved.
For licensing, see LICENSE.html or http://ckeditor.com/license
*/

CKEDITOR.editorConfig = function( config )
{
	// Define changes to default configuration here. For example:
	config.language = 'zh-cn';
	config.enterMode = CKEDITOR.ENTER_BR;
	config.shiftEnterMode = CKEDITOR.ENTER_P;
	config.skin = 'kama';
	config.toolbar = "MyToolbar";  //指定默认工具栏
	 	    //声明一个工具栏     
    config.toolbar_MyToolbar =    
       [['Format','Font','FontSize','-','TextColor','BGColor','-','Bold','Italic','Underline',
      'Strike','Subscript','Superscript','NumberedList','BulletedList'],
         '/',
        ['Undo','Redo','Cut','Copy','Paste'],
        ['JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock','-',
              'Outdent','Indent'],
        ['Link','Unlink','Anchor','Image','Flash','Table',
          'HorizontalRule','SpecialChar'],
       ['Source','-']
       ];
     //加入中文字体
    config.font_names='宋体/宋体;黑体/黑体;仿宋/仿宋_GB2312;楷体/楷体_GB2312;隶书/隶书;幼圆/幼圆;' + config.font_names;
    //config.extraPlugins = 'insertcode';
};
