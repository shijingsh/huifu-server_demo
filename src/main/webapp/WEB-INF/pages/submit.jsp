<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<body>
<div class="main-con">
	<form action="${url}" method="post" id="autoForm" name="autoForm" OnSubmit="true">
	<table>
		<tr>
     	  <td>业务代号:</td>
          <td >
              <input type="text" name="cmd_id" id="cmd_id" value="${cmdId}">
          </td>
          <td>商户客户号:</td>
          <td >
              <input type="text" name="mer_cust_id" id="mer_cust_id" value="${merCustId}">
          </td>
          <td>版本号:</td>
          <td>
              <input type="text" name="version" id="version" value="${version}">
          </td>
          <td>SIGN:</td>
          <td>
              <input type="text" name="check_value" id="check_value" value="${sign}">
          </td>

        </tr>	
	</table>
	</form>
</div>	

</body>

<script>
	window.onload=function(){
		document.getElementById("autoForm").submit();
	}

</script>
</html>
