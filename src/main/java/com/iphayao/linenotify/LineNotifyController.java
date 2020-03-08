package com.iphayao.linenotify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(path = "/linenotify")
@CrossOrigin
@RestController
public class LineNotifyController {

	@Autowired
	private LineNotifyManager linenotifymanager;
	
    @GetMapping("/text")
    public String getTesttext() {
//        System.out.print("Test Controller !!!");
        return "Test Controller !!!";
    }
    
	@GetMapping("/line")
	public void lineNotify() {
		linenotifymanager.notifyLine("TEST LINE NOTIFY !!!");
	}
	
}
