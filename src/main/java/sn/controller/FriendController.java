package sn.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sn.service.IFriendService;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {
    private final IFriendService friendService;

    //TODO SN-25

}
