package tn.esprit.brody;

import java.net.URI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.twilio.Twilio;
import com.twilio.base.ResourceSet;
import com.twilio.converter.Promoter;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;



@SpringBootApplication
public class SmsApplication {
	
	private static final String ACCOUNT_SID = "PUT YOUR TWILIO ACCOUNT SID";
	private static final String AUTH_TOKEN = "PUT YOUR TWILIO AUTH TOKEN";
	private static final String TWILIO_NUMBER = "PUT YOUR TWILIO PHONE NUMBER";

	public static String sendingSms() {
		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
		Message message = Message.creator(
		    new PhoneNumber("Put the number which you want to send sms"),
		    new PhoneNumber(TWILIO_NUMBER),
		    "Sample Twilio SMS using Java")
		.create();
		
		return message.toString();
	}
	
	public static String sendingMms() {
		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
		
		//Message.creator() requires 3 parameters: To phone number, From phone number, and the message body
		Message message = Message.creator(
		    new PhoneNumber("Put the number which you want to send mms"),
		    new PhoneNumber(TWILIO_NUMBER),
		    "Sample Twilio MMS using Java")
		.setMediaUrl(
		    Promoter.listOfOne(URI.create("https://brandlogos.net/wp-content/uploads/2015/09/Google-logo-1.png")))
		.create();
		return message.toString();
	}
	
	public static void checkingDeliveryStatusSynchronous() {
		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
		ResourceSet<Message> messages = Message.reader().read();
		for (Message message : messages) {
		    System.out.println(message.getSid() + " : " + message.getStatus());
		}
		//Note that Message.reader().read() makes a remote API call so use it sparingly. By default, 
		//it returns all messages we've sent, but we can filter the returned messages by phone numbers or date range.
	}
	
	public static void checkingDeliveryStatusAsynchronous() {
		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
		com.google.common.util.concurrent.ListenableFuture<ResourceSet<Message>> future = Message.reader().readAsync();
		Futures.addCallback(
		    future,
		    new FutureCallback<ResourceSet<Message>>() {
		        public void onSuccess(ResourceSet<Message> messages) {
		            for (Message message : messages) {
		                System.out.println(message.getSid() + " : " + message.getStatus());
		             }
		         }
		         public void onFailure(Throwable t) {
		             System.out.println("Failed to get message status: " + t.getMessage());
		         }
		     });
		
		//Because retrieving message status requires a remote API call, it can take a long time.
		//To avoid blocking the current thread,
		//the Twilio Java client provides also an asynchronous version of Message.getStatus().read().
	}
	
	public static void main(String[] args) {
		SpringApplication.run(SmsApplication.class, args);
		String sms = sendingSms();
		String mms = sendingMms();
		System.out.println("++++++++++++++ SMS +++++++++++++");
		System.out.println(sms);
		System.out.println("++++++++++++++ MMS +++++++++");
		System.out.println(mms);
		System.out.println("++++++++++++++++  STATUS SYNC +++++++++++");
		checkingDeliveryStatusSynchronous();
		System.out.println("+++++++++++++++  STATUS ASYNC ++++++++++++");
		checkingDeliveryStatusAsynchronous();
		
	}

}
