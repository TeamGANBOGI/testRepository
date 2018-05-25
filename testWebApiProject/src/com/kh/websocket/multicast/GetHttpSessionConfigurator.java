package com.kh.websocket.multicast;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import javax.websocket.server.ServerEndpointConfig.Configurator;

// Configurator란
// 사용자 연결을 위한 websocket 객체를 
// 생성할 때 기본적으로 설정할 정보들을 
// 작성하는 클래스 

// Configurator 클래스를 상속 받을 경우
// modifyHandshake() 메소드를 통해 
// webSocket.Sessoin을 통해 전달할 데이터를 HttpSession과
// 연결하여 데이터를 담을 수 있다.
public class GetHttpSessionConfigurator extends Configurator{

	 @Override
	    public void modifyHandshake(ServerEndpointConfig config, 
	                                HandshakeRequest request, 
	                                HandshakeResponse response)
	    {
		 // config는 기존 설정 부분을 가져와서 
		 // 수정할 때 사용하는 객체 
		 // request : HttpServletRequest와 
		 // 	 	   동일한 역할을 수행하는 객체 
		 // response : HttpServletResponse와 
		 // 		    동일한 역할을 수행하는 객체 
	        config.getUserProperties().put("chat_id", (String)((HttpSession)request.getHttpSession()).getAttribute("chat_id"));
	        System.out.println("config : "+(String)((HttpSession)request.getHttpSession()).getAttribute("chat_id"));
	    }
	
}