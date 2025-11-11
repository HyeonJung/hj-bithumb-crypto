package com.hj.crypto.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hj.crypto.common.bithumb.model.BithumbAccount;
import com.hj.crypto.common.bithumb.service.BithumbService;



@Controller
@RequestMapping("/bithumb")
public class BithumbController {

    @Autowired private BithumbService bithumbService;
    
    @GetMapping("/accounts")
    public String getMethodName(Model model) {
        List<BithumbAccount> bithumbAccounts = bithumbService.getAccounts();
        model.addAttribute("bithumbAccounts", bithumbAccounts);
        return "bithumb/accounts";
    }

    @PostMapping("/order")
    public String placeOrder(@RequestParam String market,
                             @RequestParam String side,
                             @RequestParam double volume,
                             @RequestParam double price,
                             @RequestParam String orderType) {
        return bithumbService.placeOrder(market, side, volume, price, orderType);
    }
}
