package uit.se100.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uit.se100.services.seed.DefaultDataService;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final DefaultDataService defaultDataService;

    public AdminController(DefaultDataService defaultDataService) {
        this.defaultDataService = defaultDataService;
    }

    @PostMapping("/seed")
    public void loadDefaultData() {
        defaultDataService.loadDefaultData();
    }
}
