package com.hj.crypto.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
    @ResponseBody
    public ResponseEntity<?> placeOrder(@RequestParam String market,
                             @RequestParam String side,
                             @RequestParam double volume,
                             @RequestParam double price,
                             @RequestParam String orderType) {

        try {
            bithumbService.placeOrder(market, side, volume, price, orderType);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().build();
    }
}
