package com.crawling.webanalyzer.controllers;

import com.crawling.webanalyzer.services.AnalizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class IndexController {

    @Autowired
    private AnalizeService analizeService;

    @GetMapping(path = "/")
    public String home() {
        return "redirect:"+"/index";
    }

    @GetMapping(name = "index",path = "/index")
    public String index(Model model) {
        if(!model.containsAttribute("url"))
            model.addAttribute("url","");
        return "index";
    }

    @PostMapping(name = "/index",path = "/index")
    public String submit(@ModelAttribute("url") String url,Model model) {
        String err;
        try {
            // chargement du document html dans le service.
            this.analizeService.loadPage(url);

            // chargement des infos du document html dans le service.
            this.analizeService.loadInfos();

            int internsLinksNb = this.analizeService.getLinks()[0].size();
            int externsLinksNb = this.analizeService.getLinks()[1].size();

            model.addAttribute("info",this.analizeService.getCurrentPageInfos());
            model.addAttribute("internsLinksNb",internsLinksNb);
            model.addAttribute("externsLinksNb",externsLinksNb);

            return "index";

        }catch (Exception e) {
            err =e.getMessage();
            model.addAttribute("error",err);

            return "index";

        }

    }
}
