//package com.ss.coffeecafe.config;
//
//import org.springframework.beans.propertyeditors.StringTrimmerEditor;
//import org.springframework.web.bind.WebDataBinder;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.InitBinder;
//
//@ControllerAdvice
//public class ControllerSetup
//{
//    @InitBinder
//    public void initBinder ( WebDataBinder binder )
//    {
//        StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
//        binder.registerCustomEditor(String.class, stringtrimmer);
//    }
//}
