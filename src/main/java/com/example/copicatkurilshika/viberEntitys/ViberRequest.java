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
public class ViberRequest {
    //@ApiModelProperty(notes = "Existing unit id", example = "3100")
    private Integer service_id;
    //@ApiModelProperty(notes = "Destination phone number (with country code)", example = "380000000000", position = 1)
    private String dest;
    //@ApiModelProperty(notes = "Unique identifier (U32) of a message of the sender's side", example = "100035", position = 2)
    private Long seq;
    //@ApiModelProperty(notes = "TTL value interval (seconds), range - 15 to 86400", example = "60", position = 4)
    private Integer ttl;
    //@ApiModelProperty(position = 7)
    private ViberMessage message;
}

