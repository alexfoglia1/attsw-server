package com.alexfoglia.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AWebController {
	@Autowired
	private IGridService service;
	private static final String USERCONTENT="usercontent";
	@RequestMapping("/")
	public String welcome(Model model) {
		return "database";
	}
}
