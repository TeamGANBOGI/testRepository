package com.kh.websocket.multicast;

import java.io.IOException;
import java.util.*;

import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.kh.websocket.controller.MulticastViewServlet;
//
// value : 서버에 접속하기 위한 url 주소 경로
// {value} : 연결 주소 중 변경될 소지가 있는 부분
//
// 이후  해당 값을 @PathParam 이라는 
// 어노테이션을 통해 해당 값을 사용할 수 있다.
// configurator : 서버 설정에 관한 부분으로 
// 			서버 개통시에 기본적으로 해야할 설정 
//			정보들을 담는 객체 
@ServerEndpoint(value="/multicast/{chatRoom}", configurator=GetHttpSessionConfigurator.class)
public class Multicast {
	
	// 각 방 별로 사용자들의 정보를 담기 위한 
	// Map 객체를 선언하여 key로 
	// 사용자 리스트를 Set<Session> 컬렉션 객체로 
	// 담아서 서버 사용자들의 접속 정보를 통합 관리하기 위한 객체이다. 
	
   private static Map<String, Set<Session>> chatRooms
     = (Map<String,Set<Session>>)Collections.synchronizedMap(new HashMap<String, Set<Session>>());
   
   // 각 방별로 어떤 사용자들이 있는지 리스트를 출력하기 위한 메소드 생성 
   public static Set<Session> getUserList(String room){
       // 사용자 리스트의 정보를 가져오는 객체
	   Set<Session> userList = chatRooms.get(room); 
       
       // 만약에 사용자 정보가 없다면
	   // 새로 생성하여 사용자를 받을 수 있다록
	   // 객체를 생성해준다.
       if(userList == null) {
    	  // 첫번재 사용자도 받을 수 있는 
    	  // set 객체를 선언하여
    	  // userList 정보를 등록한다.
          userList = Collections.synchronizedSet(new HashSet<Session>());
          chatRooms.put(room, userList);
       }
       return userList; 
    }
   
   // 연결하려는 사용자의 정보와 접속한 방의 이름을 받아서 
   // 해당 방의 사용자 리스트에 갱신을 해주어야 한다.
    @OnOpen
    public void onOpen(EndpointConfig config, Session session,
    		@PathParam("chatRoom") String room) {
    	// EndpointConfig : 기본 설정을 적용하겠다.
    	// Session : 현재 접속자의 소켓 정보를 담은 연결 객체 (not HttpSession)
    	// @PathParam : 동적 url 경로를 통해 접근할 경우 해당 경로 부분을 변수로 활용할 수 있게 매개변수로 
    	// 				변경하여 받을 수 있는 어노테이션 
    	
    	// session.getUserProperties()
    	// 웹 소켓 세션에 사용자가 별도로 추가하거나 수정할 수 있는 
    	// 설정 부분을 가져올 때 사용하는 메소드
    	
    	// getUserProperties()
    	// 해당 설정을 통해 
    	// 웹 소켓 session 내부에 
    	// 사용자의 ID와 해당 방의 이름을 
    	// 저장하여 이후에 꺼내어 사용할 수 있게 등록한다.
       session.getUserProperties().put("chat_id", config.getUserProperties().get("chat_id"));
       
       session.getUserProperties().put("room", room);
       
       // 기존의 사용자 리스트를 받는다.
       Set<Session> userList = getUserList(room);
       
       // 현재 접속한 사용자의 세션정보를 추가한다. 
       userList.add(session);
       
       System.out.println(session);
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
    	// 현재 사용자가 보낸 정보를
    	// 같은 방의 다른 사용자들에게 
    	// 전달하는 메소드 
    	
    	// 현재는 사용하지 않지만 이후 확장가능성 
    	// 즉, 나중에 사용자 리스트를 표현하거나 
    	// 어떤 사용자가 보낸 것인지 표현할 때 사용할 수 있으므로 
    	// 코드를 작성 해 놓은 부분
      String chat_id = (String) session.getUserProperties().get("chat_id");
      
      // 현재 데이터를 보낸 사용자의 방 정보를 받아서 
      // 같은 방에 있는 사용자들에게 해당 정보를 전달하기
      // 위한 객체 
      String room = (String) session.getUserProperties().get("room");
      Set<Session> userList = getUserList(room);
      System.out.println("서버에서 받은 메세지 : "+message);
      System.out.println(userList);
      
      // 같은 방 안의 사용자들에게 데이터를 뿌려주는 
      // forEach 메소드 작성 
      userList.stream().forEach( x -> {
    	 // 람다식 사용부 : 
    	 // 기존 절차지향 언어가 가지는 
    	 // 특성인 익명 함수 구현부분을 
    	 // 객체 지향언어에서도 흉내내기 위한 
    	 // 소스 코드 작성 기술
    	 // 쉽게 이해하기 위해 javaScript의
    	 // function(x){ ... } :: (x) -> { ... }
    	 // 를 생각해보자 !! 
    	  
    	 // x : sessoin 
         try {
        	 // 실제 데이터를 전달하는 로직 부분 
            if(!x.equals(session))
               x.getBasicRemote().sendText(message);
         } catch (IOException e) {
            e.printStackTrace();
         }
      });
    }
    
    @OnClose
    public void onClose(Session session) {
       // 해당하는 방의 사용자 리스트를 가져와서 
       // 현재 사용자를 제거한다. 
       String room = (String)session.getUserProperties().get("room");
       Set<Session> userList = getUserList(room);
       userList.remove(session);
       
       // 만약 방 안에 사용자가 한 명도 없다면
       // 채팅방을 폐쇄한다. 
       if(userList.size() == 0)  MulticastViewServlet.roomList.remove(room);
    }
    
    @OnError
    public void onError(Throwable e) {
       e.printStackTrace();
    }
}
