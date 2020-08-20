package sn.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceResponse<T extends AbstractResponse> {
    /**
     * Root entity for service response
     *
     * @param error Message, in case of error, this will be the reason
     *              Сообщение, в случае ошибки здесь будет причина
     * @param timestamp Creation time - example: 1559751301818
     * @param total Elements number in the list
     *              Количество элементов в списке
     * @param offset Indent from the beginning of the list
     *               Отступ от начала списка
     * @param perPage Elements per page
     *                Количество элементов на страницу
     */
    private String error;
    private Number timestamp;
    private Integer total;
    private Integer offset;
    private Integer perPage;
    private T data;


    public ServiceResponse() {
        this.timestamp = Timestamp.valueOf(LocalDateTime.now()).getTime();
    }

    public ServiceResponse(T data) {
        this();
        this.data = data;
    }

    public ServiceResponse(String error, T data) {
        this.timestamp = Timestamp.valueOf(LocalDateTime.now()).getTime();
        this.error = error;
        this.data = data;
    }

    public ServiceResponse(int total, int offset, int perPage, T data) {
        this.total = total;
        this.offset = offset;
        this.perPage = perPage;
        this.data = data;
    }

}
