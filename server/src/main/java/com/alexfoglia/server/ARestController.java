package com.alexfoglia.server;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ARestController {
	@GetMapping("/api")
	public String index() {
		return "";
	}

}
