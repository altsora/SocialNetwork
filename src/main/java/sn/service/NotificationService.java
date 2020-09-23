package sn.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.api.requests.NotificationSettingRequest;
import sn.api.response.NotificationResponse;
import sn.api.response.ResponseDataMessage;
import sn.api.response.ServiceResponse;
import sn.api.response.ServiceResponseDataList;
import sn.model.Notification;
import sn.model.NotificationSettings;
import sn.model.NotificationType;
import sn.model.Person;
import sn.model.enums.NotificationTypeCode;
import sn.repositories.NotificationRepository;
import sn.repositories.NotificationSettingsRepository;
import sn.repositories.NotificationTypeRepository;
import sn.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author Andrey.Kazakov
 * @date 21.09.2020
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final static String DATE_FILED_SORT = "sentTime";

    private final ConcurrentHashMap<NotificationTypeCode, NotificationType> notificationTypeMap
                            = new ConcurrentHashMap<>();

    private final NotificationRepository notificationRepository;
    private final NotificationSettingsRepository notificationSettingsRepository;
    private final NotificationTypeRepository notificationTypeRepository;

    /**
     * Метод getNotificationByPage.
     * Постраничное чтение списка нотификаций.
     *
     * @param person пользователь читающий список нотификаций.
     * @param offset смещение.
     * @param perPage количество на странице.
     * @return ServiceResponseDataList<NotificationResponse>
     */
    public ServiceResponseDataList<NotificationResponse> getNotificationByPage(Person person, int offset, int perPage){
        Pageable pageable = PageRequest.of(offset, perPage, Sort.Direction.DESC, DATE_FILED_SORT);
        Page<Notification> pageResponse = notificationRepository.findByToWhomAndIsReadedFalse(person, pageable);
        int total = (int)pageResponse.getTotalElements();
        List<NotificationResponse> response = convertNotificationListToNotificationResponseList(pageResponse.getContent());

        return new ServiceResponseDataList<>(total, offset, perPage, response);
    }

    /**
     * Метод readAllNotification.
     * Читает (берет из базы и устанавливает признак "прочитано") все нотификации для пользователя.
     *
     * @param person пользователь читающий список нотификаций.
     * @return ServiceResponseDataList<NotificationResponse>
     */
    @Transactional
    public ServiceResponseDataList<NotificationResponse> readAllNotification(Person person){
        List<Notification> notificationList = notificationRepository.findAllByToWhomAndIsReadedFalse(person);

        if(!notificationList.isEmpty()){
            notificationList.parallelStream().forEach(notification -> notification.setReaded(true));
            notificationRepository.saveAll(notificationList);
        }

        return new ServiceResponseDataList<>(convertNotificationListToNotificationResponseList(notificationList));
    }

    /**
     * Метод readNotificationById.
     * Читает (берет из базы и устанавливает признак "прочитано") нотификацию по ее номеру.
     *
     * @param person пользователь читающий список нотификаций.
     * @param id номер нотификации.
     * @return ServiceResponseDataList<NotificationResponse>
     */
    @Transactional
    public ServiceResponseDataList<NotificationResponse> readNotificationById(Person person, Long id){
        Optional<Notification> notificationOptional = notificationRepository.findById(id);
        if(notificationOptional.isEmpty()){
            return new ServiceResponseDataList<>(String.format("Для пользователя [%s] не найдено уведомление c кодом [%s]",
                                                                      person.getEmail(), id));
        }

        if(notificationOptional.get().isReaded()) {
            return new ServiceResponseDataList<>(String.format("Уведомление c кодом [%s] уже прочитанно", id));
        }
        List<NotificationResponse> notificationResponseList = new ArrayList<>();
        Notification notification = notificationOptional.get();
        notification.setReaded(true);
        notificationRepository.save(notification);

        notificationResponseList.add(convertNotificationToNotificationResponse(notification));
        return new ServiceResponseDataList<>(notificationResponseList);
    }

    /**
     * Метод saveNotificationSettings.
     * Снимает или устанавливает настройку.
     *
     * @param person пользователь установивший настройку.
     * @param request Запрос по смене настройки.
     * @return ServiceResponse<ResponseDataMessage>
     */
    @Transactional
    public ServiceResponse<ResponseDataMessage> saveNotificationSettings(Person person, NotificationSettingRequest request) {

        //ЕСЛИ есть где это список будет заполняться разом, тогда можно это закомментировать
        if(!notificationTypeMap.containsKey(request.getNotificationType())) {
            notificationTypeMap.put(request.getNotificationType(),
                                    notificationTypeRepository.findByCode(request.getNotificationType())
                                                                                 .orElseThrow());
        }

        NotificationSettings setting = notificationSettingsRepository.findByOwnerAndType(person, request.getNotificationType())
                                                        .orElse(NotificationSettings.builder()
                                                                .owner(person)
                                                                .type(notificationTypeMap.get(request.getNotificationType()))
                                                                .build()
                                                        );

        setting.setEnable(request.isEnable());
        notificationSettingsRepository.save(setting);

        return new ServiceResponse<>(ResponseDataMessage.ok());
    }

    /**
     * Метод getAllNotificationType.
     * Получение все типы нотификации и сохраняет в HASHMAP.
     *
     * @return ServiceResponse<ResponseDataMessage>
     */
    public ServiceResponse<ResponseDataMessage> getAllNotificationType() {
        for (NotificationType type : notificationTypeRepository.findAll()) {
            if (!notificationTypeMap.containsKey(type.getCode())) {
                notificationTypeMap.put(type.getCode(), type);
            }
        }

        return new ServiceResponse<>(ResponseDataMessage.ok());
    }

    private List<NotificationResponse> convertNotificationListToNotificationResponseList(List<Notification> listNotification){
        return listNotification.parallelStream()
                .map(this::convertNotificationToNotificationResponse)
                .collect(Collectors.toList());
    }

    private NotificationResponse convertNotificationToNotificationResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .typeId(notification.getType().getId())
                .sentTime(TimeUtil.getTimestampFromLocalDateTime(notification.getSentTime()))
                .entityId(notification.getEntityId())
                .info(notification.getInfo())
                .build();
    }


}
