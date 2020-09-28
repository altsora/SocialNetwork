package sn.controller;

/**
 * @author Andrey.Kazakov
 * @date 21.09.2020
 */

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sn.api.response.NotificationResponse;
import sn.api.response.ServiceResponseDataList;
import sn.service.AccountService;
import sn.service.NotificationService;

/**
 * REST-контроллер для работы с уведомлением.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final AccountService accountService;

    /**
     * Метод getNotificationList().
     * Получить список уведомлений для текущего пользователя.
     * GET запрос /api/v1/notifications.
     *
     * @param offset      - отступ от начала списка.
     * @param itemPerPage - количество диалогов на страницу.
     * @return список диалогов пользователя в json формате
     */
    @GetMapping
    public ServiceResponseDataList<NotificationResponse> getNotificationList(@RequestParam int offset,
                                                                            @RequestParam(defaultValue = "20") int itemPerPage){

        return notificationService.getNotificationByPage(accountService.findCurrentUser(), offset, itemPerPage);
    }


    /**
     * Метод getNotificationList().
     * Отметить уведомление как "прочитанное".
     * PUT запрос /api/v1/notifications.
     *
     * @param id      - ID уведомления.
     * @param all - количество диалогов на страницу.
     * @return список диалогов пользователя в json формате
     */
    @PutMapping
    public ServiceResponseDataList<NotificationResponse> getNotificationByIdOrAll(@RequestParam Long id,
                                                                                  @RequestParam boolean all) {
        if(all){
            return notificationService.readAllNotification(accountService.findCurrentUser());
        } else {
            return notificationService.readNotificationById(accountService.findCurrentUser(), id);
        }

    }

}
