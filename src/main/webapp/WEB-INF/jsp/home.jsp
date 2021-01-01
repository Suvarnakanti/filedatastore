<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html lang="en">
   <head>
      <link rel="stylesheet" type="text/css"
         href="webjars/bootstrap/3.3.7/css/bootstrap.min.css" />
      <!-- 
         <spring:url value="/css/main.css" var="springCss" />
         <link href="${springCss}" rel="stylesheet" />
          -->
      <c:url value="/css/main.css" var="jstlCss" />
      <link href="${jstlCss}" rel="stylesheet" />
   </head>
   <body>
      <nav class="navbar navbar-inverse">
         <div class="container">
            <div class="navbar-header">
               <a class="navbar-brand" href="./">File Based Data Storage</a>
            </div>
            <div id="navbar" class="collapse navbar-collapse">
               <ul class="nav navbar-nav">
                  <c:choose>
                     <c:when test="${message=='create' || message=='save' || message=='home'}">
                        <li class="active"><a href="./create">Create</a></li>
                        <li><a href="./read">Read</a></li>
                        <li><a href="./delete">Delete</a></li>
                     </c:when>
                     <c:when test="${message=='read' || message=='fetch'}">
                        <li><a href="./create">Create</a></li>
                        <li class="active"><a href="./read">Read</a></li>
                        <li><a href="./delete">Delete</a></li>
                     </c:when>
                     <c:when test="${message=='delete' || message=='deleted'}">
                        <li><a href="./create">Create</a></li>
                        <li><a href="./read">Read</a></li>
                        <li class="active"><a href="./delete">Delete</a></li>
                     </c:when>
                  </c:choose>
               </ul>
            </div>
         </div>
      </nav>
      <div class="container">
         <div class="starter-template">
            <c:choose>
               <c:when test="${message=='create'}">
                  <form action="save" method="post">
                     <div style="overflow-x:auto;">
                        <table>
                           <tr>
                              <td>Enter File Name (Optional)</td>
                              <td><input type ="text" name="fileName"></td>
                           </tr>
                           <tr>
                              <td>Enter Key (Optional)</td>
                              <td><input type ="text" name="key" maxlength="22"></td>
                           </tr>
                           <tr>
                              <td>Enter Time-to-live [in seconds] (Optional)</td>
                              <td><input type ="number" name="timeToLive"></td>
                           </tr>
                           <tr>
                              <td>Enter Value (Mandatory)</td>
                              <td><input type ="text" name="value" required></td>
                           </tr>
                           <tr>
                              <td colspan="2"><input type ="submit" value="Save"></td>
                           </tr>
                        </table>
                     </div>
                  </form>
               </c:when>
               <c:when test="${message=='save'}">
                  <h2>${output} </h2>
               </c:when>
               <c:when test="${message=='read'}">
                  <form action="fetch" method="post">
                     <table>
                        <tr>
                           <td>Enter File Name</td>
                           <td><input type ="text" name="fileName" required></td>
                        </tr>
                        <tr>
                           <td>Enter Key (Optional)</td>
                           <td><input type ="text" name="key" maxlength="22"></td>
                        </tr>
                        <tr>
                           <td colspan="2"><input type ="submit" value="Fetch"></td>
                        </tr>
                     </table>
                  </form>
               </c:when>
               <c:when test="${message=='fetch'}">
                  <pre><h2>${output} </h2></pre>
               </c:when>
               <c:when test="${message=='delete'}">
                  <form action="deleted" method="post">
                     <table>
                        <tr>
                           <td>Enter File Name</td>
                           <td><input type ="text" name="fileName" required></td>
                        </tr>
                        <tr>
                           <td>Enter Key (Optional)</td>
                           <td><input type ="text" name="key" maxlength="22"></td>
                        </tr>
                        <tr>
                           <td colspan="2"><input type ="submit" value="Delete"></td>
                        </tr>
                     </table>
                  </form>
               </c:when>
               <c:when test="${message=='deleted'}">
                  <h2>${output} </h2>
               </c:when>
               <c:otherwise>
                  <h2>Welcome to Home Page</h2>
               </c:otherwise>
            </c:choose>
         </div>
      </div>
      <!-- /.container -->
      <script type="text/javascript"
         src="webjars/bootstrap/3.3.7/js/bootstrap.min.js"></script>
   </body>
</html>