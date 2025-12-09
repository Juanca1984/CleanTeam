// Java
package com.cleanteam.mandarinplayer.DTO;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class JoinMatchRequest {

    @JsonProperty("roomCode")
    private String roomCode;

    @JsonProperty("nickname")
    private String nickname;

    public JoinMatchRequest() {
        // requerido por Jackson
    }

    @JsonCreator
    public JoinMatchRequest(
            @JsonProperty("roomCode") String roomCode,
            @JsonProperty("nickname") String nickname) {
        this.roomCode = roomCode;
        this.nickname = nickname;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
