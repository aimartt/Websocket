package com.aimartt.websocket;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/websocketChat")
public class ChatWebSocketTest {
	
	private static final Map<String, String> NAMES_MAP = new HashMap<String, String>();
	
	static {
		NAMES_MAP.put("1", "张老师");
		NAMES_MAP.put("2", "梅西");
		NAMES_MAP.put("3", "西罗");
	}
	
	@OnMessage
    public void onMessage(String message, Session session) 
    		throws IOException, InterruptedException {
		if (message == null || "".equals(message)) {
			return;
		}
		System.out.println("收到消息: " + message);
		Set<Session> sessionSet = session.getOpenSessions();
		String timestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
		Map<String, List<String>> map = session.getRequestParameterMap();
        String id = map.get("id").get(0);
        String withUserId = map.get("withUserId").get(0);
		for (Session s : sessionSet) {
			if (s.isOpen()) {
				Map<String, List<String>> currmap = s.getRequestParameterMap();
				String currId = currmap.get("id").get(0);
				 String currWithUserId = currmap.get("withUserId").get(0);
				if ((id.equals(currId) && withUserId.equals(currWithUserId)) || 
						(id.equals(currWithUserId) && withUserId.equals(currId))) {
					//互相聊天的两人才能收到消息
					s.getBasicRemote().sendText(NAMES_MAP.get(id) + "\t" + timestamp + "\n" + message);
				}
			}
		}
    }
	
	@OnOpen
    public void onOpen(Session session) {
        Map<String, List<String>> map = session.getRequestParameterMap();
        String id = map.get("id").get(0);
        String withUserId = map.get("withUserId").get(0);
        System.out.println("客户端已连接, sessionId=" + session.getId() + "，id=" + id + ", name=" + NAMES_MAP.get(withUserId));
    }

    @OnClose
    public void onClose (Session session) {
    	System.out.println("连接关闭, id=" + session.getId());
    }
    
}