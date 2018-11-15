package com.example.copicatkurilshika.entities;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class HttpAnswer {

    private Integer code;
    private String text;
    private long duration;

    @Builder
    public HttpAnswer(Integer code, String text) {
        this.code = code;
        this.text = text;
    }

    public HttpAnswer() {
    }

    public String durationInSec() {
        return String.format("%.3fs", (double) duration / 1000);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HttpAnswer{");
        sb.append("[").append(code).append("]");
        sb.append(", spent: ").append(durationInSec());
        sb.append(", text: '").append(text).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
