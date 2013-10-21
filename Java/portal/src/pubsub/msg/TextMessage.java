package pubsub.msg;


public class TextMessage extends AbstractMessage {
	
	private static final long serialVersionUID = 1L;
	private String content;
	public TextMessage(String topic, String content) {
		super(topic);
		this.content = content;
	}
	
	public String getContent() {
		return content;
	}


}