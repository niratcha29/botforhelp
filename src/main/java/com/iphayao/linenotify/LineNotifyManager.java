package com.iphayao.linenotify;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class LineNotifyManager {

	public void notifyLine(String str){
		 final String USER_TOKEN = "sSFXh4h66CQ73ZFqz8N21Gky85ElORCoPFSGoFDbsda"; //---Token-line-------------
		 LineNotify ln = new LineNotify(USER_TOKEN);
			try {
				ln.notifyMe(str);
			} catch (IOException ex) {
				log.error(ex.getMessage(), ex);
			}
	}
	
}
