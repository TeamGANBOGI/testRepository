package com.kh.websocket.unicast;

import javax.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

@ServerEndpoint("/unicast")
public class Unicast {
	// 연결한 사용자 리스트
	// 단 한 사용자가 2번 연결하면 안되기 때문에
	// Set을 통해 연결 정보에 대한 중복을 배재한다.
	private static Set<Session> clients
	 = Collections.synchronizedSet(
			 new HashSet<Session>());
	
	@OnOpen
	public void onOpen(Session session){
		// 서버에 연결한 시점에 동작하는 메소드
		System.out.println(session);
		
		// 기존 사용자 리스트에 새로 연결 요청이
		// 들어온 사용자를 추가한다.
		clients.add(session);
	}
	
	@OnMessage
	 public void onMessage(
			 String msg, Session session) throws IOException{
		// 서버로 데이터를 전송할 경우
		// 동작할 메소드
		
		System.out.println(msg);
		
		// 하나의 일 처리를 수행할 동안
		// 사용자 변경이 일어나면 안된다.
		// 즉, Nullpointer를 방지하기 위해서
		// 동기화 처리를 수행한다.
		
		synchronized(clients){
			// 현재 연결된 사용자 모두에게
			// 데이터를 전달한다.
			for(Session client : clients){
				
				// 전달한 당사자만 제외하고
				if(!client.equals(session)){
					
					// 실제 데이터를 전달하는 부분
					client.getBasicRemote()
					.sendText(msg);
				}
			}
		}
		
	}
	 
	@OnError
	public void onError(Throwable e){
		// 데이터 전달 과정에서 에러가
		// 발생할 경우 수행하는 메소드
		
		e.printStackTrace();
	}
	
	@OnClose
	public void onClose(Session session){
		clients.remove(session);
	}
	
}


















