package com.example.copicatkurilshika.viberEntitys;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ViberMessage {

    //@ApiModelProperty(notes = "Some text", example = "a message for our viber user")
   // @JsonProperty("#txt")
    private String txt;



}
