package test.src.websocket;

//import java.awt.List;
import java.io.IOException;
//import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

//import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.*;

//@ApplicationScoped
@ServerEndpoint("/hello")
public class GameWebSocket {
	private static Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());
	private static Map<String, String> locationOfUsers = Collections.synchronizedMap(new HashMap<String, String>());
		
	@OnOpen
    public void open(Session session) {	
		for(Session peer:peers){			
			try {				
				peer.getBasicRemote().sendText("Create," + session.getId());
				session.getBasicRemote().sendText("Create," + peer.getId() + "," + locationOfUsers.get(peer.getId()));
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//Add user to all connections
		peers.add(session);
	}

	@OnClose
    public void close(Session session) {
		peers.remove(session);
	}

	@OnError
    public void onError(Throwable error) {
	}

	@OnMessage
    public void handleMessage(String message, Session session) {
		for(Session peer:peers){
			if(peer != session){
				try {
					if (message.contains(",")){							
						locationOfUsers.put(session.getId(),message);
					}else{
						peer.getBasicRemote().sendText(message + " From Session ID:" + session.getId());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
