package com.kakaopay.flex.api.sprinkle.entity;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class ReceiveInfo implements Serializable {

    int money;
    Long userId;

}