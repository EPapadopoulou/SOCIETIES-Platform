<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="xc" uri="http://java.sun.com/jsp/jstl/core"  %> 
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
<title>Societies</title>
<meta charset="utf-8">
<link rel="stylesheet" href="css/style.css">
<link rel="stylesheet" href="css/dialog.css">
<script>!window.jQuery && document.write('<script src="js/jquery-v1.8.1.js"><\/script>')</script>
<!--[if IE]>
<script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script><![endif]--> 
<!--[if lt IE 7]>
<link rel="stylesheet" type="text/css" media="all" href="css/ie7.css" />
<script src="http://ie7-js.googlecode.com/svn/version/2.1(beta4)/IE7.js"></script><![endif]-->
<!--[if IE 7]>
<link rel="stylesheet" type="text/css" media="all" href="css/ie7.css" /><![endif]-->
<!-- Menu -->
<script src="js/webmenu_nav.js"></script>
</head>
<body>
<div id="wrapper" class="clearfix">
<div id="container" class="container_12 clearfix">
<!-- Header -->
<header id="header" class="grid_12">
<div class="login-form">   
</div>
<!-- Logo -->
<h1 id="logo" class="logo-pos">
<a href="index.html"><img src="images/societies_logo.jpg" alt="Logo" /></a>
</h1>
<!--WebMenu -->
<nav id="webmenu_nav">
<ul id="navigation" class="grid_8">
<li><a href="myProfile.html"><br />My Account</a>
<ul class="sub-menu"> 
<li><a href="myProfile.html">My Profile</a></li>
<li><a href="profilesettings.html">Profile Settings</a></li>
<li><a href="settings.html">Security Settings</a></li>
<li><a href="privacysettings.html">Privacy Settings</a></li>
</ul>
</li>
<li><a href="your_installed_apps.html"><br />Apps</a>
</li>
<li><a href="your_societies_friends_list.html"><br />Friends</a>
<ul class="sub-menu"> 
<li><a href="your_societies_friends_list.html">Your Friends</a></li>
<li><a href="suggested_societies_friends_list.html">Suggested Friends</a></li>
</ul>
</li>
<li><a href="your_communities_list.html"><br />Communities</a>
<ul class="sub-menu"> 
<li><a href="your_communities_list.html">Your Communities</a></li>
<li><a href="manage_communities.html">Manage your Communities</a></li>
<li><a href="create_community.html">Create a Community</a></li>
<li><a href="your_suggested_communities_list.html">Suggested Communities</a></li>
</ul>
</li>
<li><a href="index.html"><br />Home</a></li>
</ul>
</nav><!-- #webmenu -->
<div class="clear"></div>
</header><!-- #header -->
<div class="hr grid_12 clearfix">&nbsp;</div>
<section  class="grid_12">
<section>
<div class="breadcrumbs"><a href="">Home</a> / <a href="community_profile.html?cisId=${cisInfo.getCommunityJid()}">${cisInfo.getCommunityName()}</a></div>
</section>
<div class="websearchbar">
<div class="websearchtitle">
<h4 class="profile_title">${cisInfo.getCommunityName()} Profile</h4>
</div>
<div class="groupsearch">
<form action="" class="websearch-form frame nobtn rsmall">
<input type="text" name="search" class="websearch-input" placeholder="Search for Friends..." />
</form>
</div>
</div>
</section>
<!-- Left Column -->
<section id="left_col" class="grid_8">
<section>
<figure class="gravatar">
<img alt="" src="images/webcommunity_pic_sample1.jpg" height="48" width="48" />
<a class="furtherinfo-link" href="delete_community.html?cisId=${cisInfo.getCommunityJid()}">REMOVE</a>
</figure>
<div class="keyinfo_content">
<div class="clearfix">
<cite class="author_name"><a href="friend_profile.html?cssId=${cisInfo.getOwnerJid()}">Owner</a></cite>
</div>
<div class="keyinfo_text">
<p>${cisInfo.getDescription()}</p>
</div>
<p><strong>Activity Feed:</strong></p>
<!-- Unordered -->

<xc:if test="${acitivityAddError != 'null'}">
	<div class="error">${acitivityAddError}</div>
</xc:if>

<form:form method="POST" action="community_profile.html" commandName="activityForm" name="AddActivityForm">
		<form:errors path="*" cssClass="errorblock" element="div" />
		
		<table id="addActivityFormInputs">
		<tr>
		<td><form:input path="object" defaultValue="write your activity here"/></td>
		<td><form:errors path="object" cssClass="error" /></td>
		</tr>
		
		<tr>
			<td><form:input path="cisId" style="display:none;" value="${cisInfo.getCommunityJid()}"/></td>
			<td><form:errors path="cisId" cssClass="error" /></td>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td><form:input path="verb" style="display:none;" value="posted"/></td>
			<td><form:errors path="verb" cssClass="error" /></td>
			<td>&nbsp;</td>
		</tr>
			<tr>
				<td colspan="3"><input id="postActButton" type="button" value="PostActivity"/></td>
			</tr>
		</table>
		
</form:form>

<ul>
<xc:forEach var="activity" items="${activities}">
		<li>
${activity.getActor()}  ${activity.getVerb()}  ${activity.getObject()}  at  ${activity.getTarget()} 
		</li>
</xc:forEach>
</ul>

<ul>
<li>List item example</li>
<li>List item example
</li>
<li><a class="tooltip" href="#" title="">List item example</a></li>
</ul>
<article class="post">
&nbsp;
</article>
<p><strong>Additional Info:</strong></p>
<!-- Unordered -->
<ul>
<li>List item example</li>
<li>List item example
</li>
<li><a class="tooltip" href="#" title="">List item example</a></li>
</ul>
<article class="post">
&nbsp;
</article>
<p><em>Further Information:</em></p> 
<p><strong>Location:</strong> Dublin, Ireland - 53°20' 52" N 6°1' 3" W</p>
<p><strong>Related:</strong> Details</p>
<p><strong>Other Title:</strong> Information</p>
</div>
<div class="hr dotted clearfix">&nbsp;</div>
</section>
</section>
<!-- Right Column / Sidebar -->
<aside id="sidebar_right" class="grid_4">
<div class="sidebar_top_BG"></div>		
<div class="hr dotted clearfix">&nbsp;</div>	
<section>
<header>
<h3>Other Apps</h3>
</header>
<ul class="sidebar">
<li><a href="">App Name</a></li>
<li><a href="">App Name</a></li>
<li><a href="">App Name</a></li>
<li><a href="">App Name</a></li>
<li><a href="">App Name</a></li>
</ul>
</section>
<section>
<header>
<h3>App Activity</h3>
</header>
<div class="hr dotted clearfix">&nbsp;</div>
<ul class="contact_data">
<li>Lorem ipsum dolor sit amet, consectetur adipiscing elit.</li>
</ul>
<ul class="contact_data">	
<li>Other Link - <a href="#">Link 1</a></li>
<li>Other Link - <a href="#">Link 2</a></li>
</ul> 
</section>
<div class="hr dotted clearfix">&nbsp;</div>
<div class="sidebar_bottom_BG"></div>
</aside>
<div class="hr grid_12 clearfix">&nbsp;</div>
</div>

<!-- Button Script -->
	<script type="text/javascript">
�
$(document).ready(function(){
	//startup functionality
	
 
 var i = 0;

 document.getElementById('postActButton').onclick = function() {
	 document.AddActivityForm.submit();
	 };
 

 


});// end of $(document).ready(function()
	�
</script>

<!-- Footer -->
<footer class="container_12 clearfix">
<section class="footer">
<p class="footer-links">
<span><a href="termsofuse.html">Terms of Use</a> | <a href="disclaimer.html">Disclaimer</a> | <a href="privacy.html">Privacy</a> | <a href="help.html">Help</a> | <a href="about.html">About</a></span>
<a class="float right toplink" href="#">top</a>
</p>
</section>
</footer>
</div> 	
</body>
</html>