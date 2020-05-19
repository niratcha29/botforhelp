package com.iphayao.linebot;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.google.common.io.ByteStreams;
import com.iphayao.LineApplication;
import com.iphayao.linebot.flex.CatalogueFlexMessageSupplier;
import com.iphayao.linebot.flex.NewsFlexMessageSupplier;
import com.iphayao.linebot.flex.ReceiptFlexMessageSupplier;
import com.iphayao.linebot.flex.RestaurantFlexMessageSupplier;
import com.iphayao.linebot.flex.RestaurantMenuFlexMessageSupplier;
import com.iphayao.linebot.flex.TicketFlexMessageSupplier;
import com.iphayao.linebot.helper.RichMenuHelper;
import com.iphayao.linebot.model.UserLog;
import com.iphayao.linebot.model.UserLog.status;
import com.iphayao.repository.LineRepository;
import com.iphayao.repository.LogRepo;
import com.iphayao.service.FoodsService;
import com.iphayao.service.HolidayService;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.MessageContentResponse;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.action.DatetimePickerAction;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.ImageMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.StickerMessage;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.template.CarouselColumn;
import com.linecorp.bot.model.message.template.CarouselTemplate;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ComponentScan
@LineMessageHandler

public class LineBotController {

	@Autowired
	private LineMessagingClient lineMessagingClient;

	@Autowired
	private LineRepository lineRepo;
	
	@Autowired
	private LogRepo logRepo;
	
	@Autowired
	private HolidayService holiday;
	
	@Autowired
	private FoodsService foods;

	// private status userLog.setStatusBot(status.DEFAULT); // Default status
	private Map<String, UserLog> userMap = new HashMap<String, UserLog>();

	@EventMapping
	public void handleTextMessage(MessageEvent<TextMessageContent> event) throws IOException, InterruptedException, ExecutionException {
		log.info(event.toString());
		TextMessageContent message = event.getMessage();
		handleTextContent(event.getReplyToken(), event, message);
	}

	@EventMapping
	public void handlePostbackEvent(PostbackEvent event) {
		String replyToken = event.getReplyToken();
		replyToken = replyToken.replace("date", "");
		this.replyText(replyToken, event.getPostbackContent().getData().toString().replace("date", "")
				+ event.getPostbackContent().getParams().toString());
	}

	@EventMapping
	public void handleOtherEvent(Event event) {
		log.info("Received message(Ignored): {}", event);
	}

	@EventMapping
	public void handleImageMessage(MessageEvent<ImageMessageContent> event) {
		log.info(event.toString());
		ImageMessageContent content = event.getMessage();
		String replyToken = event.getReplyToken();

		try {
			MessageContentResponse response = lineMessagingClient.getMessageContent(content.getId()).get();
			DownloadedContent jpg = saveContent("jpg", response);
			DownloadedContent previewImage = createTempFile("jpg");

			system("convert", "-resize", "240x", jpg.path.toString(), previewImage.path.toString());

			reply(replyToken, new ImageMessage(jpg.getUri(), previewImage.getUri()));

		} catch (InterruptedException | ExecutionException e) {
			reply(replyToken, new TextMessage("Cannot get image: " + content));
			throw new RuntimeException(e);
		}

	}

	private static final DateFormat dateNow = new SimpleDateFormat("yyyy-MM-dd");
	private static final DateFormat dateNowHoliday = new SimpleDateFormat("dd/MM/yyyy");
	Date nowDate = new Date();

	private void handleTextContent(String replyToken, Event event, TextMessageContent content) throws IOException, InterruptedException, ExecutionException {
		UserLog userLog = userMap.get(event.getSource().getSenderId());

		if (userLog == null) {
			userLog = new UserLog(event.getSource().getSenderId(), status.DEFAULT);
			userMap.put(event.getSource().getSenderId(), userLog);
		}
		String text = content.getText();
		ModelMapper modelMapper = new ModelMapper();


		if (userLog.getStatusBot().equals(status.DEFAULT)) {
			switch (text) {
			case "รายชื่อปัญหา": {
				String pathYamlHome = "asset/richmenu-home.yml";
				String pathImageHome = "asset/richmenu-home.jpg";
				RichMenuHelper.createRichMenu(lineMessagingClient, pathYamlHome, pathImageHome, userLog.getUserID());
				break;
			}
			case "เปิดใช้งานคอมพิวเตอร์ไม่ได้": {
				logRepo.saveLog("เปิดใช้งานคอมพิวเตอร์ไม่ได้",userLog.getUserID());
				this.reply(replyToken,
						Arrays.asList(new ImageMessage("https://scontent.fbkk20-1.fna.fbcdn.net/v/t1.15752-9/90148413_585082952088954_752638562733654016_n.jpg?_nc_cat=109&_nc_sid=b96e70&_nc_eui2=AeGQfaZBFSiSxo5iJEm3rggtng22Rdqt3PmeDbZF2q3c-dPDG8x9dai5-xj4BSQg4fWSk96VyDLFfkqbsNk6_X4H&_nc_ohc=XqQ0uKpmr_8AX9r2teY&_nc_ht=scontent.fbkk20-1.fna&oh=455e4cf5f48e511e10509b26ecaf0cbe&oe=5EE9457F",
								"https://scontent.fbkk20-1.fna.fbcdn.net/v/t1.15752-9/90148413_585082952088954_752638562733654016_n.jpg?_nc_cat=109&_nc_sid=b96e70&_nc_eui2=AeGQfaZBFSiSxo5iJEm3rggtng22Rdqt3PmeDbZF2q3c-dPDG8x9dai5-xj4BSQg4fWSk96VyDLFfkqbsNk6_X4H&_nc_ohc=XqQ0uKpmr_8AX9r2teY&_nc_ht=scontent.fbkk20-1.fna&oh=455e4cf5f48e511e10509b26ecaf0cbe&oe=5EE9457F"),
								new TextMessage("1.ดูว่าปลั๊กไฟตามรูปถูกเสียบอยู่หรือไม่"+ "\n" + "2.ดูว่าเสียบปลั๊กไฟตามรูปแน่นหรือไม่\n"),
								new ImageMessage("https://scontent.fbkk20-1.fna.fbcdn.net/v/t1.15752-9/96780735_1551698928332155_32072028332752896_n.jpg?_nc_cat=103&_nc_sid=b96e70&_nc_eui2=AeGkxxTFxTrzc5_j1DSY8YeML_AUo4GCo48v8BSjgYKjjxpreQlB48QZrAlVf2tfeXOOLfE2wlwUX0aBkBLstNSd&_nc_ohc=6tDBUsdR4rsAX_JwcOQ&_nc_ht=scontent.fbkk20-1.fna&oh=66496127fa9b43c5faba09e7d029d31f&oe=5EEB8D50",
										"https://scontent.fbkk20-1.fna.fbcdn.net/v/t1.15752-9/96780735_1551698928332155_32072028332752896_n.jpg?_nc_cat=103&_nc_sid=b96e70&_nc_eui2=AeGkxxTFxTrzc5_j1DSY8YeML_AUo4GCo48v8BSjgYKjjxpreQlB48QZrAlVf2tfeXOOLfE2wlwUX0aBkBLstNSd&_nc_ohc=6tDBUsdR4rsAX_JwcOQ&_nc_ht=scontent.fbkk20-1.fna&oh=66496127fa9b43c5faba09e7d029d31f&oe=5EEB8D50"),
								new TextMessage("3.ดูสวิตซ์ไฟว่าเปิดหรือไม่" + "\n")));
				userLog.setStatusBot(status.DEFAULT);
				break;
			}
			case "ลำโพงเสียงไม่ดัง": {
				logRepo.saveLog("ลำโพงเสียงไม่ดัง",userLog.getUserID());
				this.reply(replyToken,
						Arrays.asList(new TextMessage("ตรวจสอบ Device Manager : Sound\n" + "1.คลิกขวาที่ Start > ทำการเลือก Device Manager\n"),
								new ImageMessage("https://notebookspec.com/web/wp-content/uploads/2018/08/Screenshot_1.jpg",
										"https://notebookspec.com/web/wp-content/uploads/2018/08/Screenshot_1.jpg"),
								new TextMessage("2.คลิก Device Manager > ทำการดูหัวข้อ Sound Video and Game Controllers  : ทำการคลิกด้านหน้า\n" + 
										"3.จะเห็น High Definition Audio Device ให้ทำการคลิกขวา และเลือก Properties > แล้วดูในช่องของ General Device Status จะต้องขึ้น This Device is working properly\n"),
								new ImageMessage("https://notebookspec.com/web/wp-content/uploads/2018/08/Screenshot_2-1.jpg",
										"https://notebookspec.com/web/wp-content/uploads/2018/08/Screenshot_2-1.jpg")));
				userLog.setStatusBot(status.DEFAULT);
				break;
			}
			case "เครื่องคอมพิวเตอร์ดับเอง": {
				logRepo.saveLog("เครื่องคอมพิวเตอร์ดับเอง",userLog.getUserID());
				this.reply(replyToken,
				Arrays.asList(new TextMessage("ตรวจสอบสายปลั๊กไฟต่างๆว่าเสียบแน่นหรือไม่\n"),
							new ImageMessage("https://scontent.fbkk20-1.fna.fbcdn.net/v/t1.15752-9/96780735_1551698928332155_32072028332752896_n.jpg?_nc_cat=103&_nc_sid=b96e70&_nc_eui2=AeGkxxTFxTrzc5_j1DSY8YeML_AUo4GCo48v8BSjgYKjjxpreQlB48QZrAlVf2tfeXOOLfE2wlwUX0aBkBLstNSd&_nc_ohc=6tDBUsdR4rsAX_JwcOQ&_nc_ht=scontent.fbkk20-1.fna&oh=66496127fa9b43c5faba09e7d029d31f&oe=5EEB8D50",
								"https://scontent.fbkk20-1.fna.fbcdn.net/v/t1.15752-9/96780735_1551698928332155_32072028332752896_n.jpg?_nc_cat=103&_nc_sid=b96e70&_nc_eui2=AeGkxxTFxTrzc5_j1DSY8YeML_AUo4GCo48v8BSjgYKjjxpreQlB48QZrAlVf2tfeXOOLfE2wlwUX0aBkBLstNSd&_nc_ohc=6tDBUsdR4rsAX_JwcOQ&_nc_ht=scontent.fbkk20-1.fna&oh=66496127fa9b43c5faba09e7d029d31f&oe=5EEB8D50"),
							new TextMessage("หรือทำขั้นตอนตามลิงค์นี้ http://itnews4u.com/fix-computer-turn-off-automatically.html")));
		userLog.setStatusBot(status.DEFAULT);
		break;
			}
			case "ไฟล์งานหาย": {
				logRepo.saveLog("ไฟล์งานหาย",userLog.getUserID());
				this.reply(replyToken,
				Arrays.asList(new TextMessage("ไฟล์งานเอกสาร Word หาย ทำตามขั้นตอนตามลิ้งค์ต่อไปนี้  http://th.wondershare.com/recover-data/recover-deleted-word-document.html?fbclid=IwAR1JQkQ_erJ8mnbH8M63KF-JtQ90HXu-fRTG6zZ2Qu04tFoFvgGIvm7P3-Y\n" + 
						"ไฟล์หรือโฟล์เดอร์หาย ทำตามขั้นตอนดังนี้ http://www.icom.co.th/faq/forum_ans.php?FaqId=55")));
		userLog.setStatusBot(status.DEFAULT);
		break;
			}
			case "อินเตอร์เน็ตใช้งานไม่ได้": {
				logRepo.saveLog("อินเตอร์เน็ตใช้งานไม่ได้",userLog.getUserID());
				this.reply(replyToken,
						Arrays.asList(new TextMessage("WIFI"+ "\n" + "1.ตรวจสอบว่าเปิดใช้งาน WiFi หรือไม่\n" + "2.ตรวจสอบการกรอกรหัส WiFi (ภาษาถูกหรือไม่, ตรวจสอบพิมพ์เล็กพิมพ์ใหญ่)\n"),
								new TextMessage("ตรวจสอบสายอินเตอร์เน็ต" + "\n" + "สำหรับ window 7 ลองทำตามลิ้งค์ต่อไปนี้ https://www.windowssiam.com/problem-windows-7-ip-address/?fbclid=IwAR335E9flxyKRaGMCS9_ud9J2R3TsY-SHnBkPblya12jXnWPxW6gGUpjjs0\r\n" + 
										"สำหรับ window 10 ลองทำตามลิ้งค์ต่อไปนี้ https://answers.microsoft.com/th-th/windows/forum/all/%E0%B8%A7%E0%B8%99%E0%B9%82%E0%B8%94%E0%B8%A710/d4bee942-e7c0-49b9-8cf3-a3f773d9b73a?fbclid=IwAR0QThlQOugeUCxq5iileM1Mq5fhklVVXQkR-2Jeov7-C3H_JYboydwkl8k\n"),
								new TextMessage("สายแลนและช่องเสียบสายแลนไม่เสีย แต่อินเตอร์เน็ตใช้ไม่ได้\n" + "1.คลิ๊กที่ปุ่มค้นหา ใกล้ StartMenu (ปุ่มแว่นขยาย) พิมพ์คำว่า Device Manager แล้ว Enter\n" + "2.คลิ๊กที่หัวข้อ Network Adapters  จะเห็นการ์ดแลน\n" + "3.เมื่อเอาเม้าส์ไปคลิ๊กขวาที่ การ์ดแลนซึ่งอยู่ใต้หัวข้อ Network Adapters  จะขึ้นคำว่า  Enable device ให้กด Enable device  การ์ดแลนก็จะกลับมาใช้งานได้ตามปกติ\n")));
				userLog.setStatusBot(status.DEFAULT);
				break;
			}
			case "ทั้งหมด": {
				String Holiday_In_Year = holiday.getAllHoliday();
				this.reply(replyToken,
						Arrays.asList(new TextMessage("ข้อมูลวันหยุดประจำปี ทั้งหมดค่ะ  " + "\n" + Holiday_In_Year)));
				userLog.setStatusBot(status.DEFAULT);
				break;
			}
			case "เร็วๆ": {
				String holidaySoon = holiday.getHolidaySoon();
				this.reply(replyToken, Arrays.asList(new TextMessage(holidaySoon)));
				System.out.println(holidaySoon);
				userLog.setStatusBot(status.DEFAULT);
				break;
			}
			case "ย้อนกลับค่ะ": {
//				String pathYamlHome = "asset/select_event.yml";
//				String pathImageHome = "asset/select_event.jpg";
//				RichMenuHelper.createRichMenu(lineMessagingClient, pathYamlHome, pathImageHome, userLog.getUserID());
				this.reply(replyToken, Arrays.asList(new TextMessage("เลือกเมนูที่ต้องการ ได้เลยค่ะ  ??")));
				userLog.setStatusBot(status.DEFAULT);
				break;
			}
			case "profile": {
				String userId = event.getSource().getUserId();
				if (userId != null) {
					lineMessagingClient.getProfile(userId).whenComplete((profile, throwable) -> {
						if (throwable != null) {
							this.replyText(replyToken, throwable.getMessage());
							return;
						}
						this.reply(replyToken,
								Arrays.asList(new TextMessage(
										"Display name : " + profile.getDisplayName() + "\n Status message : "
												+ profile.getStatusMessage() + "\n User ID : " + profile.getUserId())));
					});
				}
				userLog.setStatusBot(status.DEFAULT);
				break;
			}
			case "ขอลาหยุดครับผม": {
				String imageUrl = createUri("/static/buttons/1040.jpg");
				CarouselTemplate carouselTemplate = new CarouselTemplate(
						Arrays.asList(new CarouselColumn(imageUrl, "ประเภทการลา", "กรุณาเลือก ประเภทการลา ด้วยค่ะ",
								Arrays.asList(new MessageAction("ลากิจ", "ลากิจครับ"),
										new MessageAction("ลาป่วย", "ลาป่วยครับ"),
										new MessageAction("ลาพักร้อน", "ลาพักร้อนครับ")))));
				TemplateMessage templateMessage = new TemplateMessage("Carousel alt text", carouselTemplate);
				this.reply(replyToken, templateMessage);

				// userLog.setStatusBot(status.Q11);
				userLog.setStatusBot(status.DEFAULT);
				break;
			}
			case "help": {
				this.reply(replyToken, Arrays.asList(new TextMessage(
						"โปรดเลือกรายการ \n พิมพ์  profile : ดูข้อมูล Profile  \n พิมพ์  list : ดู Agenda \n พิมพ์  add : เพิ่ม Agenda")));
				userLog.setStatusBot(status.DEFAULT);
				break;
			}
			case "เมนูถัดไป": {
				String pathYamlHome = "asset/richmenu-home.yml";
				String pathImageHome = "asset/richmenu-home.jpg";
				RichMenuHelper.createRichMenu(lineMessagingClient, pathYamlHome, pathImageHome, userLog.getUserID());
				break;
			}
			case "ย้อนกลับ": {
				RichMenuHelper.deleteRichMenu(lineMessagingClient,userLog.getUserID());
				userLog.setStatusBot(status.DEFAULT);
				break;
			}
			case "สอบถาม ข้อมูลทั่วไป": {
				RichMenuHelper.deleteRichMenu(lineMessagingClient, userLog.getUserID());
				break;
			}
			case "Flex Restaurant": {
				this.reply(replyToken, new RestaurantFlexMessageSupplier().get());
				break;
			}
			case "Flex Menu": {
				this.reply(replyToken, new RestaurantMenuFlexMessageSupplier().get());
				break;
			}
			case "Flex Receipt": {
				this.reply(replyToken, new ReceiptFlexMessageSupplier().get());
				break;
			}
			case "Flex News": {
				this.reply(replyToken, new NewsFlexMessageSupplier().get());
				break;
			}
			case "Flex Ticket": {
				this.reply(replyToken, new TicketFlexMessageSupplier().get());
				break;
			}
			case "Flex Catalogue": {
				this.reply(replyToken, new CatalogueFlexMessageSupplier().get());
				break;
			}
			case "carousel": {
				String imageUrl = createUri("/static/buttons/1040.jpg");
				CarouselTemplate carouselTemplate = new CarouselTemplate(Arrays.asList(
						new CarouselColumn(imageUrl, "hoge", "fuga",
								Arrays.asList(new URIAction("Go to line.me", "https://line.me"),
										new URIAction("Go to line.me", "https://line.me"),
										new PostbackAction("Say hello1", "hello ?????", "hello ?????"))),
						new CarouselColumn(imageUrl, "hoge", "fuga",
								Arrays.asList(new PostbackAction("? hello2", "hello ?????", "hello ?????"),
										new PostbackAction("? hello2", "hello ?????", "hello ?????"),
										new MessageAction("Say message", "Rice=?"))),
						new CarouselColumn(imageUrl, "Datetime Picker", "Please select a date, time or datetime",
								Arrays.asList(
										new DatetimePickerAction("Datetime", "action=sel", "datetime",
												"2017-06-18T06:15", "2100-12-31T23:59", "1900-01-01T00:00"),
										new DatetimePickerAction("Date", "action=sel&only=date", "date", "18-06-2017",
												"31-12-2100", "01-01-1900"),
										new DatetimePickerAction("Time", "action=sel&only=time", "time", "06:15",
												"23:59", "00:00")))));
				TemplateMessage templateMessage = new TemplateMessage("Carousel alt text", carouselTemplate);
				this.reply(replyToken, templateMessage);
				break;
			}
			case "โหวตอาหาร": {
				lineRepo.CountVote(userLog);
				if (userLog.getCountVout_CheckPossilibity() >= 10) {
					this.reply(replyToken, Arrays.asList(new TextMessage(
							"คุณโหวตอาหารครบ 10 รายการสำหรับอาทิตย์นี่เเล้วค่ะ   กรุณารออาทิตย์ถัดไปสำหรับการโหวตครั้งใหม่นะคะ")));
					userLog.setStatusBot(status.DEFAULT);
				} else {
					this.reply(replyToken,
							Arrays.asList(new TextMessage("ใส่ หมายเลขอาหาร ที่ต้องการโหวตได้เลยค่ะ  ??")));
					userLog.setStatusBot(status.VOTE_FOODS);
				}

				break;
			}
			default:
				this.reply(replyToken, Arrays.asList(new TextMessage("ไม่เข้าใจคำสั่ง")));
			}
		} else if (userLog.getStatusBot().equals(status.VOTE_FOODS)) {
			lineRepo.CountVote(userLog);
			
		} else if (userLog.getStatusBot().equals(status.SAVE)) {
			switch (text) {
			case "cancel": {
				this.reply(replyToken, Arrays.asList(new TextMessage("ยกเลิกสำเร็จ ")));
				userLog.setStatusBot(status.DEFAULT);
				break;
			}
			default:
			}
		} else if (userLog.getStatusBot().equals(status.Q11)) {

			switch (text) {

			case "ลากิจครับ": {

				String imageUrl = createUri("/static/buttons/1040.jpg");

				CarouselTemplate carouselTemplate = new CarouselTemplate(Arrays.asList(

						new CarouselColumn(imageUrl, "วันลา  เริ่มต้น ",
								"กรุณา กำหนดวันลา เริ่มต้นด้วยค่ะ" + "\n" + "(ไม่สามารถเกลับมาเเก้ไขได้!!)",
								Arrays.asList(

										new DatetimePickerAction("กำหนดวัน", "วันลา  เริ่มต้นของคุณคือ ", "date",
												dateNow.format(nowDate), "2100-12-31", dateNow.format(nowDate))))));

				TemplateMessage templateMessage = new TemplateMessage("Carousel alt text", carouselTemplate);

				this.reply(replyToken, templateMessage);

				// log.info("Return echo message %s : %s", replyToken, text);
				this.reply(replyToken, Arrays.asList(new TextMessage("หนุกหนานลากิจ")));
				userLog.setStatusBot(status.DEFAULT);
				break;

			}
			case "ลาป่วยครับ": {
				log.info("Return echo message %s : %s", replyToken, text);
				this.reply(replyToken, Arrays.asList(new TextMessage("หนุกหนาน ลาป่วย")));
				userLog.setStatusBot(status.DEFAULT);
				break;
			}
			case "ลาพักร้อนครับ": {
				this.reply(replyToken, Arrays.asList(new TextMessage("หนุกหนาน พักร้อน")));
				userLog.setStatusBot(status.DEFAULT);
				break;
			}

			case "ขอทราบวันหยุด ทั้งหมดภายในปีนี้ค่ะ": {

				String Holiday_In_Year = holiday.getAllHoliday();
				this.reply(replyToken,
						Arrays.asList(new TextMessage("ข้อมูลวันหยุดประจำปี ทั้งหมดค่ะ  " + "\n" + Holiday_In_Year)));
				userLog.setStatusBot(status.DEFAULT);
				break;
			}
			default:
				String imageUrl = createUri("/static/buttons/1040.jpg");
				CarouselTemplate carouselTemplate = new CarouselTemplate(
						Arrays.asList(new CarouselColumn(imageUrl, "ประเภทการลา", "กรุณาเลือก ประเภทการลา ด้วยค่ะ",
								Arrays.asList(new MessageAction("ลากิจ", "รอ Flow ของลากิจครับ"),
										new MessageAction("ลาป่วย", "รอ Flow ลาป่วยครับ"),
										new MessageAction("ลาพักร้อน", "รอ Flow ลาหักร้อนครับ")))));
				TemplateMessage templateMessage = new TemplateMessage("Carousel alt text", carouselTemplate);
				this.reply(replyToken, templateMessage);
				userLog.setStatusBot(status.DEFAULT);
				break;
			}
		} else if (userLog.getStatusBot().equals(status.FINDEMP)) {
			userLog.setEmpCode(text.toString());
		
		} else if (userLog.getStatusBot().equals(status.FINDCONFIRM)) {
			switch (text) {
			case "ใช่": {
				lineRepo.register(userLog);
				userLog.setStatusBot(status.DEFAULT);
//				String pathYamlHome = "asset/select_event.yml";
//				String pathImageHome = "asset/select_event.jpg";
//				RichMenuHelper.createRichMenu(lineMessagingClient, pathYamlHome, pathImageHome, userLog.getUserID());
				this.reply(replyToken, Arrays.asList(new TextMessage(
						"ลงทะเบียนสำเร็จ  " + "\n" + "กรุณา  เลือกเมนู ที่ต้องการทำรายการ ได้เลยค่ะ  ??")));
				break;
			}
			case "ไม่ใช่": {
				this.reply(replyToken, Arrays.asList(new TextMessage(
						"กรุณากรอก รหัสพนักงาน ของตัวเองให้ถูกต้อง" + "\n" + "เพื่อยืนยันตัวตนอีกครั้งค่ะ")));
				userLog.setStatusBot(status.FINDEMP);
				break;
			}
			default:
				log.info("Return echo message %s : %s", replyToken, text);
			}
		} else {
			this.push(event.getSource().getSenderId(), Arrays.asList(new TextMessage("บอทหลับอยู่")));
			this.reply(replyToken, new StickerMessage("1", "17"));
		}

		userMap.put(event.getSource().getSenderId(), userLog);

	}

	private void replyText(@NonNull String replyToken, @NonNull String message) {
		if (replyToken.isEmpty()) {
			throw new IllegalArgumentException("replyToken is not empty");
		}

		if (message.length() > 1000) {
			message = message.substring(0, 1000 - 2) + "...";
		}
		this.reply(replyToken, new TextMessage(message));
	}

	private void reply(@NonNull String replyToken, @NonNull Message message) {
		reply(replyToken, Collections.singletonList(message));
	}

	private void push(@NonNull String replyToken, @NonNull List<Message> messages) {
		try {
			lineMessagingClient.pushMessage(new PushMessage(replyToken, messages)).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	private void reply(@NonNull String replyToken, @NonNull List<Message> messages) {
		try {
			lineMessagingClient.replyMessage(new ReplyMessage(replyToken, messages)).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	private void system(String... args) {
		ProcessBuilder processBuilder = new ProcessBuilder(args);
		try {
			Process start = processBuilder.start();
			int i = start.waitFor();
			log.info("result: {} => {}", Arrays.toString(args), i);
		} catch (InterruptedException e) {
			log.info("Interrupted", e);
			Thread.currentThread().interrupt();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static DownloadedContent saveContent(String ext, MessageContentResponse response) {
		log.info("Content-type: {}", response);
		DownloadedContent tempFile = createTempFile(ext);
		try (OutputStream outputStream = Files.newOutputStream(tempFile.path)) {
			ByteStreams.copy(response.getStream(), outputStream);
			log.info("Save {}: {}", ext, tempFile);
			return tempFile;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static DownloadedContent createTempFile(String ext) {
		String fileName = LocalDateTime.now() + "-" + UUID.randomUUID().toString() + "." + ext;
		Path tempFile = LineApplication.downloadedContentDir.resolve(fileName);
		tempFile.toFile().deleteOnExit();
		return new DownloadedContent(tempFile, createUri("/downloaded/" + tempFile.getFileName()));
	}

	private static String createUri(String path) {
		return ServletUriComponentsBuilder.fromCurrentContextPath().path(path).toUriString();
	}

	@Value
	public static class DownloadedContent {
		Path path;
		String uri;
	}
}
