package sn.api.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MessageSendRequest {
    @JsonProperty("message_text")
    private String messageText;
    // С одним полем десериализация не работает, потому добавлено второе поле для решения проблемы (или есть иные варианты)
    private String var;
}
