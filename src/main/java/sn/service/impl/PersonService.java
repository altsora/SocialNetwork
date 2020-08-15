package sn.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sn.api.requests.PersonEditRequest;
import sn.api.response.CityResponse;
import sn.api.response.CountryResponse;
import sn.api.response.PersonResponse;
import sn.model.Person;
import sn.repositories.PersonRepository;
import sn.service.IPersonService;
import sn.utils.TimeUtil;

import java.util.List;
import java.util.Optional;

/**
 * Класс PersonService.
 * Сервисный слой для Person.
 * Имплементирует IPersonService.
 *
 * @version 1.0
 * @see sn.service.IPersonService
 */
@Service("person-service")
public class PersonService implements IPersonService {

    @Autowired
    private PersonRepository personRepository;

    /**
     * Метод findByEmail.
     * Поиск по email.
     *
     * @param email - почтовый адрес.
     * @return Person.
     * @throws Exception - если пользователь не найден по email.
     */
    @Override
    public Person findByEmail(String email) throws Exception {
        return personRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("Person not found by email."));
    }

    /**
     * Метод findByUsername.
     * Поиск по имени пользователя.
     *
     * @param username - имя пользователя.
     * @return Person.
     * @throws Exception - если пользователь не найден по имени пользователя.
     */
    @Override
    public Person findByUsername(String username) throws Exception {
        return personRepository.findByEmail(username)
                .orElseThrow(() -> new Exception("Person not found by username."));
    }

    /**
     * Сохранение пользователя в базу данных.
     *
     * @param person - объект класса Person.
     * @return - результат сохранения в Optional обертке.
     */
    @Override
    public Optional<Person> save(Person person) {
        return Optional.of(personRepository.save(person));
    }

    /**
     * Поиск пользователя по его идентификатору.
     *
     * @param personId - идентификатор пользователя.
     * @return - возврат пользователя, если существует, иначе null.
     */
    @Override
    public Person findById(long personId) {
        return personRepository.findById(personId)
                .orElse(null);
    }

    /**
     * Обновление данных о пользователе.
     *
     * @param personId - идентификатор пользователя;
     * @return - возвращается обновлённый пользователь.
     */
    @Override
    public Person updatePerson(long personId, PersonEditRequest personEditRequest) {
        Person person = findById(personId);
        person.setFirstName(personEditRequest.getFirstName());
        person.setLastName(personEditRequest.getLastName());
        person.setBirthDate(TimeUtil.getLocalDateFromTimestamp(personEditRequest.getBirthDate()));
        person.setPhone(personEditRequest.getPhone());
        person.setPhoto(personEditRequest.getPhoto());
        person.setAbout(personEditRequest.getAbout());
        //TODO: город и страна без изменений
        person.setMessagesPermission(personEditRequest.getMessagesPermission());
        return personRepository.saveAndFlush(person);
    }

    /**
     * Удаление пользователя по его идентификатору.
     *
     * @param personId - идентификатор пользователя.
     */
    @Override
    public void deleteById(long personId) {
        personRepository.deleteById(personId);
    }

    /**
     * Изменяет статус блокировки пользователя на противоположный.
     *
     * @param personId - идентификатор пользователя.
     * @return - возврат true, если статус изменён, иначе false.
     */
    @Override
    public boolean changeUserLockStatus(long personId) {
        Person person = findById(personId);
        if (person == null) {
            return false;
        }
        person.setBlocked(!person.isBlocked());
        personRepository.saveAndFlush(person);
        return true;
    }

    /**
     * Формирует PersonResponse на основе Person.
     *
     * @param person - объект класса Person.
     * @return - возврат true, если статус изменён, иначе false.
     */
    @Override
    public PersonResponse getPersonResponse(Person person) {
        //TODO: Нет данных, откуда берутся город и страна
        return PersonResponse.builder()
                .id(person.getId())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .regDate(TimeUtil.getTimestampFromLocalDateTime(person.getRegDate()))
                .birthDate(TimeUtil.getTimestampFromLocalDate(person.getBirthDate()))
                .email(person.getEmail())
                .phone(person.getPhone())
                .photo(person.getPhoto())
                .about(person.getAbout())
                .city(new CityResponse("Москва"))
                .country(new CountryResponse("Россия"))
                .messagesPermission(person.getMessagesPermission())
                .lastOnlineTime(TimeUtil.getTimestampFromLocalDateTime(person.getLastOnlineTime()))
                .isBlocked(person.isBlocked())
                .build();
    }

    /**
     * Возвращает общее количество пользователей в базе.
     *
     * @return - целое число, равное количеству пользователей.
     */
    @Override
    public int getTotalCountUsers() {
        return personRepository.getTotalCountUsers();
    }

    /**
     * Осуществляет поиск пользователей по заданным параметрам.
     *
     * @param firstName   - имя пользователя;
     * @param lastName    - фамилия пользователя;
     * @param ageFrom     - минимальный возраст пользователя;
     * @param ageTo       - максимальный возраст пользователя;
     * @param offset      - отступ от начала списка;
     * @param itemPerPage - количество элементов на страницу;
     * @return - возвращает список пользователей, подходящих по заданным параметрам.
     */
    @Override
    public List<Person> searchPersons(String firstName, String lastName, Integer ageFrom, Integer ageTo, Integer offset, Integer itemPerPage) {
        int pageNumber = offset / itemPerPage;
        Pageable pageable = PageRequest.of(pageNumber, itemPerPage);
        return personRepository.searchPersons(firstName, lastName, ageFrom, ageTo, pageable);
    }
}
