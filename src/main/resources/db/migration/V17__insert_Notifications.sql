INSERT INTO notification_type
        (code, name)
        VALUES
        ('POST', 'Публикация нового поста'),
        ('MESSAGE', 'Получено новое сообщение'),
        ('FRIEND_REQUEST', 'Запрос в друзья'),
        ('LIKE', 'Пользователь поставил лайк'),
        ('POST_COMMENT', 'Новый комментарий под постом'),
        ('COMMENT_COMMENT', 'Ответ на комментарий');

INSERT INTO notification_settings
        (person_id, notification_type_id, enable)
        VALUES
        (1, 1, true),
        (1, 2, true),
        (1, 3, true),
        (1, 4, true),
        (1, 5, true),
        (1, 6, true),
        (2, 1, true),
        (2, 2, true),
        (2, 3, true),
        (2, 4, true),
        (2, 5, true),
        (2, 6, true);

INSERT INTO notification
        (type_id, sent_time, entity_id, person_id, contact, is_readed)
        VALUES
        (3, '2020-09-01 12:00:00', 1, 2, 'contact_1', false), -- Добавление в друзья (id=1, тип 3), уведомляем юзера (id=2)
        (5, '2020-09-09 15:40:00', 2, 1, 'contact_2', false), -- Комментарий (id=2) к посту (5), уведомляем юзера (id=1)
        (5, '2020-09-10 15:03:00', 4, 1, 'contact_3', false), -- Комментарий (id=4) к посту (5), уведомляем юзера (id=1)
        (6, '2020-09-10 15:05:00', 5, 2, 'contact_4', false), -- Комментарий (id=5) к комменту (6), уведомляем юзера (id=2)
        (6, '2020-09-10 15:07:00', 6, 1, 'contact_5', false); -- Комментарий (id=6) к комменту (6), уведомляем июзера (id=1)
