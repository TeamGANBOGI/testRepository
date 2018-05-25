<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="java.util.*"%>
<%
	// 기존에 서버에 등록된 방 리스트를 받는다.
	ArrayList<String> roomList
	 = (ArrayList<String>)application.getAttribute("roomList");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Multicast</title>
<script src="<%=request.getContextPath()%>/resources/js/jquery-3.3.1.min.js"></script>
<style>
	.createRoom {
		display: none;
	}
</style>
</head>
<body>
	<h1>Multicast 란?</h1>
	<h3>하나의 네트워크 안의 여러 사용자 중
	   특정한 그룹에 속한 사용자끼리 소통하는 1:m의 통신 방식</h3>
	<p>Ex) 단체 카톡, 디스코드 톡, 게임 톡, 스카이프 등</p>
	<br>
	<form action="/test/multiChatRoom.do" method="post">
		<h3>참여할 채팅방 : 
		<select id="chatRoom" name="chatRoom">
		  <% for(String room : roomList) { %>
			<option value="<%=room%>"><%=room%></option>
		  <% } %>
		    <option value="newRoom">새로 개설</option>
		</select> </h3>
		
		<h3>사용할 이름 : <input type="text" name="chat_id" id="chat_id" /></h3>
		
		<!-- 새로운 채팅방을 개설할 경우 -->
		<h3 class="createRoom">방 제목 : <input type="text" 
		    name="roomName" id="roomName" /></h3>
		<button id="startBtn">참여하기</button><br>
	</form>
	
	<script>
		$('#chatRoom').on('change', function(){
			if($(this).val() == "newRoom"){
				$('#startBtn').text("개설하기");
				$(".createRoom").each(function(index, value){
					$(this).css('display','block');
				});
			} else {
				$('#startBtn').text("참여하기");
				$(".createRoom").each(function(index, value){
					$(this).css('display','none');
				});
			}
		});
	</script>
</body>
</html>



















