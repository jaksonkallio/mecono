package endpoint;

import java.io.IOException;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.Gson;

@ServerEndpoint(value = "/test", decoders = TestEndpoint.MessageDecoder.class, encoders = TestEndpoint.MessageEncoder.class )
public class TestEndpoint {
	@OnOpen
    public void onOpen(Session session) throws IOException {
        System.out.println("Opened");
    }
 
    @OnMessage
    public void onMessage(Session session, Message message) throws IOException {
    	System.out.println("Received message: " + message);
    }
 
    @OnClose
    public void onClose(Session session) throws IOException {
        System.out.println("Closed");
    }
 
    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("Error");
    }
    
    public class Message {
        public String from;
        public String to;
        public String content;
    }
    
    public class MessageEncoder implements Encoder.Text<Message> {
    	 
        private Gson gson = new Gson();
     
        @Override
        public String encode(Message message) throws EncodeException {
            return gson.toJson(message);
        }
     
        @Override
        public void init(EndpointConfig endpointConfig) {
            // Custom initialization logic
        }
     
        @Override
        public void destroy() {
            // Close resources
        }
    }
    
    public class MessageDecoder implements Decoder.Text<Message> {
    	 
        private Gson gson = new Gson();
     
        @Override
        public Message decode(String s) throws DecodeException {
            return gson.fromJson(s, Message.class);
        }
     
        @Override
        public boolean willDecode(String s) {
            return (s != null);
        }
     
        @Override
        public void init(EndpointConfig endpointConfig) {
            // Custom initialization logic
        }
     
        @Override
        public void destroy() {
            // Close resources
        }
    }
}