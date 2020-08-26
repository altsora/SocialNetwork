package sn.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.stereotype.Service;
import sn.api.requests.PersonEditRequest;
import sn.api.response.CityResponse;
import sn.api.response.CountryResponse;
import sn.api.response.PersonResponse;
import sn.model.Person;
import sn.model.dto.account.UserRegistrationRequest;
import sn.repositories.PersonRepository;
import sn.service.IAccountService;
import sn.service.MailSenderService;
import sn.utils.TimeUtil;

import javax.transaction.Transactional;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

/**
 * Класс AccountService.
 * Сервисный слой для аккаунта пользователя.
 *
 * @version 1.0
 */
@Slf4j
@Service("account-service")
public class AccountService implements IAccountService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Authentication authentication;

    @Autowired
    private MailSenderService mailSenderService;

    /**
     * Метод findByEmail.
     * Поиск по email.
     *
     * @param email - почтовый адрес.
     * @return Person.
     * @throws Exception - если пользователь не найден по email.
     */
    @Override
    public Person findByEmail(String email) throws UsernameNotFoundException {
        return personRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Person not found by email."));
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
     * Изменяет статус блокировки пользователя на противоположный.
     *
     * @param personId - идентификатор пользователя.
     * @return - возврат true, если статус изменён, иначе false.
     */
    @Override
    public boolean changeUserLockStatus(long personId) {
        Person person = findById(personId);
        if (person == null) {
            log.warn("User with id {} do not exist. Lock status didn't changed", personId);
            return false;
        }
        person.setBlocked(!person.isBlocked());
        personRepository.saveAndFlush(person);
        log.info("User with id {} changed lock status", personId);
        return true;
    }

    /**
     * Обновление данных о пользователе.
     *
     * @param person - пользователя;
     * @return - возвращается обновлённый пользователь.
     */
    @Override
    public Person updatePerson(Person person, PersonEditRequest personEditRequest) {
        person.setFirstName(personEditRequest.getFirstName());
        person.setLastName(personEditRequest.getLastName());
        person.setBirthDate(TimeUtil.getLocalDateFromTimestamp(personEditRequest.getBirthDate()));
        person.setPhone(personEditRequest.getPhone());
        person.setPhoto(personEditRequest.getPhoto());
        person.setAbout(personEditRequest.getAbout());
        //TODO: город и страна без изменений
        person.setMessagesPermission(personEditRequest.getMessagesPermission());
        log.info("Update data for user with id {}.", person.getId());
        return personRepository.saveAndFlush(person);
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

    /**
     * Удаление пользователя по его идентификатору.
     *
     * @param personId - идентификатор пользователя.
     */
    @Override
    public void deleteById(long personId) {
        personRepository.deleteById(personId);
        log.info("User with id {} delete.", personId);
    }


    /**
     * Метод register.
     * Регистрация нового пользователя.
     *
     * @param userRegistrationRequest - данные с веб-формы.
     * @return true - если пользователь сохранен в базу, false - не совпали пароли, есть пользователь с такой же почтой,
     * база данных недоступна.
     * @see UserRegistrationRequest ;
     */
    @Override
    public boolean register(UserRegistrationRequest userRegistrationRequest) {
        if (!userRegistrationRequest.getPasswd1().equals(userRegistrationRequest.getPasswd2())) {
            log.warn("Passwords mismatch. Registration cancelled.");
            return false;
        }
        if (personRepository.findByEmail(userRegistrationRequest.getEmail()).isPresent()) {
            log.error("User [{}] is exists.", userRegistrationRequest.getEmail());
            return false;
        };
        Person person = new Person();
        person.setFirstName(userRegistrationRequest.getFirstName());
        person.setLastName(userRegistrationRequest.getLastName());
        person.setPassword(passwordEncoder.encode(userRegistrationRequest.getPasswd1()));
        person.setEmail(userRegistrationRequest.getEmail());
        personRepository.save(person);
        log.info("Person successfully registered");
        return true;
    }

    /**
     * Метод recoveryPassword.
     * Метод для восстановления пароля.
     *
     * @param email почта пользователя.
     * @return true - новый пароль сохранен и отправлен пользователю, false - person не найден в базе, база недоступна.
     */
    @Override
    public boolean recoveryPassword(String email) {
        Optional<Person> personOpt = personRepository.findByEmail(email);
        if (personOpt.isEmpty()) {
            log.warn("Person [{}] not found for password recovery." , email);
            return false;
        }
        Person person = personOpt.get();
        String newPassword = generateNewPassword(9);
        person.setPassword(passwordEncoder.encode(newPassword));
        personRepository.save(person);
        log.info("New password set to the person.");
        CompletableFuture.runAsync(() -> mailSenderService.send(person.getEmail(), "Password recovery",
                "Your new password: " + newPassword));
        return true;
    }

    /**
     * Метод setNewPassword.
     * Осознанная смена пароля.
     *
     * @param password новый пароль.
     * @return true - если пароль изменен, false - если пользователь не найден в базе, база недоступна.
     */
    @Override
    @Transactional
    public boolean setNewPassword(String password) {
        Optional<Person> personOpt = personRepository.findByEmail(authentication.getName());
        if (personOpt.isEmpty()) {
            log.warn("Person not found by email [{}].", authentication.getName());
            return false;
        }
        Person person = personOpt.get();
        person.setPassword(passwordEncoder.encode(password));
        personRepository.save(person);
        log.info("Person password successfully recovered.");
        CompletableFuture.runAsync(() -> mailSenderService.send(person.getEmail(), "Password recovery",
                new StringBuffer("Your password has been changed successfully from ")
                        .append(getIpAddress()).append(" at ").append(LocalDateTime.now()).toString()));
        return true;
    }

    /**
     * Метод changeEmail.
     * Осознанная смена почтового адреса.
     *
     * @param newEmail новый адрес почты.
     * @return true - если адрес измене, false - если существует пользователь с таким же почтовым адресом,
     * пользователь не найден, база недоступна.
     */
    @Override
    @Transactional
    public boolean changeEmail(String newEmail) {
        Optional<Person> personOpt = personRepository.findByEmail(authentication.getName());
        if (personOpt.isEmpty()) {
            log.warn("Person not found by email [{}].", authentication.getName());
            return false;
        }
        Person person = personOpt.get();
        person.setEmail(newEmail);
        personRepository.save(person);
        log.info("Person email successfully changed.");
        return true;
    }

    /**
     * Метод findCurrentUser.
     * Получение текущего пользователя.
     *
     * @return Person или null, если текущий пользователь не аутентифицирован.
     */
    public Person findCurrentUser() {
        if (authentication instanceof AnonymousAuthenticationToken) {
            log.warn("Anonymous user authenticated");
            return null;
        }
        Optional<Person> personOpt = personRepository.findByEmail(authentication.getName());
        if (personOpt.isEmpty()) {
            log.warn("Person not found by email [{}].", authentication.getName());
            return null;
        }
        return personOpt.get();
    }

    /**
     * Метод generateNewPassword.
     * Генератор пароля.
     *
     * @param length длина пароля.
     * @return пароль.
     */
    private String generateNewPassword(int length) {
        int leftLimit = 48;
        int rightLimit = 122;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    /**
     * Метод getIpAddress.
     * Получение ip-адреса пользователя.
     *
     * @return ip-алрес пользователя.
     */
    private InetAddress getIpAddress() {
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            log.error("Can't get ip address.");
            e.printStackTrace();
        }
        return null;
    }

}
